package com.solo.framework.web.util;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Map;

public class MessageUtil {

    private MessageUtil() {

    }

    /**
     * 获取国际化消息
     * @param messageTemplate 国际化消息模版
     * @return 处理后的消息
     */
    public static String getInternationMessage(String messageTemplate) {
        return getInternationMessage(messageTemplate, messageTemplate);
    }

    /**
     * 获取国际化消息
     * @param messageTemplate 国际化消息模版
     * @param message 默认消息内容,国际化消息解析失败则返回默认消息
     * @return 处理后的消息
     */
    public static String getInternationMessage(String messageTemplate, String message) {
        // 是否开启国际化, 未开启返回原有信息
        if (! SoloFrameworkRuntimeInfo.INSTANCE.getSoloFrameworkProperties().getWeb().getInternation().isEnabled()) {
            return message;
        }

        if (StrUtil.isBlank(messageTemplate)) {
            return message;
        }

        return SoloFrameworkContextHolder.getBean(MessageSource.class).getMessage(messageTemplate, null, message, LocaleContextHolder.getLocale());
    }

    /**
     * 模版消息占位符替换(主要用于验证错误消息替换)
     * @param messageTemplate 消息模版内容
     * @param attributes 模版填充值
     * @return 转换后的消息内容
     */
    public static String getInternationPlaceholdersMessage(String messageTemplate, Map<String, Object> attributes) {
        return replacePlaceholders(getInternationMessage(messageTemplate), attributes);
    }

    /**
     * 模版消息占位符替换(主要用于验证错误消息替换)
     * @param messageTemplate 消息模版内容
     * @param attributes 模版填充值
     * @return 转换后的消息内容
     */
    public static String replacePlaceholders(String messageTemplate, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            messageTemplate = messageTemplate.replace(placeholder, entry.getValue().toString());
        }
        return messageTemplate;
    }

}
