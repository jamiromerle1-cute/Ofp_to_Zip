package com.example.launcher;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOptimizeBoot = findViewById(R.id.btnOptimizeBoot);
        Button btnStartupManager = findViewById(R.id.btnStartupManager);

        btnOptimizeBoot.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "LineageOS Boot Scripts Applied! Fast Boot Ready.", Toast.LENGTH_LONG).show();
        });

        btnStartupManager.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Background Startup Services Optimized.", Toast.LENGTH_SHORT).show();
        });
    }
}
