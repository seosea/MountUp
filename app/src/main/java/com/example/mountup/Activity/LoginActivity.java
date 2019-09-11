package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnLogin, btnSignUp;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void initView(){
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }

    private void initListener(){
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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
        }
    }
}
