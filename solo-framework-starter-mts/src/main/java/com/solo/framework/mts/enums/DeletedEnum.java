package com.solo.framework.mts.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 删除标记枚举（0 未删除，1 已删除）
 */
@AllArgsConstructor
@Getter
public enum DeletedEnum {

    /**
     * 未删除
     */
    NORMAL(0),

    /**
     * 已删除
     */
    DELETED(1);

    /**
     * 枚举值
     */
    private final int value;

}
