package com.example.mountup.VO;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class MountVO {

    private int m_id;
    private Bitmap m_thumbnail;
    private String m_name;
    private int m_height;
    private float m_distance;
    private float m_grade;
    private Boolean m_isClimbed;
    private double m_x, m_y;
    private String m_address; // 주소
    private String m_imgURL; // 이미지 URL
    private String m_intro; // 산 소개

    public MountVO() {
    }

    //  "mntID": 1,
    //  "mntName": "개운산",
    //  "mntHeight": "134",
    //  "mntInfo": "개운산입니다.",
    //  "mntPlace": "서울특별시 성북구 종암동",
    //  "mntStar": 4.5,
    //  "mntLocX": "37.598068",
    //  "mntLocY": "127.025347"

    public void setMount(int id, String name, int height, String intro, String address, float grade, double x, double y) {
        this.m_id = id;
        this.m_name = name;
        this.m_height = height;
        this.m_intro = intro;
        this.m_address = address;
        this.m_grade = grade;
        this.m_x = x;
        this.m_y = y;
    }

    /*
    public MountVO(Drawable thumbnail, String name, int height, float distance, float grade, Boolean isClimbed)
    {
        this.m_thumbnail = thumbnail;
        this.m_name = name;
        this.m_height = height;
        this.m_distance = distance;
        this.m_grade = grade;
        this.m_isClimbed = isClimbed;
    }
    */

    public int getID() {
        return m_id;
    }
    public void setID(int id) {
        this.m_id = id;
    }

    public Bitmap getThumbnail() {
        return m_thumbnail;
    }
    public void setThumbnail(Bitmap thumbnail) {
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

    public float getDistance() {
        return Math.round(m_distance * 10) / 10.0f;
    }
    public void setDistance(float distance) {
        this.m_distance = distance;
    }

    public float getGrade() {
        return Math.round(m_grade * 10) / 10.0f;
    }
    public void setGrade(float grade) { this.m_grade = grade; }

    public double getLocX() { return m_x; }
    public double getLocY() { return m_y; }

    public String getAddress() {
        return m_address;
    }
    public void setAddress(String address) {
        this.m_address = address;
    }

    public String getIntro() {
        return m_intro;
    }
    public void setIntro(String intro) {
        this.m_intro = intro;
    }

    public boolean isClimbed() {
        return m_isClimbed;
    }
    public void setClimb(Boolean bClimb) {
        this.m_isClimbed = bClimb;
    }
}
