package com.barbrdo.app.managers;

import android.content.Context;

import com.barbrdo.app.interfaces.CallBack;

import retrofit2.Call;


/**
 * Created by Anurag Sethi
 * This class will be responsible for the communication and will be used to call the webservice requests
 */
public class CommunicationManager {

    private Context context;

    /**
     * Constructor
     *
     * @param contextObj The Context from where the method is called
     * @return none
     */

    public CommunicationManager(Context contextObj) {
        this.context = contextObj;
    }

    /**
     * Call the required web service through library
     *
     * @param listnerObj object of interface CallBack
     * @param tasksID    the ID to differential multiple webservice calls
     * @param call       An invocation of a Retrofit method that sends a request to a webserver and returns a response.
     * @return none
     */

    public void CallWebService(CallBack listnerObj, int tasksID, Call call) {
        GetWebServiceData gwsdObj = new GetWebServiceData(listnerObj, tasksID, call);
        gwsdObj.getWebServiceData();
    }
}
