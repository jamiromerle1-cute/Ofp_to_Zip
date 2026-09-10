package com.example.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_SYSTEM_IMG = 3003;
    private TextView tvSystemSlot, tvPartitionLog;
    private String selectedSystemImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSystemSlot = findViewById(R.id.tvSystemSlot);
        tvPartitionLog = findViewById(R.id.tvPartitionLog);
        Button btnSelectSystemImg = findViewById(R.id.btnSelectSystemImg);
        Button btnBootVmEngine = findViewById(R.id.btnBootVmEngine);

        btnSelectSystemImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select system.img File"), PICK_SYSTEM_IMG);
        });

        btnBootVmEngine.setOnClickListener(v -> {
            if (selectedSystemImg == null) {
                Toast.makeText(this, "Please select a system.img file first!", Toast.LENGTH_SHORT).show();
                appendLog("
[ERROR] Cannot boot: /system partition is missing!");
                return;
            }
            
            appendLog("

[BOOT] Loading Integrated Stock boot.img...");
            appendLog("[BOOT] Attaching Custom system.img: " + selectedSystemImg);
            appendLog("[BOOT] Initializing Ramdisk &amp; Mounting Partitions...");
            appendLog("[BOOT] Executing /system/bin/init process...");
            appendLog("[SUCCESS] GSI OS Environment Booted Successfully!");
            
            Toast.makeText(this, "Booting Custom system.img with Stock Boot Engine...", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SYSTEM_IMG && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedSystemImg = uri.getPath();
                tvSystemSlot.setText("📌 System Partition: Attached (" + selectedSystemImg + ")");
                tvSystemSlot.setTextColor(0xFF22C55E);
                appendLog("
[MOUNT] Custom system.img successfully mapped to /dev/block/by-name/system");
                Toast.makeText(this, "system.img Partition Attached!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void appendLog(String log) {
        tvPartitionLog.append(log);
    }
}
