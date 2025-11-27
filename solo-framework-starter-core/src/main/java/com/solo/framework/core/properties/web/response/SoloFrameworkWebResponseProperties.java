package com.solo.framework.core.properties.web.response;

import cn.hutool.http.HttpStatus;
import com.solo.framework.core.constant.SoloFrameworkPropertiesPrefixConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SoloFrameworkPropertiesPrefixConstant.SOLO_FRAMEWORK_WEB_RESPONSE_PREFIX)
public class SoloFrameworkWebResponseProperties {

    /**
     * 是否启用响应包装
     */
    private boolean enabled = true;

    /**
     * 接口地址错误时, API统一HTTP响应码
     */
    private int apiNotFoundCode = HttpStatus.HTTP_OK;

    /**
     * 接口参数错误时, API统一HTTP响应码
     */
    private int apiBadRequestCode = HttpStatus.HTTP_OK;

    /**
     * 系统异常时, API统一HTTP响应码
     */
    private int apiErrorCode = HttpStatus.HTTP_OK;

    /**
     * 是否显示参数校验失败字段:
     *  true: 接口参数字段校验失败时,错误信息会展示校验失败的字段,开发阶段可以打开用于快速调试
     */
    private boolean showValidFailField = false;

    /**
     * 是否包装Mybatis Plus分页字段
     */
    private boolean wrapperPage = true;

}
