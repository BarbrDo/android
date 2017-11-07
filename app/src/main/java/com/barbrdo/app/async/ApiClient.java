package com.barbrdo.app.async;


import android.text.TextUtils;

import com.barbrdo.app.utils.Constants;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static String LATITUDE = "";
    public static String LONGITUDE = "";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(Constants.WebServices.WS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null, null, null, null, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken, final String userId,
                                      final String lat, final String lng, final String token, final String userType) {
        if (authToken != null) {
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    // Request customization: add request headers
                    Request.Builder requestBuilder;
                    if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)) {
                        requestBuilder = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("device_type", "Android")
                                .header("device_id", FirebaseInstanceId.getInstance().getToken())
                                .header("device_latitude", lat)
                                .header("device_longitude", lng)
                                .header("Authorization", "Bearer " + token)
                                .header("user_id", userId)
                                .header("user_type", userType)
                                .method(original.method(), original.body());
                    } else {
                        requestBuilder = original.newBuilder()
                                .header("Accept", "application/json")
                                .header("device_type", "Android")
                                .header("device_id", FirebaseInstanceId.getInstance().getToken())
                                .header("device_latitude", LATITUDE)
                                .header("device_longitude", LONGITUDE)
                                .header("Authorization", "Bearer " + token)
                                .header("user_id", userId)
                                .header("user_type", userType)
                                .method(original.method(), original.body());
                    }

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
