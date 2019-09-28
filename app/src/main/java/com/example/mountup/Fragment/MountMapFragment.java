package com.example.mountup.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mountup.Activity.MainActivity;
import com.example.mountup.Activity.MountDetailActivity;
import com.example.mountup.Activity.ReviewWriteActivity;
import com.example.mountup.Helper.Calculator;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.GpsInfo;
import com.example.mountup.Helper.GpsTracker;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.MountVO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;


public class MountMapFragment extends Fragment
        implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener, com.google.android.gms.location.LocationListener {

    private ImageView imgWeather;
    private TextView txtTemperature, txtMicroDust, txtOzone;

    private WeatherConnection weatherConnection;
    private AsyncTask<String, String, String> result;

    private String strTemperature, strWeather, strMicroDust, strOzone;

    private ImageButton btnRefresh;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private AppCompatActivity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;

    LocationRequest locationRequest = new LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

    private GpsInfo gps;
    private LatLng latLng;

    private FloatingActionButton ftnGPS, ftnClimb;

    private GpsTracker gpsTracker;
    private TextView txtAddress;

    private CircleOptions circle1KM; // 원 반경
    private Marker selectedMarker = null; // 선택된 마커
    private MountVO selectedMount = null; // 선택된 산

    private LinearLayout linearMountInfo;
    private ImageView imgMount, imgIsClimbed;
    private TextView txtMountName, txtMountDistance, txtMountHeight, txtMountGrade;
    private RatingBar rbStar;

    private String m_url;

    private Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mount_map, container, false);

        initView(view);
        initListener();
        setWeather();

        gps = new GpsInfo(getContext());
        setGPS();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    private void initView(View view){
        imgWeather = view.findViewById(R.id.img_weather_map);
        txtTemperature = view.findViewById(R.id.txt_temperature_map);
        txtMicroDust = view.findViewById(R.id.txt_micro_dust_map);
        txtOzone = view.findViewById(R.id.txt_ozone_map);

        txtAddress = view.findViewById(R.id.txt_my_address_user);

        weatherConnection = new WeatherConnection();

        btnRefresh = view.findViewById(R.id.btn_refresh_weather);

        ftnGPS = view.findViewById(R.id.ftn_gps_map);
        ftnClimb = view.findViewById(R.id.ftn_climb_map);

        MapsInitializer.initialize(getContext());

        linearMountInfo = view.findViewById(R.id.linear_mount_info_map);

        imgMount = view.findViewById(R.id.img_mount_map);
        imgIsClimbed = view.findViewById(R.id.img_mount_climbed_map);

        txtMountDistance = view.findViewById(R.id.txt_mount_distance_map);
        txtMountName = view.findViewById(R.id.txt_mount_name_map);
        txtMountHeight = view.findViewById(R.id.txt_mount_height_map);
        txtMountGrade = view.findViewById(R.id.txt_mount_grade_map);

        rbStar = view.findViewById(R.id.rb_mount_grade_map);

    }

    @Override
    public void onResume() {

        super.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }

    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }

    // GPS를 통한 마커 추가
    private void setGPS() {
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            Constant.X = latitude;
            Constant.Y = longitude;

            latLng = new LatLng(latitude, longitude);

            GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            gpsTracker = new GpsTracker(getContext());

            String strAddress = getCurrentAddress(latitude, longitude);
            String[] address = strAddress.split(" ");
            Constant.CURRENT_ADDRESS = address[1] + " " + address[2] + " " + address[3];
            Log.v("CURRENT_ADDRESS", Constant.CURRENT_ADDRESS);

            txtAddress.setText(Constant.CURRENT_ADDRESS);
        }
    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Log.v("GPS","지오코더 서비스 사용불가");
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.v("GPS","잘못된 GPS 좌표");
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Log.v("GPS","주소 미발견");
            return "주소 미발견";
        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    private void initListener(){
        btnRefresh.setOnClickListener(this);
        ftnGPS.setOnClickListener(this);
        ftnClimb.setOnClickListener(this);
        linearMountInfo.setOnClickListener(this);
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // 날씨 설정(재설정)
    private void setWeather(){
        String msg = connectWeatherCast();
        setWeatherView(msg);
    }

    // 날씨 view 설정(재설정)
    private void setWeatherView(String msg){
        String[] txtWeatherCast = msg.split("℃");

        strTemperature = txtWeatherCast[0];
        strWeather = txtWeatherCast[1];
        strMicroDust = txtWeatherCast[2];
        strOzone = txtWeatherCast[3];

        if(strWeather.contains("눈")){
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_snow));
        } else if(strWeather.contains("비")){
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_rain));
        } else if(strWeather.contains("구름")||strWeather.contains("흐림")){
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_cloud));
        } else {
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_sun));
        }
        txtTemperature.setText(strTemperature);
        txtMicroDust.setText(strMicroDust);
        txtOzone.setText(strOzone);

    }

    // 날씨 크롤링
    private String connectWeatherCast(){

        if(result == null) {
            result = weatherConnection.execute("", "");
        }

        System.out.println("RESULT");

        try{
            String msg = result.get();
            System.out.println("MSG : " + msg);
            return msg;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_refresh_weather:
                setWeather();
                break;
            case R.id.ftn_gps_map:
                setGPS();
                break;
            case R.id.ftn_climb_map:
                if(selectedMarker==null){ // 산 선택 안됨
                  Toast.makeText(getContext(),"선택된 산이 없습니다.",Toast.LENGTH_SHORT).show();
                } else{
                    if(Constant.X != 0.0) {
                        float distance = Calculator.calculateDistance(
                                selectedMarker.getPosition().latitude,
                                selectedMarker.getPosition().longitude);
                        if (distance > 0.5) { // 멀 때 (500m 이상)
                            Toast.makeText(getContext(), "거리가 너무 멉니다.", Toast.LENGTH_SHORT).show();
                        } else { // 등산 성공
                            connectNetwork();
                        }
                    } else {
                        Toast.makeText(getContext(), "위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.linear_mount_info_map:
                Intent intent = new Intent(getContext(), MountDetailActivity.class);
                intent.putExtra("MountID", Integer.toString(selectedMount.getID()));

                getContext().startActivity(intent);
                break;


        }
    }

    public void receiveResult(String result){
        try {
            JSONObject jsonObj = new JSONObject(result);

            int code = jsonObj.getInt("code");

            Log.v("Code", code+"");

            if(code==200){
                m_url = "http://15011066.iptime.org:8888/api/exp";

                ContentValues contentValues = new ContentValues();

                NetworkTaskExp networkTaskExp = new NetworkTaskExp(m_url,contentValues);
                networkTaskExp.execute();

                Message msgProfile = handlerMountUp.obtainMessage();
                handlerMountUp.sendMessage(msgProfile);

            } else {
                Message msgProfile = handlerAlreadyUp.obtainMessage();
                handlerAlreadyUp.sendMessage(msgProfile);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    final Handler handlerMountUp = new Handler()
    {
        public void handleMessage(Message msg)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
            View customLayout=View.inflate(getContext(),R.layout.dialog_mountup,null);
            builder.setView(customLayout);

            customLayout.findViewById(R.id.btn_cancel_network_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            customLayout.findViewById(R.id.btn_retry_network_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent = new Intent(view.getContext(), ReviewWriteActivity.class);
                    intent.putExtra("mountID", "" + selectedMount.getID());
                    startActivity(intent);
                }
            });

            dialog=builder.create();
            dialog.show();
        }

    };


    final Handler handlerAlreadyUp = new Handler()
    {
        public void handleMessage(Message msg)
        {
            Toast.makeText(getContext(), "이미 등산하였습니다", Toast.LENGTH_SHORT).show();
        }

    };

    private void connectNetwork(){
        m_url = "http://15011066.iptime.org:8888/api/mntup";

        ContentValues contentValues = new ContentValues();
        contentValues.put("mntID",selectedMount.getID());

        NetworkTask networkTask = new NetworkTask(m_url,contentValues);
        networkTask.execute();
    }

    public class NetworkTaskExp extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTaskExp(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            PostHttpURLConnection postHttpURLConnection = new PostHttpURLConnection();
            result = postHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.d("exp result",result);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            PostHttpURLConnection postHttpURLConnection = new PostHttpURLConnection();
            result = postHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.d("mount up result",result);

            receiveResult(result);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mGoogleMap = googleMap;


        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        //mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });

        mMap = googleMap;
        // 맵의 이동
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        mMap.setOnMarkerClickListener(this);

        setMountMarker();

        circle1KM = new CircleOptions();
        linearMountInfo.setVisibility(View.INVISIBLE);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        Constant.X = location.getLatitude();
        Constant.Y = location.getLongitude();

        mMoveMapByUser = false;


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mGoogleMap.addMarker(markerOptions);


        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation() {

        mMoveMapByUser = false;

        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    private void setMountMarker(){
        for(int i=0;i< MountManager.getInstance().getItems().size();i++) {
            MountVO mount = MountManager.getInstance().getItems().get(i);
            LatLng mountLocation = new LatLng(mount.getLocX(),mount.getLocY());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mountLocation);
            markerOptions.title(mount.getName());
            markerOptions.snippet(mount.getAddress());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            mMap.addMarker(markerOptions).showInfoWindow();
        }
    }

    @Override
    public void onStart() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    public void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {


        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }


    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude());


        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition.latitude,currentPosition.longitude);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocatiion = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        linearMountInfo.setVisibility(View.INVISIBLE);
        if(marker.getPosition().latitude != Constant.X && marker.getPosition().longitude != Constant.Y) {
            mMap.clear();
            setMountMarker();
            // 반경 1KM원
            circle1KM.center(marker.getPosition()) //원점
                    .radius(500)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#4D7DB249")); //배경색

            mMap.addCircle(circle1KM);
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),12));

            selectedMarker = marker;

            viewMountInformation(marker);
        }
        return true;
    }

    private void viewMountInformation(Marker marker){
        String name = marker.getTitle();
        for(MountVO mount : MountManager.getInstance().getItems()){
            if(mount.getName().equals(name)) selectedMount = mount;
        }
        Log.v("name", name);
        Log.v("name", selectedMount.getName());

        if(selectedMount !=null){
            linearMountInfo.setVisibility(View.VISIBLE);
            imgMount.setImageBitmap(selectedMount.getThumbnail());
            txtMountName.setText(selectedMount.getName());
            txtMountDistance.setText(Float.toString(selectedMount.getDistance()) + "km");
            txtMountHeight.setText(Integer.toString(selectedMount.getHeight()) + "m");
            txtMountGrade.setText(Float.toString(selectedMount.getGrade()));
            rbStar.setRating(selectedMount.getGrade());
            if (selectedMount.isClimbed()) {
                imgIsClimbed.setVisibility(View.VISIBLE);
            } else{
                imgIsClimbed.setVisibility(View.INVISIBLE);
            }

        } else {
            linearMountInfo.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(),"다시 시도해주세요.",Toast.LENGTH_SHORT).show();
        }

    }

    // 날씨 크롤링 테스크. 네트워크는 AsyncTask 를 사용, 백에서 작업
    public class WeatherConnection extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            try{
                String path = "http://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09650510";
                Document document = Jsoup.connect(path).get();
                Elements elements = document.select("em");

                System.out.println("Element : " + elements);
                Element targetElement1 = elements.get(2);
                Element targetElement2 = elements.get(3);
                Element targetElement3 = elements.get(4);

                String text = targetElement1.text() + "℃" + targetElement2.text() + "℃" + targetElement3.text();
                System.out.println("Text : " + text);

                return text;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }



            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }


}

