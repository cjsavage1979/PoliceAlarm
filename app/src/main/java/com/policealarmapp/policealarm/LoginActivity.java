package com.policealarmapp.policealarm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnRegister;
    EditText txtCellPhone;
    EditText txtPwd;
    SharedPreferences loginInfor;
    OkHttpClient httpClient;
    CheckBox ckRememberPwd;
    private static final String TAG = "LoginActivity";
    void initViews()
    {
        btnLogin=findViewById(R.id.btnLogin);
        btnRegister=findViewById(R.id.btnRegister);
        txtCellPhone=findViewById(R.id.txtUserName);
        txtPwd=findViewById(R.id.txtUserPwd);
        httpClient=new OkHttpClient();
        ckRememberPwd=findViewById(R.id.ckRememberPwd);
        loginInfor=getSharedPreferences("CallPolice", Activity.MODE_PRIVATE);

        ckRememberPwd.setChecked(loginInfor.getBoolean("RememberPwd",false));
        if(ckRememberPwd.isChecked())
        {
            txtPwd.setText(loginInfor.getString("UserPwd",""));
            txtCellPhone.setText(loginInfor.getString("UserCellPhone",""));
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestBody formBody =new FormBody.Builder()
                        .add("userCellphone",txtCellPhone.getText().toString())
                        .add("userPwd",txtPwd.getText().toString())
                        .build();
                final  Request request=new Request.Builder()
                        .url("http://120.203.70.158:8000/users/UserLogin")
                        .post(formBody)
                        .build();

                Call call=httpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {


                        final String resp=response.body().string();
                        Log.d(TAG,resp);

                        try {
                            JSONObject json=new JSONObject(resp);
                            if(json.getBoolean("success")==true)
                            {
                                SharedPreferences.Editor editor = loginInfor.edit();
                                if (ckRememberPwd.isChecked()) {

                                    editor.putString("UserCellPhone", txtCellPhone.getText().toString());
                                    editor.putString("UserPwd", txtPwd.getText().toString());
                                    editor.putBoolean("RememberPwd",true);
                                    editor.commit();

                                } else
                                {
                                    editor.putString("UserCellPhone", "");
                                    editor.putString("UserPwd","");
                                    editor.putBoolean("RememberPwd",false);
                                    editor.commit();
                                }
                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("UserID",json.getInt("userId"));
                                startActivity(intent);
                                Log.e(TAG, "onResponse: ");
                                LoginActivity.this.finish();

                            }
                            else
                            {
                                LoginActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


            }
        });


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }
}
