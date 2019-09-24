package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MountTask extends AsyncTask<Void, Void, Void> {
    AsyncCallback m_callback;
    Exception m_exception;
    String m_url;
    ContentValues m_values;

    public MountTask(String url, ContentValues values, AsyncCallback callback) {
        this.m_callback = callback;
        this.m_url = url;
        this.m_values = values;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected Void doInBackground(Void... params) {
        String mountList_json_str;

        try {
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            mountList_json_str = requestHttpURLConnection.request(m_url, Constant.ADMIN_ID, m_values);

            initMountFromJson(mountList_json_str);
        } catch(Exception e) {
            this.m_exception = e;
            return null;
        }

        return null; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (m_callback != null && m_exception == null) {
            m_callback.onSuccess(true);
        } else {
            m_callback.onFailure(m_exception);
        }
    }

    private void initMountFromJson(String json_str) {
        //Log.d("mmee:initMountFromJson", "json_str : " + json_str);

        try {
            JSONArray jsonArray = new JSONArray(json_str);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                int mntID = jsonObj.getInt("mntID");
                String mntName = jsonObj.getString("mntName");
                int mntHeight = jsonObj.getInt("mntHeight");
                String mntInfo = jsonObj.getString("mntInfo");
                String mntPlace = jsonObj.getString("mntPlace");
                double mntStar = jsonObj.getDouble("mntStar");
                double mntLocX = jsonObj.getDouble("mntLocX");
                double mntLocY = jsonObj.getDouble("mntLocY");

                MountVO newItem = new MountVO();
                newItem.setMount(mntID, mntName, mntHeight, mntInfo, mntPlace, (float)mntStar, mntLocX, mntLocY);

                //String url_img = Constant.URL + "/basicImages/" + (i + 1) + ".jpg";
                //newItem.setThumbnail(MountManager.getInstance().getMountBitmapFromURL("url_img","mount" + (i + 1)));
                //Log.d("mmee:mountTask", "get mount resource " + (i + 1));

                // (임시) 거리, 등반 확인, 별점
                newItem.setDistance(new Random().nextFloat() * 100);
                newItem.setGrade(new Random().nextFloat() * 5);
                newItem.setClimb(false);

                MountManager.getInstance().getItems().add(newItem);

                //Log.d("mmee:createItems","mntID :  " + mntID + " / MntName : " + mntName + " / mntHeight :  " + mntHeight + "/ mntInfo :  " + mntInfo + " / mntPlace : " + mntPlace + " / mntStar :  " + mntStar
                //                                    + "/ mntLocX : " + mntLocX + " / mntLocY : " + mntLocY);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}