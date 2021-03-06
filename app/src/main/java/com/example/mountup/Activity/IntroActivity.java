package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.MountTask;
import com.example.mountup.Singleton.MountManager;

public class IntroActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tv_loadPercent;
    private BackPressCloseHandler backPressCloseHandler;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_intro);
        tv_loadPercent = (TextView) findViewById(R.id.tv_loadPercent);

        initMountList();
        startLoading();
    }

    private void initMountList() {
        MountManager.getInstance().setLoadPercent(0);
        MountManager.getInstance().getItems().clear();
        loadMountData();
    }

    private void startLoading() {
        handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (MountManager.getInstance().getLoadPercent() < 100) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(MountManager.getInstance().getLoadPercent());
                            tv_loadPercent.setText(MountManager.getInstance().getLoadPercent() + " %");
                        }
                    });
                    try {
                        // Sleep for 100 milliseconds to show the progress slowly.
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void loadMountData() {
        // 산 URL 설정
        String url = Constant.URL + "/api/mntall";

        // execute, 산 리스트 생성 및 저장
        MountTask mountTask = new MountTask(Constant.GET_NEW, url, null, new AsyncCallback() {
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
