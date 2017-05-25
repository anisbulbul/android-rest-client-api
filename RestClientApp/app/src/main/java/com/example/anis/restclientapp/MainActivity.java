package com.example.anis.restclientapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.restclientlib.clients.RestClientAPI;
import com.example.restclientlib.clients.RestClientAPIManager;
import com.example.restclientlib.interfaces.RestServiceCallBack;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private RestClientAPI restClientAPI;
    private String serviceAuthToken = Base64.encodeToString(("kalam" + ":" + "3608a6d1a05aba23ea390e5f3b48203dbb7241f7").getBytes(), Base64.NO_WRAP);
    private JSONObject jsonObject;
    private Button demo1, demo2, testValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        demo1 = (Button) findViewById(R.id.demo1);
        demo2 = (Button) findViewById(R.id.demo2);
        testValue = (Button) findViewById(R.id.testValue);

        try {
            jsonObject = new JSONObject("{\"requestID\":\"353145050042577/353145050042577###20160808085458\",\"loginID\":\"kalam\",\"password\":\"3608a6d1a05aba23ea390e5f3b48203dbb7241f7\",\"mobileIMEI\":\"353145050042577\",\"operationType\":\"agentLogin\"}");
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
        }

        demo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoAPIClient();
            }
        });
        demo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                demoAPIClient2();
            }
        });
        testValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testDemo();
            }
        });


    }

    private void testDemo() {

    }

    private void demoAPIClient() {
        restClientAPI = RestClientAPIManager.loadClient(RestClientAPIManager.RESTCLIENT_API_OKHTTP);
        restClientAPI.init(MainActivity.this);
        restClientAPI.setConfig("http://10.16.16.59/agent/agentlogin");
        restClientAPI.authToken(serviceAuthToken);
        restClientAPI.setMessageBody(jsonObject.toString());
        restClientAPI.callService(new RestServiceCallBack() {
            @Override
            public void onResponse(int responseCode, String messageBody) {
                Log.e(TAG, "responseCode : " + responseCode + ", messageBody : " + messageBody);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Throwable : " + t.getMessage());
            }
        });
    }

    private void demoAPIClient2() {
        restClientAPI = RestClientAPIManager.loadClient(RestClientAPIManager.RESTCLIENT_API_OKHTTP);
        restClientAPI.restService(MainActivity.this, "http://10.16.16.59/agent/agentlogin", serviceAuthToken, jsonObject.toString(), new RestServiceCallBack() {
            @Override
            public void onResponse(int responseCode, String messageBody) {
                Log.e(TAG, "responseCode : " + responseCode + ", messageBody : " + messageBody);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Throwable : " + t.getMessage());
            }
        });
    }
}
