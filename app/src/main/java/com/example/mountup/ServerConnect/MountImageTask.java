package com.example.mountup.ServerConnect;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.VO.MountVO;
import java.util.ArrayList;

public class MountImageTask extends AsyncTask<Void, Void, Void> {
    AsyncCallback m_callback;
    Exception m_exception;

    public MountImageTask(AsyncCallback callback) {
        this.m_callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected Void doInBackground(Void... params) {
        ArrayList<MountVO> mountList = MountManager.getInstance().getItems();

        try {
            for (int i = 0; i < 10; i++) {
                if (mountList.get(i).getThumbnail() == null) {
                    int id = mountList.get(i).getID();
                    String url_img = Constant.URL + "/basicImages/" + id + ".jpg";
                    mountList.get(i).setThumbnail(MountManager.getInstance().getMountBitmapFromURL(url_img, "mount" + id));
                    Log.d("mmee:loadFirstData", "get mount resource " + id);
                }
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
}