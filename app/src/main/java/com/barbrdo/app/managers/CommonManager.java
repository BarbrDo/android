package com.barbrdo.app.managers;

import android.content.Context;
import android.content.Intent;

import com.barbrdo.app.activities.LogInActivity;
import com.barbrdo.app.activities.SubscriptionDialogActivity;
import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.AccountUpdate;
import com.barbrdo.app.dataobject.AppInstance;
import com.barbrdo.app.dataobject.BaseModel;
import com.barbrdo.app.dataobject.ContactInput;
import com.barbrdo.app.dataobject.SendInviteInput;
import com.barbrdo.app.dataobject.States;
import com.barbrdo.app.dataobject.SubscriptionPlans;
import com.barbrdo.app.dataobject.SubscriptionSuccessInput;
import com.barbrdo.app.dataobject.UpdateAccountInput;
import com.barbrdo.app.dataobject.UpdateShopInput;
import com.barbrdo.app.dataobject.UserProfile;
import com.barbrdo.app.interfaces.ApiInterface;
import com.barbrdo.app.interfaces.CallBack;
import com.barbrdo.app.interfaces.ServiceRedirection;
import com.barbrdo.app.utils.Constants;
import com.barbrdo.app.utils.ResponseCodes;
import com.barbrdo.app.utils.SessionManager;
import com.barbrdo.app.utils.Utility;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Anurag Sethi
 * The class will handle all the implementations related to the login operations
 */
public class CommonManager implements CallBack {

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
    public CommonManager(Context contextObj, ServiceRedirection successRedirectionListener) {
        context = contextObj;
        utilObj = new Utility(contextObj);
        serviceRedirectionObj = successRedirectionListener;
        appInstance = AppInstance.getInstance();
        utilityObj = new Utility(contextObj);
        sessionManager = new SessionManager(contextObj);

        apiService = ApiClient.createService(ApiInterface.class, authentication, sessionManager.getUserId(),
                sessionManager.getDeviceLatitude(), sessionManager.getDeviceLongitude(), sessionManager.getAuthorizationToken(),
                sessionManager.getUserType());
    }

    public void uploadProfilePicture(File image) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.PROFILE_PICTURE;
        RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part imageFileBody = MultipartBody.Part.createFormData("file", image.getName(), fbody);
        Call call = apiService.updateProfilePicture(imageFileBody);
        commObj.CallWebService(this, tasksID, call);
    }

    public void updateProfile(UpdateAccountInput updateAccountInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.ACCOUNT;

        Call call = apiService.updateProfileDetails(updateAccountInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void userProfile(String userId) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.USER_PROFILE;

        Call call = apiService.userProfile(Constants.WebServices.WS_USER_PROFILE + userId);
        commObj.CallWebService(this, tasksID, call);
    }

    public void updateShop(UpdateShopInput updateShopInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.UPDATE_SHOP;

        Call call = apiService.updateShop(updateShopInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void contact(ContactInput contactInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CONTACT;

        Call call = apiService.contact(contactInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void logOut() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.LOGOUT;

        Call call = apiService.logOut();
        commObj.CallWebService(this, tasksID, call);
    }

    public void getStates() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.STATES;

        Call call = apiService.getStates();
        commObj.CallWebService(this, tasksID, call);
    }

    public void sendInvite(SendInviteInput sendInviteInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.INVITE_TO_APP;

        Call call = apiService.inviteToApp(sendInviteInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void getSubscriptionPlans() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.SUBSCRIPTION_PLANS;

        Call call = apiService.getSubscriptionPlans();
        commObj.CallWebService(this, tasksID, call);
    }

    public void subscribeUser(SubscriptionSuccessInput subscriptionSuccessInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.SUBSCRIBE_USER;

        Call call = apiService.subscribeUser(subscriptionSuccessInput);
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
        if (statusCode == ResponseCodes.UnauthorisedAccess) {
            Intent i = new Intent(context, LogInActivity.class);
            i.putExtra(Constants.BundleKeyTag.IS_SUBSCRIPTION_EXPIRED, false);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
            return;
        }else if(statusCode == ResponseCodes.SubscriptionExpired){
            Intent i = new Intent(context, SubscriptionDialogActivity.class);
            context.startActivity(i);
        }

        switch (tasksID) {
            case Constants.TaskID.PROFILE_PICTURE:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        AccountUpdate accountUpdate = (AccountUpdate) data.body();
                        appInstance.accountUpdate = accountUpdate;
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
                        AccountUpdate accountUpdate = (AccountUpdate) utilityObj.getObjectFromJsonString(json, AccountUpdate.class);
                        serviceRedirectionObj.onFailureRedirection(accountUpdate.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.ACCOUNT:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        AccountUpdate accountUpdate = (AccountUpdate) data.body();
                        appInstance.accountUpdate = accountUpdate;
                        appInstance.message = accountUpdate.msg;
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
                        AccountUpdate accountUpdate = (AccountUpdate) utilityObj.getObjectFromJsonString(json, AccountUpdate.class);
                        serviceRedirectionObj.onFailureRedirection(accountUpdate.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.USER_PROFILE:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        UserProfile userProfile = (UserProfile) data.body();
                        appInstance.userProfile = userProfile;
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
                        UserProfile userProfile = (UserProfile) utilityObj.getObjectFromJsonString(json, UserProfile.class);
                        serviceRedirectionObj.onFailureRedirection(userProfile.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.UPDATE_SHOP:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BaseModel baseModel = (BaseModel) data.body();
                        appInstance.message = baseModel.msg;
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
                        BaseModel baseModel = (BaseModel) utilityObj.getObjectFromJsonString(json, BaseModel.class);
                        serviceRedirectionObj.onFailureRedirection(baseModel.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;
            case Constants.TaskID.CONTACT:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BaseModel baseModel = (BaseModel) data.body();
                        appInstance.message = baseModel.msg;
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
                        BaseModel baseModel = (BaseModel) utilityObj.getObjectFromJsonString(json, BaseModel.class);
                        serviceRedirectionObj.onFailureRedirection(baseModel.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;
            case Constants.TaskID.LOGOUT:
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
            case Constants.TaskID.STATES:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        States states = (States) data.body();
                        appInstance.states = states;
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
                        States states = (States) utilityObj.getObjectFromJsonString(json, States.class);
                        serviceRedirectionObj.onFailureRedirection(states.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;
            case Constants.TaskID.INVITE_TO_APP:
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
            case Constants.TaskID.SUBSCRIBE_USER:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BaseModel baseModel = (BaseModel) data.body();
                        appInstance.successMessage = baseModel.msg;
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
                        BaseModel baseModel = (BaseModel) utilityObj.getObjectFromJsonString(json, BaseModel.class);
                        serviceRedirectionObj.onFailureRedirection(baseModel.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.SUBSCRIPTION_PLANS:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        SubscriptionPlans subscriptionPlans = (SubscriptionPlans) data.body();
                        appInstance.subscriptionPlans = subscriptionPlans;
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
                        SubscriptionPlans subscriptionPlans = (SubscriptionPlans) utilityObj.getObjectFromJsonString(json, SubscriptionPlans.class);
                        serviceRedirectionObj.onFailureRedirection(subscriptionPlans.msg);
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
