package com.example.goals_setter_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterPage extends AppCompatActivity {
    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://192.168.1.209:4000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);

        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        findViewById(R.id.switchLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(loginIntent);
                finish();
            }
        });
       
    }

    private void handleRegister() {
        final EditText email = findViewById(R.id.regEmail);
        final EditText password =  findViewById(R.id.regPassword);
        final EditText username =  findViewById(R.id.regUsername);
        if (TextUtils.isEmpty(email.getText().toString() ) ){
            Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }else if(TextUtils.isEmpty(password.getText().toString() ) ){
            Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }else if (TextUtils.isEmpty(username.getText().toString() ) ){
            Toast.makeText(getApplicationContext(), "Username cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        map.put("email",email.getText().toString());
        map.put("password",password.getText().toString());
        map.put("username",username.getText().toString());

        Call<Void> call = retrofitInterface.executeRegister(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200){
                    Toast.makeText(getApplicationContext(), "Signed Up Successfully", Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent(getApplicationContext(), LoginPage.class);
                    startActivity(loginIntent);
                }

                if (response.code() == 400){
                    Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }




}