package com.euronextclone.framework;

/**
 * Created with IntelliJ IDEA.
 * User: eprystupa
 * Date: 8/6/12
 * Time: 8:39 AM
 */
public interface Action<T> {
    void invoke(final T t);
}
