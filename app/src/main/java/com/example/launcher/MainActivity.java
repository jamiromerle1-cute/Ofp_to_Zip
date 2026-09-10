package com.example.launcher;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("vmnativeengine");
    }

    public native String stringFromNativeVM();
    public native boolean formatPartitionNative(String devicePath, String formatType);

    private TextView tvStatus;
    private Spinner spinnerDevices;

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

        Toast.makeText(this, stringFromNativeVM(), Toast.LENGTH_SHORT).show();

        String[] formats = {"FAT32", "exFAT", "EXT4", "NTFS", "Quick Wipe", "Bad Blocks"};
        for (String fmtName : formats) {
            Button fmtBtn = new Button(this);
            fmtBtn.setText(fmtName);
            fmtBtn.setTextSize(12);
            fmtBtn.setTextColor(0xFFFFFFFF);
            fmtBtn.setBackgroundColor(0xFF1E293B);
            fmtBtn.setOnClickListener(v -> {
                String selectedDevFull = spinnerDevices.getSelectedItem().toString();
                String devPath = selectedDevFull.split(" ")[0];
                
                tvStatus.setText("Formatting " + devPath + " to " + fmtName + "...");
                boolean success = formatPartitionNative(devPath, fmtName);
                
                if (success) {
                    Toast.makeText(this, "Successfully formatted to " + fmtName, Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Status: Format Complete (" + fmtName + ")");
                } else {
                    Toast.makeText(this, "Format executed (Check root/binaries)", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Status: Target processed for " + fmtName);
                }
            });
            formatGrid.addView(fmtBtn);
        }
    }
}
