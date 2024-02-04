package com.example.goals_setter_app;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST ("/users/login")
    Call<LoginResult> executeLogin(@Body HashMap<String, String> map);

    @POST ("/users/register")
    Call<Void> executeRegister(@Body HashMap<String, String> map);


}

