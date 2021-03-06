package com.example.mountup.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.GpsTracker;
import com.example.mountup.Helper.NetworkStatus;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Model.User;
import com.example.mountup.Popup.ConfirmDialog;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.LoginTask;
import com.example.mountup.ServerConnect.MountTask;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.google.android.gms.common.ErrorDialogFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private Button btnLogin, btnSignUp;
    private EditText editID, editPass;

    private BackPressCloseHandler backPressCloseHandler;
    private Dialog dialog;
    private ConfirmDialog errDialog;

    public static boolean isSignUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUser();
        if (MountManager.getInstance().getItems().isEmpty()) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }

        initView();
        initListener();

        //loadMountData();

        backPressCloseHandler = new BackPressCloseHandler(this);

        connectNetwork();
        // postToken 내부에서 postMountList, initListener, callback으로 순차적 실행

        getGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isSignUp){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            View customLayout=View.inflate(getApplicationContext(),R.layout.dialog_signup,null);
            builder.setView(customLayout);

            customLayout.findViewById(R.id.btn_ok_dialog).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog=builder.create();
            dialog.show();
            isSignUp = false;
        }

    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        editID = findViewById(R.id.edit_id_login);
        editPass = findViewById(R.id.edit_password_login);

        errDialog = new ConfirmDialog(this);
    }

    private void initListener() {
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    private void getGPS(){
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(LoginActivity.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        Constant.X = latitude;
        Constant.Y = longitude;

        String strAddress = getCurrentAddress(latitude, longitude);
        String[] address = strAddress.split(" ");
        Log.v("address", strAddress);

        if(latitude != 0.0 && longitude != 0.0 && address.length>2) {
            Constant.CURRENT_ADDRESS = address[1] + " " + address[2] + " " + address[3];
        }
        else Constant.CURRENT_ADDRESS = strAddress;

        Log.v("CURRENT_ADDRESS", Constant.CURRENT_ADDRESS);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(LoginActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(LoginActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 이미 퍼미션을 가지고 있다면
            // 위치 값을 가져올 수 있음

        } else {  //퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(LoginActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(LoginActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(LoginActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

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


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void connectNetwork(){
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE) {
            Log.v("Network","모바일로 연결됨");
        } else if (status == NetworkStatus.TYPE_WIFI) {
            Log.v("Network","무선랜으로 연결됨");
        } else {
            AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
            View customLayout=View.inflate(getApplicationContext(),R.layout.dialog_network,null);
            builder.setView(customLayout);

            customLayout.findViewById(R.id.btn_cancel_network_dialog).setOnClickListener(this);
            customLayout.findViewById(R.id.btn_retry_network_dialog).setOnClickListener(this);

            dialog=builder.create();
            dialog.show();
        }
    }

    private void switchActivityToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                // TODO: 로그인
                postLogin();
                break;
            case R.id.btn_sign_up:
                // TODO: 회원가입
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cancel_network_dialog:
                dialog.dismiss();
                finish();
                break;
            case R.id.btn_retry_network_dialog:
                dialog.dismiss();
                connectNetwork();
                break;
        }
    }

    private void initUser() {
        MyInfo.getInstance().setUser(new User());
    }

    private void postLogin() {
        // ID PW 설정
        ContentValues values = new ContentValues();
        values.put("id", editID.getText().toString());
        values.put("pw", editPass.getText().toString());

        // 로그인 URL 설정
        String url = Constant.URL + "/api/login";

        // execute 및 MyInfo에 토큰 저장
        LoginTask loginTask = new LoginTask(url, values, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                getGPS();
                switchActivityToMain();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("mmee:LoginActivity",e.toString());
                errDialog.setErrorMessage("아이디 또는 비밀번호가 일치하지 않습니다.\n다시 입력해 주세요.");
                errDialog.show();
            }
        });
        loginTask.execute();
        //tokenTask.executeOnExecutor()
    }

    /*
    private void loadMountData() {
        // 산 URL 설정
        String url = Constant.URL + "/api/mntall";

        // execute, 산 리스트 생성 및 저장
        MountTask mountTask = new MountTask(Constant.GET_NEW, url, null, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d("mmee:mountTask", "get mount resource success!");
            }

            @Override
            public void onFailure(Exception e) {
                //e.printStackTrace();
            }
        });
        mountTask.execute();
    }
    */
}
