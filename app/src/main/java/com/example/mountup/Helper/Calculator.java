package com.example.mountup.Helper;

import android.util.Log;

public class Calculator {

    public static float calculateDistance(double lat2, double lon2){

        double theta = Constant.Y - lon2;
        double dist = Math.sin(deg2rad(Constant.X)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(Constant.X)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344;

        Log.v("Distance", String.valueOf(dist));
        Log.v("x", String.valueOf(lat2));
        Log.v("y", String.valueOf(lon2));
        return (float)(dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
