    package com.example.goals_setter_app;

    import androidx.appcompat.app.AppCompatActivity;
    import android.content.Context;
    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import java.io.BufferedReader;
    import java.io.FileInputStream;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.util.HashMap;
    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class LoginPage extends AppCompatActivity {
        private Retrofit retrofit;
        private RetrofitInterface retrofitInterface;
        private String BASE_URL = "http://192.168.1.209:4000";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login_page);

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            retrofitInterface = retrofit.create(RetrofitInterface.class);
            Context context = this;
            findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleLogin(context);
                }
            });
            FileInputStream fis = null;
            try {
                fis = openFileInput("localstorage.txt");
                if(fis != null){
                    Intent loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(loginIntent);
                    finish();
                }

            } catch (FileNotFoundException e) {

            }finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            findViewById(R.id.switchRegister).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent loginIntent = new Intent(getApplicationContext(), RegisterPage.class);
                    startActivity(loginIntent);
                    finish();
                }
            });
        }

        private void handleLogin(Context context) {
            EditText email = findViewById(R.id.email);
            EditText password =  findViewById(R.id.password);
            HashMap<String, String> map = new HashMap<>();
            if (TextUtils.isEmpty(email.getText().toString() ) ){
                Toast.makeText(getApplicationContext(), "Email cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }else if(TextUtils.isEmpty(password.getText().toString() ) ){
                Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            map.put("email", email.getText().toString());
            map.put("password", password.getText().toString());

            Call<LoginResult> call = retrofitInterface.executeLogin(map);
            call.enqueue(new Callback<LoginResult>() {
                @Override
                public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                    LoginResult loginResult = response.body();
                    if(response.code() == 200){
                        Toast.makeText(getApplicationContext(), loginResult.getEmail(), Toast.LENGTH_LONG).show();
                        FileOutputStream fos = null;
                        try {
                            fos = openFileOutput("localstorage.txt", Context.MODE_PRIVATE);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        String data  = loginResult.getToken();
                        try {
                            fos.write(data.getBytes());
                            fos.flush();
                            fos.close();
                            Log.i("localStorageStatus", "Working");
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                        Intent  mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainIntent);
                        finish();

                    }

                    if(response.code() == 500){
                                Toast.makeText(getApplicationContext(), "user not found", Toast.LENGTH_LONG).show();
                    }

                    if(response.code() == 400){
                                Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResult> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }


    }
