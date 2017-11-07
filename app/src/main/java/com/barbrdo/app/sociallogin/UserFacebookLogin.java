package com.barbrdo.app.sociallogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.barbrdo.app.interfaces.FBActivityCallBack;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by abhinav.rai on 30/05/16.
 */
public class UserFacebookLogin {

    private static final String KEY_EMAIL = "email";

    private FragmentActivity mActivity;
    private CallbackManager callbackManager;
    private LoginButton mFacebookLogin;
    FBActivityCallBack fbActivityCallBack;


    public UserFacebookLogin(FragmentActivity activity, LoginButton loginButton) {
        this.mActivity = activity;
        this.mFacebookLogin = loginButton;
    }

    public void setFBActivityCallBack(FBActivityCallBack fbActivityCallBack) {
        this.fbActivityCallBack = fbActivityCallBack;
    }

    public void initialiseFacebook() {
        callbackManager = CallbackManager.Factory.create();
        mFacebookLogin.setReadPermissions(Arrays.asList("email", "user_friends"));

        mFacebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        fbActivityCallBack.onGetFBUserData(object.toString());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name, last_name, name, email, gender, birthday, link");
                request.setParameters(parameters);
                request.executeAsync();
                com.facebook.login.LoginManager.getInstance().logOut();
            }

            @Override
            public void onCancel() {
                fbActivityCallBack.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("error", " error " + error.toString());
                fbActivityCallBack.onError(error.toString());
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
