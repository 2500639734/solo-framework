package com.solo.framework.web.exception;

import com.solo.framework.web.enums.IErrorCode;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Objects;

@Getter
public class IErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 异常错误码
     */
    private final int errorCode;

    /**
     * 异常错误信息
     */
    private final String errorMessage;

    public IErrorException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public IErrorException(int errorCode, String errorMessage, Throwable cause) {
        this(errorCode, errorMessage);
        initCauseIfNoneNull(cause);
    }

    public IErrorException(IErrorCode iErrorCode) {
        this(iErrorCode.getCode(), iErrorCode.getMessage());
    }

    public IErrorException(IErrorCode iErrorCode, Throwable cause) {
        this(iErrorCode);
        initCauseIfNoneNull(cause);
    }

    public IErrorException(IErrorCode iErrorCode, Object ... msgArgs) {
        this(iErrorCode, null, msgArgs);
    }

    public IErrorException(IErrorCode iErrorCode, Throwable cause, Object ... msgArgs) {
        this(iErrorCode.getCode(), MessageFormat.format(iErrorCode.getMessage(), msgArgs), cause);
    }

    private void initCauseIfNoneNull(Throwable cause) {
        if (Objects.nonNull(cause)) {
            initCause(cause);
        }
    }

}
