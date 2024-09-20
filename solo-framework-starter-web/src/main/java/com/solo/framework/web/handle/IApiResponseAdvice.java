package com.solo.framework.web.handle;

import com.solo.framework.common.enumeration.SoloFrameworkLoggingEnum;
import com.solo.framework.web.exception.IErrorException;
import com.solo.framework.web.exception.IErrorHttpNoFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

public interface IApiResponseAdvice {

    Map<Class<? extends Throwable>, SoloFrameworkLoggingEnum> exceptionHandlerLogLevel = new HashMap<Class<? extends Throwable>, SoloFrameworkLoggingEnum>() {
        private static final long serialVersionUID = 1L;

        {
            put(HttpMessageNotReadableException.class, SoloFrameworkLoggingEnum.WARN);
            put(MethodArgumentNotValidException.class, SoloFrameworkLoggingEnum.WARN);
            put(IErrorHttpNoFoundException.class, SoloFrameworkLoggingEnum.WARN);
            put(IErrorException.class, SoloFrameworkLoggingEnum.WARN);
            put(RuntimeException.class, SoloFrameworkLoggingEnum.ERROR);
            put(Exception.class, SoloFrameworkLoggingEnum.ERROR);
        }

    };

    default void putExceptionLogLevel(Class<? extends Throwable> exClass, SoloFrameworkLoggingEnum loggingEnum) {
        exceptionHandlerLogLevel.put(exClass, loggingEnum);
    }

    default SoloFrameworkLoggingEnum getExceptionLogLevel(Class<? extends Throwable> exClass) {
        return exceptionHandlerLogLevel.get(exClass);
    }

    default void putExceptionLogLevel(Throwable ex, SoloFrameworkLoggingEnum loggingEnum) {
        exceptionHandlerLogLevel.put(ex.getClass(), loggingEnum);
    }

    default SoloFrameworkLoggingEnum getExceptionLogLevel(Throwable ex) {
        return exceptionHandlerLogLevel.get(ex.getClass());
    }

}
