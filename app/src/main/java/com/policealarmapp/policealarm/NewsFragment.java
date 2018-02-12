package com.policealarmapp.policealarm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class NewsFragment extends Fragment {
    private ListView listView;
    private List<Map<String, Object>> mData;
    private FloatingActionButton reFreshButton;

    private OkHttpClient httpClient;
    public void loadData()
    {

        mData.clear();
        Request request = new Request.Builder()
                .url("http://120.203.70.158:8000/policenews/PoliceNewsIndex")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {




                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String jsonArray=response.body().string();
                            Log.d(TAG, jsonArray);
                            JSONArray newsArray= new JSONArray(jsonArray);
                            for (int i=0;i<newsArray.length();i++)
                            {
                                JSONObject object=newsArray.getJSONObject(i);


                                Map<String,Object> map=new HashMap<String,Object>();
                                map.put("NewsId",object.getString("NewsId"));
                                map.put("NewsTitle",object.getString("NewsTitle"));
                                map.put("NewsContent",object.getString("NewsContent"));

                                // DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                  map.put("CreateTime", object.getString("CreateTime").toString());


                                mData.add(map);

                                SimpleAdapter simpleAdapter=new SimpleAdapter(getContext(),mData,R.layout.listviewitem_police_news,new String[]{"NewsTitle","CreateTime"},new int[]{R.id.policeNewsTitle,R.id.policeNewsTime});

                                listView.setAdapter(simpleAdapter);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_news, container, false);

        listView=view.findViewById(R.id.policeNews);

        reFreshButton=view.findViewById(R.id.btn_refresh_news);
        mData=new ArrayList<Map<String,Object>>();

        httpClient=new OkHttpClient();

        reFreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String content = (String) ((Map<String, Object>) mData.get(i)).get("NewsContent");

                String title = (String) ((Map<String, Object>) mData.get(i)).get("NewsTitle");

                Intent intent=new Intent(getActivity(),NewsDetailActivity.class);
                intent.putExtra("NewsContent",content);
                intent.putExtra("NewsTitle",title);
                startActivity(intent);
            }
        });
        return  view;
    }

    @Override
    public void onResume() {

        loadData();
        super.onResume();
    }
}
