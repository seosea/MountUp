package com.example.mountup.VO;

import android.graphics.Bitmap;

public class ReviewVO {
    private int m_reivewID;
    private String m_userID; // 리뷰 작성한 유저
    private int m_mntID;
    private String m_cotent; // 내용
    private Bitmap m_Image; // image
    private int m_like;
    private double m_grade;

    private boolean m_Pic;

    public ReviewVO(){ }



    public ReviewVO(int m_reivewID, String m_userID, int m_mntID, String m_cotent, Bitmap m_Image, int m_like, double m_grade, boolean m_myLike) {
        this.m_reivewID =m_reivewID;
        this.m_userID = m_userID;
        this.m_mntID = m_mntID;
        this.m_cotent = m_cotent;
        this.m_Image = m_Image;
        this.m_like = m_like;
        this.m_grade = m_grade;
        this.m_Pic = m_myLike;
    }

    public ReviewVO(int m_reivewID, String m_userID, int m_mntID, String m_cotent, double m_grade, boolean m_Pic) {
        this.m_reivewID = m_reivewID;
        this.m_userID = m_userID;
        this.m_mntID = m_mntID;
        this.m_cotent = m_cotent;
        this.m_grade = m_grade;
        this.m_Pic = m_Pic;
    }

    public ReviewVO(int m_reivewID, String m_userID, int m_mntID, String m_cotent, boolean m_Pic) {
        this.m_reivewID = m_reivewID;
        this.m_userID = m_userID;
        this.m_mntID = m_mntID;
        this.m_cotent = m_cotent;
        this.m_Image = null;
        this.m_like = 0;
        this.m_grade = 0;
        this.m_Pic = m_Pic;
    }

    public int getM_reivewID() { return m_reivewID; }
    public void setM_reivewID(int m_reivewID) { this.m_reivewID = m_reivewID; }

    public String getUserId() { return m_userID; }
    public void setUserId(String m_userId) { this.m_userID = m_userId; }

    public int getM_mntID() { return m_mntID; }
    public void setM_mntID(int m_mntID) { this.m_mntID = m_mntID; }

    public String getCotent() { return m_cotent; }
    public void setCotent(String m_cotent) { this.m_cotent = m_cotent; }

    public Bitmap getImage() { return m_Image; }
    public void setImage(Bitmap m_mainImage) { this.m_Image = m_mainImage; }

    public int getLike() { return m_like; }
    public void setLike(int m_like) { this.m_like = m_like; }

    public double getGrade() { return m_grade; }
    public void setGrade(double m_grade) { this.m_grade = m_grade; }

    public boolean isPic() { return m_Pic; }
    public void setPic(boolean m_Pic) { this.m_Pic = m_Pic; }



    public void setReview(int m_reivewID, String m_userID, int m_mntID, String m_cotent, double m_grade, int m_like){
        this.m_reivewID = m_reivewID;
        this.m_userID = m_userID;
        this.m_mntID = m_mntID;
        this.m_cotent = m_cotent;
        this.m_grade = m_grade;
        this.m_like = m_like;
    }
}