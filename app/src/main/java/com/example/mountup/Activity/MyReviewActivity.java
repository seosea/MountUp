package com.example.mountup.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mountup.Adapter.ReviewRecyclerViewAdapter;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.ServerConnect.ReviewImageTask;
import com.example.mountup.ServerConnect.UserImageTask;
import com.example.mountup.Singleton.LikeReviewManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.ReviewVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MyReviewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{
    private ArrayList<ReviewVO> m_reviewItems; //리뷰 리스트
    private ArrayList<ReviewVO> m_bufferList;  //맨처음 모든 리뷰를 받아옴

    private RecyclerView m_recyclerView ;
    private LinearLayoutManager m_layoutManager;
    private ReviewRecyclerViewAdapter m_adapter;

    private SwipeRefreshLayout m_swipeRefreshLayout;
    private Button btn_reviewExit;

    private String m_url;
    private String m_mountID;

    private TextView txtNull;

    private CheckTypesTask loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);

        initView();
        m_url = "http://15011066.iptime.org:8888/api/reviewuser";

        ContentValues contentValues = new ContentValues();
        //TODO : 본인 id로 변경
        contentValues.put("id", MyInfo.getInstance().getUser().getID());

        NetworkTask networkTask = new NetworkTask(m_url, contentValues, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                Log.d("mmee:ReviewActivity","get review success");
                getData();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("mmee:ReviewActivity","get review fail");
            }
        });
        networkTask.execute();
    }

    private void initView(){
        txtNull = findViewById(R.id.txt_null_my_review);

        m_reviewItems = new ArrayList();
        m_bufferList = new ArrayList();

        m_recyclerView = findViewById(R.id.rc_review_recyclerview);

        //레이아웃매니저 설정 설정
        m_layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        m_recyclerView.setLayoutManager(m_layoutManager);

        btn_reviewExit = findViewById(R.id.btn_exitReview);
        btn_reviewExit.setOnClickListener(this);

        //어탭더 설정
        m_adapter = new ReviewRecyclerViewAdapter(this,m_recyclerView,m_reviewItems,new ReviewRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (m_reviewItems.size() <= 100) {
                    Log.d("smh:loadmore","more");
                    m_reviewItems.add(null);
                    m_recyclerView.post(new Runnable() {
                        public void run() {
                            m_adapter.notifyItemInserted(m_reviewItems.size() - 1);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            m_reviewItems.remove(m_reviewItems.size() - 1);
                            m_adapter.notifyItemRemoved(m_reviewItems.size());

                            //Generating more data
                            int index = m_reviewItems.size();
                            int end = index + 5;
                            if (end > m_bufferList.size()) {
                                end = m_bufferList.size();
                            }

                            if(index == end){
                                return;
                            }

                            for (int i = index; i < end; i++) {
                                m_reviewItems.add(m_bufferList.get(i));
                            }
                            m_adapter.notifyDataSetChanged();
                            m_adapter.setLoaded();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(MyReviewActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        m_recyclerView.setAdapter(m_adapter);

        // 새로고침
        m_swipeRefreshLayout =  findViewById(R.id.swipeContainer);
        m_swipeRefreshLayout.setOnRefreshListener(this);
    }

    public class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(MyReviewActivity.this, R.style.progress_bar_style);

        @Override
        protected void onPreExecute() {

            asyncDialog.setCancelable(false);
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("리뷰를 불러오고 있습니다");

            // show dialog
            asyncDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    asyncDialog.setProgress(i*30);
                    Thread.sleep(500);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }

    }

    final Handler handlerLoadingStart = new Handler()
    {
        public void handleMessage(Message msg)
        {
            loading = new CheckTypesTask();
            loading.execute();
        }
    };

    final Handler handlerLoading = new Handler()
    {
        public void handleMessage(Message msg)
        {
            loading.onPostExecute(null);
        }
    };

    private void getData(){
        Log.d("smh:get","data");
        m_reviewItems.clear();
        int end = 5;

        if(m_bufferList.size() < 5){
            end = m_bufferList.size();
        }
        Log.d("smh:first size",""+m_bufferList.size());

        for(int i =0;i<end;i++){
            m_reviewItems.add(m_bufferList.get(i));
        }
        
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

    public void receiveReview(String result){
        try {
            JSONArray jsonArray = new JSONArray(result);
            Log.d("smh:length",""+jsonArray.length());

            if(jsonArray.length() == 0){
                txtNull.setVisibility(View.VISIBLE);
            } else {
                txtNull.setVisibility(View.INVISIBLE);
            }

            for(int i =0;i<jsonArray.length();i++) {
                Log.d("mmee:ReviewActivity","1 review");

                JSONObject jsonObj = jsonArray.getJSONObject(i);

                int reviewID = jsonObj.getInt("reviewID");
                String reviewUserID = jsonObj.getString("reviewUserID");
                int reviewMntID = jsonObj.getInt("reviewMntID");
                String reviewString = jsonObj.getString("reviewString");
                Double reviewStar = jsonObj.getDouble("reviewStar");
                String reviewPic = jsonObj.getString("reviewPic");
                int reviewLike = jsonObj.getInt("LIKE");
                int reviewIFLIKE = jsonObj.getInt("IFLIKE");
                final ReviewVO newReview = new ReviewVO();
                if (reviewIFLIKE == 1) {
                    newReview.setReview(reviewID, reviewUserID, reviewMntID, reviewString, reviewStar, reviewPic, reviewLike, true);
                } else {
                    newReview.setReview(reviewID, reviewUserID, reviewMntID, reviewString, reviewStar, reviewPic, reviewLike, false);
                }

                getReviewImage(newReview);
                getUserImage(newReview);

                m_bufferList.add(newReview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getReviewImage(ReviewVO newReview) {
        InputStream is = null;
        try {
            String reviewImg_url = "http://15011066.iptime.org:8888/reviewimages/" + newReview.getImageName();
            //Log.d("mmee:ReviewActivity", "url : " + reviewImg_url + "\nininputStream : " + is.toString());
            is = (InputStream) new URL(reviewImg_url).getContent();
        } catch (IOException e) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_mountain_ranking_main);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            newReview.setImage(bitmap);
            e.printStackTrace();
            return;
        }

        Drawable review_drawable = Drawable.createFromStream(is, "mount" + newReview.getReivewID());
        newReview.setImage(((BitmapDrawable) review_drawable).getBitmap());
        Log.d("mmee:ReviewActivity", "Get review image");
    }

    public void getUserImage(ReviewVO newReview) {
        InputStream is = null;
        try {
            String userImg_url = "http://15011066.iptime.org:8888/userimages/" + newReview.getUserId() + ".jpg";
            is = (InputStream) new URL(userImg_url).getContent();

        } catch (IOException e) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_mountain_ranking_main);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            newReview.setUserImage(bitmap);
            e.printStackTrace();
            return;
        }

        Drawable user_drawable = Drawable.createFromStream(is, "mount" + newReview.getUserId());
        newReview.setUserImage(((BitmapDrawable) user_drawable).getBitmap());
        Log.d("mmee:ReviewActivity", "Get user image");
    }

    @Override
    public void onClick(View view) {
        String str = ""+view.getId();
        Log.d("button click",str);

        finish();
        overridePendingTransition(R.anim.anim_slide_in_top,R.anim.anim_slide_out_bottom);
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private AsyncCallback callback;
        private Exception exception;
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values, AsyncCallback callback) {
            this.exception = null;
            this.callback = callback;
            this.url = url;
            this.values = values;

            Message msgProfile = handlerLoadingStart.obtainMessage();
            handlerLoadingStart.sendMessage(msgProfile);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            PostHttpURLConnection postHttpURLConnection = new PostHttpURLConnection();
            result = postHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.d("smh:result",result);

            receiveReview(result);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Message msgProfile = handlerLoading.obtainMessage();
            handlerLoading.sendMessage(msgProfile);

            if (callback != null && exception == null) {
                callback.onSuccess(true);
            } else {
                callback.onFailure(exception);
            }
        }
    }
}