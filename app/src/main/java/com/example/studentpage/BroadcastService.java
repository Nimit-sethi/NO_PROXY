package com.example.studentpage;

//package com.example.backgoundtimercount;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BroadcastService extends Service {
    private String TAG = "BroadcastService";
    public static final String COUNTDOWN_BR = "com.example.backgoundtimercount";
    Intent intent = new Intent(COUNTDOWN_BR);
    CountDownTimer countDownTimer = null;
    boolean mTimer;
    FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        db= FirebaseFirestore.getInstance();
        Log.i(TAG,"Starting timer...");
        sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);
        long millis = sharedPreferences.getLong("time",3000);
        if (millis / 1000 == 0) {
            millis = 300000;
        }
        countDownTimer = new CountDownTimer(300000,1000) {

            @Override

            public void onTick(long millisUntilFinished) {
                Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
                intent.putExtra("countdown",millisUntilFinished);
                sendBroadcast(intent);
            }

            @Override
            public void onFinish() {
                mTimer = false;
                DocumentReference constant= db.collection("constants").document("constant");
                constant.update("timer",false).addOnSuccessListener(unused -> Log.d(TAG,"DocumentSnapshot successfully updted")).addOnFailureListener(e -> Log.w(TAG, "Error updating document",e));

            }
        };
        countDownTimer.start();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
//        countDownTimer.start();
        countDownTimer.cancel();

    }


    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        super.unregisterReceiver(receiver);


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        DocumentReference constant= db.collection("constants").document("constant");
        constant.update("timer",false).addOnSuccessListener(unused -> Log.d(TAG,"DocumentSnapshot successfully updted")).addOnFailureListener(e -> Log.w(TAG, "Error updating document",e));

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
}
