package com.example.mountup.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mountup.Model.User;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.GetHttpURLConnection;
import com.example.mountup.ServerConnect.PostHttpURLConnection;
import com.example.mountup.Singleton.MyInfo;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ReviewWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private String m_mountID;
    private User m_user;

    private ImageButton btn_close;
    private ImageButton btn_imageButton;

    private Button btn_submit;

    private RatingBar ratingBar_review;
    private EditText editText_review;
    private TextView tv_review_length;

    private String m_URL;

    private NetworkTask m_networkTask;
    private File tempFile;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        initData();
        initView();
        initListener();
    }

    void initData(){
        m_user = MyInfo.getInstance().getUser();
        m_URL = "http://15011066.iptime.org:8888";

        Intent intent = getIntent();
        m_mountID = intent.getStringExtra("mountID");

        Log.d("mountID",""+m_mountID);
    }

    void initView(){
        btn_close = findViewById(R.id.btn_review_close);
        btn_imageButton = findViewById(R.id.btn_review_imageButton);
        btn_submit = findViewById(R.id.btn_review_submit);


        ratingBar_review = findViewById(R.id.ratingBar_reivew);
        editText_review = findViewById(R.id.editText_review);

        //글자수 제한
        tv_review_length = findViewById(R.id.tv_review_length);
        int max = 200;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(max);
        editText_review.setFilters(fArray);
    }

    void initListener() {
        btn_close.setOnClickListener(this);
        btn_imageButton.setOnClickListener(this);
        btn_submit.setOnClickListener(this);

        ratingBar_review.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                Log.d("rating",""+v);
                ratingBar.setRating(v);
                ratingBar_review.setRating(v);
            }
        });
        editText_review.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = editText_review.getText().toString();
                tv_review_length.setText(input.length()+" / 200");
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

//        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK)
//        {
//            Uri uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                btn_imageButton.setImageBitmap(bitmap);
//                Toast.makeText(this, ""+ data.getData(), Toast.LENGTH_LONG).show();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


        if(requestCode == PICK_FROM_ALBUM)
        {
            Uri photoUri = data.getData();
            Cursor cursor = null;

            try {
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();

                tempFile = new File(cursor.getString(column_index));


            }finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        }
    }

    void setImage(){
        BitmapFactory.Options options = new BitmapFactory.Options();

        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);

        btn_imageButton.setImageBitmap(originalBm);
    }

    //앨범에서 이미지 가져오기
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_review_close:
                Log.d("button","close");
                finish();
                break;
            case R.id.btn_review_imageButton:
                Log.d("button","image");

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doTakeAlbumAction();
                    }
                };
                DialogInterface.OnClickListener cancelListenner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };

                new AlertDialog.Builder(this)
                        .setTitle("업로드할 이미지 선택")
                        .setNeutralButton("앨범선택",albumListener)
                        .setNegativeButton("취소",cancelListenner)
                        .show();
                break;
            case R.id.btn_review_submit:
                Log.d("button","submit");
                ContentValues values = new ContentValues();
                //통신할거 저장하기
//                Headers : {x-access-token : “token”, id : “id”}
//                Body : {"reviewUserID ":”userid”,
//                    "reviewMntID ":”mntid”,
//                    "reviewString ":”reviewstring”,
//                    "reviewStar ": “star point” }

                values.put("reviewUserID","" + m_user.getID() );
                values.put("reviewMntID","" + m_mountID);
                values.put("reviewString", editText_review.getText().toString());
                values.put("reviewStar", Float.toString(ratingBar_review.getRating()));

                m_networkTask = new NetworkTask(m_URL, values);

                m_networkTask.execute();

                finish();
                break;
        }
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            GetHttpURLConnection postHttpURLConnection = new GetHttpURLConnection();
            result = postHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            editText_review.setText("1"+s);
        }
    }
}

