package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mountup.Adapter.ReviewAdapter;
import com.example.mountup.Model.Review;
import com.example.mountup.R;

import java.util.ArrayList;

public class ReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ReviewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView ;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initView();
        getData();
    }

    private void initView(){
        adapter = new ReviewAdapter();
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView = findViewById(R.id.rc_review_recyclerview);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout =  findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void getData(){
        adapter.addItem(new Review("shin","이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. ",
                BitmapFactory.decodeResource(getResources(),R.drawable.mt2),1004,(float)4.75,true));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.mt),1004,4.35,true));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,3.1,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,1.0,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,0.1,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,0.0,false));
        adapter.addItem(new Review("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.image),1004,2.0,false));

        adapter.notifyDataSetChanged();
    }

    @Override

    public void onRefresh() {

        // 새로고침 코드

        getData();

        // 새로고침 완료

        mSwipeRefreshLayout.setRefreshing(false);
    }
}