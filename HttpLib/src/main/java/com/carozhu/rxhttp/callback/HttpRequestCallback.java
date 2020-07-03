package com.carozhu.rxhttp.callback;

import com.carozhu.rxhttp.exception.ApiException;

/**
 * DATA Request Callback
 * @param <T>
 */
public interface HttpRequestCallback<T> {
    void requestSuccess(T t);

    void requestError(ApiException apiException);
}
