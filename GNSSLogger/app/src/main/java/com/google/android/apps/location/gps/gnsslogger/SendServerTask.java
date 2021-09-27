package com.google.android.apps.location.gps.gnsslogger;

import android.content.ContentValues;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;

//import com.github.petr_s.nmea.GpsSatellite;
//import com.github.petr_s.nmea.NMEAHandler;
//import com.github.petr_s.nmea.NMEAParser;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
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
    private UiLogger uiLogger= null;
    private MapFragment mapFragment = null;

    public SendServerTask(){


    }

    public SendServerTask(String measureUrl, String Dataurl, ContentValues value) {
        this.measurementUrl = measureUrl;
        this.url = Dataurl;
        this.value = value;

    }

    public void setUILogger(UiLogger ui){
        uiLogger =ui;
    }

    public void setMapFragment(MapFragment map){
        mapFragment = map;
    }

    @Override
    protected String doInBackground(String... strings) {

        String Urlstring = "http://theprost8004.iptime.org:50080/ObservablesSmart/" + "Raw,"+url;
        if(measurementUrl!=null)
        {
            Urlstring = measurementUrl+ "Raw,"+url;
        }

        //System.out.println("결과값" + url);
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

        LoggerFragment.UIFragmentComponent component =  uiLogger.getUiFragmentComponent();
        if (component != null) {

            component.logTextFragment("Result (PNT API):",result, Color.parseColor("#ff0000"));


            if(result.compareTo("$NULL*1B")!=0)
            {
              // String test = "$GNGGA,054513.45,3622.432732,N,12719.019698,E,1,07,2.0,118.947,M,34.358,M,,*5A";
                String[] data = result.split(",");
               // String[] data = result.split(",");

                float Lat1 =Float.parseFloat(data[2].substring(0,2));
                float Lat2 =Float.parseFloat(data[2].substring(2));
                float Lat= Lat1 + Lat2/60;

                float Lng1 = Float.parseFloat(data[4].substring(0,2));
                float Lng2 = Float.parseFloat(data[4].substring(2));
                float Lng  = Lng1+ Lng2/60;

                LatLng latLng = new LatLng(Lat,Lng);

                mapFragment.AddPoint(latLng);
            }
        }
    }


    @Override
    protected void onCancelled() {
       // mapFragment.MapRemoveAll();

        super.onCancelled();
    }
}

