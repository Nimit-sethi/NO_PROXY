package com.example.studentpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class Student_Profile extends AppCompatActivity {
    private static final String TAG = "user exists";

//
    TextView name;
    TextView email_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        getSupportActionBar().hide();


        name=findViewById(R.id.name_id);
        email_id=findViewById(R.id.email_id);
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String nameOfStudent = intent.getStringExtra("name");

        name.setText(nameOfStudent);
        email_id.setText(email);

    }
}