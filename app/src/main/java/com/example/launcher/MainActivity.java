package com.example.launcher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus;
    private String selectedStoragePath = "/sdcard/target_partition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvDisplayStatus);
        GridLayout formatGrid = findViewById(R.id.formatGridContainer);
        Button btnDetectStorage = findViewById(R.id.btnDetectStorage);

        btnDetectStorage.setOnClickListener(v -> {
            File extDir = getExternalFilesDir(null);
            if (extDir != null) {
                selectedStoragePath = extDir.getAbsolutePath();
                tvStatus.setText("Target Mounted: " + selectedStoragePath);
                Toast.makeText(this, "Storage target locked.", Toast.LENGTH_SHORT).show();
            }
        });

        String[] formats = {"🟢 FAT32", "🔵 exFAT", "🟠 EXT4", "🟣 NTFS", "⚡ Quick Wipe", "🔍 Check Bad Blocks"};
        for (String fmtName : formats) {
            Button fmtBtn = new Button(this);
            fmtBtn.setText(fmtName);
            fmtBtn.setTextSize(12);
            fmtBtn.setTextColor(0xFFFFFFFF);
            fmtBtn.setBackgroundColor(0xFF1E293B);
            fmtBtn.setOnClickListener(v -> {
                Toast.makeText(this, "Executing format: " + fmtName, Toast.LENGTH_SHORT).show();
                tvStatus.setText("Status: Formatting partition to " + fmtName + "...");
            });
            formatGrid.addView(fmtBtn);
        }
    }
}
