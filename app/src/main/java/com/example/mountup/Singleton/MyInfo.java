package com.example.mountup.Singleton;

public class MyInfo {

    private String m_token;

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

}