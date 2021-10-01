package com.google.android.apps.location.gps.gnsslogger;

import android.content.ContentValues;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Environment;

//import com.github.petr_s.nmea.GpsSatellite;
//import com.github.petr_s.nmea.NMEAHandler;
//import com.github.petr_s.nmea.NMEAParser;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class SendServerTask extends AsyncTask<String, Void, String> {

    private String measurementUrl;
    private String url;
    private ContentValues value;
    private UiLogger uiLogger = null;
    private MapFragment mapFragment = null;

    public SendServerTask() {


    }

    public SendServerTask(String measureUrl, String Dataurl, ContentValues value) {
        this.measurementUrl = measureUrl;
        this.url = Dataurl;
        this.value = value;

    }

    public void setUILogger(UiLogger ui) {
        uiLogger = ui;
    }

    public void setMapFragment(MapFragment map) {
        mapFragment = map;
    }

    @Override
    protected String doInBackground(String... strings) {

        String Urlstring = "http://theprost8004.iptime.org:50080/ObservablesSmart/" + "Raw," + url;

        if (measurementUrl != null) {


            File baseDirectory = null;
            String state = Environment.getExternalStorageState(); //상태
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                baseDirectory = new File(Environment.getExternalStorageDirectory(), "gnss_log");
                if(!baseDirectory.exists()){
                    //없으면 만듭니다
                    baseDirectory.mkdirs(); //파일을 생성합니다.
                }
            }

            String filePath ="file_Settings.txt";
            File currentFile = new File(baseDirectory, filePath);
            String currentFilePath = currentFile.getAbsolutePath();
                BufferedWriter currentFileWriter;
                try {
                    currentFileWriter = new BufferedWriter(new FileWriter(currentFile));
                    currentFileWriter.write(measurementUrl);
                    currentFileWriter.flush();
                    currentFileWriter.close();

                } catch (IOException e) {

                }

            Urlstring = "http://"+measurementUrl+"/ObservablesSmart/" + "Raw," + url;
        }


        URL url = null;
        try {

            url = new URL(Urlstring);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String result = "";
            String temp = "";

            while ((temp = in.readLine()) != null) {
                result += temp;

            }

            conn.disconnect();
            in.close();
            return result;

        } catch (MalformedURLException e) {
            //   System.out.println("================데이터 진행 불가==========");
            e.printStackTrace();
        } catch (IOException e) {
            // System.out.println("=================데이터 진행 불가=========");
            e.printStackTrace();
        }
        return null;
    }

    //화면에 표출합니다 그림을
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        LoggerFragment.UIFragmentComponent component = uiLogger.getUiFragmentComponent();
        if (component != null) {
            component.logTextFragment("Result (PNT API):", result, Color.parseColor("#ff0000"));


            if (result.compareTo("$NULL*1B") != 0) {
                //String test = "$GNGGA,175025.92,-36-23.320959,N,-123-01.752922,W,1,05,2.8,246.349,M,161.760,M,,*75";
                String[] data = result.split(",");

                int min = Integer.parseInt(data[2].substring(0, 2));
                //마이너스가 붙은경우.
                float Lat1 = 0;
                float Lat2;
                try{
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
                }
                catch(Exception e){

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

