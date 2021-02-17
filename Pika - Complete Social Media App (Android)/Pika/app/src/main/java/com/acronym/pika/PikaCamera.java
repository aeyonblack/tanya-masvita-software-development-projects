package com.acronym.pika;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class PikaCamera extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private LinearLayout mPermissionsView;


    private Camera.PictureCallback jpegCallback;
    private final int CAMERA_REQUEST_CODE = 2202;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pika_camera);

        mPermissionsView = findViewById(R.id.request_permissions_view);
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceHolder = mSurfaceView.getHolder();

        // Check camera permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            mPermissionsView.setVisibility(View.VISIBLE);
        }
        else {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        // Ask for camera permissions
        findViewById(R.id.cam_request_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(PikaCamera.this,
                        new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE );
            }
        });

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();

        Camera.Parameters parameters = mCamera.getParameters();
        mCamera.setDisplayOrientation(90);
        parameters.setPreviewFrameRate(60);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = mCamera.getParameters().getSupportedPreviewSizes();
        bestSize = sizeList.get(0);
        for (int  i = 1; i < sizeList.size(); i++) {
            bestSize = sizeList.get(i);
        }
        parameters.setPictureSize(bestSize.width, bestSize.height);
        mCamera.setParameters(parameters);

        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCamera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionsView.setVisibility(View.GONE);
                mSurfaceHolder.addCallback(this);
                mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
            else {
                Toast.makeText(this, "I cannot access cam without permission",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
