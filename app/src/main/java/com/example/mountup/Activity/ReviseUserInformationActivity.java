package com.example.mountup.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mountup.Listener.AsyncCallback;
import com.example.mountup.R;
import com.example.mountup.ServerConnect.WriteImageTask;
import com.example.mountup.Singleton.MyInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ReviseUserInformationActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_FROM_ALBUM = 1;
    public final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;

    private ImageButton btnClose, btnUploadImage;
    private Button btnComplete;
    private ImageView imgProfile;
    private TextView txtID;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_user_information);
        initView();
        initListener();

        checkPermission();

        if(MyInfo.getInstance().getUser().getID() != null)
            txtID.setText(MyInfo.getInstance().getUser().getID());

        String url = "http://15011066.iptime.org:8888/userimages/";
        NetworkTask networkTask = new NetworkTask(url, new AsyncCallback() {
            @Override
            public void onSuccess(Object object) {
                imgProfile.setImageBitmap(MyInfo.getInstance().getUser().getProfile());
                setImageRound();
            }
            @Override
            public void onFailure(Exception e) {}
        });
        networkTask.execute();
    }

    private void initView(){
        btnClose = findViewById(R.id.btn_close_revise_user_information);
        btnUploadImage = findViewById(R.id.btn_upload_image_revise_user_information);
        btnComplete = findViewById(R.id.btn_complete_revise_user_information);
        imgProfile = findViewById(R.id.img_profile_revise_user_information);
        txtID = findViewById(R.id.txt_id_revise_user_information);
    }

    private void initListener(){
        btnClose.setOnClickListener(this);
        btnUploadImage.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        return cursor.getString(column_index);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK) {
            // result가 제대로 실행됨.
            if (requestCode == PICK_FROM_ALBUM ) {
                //앨범 선택
                uri = data.getData();

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
                imgProfile.setImageBitmap(rotate(bitmap, exifDegree));
                setImageRound();

                //유저 이미지 업로드
                String imageUploadURL = "http://15011066.iptime.org:8888/userimageup";
                String key = "id";
                String value = MyInfo.getInstance().getUser().getID();

                WriteImageTask writeImageTask = new WriteImageTask(imageUploadURL,key,value, getRealFilePath(uri), new AsyncCallback(){
                    @Override
                    public void onSuccess(Object object) {
                        Log.d("smh:user_image_upload","success");
                    }
                    @Override
                    public void onFailure(Exception e) { }
                });
                writeImageTask.execute();
            }
        }
    }

    private void setImageRound(){
        imgProfile.setBackground(new ShapeDrawable(new OvalShape()));
        if(Build.VERSION.SDK_INT >= 21) {
            imgProfile.setClipToOutline(true);
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) { return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) { return 270; }
        return 0;
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

    /*
    private void connectNetwork() {
        URL url = null;
        try {
            url = new URL("http://15011066.iptime.org:8888/userimageup");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        final String boundary = "SpecificString";
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        //읽기 쓰기
        con.setDoOutput(true);
        con.setDoInput(true);
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        //캐시를 사용하지 않게 설정
        con.setUseCaches(false);

        final URLConnection finalCon = con;
        new Thread() {
            public void run() {

                try {
                    DataOutputStream wr = new DataOutputStream(finalCon.getOutputStream());
                    wr.writeBytes("Content-Disposition: form-data; name=\"id\"\r\n\r\n" + Constant.ADMIN_ID);
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\"image.jpg\"\r\n");
                    wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");

                    // Bitmap을 ByteBuffer로 전환
                    Drawable d = imgProfile.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable)d).getBitmap();

                    byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
                    for (int i = 0; i < bitmap.getWidth(); ++i) {
                        for (int j = 0; j < bitmap.getHeight(); ++j) {
                            //we're interested only in the MSB of the first byte,
                            //since the other 3 bytes are identical for B&W images
                            pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
                        }
                    }
                    wr.write(pixels);

                    wr.writeBytes("\r\n--" + boundary + "--\r\n");
                    wr.flush();
                    wr.close();

                    BufferedReader rd = null;
                    // Response받기
                    InputStream responseStream = new
                            BufferedInputStream(finalCon.getInputStream());
                    BufferedReader responseStreamReader =
                            new BufferedReader(new InputStreamReader(responseStream));
                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    responseStreamReader.close();
                    String response = stringBuilder.toString();


                    //Response stream종료
                    responseStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    */

   @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_complete_revise_user_information:
                //connectNetwork();
            case R.id.btn_close_revise_user_information:
                onBackPressed();
                break;
            case R.id.btn_upload_image_revise_user_information:
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
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, Void> {
        private String url;
        private AsyncCallback m_callback;
        private Exception m_exception;

        public NetworkTask(String url,AsyncCallback callback) {
            this.url = url;
            this.m_callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String user_img = url + MyInfo.getInstance().getUser().getID()+".jpg";
            InputStream is_user = null;
            try {
                is_user = (InputStream) new URL(user_img).getContent();
            } catch (IOException e) {
                e.printStackTrace();
                m_exception = e;
            }

            if(is_user != null) {
                Drawable user_drawable = Drawable.createFromStream(is_user, "mount" + MyInfo.getInstance().getUser().getID());
                MyInfo.getInstance().getUser().setProfile(((BitmapDrawable) user_drawable).getBitmap());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (m_callback != null && m_exception == null) {
                m_callback.onSuccess(result);
            } else {
                m_callback.onFailure(m_exception);
            }
        }
    }
}
