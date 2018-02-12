package com.policealarmapp.policealarm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bottomBar;
    private ArrayList<Fragment> mViewPagerContent=new ArrayList<Fragment>();

    private FragmentPagerAdapter fragmentPagerAdapter;
    private FragmentManager fragmentManager;

    private  int UserId;
    private static final String TAG = "MainActivity";

    private double lontitude;
    private double latitude;
    private ArrayList<HashMap<String, Object>> mData;

    private void initViews()
    {
        bottomBar=findViewById(R.id.bottom_navigation_bar);

    }

    public void setStationDatas(ArrayList<HashMap<String, Object>> datas)
    {
        mData=datas;
    }

    public ArrayList<HashMap<String, Object>> getStations()
    {
        return  mData;
    }

    public  void AddStation(String name,String address)
    {
        HashMap<String,Object> map=new HashMap<String,Object>();
        map.put("policeSationName",name);
        map.put("policeAddress",address);
        mData.add(map);
    }

    public void ClearStations()
    {
        mData.clear();
    }



    public void setPosition(double lontitude1,double latitude1)
    {
        this.lontitude=lontitude1;
        this.latitude=latitude1;
    }

    public  double getLontitude()
    {
        return  lontitude;
    }

    public  double getLatitude()
    {
        return  latitude;
    }

    private void initbottomView()
    {
        bottomBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomBar.addItem(new BottomNavigationItem(R.mipmap.map,R.string.bottom_bar_map))
                .addItem(new BottomNavigationItem(R.mipmap.police,R.string.bottom_bar_police))
                .addItem(new BottomNavigationItem(R.mipmap.alarm,"报警"))
                .addItem(new BottomNavigationItem(R.mipmap.news,R.string.bottom_bar_news))
                .addItem(new BottomNavigationItem(R.mipmap.setting,R.string.bottom_bar_setting))
                .initialise();
        bottomBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {

                if(mViewPagerContent!=null)
                {
                    if(position<mViewPagerContent.size())
                    {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment fragment=mViewPagerContent.get(position);
                        ft.replace(R.id.frameLayout_content,fragment);

                        ft.commit();
                    }
                }


            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                if(mViewPagerContent!=null)
                {
                    if(position<mViewPagerContent.size())
                    {
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment fragment = mViewPagerContent.get(position);
                        ft.remove(fragment);
                        ft.commitAllowingStateLoss();
                    }
                }
            }
        });


    }
    private void initDatas()
    {
        MapFragment mapFragment=new MapFragment();
        PoliceStationFragment policeStationFragment=new PoliceStationFragment();
        AlarmFragment alarmFragment=new AlarmFragment();
        NewsFragment newsFragment=new NewsFragment();
        SettingFragment settingFragment=new SettingFragment();
        mViewPagerContent.add(mapFragment);
        mViewPagerContent.add(policeStationFragment);
        mViewPagerContent.add(alarmFragment);
        mViewPagerContent.add(newsFragment);
        mViewPagerContent.add(settingFragment);

    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.frameLayout_content, mViewPagerContent.get(0));
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mData=new ArrayList<HashMap<String, Object>>();
        fragmentManager=getSupportFragmentManager();
        initViews();
        initbottomView();
        initDatas();
        setDefaultFragment();
        UserId= getIntent().getIntExtra("UserID",0);

        Log.d(TAG,String.valueOf(UserId) );


    }

}
