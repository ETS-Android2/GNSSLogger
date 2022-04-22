package com.google.android.apps.location.gps.gnsslogger;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;

//
//import com.github.petr_s.nmea.GpsSatellite;
//import com.github.petr_s.nmea.NMEAHandler;
//import com.github.petr_s.nmea.NMEAParser;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class SendServerTask extends AsyncTask<String, Void, String> {

    private String measurementUrl;
    private String url;
    private ContentValues value;
    private UiLogger uiLogger = null;
    private String serverNum = null;
    private MapFragment mapFragment = null;
    private float GPSSpeed = 0.0f;
    private float GPSBreaing = 0.0f;
    File baseDirectory = null;

    public SendServerTask() {

    }

    //public SendServerTask(String measureUrl, String Dataurl, ContentValues value, float speed, float bearing) {
    public SendServerTask(String measureUrl, String Dataurl,String serverNum,ContentValues value, float speed, float bearing) {
        this.measurementUrl = measureUrl;
        this.url = Dataurl;
        this.value = value;
        this.GPSSpeed = speed;
        this.GPSBreaing = bearing;
        this.serverNum = serverNum;

    }

    public void setUILogger(UiLogger ui) {
        uiLogger = ui;
    }

    public void setMapFragment(MapFragment map) {
        mapFragment = map;
    }

    @Override
    protected String doInBackground(String... strings) {


        String devicename= serverNum;
        if(serverNum=="")
        {
            int i=0;
        }
        //String devicename= ((MainActivity)MainActivity.context).DeviceName;
        //String Urlstring = "http://theprost8004.iptime.org:50080/ObservablesSmartMulti"+"/"+devicename;
        String Urlstring = "http://192.168.0.6:5000/ObservablesSmartMulti"+"/"+devicename;


        String state = Environment.getExternalStorageState(); //상태
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            baseDirectory = new File(Environment.getExternalStorageDirectory(), "gnss_log/Debug");
            if (!baseDirectory.exists()) {
                //없으면 만듭니다
                baseDirectory.mkdirs(); //파일을 생성합니다.
            }
        }

        String sendData = "sendData.txt";
        File sendDatafile = new File(baseDirectory, sendData);
        BufferedWriter sendDataWr;
        try {
            sendDataWr = new BufferedWriter(new FileWriter(sendDatafile, true));
            sendDataWr.write(Urlstring);
            sendDataWr.newLine();
            sendDataWr.newLine();
            sendDataWr.flush();
            sendDataWr.close();
        } catch (IOException e) {

        }

        //경로값이 바뀌었을 경우 바뀐값을 저장해줍니다
        if (measurementUrl != null) {

            String filePath = "file_Settings.txt";
            File currentFile = new File(baseDirectory, filePath);
            BufferedWriter currentFileWriter;
            try {
                currentFileWriter = new BufferedWriter(new FileWriter(currentFile));
                currentFileWriter.write(measurementUrl);
                currentFileWriter.flush();
                currentFileWriter.close();

            } catch (IOException e) {

            }
          //  Urlstring = "http://" + measurementUrl + "/ObservablesSmartMulti/" + "Raw," + url;
            Urlstring = "http://" + measurementUrl + "/ObservablesSmartMulti"+"/"+devicename;;
        }


        try {
            URL obj = new URL(Urlstring);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Java client");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json");

            //서버에 데이터를 보내고 결과값을 전달받습니다
            url = "\"" + url + "\"";
            byte[] outputInBytes = url.getBytes("UTF-8");
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(outputInBytes);

            Log.i("보내는 데이터",url);


            wr.flush();
            wr.close();

            int retCode = conn.getResponseCode();

            if (retCode != HttpURLConnection.HTTP_OK) {
                return "연결에 실패했습니다";
            }


            //서버와 통신했던 결과물을 받게됩니다
            InputStream is = conn.getInputStream(); //input스트림 개방

            StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8")); //문자열 셋 세팅
            String line ;

            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }

            String result = builder.toString();
            return result; //통신결과물을 return 합니다 onPostExecute로 값이 넘어갑니다

        } catch (Exception e) {
            return e+"서버접속에 실패했습니다";
            // e.printStackTrace();
        }
    }

    //doInBackground에서 return 된값이 매개변수로 넘어옵니다.
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        LoggerFragment.UIFragmentComponent component = uiLogger.getUiFragmentComponent();

        if (result == null || result == "") {
            component.logTextFragment("Result (PNT API):", "서버통신의 결과물이 비어있습니다", Color.parseColor("#20B2AA"));
        }
        else{
            if(result=="연결에 실패했습니다"){
                component.logTextFragment("Result (PNT API):", "연결에 실패했습니다 주소값을 확인해주세요", Color.parseColor("#20B2AA"));
            }

        }


        if (component != null) {
            component.logTextFragment("Result (PNT API):", result, Color.parseColor("#ff0000"));


            if (result.contains("NULL") == false) {

                String filePath = "Debug.txt";
                File currentFile = new File(baseDirectory, filePath);
                BufferedWriter currentFileWriter;
                try {
                    currentFileWriter = new BufferedWriter(new FileWriter(currentFile));
                    currentFileWriter.write("결과값 : " + result);
                    currentFileWriter.newLine();
                    currentFileWriter.write("================================================================");
                    currentFileWriter.flush();
                    currentFileWriter.close();

                } catch (IOException e) {

                }

                //String test = "$GNGGA,175025.92,-36-23.320959,N,-123-01.752922,W,1,05,2.8,246.349,M,161.760,M,,*75";

                String[] data = result.split(",");

                if (data.length == 1) {
                    component.logTextFragment("Result (PNT API):", result, Color.parseColor("#20B2AA"));
                    return;
                }

                int min = Integer.parseInt(data[2].substring(0, 2));
                //마이너스가 붙은경우.
                float Lat1 = 0;
                float Lat2;
                try {
                    if (min < 0) {
                        Lat1 = Float.parseFloat(data[2].substring(0, 3));
                        Lat2 = Float.parseFloat(data[2].substring(3));
                    } else {
                        Lat1 = Float.parseFloat(data[2].substring(0, 2));
                        Lat2 = Float.parseFloat(data[2].substring(2));
                    }
                    float Lat = Lat1 + Lat2 / 60;


                    int min2 = Integer.parseInt(data[4].substring(0, 2));
                    float Lng1;
                    float Lng2;
                    if (min2 < 0) {
                        Lng1 = Float.parseFloat(data[4].substring(0, 4));
                        Lng2 = Float.parseFloat(data[4].substring(4));
                    } else {
                        Lng1 = Float.parseFloat(data[4].substring(0, 3));
                        Lng2 = Float.parseFloat(data[4].substring(3));
                    }

                    float Lng = Lng1 + Lng2 / 60;
                    LatLng latLng = new LatLng(Lat, Lng);

                    mapFragment.AddPoint(latLng);
                    //mapFragment.AddPoint(latLng,GPSSpeed,GPSBreaing);
                } catch (Exception e) {

                    return;
                }
            }
        }
    }


    @Override
    protected void onCancelled() {
        // mapFragment.MapRemoveAll();

        super.onCancelled();
    }
}

