package com.example.mountup.Singleton;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.mountup.VO.MountVO;
import com.example.mountup.VO.ReviewVO;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class LikeReviewManager {

    private LikeReviewManager() {
        m_items = new ArrayList();
    }

    private static class LikeReviewManagerHolder {
        public static final LikeReviewManager instance = new LikeReviewManager();
    }

    public static LikeReviewManager getInstance() {
        return LikeReviewManagerHolder.instance;
    }

    private ArrayList<ReviewVO> m_items;

    public ArrayList<ReviewVO> getItems() { return m_items; }

}