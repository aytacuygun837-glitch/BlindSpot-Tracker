package com.carai.blindspot;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // MJPEG Yayın Servisini Başlat
        MjpegServerService mjpegService = new MjpegServerService();
        mjpegService.startServerAndSyncFirebase();
    }
}
