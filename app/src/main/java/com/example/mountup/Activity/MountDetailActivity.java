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

    private TextView m_tv_mountName;
    private TextView m_tv_mountHeight;
    private TextView m_tv_mountDistance;
    private TextView m_tv_mountGrade;
    private TextView m_tv_mountIntro;
    private TextView m_tv_mountAddress;
    private ImageView m_iv_isClimbed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mount_detail);

        m_mount = new MountVO();

        Intent intent = getIntent();
        for (MountVO item : MountManager.getInstance().getItems()) {
            Log.d("mee:MountDetail","item.getID() : " + item.getID() + " / intent : " + Integer.parseInt(intent.getStringExtra("MountID")));
            if (item.getID() == Integer.parseInt(intent.getStringExtra("MountID"))) {
                m_mount = item;
                break;
            }
        }

        //m_mount.setName(intent.getStringExtra("name"));
        //m_mount.setHeight(Integer.parseInt(intent.getStringExtra("height")));
        //m_mount.setDistance(Float.parseFloat(intent.getStringExtra("distance")));
        //m_mount.setGrade(Float.parseFloat(intent.getStringExtra("grade")));
        //m_mount.setClimb(Boolean.parseBoolean(intent.getStringExtra("isClimbed")));
        //Log.d("mee:MountDetailActivity", "isClimbed : " + Boolean.parseBoolean(intent.getStringExtra("isClimbed")));

        m_tv_mountName = (TextView) this.findViewById(R.id.tv_mountName);
        m_tv_mountName.setText(m_mount.getName());

        m_tv_mountHeight = (TextView) this.findViewById(R.id.tv_mountHeight);
        m_tv_mountHeight.setText(Integer.toString(m_mount.getHeight()) + "m");

        m_tv_mountDistance = (TextView) this.findViewById(R.id.tv_mountDistance);
        m_tv_mountDistance.setText(Float.toString(m_mount.getDistance()) + "km");

        m_tv_mountGrade = (TextView) this.findViewById(R.id.tv_mountGrade);
        m_tv_mountGrade.setText(Float.toString(m_mount.getGrade()));

        m_tv_mountAddress = (TextView) this.findViewById(R.id.tv_mountAddress);
        m_tv_mountAddress.setText(m_mount.getAddress());

        m_tv_mountIntro = (TextView) this.findViewById(R.id.tv_mountIntro);
        m_tv_mountIntro.setText(m_mount.getIntro());

        m_iv_isClimbed = (ImageView) this.findViewById(R.id.img_isClimbed);

        if (! m_mount.isClimbed())
            m_iv_isClimbed.setVisibility(View.INVISIBLE);

        // rattingBar
        RatingBar rb_mountGrage = (RatingBar) this.findViewById(R.id.rb_mountGrade);
        rb_mountGrage.setRating(m_mount.getGrade());

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
