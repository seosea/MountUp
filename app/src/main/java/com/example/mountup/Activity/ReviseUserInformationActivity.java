package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.mountup.R;

public class ReviseUserInformationActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton btnClose, btnUploadImage;
    Button btnComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_user_information);
        initView();
        initListener();
    }

    private void initView(){
        btnClose = findViewById(R.id.btn_close_revise_user_information);
        btnUploadImage = findViewById(R.id.btn_upload_image_revise_user_information);
        btnComplete = findViewById(R.id.btn_complete_revise_user_information);

    }

    private void initListener(){
        btnClose.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_complete_revise_user_information:
            case R.id.btn_close_revise_user_information:
                onBackPressed();
                break;
            case R.id.btn_upload_image_revise_user_information:
                break;
        }
    }
}
