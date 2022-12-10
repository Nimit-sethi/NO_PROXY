package com.example.studentpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private CardView card;
    private TextView title;
    private LinearLayout profilel;
    private LinearLayout fingerprintl;
    private LinearLayout attendancel;
    private LinearLayout timetablel;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private String nameOfStudent;
    boolean value=false;
    boolean timer=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        profilel=findViewById(R.id.profile);
        fingerprintl=findViewById(R.id.biometric_Attendance);
        attendancel=findViewById(R.id.attendance);
        timetablel=findViewById(R.id.timetable);
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        card=findViewById(R.id.card_2);
        title=findViewById(R.id.title_1);
        CollectionReference newdetailRef = db.collection("student_details");

        user=auth.getCurrentUser();
//        assert user != null;
        assert user != null;
        String email=user.getEmail();
//

//        assert user != null;
        Query query = newdetailRef.whereEqualTo("email", email);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                     String email1 = documentSnapshot.getString("email");
                     nameOfStudent = documentSnapshot.getString("name");

                    assert email1 != null;
                    if(email1.equals(email)){
                        title.setText("Hello,\n\t\t\t"+nameOfStudent+"\uD83D\uDC4B");
                        Log.d("TAG", "User Exists");
//                        Toast.makeText(Student_Profile.this, "Email exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            if(task.getResult().size() == 0 ){
                Log.d("TAG", "Email does not Exists");
                //You can store new user information he


            }
        });
        profilel.setOnClickListener(view -> {
                    Toast.makeText(MainActivity.this, "My Profile", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(MainActivity.this,Student_Profile.class);
                    i.putExtra("email",email);
                    i.putExtra("name",nameOfStudent);

                    startActivity(i);
//                    finish();
                }

        );


        fingerprintl.setOnClickListener(view -> {

              getData();

//            if(returned_value){
//                Toast.makeText(MainActivity.this, "FingerPrint", Toast.LENGTH_SHORT).show();
//                Intent i=new Intent(MainActivity.this,MainActivity2.class);
//                startActivity(i);
//            }else{
//                Toast.makeText(MainActivity.this, "You are not within range of attendance", Toast.LENGTH_SHORT).show();
//
//            }

        });

//        attendancel.setOnClickListener(view -> {
//            Toast.makeText(MainActivity.this, "My Attendance", Toast.LENGTH_SHORT).show();
//            Intent i=new Intent(MainActivity.this,MainActivity3.class);
//            startActivity(i);
//        });

        timetablel.setOnClickListener(view -> {

            Intent i=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(i);
        });

    }

    private void getData() {
//        final boolean[] value = new boolean[1];
        DocumentReference docRef = db.collection("constants").document("constant");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
//                         value = (boolean) document.get("value");
                         timer= (boolean) document.get("timer");

//                        Log.i("LOGGER","First "+document.getString("first"));
//                        Log.i("LOGGER","Last "+document.getString("last"));
//                        Log.i("LOGGER","Born "+document.getString("born"));
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

        CollectionReference newdetailRef = db.collection("student_details");



        Query query = newdetailRef.whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    String email1 = documentSnapshot.getString("email");
                    Boolean bool=documentSnapshot.getBoolean("value");
                    assert email1 != null;
                    if(email1.equals(email1)){

                        value=bool;
                    }
                }
            }

            if(value && timer){
                Toast.makeText(MainActivity.this, "FingerPrint", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this,MainActivity2.class);
                startActivity(i);
            }else if(timer){
                Toast.makeText(MainActivity.this, "You are not within range of attendance ", Toast.LENGTH_SHORT).show();

            }else if(value){
                Toast.makeText(MainActivity.this, " Attendance time is not yet started", Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(MainActivity.this, "Attendance not started", Toast.LENGTH_SHORT).show();

            }


        });
//        return  value[0];
    }
}