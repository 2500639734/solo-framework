package com.solo.framework.core.user;

/**
 * 登录用户信息接口, 应用方可以实现此接口, 注入自己的用户信息
 */
public interface ISoloFrameworkUser {

    /**
     * 获取登录用户信息
     */
    LoginUser<?> getLoginUser();

}
