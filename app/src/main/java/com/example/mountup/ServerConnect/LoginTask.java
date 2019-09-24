package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Singleton.MyInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginTask extends AsyncTask<Void, Void, Void>  {
    private AsyncCallback m_callback;
    private Exception m_exception;
    String url;
    ContentValues values;

    public LoginTask(String url, ContentValues values, AsyncCallback callback){
        this.m_callback = callback;
        this.url = url;
        this.values = values;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progress bar를 보여주는 등등의 행위
    }

    @Override
    protected Void doInBackground(Void... params) {
        String result;

        // MyInfo에 토큰 설정
        try {
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // post token

            if (!isEnteredLoginDataValid(result)) {
                throw new Exception("Entered Login Data not valid");
            }

            // 토큰 설정
            JSONObject job = new JSONObject(result);
            String str = job.getString("token");
            MyInfo.getInstance().setToken(str);

            // 유저 ID, Password 설정
            MyInfo.getInstance().getUser().setID(values.getAsString("id"));
            MyInfo.getInstance().getUser().setPassword(values.getAsString("pw"));

        } catch (Exception e) {
            e.printStackTrace();
            m_exception = e;
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

    private boolean isEnteredLoginDataValid(String json_str) {
        // 성공 : 200, 실패 : 204
        try {
            JSONObject jsonObj = new JSONObject(json_str);

            int code = jsonObj.getInt("code");

            if (code == 204) {
                return false;
            } else if (code == 200) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}