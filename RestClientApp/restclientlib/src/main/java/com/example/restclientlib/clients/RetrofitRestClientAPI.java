package com.example.restclientlib.clients;

import android.app.Activity;
import android.util.Log;

import com.example.restclientlib.interfaces.RestServiceCallBack;
import com.example.restclientlib.interfaces.RetrofitAPIService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anis on 5/21/17.
 */

public class RetrofitRestClientAPI extends RestClientAPI {

    private static final String TAG = RetrofitRestClientAPI.class.getSimpleName();

    private static Activity activity;
    private static Retrofit retrofit;
    private static String serviceAuth;
    private static Call<Object> agentCall;
    private static RetrofitAPIService retrofitAPIService;
    private static Gson gson;
    private static String BASE_URL_TEST;
    private static String messageBody;

    public RetrofitRestClientAPI() {
        activity = null;
        retrofit = null;
        serviceAuth = null;
        agentCall = null;
        retrofitAPIService = null;
        gson = null;
        BASE_URL_TEST = null;
        messageBody = null;
    }

    @Override
    public void init(Activity activity) {
        this.activity = activity;
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Override
    public void setConfig(String url) {
        this.BASE_URL_TEST = url;
        retrofit = new Retrofit.Builder().baseUrl(Utilities.getUrlAuthority()).addConverterFactory(GsonConverterFactory.create(gson)).build();
        retrofitAPIService = retrofit.create(RetrofitAPIService.class);
    }

    @Override
    public void authToken(String authToken) {
        this.serviceAuth = "Basic " + authToken;
    }

    @Override
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public void callService(final RestServiceCallBack restServiceCallBack) {

        agentCall = retrofitAPIService.postValue(gson.fromJson(messageBody, Object.class), Utilities.getUrlPath(), serviceAuth);
        agentCall.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                restServiceCallBack.onResponse(response.code(), gson.toJson(response.body()));
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                restServiceCallBack.onFailure(t);
            }
        });
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
        static String getUrlAuthority() {
            try {
                String ss = new URL(BASE_URL_TEST).getAuthority();
                Log.d("sss_jsong_url", ss);
                return "http://" + ss + "/";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";
            }
        }

        static String getUrlPath() {
            try {
                String ss = new URL(BASE_URL_TEST).getPath();
                Log.d("sss_jsong_url2", ss);
                return ss.substring(1);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";
            }
        }

    }
}
