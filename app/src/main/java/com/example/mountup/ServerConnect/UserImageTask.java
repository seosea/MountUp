package com.example.mountup.ServerConnect;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.VO.ReviewVO;

import java.io.InputStream;
import java.net.URL;

public class UserImageTask extends AsyncTask<Void, Void, Void> {
    private AsyncCallback m_callback;
    private Exception m_exception;

    private String userImageUri;
    private ReviewVO m_review;

    public UserImageTask(ReviewVO review, AsyncCallback m_callback) {
        userImageUri = "http://15011066.iptime.org:8888/userimages/";
        this.m_review = review;
        this.m_callback = m_callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String result = null;

        try {
            String user_img = userImageUri + m_review.getUserId()+".jpg";
            InputStream is_user = (InputStream) new URL(user_img).getContent();
            if(is_user !=null){
            Drawable user_drawable = Drawable.createFromStream(is_user, "mount" +m_review.getUserId());
            m_review.setUserImage(((BitmapDrawable) user_drawable).getBitmap());
            }
        } catch (Exception e) {
            e.printStackTrace();
            m_exception = e;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (m_callback != null && m_exception == null) {
            m_callback.onSuccess(true);
        } else {
            m_callback.onFailure(m_exception);
        }
    }
}