package com.google.android.apps.location.gps.gnsslogger;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendServerTask extends AsyncTask<String, Void, String> {

    private String measurementUrl;
    private String url;
    private ContentValues value;
    private UiLogger uiLogger= null;

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
                int i=0;
            }
            //component.logTextFragment(tag, text, color);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}

