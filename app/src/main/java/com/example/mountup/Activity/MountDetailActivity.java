package com.example.mountup.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mountup.R;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;

public class MountDetailActivity extends AppCompatActivity {

    private MountVO m_mount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mount_detail);

        initActivityWidget();
    }

    public MountVO getMountDataFromID(int mountID) {
        MountVO mount = new MountVO();
        for (MountVO item : MountManager.getInstance().getItems()) {
            //Log.d("mmee:MountDetail","item.getID() : " + item.getID() + " / intent : " + Integer.parseInt(intent.getStringExtra("MountID")));
            if (item.getID() == mountID) {
                mount = item;
                break;
            }
        }
        return mount;
    }

    public void initActivityWidget() {
        m_mount = getMountDataFromID(Integer.parseInt(getIntent().getStringExtra("MountID")));

        //Log.d("mmee:initActivityWidget","Thumbnail : " + m_mount.getThumbnail());
        ImageView m_iv_mountThumbnail = (ImageView) this.findViewById(R.id.iv_mountThumbnail);
        m_iv_mountThumbnail.setImageBitmap(m_mount.getThumbnail());

        TextView m_tv_mountName = (TextView) this.findViewById(R.id.tv_mountName);
        m_tv_mountName.setText(m_mount.getName());

        TextView m_tv_mountHeight = (TextView) this.findViewById(R.id.tv_mountHeight);
        m_tv_mountHeight.setText(Integer.toString(m_mount.getHeight()) + "m");

        TextView m_tv_mountDistance = (TextView) this.findViewById(R.id.tv_mountDistance);
        m_tv_mountDistance.setText(Float.toString(m_mount.getDistance()) + "km");

        TextView m_tv_mountGrade = (TextView) this.findViewById(R.id.tv_mountGrade);
        m_tv_mountGrade.setText(Float.toString(m_mount.getGrade()));

        TextView m_tv_mountAddress = (TextView) this.findViewById(R.id.tv_mountAddress);
        m_tv_mountAddress.setText(m_mount.getAddress());

        TextView m_tv_mountIntro = (TextView) this.findViewById(R.id.tv_mountIntro);
        m_tv_mountIntro.setText(m_mount.getIntro());

        ImageView m_iv_isClimbed = (ImageView) this.findViewById(R.id.img_isClimbed);

        if (! m_mount.isClimbed())
            m_iv_isClimbed.setVisibility(View.INVISIBLE);

        // rattingBar
        RatingBar rb_mountGrade = (RatingBar) this.findViewById(R.id.rb_mountGrade);
        rb_mountGrade.setRating(m_mount.getGrade());

        // close 버튼
        ImageButton closeButton = (ImageButton) this.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO:
                // This function closes Activity Two
                // Hint: use Context's finish() method
                finish();
            }
        });
    }
}
