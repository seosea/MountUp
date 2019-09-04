package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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

    ImageButton btnMountList, btnMountMap, btnUser, btnSetting;
    View selectedMountList, selectedMountMap, selectedUser, selectedSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constant.context = this;

        getDisplaySize();
        initFragment();
        initView();
        initListener();

    }

    private void getDisplaySize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Constant.WIDTH = size.x;
        Constant.HEIGHT = size.y;
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
        btnUser = findViewById(R.id.btn_user);
        btnSetting = findViewById(R.id.btn_setting);

        selectedMountList = findViewById(R.id.view_selected_mount_list);
        selectedMountMap = findViewById(R.id.view_selected_mount_map);
        selectedUser = findViewById(R.id.view_selected_user);
        selectedSetting = findViewById(R.id.view_selected_setting);
    }

    private void initListener(){
        btnMountList.setOnClickListener(this);
        btnMountMap.setOnClickListener(this);
        btnUser.setOnClickListener(this);
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

                selectedMountList.setVisibility(View.VISIBLE);
                selectedMountMap.setVisibility(View.INVISIBLE);
                selectedUser.setVisibility(View.INVISIBLE);
                selectedSetting.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_mount_map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MountMapFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.INVISIBLE);
                selectedMountMap.setVisibility(View.VISIBLE);
                selectedUser.setVisibility(View.INVISIBLE);
                selectedSetting.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_user:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new MyReviewFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.INVISIBLE);
                selectedMountMap.setVisibility(View.INVISIBLE);
                selectedUser.setVisibility(View.VISIBLE);
                selectedSetting.setVisibility(View.INVISIBLE);
                break;
            case R.id.btn_setting:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment, new SettingFragment())
                        .addToBackStack(null)
                        .commit();

                selectedMountList.setVisibility(View.INVISIBLE);
                selectedMountMap.setVisibility(View.INVISIBLE);
                selectedUser.setVisibility(View.INVISIBLE);
                selectedSetting.setVisibility(View.VISIBLE);
                break;

        }
    }

}
