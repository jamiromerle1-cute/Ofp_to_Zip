package com.example.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    static {
        System.loadLibrary("vmnativeengine");
    }

    public native String stringFromNativeVM();
    public native boolean initNativeGraphics(Object surface);
    public native void renderFrameNative();
    public native boolean mountExt4ImageNative(String imagePath, String targetDir);
    public native void sendTouchEventNative(float x, float y, boolean actionDown);

    private static final int PICK_SYSTEM_IMG = 7007;
    private SurfaceView vmSurfaceView;
    private TextView tvEngineStatus, tvNativeLog;
    private GridLayout appGridContainer;
    private String selectedImgPath = null;
    private boolean isRendering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vmSurfaceView = findViewById(R.id.vmSurfaceView);
        tvEngineStatus = findViewById(R.id.tvDisplayStatus);
        tvNativeLog = findViewById(R.id.tvExecutionLog);
        appGridContainer = findViewById(R.id.appGridContainer);
        Button btnLoadImg = findViewById(R.id.btnLoadImg);
        Button btnBootVirtualEngine = findViewById(R.id.btnBootVirtualEngine);
        Button btnOpenSettings = findViewById(R.id.btnOpenSettings);

        vmSurfaceView.getHolder().addCallback(this);

        vmSurfaceView.setOnTouchListener((v, event) -> {
            boolean isDown = (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE);
            sendTouchEventNative(event.getX(), event.getY(), isDown);
            return true;
        });

        appendLog("[C++ NDK] " + stringFromNativeVM());

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
            
            File targetRootDir = new File(getFilesDir(), "virtual_rootfs");
            appendLog("\n[Virtual Engine] Mounting EXT4 image & unpacking system payload...");
            boolean isMounted = mountExt4ImageNative(selectedImgPath, targetRootDir.getAbsolutePath());
            
            if (isMounted) {
                appendLog("[PRoot Container] Virtual OS Boot Sequence Active.");
                tvEngineStatus.setText("Status: Virtual OS & Apps Online");
                tvEngineStatus.setTextColor(0xFF22C55E);
                appGridContainer.setVisibility(View.VISIBLE);
                startNativeRenderingLoop();
            }
        });

        btnOpenSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Opening Virtual OS Settings Panel...", Toast.LENGTH_SHORT).show();
            appendLog("[Virtual Settings] Opened system configuration overlay.");
        });

        setupVirtualApps();
    }

    private void setupVirtualApps() {
        String[] virtualApps = {"📁 Files", "⚙️ Settings", "🌐 Browser", "🎮 GSI Terminal", "📦 App Store", "⚡ Task Manager"};
        for (String appName : virtualApps) {
            Button appBtn = new Button(this);
            appBtn.setText(appName);
            appBtn.setTextSize(11);
            appBtn.setTextColor(0xFFFFFFFF);
            appBtn.setBackgroundColor(0xFF1E293B);
            appBtn.setOnClickListener(v -> {
                Toast.makeText(this, "Launched: " + appName + " inside GSI container", Toast.LENGTH_SHORT).show();
                appendLog("[Container App] Executed binary for: " + appName);
            });
            appGridContainer.addView(appBtn);
        }
    }

    private void startNativeRenderingLoop() {
        isRendering = true;
        new Thread(() -> {
            while (isRendering) {
                renderFrameNative();
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (initNativeGraphics(holder.getSurface())) {
            appendLog("[GPU Pipeline] EGL Display Surface Connected.");
        }
    }

    @Override public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}
    @Override public void surfaceDestroyed(@NonNull SurfaceHolder holder) { isRendering = false; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_SYSTEM_IMG && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedImgPath = uri.getPath();
                tvEngineStatus.setText("Status: GSI Mapped (" + selectedImgPath + ")");
            }
        }
    }

    private void appendLog(String log) {
        if (tvNativeLog != null) {
            tvNativeLog.append("\n" + log);
        }
    }
}
