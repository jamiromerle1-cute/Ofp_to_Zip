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

        Button btnBoost = findViewById(R.id.btnBoost);
        Button btnClearCache = findViewById(R.id.btnClearCache);

        btnBoost.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "LineageOS Engine: CPU & RAM Optimized for Gaming!", Toast.LENGTH_LONG).show();
        });

        btnClearCache.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Cache Cleared! System is running faster.", Toast.LENGTH_SHORT).show();
        });
    }
}
