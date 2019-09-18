package com.example.mountup.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mountup.Helper.BackPressCloseHandler;
import com.example.mountup.Helper.Constant;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.MountVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnSignUp;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();

        backPressCloseHandler = new BackPressCloseHandler(this);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
    }

    private void initListener() {
        btnLogin.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                    //////////////////////////////////////////////////////
                Constant.ADMIN_ID = "admin";
                Constant.ADMIN_PW = "1234";

                // ID PW 설정
                ContentValues values = new ContentValues();
                values.put("id", Constant.ADMIN_ID);
                values.put("pw", Constant.ADMIN_PW);

                // 로그인 URL 설정
                String url = "http://15011066.iptime.org:8888/api/login";

                // execute 및 MyInfo에 토큰 저장
                TokenTask tokenTask = new TokenTask(url, values);
                tokenTask.execute();
                //tokenTask.executeOnExecutor()


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues values = new ContentValues();
                        values.put("id", Constant.ADMIN_ID);
                        values.put("pw", Constant.ADMIN_PW);

                        // 산 URL 설정
                        String url = "http://15011066.iptime.org:8888/api/all";

                        // execute, 산 리스트 생성 및 저장
                        MountTask mountTask = new MountTask(url, values);
                        mountTask.execute();
                    }
                }, 2000);

                /////////////////////////////////////////////////////////////

                // TODO: 로그인
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.btn_sign_up:
                // TODO: 회원가입
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    public class MountTask extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        MountTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.

            Log.d("mee:getMountJson", "result : " + result);

            try {
                JSONArray jsonArray = new JSONArray(result);
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
                    // (임시) 사진, 거리, 등반 확인, 별점
                    newItem.setThumbnail(ContextCompat.getDrawable(Constant.context, R.drawable.mountain_sample));
                    newItem.setDistance(new Random().nextFloat() * 100);
                    newItem.setGrade(new Random().nextFloat() * 5);
                    newItem.setClimb(new Random().nextInt(2) > 0 ? true : false);

                    MountManager.getInstance().getItems().add(newItem);
                    /*
                    Log.d("mee:createItems","mntID :  " + mntID + " / MntName : " + mntName + " / mntHeight :  " + mntHeight
                                                    + "/ mntInfo :  " + mntInfo + " / mntPlace : " + mntPlace + " / mntStar :  " + mntStar
                                                    + "/ mntLocX : " + mntLocX + " / mntLocY : " + mntLocY);
                                                    */
                }

                //  "mntID": 1,
                //  "mntName": "개운산",
                //  "mntHeight": "134",
                //  "mntInfo": "개운산입니다.",
                //  "mntPlace": "서울특별시 성북구 종암동",
                //  "mntStar": 4.5,
                //  "mntLocX": "37.598068",
                //  "mntLocY": "127.025347"
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            //Log.d("mee:MountTask", result);
        }
    }

    public class TokenTask extends AsyncTask<Void, Void, String>  {
        String url;
        ContentValues values;

        TokenTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            PostHttpURLConnection requestHttpURLConnection = new PostHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);

            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("mee:TokenPost", "result : " + result);

            // MyInfo에 토큰 설정
            try {
                JSONObject job = new JSONObject(result);
                String token_str = job.getString("token");
                MyInfo.getInstance().setToken(token_str);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다.
        }

        /* HttpPost 형식
        @Override
        protected String doInBackground(String... urls) {
            String json_string = "";
            JSONObject jsonObject = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(urls[0]);
                httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("id", "admin00"));
                params.add(new BasicNameValuePair("pw", "123"));

                httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity respEntity = response.getEntity();

                if (respEntity != null) {
                    json_string = EntityUtils.toString(respEntity);
                    jsonObject = new JSONObject(json_string);
                    return json_string;
                }

                Log.d("mee:doInBackground", "result : " + json_string);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        */
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
                Log.d("mee:doInBackground", "URL : " + url.toString());

                // json object 생성
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("id", "admin00");
                jsonObject.accumulate("pw", "123");
                json = jsonObject.toString();

                Log.d("mee:doInBackground", "jsonObject : " + json);

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
                        Log.d("mee:Login", "POST : " + result);

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
}
