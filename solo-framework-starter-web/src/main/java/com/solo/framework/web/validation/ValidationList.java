package com.solo.framework.web.validation;

import lombok.Data;
import lombok.experimental.Delegate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 部分场景下,标记List参数不生效的问题
 *  例如:
 *  - 想要对入参对象的校验分组时,只能使用@Validated注解,而@Validated注解不支持校验List等集合类型
 *  - 这种场景下使用ValidationList
 *  {@code public UserResponse user(@Validated({CreateGroup.class}) @NotEmpty(message = "用户列表不能为空") @RequestBody ValidationList<UserRequest> requestList)}
 */
@Data
public class ValidationList<T> implements List<T> {

    @Delegate
    @Valid
    private final List<T> delegate = new ArrayList<>();

}
