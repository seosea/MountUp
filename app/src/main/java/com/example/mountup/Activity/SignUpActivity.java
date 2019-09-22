package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mountup.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnComplete;
    ImageButton btnBack;

    EditText editID, editPass, editPassConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initView();
        initListener();
    }

    private void initView() {
        btnComplete = findViewById(R.id.btn_complete_sign_up);
        btnBack = findViewById(R.id.btn_back_sign_up);

        editID = findViewById(R.id.edit_id_sign_up);
        editPass = findViewById(R.id.edit_password_sign_up);
        editPassConfirm = findViewById(R.id.edit_password_confirm_sign_up);
    }

    private void initListener() {
        btnComplete.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)   {
        switch (view.getId()) {
            case R.id.btn_complete_sign_up:
                if(editID.getText().toString().length()>5
                        && editPass.getText().toString().length()>5
                        && editPass.getText().equals(editPassConfirm.getText())) {
                    // TODO: 회원가입 완료
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    startActivity(intent);
                }
                break;
            case R.id.btn_back_sign_up:

                onBackPressed();
                break;
        }
    }
}
