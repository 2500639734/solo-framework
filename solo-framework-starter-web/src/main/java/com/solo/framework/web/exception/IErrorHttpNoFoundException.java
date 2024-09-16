package com.solo.framework.web.exception;

import lombok.Getter;

@Getter
public class IErrorHttpNoFoundException extends IErrorException {

    private static final long serialVersionUID = 1L;

    public IErrorHttpNoFoundException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

}
