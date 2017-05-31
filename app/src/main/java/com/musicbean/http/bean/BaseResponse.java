package com.musicbean.http.bean;

/**
 * Created by boyo on 16/5/28.
 */
public class BaseResponse<T> {
    public int errorno;
    public String errmsg;
    public T data;
}
