package com.google.android.apps.location.gps.gnsslogger;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Debug;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendServerTask extends AsyncTask<String, Void, String> {

    private String measurementUrl;
    private String url;
    private ContentValues value;

    public SendServerTask(){


    }

    public SendServerTask(String measureUrl, String Dataurl, ContentValues value) {
        this.measurementUrl = measureUrl;
        this.url = Dataurl;
        this.value = value;

    }

    @Override
    protected String doInBackground(String... strings) {

        String Urlstring = "http://theprost8004.iptime.org:50080/ObservablesSmart/" + "Raw,"+url;
        if(measurementUrl!="")
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

        } catch (MalformedURLException e) {
         //   System.out.println("================데이터 진행 불가==========");
            e.printStackTrace();
        } catch (IOException e) {
           // System.out.println("=================데이터 진행 불가=========");
            e.printStackTrace();
        }


        return null;


    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //System.out.println("결과값" + result);

        //result 값을 파싱하여 원하는 작업을 한다
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}

