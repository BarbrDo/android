package com.barbrdo.app.interfaces;

/**
 * Created by sahilsa on 16/6/17.
 */

public interface OnRequest<T> {
    void onAccept(T t);

    void onReject(T t);
}
