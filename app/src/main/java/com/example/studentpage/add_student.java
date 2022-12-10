package com.example.studentpage;

//import android.app.ProgressDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
//import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class add_student extends AppCompatActivity {
    private static final String TAG = "";
    private EditText email;
    private EditText password;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Task previous_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user=auth.getCurrentUser();

//        previous_user=auth.getAccessToken(true);
        EditText name = findViewById(R.id.name);
        email=findViewById(R.id.email);
        password=findViewById(R.id.pass);
        Button addStudent = findViewById(R.id.add_student);

        addStudent.setOnClickListener(view -> {
            String name_st=name.getText().toString();
            String email_st=email.getText().toString();
            String password_st=password.getText().toString();

            Sendmail sm= new Sendmail();

            sm.execute();


            insertStudent(name_st,email_st);
            AddStudent(name_st,email_st,password_st);

//            insertStudent(name_st,email_st);
            name.setText("");
            email.setText("");
            password.setText("");

        });

    }

    private void insertStudent(String name,String email) {
        CollectionReference newdetailRef = db.collection("student_details");



        Query query = newdetailRef.whereEqualTo("email", email);
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    String email1 = documentSnapshot.getString("email");

                    assert email1 != null;
                    if(email1.equals(email1)){
                        Log.d(TAG, "User Exists");
                        Toast.makeText(add_student.this, "Email exists", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            if(task.getResult().size() == 0 ){
                Log.d(TAG, "Email does not Exists");
                //You can store new user information here
                Map<String,Object> items= new HashMap<>();
                items.put("name",name);
                items.put("email",email);
                items.put("value",false);
                newdetailRef.add(items)
                        .addOnSuccessListener(documentReference -> Toast.makeText(add_student.this, "Data Added", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(add_student.this, "Failed to ADD", Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });


    }


    private void AddStudent(String name,String email,String password) {


        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("success", "createUserWithEmail:success");
                        Toast.makeText(add_student.this, "STUDENT SUCCESSFULLY ADDED.",
                                Toast.LENGTH_SHORT).show();
                        Log.d("success", auth.getCurrentUser().getEmail());

                        auth.signOut();
                        ProgressDialog dialog= new ProgressDialog(add_student.this);
                        dialog.setMessage("");
                        dialog.show();
                        auth.signInWithEmailAndPassword("admin@gmail.com","admin123").addOnSuccessListener(add_student.this, authResult -> {

                            if(Objects.equals(Objects.requireNonNull(authResult.getUser()).getEmail(), "admin@gmail.com")){

                                Intent intent = new Intent(add_student.this,TeacherMain.class);
                                startActivity(intent);
                                finish();
//                                Toast.makeText(TeacherSignIn.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                            }else{
//                                Toast.makeText(TeacherSignIn.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            dialog.dismiss();


                        });
//                        auth.updateCurrentUser(user);
//                        Log.d("success", auth.getCurrentUser().getEmail());

//                        finish();



                    } else{
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }  catch(FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(add_student.this, "EMAIL OR PASSWORD INCORRECT.",
                                    Toast.LENGTH_SHORT).show();
                        } catch(FirebaseAuthUserCollisionException e) {
                            Toast.makeText(add_student.this, "USER ALREADY EXISTS",
                                    Toast.LENGTH_SHORT).show();
                        } catch(Exception e) {
                            Log.e("ERROR", e.getMessage());
                        }
                        // If sign in fails, display a message to the user.
                        Log.w("fails", "createUserWithEmail:failure", task.getException());

                        Toast.makeText(add_student.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }
    public class Sendmail extends AsyncTask {
        private Session session;


        @Override
        protected Object doInBackground(Object[] objects) {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("20ucs129@lnmiit.ac.in","************" );

                }
            });


            //Step 2 : compose the message [text,multi media]

            Log.d("Test","here");
            try {
                MimeMessage msg = new MimeMessage(session);
    Log.d("mail",email.getText().toString());
                msg.setFrom(new InternetAddress("20ucs129@lnmiit.ac.in"));
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email.getText().toString()));
                msg.setSubject("Login Credentials");

                Multipart emailContent = new MimeMultipart();

                //Text body part
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText("Welcome to IMAD Course \nBelow are your credentials to login into Biometric Attendance App\n" +
                        "Email: "+email.getText().toString()+"\nPassword: "+password.getText().toString());
                emailContent.addBodyPart(textBodyPart);
                msg.setContent(emailContent);
                Transport.send(msg);

            } catch (MessagingException e) {
                e.printStackTrace();

            }
            return null;
        }
    }
}
