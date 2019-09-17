package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import com.example.mountup.R;

public class ReviewWriteActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btn_close;
    ImageButton btn_imageButton;

    Button btn_submit;

    RatingBar ratingBar_review;
    EditText editText_review;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        initView();
        initListener();
    }

    void initView(){
        btn_close = findViewById(R.id.btn_review_close);
        btn_imageButton = findViewById(R.id.btn_review_imageButton);
        btn_submit = findViewById(R.id.btn_review_submit);

        ratingBar_review = findViewById(R.id.ratingBar_reivew);
        editText_review = findViewById(R.id.editText_review);
    }

    void initListener() {
        btn_close.setOnClickListener(this);
        btn_imageButton.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        ratingBar_review.setOnRatingBarChangeListener(new Listener());
    }

    class Listener implements RatingBar.OnRatingBarChangeListener
    {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            Log.d("rating",""+v);
            ratingBar.setRating(v);
            ratingBar_review.setRating(v);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_review_close:
                Log.d("button","close");
                break;
            case R.id.btn_review_imageButton:
                Log.d("button","image");
                break;
            case R.id.btn_review_submit:
                Log.d("button","submit");


                break;
        }
    }

}
