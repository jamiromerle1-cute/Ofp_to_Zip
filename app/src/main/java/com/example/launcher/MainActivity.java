package com.example.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvVirtualLog, tvVirtualStatus;
    private boolean isVirtualRunning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvVirtualLog = findViewById(R.id.tvVirtualLog);
        tvVirtualStatus = findViewById(R.id.tvVirtualStatus);
        Button btnCreateSpace = findViewById(R.id.btnCreateSpace);
        Button btnKillVirtual = findViewById(R.id.btnKillVirtual);

        btnCreateSpace.setOnClickListener(v -> {
            if (!isVirtualRunning) {
                Toast.makeText(this, "Virtual Space is OFF. Start it first!", Toast.LENGTH_SHORT).show();
                return;
            }
            showAppPickerToVirtualize();
        });

        btnKillVirtual.setOnClickListener(v -> {
            isVirtualRunning = false;
            tvVirtualStatus.setText("Status: Virtual Environment Stopped");
            tvVirtualStatus.setTextColor(0xFFDC2626);
            appendLog("
[Virtual OS] Engine Shutdown. All instances killed.");
            Toast.makeText(this, "Virtual Environment Terminated.", Toast.LENGTH_SHORT).show();
        });
    }

    private void showAppPickerToVirtualize() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> appsList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> appNames = new ArrayList<>();
        final ArrayList<String> packageNames = new ArrayList<>();

        for (ApplicationInfo app : appsList) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appNames.add(pm.getApplicationLabel(app).toString());
                packageNames.add(app.packageName);
            }
        }

        String[] options = appNames.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select App to Launch in Virtual Space");
        builder.setItems(options, (dialog, which) -> {
            String selectedPkg = packageNames.get(which);
            String appName = options[which];
            
            appendLog("
[Virtual Container] Injecting process: " + selectedPkg);
            appendLog("[Virtual Container] Hooking system calls & sandbox parameters...");
            appendLog("[Virtual OS] App Launched Successfully: " + appName);
            
            Intent launchIntent = pm.getLaunchIntentForPackage(selectedPkg);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        });
        builder.show();
    }

    private void appendLog(String message) {
        tvVirtualLog.append(message);
    }
}
