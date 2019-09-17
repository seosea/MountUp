package com.example.mountup.VO;

import android.graphics.Bitmap;

public class ReviewVO {
    private String m_userId;
    private String m_cotent;
    private Bitmap m_Image;
    private int m_like;
    private double m_grade;

    private boolean m_myLike;

    public ReviewVO(String m_userId, String m_cotent, Bitmap m_Image, int m_like, double m_grade, boolean m_myLike) {
        this.m_userId = m_userId;
        this.m_cotent = m_cotent;
        this.m_Image = m_Image;
        this.m_like = m_like;
        this.m_grade = m_grade;
        this.m_myLike = m_myLike;
    }

    public String getUserId() { return m_userId; }
    public void setUserId(String m_userId) { this.m_userId = m_userId; }

    public String getCotent() { return m_cotent; }
    public void setCotent(String m_cotent) { this.m_cotent = m_cotent; }

    public Bitmap getImage() { return m_Image; }
    public void setImage(Bitmap m_mainImage) { this.m_Image = m_mainImage; }

    public int getLike() { return m_like; }
    public void setLike(int m_like) { this.m_like = m_like; }

    public double getGrade() { return m_grade; }
    public void setGrade(double m_grade) { this.m_grade = m_grade; }

    public boolean isMyLike() { return m_myLike; }
    public void setMyLike(boolean m_myLike) { this.m_myLike = m_myLike; }
}