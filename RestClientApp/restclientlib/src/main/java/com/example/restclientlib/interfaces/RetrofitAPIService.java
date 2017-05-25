package com.example.restclientlib.interfaces;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitAPIService {
    @POST("{serviceName}")
    Call<Object> postValue(@Body Object o, @Path(value = "serviceName", encoded = true) String serviceName, @Header("authorization") String contentRange);
}
