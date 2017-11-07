package com.barbrdo.app.managers;

import android.content.Context;
import android.util.Log;

import com.barbrdo.app.interfaces.CallBack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetWebServiceData {
    CallBack callbackObj;
    int tasksID;
    Call call;
    String result;
    static int errorTaskID = 404;
    Context context;

    public GetWebServiceData(Context contextObj) {
        this.context = contextObj;
    }

    public GetWebServiceData(CallBack listnerObj, int tasksID, Call call) {
        this.callbackObj = listnerObj;
        this.tasksID = tasksID;
        this.call = call;
    }

    public void getWebServiceData() {
        this.call.enqueue(new Callback() {
            public void onResponse(Call call, Response response) {
                int statusCode = response.code();
                Log.e("response", "" + response.body());
                GetWebServiceData.this.callbackObj.onResult(response, GetWebServiceData.this.tasksID, statusCode, response.message());
            }

            public void onFailure(Call call, Throwable t) {
                GetWebServiceData.this.result = t.toString();
                GetWebServiceData.this.callbackObj.onResult((Response) null, GetWebServiceData.this.tasksID, GetWebServiceData.errorTaskID, t.getMessage());
            }
        });
    }
}
