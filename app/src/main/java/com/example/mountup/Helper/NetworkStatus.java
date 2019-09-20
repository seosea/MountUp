package com.example.mountup.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatus {
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_NOT_CONNECTED = 3;

    public static int getConnectivityStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo != null){
            int type = networkInfo.getType();
            if(type == ConnectivityManager.TYPE_MOBILE){ //3G or LTE
                return TYPE_MOBILE;
            }else if(type == ConnectivityManager.TYPE_WIFI){ //WIFI
                return TYPE_WIFI;
            }
        }
        return TYPE_NOT_CONNECTED;  //연결이 되지않은 상태
    }
}