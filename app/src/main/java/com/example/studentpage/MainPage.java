package com.example.studentpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainPage extends AppCompatActivity {
    private LinearLayout student;
    private LinearLayout teacher;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
//        assert user != null;
//        assert user != null;
//        Log.d("imad", String.valueOf(user.getEmail()));

        if(user!=null && Objects.equals(user.getEmail(), "admin@gmail.com"))
        {
            startActivity(new Intent (MainPage.this,TeacherMain.class));
            finish();
        }else if(user!=null){
            startActivity(new Intent (MainPage.this,MainActivity.class));
            finish();
        }


        student= findViewById(R.id.studentl);
        teacher=findViewById(R.id.teacherl);
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainPage.this,StudentSignIn.class);
                startActivity(i);
                finish();
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainPage.this,TeacherSignIn.class);
                startActivity(i);
                finish();
            }
        });

    }
}