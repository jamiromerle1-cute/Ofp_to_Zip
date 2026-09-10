package com.example.launcher;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView btnThreeDots = findViewById(R.id.btnThreeDots);
        Button btnCapture = findViewById(R.id.btnCapture);

        btnThreeDots.setOnClickListener(v -> showHorizontalGameLockMenu());

        btnCapture.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "iOS Photo Captured!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showHorizontalGameLockMenu() {
        String[] options = {"Game Lock: ON", "Game Lock: OFF", "Performance Boost", "Exit Camera"};
        
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("iOS Camera & Game Mode");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                Toast.makeText(MainActivity.this, "Game Lock Activated (Horizontal)", Toast.LENGTH_SHORT).show();
            } else if (which == 1) {
                Toast.makeText(MainActivity.this, "Game Lock Deactivated", Toast.LENGTH_SHORT).show();
            } else if (which == 2) {
                Toast.makeText(MainActivity.this, "Performance Boosted!", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        });
        builder.show();
    }
}
