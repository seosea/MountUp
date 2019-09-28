package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Popup.ConfirmDialog;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.SignUpTask;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnComplete;
    ImageButton btnBack;

    EditText editID, editPass, editPassConfirm;

    ConfirmDialog errDialog;

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

        errDialog = new ConfirmDialog(this);
    }

    private void initListener() {
        btnComplete.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void BackToLoginActivity() {
        onBackPressed();
        // TODO: 회원가입 완료
        /*
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        */
    }

    private void postSignUp() {
        ContentValues values = new ContentValues();
        values.put("id", editID.getText().toString());
        values.put("pw", editPass.getText().toString());

        String url = Constant.URL + "/api/register";

        SignUpTask signUpTask = new SignUpTask(url, values, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(SignUpActivity.this,
                        "회원 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                BackToLoginActivity();
            }

            @Override
            public void onFailure(Exception e) {
                // 아이디 중복 체크
                Log.d("mmee:SignUpActivity",e.toString());
                errDialog.setErrorMessage("중복된 아이디 입니다.\n다른 아이디를 설정해주세요.");
                errDialog.show();
            }
        });
        signUpTask.execute();
    }

    private boolean isSignUpDataValid() {
        String errorMsg = "";

       if (editID.getText().toString().length() < 6) {
            errorMsg = "아이디는 6자 이상이어야 합니다.";
        } else if (editPass.getText().toString().length() < 6) {
            errorMsg = "비밀번호는 6자 이상이어야 합니다.";
        } else if (!isPasswordValid()) {
            errorMsg = "비밀번호는 영어+숫자 조합의\n6자 이상이어야 합니다.";
        } else if (!editPass.getText().toString().equals(editPassConfirm.getText().toString())) {
            errorMsg = "비밀번호 확인이 일치하지 않습니다.";
        }

        if (!errorMsg.equals("")) {
            errDialog.setErrorMessage(errorMsg);
            return false;
        }
        return true;
    }

    private boolean isPasswordValid() {
        int cntNum = 0, cntEng = 0;
        String pw = editPass.getText().toString();
        for (int i = 0; i < pw.length(); i++){
            char ch = pw.charAt(i);
            if ('0' <= ch && ch <= '9') {
                cntNum++;
            } else if (('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z')) {
                cntEng++;
            }
        }
        if (cntNum == 0 || cntEng == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view)   {
        switch (view.getId()) {
            case R.id.btn_complete_sign_up:
                if (!isSignUpDataValid()) {
                    errDialog.show();
                } else {
                    postSignUp();
                }
                break;
            case R.id.btn_back_sign_up:

                onBackPressed();
                break;
        }
    }
}
