package com.policealarmapp.policealarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;


public class SettingFragment extends Fragment {

    private EditText txtUserName;
    private EditText txtUserPhone;

    private EditText txtUserRelateivePhone;
    private EditText txtUserHomeAddress;
    private Button btnSaveUserInfo;

    private  OkHttpClient httpClient;
    private View rootView;

    private int userId;
    private  void  initView()
    {
        txtUserName=rootView.findViewById(R.id.txtUserInforName);
        txtUserPhone=rootView.findViewById(R.id.txtUserInforCellPhone);

        txtUserRelateivePhone=rootView.findViewById(R.id.txtUserInforRelativePhone);
        txtUserHomeAddress=rootView.findViewById(R.id.txtUserInforHomeAddress);
        btnSaveUserInfo=rootView.findViewById(R.id.btnSaveUserInfor);

        httpClient=new OkHttpClient();

        btnSaveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfor();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_setting, container, false);
        rootView=view;

        initView();

        userId=getActivity().getIntent().getIntExtra("UserID",0);



        loadData();

        return view;
    }


    private void  loadData()
    {

        RequestBody requestBody= new FormBody.Builder()
                .add("userId",String.valueOf(userId))
                .build();

        final Request request = new Request.Builder()
                .url("http://120.203.70.158:8000/users/GetUserInfor")
                .post(requestBody)
                .build();

        Call call=httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"获取用户数据失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content=response.body().string();

                try {
                    final JSONObject jsonObject=new JSONObject(content);

                    if(jsonObject.getBoolean("success"))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    txtUserName.setText(jsonObject.getString("UserName"));
                                    txtUserPhone.setText(jsonObject.getString("UserCellPhone"));
                                    txtUserHomeAddress.setText(jsonObject.getString("UserAddress"));
                                    txtUserRelateivePhone.setText(jsonObject.getString("UserRelative"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });





    }

    private void  updateUserInfor()
    {
        RequestBody requestBody= new FormBody.Builder()
                .add("userId",String.valueOf(userId))
                .add("userCellPhone",txtUserPhone.getText().toString())
                .add("userName",txtUserName.getText().toString())
                .add("UserRelativeCellPhone",txtUserRelateivePhone.getText().toString())
                .add("address",txtUserHomeAddress.getText().toString())
                .build();

        final Request request = new Request.Builder()
                .url("http://120.203.70.158:8000/users/UpdateUserInfor")
                .post(requestBody)
                .build();

        Call call=httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"网络通讯失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String conteng=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(conteng);
                    if(jsonObject.getBoolean("success"))
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"更新成功",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"更新失败",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {



        super.onResume();
    }
}
