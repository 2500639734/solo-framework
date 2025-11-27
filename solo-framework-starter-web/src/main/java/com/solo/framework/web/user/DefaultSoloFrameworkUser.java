package com.solo.framework.web.user;

import com.solo.framework.core.user.ISoloFrameworkUser;
import com.solo.framework.core.user.LoginUser;

/**
 * 登录用户信息接口的默认实现
 */
public class DefaultSoloFrameworkUser implements ISoloFrameworkUser {

    /**
     * 获取登录用户信息
     */
    @Override
    public LoginUser<?> getLoginUser() {
        LoginUser<?> loginUser = new LoginUser<>();
        loginUser.setUserCode("system");
        loginUser.setUserName("系统用户");
        loginUser.setEntId(0L);
        return loginUser;
    }

}
