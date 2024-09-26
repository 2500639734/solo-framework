package com.solo.framework.web.context;

import cn.hutool.core.util.StrUtil;
import com.solo.framework.core.context.SoloFrameworkContextHolder;
import com.solo.framework.core.env.SoloFrameworkRuntimeInfo;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public class SoloFrameworkWebContextHolder {

    private SoloFrameworkWebContextHolder() {

    }

    public static String getInternationMessage(String messageCode) {
        return getInternationMessage(messageCode, messageCode);
    }

    public static String getInternationMessage(String messageCode, String message) {
        if (! SoloFrameworkRuntimeInfo.INSTANCE.getSoloFrameworkProperties().getWeb().getInternation().isEnabled()) {
            return message;
        }

        if (StrUtil.isBlank(messageCode)) {
            return message;
        }

        return SoloFrameworkContextHolder.getBean(MessageSource.class).getMessage(messageCode, null, message, LocaleContextHolder.getLocale());
    }

}
