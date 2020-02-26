package vcims.com.vrapid.fragments;


import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import vcims.com.vrapid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PictureInPictureFragment extends Fragment implements SurfaceHolder.Callback {


    private final String TAG = PictureInPictureFragment.class.getSimpleName();
    private android.hardware.Camera camera;
    private SurfaceView cameraSurfaceView;
    private SurfaceHolder surfaceHolder;
    public PictureInPictureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_picture_in_picture, container, false);

        initialize(view);
        startCamera();
        //navToLauncherTask(getActivity());

        return view;
    }

    private void initialize(View v) {
        cameraSurfaceView = v.findViewById(R.id.cameraSurfaceView);
        surfaceHolder = cameraSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void startCamera() {
        try {
            int cameraCount = 0;
            android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo();
            cameraCount = android.hardware.Camera.getNumberOfCameras();
            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                android.hardware.Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        //camera = android.hardware.Camera.open();
                        camera = android.hardware.Camera.open(camIdx);
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return;
        }
        android.hardware.Camera.Parameters param = camera.getParameters();
        param.setPreviewFrameRate(20);
        param.setPreviewSize(176, 144);
        camera.setParameters(param);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            //camera.takePicture(shutter, raw, jpeg)
        } catch (Exception e) {
            Log.e(TAG, "init_camera: " + e);
            return;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        resetCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    public void resetCamera() {
        if (surfaceHolder.getSurface() == null) {
            // Return if preview surface does not exist
            return;
        }

        if (camera != null) {
            // Stop if preview surface is already running.
            camera.stopPreview();
            try {
                // Set preview display
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Start the camera preview...
            camera.startPreview();
        }
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void navToLauncherTask(Context appContext) {
        ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        // iterate app tasks available and navigate to launcher task (browse task)
        final List<ActivityManager.AppTask> appTasks = activityManager.getAppTasks();
        for (ActivityManager.AppTask task : appTasks) {
            final Intent baseIntent;
            baseIntent = task.getTaskInfo().baseIntent;
            final Set<String> categories = baseIntent.getCategories();
            if (categories != null && categories.contains(Intent.CATEGORY_LAUNCHER)) {
                task.finishAndRemoveTask();
                return;
            }
        }
    }

}
