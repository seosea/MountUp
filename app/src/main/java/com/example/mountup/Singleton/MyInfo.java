package com.example.mountup.Singleton;

import com.example.mountup.Model.User;

public class MyInfo {
    private String m_token;
<<<<<<< HEAD
    private User m_User;
=======
    private String m_userID;
    private String m_userPW;
>>>>>>> 510f06e791729893f0db1780523c66c4f5a9c9f5

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

<<<<<<< HEAD
    public User getUser(){ return m_User; }

    public void setUser(User user){ m_User = user; }
=======
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
>>>>>>> 510f06e791729893f0db1780523c66c4f5a9c9f5
}