package com.example.studentpage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;


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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class attendance_summary extends AppCompatActivity{

    private static final String TAG = "IMAD";

    private Button send;
    private ArrayList<String> userList=new ArrayList<String>();
    private Map<String,String> m= new HashMap<>();
    private ArrayList<String> attendList=new ArrayList<String>();
    private File file;
    private FirebaseFirestore db;

    int present_count;
    int absent_count;
    PieChart pieChart;
    List<PieEntry> pieEntryList ;
//    private EditText location;
    private Session session;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_attendance_summary);
        getSupportActionBar().hide();

        db=FirebaseFirestore.getInstance();

//        Intent intent = getIntent();
//        Map<String, String> m = (HashMap<String, String>)intent.getSerializableExtra("map");
//        Log.v("HashMapTest", hashMap.get("key"));

        pieChart = findViewById(R.id.chart);
        getSupportActionBar().hide();
        send= findViewById(R.id.button_send);
//        location=findViewById(R.id.location);
//        Button button=findViewById(R.id.ExelWrite);
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        send.setOnClickListener(this::onClick);


        pieEntryList =  new ArrayList<>();
        pieChart = findViewById(R.id.chart);

        Log.d("checka2", "oncreate");



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart(){
        super.onStart();
        Data_added  data= new Data_added();

        data.execute();






    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClick(View v) {


        userList.addAll(m.keySet());
        attendList.addAll(m.values());
                    for(int j   =0;j<attendList.size();j++){
                if(Objects.equals(attendList.get(j), "P")){

                    present_count++;
                }else{
                    absent_count++;
                }
            }

            setvalues();
            setUpChart();
        Log.d("checkm", String.valueOf(m.size()));
        Log.d("check", String.valueOf(attendList.size()));
        for(int i=0;i<attendList.size();i++){
            Log.d("check", attendList.get(0));
        }
        Workbook wb=new HSSFWorkbook();
        Cell cell=null;
        CellStyle cellStyle=wb.createCellStyle();



        //Now we are creating sheet
        Sheet sheet=null;
        sheet = wb.createSheet("AttendanceIMAD");
        //Now column and row
        Row row =sheet.createRow(0);

        cell= (Cell) row.createCell(0);

        ((Cell) cell).setCellValue("Roll No");
        ((Cell) cell).setCellStyle(cellStyle);

        cell= (Cell) row.createCell(1);
        ((Cell) cell).setCellValue("Attendance");
        ((Cell) cell).setCellStyle(cellStyle);

        sheet.setColumnWidth(0,(20*200));
        sheet.setColumnWidth(1,(20*200));

        for (int i = 0; i < userList.size(); i++) {
            Row row1 = sheet.createRow(i + 1);

            cell = (Cell) row1.createCell(0);
            ((Cell) cell).setCellValue(userList.get(i));

            cell = (Cell) row1.createCell(1);
            ((HSSFCell) cell).setCellValue(attendList.get(i));
            //  cell.setCellStyle(cellStyle);


            sheet.setColumnWidth(0,(20*200));
            sheet.setColumnWidth(1,(20*200));


        }

        file = new File(getExternalFilesDir(null),System.currentTimeMillis() +".xls");
        FileOutputStream outputStream =null;

        try {
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(getApplicationContext(),"Mail Sent successfully",Toast.LENGTH_LONG).show();


        } catch (IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(),"Error occurred while creating file",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        // EMAIL

        Sendmail  sm= new Sendmail();
        sm.execute();


    }
    public class Data_added extends AsyncTask{






        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Object doInBackground(Object[] objects) {

//            Map<String,String> m= new HashMap<>();
//            m.clear();
            absent_count=0;
            present_count=0;
//            attendList.clear();
//            userList.clear();

            String date=java.time.LocalDate.now().toString();
            Log.d("check", date);

            CollectionReference user=db.collection(date);

            user.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("check", document.getId() + " => " + document.getData());
                        String email= (String) document.get("email");
                        Boolean attendance= document.getBoolean(
                                "attendance taken"
                        );
                        if(Boolean.TRUE.equals(attendance)){
                            m.put(email,"P");
Log.d("present","ajdfasj");
                        }

                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
            CollectionReference user_absent=db.collection("student_details");

            user_absent.get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
//                            Log.d(TAG, document.getId() + " => " + document.getData());
                        String email= (String) document.get("email");
                        if(!m.containsKey(email)){
                            m.put(email,"A");
                        }



                    }
//                    dialog.dismiss();






                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
            );
            Log.d("checka2", "success");



            Log.d("checka2", String.valueOf(m.size()));





//
            return null;
        }

//
    }
    public class Sendmail extends AsyncTask {
        private Session session;
//        private ProgressDialog progressDialog;

        @Override
        protected Object doInBackground(Object[] objects) {

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("20ucs129@lnmiit.ac.in","college+-*/@123" );

                }
            });


            //Step 2 : compose the message [text,multi media]

            Log.d("Test","here");
            try {
                MimeMessage msg = new MimeMessage(session);
                Log.d("Test","here1");
                msg.setFrom(new InternetAddress("20ucs129@lnmiit.ac.in"));
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress("nimitsethi22@gmail.com"));
                msg.setSubject("Attendance"); Log.d("Test","here2");

                Multipart emailContent = new MimeMultipart();

                //Text body part
                MimeBodyPart textBodyPart = new MimeBodyPart();
                textBodyPart.setText("Today attendance of students are attached below:");
                Log.d("Test","here3");
                //Attachment body part.
                MimeBodyPart pdfAttachment = new MimeBodyPart();


                pdfAttachment.attachFile(file);
                Log.d("Test","here4");
                //Attach body parts
                emailContent.addBodyPart(textBodyPart);
                emailContent.addBodyPart(pdfAttachment);

                Log.d("Test","here5");
                msg.setContent(emailContent);
                Log.d("Test","here6");

                Transport.send(msg);
                Log.d("Test","here7");
            } catch (MessagingException | IOException e) {
                e.printStackTrace();
                Log.d("Test","here8");
            }
            return null;
        }
    }
    private void setUpChart() {
        pieChart.clear();
//        dialog.show();
        final int[] MY_COLORS = {  Color.rgb(246,162,37), Color.rgb(1,166,249), Color.rgb(79,129,189)};

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);
        PieDataSet pieDataSet = new PieDataSet(pieEntryList,"Attendance");
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(colors);
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    private void setvalues() {

        pieEntryList.clear();
        pieEntryList.add(new PieEntry(absent_count, "Absent"));
        pieEntryList.add(new PieEntry(present_count, "Present"));

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}