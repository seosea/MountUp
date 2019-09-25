package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.R;

public class IntroActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        startLoading();
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

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

}
