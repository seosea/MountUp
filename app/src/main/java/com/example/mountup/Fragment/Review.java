package com.example.mountup.Fragment;

import android.graphics.Bitmap;

public class Review {
    private String m_review_user_id;
    private String m_review_cotent;
    private Bitmap m_review_image;

    public Review(String m_review_user_id, String m_review_cotent, Bitmap m_review_image) {
        this.m_review_user_id = m_review_user_id;
        this.m_review_cotent = m_review_cotent;
        this.m_review_image = m_review_image;
    }

    public String getM_review_user_id() {
        return m_review_user_id;
    }

    public void setM_review_user_id(String m_review_user_id) {
        this.m_review_user_id = m_review_user_id;
    }

    public String getM_review_cotent() {
        return m_review_cotent;
    }

    public void setM_review_cotent(String m_review_cotent) {
        this.m_review_cotent = m_review_cotent;
    }

    public Bitmap getM_review_image() {
        return m_review_image;
    }

    public void setM_review_image(Bitmap m_review_image) {
        this.m_review_image = m_review_image;
    }
}