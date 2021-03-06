package com.example.mountup.Helper;

import android.content.Context;

public class Constant {

    public static Context context;

    // 화면 사이즈 저장
    public static int WIDTH;
    public static int HEIGHT;

    // 관리자 ID PW
    public static String ADMIN_ID = "admin";
    public static String ADMIN_PW = "1234";

    public static String URL = "http://15011066.iptime.org:8888";

    // Fragment Type
    public static final int FRAGMENT_LIST = 1;
    public static final int FRAGMENT_MAP = 2;
    public static final int FRAGMENT_USER = 3;
    public static final int FRAGMENT_SETTING = 4;

    // Server Task 타입
    // 산 데이터 Task Type
    public static final int GET_NEW = 1;
    public static final int UPDATE_STAR = 2;

    // 산 이미지 Task Type
    public static final int FIRST_TEN = 1;
    public static final int CLIMBED = 2;

    public static String CURRENT_ADDRESS;
    public static Double X,Y;
}
