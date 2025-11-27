package com.solo.framework.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.solo.framework.core.user.LoginUser;

/**
 * 提供WEB应用的用户信息管理
 */
public class SoloFrameworkUserContextHolder {

    private static final TransmittableThreadLocal<LoginUser<?>> USER_HOLDER = new TransmittableThreadLocal<>();

    /**
     * 设置用户信息
     */
    public static void setUser(LoginUser<?> user) {
        USER_HOLDER.set(user);
    }

    /**
     * 获取用户信息
     */
    public static LoginUser<?> getUser() {
        return USER_HOLDER.get();
    }

    /**
     * 清除用户信息
     */
    public static void clear() {
        USER_HOLDER.remove();
    }

}
