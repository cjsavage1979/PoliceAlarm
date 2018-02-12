package com.policealarmapp.policealarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MapFragment extends Fragment {


    private MapView mapView = null;
    private View view = null;

    private FloatingActionButton locationButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        //   SDKInitializer.initialize(getActivity().getApplicationContext());
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        view = root;

        List<Map<String,Object>> mData=new ArrayList<Map<String,Object>>();
        initView();
        return root;
    }

    private void initView() {
        locationButton=view.findViewById(R.id.locationButton);

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng cenpt = new LatLng(28.682376,115.857949);
                MapStatus mMapStatus = new MapStatus.Builder()//定义地图状态
                        .target(cenpt)
                        .zoom(18)
                        .build();  //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                mBaiduMap.setMapStatus(mMapStatusUpdate);//改变地图状态
            }
        });
        mapView = view.findViewById(R.id.bmapView);
        mBaiduMap = mapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //设定地图缩放级别
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(16.0f);
        mBaiduMap.animateMapStatus(mapStatusUpdate);

        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//获取城市地址信息
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();


        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            //手动滑动地图开始状态
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            //手动滑动地图滑动进行中的状态
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }
            //手动滑动地图停止后,获取到当前地图中心坐标的经纬度
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                String _str = mapStatus.toString();
                String _regex = "target lat: (.*)\ntarget lng";
                String _regex2 = "target lng: (.*)\ntarget screen x";
                String latitude = latlng(_regex, _str);
                String longitude = latlng(_regex2, _str);
                mCurrentLantitude = Double.parseDouble(latitude);
                mCurrentLongitude = Double.parseDouble(longitude);
                KLog.e(String.valueOf(mCurrentLantitude)+"-"+String.valueOf(mCurrentLongitude));
                ((MainActivity)getActivity()).setPosition(mCurrentLongitude,mCurrentLantitude);
                searchNeayBy();
            }
        });



    }

    /**
     * 截取字符串 获取经纬度
     */
    private String latlng(String regexStr, String str) {
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            str = matcher.group(1);
        }
        return str;
    }

    private BDLocation lastLocation;
    private Double mCurrentLantitude, mCurrentLongitude;
    private LatLng latLng;
    private Double locationLat, locationLng;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;// 定位相关
    private MyLocationListenner myListener = new MyLocationListenner();

    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }
            locationLat = location.getLatitude();
            locationLng = location.getLongitude();
            KLog.e("addr:" + location.getAddrStr());
            if (lastLocation != null) {
                if (lastLocation.getLatitude() == location.getLatitude() &&
                        lastLocation.getLongitude() == location.getLongitude()) {
                    KLog.e("same location, skip refresh");
                    return;
                }
            }
            lastLocation = location;
            mBaiduMap.clear();
            mCurrentLantitude = lastLocation.getLatitude();
            mCurrentLongitude = lastLocation.getLongitude();
            latLng = new LatLng(mCurrentLantitude, mCurrentLongitude);
            KLog.e(mCurrentLantitude + "," + mCurrentLongitude);
            LatLng llA = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            CoordinateConverter converter = new CoordinateConverter();
            converter.coord(llA);
            converter.from(CoordinateConverter.CoordType.COMMON);
            LatLng convertLatLng = converter.convert();
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 16.0f);
            mBaiduMap.animateMapStatus(u);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchNeayBy();
                }
            }).start();
        }
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
        }
    }

    private int radius;
    private PoiSearch mPoiSearch = PoiSearch.newInstance();
    private ArrayList<PoiInfo> nearList = new ArrayList<>();
    BitmapDescriptor startPointIcon = BitmapDescriptorFactory.fromResource(R.drawable.alarm);//起点覆盖物
    BitmapDescriptor policeCar = BitmapDescriptorFactory.fromResource( R.mipmap.police_hat);//起点覆盖物


    private void searchNeayBy() {
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword("派出所");
        option.sortType(PoiSortType.distance_from_near_to_far);
        option.location(new LatLng(mCurrentLantitude, mCurrentLongitude));
        if (radius != 0) {
            option.radius(radius);
        } else {
            option.radius(1000);
        }

        option.pageCapacity(40);
        mPoiSearch.searchNearby(option);
        /**
         * * 接受周边地理位置结果
         * @param poiResult
         */
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult != null) {
              //     mData.clear();
                    if (poiResult.getAllPoi() != null && poiResult.getAllPoi().size() > 0) {
                        nearList.clear();
                        ((MainActivity)getActivity()).ClearStations();
                        nearList.addAll(poiResult.getAllPoi());
                        KLog.e(nearList.get(0).location.latitude + "   " + nearList.get(0).location.longitude);
                        if (nearList != null && nearList.size() > 0) {


                            for (int i = 0; i < nearList.size(); i++) {
                                KLog.e(nearList.get(i).address + "  " + nearList.get(i).city + "  "
                                        + nearList.get(i).name + "  " + nearList.get(i).postCode + "  " + nearList
                                        .get(i).phoneNum + "  " + nearList.get(i).uid);


                                ((MainActivity)getActivity()).AddStation(nearList.get(i).name,nearList.get(i).address);

                            }
                        }
                        Message msg = new Message();
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }




                }
            }
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    addMarker();
                    break;

            }
        }
    };

    private void addMarker(){
        mBaiduMap.clear();
        for (int i = 0; i < nearList.size(); i++) {
            OverlayOptions markerOptions = new MarkerOptions()
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .icon(startPointIcon)
                    .position(nearList.get(i).location);
            mBaiduMap.addOverlay(markerOptions);
        }


    }

    private  void addCar(LatLng position)
    {
        OverlayOptions policeCar=new MarkerOptions()
                .flat(true)
                .anchor(0.5f,0.5f)
                .icon(startPointIcon)
                .position(position);

        mBaiduMap.addOverlay(policeCar);


    }
}
