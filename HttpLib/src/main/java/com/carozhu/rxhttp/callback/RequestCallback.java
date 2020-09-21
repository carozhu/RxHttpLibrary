package com.carozhu.rxhttp.callback;

import com.carozhu.rxhttp.exception.ApiException;

/**
 * DATA Request Callback
 * @param <T>
 */
public interface RequestCallback<T> {
    void startRequesting();

    void requestSuccess(T t);

    void requestFailed(int code,String msg);

    void requestException(ApiException apiException);

    void completeRequest();
}
