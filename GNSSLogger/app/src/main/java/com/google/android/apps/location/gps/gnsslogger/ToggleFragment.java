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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;

import org.achartengine.ChartFactory;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import static android.content.Context.SENSOR_SERVICE;

public class ToggleFragment  extends Fragment implements SensorEventListener
{
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

    double total;


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

    View rootView;
    //GPS 정보

    LocationManager LocMan;
    String provider;

    Location location1;

    @SuppressLint("ValidFragment")
    public ToggleFragment(Context baseContext)
    {
       context=baseContext;
    }

  /*  public void ToggleFragment(Context _context)
    {
        context=_context;
    }*/


    @Override //이걸
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {

         rootView = inflater.inflate(R.layout.fragment_togglesenser, container, false);

        //gps값
         LocMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        provider = LocMan.getBestProvider(new Criteria(), true);    // 최고의 GPS 찾기
        //Accelermeter
        txtAccelerometerX = (TextView)rootView.findViewById(R.id.AccelerometerX);
        txtAccelerometerY = (TextView)rootView. findViewById(R.id.AccelerometerY);
        txtAccelerometerZ = (TextView)rootView. findViewById(R.id.AccelerometerZ);
        //Geavity
        txtGravityX = (TextView)rootView. findViewById(R.id.GravityX);
        txtGravityY = (TextView) rootView.findViewById(R.id.GravityY);
        txtGravityZ = (TextView) rootView.findViewById(R.id.GravityZ);
        //자이로스코프
        txtGyroscopeX = (TextView) rootView.findViewById(R.id.GyroscopeX);
        txtGyroscopeY = (TextView)rootView. findViewById(R.id.GyroscopeY);
        txtGyroscopeZ = (TextView)rootView. findViewById(R.id.GyroscopeZ);
        txtMagneticX = (TextView) rootView.findViewById(R.id.Magnetic_FieldX);
        txtMagneticY = (TextView)rootView. findViewById(R.id.Magnetic_FieldY);
        txtMagneticZ = (TextView)rootView. findViewById(R.id.Magnetic_FieldZ);
        //GPS값
        txtGPSPositionX = (TextView)rootView. findViewById(R.id.GPS_PositionX);
        txtGPSPositionY = (TextView)rootView. findViewById(R.id.GPS_PositionY);
        txtGPSPositionZ = (TextView) rootView.findViewById(R.id.GPS_PositionZ);
        txtOrientationX = (TextView)rootView. findViewById(R.id.OrientationX);
        txtOrientationY = (TextView)rootView. findViewById(R.id.OrientationY);
        txtOrientationZ = (TextView) rootView.findViewById(R.id.OrientationZ);
        txtLinear_accelX = (TextView)rootView. findViewById(R.id.Linear_AccelX);
        txtLinear_accelY = (TextView) rootView.findViewById(R.id.Linear_AccelY);
        txtLinear_accelZ = (TextView)rootView. findViewById(R.id.Linear_AccelZ);
        txtRotationVectorX = (TextView)rootView.findViewById(R.id.Rotation_VectX);
        txtRotationVectorY = (TextView) rootView.findViewById(R.id.Rotation_VectY);
        txtRotationVectorZ = (TextView) rootView.findViewById(R.id.Rotation_VectZ);
        txtPressure = (TextView)rootView. findViewById(R.id.PressureValue);


        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE); //센서 매니저 생성

        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 가속력 (중력을 포함한 가속력)
        mMagnetomter = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); //자기력?
        mGravitymeter = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY); //중력
        mGyroscopemter = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //자이로스코프
        mLinearAccelmeter = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotationVectmeter = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mPerssuremeter = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mBatteryTempmeter = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        return rootView;
    }


    @Override
    public void onResume()
    {
        super.onResume();
       sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mMagnetomter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mGravitymeter, SensorManager.SENSOR_DELAY_UI); //중력
        sensorManager.registerListener(this, mGyroscopemter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mLinearAccelmeter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mRotationVectmeter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mPerssuremeter, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, mBatteryTempmeter, SensorManager.SENSOR_DELAY_UI);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            txtGPSPositionX.setText("Pending");
            txtGPSPositionY.setText("Pending");
            txtGPSPositionZ.setText("Pending");
            return;
        }
        LocMan.requestLocationUpdates(provider, 1000, 0, gpsListener); // 리스너 등록(위치 업데이트마다 gpsListener 호출), 매개변수 : GPS또는 네트워크, 업데이트 주기, 업데이트 거리, 리스너
    }

    @Override
    public void onDestroy()
    {
       // mMapView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onPause()
    {
        //센서를 종료합니다.
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onSensorChanged(SensorEvent event)
    {


        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            mGravity=event.values;

            txtAccelerometerX.setText(String.format("%.3f", event.values[0]));
            txtAccelerometerY.setText(String.format("%.3f",  event.values[1]));
            txtAccelerometerZ.setText(String.format("%.3f",  event.values[2]));
        }

        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
        {
            mGeomagnetic=event.values;
            txtMagneticX.setText(String.format("%.3f",event.values[0]));
            txtMagneticY.setText(String.format("%.3f",event.values[1]));
            txtMagneticZ.setText(String.format("%.3f",event.values[2]));
        }

        if (mGravity!=null&&mGeomagnetic!=null) //Orientation 값 설정
        {
            float[] R = new float[9];
            float[] I = new float[9];

            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            //소수점 셋째자리 이후 반올림

            if (success) //여기가 false 가 납니다.
            {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                float azimut, pitch, roll;

                azimut = orientation[0];
                pitch = orientation[1];
                roll = orientation[2];

                txtOrientationX.setText(String.format("%.3f", azimut));
                txtOrientationY.setText(String.format("%.3f", pitch));
                txtOrientationZ.setText(String.format("%.3f", roll));

            }

        }


        if(event.sensor.getType()==Sensor.TYPE_GRAVITY)
        {
            // total=Math.sqrt(Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2));
            txtGravityX.setText(String.format("%.3f",event.values[0]));
            txtGravityY.setText(String.format("%.3f",event.values[1]));
            txtGravityZ.setText(String.format("%.3f",event.values[2]));
        }

        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {
            txtGyroscopeX.setText(String.format("%.3f",event.values[0]));
            txtGyroscopeY.setText(String.format("%.3f",event.values[1]));
            txtGyroscopeZ.setText(String.format("%.3f",event.values[2]));
        }

        if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION)
        {
            txtLinear_accelX.setText(String.format("%.3f",event.values[0]));
            txtLinear_accelY.setText(String.format("%.3f",event.values[1]));
            txtLinear_accelZ.setText(String.format("%.3f",event.values[2]));
        }

        if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR)
        {
            txtRotationVectorX.setText(String.format("%.3f",event.values[0]));
            txtRotationVectorY.setText(String.format("%.3f",event.values[1]));
            txtRotationVectorZ.setText(String.format("%.3f",event.values[2]));
        }

        if(event.sensor.getType()==Sensor.TYPE_PRESSURE)
        {
            txtPressure.setText(String.format("%.3f",event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }

    LocationListener gpsListener = new LocationListener()   // GPS가 정보가 변경 될때 호출 되는 리스너
    {
        @SuppressLint("DefaultLocale")
        @Override
        public void onLocationChanged(Location location)    // 위에서 설정한 위치 변경시 호출
        {
            txtGPSPositionX.setText(String.format("%.3f", location.getLongitude()));
            txtGPSPositionY.setText(String.format("%.3f", location.getLatitude()));
            txtGPSPositionZ.setText(String.format("%.3f", location.getAltitude()));
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
}
