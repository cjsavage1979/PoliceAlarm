package com.policealarmapp.policealarm;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


public class  AlarmFragment extends Fragment {


    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int PHOTO_REQUEST_VIDEO = 3;// 视频
    public static final int CROP_PHOTO = 2;

    private ImageView picture;
    private VideoView video;
    private Button takeVideo;
    private Button takePhoto;
    private Button btnAlarm;

    private  int userId;

    private OkHttpClient httpClient;


    private File file;
    private File vidoFile;
    private Uri imageUri;
    private Uri videoUri;
    public static File tempFile;

    public static  File tempVideoFile;
    private static final String TAG = "AlarmFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //initPhoto();

        View view=inflater.inflate(R.layout.fragment_alarm, container, false);
        picture=view.findViewById(R.id.imgViewPhoto);
        video=view.findViewById(R.id.imgViewCamera);
        takePhoto=view.findViewById(R.id.btnTakePhoto);
        takeVideo=view.findViewById(R.id.btnTakeVideo);

        btnAlarm=view.findViewById(R.id.btn_alarm);

        httpClient=new OkHttpClient();
        userId = getActivity().getIntent().getIntExtra("UserID", 0);

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera(getActivity());
            }
        });

        takeVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVideo(getActivity());
            }
        });


        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MultipartBody.Builder requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                double latitude=((MainActivity)getActivity()).getLatitude();
                double longtitude=((MainActivity)getActivity()).getLontitude();
                requestBody.addFormDataPart("UserId",String.valueOf(userId))
                        .addFormDataPart("Longitide",String.valueOf(longtitude))
                        .addFormDataPart("Latitude",String.valueOf(latitude))
                        .addFormDataPart("FileType",String.valueOf(1))
                        .addFormDataPart("AlarmContent","Alarm");


                Log.i(TAG, "onClick: "+String.valueOf(longtitude)+"-"+String.valueOf(latitude));
                if(tempFile.length()>0)
                {
                    RequestBody body = RequestBody.create(MediaType.parse("image/*"), tempFile);
                    String filename = tempFile.getName();
                    // 参数分别为， 请求key ，文件名称 ， RequestBody
                    requestBody.addFormDataPart("AlarmFile", filename, body);
                }

                Request request = new Request.Builder().url("http://120.203.70.158:8000/Alarms/CreateAlarm").post(requestBody.build()).tag(getContext()).build();

                httpClient.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"报警提交失败",Toast.LENGTH_LONG).show();;
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"报警提交成功",Toast.LENGTH_LONG).show();;
                            }
                        });
                    }
                });





            }
        });

        return  view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case PHOTO_REQUEST_CAREMA:
                if (resultCode == RESULT_OK) {
                    // Intent intent = new Intent("com.android.camera.action.CROP");
                    // intent.setDataAndType(imageUri, "image/*");
                    // intent.putExtra("scale", true);
                    // intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    // startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序

                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver()
                                .openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);

                        Toast.makeText(getContext(),imageUri.getPath().toString(),Toast.LENGTH_LONG).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver()
                                .openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PHOTO_REQUEST_VIDEO:
                if(resultCode==RESULT_OK)
                {
                    Toast.makeText(getContext(),videoUri.getPath().toString(),Toast.LENGTH_LONG).show();
                     video.setVideoPath(videoUri.getPath());
                    video.start();
                }
                break;


        }
    }



    public void openCamera(Activity activity) {
        //獲取系統版本
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                    "yyyy_MM_dd_HH_mm_ss");
            String filename = timeStampFormat.format(new Date());
            tempFile = new File(Environment.getExternalStorageDirectory(),
                    filename + ".jpg");
            if (currentapiVersion < 24) {
                // 从文件中创建uri
                imageUri = Uri.fromFile(tempFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                //兼容android7.0 使用共享文件的形式
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
                //检查是否有存储权限，以免崩溃
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    Toast.makeText(getContext(),"请开启存储权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }
        }
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }


    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }



    public void openVideo(Activity activity)
    {
        Intent intent = new Intent();
        intent.setAction("android.media.action.VIDEO_CAPTURE");
        intent.addCategory("android.intent.category.DEFAULT");
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        SimpleDateFormat timeStampFormat = new SimpleDateFormat(
                "yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        tempVideoFile = new File(Environment.getExternalStorageDirectory(),
                filename + ".mp4");

        Log.d(TAG, "openVideo: "+tempVideoFile);
        // 保存录像到指定的路径
        if (currentapiVersion < 24) {
            videoUri = Uri.fromFile(tempVideoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);

            startActivityForResult(intent, PHOTO_REQUEST_VIDEO);

        }
        else
        {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Video.Media.DATA, tempVideoFile.getAbsolutePath());
            //检查是否有存储权限，以免崩溃
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                Toast.makeText(getContext(),"请开启存储权限",Toast.LENGTH_SHORT).show();
                return;
            }
            videoUri = activity.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(intent, PHOTO_REQUEST_VIDEO);
        }


    }



}
