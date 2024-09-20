package com.solo.framework.common.util;

import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class LogUtil {

    private LogUtil() {

    }

    public static void log(String message, SoloFrameworkLoggingEnum loggingEnum) {
        logMessage(loggingEnum, message);
    }

    public static void log(SoloFrameworkLoggingEnum loggingEnum, String message, Object... args) {
        logMessage(loggingEnum, message, args);
    }

    public static void log(String message, SoloFrameworkLoggingEnum loggingEnum, Object... args) {
        logMessage(loggingEnum, message, args);
    }

    public static void log(SoloFrameworkLoggingEnum loggingEnum, String message, Throwable ex) {
        logMessage(loggingEnum, message, ex);
    }

    public static void log(String message, SoloFrameworkLoggingEnum loggingEnum, Throwable ex) {
        logMessage(loggingEnum, message, ex);
    }

    private static void logMessage(SoloFrameworkLoggingEnum loggingEnum, String message) {
        switch (loggingEnum) {
            case DEBUG:
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }
                break;
            case INFO:
                if (log.isInfoEnabled()) {
                    log.info(message);
                }
                break;
            case WARN:
                if (log.isWarnEnabled()) {
                    log.warn(message);
                }
                break;
            case ERROR:
                if (log.isErrorEnabled()) {
                    log.error(message);
                }
                break;
            default:
                log.info(message);
        }
    }

    private static void logMessage(SoloFrameworkLoggingEnum loggingEnum, String message, Object... args) {
        switch (loggingEnum) {
            case DEBUG:
                if (log.isDebugEnabled()) {
                    if (Objects.isNull(args)) {
                        log.debug(message);
                    } else {
                        log.debug(message, args);
                    }
                }
                break;
            case INFO:
                if (log.isInfoEnabled()) {
                    if (Objects.isNull(args)) {
                        log.info(message);
                    } else {
                        log.info(message, args);
                    }
                }
                break;
            case WARN:
                if (log.isWarnEnabled()) {
                    if (Objects.isNull(args)) {
                        log.warn(message);
                    } else {
                        log.warn(message, args);
                    }
                }
                break;
            case ERROR:
                if (log.isErrorEnabled()) {
                    if (Objects.isNull(args)) {
                        log.error(message);
                    } else {
                        log.error(message, args);
                    }
                }
                break;
            default:
                log.info(message, args);
        }
    }

}
