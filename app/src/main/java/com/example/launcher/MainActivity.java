package com.example.launcher;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
ri.rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("vmnativeengine");
    }

    public native String stringFromNativeVM();
    public native boolean formatPartitionNative(String devicePath, String formatType);

    private TextView tvStatus;
    private Spinner spinnerDevices;

    private static final int SHIZUKU_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvDisplayStatus);
        spinnerDevices = findViewById(R.id.spinnerDevices);
        GridLayout formatGrid = findViewById(R.id.formatGridContainer);

        String[] detectedDevices = {"/dev/block/mmcblk1p1 (SD Card)", "/dev/block/sda1 (OTG USB)", "/dev/block/sdb1 (External Drive)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, detectedDevices);
        spinnerDevices.setAdapter(adapter);

        checkShizukuPermission();

        String[] formats = {"FAT32", "exFAT", "EXT4", "NTFS", "Quick Wipe", "Bad Blocks"};
        for (String fmtName : formats) {
            Button fmtBtn = new Button(this);
            fmtBtn.setText(fmtName);
            fmtBtn.setTextSize(12);
            fmtBtn.setTextColor(0xFFFFFFFF);
            fmtBtn.setBackgroundColor(0xFF1E293B);
            fmtBtn.setOnClickListener(v -> {
                if (!isShizukuAvailable()) {
                    Toast.makeText(this, "Shizuku is not running or permission denied!", Toast.LENGTH_LONG).show();
                    return;
                }

                String selectedDevFull = spinnerDevices.getSelectedItem().toString();
                String devPath = selectedDevFull.split(" ")[0];
                
                tvStatus.setText("Formatting " + devPath + " to " + fmtName + " via Shizuku...");
                boolean success = formatPartitionNative(devPath, fmtName);
                
                if (success) {
                    Toast.makeText(this, "Successfully formatted to " + fmtName, Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Status: Format Complete (" + fmtName + ")");
                } else {
                    Toast.makeText(this, "Execution finished (Check device binaries)", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Status: Target processed for " + fmtName);
                }
            });
            formatGrid.addView(fmtBtn);
        }
    }

    private void checkShizukuPermission() {
        try {
            if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
                tvStatus.setText("Status: Shizuku version too old");
                return;
            }

            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                tvStatus.setText("Status: Shizuku Connected (Ready for Non-Root format)");
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                tvStatus.setText("Status: Shizuku permission required");
            } else {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE);
            }
        } catch (Exception e) {
            tvStatus.setText("Status: Shizuku service not found. Make sure it is running.");
        }
    }

    private boolean isShizukuAvailable() {
        try {
            return Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        } catch (Exception e) {
            return false;
        }
    }
}
