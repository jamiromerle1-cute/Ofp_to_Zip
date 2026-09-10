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

    static {
        System.loadLibrary("vmnativeengine");
    }

    // C++ JNI Native Methods
    public native String stringFromNativeVM();
    public native boolean mountExt4ImageNative(String imagePath);

    private static final int PICK_SYSTEM_IMG = 5005;
    private TextView tvEngineStatus, tvNativeLog;
    private String selectedImgPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEngineStatus = findViewById(R.id.tvDisplayStatus);
        tvNativeLog = findViewById(R.id.tvExecutionLog);
        Button btnLoadImg = findViewById(R.id.btnLoadImg);
        Button btnBootVirtualEngine = findViewById(R.id.btnBootVirtualEngine);

        // Call C++ Native Code on App Launch
        String nativeInfo = stringFromNativeVM();
        appendLog("[C++ NDK] " + nativeInfo);

        btnLoadImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent, "Select GSI system.img"), PICK_SYSTEM_IMG);
        });

        btnBootVirtualEngine.setOnClickListener(v -> {
            if (selectedImgPath == null) {
                Toast.makeText(this, "Pumili muna ng system.img file!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            appendLog("
[C++ NDK] Invoking Native EXT4 Container Mount...");
            boolean isMounted = mountExt4ImageNative(selectedImgPath);
            
            if (isMounted) {
                appendLog("[C++ NDK] EXT4 System Image Successfully Attached to Native Pipeline.");
                appendLog("[C++ NDK] Starting User-Space PRoot Container Environment...");
                tvEngineStatus.setText("Status: Native C++ Container Running");
                tvEngineStatus.setTextColor(0xFF22C55E);
                Toast.makeText(this, "Native C++ Engine Executing System Image!", Toast.LENGTH_SHORT).show();
            } else {
                appendLog("[C++ NDK ERROR] Failed to mount image file.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SYSTEM_IMG && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImgPath = uri.getPath();
                tvEngineStatus.setText("Status: Image Mapped via NDK (" + selectedImgPath + ")");
                appendLog("
[JAVA] Image File Path Passed to Native Core: " + selectedImgPath);
            }
        }
    }

    private void appendLog(String log) {
        if (tvNativeLog != null) {
            tvNativeLog.append("
" + log);
        }
    }
}
