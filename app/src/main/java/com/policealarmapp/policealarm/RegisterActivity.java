package com.policealarmapp.policealarm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {


    private EditText txtCellPhone;

    private EditText txtPassword;

    private EditText txtRePassword;

    private Button btnRegister;
    private OkHttpClient httpClient;
    private static final String TAG = "RegisterActivity";


    private void initView() {
        txtCellPhone = findViewById(R.id.txtCellPhone);
        txtPassword = findViewById(R.id.txtUserPwd);
        txtRePassword = findViewById(R.id.txtUserReCheckPwd);
        btnRegister = findViewById(R.id.btnRegister);

        httpClient = new OkHttpClient();
    }

    private void initAction() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!txtPassword.getText().toString().equals(txtRePassword.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, "两次输入的密码不一致", Toast.LENGTH_LONG).show();

                    return;

                }

                if (txtCellPhone.getText().toString().length() == 0 || txtPassword.getText().toString().length() == 0
                        || txtRePassword.getText().toString().length() == 0) {
                    Toast.makeText(RegisterActivity.this, "手机号或者密码输入不能为空", Toast.LENGTH_LONG).show();
                    return;

                }


                RequestBody formBody = new FormBody.Builder()
                        .add("userCellphone", txtCellPhone.getText().toString())
                        .add("userPwd", txtPassword.getText().toString())
                        .build();


                final Request request = new Request.Builder()
                        .url("http://120.203.70.158:8000/users/CreateUser")
                        .post(formBody)
                        .build();

                Call call = httpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {


                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    String returns = response.body().string();
                                    Log.d(TAG, returns);


                                    JSONObject returnObject = new JSONObject(returns);
                                    if (returnObject.getBoolean("success") == true) {
                                        Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                        RegisterActivity.this.finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "该手机已经被注册，请勿重复注册", Toast.LENGTH_SHORT)
                                                .show();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                    }
                });


            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initAction();
    }


}
