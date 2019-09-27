package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mountup.Listener.AsyncCallback;

public class LikeTask extends AsyncTask<Void, Void, String> {
    private AsyncCallback m_callback;
    private Exception m_exception;

    String m_url;
    ContentValues m_values;

    public LikeTask(String m_url, ContentValues m_values, AsyncCallback m_callback) {
        this.m_url = m_url;
        this.m_values = m_values;
        this.m_callback = m_callback;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String result = null;

        try {
            PostHttpURLConnection postHttpURLConnection = new PostHttpURLConnection();
            result = postHttpURLConnection.request(m_url, m_values);

            Log.d("smh:result ",result);
        } catch (Exception e) {
            e.printStackTrace();
            m_exception = e;
        }

        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (m_callback != null && m_exception == null) {
            m_callback.onSuccess(s);
        } else {
            m_callback.onFailure(m_exception);
        }
    }
}

