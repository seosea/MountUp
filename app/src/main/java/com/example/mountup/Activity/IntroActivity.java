package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.MountTask;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        loadMountData();
        //startLoading();
    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    private void loadMountData() {
        // 산 URL 설정
        String url = Constant.URL + "/api/mntall";

        // execute, 산 리스트 생성 및 저장
        MountTask mountTask = new MountTask(url, null, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d("mmee:mountTask", "get mount resource success!");
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                //e.printStackTrace();
            }
        });
        mountTask.execute();
    }
}
