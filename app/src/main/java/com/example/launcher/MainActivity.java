package com.example.launcher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
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

    private static final int PICK_SYSTEM_IMG = 7007;
    private SurfaceView vmSurfaceView;
    private TextView tvEngineStatus, tvNativeLog;
    private String selectedImgPath = null;
    private boolean isRendering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vmSurfaceView = findViewById(R.id.vmSurfaceView);
        tvEngineStatus = findViewById(R.id.tvDisplayStatus);
        tvNativeLog = findViewById(R.id.tvExecutionLog);
        Button btnLoadImg = findViewById(R.id.btnLoadImg);
        Button btnBootVirtualEngine = findViewById(R.id.btnBootVirtualEngine);

        vmSurfaceView.getHolder().addCallback(this);

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
            appendLog("\n[C++ EXT4 Driver] Analyzing Superblock & Parsing Image...");
            boolean isMounted = mountExt4ImageNative(selectedImgPath, targetRootDir.getAbsolutePath());
            
            if (isMounted) {
                appendLog("[C++ EXT4 Driver] RootFS Mount Point Ready: " + targetRootDir.getAbsolutePath());
                appendLog("[C++ NDK] Starting GPU OpenGL Framebuffer Stream...");
                tvEngineStatus.setText("Status: Native EXT4 Unpacker & GPU Pipeline Active");
                tvEngineStatus.setTextColor(0xFF22C55E);
                startNativeRenderingLoop();
            } else {
                appendLog("[C++ ERROR] Invalid or unreadable disk image file.");
            }
        });
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
            appendLog("[C++ NDK] EGL Window Surface Connected.");
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
                tvEngineStatus.setText("Status: Image Mapped (" + selectedImgPath + ")");
            }
        }
    }

    private void appendLog(String log) {
        if (tvNativeLog != null) {
            tvNativeLog.append("\n" + log);
        }
    }
}
