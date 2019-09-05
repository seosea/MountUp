package com.example.mountup.VO;

import android.graphics.drawable.Drawable;

public class MountVO {

    private Drawable m_thumbnail;
    private String m_name;
    private int m_height;
    private float m_distance;
    private float m_grade;
    private Boolean m_bClimb;

    public Drawable getThumbnail() {
        return m_thumbnail;
    }
    public void setThumbnail(Drawable thumbnail) {
        this.m_thumbnail = thumbnail;
    }

    public String getName() {
        return m_name;
    }
    public void setName(String name) {
        this.m_name = name;
    }

    public int getHeight() { return m_height; }
    public void setHeight(int height) {
        this.m_height = height;
    }

    public float getDistance() { return m_distance; }
    public void setDistance(float distance) {
        this.m_distance = distance;
    }

    public float getGrade() { return m_grade; }
    public void setGrade(float grade) { this.m_grade = grade; }

    public boolean isClimb() {
        return m_bClimb;
    }
    public void setClimb(Boolean bClimb) {
        this.m_bClimb = bClimb;
    }
}
