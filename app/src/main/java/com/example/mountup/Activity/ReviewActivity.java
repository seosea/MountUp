package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mountup.R;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        Button.OnClickListener onClickListener = new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId())
                {
                    case R.id.btn_review_close:
                        finish();
                        break;
                    case R.id.btn_review_image:
                        //핸드폰 이미지 불러오기

                       

                        break;
                    case R.id.btn_review_submit:



                        finish();
                        break;
                }
            }
        };



    }

}
