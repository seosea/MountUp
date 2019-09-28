package com.example.mountup.Popup;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.mountup.R;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;

public class MountImagePopup extends Dialog {

    ImageView m_iv_mount;

    public MountImagePopup(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //다이얼로그의 타이틀바를 없애주는 옵션입니다.
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(R.layout.popup_mountimgae);     //다이얼로그에서 사용할 레이아웃입니다.

        m_iv_mount = (ImageView) findViewById(R.id.iv_mount);
        //m_iv_mount.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        //m_iv_mount.setAdjustViewBounds(true);

        for (MountVO mount : MountManager.getInstance().getItems()) {
            if (mount.getID() == MountManager.getInstance().getSeletedMountID()) {
                m_iv_mount.setImageBitmap(mount.getThumbnail());
                break;
            }
        }

        m_iv_mount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();   //다이얼로그를 닫는 메소드입니다.
            }
        });
    }
}
