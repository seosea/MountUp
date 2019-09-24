package com.example.mountup.Singleton;

import com.example.mountup.Model.User;

public class MyInfo {
    private String m_token;
    private User m_User;

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

    public User getUser(){ return m_User; }

    public void setUser(User user){ m_User = user; }
}