package com.example.mountup.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mountup.Helper.Constant;
import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.MountTask;
import com.example.mountup.ServerConnect.StarTask;
import com.example.mountup.ServerConnect.WriteImageTask;
import com.example.mountup.ServerConnect.WriteTask;
import com.example.mountup.Singleton.MountManager;
import com.example.mountup.Singleton.MyInfo;
import com.example.mountup.VO.MountVO;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReviewWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton btn_close;
    private ImageButton btn_imageButton;
    private Button btn_submit;

    private RatingBar ratingBar_review;
    private EditText editText_review;
    private TextView tv_review_length;
    //view part
    private int m_mountID;
    private Uri m_uri;

    private String m_reviewSentURL;
    private String m_reviewImageUploadURL;
    private String m_reviewStarURL;

    private static final int PICK_FROM_ALBUM = 1;

    public final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_write);

        initData();
        initView();
        initListener();
    }

    void initData(){
        m_reviewSentURL = "http://15011066.iptime.org:8888/api/review";
        m_reviewImageUploadURL = "http://15011066.iptime.org:8888/reviewimageup";
        m_reviewStarURL = "http://15011066.iptime.org:8888/api/star";

        Intent intent = getIntent();
        m_mountID = Integer.parseInt(intent.getStringExtra("mountID"));

        checkPermission();
        //권한 체크
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
        if(resultCode == RESULT_OK) {
            // result가 제대로 실행됨.
            if (requestCode == PICK_FROM_ALBUM ) {
                //앨범 선택
                Uri uri = data.getData();
                m_uri = uri;
                ExifInterface exif = null;
                String imagePath = getRealPathFromURI(uri);
                try {
                    exif = new ExifInterface(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                btn_imageButton.setImageBitmap(rotate(bitmap, exifDegree));
            }
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }
        return 0;
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        Log.d("smh:getRealPathFromURI", cursor.getString(column_index));
        return cursor.getString(column_index);
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree); // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    //앨범에서 이미지 가져오기
    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_FROM_ALBUM);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_review_close:
                Log.d("smh:button","close");
                finish();
                break;
            case R.id.btn_review_imageButton:
                Log.d("smh:button","image");
                pushImageButton();
                break;
            case R.id.btn_review_submit:
                Log.d("smh:button","submit");
                pushSubmitButton();
                break;
        }
    }

    public void pushImageButton(){
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
    }

    public void pushSubmitButton(){
        btn_submit.setEnabled(false);

        ContentValues values = new ContentValues();

        values.put("reviewUserID", MyInfo.getInstance().getUser().getID() );
        values.put("reviewMntID", m_mountID);
        values.put("reviewString", editText_review.getText().toString());
        values.put("reviewStar", ratingBar_review.getRating());

        WriteTask writeTask = new WriteTask(m_reviewSentURL, values, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                String result = object.toString();
                String reviewID = null;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    reviewID = jsonObj.getString("reviewID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                WriteImageTask imageTask = new WriteImageTask(m_reviewImageUploadURL,"reviewID",reviewID,getRealFilePath(m_uri),new AsyncCallback(){
                    @Override
                    public void onSuccess(Object object) {
                        ContentValues values = new ContentValues();
                        values.put("reviewMntID", m_mountID);

                        StarTask starTask = new StarTask(m_reviewStarURL, values, new AsyncCallback() {
                            @Override
                            public void onSuccess(Object object) {
                                String url = Constant.URL + "/api/mntall";
                                MountTask mountTask = new MountTask(Constant.UPDATE_STAR, url, null, new AsyncCallback() {
                                    @Override
                                    public void onSuccess(Object object) {
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {

                                    }
                                });
                                mountTask.execute();
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                        starTask.execute();
                }
                    @Override
                    public void onFailure(Exception e) { }
                    });
                imageTask.execute();
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
        writeTask.execute();
    }

    public String getRealFilePath(Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Log.d("smh:uri",""+contentUri.toString());

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        Log.d("smh:", "getRealfilepath(), path : " + uri.toString());

        cursor.close();
        return path;
    }

    //갤러리 접근 권한 설정
    private void checkPermission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"갤러리 사용을 위해 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
                Toast.makeText(this,"갤러리 사용을 위해 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this,"승인이 허가되어 있습니다.",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,"아직 승인받지 않았습니다.",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}

