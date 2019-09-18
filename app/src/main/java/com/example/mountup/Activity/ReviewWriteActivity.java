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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.mountup.R;
import com.example.mountup.ServerConnect.GetHttpURLConnection;
import com.example.mountup.ServerConnect.PostHttpURLConnection;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ReviewWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btn_close;
    private ImageButton btn_imageButton;

    private Button btn_submit;

    private RatingBar ratingBar_review;
    private EditText editText_review;

    private Uri m_imageCaptureUri;
    private String absolutePath;

    private String m_URL;

    private NetworkTask m_networkTask;

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        initView();
        initListener();

        m_URL = "http://15011066.iptime.org:8888";

        ContentValues values = new ContentValues();

        values.put("id","admin00");
        values.put("pw","123");

        m_networkTask = new NetworkTask(m_URL, null);

    }

    void initView(){
        btn_close = findViewById(R.id.btn_review_close);
        btn_imageButton = findViewById(R.id.btn_review_imageButton);
        btn_submit = findViewById(R.id.btn_review_submit);

        ratingBar_review = findViewById(R.id.ratingBar_reivew);
        editText_review = findViewById(R.id.editText_review);
    }

    void initListener() {
        btn_close.setOnClickListener(this);
        btn_imageButton.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        ratingBar_review.setOnRatingBarChangeListener(new Listener());
    }

    class Listener implements RatingBar.OnRatingBarChangeListener
    {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            Log.d("rating",""+v);
            ratingBar.setRating(v);
            ratingBar_review.setRating(v);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_review_close:
                Log.d("button","close");
                break;
            case R.id.btn_review_imageButton:
                Log.d("button","image");
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doTakePhotoAction();
                    }
                };
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
                        .setPositiveButton("사진촬영",cameraListener)
                        .setNeutralButton("앨범선택",albumListener)
                        .setNegativeButton("취소",cancelListenner)
                        .show();
                break;
            case R.id.btn_review_submit:
                Log.d("button","submit");

                m_networkTask.execute();

                break;
        }
    }
    public void doTakePhotoAction(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + System.currentTimeMillis() + ".png";
        m_imageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));

        intent.putExtra(MediaStore.EXTRA_OUTPUT,m_imageCaptureUri);
        startActivityForResult(intent,PICK_FROM_CAMERA);
    }

    //앨범에서 이미지 가져오기
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        Toast.makeText(getBaseContext(), "resultCode : "+resultCode,Toast.LENGTH_SHORT).show();

        if(resultCode== Activity.RESULT_OK)
        {
            try {
                //Uri에서 이미지 이름을 얻어온다.
                String name_Str = getImageNameToUri(data.getData());

                String str = ""+data.getData();

                //이미지 데이터를 비트맵으로 받아온다.
                //Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                Bitmap image_bitmap = resize(this,data.getData(),btn_imageButton.getHeight());


                //배치해놓은 ImageView에 set
                btn_imageButton.setImageBitmap(image_bitmap);
                //btn_imageButton.setImageURI(data.getData());

                Toast.makeText(getBaseContext(), "name_Str : "+name_Str , Toast.LENGTH_SHORT).show();

//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    private Bitmap resize(Context context, Uri uri, int resize){
        Bitmap resizeBitmap=null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번

            int width = options.outWidth;
            int height = options.outHeight;
            int samplesize = 1;

            while (true) {//2번width / 2 < resize ||
                if (height / 2 < resize)
                    break;
                width /= 2;
                height /= 2;
                samplesize *= 2;
            }

            options.inSampleSize = samplesize;
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
            resizeBitmap=bitmap;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resizeBitmap;
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

