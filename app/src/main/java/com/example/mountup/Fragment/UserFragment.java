package com.example.mountup.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.mountup.Activity.LikeReviewActivity;
import com.example.mountup.Activity.MyReviewActivity;
import com.example.mountup.Activity.ReviewActivity;
import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.Adapter.MountClimbedListRecyclerViewAdapter;

import com.example.mountup.Helper.MountListRecyclerViewDecoration;

import com.example.mountup.ServerConnect.MountImageTask;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.ServerConnect.UserClimbedListTask;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.MountVO;
import com.example.mountup.VO.ReviewVO;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class UserFragment extends Fragment implements MountClimbedListRecyclerViewAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private RecyclerView m_mountRecycleView;
    private RecyclerView.LayoutManager m_layoutManager;
    private MountClimbedListRecyclerViewAdapter m_adapter;
    private ArrayList<MountVO> m_bufferItems; // 버퍼로 사용할 리스트

    private TextView txtCount1, txtCount2, txtTotalHeight;
    private int nCount=0;
    private int nTotalHeight=0;

    private Button btnMyReview, btnLikeReview;

    private TextView txtID, txtLevel;
    private ImageView imgProfile;

    private String userID, userPW, userPIC;
    private int totalheight, exp;
    private Drawable userDrawable;
    private View vExp;

    private String m_url;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        m_bufferItems = new ArrayList();

        // RecycleView 생성 및 사이즈 고정
        m_mountRecycleView = (RecyclerView) view.findViewById(R.id.rv_mountList);
        m_mountRecycleView.setHasFixedSize(true);

        // Grid 레이아웃 적용
        m_layoutManager= new LinearLayoutManager(getContext());
        ((LinearLayoutManager) m_layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);

        m_mountRecycleView.setLayoutManager(m_layoutManager);
        m_mountRecycleView.addItemDecoration(new MountListRecyclerViewDecoration(getActivity()));

        loadAll();

        // 어뎁터 연결
        m_adapter = new MountClimbedListRecyclerViewAdapter(getContext(), this);
        m_mountRecycleView.setAdapter(m_adapter);

        txtCount1 = view.findViewById(R.id.txt_count_1_user);
        txtCount2 = view.findViewById(R.id.txt_count_2_user);
        txtTotalHeight = view.findViewById(R.id.txt_height_user);

        btnMyReview = view.findViewById(R.id.btn_user_my_review);
        btnLikeReview = view.findViewById(R.id.btn_user_like_review);

        btnMyReview.setOnClickListener(this);
        btnLikeReview.setOnClickListener(this);

        txtID = view.findViewById(R.id.txt_id_user);
        txtLevel = view.findViewById(R.id.txt_level_user);
        imgProfile = view.findViewById(R.id.img_profile_user);
        vExp = view.findViewById(R.id.view_exp_user);

        m_url = "http://15011066.iptime.org:8888/api/userinfo";

        ContentValues contentValuesUser = new ContentValues();

        NetworkTask networkTaskUser = new NetworkTask(m_url,contentValuesUser);
        networkTaskUser.execute();


        // User 등반 리스트 갱신
        String url_userClimbedList = Constant.URL + "/api/mntuplist";

        UserClimbedListTask userClimbedListTask = new UserClimbedListTask(url_userClimbedList, null);
        userClimbedListTask.execute();

        return view;
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            PostHttpURLConnection postHttpURLConnection = new PostHttpURLConnection();
            result = postHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.d("user result",result);

            receiveResult(result);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void receiveResult(String result){
        try {

            JSONArray jsonArray = new JSONArray(result);
            Log.d("smh:length",""+jsonArray.length());

            for(int i =0;i<jsonArray.length();i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                userID = jsonObj.getString("userID");
                userPW = jsonObj.getString("userPW");
                userPIC = jsonObj.getString("userPIC");
                totalheight = jsonObj.getInt("totalheight");
                exp = jsonObj.getInt("exp");

                Message msgUser = handlerUser.obtainMessage();
                handlerUser.sendMessage(msgUser);

                if(userPIC != null){
                    String url_img = "http://15011066.iptime.org:8888/userimages/" + userPIC;
                    InputStream is = null;
                    try {
                        is = (InputStream) new URL(url_img).getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userDrawable = Drawable.createFromStream(is, "userPIC");

                    Message msgProfile = handlerImg.obtainMessage();
                    handlerImg.sendMessage(msgProfile);
                } else {
                    Log.v("user profile", "null");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    final Handler handlerImg = new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(userDrawable!=null) {
                imgProfile.setImageBitmap(((BitmapDrawable) userDrawable).getBitmap());
                MyInfo.getInstance().getUser().setProfile(((BitmapDrawable) userDrawable).getBitmap());
                imgProfile.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    imgProfile.setClipToOutline(true);
                }

            }
        }

    };

    final Handler handlerUser = new Handler()
    {
        public void handleMessage(Message msg)
        {
            txtID.setText(userID);

            String strExp = String.valueOf((exp/1000) + 1);
            txtLevel.setText(strExp);

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vExp.getLayoutParams();
            int nExp = exp%1000;
            lp.width = (int)((double)Constant.WIDTH/1000 * nExp);
            vExp.setLayoutParams(lp);

            MyInfo.getInstance().getUser().setID(userID);
            MyInfo.getInstance().getUser().setPassword(userPW);
            MyInfo.getInstance().getUser().setExperience(exp);
            MyInfo.getInstance().getUser().setLevel((exp/1000) + 1);
            MyInfo.getInstance().getUser().setTotalHeight(totalheight);
        }

    };

    private void loadAll() {
        MountImageTask mountImageTask = new MountImageTask(Constant.CLIMBED, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                ArrayList < MountVO > mountList = MountManager.getInstance().getItems();
                m_bufferItems.clear();
                for (int i = 0; i < mountList.size(); i++) {
                    if(mountList.get(i).isClimbed()) {
                        m_bufferItems.add(MountManager.getInstance().getItems().get(i));
                        nCount++;
                        nTotalHeight+=MountManager.getInstance().getItems().get(i).getHeight();
                    }
                }
                m_adapter.addAll(m_bufferItems);

                txtCount1.setText(String.valueOf(nCount)+"회");
                txtCount2.setText(String.valueOf(nCount));
                txtTotalHeight.setText(String.valueOf(nTotalHeight)+"m");
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        mountImageTask.execute();
    }


    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_user_my_review:
                Intent intent = new Intent(getContext(), MyReviewActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.anim_slide_out_top);
                break;
            case R.id.btn_user_like_review:
                intent = new Intent(getContext(), LikeReviewActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_slide_in_bottom,R.anim.anim_slide_out_top);
                break;
        }
    }
}
