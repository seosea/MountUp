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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.GpsTracker;
import com.example.mountup.Helper.NetworkStatus;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.MountVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Button btnLogin, btnSignUp;

    private BackPressCloseHandler backPressCloseHandler;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        backPressCloseHandler = new BackPressCloseHandler(this);

        Constant.ADMIN_ID = "admin";
        Constant.ADMIN_PW = "1234";

        connectNetwork();
        // postToken 내부에서 postMountList, initListener, callback으로 순차적 실행

        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        //initListener();

        getGPS();
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
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
        Log.v("latitude", latitude + "");
        if(latitude != 0.0) {
            String[] address = strAddress.split(" ");
            Log.v("address", strAddress);
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
        if(status == NetworkStatus.TYPE_MOBILE){
            Log.v("Network","모바일로 연결됨");
            postToken();
        }else if (status == NetworkStatus.TYPE_WIFI){
            Log.v("Network","무선랜으로 연결됨");
            postToken();
        }else {
            AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
            View customLayout=View.inflate(getApplicationContext(),R.layout.dialog_network,null);
            builder.setView(customLayout);

            customLayout.findViewById(R.id.btn_cancel_network_dialog).setOnClickListener(this);
            customLayout.findViewById(R.id.btn_retry_network_dialog).setOnClickListener(this);

            dialog=builder.create();
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                // TODO: 로그인
                getGPS();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_sign_up:
                // TODO: 회원가입
                intent = new Intent(this, SignUpActivity.class);
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

    private void postToken() {
        // ID PW 설정
        ContentValues values = new ContentValues();
        values.put("id", Constant.ADMIN_ID);
        values.put("pw", Constant.ADMIN_PW);

        // 로그인 URL 설정
        String url = "http://15011066.iptime.org:8888/api/login";

        // execute 및 MyInfo에 토큰 저장
        TokenTask tokenTask = new TokenTask(url, values, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                postMountList();
            }

            @Override
            public void onFailure(Exception e) {
                //e.printStackTrace();
            }
        });
        tokenTask.execute();
        //tokenTask.executeOnExecutor()
    }

    private void postMountList() {
        ContentValues values = new ContentValues();
        values.put("id", Constant.ADMIN_ID);
        values.put("pw", Constant.ADMIN_PW);

        // 산 URL 설정
        String url = "http://15011066.iptime.org:8888/api/all";

        // execute, 산 리스트 생성 및 저장
        MountTask mountTask = new MountTask(url, values, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d("mmee:mountTask", "get mount resource success!");
                initListener();
            }

            @Override
            public void onFailure(Exception e) {
                //e.printStackTrace();
            }
        });
        mountTask.execute();
    }

    private void initMountFromJson(String json_str) {
        //Log.d("mmee:initMountFromJson", "json_str : " + json_str);

        try {
            JSONArray jsonArray = new JSONArray(json_str);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                int mntID = jsonObj.getInt("mntID");
                String mntName = jsonObj.getString("mntName");
                int mntHeight = jsonObj.getInt("mntHeight");
                String mntInfo = jsonObj.getString("mntInfo");
                String mntPlace = jsonObj.getString("mntPlace");
                double mntStar = jsonObj.getDouble("mntStar");
                double mntLocX = jsonObj.getDouble("mntLocX");
                double mntLocY = jsonObj.getDouble("mntLocY");

                MountVO newItem = new MountVO();
                newItem.setMount(mntID, mntName, mntHeight, mntInfo, mntPlace, (float)mntStar, mntLocX, mntLocY);

                /*
                    newItem.setThumbnail(ContextCompat.getDrawable(this, R.drawable.mountain_sample));
                */
                String url_img = "http://15011066.iptime.org:8888/basicImages/" + (i + 1) + ".jpg";
                InputStream is = (InputStream) new URL(url_img).getContent();
                Drawable mount_drawable = Drawable.createFromStream(is, "mount" + (i + 1));
                /*
                Bitmap mount_bitmap = ((BitmapDrawable)mount_drawable).getBitmap();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                Bitmap resize = BitmapFactory.decodeStream(is, null, options);
                */
                newItem.setThumbnail(((BitmapDrawable)mount_drawable).getBitmap());


                Log.d("mmee:mountTask", "get mount resource " + (i + 1));

                // (임시) 거리, 등반 확인, 별점
                newItem.setDistance(new Random().nextFloat() * 100);
                newItem.setGrade(new Random().nextFloat() * 5);
                newItem.setClimb(new Random().nextInt(2) > 0 ? true : false);

                MountManager.getInstance().getItems().add(newItem);

                //Log.d("mmee:createItems","mntID :  " + mntID + " / MntName : " + mntName + " / mntHeight :  " + mntHeight + "/ mntInfo :  " + mntInfo + " / mntPlace : " + mntPlace + " / mntStar :  " + mntStar
                //                                    + "/ mntLocX : " + mntLocX + " / mntLocY : " + mntLocY);
            }

            //  "mntID": 1,
            //  "mntName": "개운산",
            //  "mntHeight": "134",
            //  "mntInfo": "개운산입니다.",
            //  "mntPlace": "서울특별시 성북구 종암동",
            //  "mntStar": 4.5,
            //  "mntLocX": "37.598068",
            //  "mntLocY": "127.025347"

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class TokenTask extends AsyncTask<Void, Void, Void>  {
        private AsyncCallback m_callback;
        private Exception m_exception;
        String url;
        ContentValues values;

        TokenTask(String url, ContentValues values, AsyncCallback callback){
            this.m_callback = callback;
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result;
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // post token
            Log.d("mmee:TokenPost", "result : " + result);
            // MyInfo에 토큰 설정
            try {
                JSONObject job = new JSONObject(result);
                String token_str = job.getString("token");
                MyInfo.getInstance().setToken(token_str);

            } catch (JSONException e) {
                e.printStackTrace();
                m_exception = e;
            }

            return null; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (m_callback != null && m_exception == null) {
                m_callback.onSuccess(true);
            } else {
                m_callback.onFailure(m_exception);
            }

            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
        }
            /*
            InputStream is = null;
            String result = "";
            BufferedReader reader = null;
            try {
                String json = "";

                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                // 전송모드 설정
                con.setDefaultUseCaches(false);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");

                // content-type 설정
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");


                con.connect();
                Log.d("mmee:doInBackground", "URL : " + url.toString());

                // json object 생성
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", "admin00");
                jsonObject.accumulate("pw", "123");
                json = jsonObject.toString();

                Log.d("mmee:doInBackground", "jsonObject : " + json);

                // 전송값 설정
                //StringBuffer buffer = new StringBuffer();
                //buffer.append("id").append("=").append("admin00").append("&");
                //buffer.append("pw").append("=").append("123");


                // 서버로 전송
                //OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
                OutputStream os = con.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();

                // 서버에서 데이터 수신
                try {
                    is = con.getInputStream();
                    if (is != null) {
                        reader = new BufferedReader(new InputStreamReader(is));

                        StringBuffer buffer = new StringBuffer();

                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        result = buffer.toString();
                        Log.d("mmee:Login", "POST : " + result);

                        return result;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }*/
    }

    public class MountTask extends AsyncTask<Void, Void, Void> {
        AsyncCallback m_callback;
        Exception m_exception;
        String url;
        ContentValues values;

        MountTask(String url, ContentValues values, AsyncCallback callback) {
            this.m_callback = callback;
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected Void doInBackground(Void... params) {
            String mountList_json_str;

            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            mountList_json_str = requestHttpURLConnection.request(url, values);

            initMountFromJson(mountList_json_str);

            return null; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (m_callback != null && m_exception == null) {
                m_callback.onSuccess(true);
            } else {
                m_callback.onFailure(m_exception);
            }
        }
    }


}
