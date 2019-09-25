package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpTask extends AsyncTask<Void, Void, Void> {
    AsyncCallback m_callback;
    Exception m_exception;
    String m_url;
    ContentValues m_values;

    public SignUpTask(String url, ContentValues values, AsyncCallback callback) {

        m_url = url;
        m_values = values;
        m_callback = callback;
        m_exception = null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String result;
        try {
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            result = requestHttpURLConnection.request(m_url, m_values);
            if (isIDOverlap(result)) {
                throw new Exception("Entered ID be overlapped");
            }
        } catch (Exception e) {
            e.printStackTrace();
            m_exception = e;
            return null;
        }

        return null;
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

    private boolean isIDOverlap(String json_str) {
        try {
            JSONObject jsonObj = new JSONObject(json_str);

            int code = jsonObj.getInt("code");
            if (code == 200) {
                return false;
            } else if (code == 400) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }
}
