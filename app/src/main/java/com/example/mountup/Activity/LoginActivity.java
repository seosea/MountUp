package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Helper.NetworkStatus;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.MountTask;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.ServerConnect.TokenTask;
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
import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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
        String url = Constant.URL + "/api/login";

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
        String url = Constant.URL + "/api/all";

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
}
