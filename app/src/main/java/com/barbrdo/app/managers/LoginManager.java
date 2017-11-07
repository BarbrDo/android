package com.barbrdo.app.managers;

import android.content.Context;

import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.AppInstance;
import com.barbrdo.app.dataobject.BaseModel;
import com.barbrdo.app.dataobject.CheckFacebookInput;
import com.barbrdo.app.dataobject.User;
import com.barbrdo.app.dataobject.UserDetail;
import com.barbrdo.app.interfaces.ApiInterface;
import com.barbrdo.app.interfaces.CallBack;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.utils.Constants;
import com.barbrdo.app.utils.ResponseCodes;
import com.barbrdo.app.utils.SessionManager;
import com.barbrdo.app.utils.Utility;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Anurag Sethi
 * The class will handle all the implementations related to the login operations
 */
public class LoginManager implements CallBack {

    Context context;
    Utility utilObj;
    CommunicationManager commObj;
    ServiceRedirection serviceRedirectionObj;
    int tasksID;
    AppInstance appInstance;

    //Retrofit Interface
    ApiInterface apiService;
    String authentication = "12345"; // you should change Authentication according to your requirement
    Utility utilityObj;
    SessionManager sessionManager;


    /**
     * Constructor
     *
     * @param contextObj                 The Context from where the method is called
     * @param successRedirectionListener The listener interface for receiving action events
     * @return none
     */
    public LoginManager(Context contextObj, ServiceRedirection successRedirectionListener) {
        context = contextObj;
        utilObj = new Utility(contextObj);
        serviceRedirectionObj = successRedirectionListener;
        appInstance = AppInstance.getInstance();
        utilityObj = new Utility(contextObj);
        sessionManager = new SessionManager(contextObj);

        apiService = ApiClient.createService(ApiInterface.class, authentication, "", sessionManager.getDeviceLatitude(), sessionManager.getDeviceLongitude(), "", "");
    }

    /**
     * Calls the Web Service of authenticateLogin
     *
     * @param userObj having the required data
     * @return none
     */
    public void authenticateLogin(User userObj) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.LOGIN;

        Call call = apiService.loginUser(userObj);
        commObj.CallWebService(this, tasksID, call);
    }

    /**
     * Calls the Web Service of Registration
     *
     * @param userObj having the required data
     * @return none
     */
    public void signUpUser(User userObj) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.SIGN_UP;

        Call call = apiService.signUpUser(userObj);
        commObj.CallWebService(this, tasksID, call);
    }

    public void forgotPassword(User userObj) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.FORGOT_PASSWORD;

        Call call = apiService.forgotPassword(userObj);
        commObj.CallWebService(this, tasksID, call);
    }

    public void checkFacebook(CheckFacebookInput checkFacebookInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CHECK_FACEBOOK;

        Call call = apiService.checkFacebook(checkFacebookInput);
        commObj.CallWebService(this, tasksID, call);
    }

    /**
     * The interface method implemented in the java files
     *
     * @param data       the result returned by the web service
     * @param tasksID    the ID to differential multiple webservice calls
     * @param statusCode the statusCode returned by the web service
     * @param message    the message returned by the web service
     * @return none
     * @since 2014-08-28
     */
    @Override
    public void onResult(Response data, int tasksID, int statusCode, String message) {
        switch (tasksID) {
            case Constants.TaskID.LOGIN:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        UserDetail userDetail = (UserDetail) data.body();
                        appInstance.userDetail = userDetail;
                        serviceRedirectionObj.onSuccessRedirection(tasksID);
                    } else if (statusCode == ResponseCodes.BadRequest || statusCode == ResponseCodes.UnauthorisedAccess) {
                        String json;
                        try {
                            json = data.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            serviceRedirectionObj.onFailureRedirection(message);
                            return;
                        }
                        UserDetail userDetail = (UserDetail) utilityObj.getObjectFromJsonString(json, UserDetail.class);
                        serviceRedirectionObj.onFailureRedirection(userDetail.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;
            case Constants.TaskID.SIGN_UP:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        UserDetail userDetail = (UserDetail) data.body();
                        appInstance.userDetail = userDetail;
                        serviceRedirectionObj.onSuccessRedirection(tasksID);
                    } else if (statusCode == ResponseCodes.BadRequest || statusCode == ResponseCodes.UnauthorisedAccess) {
                        String json;
                        try {
                            json = data.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            serviceRedirectionObj.onFailureRedirection(message);
                            return;
                        }
                        UserDetail userDetail = (UserDetail) utilityObj.getObjectFromJsonString(json, UserDetail.class);
                        serviceRedirectionObj.onFailureRedirection(userDetail.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;
            case Constants.TaskID.FORGOT_PASSWORD:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BaseModel baseModel = (BaseModel) data.body();
                        appInstance.successMessage = baseModel.msg;
                        serviceRedirectionObj.onSuccessRedirection(tasksID);
                    } else if (statusCode == ResponseCodes.BadRequest) {
                        String json;
                        try {
                            json = data.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            serviceRedirectionObj.onFailureRedirection(message);
                            return;
                        }
                        BaseModel baseModel = (BaseModel) utilityObj.getObjectFromJsonString(json, BaseModel.class);
                        serviceRedirectionObj.onFailureRedirection(baseModel.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.CHECK_FACEBOOK:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        UserDetail userDetail = (UserDetail) data.body();
                        appInstance.userDetail = userDetail;
                        serviceRedirectionObj.onSuccessRedirection(tasksID);
                    } else if (statusCode == ResponseCodes.BadRequest || statusCode == ResponseCodes.UnauthorisedAccess) {
                        String json;
                        try {
                            json = data.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            serviceRedirectionObj.onFailureRedirection(message);
                            return;
                        }
                        UserDetail userDetail = (UserDetail) utilityObj.getObjectFromJsonString(json, UserDetail.class);
                        serviceRedirectionObj.onFailureRedirection(userDetail.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;
        }
    }
}
