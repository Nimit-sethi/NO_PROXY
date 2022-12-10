package com.example.studentpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastRece";


    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private FirebaseAuth auth=FirebaseAuth.getInstance();
    @Override
    public void onReceive(Context context, Intent intent) {



        NotificationHelper notificationHelper = new NotificationHelper(context);


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        assert geofencingEvent != null;
        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        assert geofenceList != null;
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
            CollectionReference newdetailRef = db.collection("student_details");



            Query query = newdetailRef.whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
//                        String email1 = documentSnapshot.getString("email");
//                        Boolean value=documentSnapshot.getBoolean("value");
                        newdetailRef.document(documentSnapshot.getId()).update("value",true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("geofence","value updated successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("geofence","value updation failed");

                            }
                        });
//                            items.put("email",email);
//                            newdetailRef.add.addOnSuccessListener(documentReference -> Toast.makeText(add_student.this, "Data Added", Toast.LENGTH_SHORT).show()).addOnFailureListener(new OnFailureListener() {

//                            Log.d(TAG, "User Exists");
//                            Toast.makeText(add_student.this, "Email exists", Toast.LENGTH_SHORT).show();
                        }
                    }



            });

//            DocumentReference constant= db.collection("constants").document("constant");

//            constant.update("value",true).addOnSuccessListener(unused -> Log.d(TAG,"DocumentSnapshot successfully updted")).addOnFailureListener(e -> Log.w(TAG, "Error updating document",e));


        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
        } else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
            CollectionReference newdetailRef = db.collection("student_details");



            Query query = newdetailRef.whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail());
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        String email1 = documentSnapshot.getString("email");
                        Boolean value=documentSnapshot.getBoolean("value");
                        newdetailRef.document(documentSnapshot.getId()).update("value",false).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("geofence","value updated successfully");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("geofence","value updation failed");

                            }
                        });
//
                        }
                    }



            });

            Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
            notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
        }
//        throw new UnsupportedOperationException("Not yet implemented");
    }

}