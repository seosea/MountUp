package com.example.mountup.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mountup.Adapter.MountListRecyclerViewAdapter;
import com.example.mountup.Popup.ConfirmDialog;
import com.example.mountup.R;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.MountVO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MountDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MountVO m_mount;
    private GoogleMap mMap;

    private ConfirmDialog errDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mount_detail);

        initActivityWidget();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        float distance = m_mount.getDistance();
        if (distance < 1.0f) {
            m_tv_mountDistance.setText(Integer.toString((int)(distance * 1000)) + "m");
        } else {
            m_tv_mountDistance.setText(Float.toString(Math.round(distance * 10) / 10.0f) + "km");
        }
        TextView m_tv_mountGrade = (TextView) this.findViewById(R.id.txt_mount_grade_map);
        m_tv_mountGrade.setText(Float.toString(m_mount.getGrade()));

        TextView m_tv_mountAddress = (TextView) this.findViewById(R.id.tv_mountAddress);
        m_tv_mountAddress.setText(m_mount.getAddress());

        TextView m_tv_mountIntro = (TextView) this.findViewById(R.id.tv_mountIntro);
        m_tv_mountIntro.setText(m_mount.getIntro());

        ImageView m_iv_isClimbed = (ImageView) this.findViewById(R.id.img_isClimbed);

        if (! m_mount.isClimbed())
            m_iv_isClimbed.setVisibility(View.INVISIBLE);

        // rattingBar
        RatingBar rb_mountGrade = (RatingBar) this.findViewById(R.id.rb_mount_grade_map);
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

        errDialog = new ConfirmDialog(this);

        Button reviewWriteButton = (Button) this.findViewById(R.id.btn_writeReview);
        reviewWriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (m_mount.isClimbed()) {
                    Intent intent = new Intent(view.getContext(), ReviewWriteActivity.class);
                    Log.d("mountID", "" + m_mount.getID());
                    intent.putExtra("mountID", "" + m_mount.getID());
                    startActivity(intent);
                } else {
                    errDialog.setErrorMessage("산을 등반한 후에\n리뷰를 작성해주세요.");
                    errDialog.show();
                }
            }
        });

        Button reviewEnterButton = (Button) this.findViewById(R.id.btn_enterReview);
        reviewEnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ReviewActivity.class);

                Log.d("mountID",""+m_mount.getID());
                intent.putExtra("mountID",""+m_mount.getID());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.anim_slide_out_top);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng mount = new LatLng(m_mount.getLocX(), m_mount.getLocY());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mount);
        markerOptions.title(m_mount.getName());
        markerOptions.snippet(m_mount.getAddress());
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mount));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(mount));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }
}
