package com.example.mountup.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mountup.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MountMapFragment extends Fragment implements View.OnClickListener{

    private ImageView imgWeather;
    private TextView txtTemperature, txtMicroDust, txtOzone;

    private WeatherConnection weatherConnection;
    private AsyncTask<String, String, String> result;

    private String strTemperature, strWeather, strMicroDust, strOzone;

    private ImageButton btnRefresh;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mount_map, container, false);

        initView(view);

        initListener();
        setWeather();

        return view;
    }

    private void initView(View view){
        imgWeather = view.findViewById(R.id.img_weather_map);
        txtTemperature = view.findViewById(R.id.txt_temperature_map);
        txtMicroDust = view.findViewById(R.id.txt_micro_dust_map);
        txtOzone = view.findViewById(R.id.txt_ozone_map);

        weatherConnection = new WeatherConnection();

        btnRefresh = view.findViewById(R.id.btn_refresh_weather);
    }

    private void initListener(){
        btnRefresh.setOnClickListener(this);
    }

    // 날씨 설정(재설정)
    private void setWeather(){
        String msg = connectWeatherCast();
        setWeatherView(msg);
    }

    // 날씨 view 설정(재설정)
    private void setWeatherView(String msg){
        String[] txtWeatherCast = msg.split("℃");

        strTemperature = txtWeatherCast[0];
        strWeather = txtWeatherCast[1];
        strMicroDust = txtWeatherCast[2];
        strOzone = txtWeatherCast[3];

        if(strWeather.contains("눈")){
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_snow));
        } else if(strWeather.contains("비")){
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_rain));
        } else if(strWeather.contains("구름")||strWeather.contains("흐림")){
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_cloud));
        } else {
            imgWeather.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_weather_sun));
        }
        txtTemperature.setText(strTemperature);
        txtMicroDust.setText(strMicroDust);
        txtOzone.setText(strOzone);

    }

    // 날씨 크롤링
    private String connectWeatherCast(){

        if(result == null) {
            result = weatherConnection.execute("", "");
        }

        System.out.println("RESULT");

        try{
            String msg = result.get();
            System.out.println("MSG : " + msg);
            return msg;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_refresh_weather:
                setWeather();
            break;
        }
    }

    // 날씨 크롤링 테스크. 네트워크는 AsyncTask 를 사용, 백에서 작업
    public class WeatherConnection extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... params) {
            try{
                String path = "http://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=09650510";
                Document document = Jsoup.connect(path).get();
                Elements elements = document.select("em");

                System.out.println("Element : " + elements);
                Element targetElement1 = elements.get(2);
                Element targetElement2 = elements.get(3);
                Element targetElement3 = elements.get(4);

                String text = targetElement1.text() + "℃" + targetElement2.text() + "℃" + targetElement3.text();
                System.out.println("Text : " + text);

                return text;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }



}

