package com.example.launcher;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvExecutionLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExecutionLog = findViewById(R.id.tvExecutionLog);
        Button btnLaunchSettings = findViewById(R.id.btnLaunchSettings);
        Button btnLaunchStorage = findViewById(R.id.btnLaunchStorage);
        Button btnPickRealApp = findViewById(R.id.btnPickRealApp);

        // Direct Real Android System Settings Execution
        btnLaunchSettings.setOnClickListener(v -> {
            appendLog("
[INTENT] Launching Native System Settings Task...");
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        });

        // Direct Real Android Storage Execution
        btnLaunchStorage.setOnClickListener(v -> {
            appendLog("
[INTENT] Launching Native System Storage Task...");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Storage Manager Opened", Toast.LENGTH_SHORT).show();
            }
        });

        // Run any Installed App natively
        btnPickRealApp.setOnClickListener(v -> showRealAppContainerPicker());
    }

    private void showRealAppContainerPicker() {
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
        builder.setTitle("Select Real Installed App to Run");
        builder.setItems(options, (dialog, which) -> {
            String selectedPkg = packageNames.get(which);
            Intent launchIntent = pm.getLaunchIntentForPackage(selectedPkg);
            if (launchIntent != null) {
                appendLog("
[EXEC] Spawning native task process: " + selectedPkg);
                startActivity(launchIntent);
            } else {
                Toast.makeText(this, "Cannot execute package directly", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void appendLog(String log) {
        tvExecutionLog.append(log);
    }
}
