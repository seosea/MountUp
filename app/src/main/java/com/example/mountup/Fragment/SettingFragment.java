package com.example.mountup.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mountup.Activity.LoginActivity;
import com.example.mountup.Activity.ReviseUserInformationActivity;
import com.example.mountup.R;

public class SettingFragment extends Fragment implements View.OnClickListener{

    Button btnUserInformation, btnLogout, btnLanguage, btnQuestion, btnAbout;

    private Dialog dialog;

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
        btnQuestion = view.findViewById(R.id.btn_question_setting);
        btnAbout = view.findViewById(R.id.btn_about_setting);
    }

    private void initListener(){
        btnLanguage.setOnClickListener(this);
        btnUserInformation.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        btnQuestion.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
    }

    public static void setClipBoardLink(Context context , String link){

        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", link);
        clipboardManager.setPrimaryClip(clipData);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_language_setting:
                Toast.makeText(this.getContext(),"추후 추가예정 입니다.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_user_information_setting:
                Intent intent = new Intent(this.getActivity(), ReviseUserInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_logout_setting:
                Toast.makeText(this.getContext(),"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                intent = new Intent(this.getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_question_setting:
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                View customLayout=View.inflate(getContext(),R.layout.dialog_question,null);
                builder.setView(customLayout);

                customLayout.findViewById(R.id.btn_copy_tel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipBoardLink(getContext(),"01024907768");
                    }
                });

                customLayout.findViewById(R.id.btn_copy_email).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipBoardLink(getContext(),"ssyaoao@naver.com");
                    }
                });

                customLayout.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog=builder.create();
                dialog.show();
                break;
            case R.id.btn_about_setting:
                builder=new AlertDialog.Builder(getContext());
                customLayout=View.inflate(getContext(),R.layout.dialog_about,null);
                builder.setView(customLayout);

                customLayout.findViewById(R.id.btn_close_about).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog=builder.create();
                dialog.show();
                break;
        }
    }
}
