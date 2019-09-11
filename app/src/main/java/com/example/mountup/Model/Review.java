package com.example.mountup.Model;

import android.graphics.Bitmap;

public class Review {
    private String m_user_id;
    private String m_cotent;
    private Bitmap m_main_image;
    private int m_like;
    private double m_grade;
    private boolean m_mylike;

    public Review(String m_user_id, String m_cotent, Bitmap m_main_image, int m_like, double m_grade, boolean m_mylike) {
        this.m_user_id = m_user_id;
        this.m_cotent = m_cotent;
        this.m_main_image = m_main_image;
        this.m_like = m_like;
        this.m_grade = m_grade;
        this.m_mylike = m_mylike;
    }

    public String getM_user_id() {
        return m_user_id;
    }

    public void setM_user_id(String m_user_id) {
        this.m_user_id = m_user_id;
    }

    public String getM_cotent() {
        return m_cotent;
    }

    public int getM_like() {
        return m_like;
    }

    public boolean isM_mylike() {
        return m_mylike;
    }

    public void setM_mylike(boolean m_mylike) {
        this.m_mylike = m_mylike;
    }

    public void setM_like(int m_like) {
        this.m_like = m_like;
    }

    public double getM_grade() {
        return m_grade;
    }

    public void setM_grade(float m_grade) {
        this.m_grade = m_grade;
    }

    public void setM_cotent(String m_cotent) {
        this.m_cotent = m_cotent;
    }

    public Bitmap getM_main_image() {
        return m_main_image;
    }

    public void setM_main_image(Bitmap m_main_image) {
        this.m_main_image = m_main_image;
    }
}