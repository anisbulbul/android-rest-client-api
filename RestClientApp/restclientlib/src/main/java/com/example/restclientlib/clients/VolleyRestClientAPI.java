package com.example.restclientlib.clients;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.restclientlib.controllers.VolleyAppController;
import com.example.restclientlib.interfaces.RestServiceCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anis on 5/21/17.
 */

public class VolleyRestClientAPI extends RestClientAPI {

    private static final String TAG = VolleyRestClientAPI.class.getSimpleName();

    private static Activity activity;
    private static String serviceAuth;
    private static String BASE_URL_TEST;
    private static String messageBody;
    private JSONObject messageBodyJson;

    public VolleyRestClientAPI() {
        activity = null;
        serviceAuth = null;
        BASE_URL_TEST = null;
        messageBody = null;
    }

    @Override
    public void init(Activity activity) {
        this.activity = activity;
        VolleyAppController.setVolleyAppController(activity);
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
        try {
            messageBodyJson = new JSONObject(messageBody);
        } catch (JSONException e) {
            e.printStackTrace();
            messageBodyJson = new JSONObject();
        }
    }

    @Override
    public void callService(final RestServiceCallBack restServiceCallBack) {
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, BASE_URL_TEST, messageBodyJson,
                new com.android.volley.Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        restServiceCallBack.onResponse(200, response.toString());
                        Map<String, String> headers;
                        VolleyAppController.getInstance().getRequestQueue().getCache().clear();
                    }
                },
                new com.android.volley.Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Throwable t = new Throwable(error.getMessage());
                        t.setStackTrace(error.getStackTrace());
                        restServiceCallBack.onFailure(t);
                        VolleyAppController.getInstance().getRequestQueue().getCache().clear();
                    }
                }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", serviceAuth);
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Map<String, String> headers = response.headers;
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    Log.d(TAG, "key :  : " + entry.getKey() + "value : " + entry.getValue());
                }
                Log.d(TAG, "mStatusCode : " + mStatusCode);
                return super.parseNetworkResponse(response);
            }
        };

        VolleyAppController.getInstance().addToRequestQueue(jsonObjReq, "NewVolleyRestClient");
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

    }
}
