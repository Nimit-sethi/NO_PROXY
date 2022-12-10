package com.example.studentpage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class timer_broadcast extends AppCompatActivity {

    String TAG = "Main";
    TextView txt;
    Button start;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_broadcast);
        db=FirebaseFirestore.getInstance();
        txt = findViewById(R.id.txt);
        start=findViewById(R.id.button_start_pause);
        getSupportActionBar().hide();

        start.setOnClickListener(v -> {
            registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));

            Intent intent = new Intent(timer_broadcast.this,BroadcastService.class);
            startService(intent);
            DocumentReference constant= db.collection("constants").document("constant");
            constant.update("timer",true).addOnSuccessListener(unused -> Log.d(TAG,"DocumentSnapshot successfully updted")).addOnFailureListener(e -> Log.w(TAG, "Error updating document",e));
        });
//
        Log.i(TAG,"Started Service");

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Update GUI
            updateGUI(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver()
        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));
        Log.i(TAG,"Registered broadcast receiver");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG,"Unregistered broadcast receiver");
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(broadcastReceiver);

        } catch (Exception e) {
            // Receiver was probably already
        }
        super.onStop();
//        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));

    }

    @Override
    protected void onDestroy() {
//        stopService(new Intent(this,BroadcastService.class));
        Log.i(TAG,"Stopped service");
        super.onDestroy();
        registerReceiver(broadcastReceiver,new IntentFilter(BroadcastService.COUNTDOWN_BR));

    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {

            long millisUntilFinished = intent.getLongExtra("countdown",30000);
            Log.i(TAG,"Countdown seconds remaining:" + millisUntilFinished / 1000);
            int seconds = (int) (millisUntilFinished / 1000) % 60;
            int minutes=(int) millisUntilFinished / 60000;
            txt.setText( String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(),MODE_PRIVATE);

            sharedPreferences.edit().putLong("time",millisUntilFinished).apply();
        }
    }
}