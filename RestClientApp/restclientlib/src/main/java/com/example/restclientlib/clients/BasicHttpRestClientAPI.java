package com.example.restclientlib.clients;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.restclientlib.interfaces.RestServiceCallBack;
import com.example.restclientlib.utilities.LoggerUtils;
import com.example.restclientlib.utilities.MySSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;

/**
 * Created by anis on 5/21/17.
 */

public class BasicHttpRestClientAPI extends RestClientAPI {

    private static final String SUB_TAG = BasicHttpRestClientAPI.class.getSimpleName();

    private static ArrayList<NameValuePair> params;
    private static ArrayList<NameValuePair> headers;


    private static int responseCode;
    private static String message;

    private static String response;


    private static Activity activity;
    private static String serviceAuth;
    private static String BASE_URL_TEST;
    private static String messageBody;

    public BasicHttpRestClientAPI() {
        params = null;
        headers = null;
        responseCode = -1;
        message = null;
        response = null;
        activity = null;
        serviceAuth = null;
        BASE_URL_TEST = null;
        messageBody = null;
    }

    @Override
    public void init(Activity activity) {
        this.activity = activity;
        params = new ArrayList<NameValuePair>();
        headers = new ArrayList<NameValuePair>();
    }

    @Override
    public void setConfig(String url) {
        this.BASE_URL_TEST = url;
        Utilities.AddHeader("Content-Type", "application/json");
    }

    @Override
    public void authToken(String authToken) {
        this.serviceAuth = "Basic " + authToken;
    }

    @Override
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        Utilities.addParams(messageBody);
    }

    @Override
    public void callService(RestServiceCallBack restServiceCallBack) {
        new Utilities.RestClientAsyncTask(restServiceCallBack).execute();
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

        static void addParams(String messageBody) {
            try {
                JSONObject jobject = new JSONObject(messageBody);
                for (int i = 0; i < jobject.names().length(); i++) {
                    Utilities.AddParam(jobject.names().getString(i), jobject.getString(jobject.names().getString(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        static class RestClientAsyncTask extends AsyncTask<Void, Void, String> {

            private final RestServiceCallBack restServiceCallBack;

            public RestClientAsyncTask(RestServiceCallBack restServiceCallBack) {
                this.restServiceCallBack = restServiceCallBack;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    Utilities.Execute();
                    Log.d(SUB_TAG, "Response : " + response);
                    restServiceCallBack.onResponse(responseCode, response);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    Throwable t = new Throwable(exception.getMessage());
                    t.setStackTrace(exception.getStackTrace());
                    restServiceCallBack.onFailure(t);
                }
                return response;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        }

        static HttpClient getSSLHttpClient(HttpParams params) {
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);

                SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

                SchemeRegistry registry = new SchemeRegistry();
                registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                registry.register(new Scheme("https", sf, 443));

                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                return new DefaultHttpClient(params);
            }
        }

        static String convertStreamToString(InputStream is) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        static void AddParam(String name, String value) {
            params.add(new BasicNameValuePair(name, value));
        }

        static void AddHeader(String name, String value) {
            headers.add(new BasicNameValuePair(name, value));
        }

        static void executeRequest(HttpUriRequest request, String url) throws Exception {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 60 * 1000;
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutConnection);
            HttpClient client;

            if (url.startsWith("https://")) {
                client = Utilities.getSSLHttpClient(httpParameters);
            } else {
                client = new DefaultHttpClient(httpParameters);
            }
            request.setHeader("Authorization", serviceAuth);

            HttpResponse httpResponse;
            try {
                HttpProtocolParams.setUseExpectContinue(client.getParams(), false);
                httpResponse = client.execute(request);
                responseCode = httpResponse.getStatusLine().getStatusCode();
                message = httpResponse.getStatusLine().getReasonPhrase();
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    response = Utilities.convertStreamToString(instream);
                    instream.close();
                    LoggerUtils.d("BasicHttpRestClientAPI", response);
                }
            } catch (ConnectTimeoutException connectTimeoutException) {
                client.getConnectionManager().shutdown();
                throw connectTimeoutException;
            } catch (ClientProtocolException e) {
                client.getConnectionManager().shutdown();
                throw e;
            } catch (IOException e) {
                client.getConnectionManager().shutdown();
                throw e;
            }
        }

        static void Execute() throws Exception {
            try {
                HttpPost request = new HttpPost(BASE_URL_TEST);

                for (NameValuePair h : headers) {
                    request.addHeader(h.getName(), h.getValue());
                }

                if (!params.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    for (NameValuePair p : params) {
                        jsonObject.put(p.getName(), p.getValue());
                    }

                    String JSONobjectString = "";
                    if (jsonObject.toString().contains("\"{")) {
                        JSONobjectString = jsonObject.toString().replace("\"{", "{").replace("}\"", "}").replace("\\\"", "\"");
                    } else {
                        JSONobjectString = jsonObject.toString();
                    }
                    StringEntity stringEntity = new StringEntity(JSONobjectString);
                    stringEntity.setContentEncoding(HTTP.UTF_8);
                    request.setEntity(stringEntity);
                }

                if (EntityUtils.toString(request.getEntity()).length() > 4000) {
                    LoggerUtils.v("Result", "sb.length = " + EntityUtils.toString(request.getEntity()).length());
                    int chunkCount = EntityUtils.toString(request.getEntity()).length() / 4000; // integer division
                    for (int i = 0; i <= chunkCount; i++) {
                        int max = 4000 * (i + 1);
                        if (max >= EntityUtils.toString(request.getEntity()).length()) {
                            LoggerUtils.v("Result", "chunk " + i + " of " + chunkCount + ":" + EntityUtils.toString(request.getEntity()).substring(4000 * i));
                        } else {
                            LoggerUtils.v("Result", "chunk " + i + " of " + chunkCount + ":" + EntityUtils.toString(request.getEntity()).substring(4000 * i, max));
                        }
                    }
                } else {
                    LoggerUtils.v("Result", EntityUtils.toString(request.getEntity()).toString());
                }
                Utilities.executeRequest(request, BASE_URL_TEST);
            } catch (Exception e) {
                throw e;
            }
        }
    }

}
