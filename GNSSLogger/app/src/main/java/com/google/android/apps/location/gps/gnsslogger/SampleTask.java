package com.google.android.apps.location.gps.gnsslogger;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

//public class SampleTask extends AsyncTask<Void, Void, String> {
public class SampleTask extends AsyncTask<String, Void, String> {
    protected String doInBackground(String...params) {

        String result = null;

        try {
            // Open the connection
            URL url = new URL("http://theprost8004.iptime.org:61002/connect_mongodb");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            JSONObject jsonObject = new JSONObject();

            String devicename= ((MainActivity)MainActivity.context).DeviceName;
            jsonObject.accumulate("deviceid",devicename);
            jsonObject.accumulate("type",params[0]);
            jsonObject.accumulate("log", params[1]);

            SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time =new Date();
            String timeString=format.format(time);

            jsonObject.accumulate("time",timeString);

            conn.setRequestProperty("Content-type", "application/json");
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            conn.setDoInput(true);


         OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "EUC-KR");
         PrintWriter writer = new PrintWriter(outStream);
         writer.write(jsonObject.toString());
          writer.flush();

            int a=  conn.getResponseCode();
            InputStream is = conn.getInputStream();
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }

            // Set the result
            result = builder.toString();
        }
        catch (Exception e) {
            // Error calling the rest api
            //Log.e("REST_API", "GET method failed: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }


    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        //result 값을 파싱하여 원하는 작업을 한다
    }
}
