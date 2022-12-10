package com.example.studentpage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TeacherMain extends AppCompatActivity {
    private ArrayList<String> userList=new ArrayList<String>();
    private Map<String,String> m= new HashMap<>();
    private ArrayList<String> attendList=new ArrayList<String>();
    private File file;
    private FirebaseFirestore db;

    private FirebaseAuth auth;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        getSupportActionBar().hide();
        LinearLayout maps = findViewById(R.id.profile);
        LinearLayout attendance_timer = findViewById(R.id.biometric_Attendance);
        LinearLayout student_summary = findViewById(R.id.attendance);
        LinearLayout add_student = findViewById(R.id.timetable);
        logout= findViewById(R.id.logout);
        auth=FirebaseAuth.getInstance();

        db=FirebaseFirestore.getInstance();

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(TeacherMain.this, "My Profile", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(TeacherMain.this, MapsActivity.class);
                startActivity(i);
            }
        });

        attendance_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(TeacherMain.this, "FingerPrint", Toast.LENGTH_SHORT).show();

                Intent i=new Intent(TeacherMain.this, timer_broadcast.class);
                startActivity(i);
            }
        });

        student_summary.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
//                String date=java.time.LocalDate.now().toString();
//
//                CollectionReference user=db.collection(date);
//                user.get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
////                            Log.d(TAG, document.getId() + " => " + document.getData());
//                            String email= (String) document.get("email");
//                            Boolean attendance= document.getBoolean(
//                                    "attendance taken"
//                            );
//                            if(Boolean.TRUE.equals(attendance)){
//                                m.put(email,"P");
//
//                            }
//
//                        }
//                    } else {
//                        Log.d("imad", "Error getting documents: ", task.getException());
//                    }
//                });
//                CollectionReference user_absent=db.collection("student_details");
//
//                user_absent.get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
////                            Log.d(TAG, document.getId() + " => " + document.getData());
//                            String email= (String) document.get("email");
//                            if(!m.containsKey(email)){
//                                m.put(email,"A");
//                            }
//
//
//                        }
//                    } else {
//                        Log.d("iamd", "Error getting documents: ", task.getException());
//                    }
//                });

//                userList.addAll(m.keySet());
//                attendList.addAll(m.values());


//                Toast.makeText(TeacherMain.this, "My Attendance", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(TeacherMain.this,attendance_summary.class);
//                i.putExtra("map", (Serializable) m);
                startActivity(i);
            }
        });

        add_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(TeacherMain.this, "", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(TeacherMain.this,add_student.class);
                startActivity(i);
            }
        });

    }


    public void logout(View view) {
        if(auth.getCurrentUser()!=null){
            auth.signOut();

            Intent i=new Intent(TeacherMain.this,TeacherSignIn.class);
            startActivity(i);
            finish();
        }

    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        finish();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        finish();
//    }
};
