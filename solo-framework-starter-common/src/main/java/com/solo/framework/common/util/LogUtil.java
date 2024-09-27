package com.solo.framework.common.util;

import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.Objects;

@Slf4j
public class LogUtil {

    private LogUtil() {

    }

    public static void log(String message, SoloFrameworkLoggingEnum loggingEnum) {
        logMessage(loggingEnum, message);
    }

    public static void log(String message, SoloFrameworkLoggingEnum loggingEnum, Object... args) {
        logMessage(loggingEnum, message, args);
    }

    public static void log(String message, SoloFrameworkLoggingEnum loggingEnum, Throwable ex) {
        logMessage(loggingEnum, message, ex);
    }

    private static void logMessage(SoloFrameworkLoggingEnum loggingEnum, String message, Object... args) {
        switch (loggingEnum) {
            case TRACE:
                if (log.isTraceEnabled()) {
                    log.trace(message, args);
                }
                break;
            case DEBUG:
                if (log.isDebugEnabled()) {
                    log.debug(message, args);
                }
                break;
            case INFO:
                if (log.isInfoEnabled()) {
                    log.info(message, args);
                }
                break;
            case WARN:
                if (log.isWarnEnabled()) {
                    log.warn(message, args);
                }
                break;
            case ERROR:
                if (log.isErrorEnabled()) {
                    log.error(message, args);
                }
                break;

            default:
                log.info(message, args);
        }
    }

    private static void logMessage(SoloFrameworkLoggingEnum loggingEnum, String message, Throwable ex, Object... args) {
        if (Objects.nonNull(args)) {
            message = MessageFormat.format(message, args);
        }

        switch (loggingEnum) {
            case TRACE:
                if (log.isTraceEnabled()) {
                    log.trace(message, ex);
                }
                break;
            case DEBUG:
                if (log.isDebugEnabled()) {
                    log.debug(message, ex);
                }
                break;
            case INFO:
                if (log.isInfoEnabled()) {
                    log.info(message, ex);
                    log.info(message, new Object(), ex);
                }
                break;
            case WARN:
                if (log.isWarnEnabled()) {
                    log.warn(message, ex);
                }
                break;
            case ERROR:
                if (log.isErrorEnabled()) {
                    log.error(message, ex);
                }
                break;
            default:
                log.info(message, ex);
        }
    }

}
