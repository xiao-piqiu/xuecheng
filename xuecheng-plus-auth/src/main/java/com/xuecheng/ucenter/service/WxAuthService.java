package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.po.XcUser;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/27 14:50
 * @Description:
 */

public interface WxAuthService {
    XcUser wxAuth(String code);
}
