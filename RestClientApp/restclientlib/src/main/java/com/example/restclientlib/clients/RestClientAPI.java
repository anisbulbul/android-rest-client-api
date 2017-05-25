package com.example.restclientlib.clients;


import android.app.Activity;

import com.example.restclientlib.interfaces.RestServiceCallBack;

import java.io.IOException;

import retrofit2.Callback;

/**
 * Created by ayusuf on 12/28/2016.
 */

public abstract class RestClientAPI {
    public abstract void init(Activity activity);
    public abstract void setConfig(String url);
    public abstract void authToken(String authToken);
    public abstract void setMessageBody(String messageBody);
    public abstract void callService( RestServiceCallBack restServiceCallBack);
    public abstract void restService(Activity activity, String url, String authToken, String messageBody, RestServiceCallBack restServiceCallBack);
}

