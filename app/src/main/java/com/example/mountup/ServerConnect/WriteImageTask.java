package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.example.mountup.Listener.AsyncCallback;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WriteImageTask extends AsyncTask<Void, Void, String> {
    private AsyncCallback m_callback;
    private Exception m_exception;

    private String m_url;
    private String m_reviewID;
    private String m_filePath;

    public WriteImageTask(String m_url,String reviewID,String filePath, AsyncCallback m_callback) {
        this.m_url = m_url;
        this.m_reviewID = reviewID;
        this.m_filePath = filePath;
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
            imageSent();
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
            m_callback.onSuccess(true);
        } else {
            m_callback.onFailure(m_exception);
        }
    }

    public void imageSent(){
        // URL 뒤에 붙여서 보낼 파라미터
        URL url = null;
        try {
            url = new URL(m_url);
            String boundary = "-------";
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            con.setDoInput(true);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            DataOutputStream wr = new DataOutputStream(os);
            wr.writeBytes("\r\n--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"reviewID\"\r\n\r\n" + m_reviewID);
            wr.writeBytes("\r\n--" + boundary + "\r\n");
            wr.writeBytes("Content-Disposition: form-data; name=\"img\"; filename=\"image.jpg\"\r\n");
            wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            FileInputStream fileInputStream = new FileInputStream(m_filePath);
            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 4096;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                // Upload file part(s)
                DataOutputStream dataWrite = new DataOutputStream(con.getOutputStream());
                dataWrite.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            fileInputStream.close();
            wr.writeBytes("\r\n--" + boundary + "--\r\n");
            wr.flush();

            BufferedReader rd = null;

            Log.d("smh:getResponsecode" ,""+ con.getResponseCode());

            rd = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            String line = null;

            while ((line = rd.readLine()) != null) {
                Log.i("Lifeclue", line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}