package com.carai.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private TextView statusTextView;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusTextView = new TextView(this);
        statusTextView.setTextSize(18);
        statusTextView.setPadding(32, 32, 32, 32);
        statusTextView.setText("Sentry Mode Başlatılıyor...");
        setContentView(statusTextView);

        mDatabase = FirebaseDatabase.getInstance("https://karsilama-default-rtdb.firebaseio.com").getReference("sentry_system");

        initStorageFolder();

        if (checkAndRequestPermissions()) {
            startSentrySystem();
        }
    }

    private void initStorageFolder() {
        File captureFolder = new File(getExternalFilesDir(null), "SentryCaptures");
        if (!captureFolder.exists()) {
            captureFolder.mkdirs();
        }
    }

    private boolean checkAndRequestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        boolean allGranted = true;
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                startSentrySystem();
            } else {
                Toast.makeText(this, "Sentry Mode için gerekli izinler verilmedi!", Toast.LENGTH_LONG).show();
                statusTextView.setText("HATA: İzinler verilmedi.");
            }
        }
    }

    private void startSentrySystem() {
        String ipAddress = getIPAddress(true);
        if (!ipAddress.isEmpty()) {
            String baseUrl = "http://" + ipAddress + ":8080/";
            mDatabase.child("left_url").setValue(baseUrl + "left");
            mDatabase.child("front_url").setValue(baseUrl + "front");
            mDatabase.child("rear_url").setValue(baseUrl + "rear");
            mDatabase.child("right_url").setValue(baseUrl + "right");

            statusTextView.setText("SENTRY MOD AKTİF!\n\nCanlı Yayın Adresi:\n" + baseUrl + "\n\nKayıt Konumu:\n" + getExternalFilesDir(null) + "/SentryCaptures");
        } else {
            statusTextView.setText("SENTRY MOD AKTİF!\n\nİnternet/Wi-Fi bağlantısı bekleniyor...");
        }
    }

    private String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (useIPv4) {
                            if (isIPv4) return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { }
        return "";
    }
}
