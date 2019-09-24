package com.example.mountup.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.example.mountup.Activity.MountDetailActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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


public class MountMapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private ImageView imgWeather;
    private TextView txtTemperature, txtMicroDust, txtOzone;

    private WeatherConnection weatherConnection;
    private AsyncTask<String, String, String> result;

    private String strTemperature, strWeather, strMicroDust, strOzone;

    private ImageButton btnRefresh;
    private GoogleMap mMap;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mount_map, container, false);

        initView(view);
        initListener();
        setWeather();

        gps = new GpsInfo(getContext());
        setGPS();

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
            Toast.makeText(getContext(), "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getContext(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getContext(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    /*
    // 구글 자동완성
    private void setPlaceAutoComplete() {

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                setCurrentMarker(true, latLng, place.getName().toString(), place.getAddress().toString());
            }

            @Override
            public void onError(Status status) {
                Log.i("Place Error", "An error occurred: " + status);
            }
        });
    }
    */

    // 인스턴트 마커 표시
    private void setCurrentMarker(boolean flag, LatLng latLng, String markerTitle, String markerSnippet) {
        if(flag){
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            mMap.addMarker(markerOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }

    private void initListener(){
        btnRefresh.setOnClickListener(this);
        ftnGPS.setOnClickListener(this);
        ftnClimb.setOnClickListener(this);
        linearMountInfo.setOnClickListener(this);
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
                            Toast.makeText(getContext(), "등산하였습니다!", Toast.LENGTH_SHORT).show();
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
            String msg = jsonObj.getString("msg");

            Log.v(String.valueOf(code), msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void connectNetwork(){
        m_url = "http://15011066.iptime.org:8888/api/mntup";

        ContentValues contentValues = new ContentValues();
        contentValues.put("mntID",selectedMount.getID());

        NetworkTask networkTask = new NetworkTask(m_url,contentValues);
        networkTask.execute();
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
            Log.d("exp result",result);

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
        mMap = googleMap;
        // 맵의 이동
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("현재 위치");
        markerOptions.snippet(Constant.CURRENT_ADDRESS);
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

        mMap.setOnMarkerClickListener(this);

        setMountMarker();

        circle1KM = new CircleOptions();
        linearMountInfo.setVisibility(View.INVISIBLE);
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

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
        if((marker.getPosition().latitude != gps.getLatitude()) && (marker.getPosition().longitude != gps.getLongitude())) {
            mMap.clear();
            setMountMarker();
            // 반경 1KM원
            circle1KM.center(marker.getPosition()) //원점
                    .radius(500)      //반지름 단위 : m
                    .strokeWidth(0f)  //선너비 0f : 선없음
                    .fillColor(Color.parseColor("#4D7DB249")); //배경색

            mMap.addCircle(circle1KM);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(),14));

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



}

