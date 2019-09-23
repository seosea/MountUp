package com.example.mountup.Singleton;

public class MyInfo {

    private String m_token;
    private String m_userID;
    private String m_userPW;

    private MyInfo() {}

    public static MyInfo getInstance() {
        return MyInfoHolder.instance;
    }

    private static class MyInfoHolder {
        public static final MyInfo instance = new MyInfo();
    }

    public String getToken() {
        return m_token;
    }

    public void setToken(String token) {
        m_token = token;
    }

    public String getUserID() {
        return m_userID;
    }

    public void setUserID(String userID) {
        this.m_userID = userID;
    }

    public String getUserPW() {
        return m_userPW;
    }

    public void setUserPW(String userPW) {
        this.m_userPW = userPW;
    }
}