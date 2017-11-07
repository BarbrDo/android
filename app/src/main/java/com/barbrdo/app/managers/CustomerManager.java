package com.barbrdo.app.managers;

import android.content.Context;
import android.content.Intent;

import com.barbrdo.app.activities.LogInActivity;
import com.barbrdo.app.activities.SubscriptionDialogActivity;
import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.AppInstance;
import com.barbrdo.app.dataobject.BaseModel;
import com.barbrdo.app.dataobject.ContactBarberInput;
import com.barbrdo.app.dataobject.CustomerAppointment;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.CustomerRequestDetails;
import com.barbrdo.app.dataobject.CustomerRequestInput;
import com.barbrdo.app.dataobject.GalleryUpdate;
import com.barbrdo.app.dataobject.MessageInput;
import com.barbrdo.app.dataobject.RateBarberInput;
import com.barbrdo.app.dataobject.RequestCheckInInput;
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
public class CustomerManager implements CallBack {

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
    public CustomerManager(Context contextObj, ServiceRedirection successRedirectionListener) {
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

    public void getAppointments() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.APPOINTMENT;

        Call call = apiService.appointments();
        commObj.CallWebService(this, tasksID, call);
    }

    public void updateGallery(File image) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CUSTOMER_GALLERY;
        RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part imageFileBody = MultipartBody.Part.createFormData("file", image.getName(), fbody);
        Call call = apiService.updateGallery(imageFileBody);
        commObj.CallWebService(this, tasksID, call);
    }

    public void deleteImage(String imageID) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CUSTOMER_GALLERY;

        Call call = apiService.updateGallery(imageID);
        commObj.CallWebService(this, tasksID, call);
    }

    public void rateBarber(RateBarberInput rateBarberInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.RATE_BARBER;

        Call call = apiService.rateBarber(rateBarberInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void contactBarber(ContactBarberInput contactBarberInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CONTACT_BARBER;

        Call call = apiService.contactBarber(contactBarberInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void getCustomerBarbers() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CUSTOMER_BARBERS;

        Call call = apiService.getCustomerBarbers();
        commObj.CallWebService(this, tasksID, call);
    }

    public void postCustomerRequest(CustomerRequestInput customerRequestInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CUSTOMER_REQUEST;

        Call call = apiService.postCustomerRequest(customerRequestInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void cancelAppointment(String appointmentId, RequestCheckInInput requestCheckInInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CANCEL_APPOINTMENT;

        Call call = apiService.cancelCustomerAppointment(appointmentId, requestCheckInInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void getFavoriteBarbers() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.FAVORITE_BARBER;

        Call call = apiService.getFavoriteBarbers();
        commObj.CallWebService(this, tasksID, call);
    }

    public void deleteFavBarber(String barberId) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.DELETE_FAVORITE_BARBER;

        Call call = apiService.deleteFavoriteBarber(barberId);
        commObj.CallWebService(this, tasksID, call);
    }

    public void messageToBarber(MessageInput messageInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.MESSAGE_BARBER;

        Call call = apiService.messageToBarber(messageInput);
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
            case Constants.TaskID.APPOINTMENT:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        CustomerAppointment appointment = (CustomerAppointment) data.body();
                        appInstance.customerAppointment = appointment;
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
                        CustomerAppointment appointment = (CustomerAppointment) utilityObj.getObjectFromJsonString(json, CustomerAppointment.class);
                        serviceRedirectionObj.onFailureRedirection(appointment.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.CUSTOMER_GALLERY:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        GalleryUpdate galleryUpdate = (GalleryUpdate) data.body();
                        appInstance.galleryUpdate = galleryUpdate;
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
                        GalleryUpdate galleryUpdate = (GalleryUpdate) utilityObj.getObjectFromJsonString(json, GalleryUpdate.class);
                        serviceRedirectionObj.onFailureRedirection(galleryUpdate.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.RATE_BARBER:
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

            case Constants.TaskID.CONTACT_BARBER:
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

            case Constants.TaskID.CUSTOMER_BARBERS:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        CustomerBarbers customerBarbers = (CustomerBarbers) data.body();
                        appInstance.customerBarbers = customerBarbers;
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
                        CustomerBarbers customerBarbers = (CustomerBarbers) utilityObj.getObjectFromJsonString(json, CustomerBarbers.class);
                        serviceRedirectionObj.onFailureRedirection(customerBarbers.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.CUSTOMER_REQUEST:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        CustomerRequestDetails customerRequestDetails = (CustomerRequestDetails) data.body();
                        appInstance.customerRequestDetails = customerRequestDetails;
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
                        CustomerRequestDetails customerRequestDetails = (CustomerRequestDetails) utilityObj.getObjectFromJsonString(json, CustomerRequestDetails.class);
                        serviceRedirectionObj.onFailureRedirection(customerRequestDetails.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.CANCEL_APPOINTMENT:
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

            case Constants.TaskID.FAVORITE_BARBER:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        CustomerBarbers customerBarbers = (CustomerBarbers) data.body();
                        appInstance.customerBarbers = customerBarbers;
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
                        CustomerBarbers customerBarbers = (CustomerBarbers) utilityObj.getObjectFromJsonString(json, CustomerBarbers.class);
                        serviceRedirectionObj.onFailureRedirection(customerBarbers.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.DELETE_FAVORITE_BARBER:
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

            case Constants.TaskID.MESSAGE_BARBER:
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
        }
    }
}
