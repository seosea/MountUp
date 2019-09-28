package com.example.mountup.Popup;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.mountup.R;

public class FullImagePopup extends Dialog {
    private ImageView m_iv_fullImage;

    public FullImagePopup(Context context, Bitmap bitmap) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(R.layout.popup_full_image);     //다이얼로그에서 사용할 레이아웃입니다.

        m_iv_fullImage = (ImageView) findViewById(R.id.iv_fullSize);
        //m_iv_mount.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //m_iv_mount.setAdjustViewBounds(true);

        m_iv_fullImage.setImageBitmap(bitmap);

        m_iv_fullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });
    }
}
