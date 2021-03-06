package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;

import com.example.mountup.Fragment.MountListFragment;
import com.example.mountup.Fragment.MountMapFragment;
import com.example.mountup.Fragment.UserFragment;
import com.example.mountup.Fragment.SettingFragment;
import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FragmentManager fragmentManager;
    Fragment fragment;
    private int curFragment;

    ImageButton btnMountList, btnMountMap, btnUser, btnSetting;
    View selectedMountList, selectedMountMap, selectedUser, selectedSetting;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("text", "text");
        Constant.context = this;

        setContentView(R.layout.activity_main);

        getDisplaySize();
        initFragment();
        initView();
        initListener();

        backPressCloseHandler = new BackPressCloseHandler(this);

    }

    @Override public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void getDisplaySize(){
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();

        Constant.WIDTH  = dm.widthPixels;
        Constant.HEIGHT = dm.heightPixels;

    }

    private void initFragment(){
        fragment = new MountListFragment();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace( R.id.main_fragment, fragment );
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        curFragment = Constant.FRAGMENT_LIST;
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
        switch (view.getId()) {
            case R.id.btn_mount_list:
                if (curFragment != Constant.FRAGMENT_LIST) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new MountListFragment())
                            .addToBackStack(null)
                            .detach(fragment)
                            .attach(fragment)
                            .commit();

                    selectedMountList.setVisibility(View.VISIBLE);
                    selectedMountMap.setVisibility(View.INVISIBLE);
                    selectedUser.setVisibility(View.INVISIBLE);
                    selectedSetting.setVisibility(View.INVISIBLE);
                    curFragment = Constant.FRAGMENT_LIST;
                }
                break;

            case R.id.btn_mount_map:
                if (curFragment != Constant.FRAGMENT_MAP) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new MountMapFragment())
                            .addToBackStack(null)
                            .detach(fragment)
                            .attach(fragment)
                            .commit();

                    selectedMountList.setVisibility(View.INVISIBLE);
                    selectedMountMap.setVisibility(View.VISIBLE);
                    selectedUser.setVisibility(View.INVISIBLE);
                    selectedSetting.setVisibility(View.INVISIBLE);
                    curFragment = Constant.FRAGMENT_MAP;
                }
                break;
            case R.id.btn_user:
                if (curFragment != Constant.FRAGMENT_USER) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new UserFragment())
                            .addToBackStack(null)
                            .detach(fragment)
                            .attach(fragment)
                            .commit();

                    selectedMountList.setVisibility(View.INVISIBLE);
                    selectedMountMap.setVisibility(View.INVISIBLE);
                    selectedUser.setVisibility(View.VISIBLE);
                    selectedSetting.setVisibility(View.INVISIBLE);
                    curFragment = Constant.FRAGMENT_USER;
                }
                break;
            case R.id.btn_setting:
                if (curFragment != Constant.FRAGMENT_SETTING) {

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fragment, new SettingFragment())
                            .addToBackStack(null)
                            .detach(fragment)
                            .attach(fragment)
                            .commit();

                    selectedMountList.setVisibility(View.INVISIBLE);
                    selectedMountMap.setVisibility(View.INVISIBLE);
                    selectedUser.setVisibility(View.INVISIBLE);
                    selectedSetting.setVisibility(View.VISIBLE);
                    curFragment = Constant.FRAGMENT_SETTING;
                }
                break;
        }
    }
}