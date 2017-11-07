package com.barbrdo.app.managers;

import android.content.Context;
import android.content.Intent;

import com.barbrdo.app.activities.LogInActivity;
import com.barbrdo.app.activities.SubscriptionDialogActivity;
import com.barbrdo.app.async.ApiClient;
import com.barbrdo.app.dataobject.AddShopInput;
import com.barbrdo.app.dataobject.AppInstance;
import com.barbrdo.app.dataobject.AssociateShopInput;
import com.barbrdo.app.dataobject.BarberDetail;
import com.barbrdo.app.dataobject.BarberFinance;
import com.barbrdo.app.dataobject.BarberHome;
import com.barbrdo.app.dataobject.BaseModel;
import com.barbrdo.app.dataobject.CancelReasonInput;
import com.barbrdo.app.dataobject.ContactShopInput;
import com.barbrdo.app.dataobject.GalleryUpdate;
import com.barbrdo.app.dataobject.GoOnlineInput;
import com.barbrdo.app.dataobject.MessageInput;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.dataobject.RequestDateInput;
import com.barbrdo.app.dataobject.SearchShops;
import com.barbrdo.app.dataobject.ShopIdInput;
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
public class BarberManager implements CallBack {

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
    public BarberManager(Context contextObj, ServiceRedirection successRedirectionListener) {
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

    public void updateBarberGallery(File image) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_GALLERY;
        RequestBody fbody = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part imageFileBody = MultipartBody.Part.createFormData("file", image.getName(), fbody);
        Call call = apiService.updateBarberGallery(imageFileBody);
        commObj.CallWebService(this, tasksID, call);
    }

    public void deleteImage(String imageID) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_GALLERY;

        Call call = apiService.updateBarberGallery(imageID);
        commObj.CallWebService(this, tasksID, call);
    }

    public void contactShop(ContactShopInput contactShopInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CONTACT_SHOP;

        Call call = apiService.contactShop(contactShopInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void getBarberFinance(String startDate, String endDate) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_FINANCE;

        Call call = apiService.getBarberFinance(Constants.WebServices.WS_BARBER_FINANCE + startDate + "/" + endDate);
        commObj.CallWebService(this, tasksID, call);
    }

    public void barberHome() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_HOME;

        Call call = apiService.barberHome();
        commObj.CallWebService(this, tasksID, call);
    }

    public void goOnline(GoOnlineInput goOnlineInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.GO_ONLINE;

        Call call = apiService.goOnline(goOnlineInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void goOffline() {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.GO_OFFLINE;

        Call call = apiService.goOffline();
        commObj.CallWebService(this, tasksID, call);
    }

    public void declineAppointment(String appointmentId, RequestCheckInInput requestCheckInInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_DECLINE_REQUEST;

        Call call = apiService.declineCustomerRequest(appointmentId, requestCheckInInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void acceptAppointment(String appointmentId, RequestDateInput requestDateInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_ACCEPT_REQUEST;

        Call call = apiService.acceptCustomerRequest(appointmentId, requestDateInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void barberCheckIn(String appointmentId, RequestCheckInInput requestCheckInInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_CHECK_IN;

        Call call = apiService.barberCheckIn(appointmentId, requestCheckInInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void barberDetails(String barberId) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_DETAIL;

        Call call = apiService.barberDetail(barberId);
        commObj.CallWebService(this, tasksID, call);
    }

    public void defaultShop(ShopIdInput shopIdInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.BARBER_DEFAULT_SHOP;

        Call call = apiService.defaultShop(shopIdInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void messageToCustomer(MessageInput messageInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.MESSAGE_CUSTOMER;

        Call call = apiService.messageToCustomer(messageInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void searchShops(String name, String city, String state, String zip) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.SEARCH_SHOPS;

        Call call = apiService.searchShops(Constants.WebServices.WS_SEARCH_SHOPS
                + "?name=" + name + "&city=" + city + "&state=" + state + "&zip=" + zip);
        commObj.CallWebService(this, tasksID, call);
    }

    public void associateShop(AssociateShopInput associateShopInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.ASSOCIATE_SHOP;

        Call call = apiService.associatesShop(associateShopInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void deleteShop(ShopIdInput shopIdInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.DELETE_SHOP;

        Call call = apiService.deleteShop(shopIdInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void addShop(AddShopInput addShopInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.ADD_SHOP;

        Call call = apiService.addShop(addShopInput);
        commObj.CallWebService(this, tasksID, call);
    }

    public void cancelAppointmentBarber(String appointmentId, CancelReasonInput cancelReasonInput) {
        commObj = new CommunicationManager(this.context);
        tasksID = Constants.TaskID.CANCEL_APPOINTMENT_BARBER;

        Call call = apiService.cancelAppointmentByBarber(appointmentId, cancelReasonInput);
        commObj.CallWebService(this, tasksID, call);
    }

    /**
     * The interface method implemented in the java files
     *
     * @param data       the result returned by the web service
     * @param tasksID    the ID to differential multiple webservice calls
     * @param statusCode the statusCode returned by the web servicepublic static final int BARBER_CANCEL_APPOINTMENT = 123;
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
            case Constants.TaskID.BARBER_GALLERY:
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

            case Constants.TaskID.CONTACT_SHOP:
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

            case Constants.TaskID.BARBER_FINANCE:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BarberFinance barberFinance = (BarberFinance) data.body();
                        appInstance.barberFinance = barberFinance;
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
                        BarberFinance barberFinance = (BarberFinance) utilityObj.getObjectFromJsonString(json, BarberFinance.class);
                        serviceRedirectionObj.onFailureRedirection(barberFinance.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.BARBER_HOME:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BarberHome barberHome = (BarberHome) data.body();
                        appInstance.barberHome = barberHome;
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
                        BarberHome barberHome = (BarberHome) utilityObj.getObjectFromJsonString(json, BarberHome.class);
                        serviceRedirectionObj.onFailureRedirection(barberHome.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.GO_ONLINE:
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

            case Constants.TaskID.GO_OFFLINE:
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

            case Constants.TaskID.BARBER_DECLINE_REQUEST:
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

            case Constants.TaskID.BARBER_ACCEPT_REQUEST:
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

            case Constants.TaskID.BARBER_CHECK_IN:
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

            case Constants.TaskID.BARBER_DETAIL:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        BarberDetail barberDetail = (BarberDetail) data.body();
                        appInstance.barberDetail = barberDetail;
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
                        BarberDetail barberDetail = (BarberDetail) utilityObj.getObjectFromJsonString(json, BarberDetail.class);
                        serviceRedirectionObj.onFailureRedirection(barberDetail.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.BARBER_DEFAULT_SHOP:
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

            case Constants.TaskID.MESSAGE_CUSTOMER:
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

            case Constants.TaskID.SEARCH_SHOPS:
                if (data != null) {
                    if (statusCode == ResponseCodes.Success) {
                        SearchShops searchShops = (SearchShops) data.body();
                        appInstance.searchShops = searchShops;
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
                        SearchShops searchShops = (SearchShops) utilityObj.getObjectFromJsonString(json, SearchShops.class);
                        serviceRedirectionObj.onFailureRedirection(searchShops.msg);
                    } else {
                        serviceRedirectionObj.onFailureRedirection(message);
                    }
                } else {
                    serviceRedirectionObj.onFailureRedirection(message);
                }
                break;

            case Constants.TaskID.ASSOCIATE_SHOP:
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

            case Constants.TaskID.DELETE_SHOP:
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

            case Constants.TaskID.ADD_SHOP:
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

            case Constants.TaskID.CANCEL_APPOINTMENT_BARBER:
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
