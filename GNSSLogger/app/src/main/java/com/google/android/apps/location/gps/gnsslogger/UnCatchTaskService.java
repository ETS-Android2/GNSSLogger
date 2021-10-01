package com.google.android.apps.location.gps.gnsslogger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class UnCatchTaskService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //프로그램이 강제로 종료되었을경우 발생하는 이벤트
    @Override
    public void onTaskRemoved(Intent intent){

        Toast.makeText(this,"강제종료되었습니다",Toast.LENGTH_SHORT).show();
    }

}
