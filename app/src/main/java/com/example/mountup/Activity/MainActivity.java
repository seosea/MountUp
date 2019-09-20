package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mountup.Fragment.MountListFragment;
import com.example.mountup.Fragment.MountMapFragment;
import com.example.mountup.Fragment.UserFragment;
import com.example.mountup.Fragment.SettingFragment;
import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.GpsInfo;
import com.example.mountup.Helper.GpsTracker;
import com.example.mountup.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final int MY_PERMISSIONS_LOCATION=1001;
    private GpsTracker gpsTracker;

    FragmentManager fragmentManager;
    Fragment fragment;

    ImageButton btnMountList, btnMountMap, btnUser, btnSetting;
    View selectedMountList, selectedMountMap, selectedUser, selectedSetting;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constant.context = this;

        setContentView(R.layout.activity_main);

        getDisplaySize();
        initFragment();
        initView();
        initListener();

        backPressCloseHandler = new BackPressCloseHandler(this);

        checkPermission();

        gpsTracker = new GpsTracker(getApplicationContext());

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        Constant.X = latitude;
        Constant.Y = longitude;

        String strAddress = getCurrentAddress(latitude, longitude);
        String[] address = strAddress.split(" ");
        Constant.CURRENT_ADDRESS = address[1] + " " + address[2] + " " + address[3];
        Log.v("CURRENT_ADDRESS", Constant.CURRENT_ADDRESS);
  }

    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    @Override public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void getDisplaySize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Constant.WIDTH = size.x;
        Constant.HEIGHT = size.y;
    }

    private void initFragment(){
        fragment = new MountListFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.main_fragment, fragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initView(){
        btnMountList = findViewById(R.id.btn_mount_list);
        btnMountMap = findViewById(R.id.btn_mount_map);
        btnUser = findViewById(R.id.btn_user);
        btnSetting = findViewById(R.id.btn_setting);

        selectedMountList = findViewById(R.id.view_selected_mount_list);
        selectedMountMap = findViewById(R.id.view_selected_mount_map);
        selectedUser = findViewById(R.id.view_selected_user);
        selectedSetting = findViewById(R.id.view_selected_setting);
    }

    private void initListener(){
        btnMountList.setOnClickListener(this);
        btnMountMap.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
    }

    private void checkPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,"GPS 사용을 위해 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_LOCATION);
                Toast.makeText(this,"GPS 사용을 위해 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_mount_list:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MountListFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.VISIBLE);
                selectedMountMap.setVisibility(View.INVISIBLE);
                selectedUser.setVisibility(View.INVISIBLE);
                selectedSetting.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_mount_map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MountMapFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.INVISIBLE);
                selectedMountMap.setVisibility(View.VISIBLE);
                selectedUser.setVisibility(View.INVISIBLE);
                selectedSetting.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_user:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new UserFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.INVISIBLE);
                selectedMountMap.setVisibility(View.INVISIBLE);
                selectedUser.setVisibility(View.VISIBLE);
                selectedSetting.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_setting:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new SettingFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.INVISIBLE);
                selectedMountMap.setVisibility(View.INVISIBLE);
                selectedUser.setVisibility(View.INVISIBLE);
                selectedSetting.setVisibility(View.VISIBLE);
                break;
        }
    }
}
