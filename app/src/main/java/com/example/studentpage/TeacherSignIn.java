package com.example.studentpage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class TeacherSignIn extends AppCompatActivity {

    private EditText teacher_email;
    private EditText teacher_password;
    private Button button;
    private FirebaseAuth auth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_sign_in);
        getSupportActionBar().hide();

        teacher_email=findViewById(R.id.tv_email1);
        teacher_password=findViewById(R.id.editTextTextPassword);
        button=findViewById(R.id.add_student);
        auth=FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= teacher_email.getText().toString();
                String password=teacher_password.getText().toString();

                    teacherLogin(email,password);


            }
        });


    }

    private void teacherLogin(String email, String password) {
        dialog= new ProgressDialog(TeacherSignIn.this);
        dialog.setMessage("LOGGING IN........");
        dialog.show();
//
        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(TeacherSignIn.this, authResult -> {

            if(Objects.equals(Objects.requireNonNull(authResult.getUser()).getEmail(), "admin@gmail.com")){

                Intent intent = new Intent(TeacherSignIn.this,TeacherMain.class);
                startActivity(intent);
                finish();
                Toast.makeText(TeacherSignIn.this, "Login Successfull", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(TeacherSignIn.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                finish();
            }
            dialog.dismiss();


        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(TeacherSignIn.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();

        });

    }

    public void StudentLogIn(View view) {
        Intent intent = new Intent(this,StudentSignIn.class);
        startActivity(intent);
        finish();
    }


}