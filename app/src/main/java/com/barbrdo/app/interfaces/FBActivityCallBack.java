package com.barbrdo.app.interfaces;

/**
 * Created by sahilsa on 6/10/16.
 */

public interface FBActivityCallBack {
    void onGetFBUserData(String userData);
    void onError(String error);
    void onCancel();
}
