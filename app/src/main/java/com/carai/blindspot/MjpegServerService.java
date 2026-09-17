package com.carai.blindspot;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MjpegServerService {

    private DatabaseReference mDatabase;
    private int port = 8080;

    public void startServerAndSyncFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference("sentry_system");

        // Yerel yayın linklerini Firebase'e kaydet (iPhone'un okuması için)
        mDatabase.child("front_url").setValue("http://192.168.1.100:" + port + "/front");
        mDatabase.child("rear_url").setValue("http://192.168.1.100:" + port + "/rear");
        mDatabase.child("left_url").setValue("http://192.168.1.100:" + port + "/left");
        mDatabase.child("right_url").setValue("http://192.168.1.100:" + port + "/right");
    }

    public void onMotionDetected(String cameraZone, boolean hasMotion) {
        if (mDatabase != null) {
            mDatabase.child(cameraZone + "_motion").setValue(hasMotion);
        }
    }
}
