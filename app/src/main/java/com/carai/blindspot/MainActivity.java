package com.carai.blindspot;

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private DatabaseReference mDatabase;
    private TextToSpeech tts;
    private boolean lastLeftAlert = false;
    private boolean lastRightAlert = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tts = new TextToSpeech(this, this);

        // Firebase Veritabanı Dinleyici
        mDatabase = FirebaseDatabase.getInstance().getReference("blind_spot_system");
        startTracking();
    }

    private void startTracking() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean left = snapshot.child("left_warning").getValue(Boolean.class);
                    Boolean right = snapshot.child("right_warning").getValue(Boolean.class);

                    boolean leftAlert = left != null && left;
                    boolean rightAlert = right != null && right;

                    // Sesli Uyarı Kontrolü
                    if (leftAlert && !lastLeftAlert) {
                        speak("Sol kör noktada araç var");
                    }
                    if (rightAlert && !lastRightAlert) {
                        speak("Sağ kör noktada araç var");
                    }

                    lastLeftAlert = leftAlert;
                    lastRightAlert = rightAlert;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BlindSpot_TTS");
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(new Locale("tr", "TR"));
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
