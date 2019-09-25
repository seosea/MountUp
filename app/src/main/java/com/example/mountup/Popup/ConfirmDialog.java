package com.example.mountup.Popup;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.mountup.R;

public class ConfirmDialog extends Dialog {
    TextView m_tv_message;
    Button m_btn_ok, m_btn_cancel;
    public ConfirmDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(R.layout.dialog_error);     //다이얼로그에서 사용할 레이아웃입니다.

        m_tv_message = (TextView) findViewById(R.id.tv_message_dialog);

        m_btn_ok = (Button) findViewById(R.id.btn_ok_dialog);
        m_btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });

        m_btn_cancel = (Button) findViewById(R.id.btn_cancel_dialog);
        m_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });
    }

    public void setErrorMessage(String message) {
        m_tv_message.setText(message);
    }

    public String getErrorMessage() {
        return m_tv_message.getText().toString();
    }
}
