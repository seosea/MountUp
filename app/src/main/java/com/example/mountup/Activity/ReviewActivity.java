package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mountup.Adapter.ReviewRecyclerViewAdapter;
import com.example.mountup.VO.ReviewVO;
import com.example.mountup.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{
    private ArrayList<ReviewVO> m_reviewItems; //리뷰 리스트

    private RecyclerView m_recyclerView ;
    private LinearLayoutManager m_layoutManager;
    private ReviewRecyclerViewAdapter m_adapter;

    private SwipeRefreshLayout m_swipeRefreshLayout;
    private Spinner m_spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initView();
        getData();
    }

    private void initView(){
        m_reviewItems = new ArrayList();

        m_recyclerView = findViewById(R.id.rc_review_recyclerview);

        //레이아웃매니저 설정 설정
        m_layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        m_recyclerView.setLayoutManager(m_layoutManager);

        //어탭더 설정
        m_adapter = new ReviewRecyclerViewAdapter(this,m_recyclerView,m_reviewItems,new ReviewRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (m_reviewItems.size() <= 20) {
                    m_reviewItems.add(null);
                    m_adapter.notifyItemInserted(m_reviewItems.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            m_reviewItems.remove(m_reviewItems.size() - 1);
                            m_adapter.notifyItemRemoved(m_reviewItems.size());

                            //Generating more data
                            int index = m_reviewItems.size();
                            int end = index + 1;
                            for (int i = index; i < end; i++) {
                                m_reviewItems.add(new ReviewVO("shin","good",BitmapFactory.decodeResource(getResources(),R.drawable.mt),1004,4.35,true));
                            }
                            m_adapter.notifyDataSetChanged();
                            m_adapter.setLoaded();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(ReviewActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        m_recyclerView.setAdapter(m_adapter);

        // 새로고침
        m_swipeRefreshLayout =  findViewById(R.id.swipeContainer);
        m_swipeRefreshLayout.setOnRefreshListener(this);

        //리스트 정렬
        m_spinner = findViewById(R.id.spinner_review);
        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortReviewList(m_spinner.getSelectedItem().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    private void getData(){
        m_reviewItems.add(new ReviewVO("shin","이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. ",
                BitmapFactory.decodeResource(getResources(),R.drawable.mt2),1004,(float)4.75,true));
        m_reviewItems.add(new ReviewVO("shin","이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. 이 산 너무 좋아요 다음에 또 오고싶어요. ",
                BitmapFactory.decodeResource(getResources(),R.drawable.mt2),1004,(float)3.75,true));

        m_adapter.addAll(m_reviewItems);
        m_adapter.notifyDataSetChanged();
    }

    @Override

    public void onRefresh() {
        // 새로고침 코드
        m_adapter.clear();
        getData();
        // 새로고침 완료
        m_swipeRefreshLayout.setRefreshing(false);
    }

    public void sortReviewList(String str){
        String log = "Arraylist size :"+m_adapter.getItemCount();

        Log.d("count",log);
        if(m_adapter.getItemCount() <= 3) return;

        if(str == "별점 순") {
            Collections.sort(m_adapter.getItems(), new Comparator<ReviewVO>() {
                        @Override
                        public int compare(ReviewVO o1, ReviewVO o2) {
                            if (o1.getGrade() < o2.getGrade()) {
                                return 1;
                            } else if (o1.getGrade() > o2.getGrade()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }
            );
        }
        else{
            Collections.sort(m_adapter.getItems(), new Comparator<ReviewVO>() {
                        @Override
                        public int compare(ReviewVO o1, ReviewVO o2) {
                            if (o1.getGrade() < o2.getGrade()) {
                                return 1;
                            } else if (o1.getGrade() > o2.getGrade()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    }
            );
        }
        m_adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        String str = ""+view.getId();
        Log.d("button click",str);

        m_adapter.notifyDataSetChanged();
    }
}