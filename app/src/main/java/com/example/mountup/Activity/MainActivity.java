package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mountup.Fragment.MountListFragment;
import com.example.mountup.Fragment.MountMapFragment;
import com.example.mountup.Fragment.MyReviewFragment;
import com.example.mountup.Fragment.RecodeFragment;
import com.example.mountup.Fragment.SettingFragment;
import com.example.mountup.Helper.Constant;
import com.example.mountup.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FragmentManager fragmentManager;
    Fragment fragment;

    Button btnMountList, btnMountMap, btnRecode, btnMyReview,btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constant.context = this;

        initFragment();
        initView();
        initListener();
    }

    private void initFragment(){
        fragment = new MountListFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.main_fragment, fragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initView(){
        btnMountList = findViewById(R.id.btn_mount_list);
        btnMountMap = findViewById(R.id.btn_mount_map);
        btnRecode = findViewById(R.id.btn_recode);
        btnMyReview = findViewById(R.id.btn_my_review);
        btnSetting = findViewById(R.id.btn_setting);
    }

    private void initListener(){
        btnMountList.setOnClickListener(this);
        btnMountMap.setOnClickListener(this);
        btnRecode.setOnClickListener(this);
        btnMyReview.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_mount_list:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MountListFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btn_mount_map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MountMapFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btn_recode:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new RecodeFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btn_my_review:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MyReviewFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.btn_setting:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new SettingFragment())
                        .addToBackStack(null)
                        .commit();
                break;

        }
    }
}
