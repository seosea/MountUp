package com.example.mountup.ServerConnect;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.VO.ReviewVO;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class ReviewImageTask extends AsyncTask<Void, Void, Void> {
    private AsyncCallback m_callback;
    private Exception m_exception;

    private String reviewImageUrl;
    private ReviewVO m_review;

    public ReviewImageTask(ReviewVO review, AsyncCallback m_callback) {
        reviewImageUrl = "http://15011066.iptime.org:8888/reviewimages/";
        this.m_review = review;
        this.m_callback = m_callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String review_img = reviewImageUrl + m_review.getImageName();
            InputStream is_review = (InputStream) new URL(review_img).getContent();
            Drawable review_drawable = Drawable.createFromStream(is_review, "mount" +m_review.getReivewID());
            m_review.setImage(((BitmapDrawable) review_drawable).getBitmap());
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