package com.solo.framework.core.user;

import lombok.Data;

/**
 * 抽象的用户实体，应用可以将自己的用户信息转换为框架内的抽象用户信息
 */
@Data
public class LoginUser<T> {

    /**
     * 用户代码
     */
    private String userCode;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 企业ID（多租户场景）
     */
    private Long entId;

    /**
     * 扩展字段
     */
    private T extra;

}
