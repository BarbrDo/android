package com.barbrdo.app.utils;

public class Constants {

    /**
     * Handles the SplashScreen constants
     */
    public static class SplashScreen {
        /**
         * The parameter is used to manage the splash screen delay
         */
        public static int SPLASH_DELAY_LENGTH = 2000;
    }


    /**
     * Handles webservice constants
     */
    public static class WebServices {
        public static String WS_BASE_URL = "http://app.barbrdo.com/api/v2/";
        //        public static String WS_BASE_URL = "http://52.39.212.226:4062/api/v2/";
        //        public static String WS_BASE_URL = "http://172.24.5.248:3000/api/v2/";
        public static final String WS_USER_LOG_IN = "login";
        public static final String WS_USER_SIGN_UP = "signup";
        public static final String WS_FORGOT_PASSWORD = "forgot";
        public static final String WS_APPOINTMENT = "customer/appointment";
        public static final String WS_SHOPS = "shops";
        public static final String WS_CHECK_FACEBOOK = "checkFaceBook";
        public static final String WS_CUSTOMER_GALLERY = "customer/gallery/";
        public static final String WS_USER_PROFILE = "userprofile/";
        public static final String WS_ACCOUNT = "account";
        public static final String WS_BARBER_GALLERY = "barber/gallery/";
        public static final String WS_CONTACT = "contact/";
        public static final String WS_RATE_BARBER = "rateBarber/";
        public static final String WS_CONTACT_SHOP = "barber/contactshop";
        public static final String WS_BARBER_FINANCE = "barber/sale/";
        public static final String WS_CONTACT_BARBER = "contactbarber";
        public static final String WS_LOGOUT = "logout";
        public static final String WS_CUSTOMER_BARBERS = "customer/barbers";
        public static final String WS_CUSTOMER_REQUEST = "customer/appointment/newrequest";
        public static final String WS_CANCEL_APPOINTMENT = "customer/appointment/cancel/";
        public static final String WS_FAVORITE_BARBER = "customer/favouritebarber/";
        public static final String WS_BARBER_HOME = "barber/home";
        public static final String WS_BARBER_GO_ONLINE = "barber/goOnline";
        public static final String WS_BARBER_GO_OFFLINE = "barber/goOffline";
        public static final String WS_BARBER_DECLINE_REQUEST = "barber/appointment/decline/";
        public static final String WS_BARBER_ACCEPT_REQUEST = "barber/appointment/accept/";
        public static final String WS_BARBER_CHECK_IN = "barber/checkin/";
        public static final String WS_BARBER_DETAIL = "barberdetail/";
        public static final String WS_DEFAULT_SHOP = "barber/makeDefaultshop";
        public static final String WS_MESSAGE_TO_BARBER = "customer/messageToBarber";
        public static final String WS_MESSAGE_TO_CUSTOMER = "barber/messageToCustomer";
        public static final String WS_SEARCH_SHOPS = "barber/shops";
        public static final String WS_STATES = "states";
        public static final String WS_ASSOCIATE_SHOP = "barber/shop";
        public static final String WS_DELETE_SHOP = "barber/associatedshops/";
        public static final String WS_ADD_SHOP = "shop/request";
        public static final String WS_INVITE_TO_APP = "referapp";
        public static final String WS_SUBSCRIPTION_PLANS = "plan";
        public static final String WS_SUBSCRIBE_USER = "subscribe";
    }

    /**
     * Handles the TaskIDs so as to differentiate the web service return values
     */
    public static class TaskID {
        public static final int LOGIN = 100;
        public static final int SIGN_UP = 101;
        public static final int FORGOT_PASSWORD = 102;
        public static final int APPOINTMENT = 103;
        public static final int SHOPS = 104;
        public static final int BARBERS = 105;
        public static final int BARBER_SERVICES = 106;
        public static final int SHOP_BARBERS = 107;
        public static final int CHECK_FACEBOOK = 108;
        public static final int CUSTOMER_GALLERY = 114;
        public static final int USER_PROFILE = 116;
        public static final int PROFILE_PICTURE = 117;
        public static final int ACCOUNT = 118;
        public static final int BARBER_GALLERY = 119;
        public static final int BARBER_CANCEL_APPOINTMENT = 123;
        public static final int BARBER_RESCHEDULE_APPOINTMENT = 124;
        public static final int UPDATE_SHOP = 130;
        public static final int CONTACT = 135;
        public static final int RATE_BARBER = 137;
        public static final int SHOP_PROFILE = 138;
        public static final int CONTACT_SHOP = 139;
        public static final int BARBER_FINANCE = 146;
        public static final int SHOP_DETAIL = 147;
        public static final int CONTACT_BARBER = 150;
        public static final int SHOP_FINANCE = 153;
        public static final int LOGOUT = 155;
        public static final int CUSTOMER_BARBERS = 156;
        public static final int CUSTOMER_REQUEST = 157;
        public static final int CANCEL_APPOINTMENT = 158;
        public static final int FAVORITE_BARBER = 159;
        public static final int DELETE_FAVORITE_BARBER = 160;
        public static final int BARBER_HOME = 161;
        public static final int GO_ONLINE = 162;
        public static final int GO_OFFLINE = 163;
        public static final int BARBER_DECLINE_REQUEST = 164;
        public static final int BARBER_ACCEPT_REQUEST = 165;
        public static final int BARBER_CHECK_IN = 166;
        public static final int BARBER_DETAIL = 167;
        public static final int BARBER_DEFAULT_SHOP = 168;
        public static final int MESSAGE_BARBER = 169;
        public static final int MESSAGE_CUSTOMER = 170;
        public static final int SEARCH_SHOPS = 171;
        public static final int STATES = 172;
        public static final int ASSOCIATE_SHOP = 173;
        public static final int DELETE_SHOP = 174;
        public static final int ADD_SHOP = 175;
        public static final int INVITE_TO_APP = 176;
        public static final int CANCEL_APPOINTMENT_BARBER = 177;
        public static final int SUBSCRIPTION_PLANS = 178;
        public static final int SUBSCRIBE_USER = 179;
    }

    /**
     * Handles the requestCodes
     */
    public static class RequestCodes {
        public static final int GALLERY = 100;
        public static final int ADD_EVENT = 101;
        public static final int APP_SUBSCRIPTION = 102;

    }

    /**
     * This is for Bundle key
     * BundleKey Tag
     */
    public static class BundleKeyTag {
        public static final String BUNDLE = "bundle";
        public static final String SERIALIZED_DATA = "serialized_data";
        public static final String BARBER_DATA = "barber_data";
        public static final String HOME = "home";
        public static final String BARBER_ID = "barber_id";
        public static final String TAB = "tab";
        public static final String PAGE = "page";
        public static final String CUSTOMER_ID = "customer_id";
        public static final String SELECTED_TIME = "selected_time";
        public static final String MESSAGE = "message";
        public static final String IS_REQUEST_ACCEPTED = "is_request_accepted";
        public static final String IS_CHECK_IN = "is_check_in";
        public static final String MESSAGE_TO_BARBER = "message_to_barber";
        public static final String IS_SUBSCRIPTION_EXPIRED = "is_subscription_expired";
        public static final String IS_UPGRADE = "is_upgrade";
        public static final String IS_USER_PROFILE = "is_user_profile";

    }

    public static class DateFormat {
        public static final String YYYYMMDD = "yyyy-MM-dd";
        public static final String YYYYMMDDTHHMMSS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public static final String DDMMYYHHMMA = "dd/MM/yyyy, hh:mm a";
        public static final String HHMMA = "hh:mm a";
        public static final String MMMYYYY = "MMM yyyy";
        public static final String MMMDD = "MMM dd";
        public static final String MMMDDYYYY = "MMM dd yyyy";
        public static final String MMMMDDYYYY = "MMM dd, yyyy";
        public static final String HHMMAA = "hh:mma";
        public static final String HHMM = "HH:mm";
        public static final String HHMMSS = "HH:mm:ss";
        public static final String DDMMYYYY = "dd/MM/yyyy";
        public static final String EEEEDDMMM = "EEEE, dd MMM";
        public static final String MMMDDYYYYHHMM = "MMM dd yyyy, hh:mm a";
        public static final String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
        public static final String MMDDYYYY = "MM/dd/yyyy";
    }

    public static class CustomerTab {
        public static final String HOME = "home";
        public static final String GALLERY = "gallery";
        public static final String NOTIFICATIONS = "notifications";
    }

    public static class UserType {
        public static final int CUSTOMER = 1;
        public static final int BARBER = 2;
        public static final int SHOP = 3;
    }

    public static class Screen {
        public static final int APPOINTMENT = 1;
        public static final int CHAIR_BOOKING = 2;
        public static final int ADD_EDIT_SERVICES = 3;
        public static final int FINANCIAL_CENTER = 4;
        public static final int MANAGE_SHOPS = 5;
    }

    public static class InApp {
        public static String PURCHASE_TYPE = "subs";
    }

    public static class GoogleAPI {
        public static final String GOOGLE_API_KEY = "AIzaSyBWFq_UoUG_zusW-CL2nRXhwqhPQ8gVpuc";
    }
}
