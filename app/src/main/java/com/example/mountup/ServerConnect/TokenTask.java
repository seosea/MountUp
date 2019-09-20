package com.example.mountup.ServerConnect;

import android.content.ContentValues;
import android.os.AsyncTask;

import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.Singleton.MyInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class TokenTask extends AsyncTask<Void, Void, Void>  {
    private AsyncCallback m_callback;
    private Exception m_exception;
    String url;
    ContentValues values;

    public TokenTask(String url, ContentValues values, AsyncCallback callback){
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
        PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
        result = requestHttpURLConnection.request(url, values); // post token
        // MyInfo에 토큰 설정
        try {
            JSONObject job = new JSONObject(result);
            String token_str = job.getString("token");
            MyInfo.getInstance().setToken(token_str);

        } catch (JSONException e) {
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

        // 통신이 완료되면 호출됩니다.
        // 결과에 따른 UI 수정 등은 여기서 합니다.
    }
            /*
            InputStream is = null;
            String result = "";
            BufferedReader reader = null;
            try {
                String json = "";

                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                // 전송모드 설정
                con.setDefaultUseCaches(false);
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setRequestMethod("POST");

                // content-type 설정
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");


                con.connect();
                Log.d("mmee:doInBackground", "URL : " + url.toString());

                // json object 생성
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", "admin00");
                jsonObject.accumulate("pw", "123");
                json = jsonObject.toString();

                Log.d("mmee:doInBackground", "jsonObject : " + json);

                // 전송값 설정
                //StringBuffer buffer = new StringBuffer();
                //buffer.append("id").append("=").append("admin00").append("&");
                //buffer.append("pw").append("=").append("123");


                // 서버로 전송
                //OutputStreamWriter os = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
                OutputStream os = con.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.flush();

                // 서버에서 데이터 수신
                try {
                    is = con.getInputStream();
                    if (is != null) {
                        reader = new BufferedReader(new InputStreamReader(is));

                        StringBuffer buffer = new StringBuffer();

                        String line = "";
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line);
                        }

                        result = buffer.toString();
                        Log.d("mmee:Login", "POST : " + result);

                        return result;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (MalformedURLException | JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }*/
}