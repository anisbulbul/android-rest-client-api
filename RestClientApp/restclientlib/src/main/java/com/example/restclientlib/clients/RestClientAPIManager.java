package com.example.restclientlib.clients;

import android.util.Log;

/**
 * Created by anis on 5/21/17.
 */

public class RestClientAPIManager {
    public static String RESTCLIENT_API_BASIC = BasicHttpRestClientAPI.class.getSimpleName();
    public static String RESTCLIENT_API_RETROFIT = RetrofitRestClientAPI.class.getSimpleName();
    public static String RESTCLIENT_API_VOLLEY = VolleyRestClientAPI.class.getSimpleName();
    public static String RESTCLIENT_API_OKHTTP = OkHttpRestClientAPI.class.getSimpleName();
    private static RestClientAPI restClientAPI = null;

    private RestClientAPIManager() {

    }

    public static RestClientAPI loadClient(String restClientAPIClassName) {
        try {
            restClientAPI = (RestClientAPI) Class.forName(RestClientAPIManager.class.getPackage().getName() + "." + restClientAPIClassName).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return restClientAPI;
    }

}
