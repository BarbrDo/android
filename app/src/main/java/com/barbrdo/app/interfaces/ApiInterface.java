package com.barbrdo.app.interfaces;

import com.barbrdo.app.dataobject.AccountUpdate;
import com.barbrdo.app.dataobject.AddShopInput;
import com.barbrdo.app.dataobject.AssociateShopInput;
import com.barbrdo.app.dataobject.BarberDetail;
import com.barbrdo.app.dataobject.BarberFinance;
import com.barbrdo.app.dataobject.BarberHome;
import com.barbrdo.app.dataobject.BaseModel;
import com.barbrdo.app.dataobject.CancelReasonInput;
import com.barbrdo.app.dataobject.CheckFacebookInput;
import com.barbrdo.app.dataobject.ContactBarberInput;
import com.barbrdo.app.dataobject.ContactInput;
import com.barbrdo.app.dataobject.ContactShopInput;
import com.barbrdo.app.dataobject.CustomerAppointment;
import com.barbrdo.app.dataobject.CustomerBarbers;
import com.barbrdo.app.dataobject.CustomerRequestDetails;
import com.barbrdo.app.dataobject.CustomerRequestInput;
import com.barbrdo.app.dataobject.GalleryUpdate;
import com.barbrdo.app.dataobject.GoOnlineInput;
import com.barbrdo.app.dataobject.MessageInput;
import com.barbrdo.app.dataobject.RateBarberInput;
import com.barbrdo.app.dataobject.RequestCheckInInput;
import com.barbrdo.app.dataobject.RequestDateInput;
import com.barbrdo.app.dataobject.SearchShops;
import com.barbrdo.app.dataobject.SendInviteInput;
import com.barbrdo.app.dataobject.ShopIdInput;
import com.barbrdo.app.dataobject.States;
import com.barbrdo.app.dataobject.SubscriptionPlans;
import com.barbrdo.app.dataobject.SubscriptionSuccessInput;
import com.barbrdo.app.dataobject.UpdateAccountInput;
import com.barbrdo.app.dataobject.UpdateShopInput;
import com.barbrdo.app.dataobject.User;
import com.barbrdo.app.dataobject.UserDetail;
import com.barbrdo.app.dataobject.UserProfile;
import com.barbrdo.app.utils.Constants;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * The interface method implemented in the java(Manager) files
 */
public interface ApiInterface {

    /**
     * For post method  use @POST
     *
     * @param user services url
     * @return
     * @Body User model class data
     */

    @POST(Constants.WebServices.WS_USER_LOG_IN)
    Call<UserDetail> loginUser(@Body User user);

    @POST(Constants.WebServices.WS_USER_SIGN_UP)
    Call<UserDetail> signUpUser(@Body User user);

    @POST(Constants.WebServices.WS_FORGOT_PASSWORD)
    Call<BaseModel> forgotPassword(@Body User user);

    @GET(Constants.WebServices.WS_APPOINTMENT)
    Call<CustomerAppointment> appointments();

    @POST(Constants.WebServices.WS_CHECK_FACEBOOK)
    Call<UserDetail> checkFacebook(@Body CheckFacebookInput checkFacebookInput);

    @Multipart
    @POST(Constants.WebServices.WS_CUSTOMER_GALLERY)
    Call<GalleryUpdate> updateGallery(@Part MultipartBody.Part file);

    @DELETE(Constants.WebServices.WS_CUSTOMER_GALLERY + "{image_id}")
    Call<GalleryUpdate> updateGallery(@Path("image_id") String imageId);

    @Multipart
    @PUT(Constants.WebServices.WS_ACCOUNT)
    Call<AccountUpdate> updateProfilePicture(@Part MultipartBody.Part file);

    @PUT(Constants.WebServices.WS_ACCOUNT)
    Call<AccountUpdate> updateProfileDetails(@Body UpdateAccountInput updateAccountInput);

    @Multipart
    @POST(Constants.WebServices.WS_BARBER_GALLERY)
    Call<GalleryUpdate> updateBarberGallery(@Part MultipartBody.Part file);

    @DELETE(Constants.WebServices.WS_BARBER_GALLERY + "{image_id}")
    Call<GalleryUpdate> updateBarberGallery(@Path("image_id") String imageId);

    @GET
    Call<UserProfile> userProfile(@Url String url);

    @PUT(Constants.WebServices.WS_SHOPS)
    Call<BaseModel> updateShop(@Body UpdateShopInput updateShopInput);

    @POST(Constants.WebServices.WS_CONTACT)
    Call<BaseModel> contact(@Body ContactInput contactInput);

    @POST(Constants.WebServices.WS_RATE_BARBER)
    Call<BaseModel> rateBarber(@Body RateBarberInput rateBarberInput);

    @POST(Constants.WebServices.WS_CONTACT_SHOP)
    Call<BaseModel> contactShop(@Body ContactShopInput contactShopInput);

    @GET
    Call<BarberFinance> getBarberFinance(@Url String url);

    @POST(Constants.WebServices.WS_CONTACT_BARBER)
    Call<BaseModel> contactBarber(@Body ContactBarberInput contactBarberInput);

    @GET(Constants.WebServices.WS_LOGOUT)
    Call<BaseModel> logOut();

    @GET(Constants.WebServices.WS_CUSTOMER_BARBERS)
    Call<CustomerBarbers> getCustomerBarbers();

    @POST(Constants.WebServices.WS_CUSTOMER_REQUEST)
    Call<CustomerRequestDetails> postCustomerRequest(@Body CustomerRequestInput customerRequestInput);

    @PUT(Constants.WebServices.WS_CANCEL_APPOINTMENT + "{appointment_id}")
    Call<BaseModel> cancelCustomerAppointment(@Path("appointment_id") String appointmentId, @Body RequestCheckInInput requestCheckInInput);

    @GET(Constants.WebServices.WS_FAVORITE_BARBER)
    Call<CustomerBarbers> getFavoriteBarbers();

    @DELETE(Constants.WebServices.WS_FAVORITE_BARBER + "{barber_id}")
    Call<BaseModel> deleteFavoriteBarber(@Path("barber_id") String barberId);

    @GET(Constants.WebServices.WS_BARBER_HOME)
    Call<BarberHome> barberHome();

    @POST(Constants.WebServices.WS_BARBER_GO_ONLINE)
    Call<BaseModel> goOnline(@Body GoOnlineInput goOnlineInput);

    @PUT(Constants.WebServices.WS_BARBER_GO_OFFLINE)
    Call<BaseModel> goOffline();

    @PUT(Constants.WebServices.WS_BARBER_DECLINE_REQUEST + "{appointment_id}")
    Call<BaseModel> declineCustomerRequest(@Path("appointment_id") String appointmentId, @Body RequestCheckInInput requestCheckInInput);

    @PUT(Constants.WebServices.WS_BARBER_ACCEPT_REQUEST + "{appointment_id}")
    Call<BaseModel> acceptCustomerRequest(@Path("appointment_id") String appointmentId, @Body RequestDateInput requestDateInput);

    @PUT(Constants.WebServices.WS_BARBER_CHECK_IN + "{appointment_id}")
    Call<BaseModel> barberCheckIn(@Path("appointment_id") String appointmentId, @Body RequestCheckInInput requestCheckInInput);

    @GET(Constants.WebServices.WS_BARBER_DETAIL + "{barber_id}")
    Call<BarberDetail> barberDetail(@Path("barber_id") String barberId);

    @POST(Constants.WebServices.WS_DEFAULT_SHOP)
    Call<BaseModel> defaultShop(@Body ShopIdInput shopIdInput);

    @POST(Constants.WebServices.WS_MESSAGE_TO_BARBER)
    Call<BaseModel> messageToBarber(@Body MessageInput messageInput);

    @POST(Constants.WebServices.WS_MESSAGE_TO_CUSTOMER)
    Call<BaseModel> messageToCustomer(@Body MessageInput messageInput);

    @GET
    Call<SearchShops> searchShops(@Url String url);

    @GET(Constants.WebServices.WS_STATES)
    Call<States> getStates();

    @POST(Constants.WebServices.WS_ASSOCIATE_SHOP)
    Call<BaseModel> associatesShop(@Body AssociateShopInput associateShopInput);

    @HTTP(method = "DELETE", path = Constants.WebServices.WS_DELETE_SHOP, hasBody = true)
    Call<BaseModel> deleteShop(@Body ShopIdInput shopIdInput);

    @POST(Constants.WebServices.WS_ADD_SHOP)
    Call<BaseModel> addShop(@Body AddShopInput addShopInput);

    @POST(Constants.WebServices.WS_INVITE_TO_APP)
    Call<BaseModel> inviteToApp(@Body SendInviteInput sendInviteInput);

    @PUT(Constants.WebServices.WS_BARBER_DECLINE_REQUEST + "{appointment_id}")
    Call<BaseModel> cancelAppointmentByBarber(@Path("appointment_id") String appointmentId, @Body CancelReasonInput cancelReasonInput);

    @GET(Constants.WebServices.WS_SUBSCRIPTION_PLANS)
    Call<SubscriptionPlans> getSubscriptionPlans();

    @POST(Constants.WebServices.WS_SUBSCRIBE_USER)
    Call<BaseModel> subscribeUser(@Body SubscriptionSuccessInput subscriptionSuccessInput);
}