package com.solo.framework.web.response;

public interface IResponse<T> {

    /**
     * 真实的返回数据
     * @return 返参数据
     */
    T getData();

}
