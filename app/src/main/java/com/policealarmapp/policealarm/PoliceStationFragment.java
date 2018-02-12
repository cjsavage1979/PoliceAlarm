package com.policealarmapp.policealarm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PoliceStationFragment extends Fragment {


    private ListView listView;
    private ArrayList<HashMap<String, Object>> mData;
    FloatingActionButton btn_refresh_stations;
    private void  initData()
    {
        mData=((MainActivity)getActivity()).getStations();
        SimpleAdapter simpleAdapter=new SimpleAdapter(this.getContext(),mData,R.layout.listviewitem_police_station,new String[]{"policeSationName","policeAddress"},new int[]{R.id.policeSationName,R.id.policeAddress});
        listView.setAdapter(simpleAdapter);
    }


    public void    setStations(ArrayList<HashMap<String,Object>> datas)
    {
       mData=datas;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mData=new ArrayList<HashMap<String, Object>>();
        View view=inflater.inflate(R.layout.fragment_police_station, container, false);
        btn_refresh_stations=view.findViewById(R.id.btn_refresh_stations);
        btn_refresh_stations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initData();

            }
        });

          //  initData();


        listView=view.findViewById(R.id.policeStationList);

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        initData();
    }
}
