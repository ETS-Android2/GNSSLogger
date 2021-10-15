package com.google.android.apps.location.gps.gnsslogger;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Output;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.SENSOR_SERVICE;

@SuppressLint("ValidFragment")
public class ToggleFragment extends Fragment  implements SensorEventListener, View.OnClickListener {
    SensorManager sensorManager;
    Sensor mAccelerometer;//가속도 센스
    Sensor mMagnetomter; //자력계 센스
    Sensor mGravitymeter; //중력센스
    Sensor mGyroscopemter; //자이로 스코프
    Sensor mLinearAccelmeter;
    Sensor mRotationVectmeter;
    Sensor mPerssuremeter;
    Sensor mBatteryTempmeter;

    Intent batteryStatus;

    float[] mGravity = null;
    float[] mGeomagnetic = null;

    // double total;

    boolean textSaveStart = false;


    private TextView txtAccelerometerX, txtAccelerometerY, txtAccelerometerZ;
    private TextView txtGravityX, txtGravityY, txtGravityZ;
    private TextView txtGyroscopeX, txtGyroscopeY, txtGyroscopeZ;
    private TextView txtMagneticX, txtMagneticY, txtMagneticZ;
    private TextView txtLinear_accelX, txtLinear_accelY, txtLinear_accelZ;
    private TextView txtRotationVectorX, txtRotationVectorY, txtRotationVectorZ;
    private TextView txtPressure;
    private TextView txtBatteryTemp;
    private TextView txtGPSPositionX, txtGPSPositionY, txtGPSPositionZ;
    private TextView txtOrientationX, txtOrientationY, txtOrientationZ;

    public Context context;

    private Button btStart, btStop;

    FileOutputStream fos = null;
    BufferedWriter writer = null;


    //IMU Log용
    float Accelerometer_x, Accelerometer_y, Accelerometer_z;
    float Gyroscope_x, Gyroscope_y, Gyroscope_z;
    float Magnetic_Field_x, Magnetic_Field_y, Magnetic_Field_z;
    float Orientation_x, Orientation_y, Orientation_z;
    float Linear_Accel_x, Linear_Accel_y, Linear_Accel_z;
    float Gravity_x, Gravity_y, Gravity_z;
    float Rotation_Vect_x, Rotation_Vect_y, Rotation_Vect_z;
    double GPS_Position_x=0.0f, GPS_Position_y=0.0f, GPS_Position_z=0.0f;
    float Pressure;

    View rootView;
    //GPS 정보
    LocationManager LocMan;
    String provider;
    Location location1;
    LogThread logth =null;

    public static boolean  startbt= false;



    @SuppressLint("ValidFragment")
    public ToggleFragment(Context baseContext) {
        context = baseContext;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_togglesenser, container, false);

        //gps값
        LocMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        provider = LocMan.getBestProvider(new Criteria(), true);    // 최고의 GPS 찾기

        //Accelermeter
        txtAccelerometerX = (TextView) rootView.findViewById(R.id.AccelerometerX);
        txtAccelerometerY = (TextView) rootView.findViewById(R.id.AccelerometerY);
        txtAccelerometerZ = (TextView) rootView.findViewById(R.id.AccelerometerZ);

        //Geavity
        txtGravityX = (TextView) rootView.findViewById(R.id.GravityX);
        txtGravityY = (TextView) rootView.findViewById(R.id.GravityY);
        txtGravityZ = (TextView) rootView.findViewById(R.id.GravityZ);

        //자이로스코프
        txtGyroscopeX = (TextView) rootView.findViewById(R.id.GyroscopeX);
        txtGyroscopeY = (TextView) rootView.findViewById(R.id.GyroscopeY);
        txtGyroscopeZ = (TextView) rootView.findViewById(R.id.GyroscopeZ);
        txtMagneticX = (TextView) rootView.findViewById(R.id.Magnetic_FieldX);
        txtMagneticY = (TextView) rootView.findViewById(R.id.Magnetic_FieldY);
        txtMagneticZ = (TextView) rootView.findViewById(R.id.Magnetic_FieldZ);

        //GPS값
        txtGPSPositionX = (TextView) rootView.findViewById(R.id.GPS_PositionX);
        txtGPSPositionY = (TextView) rootView.findViewById(R.id.GPS_PositionY);
        txtGPSPositionZ = (TextView) rootView.findViewById(R.id.GPS_PositionZ);

        txtOrientationX = (TextView) rootView.findViewById(R.id.OrientationX);
        txtOrientationY = (TextView) rootView.findViewById(R.id.OrientationY);
        txtOrientationZ = (TextView) rootView.findViewById(R.id.OrientationZ);
        txtLinear_accelX = (TextView) rootView.findViewById(R.id.Linear_AccelX);
        txtLinear_accelY = (TextView) rootView.findViewById(R.id.Linear_AccelY);
        txtLinear_accelZ = (TextView) rootView.findViewById(R.id.Linear_AccelZ);
        txtRotationVectorX = (TextView) rootView.findViewById(R.id.Rotation_VectX);
        txtRotationVectorY = (TextView) rootView.findViewById(R.id.Rotation_VectY);
        txtRotationVectorZ = (TextView) rootView.findViewById(R.id.Rotation_VectZ);
        txtPressure = (TextView) rootView.findViewById(R.id.PressureValue);

        btStart = (Button) rootView.findViewById(R.id.logger_start);
        btStart.setOnClickListener(this);

        btStop = (Button) rootView.findViewById(R.id.logger_stop);
       // btStop.setEnabled(false);
        btStop.setOnClickListener(this);

        //메세지를 저장중이라면
        if(startbt){
            btStart.setEnabled(false);
            btStop.setEnabled(true);
        }
        else{
            btStart.setEnabled(true);
            btStop.setEnabled(false);
        }



        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE); //센서 매니저 생성

        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 가속력 (중력을 포함한 가속력)
        mMagnetomter = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //자기력?
        mGravitymeter = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); //중력
        mGyroscopemter = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //자이로스코프
        mLinearAccelmeter = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotationVectmeter = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mPerssuremeter = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mBatteryTempmeter = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        logth   = new LogThread();



        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mMagnetomter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mGravitymeter, SensorManager.SENSOR_DELAY_UI); //중력
        sensorManager.registerListener(this, mGyroscopemter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mLinearAccelmeter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mRotationVectmeter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mPerssuremeter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mBatteryTempmeter, SensorManager.SENSOR_DELAY_UI);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            txtGPSPositionX.setText("Pending");
            txtGPSPositionY.setText("Pending");
            txtGPSPositionZ.setText("Pending");
            return;
        }
        LocMan.requestLocationUpdates(provider, 1000, 0, gpsListener); // 리스너 등록(위치 업데이트마다 gpsListener 호출), 매개변수 : GPS또는 네트워크, 업데이트 주기, 업데이트 거리, 리스너
    }

    @Override
    public void onDestroy() {
        // mMapView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onPause() {
        //센서를 종료합니다.
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    //변수를 지정해서 표로 출력하면됩니다
    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event) {


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;

            Accelerometer_x = event.values[0];
            Accelerometer_y = event.values[1];
            Accelerometer_z = event.values[2];

            txtAccelerometerX.setText(String.format("%.3f", Accelerometer_x));
            txtAccelerometerY.setText(String.format("%.3f", Accelerometer_y));
            txtAccelerometerZ.setText(String.format("%.3f", Accelerometer_z));
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;

            Magnetic_Field_x = event.values[0];
            Magnetic_Field_y = event.values[1];
            Magnetic_Field_z = event.values[2];

            txtMagneticX.setText(String.format("%.3f", Magnetic_Field_x));
            txtMagneticY.setText(String.format("%.3f", Magnetic_Field_y));
            txtMagneticZ.setText(String.format("%.3f", Magnetic_Field_z));
        }

        if (mGravity != null && mGeomagnetic != null) //Orientation 값 설정
        {
            float[] R = new float[9];
            float[] I = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            //소수점 셋째자리 이후 반올림

            if (success) //여기가 false 가 납니다.
            {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                Orientation_x = orientation[0];
                Orientation_y = orientation[1];
                Orientation_z = orientation[2];

                txtOrientationX.setText(String.format("%.3f", Orientation_x));
                txtOrientationY.setText(String.format("%.3f", Orientation_y));
                txtOrientationZ.setText(String.format("%.3f", Orientation_z));

            }

        }


        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {

            Gravity_x = event.values[0];
            Gravity_y = event.values[1];
            Gravity_z = event.values[2];

            // total=Math.sqrt(Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2));
            txtGravityX.setText(String.format("%.3f", Gravity_x));
            txtGravityY.setText(String.format("%.3f", Gravity_y));
            txtGravityZ.setText(String.format("%.3f", Gravity_z));
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            Gyroscope_x = event.values[0];
            Gyroscope_y = event.values[1];
            Gyroscope_z = event.values[2];

            txtGyroscopeX.setText(String.format("%.3f", Gyroscope_x));
            txtGyroscopeY.setText(String.format("%.3f", Gyroscope_y));
            txtGyroscopeZ.setText(String.format("%.3f", Gyroscope_z));
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            Linear_Accel_x = event.values[0];
            Linear_Accel_y = event.values[1];
            Linear_Accel_z = event.values[2];

            txtLinear_accelX.setText(String.format("%.3f", Linear_Accel_x));
            txtLinear_accelY.setText(String.format("%.3f", Linear_Accel_y));
            txtLinear_accelZ.setText(String.format("%.3f", Linear_Accel_z));
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            Rotation_Vect_x = event.values[0];
            Rotation_Vect_y = event.values[1];
            Rotation_Vect_z = event.values[2];

            txtRotationVectorX.setText(String.format("%.3f", Rotation_Vect_x));
            txtRotationVectorY.setText(String.format("%.3f", Rotation_Vect_y));
            txtRotationVectorZ.setText(String.format("%.3f", Rotation_Vect_z));
        }

        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            Pressure = event.values[0];
            txtPressure.setText(String.format("%.3f", Pressure));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    LocationListener gpsListener = new LocationListener()   // GPS가 정보가 변경 될때 호출 되는 리스너
    {
        @SuppressLint("DefaultLocale")
        @Override
        public void onLocationChanged(Location location)    // 위에서 설정한 위치 변경시 호출
        {
            GPS_Position_x = location.getLongitude();
            GPS_Position_y = location.getLatitude();
            GPS_Position_z = location.getAltitude();

            String dfsf= Double.toString( GPS_Position_x);



            txtGPSPositionX.setText(String.format("%.3f", GPS_Position_x));
            txtGPSPositionY.setText(String.format("%.3f", GPS_Position_y));
            txtGPSPositionZ.setText(String.format("%.3f", GPS_Position_z));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) // 상태 변경시 호출
        {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(context.getApplicationContext(), "범위 벗어남", Toast.LENGTH_LONG).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(context.getApplicationContext(), "일시적 불능", Toast.LENGTH_LONG).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Toast.makeText(context.getApplicationContext(), "사용 가능", Toast.LENGTH_LONG).show();
                    break;
            }
            //Log.i("sinwho", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Log.i("sinwho", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.logger_start:
                //버튼 활성화, 비활성화

                Toast.makeText(getContext(), R.string.start_message, Toast.LENGTH_LONG).show(); //화면에 글자
              //  String state = Environment.getExternalStorageState(); //상태
                String folderName = "gnss_log";
                File dir = new File(Environment.getExternalStorageDirectory(), folderName);
                if (!dir.exists()) {
                    dir.mkdir();
                    File log = new File(Environment.getExternalStorageDirectory() + "/" + folderName, "IMU");
                    log.mkdir();
                } else { //이미있는경우
                    File log = new File(Environment.getExternalStorageDirectory() + "/" + folderName, "IMU");
                    if (!log.exists()) {
                        log.mkdir(); //없다면 생성해야합니다
                    }
                }

                String fullFilePath = Environment.getExternalStorageDirectory() + "/" + folderName + "/IMU";
                SimpleDateFormat formatter = new SimpleDateFormat("yyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                String fileName = String.format("%s_%s.txt", "IMU_", formatter.format(now));

                Object dfsf = fullFilePath + "/" + fileName;
                try {
                    fos = new FileOutputStream(fullFilePath + "/" + fileName, true);
                    writer = new BufferedWriter(new OutputStreamWriter(fos));

                    //title에 들어갈내용
                    String context ="Timestamp,"+"Accelerometer.x,Accelerometer.y,Accelerometer.z," +
                            "Gyroscope.x,Gyroscope.y,Gyroscope.z," +
                            "Magnetic_Field.x,Magnetic_Field.y,Magnetic_Field.z," +
                            "GPS_Position.x,GPS_Position.y,GPS_Position.z," +
                            "Orientation.x,Orientation.y,Orientation.z," +
                            "Linear_Accel.x,Linear_Accel.y,Linear_Accel.z," +
                            "Gravity.x,Gravity.y,Gravity.z," +
                            "Rotation_Vect.x,Rotation_Vect.y,Rotation_Vect.z," +
                            "Pressure"+"\n";

                    writer.write(context); //들어갈 내용
                    writer.flush();

                    startbt = true;
                    btStart.setEnabled(false);
                    btStop.setEnabled(true);
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                logth.start();
                break;

            case R.id.logger_stop:
                //view.
                try {

                    if(writer==null||fos==null)
                    {
                        return;
                    }

                    Toast.makeText(getContext(), "save file", Toast.LENGTH_LONG).show(); //화면에 글자 표출
                    logth.interrupt();
                    writer.close();
                    fos.close();

                    startbt = false;
                    btStart.setEnabled(true);
                    btStop.setEnabled(false);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public class LogThread extends  Thread{

        boolean loop= true;

        public void run(){
            try {

                while(loop){
                    Thread.sleep(10); //쉽니다

                    long timeMillin = System.currentTimeMillis();
                    Date date = new Date(timeMillin);

                    String originalString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS").format(date);
                    Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS").parse(originalString);
                    String newString = new SimpleDateFormat("HH:mm:ss.SS").format(time); // 9:00

//                    String context ="Timestamp,"+"Accelerometer.x,Accelerometer.y,Accelerometer.z," +
//                            "Gyroscope.x,Gyroscope.y,Gyroscope.z," +
//                            "Magnetic_Field.x,Magnetic_Field.y,Magnetic_Field.z," +
//                            "GPS_Position.x,GPS_Position.y,GPS_Position.z," +
//                            "Orientation.x,Orientation.y,Orientation.z," +
//                            "Linear_Accel.x,Linear_Accel.y,Linear_Accel.z," +
//                            "Gravity.x,Gravity.y,Gravity.z," +
//                            "Rotation_Vect.x,Rotation_Vect.y,Rotation_Vect.z," +
//                            "Pressure"+"\n";

                    //IMU Log용
                    String context= newString+","+ Float.toString(Accelerometer_x)+","+Float.toString(Accelerometer_y)+","+ Float.toString(Accelerometer_z)+","+
                            Float.toString(Gyroscope_x)+","+Float.toString( Gyroscope_y)+","+Float.toString( Gyroscope_z)+","+
                            Float.toString(Magnetic_Field_x)+","+ Float.toString(Magnetic_Field_y)+","+Float.toString( Magnetic_Field_z)+","+
                            Double.toString(GPS_Position_x)+","+Double.toString( GPS_Position_y)+","+ Double.toString(GPS_Position_z)+","+
                            Float.toString( Orientation_x)+","+ Float.toString(Orientation_y)+","+Float.toString( Orientation_z)+","+
                            Float.toString( Linear_Accel_x)+","+Float.toString( Linear_Accel_y)+","+Float.toString( Linear_Accel_z)+","+
                            Float.toString(Gravity_x)+","+Float.toString( Gravity_y)+","+Float.toString( Gravity_z)+","+
                            Float.toString(Rotation_Vect_x)+","+Float.toString( Rotation_Vect_y)+","+ Float.toString(Rotation_Vect_z)+","+
                            Float.toString(Pressure)+"\n";




                    writer.write(context); //들어갈 내용
                    writer.flush();

                    if(Thread.interrupted())
                    {
                        loop= false;
                        break;
                    }
                }
            } catch (InterruptedException | IOException | ParseException e) {
                e.printStackTrace();
                loop= false;
            }
        }
    }

}
