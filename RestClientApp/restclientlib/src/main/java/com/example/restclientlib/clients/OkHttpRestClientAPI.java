package com.example.restclientlib.clients;

import android.app.Activity;
import android.util.Log;

import com.example.restclientlib.interfaces.RestServiceCallBack;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by anis on 5/21/17.
 */

public class OkHttpRestClientAPI extends RestClientAPI {

    private static final String TAG = OkHttpRestClientAPI.class.getSimpleName();
    private static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    private static Activity activity;
    private static String serviceAuth;
    private static String BASE_URL_TEST;
    private static String messageBody;
    private static RequestBody requestBody;
    private static OkHttpClient okHttpClient;

    public OkHttpRestClientAPI() {
        activity = null;
        serviceAuth = null;
        BASE_URL_TEST = null;
        messageBody = null;
        requestBody = null;
        okHttpClient = null;
    }

    @Override
    public void init(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void setConfig(String url) {
        this.BASE_URL_TEST = url;
    }

    @Override
    public void authToken(String authToken) {
        this.serviceAuth = "Basic " + authToken;
    }

    @Override
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        requestBody = RequestBody.create(mediaType, this.messageBody);
    }

    @Override
    public void callService(final RestServiceCallBack restServiceCallBack){

        Callback loginCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "login failed: ");
                Log.i(TAG, "login failed: Error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    restServiceCallBack.onResponse(response.code(), response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    Throwable t = new Throwable(e.getMessage());
                    t.setStackTrace(e.getStackTrace());
                    restServiceCallBack.onFailure(t);
                }
            }
        };

        okHttpClient = new OkHttpClient.Builder()
                .authenticator(Utilities.getAuthentication(this.serviceAuth))
                .protocols(Arrays.asList(Protocol.HTTP_1_1, Protocol.HTTP_2))
                .build();
        okHttpClient.newCall(new Request.Builder().url(BASE_URL_TEST).post(requestBody).build()).enqueue(loginCallback);
    }

    @Override
    public void restService(Activity activity, String url, String authToken, String messageBody, RestServiceCallBack restServiceCallBack) {
        init(activity);
        setConfig(url);
        authToken(authToken);
        setMessageBody(messageBody);
        callService(restServiceCallBack);
    }

    private static class Utilities {
        static Authenticator getAuthentication(final String authenticationToken) {
            return new Authenticator() {
                @Override
                public okhttp3.Request authenticate(Route route, okhttp3.Response response) throws IOException {
                    return response.request().newBuilder().header("Authorization", authenticationToken).build();
                }

            };
        }
    }
}
