package com.example.studentpage;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "Imad";
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportActionBar().hide();

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();

        Button btn=findViewById(R.id.biometric_Attendance);
        btn.setOnClickListener(view -> {
            BiometricPrompt.PromptInfo promptInfo=new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Please Verify")
                    .setDescription("Scan Your Fingerprint For Attendance")
                    .setNegativeButtonText("cancel")
                    .build();
            getPrompt().authenticate(promptInfo);
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt getPrompt(){
        Executor executor= ContextCompat.getMainExecutor(this);
        BiometricPrompt.AuthenticationCallback callback=new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                notifyUser(errString.toString()+" Hello bro");
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
//                notifyUser("Successfull");
                add_data();

//                Query q = productsRef.whereEqualTo("email", email);
//                q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                if (document.exists()) {
//                                    document.get("name");
//                                }
//                            }
//                        }
//                    }
//                });

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                notifyUser("Your fingerprint does not match");
            }
        };

        BiometricPrompt biometricPrompt=new BiometricPrompt(this,executor,callback);
        return  biometricPrompt;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void add_data() {
        assert user != null;
        String email= user.getEmail();
        String date=java.time.LocalDate.now().toString();
        Log.d("date",date);
        CollectionReference attendance= db.collection(date);

        Query query = attendance.whereEqualTo("email", email);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String email = documentSnapshot.getString("email");

                        assert email != null;
                        if(email.equals(email)){
                            Log.d(TAG, "Attendance done");
                            Toast.makeText(MainActivity2.this, "Your today's attendance is already taken", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                if(task.getResult().size() == 0 ){

                    //You can store new user information here
                    Map<String, Object> items= new HashMap<>();
//                items.put("name",name);
                    items.put("email",email);
                    items.put("attendance taken",true);
                    attendance.add(items).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            notifyUser("data added");
                            Log.d(TAG, "Attendance record");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            notifyUser("Failed to ADD");
//                            Toast.makeText(MainActivity2.this, "Failed to ADD", Toast.LENGTH_SHORT).show();

                        }
                    });


                }
            }
        });
    }

    private void notifyUser(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}