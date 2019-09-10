package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnComplete;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();
        initListener();
    }

    private void initView(){
        btnComplete = findViewById(R.id.btn_complete_sign_up);
        btnBack = findViewById(R.id.btn_back_sign_up);
    }

    private void initListener(){
        btnComplete.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_complete_sign_up:
                // TODO: 회원가입 완료
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_back_sign_up:
                onBackPressed();
                break;
        }
    }
}
