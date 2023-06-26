package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @author xiao_piqiu
 * @version 1.0
 * @date 2023/6/26 18:56
 * @Description:
 */

public interface AuthService {
    XcUserExt execute(AuthParamsDto authParamsDto);
}
