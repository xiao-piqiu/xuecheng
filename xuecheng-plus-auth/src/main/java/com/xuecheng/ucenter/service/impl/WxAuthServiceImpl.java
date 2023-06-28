package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.WxAuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/26 18:57
 * @Description:
 */
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService , WxAuthService {
    @Value("${weixin.appid}")
    String appid;
    @Value("${weixin.secret}")
    String secret;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    XcUserRoleMapper xcUserRoleMapper;
    @Autowired
    WxAuthServiceImpl currentPorxy;
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser==null){
            throw new RuntimeException("用户不存在,登录失败");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        //请求微信申请令牌
        Map<String, String> access_token_map = getAccess_token(code);
        // 拿到令牌查询用户信息
        String access_token = access_token_map.get("access_token");
        String openid = access_token_map.get("openid");
        Map<String, String> userinfo = getUserinfo(access_token, openid);
        // 将用户信息写入本项目数据库
        return currentPorxy.addWxUser(userinfo);
    }
    /**
     * 申请访问令牌,响应示例
     {
     "access_token":"ACCESS_TOKEN",
     "expires_in":7200,
     "refresh_token":"REFRESH_TOKEN",
     "openid":"OPENID",
     "scope":"SCOPE",
     "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */
    private Map<String,String> getAccess_token(String code) {
        String url_template="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url=String.format(url_template,appid,secret,code);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        String result = exchange.getBody();
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }
    /**获取用户信息，示例如下：
     {
     "openid":"OPENID",
     "nickname":"NICKNAME",
     "sex":1,
     "province":"PROVINCE",
     "city":"CITY",
     "country":"COUNTRY",
     "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     "privilege":[
     "PRIVILEGE1",
     "PRIVILEGE2"
     ],
     "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     }
     */
    private Map<String,String> getUserinfo(String access_token,String openid) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(wxUrl_template, access_token, openid);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
        Map<String,String> map = JSON.parseObject(result, Map.class);
        return map;
    }
    @Transactional
    public XcUser addWxUser(Map<String,String> userInfo_map){
        String unionid = userInfo_map.get("unionid");
        String nickname = userInfo_map.get("nickname");
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if (xcUser!=null){
            return xcUser;
        }
        xcUser = new XcUser();
        xcUser.setId(UUID.randomUUID().toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setWxUnionid(unionid);
        xcUser.setNickname(nickname);
        xcUser.setName(nickname);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        int insert = xcUserMapper.insert(xcUser);
        //向关系表新增
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setRoleId(UUID.randomUUID().toString());
        xcUserRole.setUserId(xcUser.getId());
        xcUserRole.setRoleId("17");
        xcUserRole.setCreateTime(LocalDateTime.now());
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }
}
