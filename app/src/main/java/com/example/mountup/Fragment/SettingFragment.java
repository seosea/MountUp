package com.example.mountup.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mountup.Activity.ReviseUserInformationActivity;
import com.example.mountup.R;

public class SettingFragment extends Fragment implements View.OnClickListener{

    Button btnUserInformation, btnLogout, btnLanguage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        initView(view);
        initListener();

        return view;
    }

    private void initView(View view){
        btnLanguage = view.findViewById(R.id.btn_language_setting);
        btnUserInformation = view.findViewById(R.id.btn_user_information_setting);
        btnLogout = view.findViewById(R.id.btn_logout_setting);
    }

    private void initListener(){
        btnLanguage.setOnClickListener(this);
        btnUserInformation.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_language_setting:
                break;
            case R.id.btn_user_information_setting:
                Intent intent = new Intent(this.getActivity(), ReviseUserInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_logout_setting:
                // TODO: 로그아웃 시
                break;
        }
    }
}
