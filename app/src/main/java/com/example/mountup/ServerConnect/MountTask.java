package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MountTask extends AsyncTask<Void, Void, Void> {
    private int taskType;
    private AsyncCallback m_callback;
    private Exception m_exception;
    private String m_url;
    private ContentValues m_values;

    public MountTask(int taskType, String url, ContentValues values, AsyncCallback callback) {
        this.m_callback = callback;
        this.m_url = url;
        this.m_values = values;
        this.taskType = taskType;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected Void doInBackground(Void... params) {
        String result;

        try {
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            result = requestHttpURLConnection.request(m_url, m_values);

            if (taskType == Constant.GET_NEW)
                initMountFromJson(result);
            else if (taskType == Constant.UPDATE_STAR) {
                updateStarFromJson(result);
            }
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

    private void updateStarFromJson(String mountList_json_str) {
        try {
            ArrayList<MountVO> MountList = MountManager.getInstance().getItems();

            JSONArray jsonArray = new JSONArray(mountList_json_str);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                int mntID = jsonObj.getInt("mntID");
                double mntStar = jsonObj.getDouble("mntStar");
                for (MountVO mount : MountList) {
                    if (mount.getID() == mntID) {
                        mount.setGrade((float)mntStar);
                        break;
                    }
                }
            }

            //MountManager.getInstance().sortMountList(MountManager.getInstance().getCurrentSort());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMountFromJson(String mountList_json_str) {
        try {
            JSONArray jsonArray = new JSONArray(mountList_json_str);
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

                String url_img = Constant.URL + "/basicImages/" + (i + 1) + ".jpg";
                newItem.setThumbnail(MountManager.getInstance().getMountBitmapFromURL(url_img,"mount" + (i + 1)));
                Log.d("mmee:mountTask", "get mount resource " + (i + 1));

                newItem.setClimb(false);

                // 임시 별점
                // newItem.setGrade(new Random().nextFloat() * 5);

                MountManager.getInstance().getItems().add(newItem);

                int loadPercent = (int)((i + 1) / (float)jsonArray.length() * 100.0f);
                MountManager.getInstance().setLoadPercent(loadPercent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}