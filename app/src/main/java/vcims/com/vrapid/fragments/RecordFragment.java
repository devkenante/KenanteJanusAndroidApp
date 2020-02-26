package vcims.com.vrapid.fragments;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.interfaces.AdminVideoCallbacks;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.SharedPref;
import vcims.com.vrapid.util.StaticMethods;

import static android.app.Activity.RESULT_OK;
import static vcims.com.vrapid.util.StaticMethods.askPermission;
import static vcims.com.vrapid.util.StaticMethods.hasPermissions;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment {

    private final int RECORDING_FRAGMENT = 5;
    private AdminVideoCallbacks adminVideoCallbacks;
    private final String TAG = RecordFragment.class.getSimpleName();
    private final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private final String RECORD_DIR = Constants.APP_NAME;
    private final int REQUEST_CODE = 1000;
    private final int DISPLAY_WIDTH = 720;
    private final int DISPLAY_HEIGHT = 1280;
    private DBHelper db;
    private SharedPref sharedPref;
    private MediaRecorder mMediaRecorder;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionCallback mMediaProjectionCallback;
    private String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
    };
    private MediaProjectionManager mProjectionManager;
    private int mScreenDensity;
    private String dialogId = "", selectedConfRoom = "";
    private int currentUserId = 0, currentUserType = 0;
    private List<Integer> occupants;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adminVideoCallbacks = (AdminVideoCallbacks)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        parseBundleExtras();
        initialize();
        //fragmentCallbacks.onFragmentReadyForData(RECORDING_FRAGMENT);
        startRecording();

        return view;
    }

    public void parseBundleExtras() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            dialogId = bundle.getString(Constants.EXTRA_DIALOG_ID);
            occupants = bundle.getIntegerArrayList(Constants.EXTRA_DIALOG_OCCUPANTS);
            CallData callData = CallData.getInstance(getActivity().getApplicationContext());
            selectedConfRoom = callData.getRoomName();
            currentUserId = callData.getCurrentUserId();
            currentUserType = callData.getCurrentUserType();
        }
    }

    public void getDataFromActivity(Bundle bundle){
        if (bundle != null) {
            currentUserId = bundle.getInt(Constants.QB_ID);
            occupants = bundle.getIntegerArrayList(Constants.EXTRA_DIALOG_OCCUPANTS);
            selectedConfRoom = bundle.getString(Constants.TAGS);
        }
        startRecording();
    }

    private void initialize() {
        db = DBHelper.getInstance(getActivity());
        sharedPref = SharedPref.getInstance();
    }

    private void startRecording() {
        initRecordingComponents();
        if (hasPermissions(getActivity(), PERMISSIONS)) {
            if (StaticMethods.isNetworkAvailable(getActivity())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //Start Screen Recording
                    initRecorder();
                    shareScreen();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            }
        } else {
            askPermission(getActivity(), PERMISSIONS);
        }
    }

    private void initRecordingComponents() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mMediaRecorder = new MediaRecorder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProjectionManager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
    }

    private void initRecorder() {
        if (mMediaRecorder != null)
            mMediaRecorder.reset();
        String filename = sharedPref.TAGS + "_" + StaticMethods.getCurrentTime() + ".mp4";
        //     String storage_dir = Environment.getExternalStorageDirectory() + "/" + RECORD_DIR + "/" + filename;
        Log.e(TAG, Environment.getExternalStorageDirectory().toString());
        File mediaStorageDir = new File(
                Environment
                        .getExternalStorageDirectory(),
                RECORD_DIR);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(RECORD_DIR, "Oops! Failed to create "
                        + RECORD_DIR + " directory");

            }
        }
        String storage_dir = Environment.getExternalStorageDirectory() + "/" + RECORD_DIR + "/" + filename;
        try {
            if (mMediaRecorder != null) {
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //THREE_GPP
                mMediaRecorder.setOutputFile(storage_dir);
                mMediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mMediaRecorder.setAudioEncodingBitRate(1024 * 1024 * 5);
                mMediaRecorder.setAudioSamplingRate(44100);
                mMediaRecorder.setVideoFrameRate(30); // 30
                mMediaRecorder.setVideoEncodingBitRate(1500 * 1024);
                int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
                int orientation = ORIENTATIONS.get(rotation + 90);
                mMediaRecorder.setOrientationHint(orientation);
                mMediaRecorder.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.insertVideoFile(filename, storage_dir, sharedPref.TAGS);
    }

    private void shareScreen() {
        if (mMediaProjection == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
                return;
            }
        }
        mVirtualDisplay = createVirtualDisplay();
        mMediaRecorder.start();
        adminVideoCallbacks.recordingStarted(true);
    }

    private VirtualDisplay createVirtualDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mMediaProjection.createVirtualDisplay(TAG, DISPLAY_WIDTH, DISPLAY_HEIGHT, mScreenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
        } else
            return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_KEY:

                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] + grantResults[2] + grantResults[3]) == PackageManager.PERMISSION_GRANTED) {
                    initRecorder();
                    shareScreen();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                    adminVideoCallbacks.recordingStarted(false);
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE) {
            if (mMediaRecorder != null)
                mMediaRecorder.reset();
        } else if (resultCode != RESULT_OK) {
            if (mMediaRecorder != null)
                mMediaRecorder.reset();
            Toast.makeText(getActivity(), getString(R.string.call_recording_necessary), Toast.LENGTH_SHORT).show();
            adminVideoCallbacks.recordingStarted(false);
        } else {
            //Send Push Notification
            //On successful Notification proceed further
            if(currentUserType == Constants.ADMIN) {
                /*try {
                    sendPushNotification(resultCode, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
            else if(currentUserType == Constants.RECORDER)
                startRecordingAfterOnActivityResult(resultCode, data);
        }
    }

    /*private void sendPushNotification(int resultCode, Intent data) throws JSONException {
        QBEvent event = new QBEvent();
        event.setNotificationType(QBNotificationType.PUSH);
        //To be changed to Production
        event.setEnvironment(QBEnvironment.PRODUCTION);
        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        occupants.remove((Object)currentUserId);
        userIds.addAll(occupants);
        event.setUserIds(userIds);
        JSONObject object = new JSONObject();
        object.put("message", "You have a call from Kenante");
        object.put("dialog_id", dialogId);
        object.put("ios_mutable_content", "1");
        object.put("category", "Call");
        object.put(Constants.OCCUPANTS, occupants.size());
        object.put("room_name", selectedConfRoom);
        event.setMessage(String.valueOf(object));

        QBPushNotifications.createEvent(event).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                startRecordingAfterOnActivityResult(resultCode, data);
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                startRecordingAfterOnActivityResult(resultCode, data);
            }
        });
    }*/

    private void startRecordingAfterOnActivityResult(int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            mMediaProjectionCallback = new MediaProjectionCallback();
            mMediaProjection.registerCallback(mMediaProjectionCallback, null);
            mVirtualDisplay = createVirtualDisplay();
            mMediaRecorder.start();
            adminVideoCallbacks.recordingStarted(true);
        }
    }

    public void stopRecording() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                Log.i("Exception", Log.getStackTraceString(e));
            } catch (RuntimeException e) {
                Log.i("Exception", Log.getStackTraceString(e));
            } catch (Exception e) {
                Log.i("Exception", Log.getStackTraceString(e));
            }
            mMediaRecorder.reset();
            mMediaRecorder.release();
        }

        if (mVirtualDisplay == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mVirtualDisplay.release();
            destroyMediaProjection();
        }
        Log.e(TAG, "Recording stopped");
    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            //mMediaProjection = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection.unregisterCallback(mMediaProjectionCallback);
                mMediaProjection.stop();
                mMediaProjection = null;
            }
        }
        Log.i(TAG, "MediaProjection Stopped");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if(mMediaRecorder!=null) {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaProjection = null;
                stopRecording();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "Recording fragment destroyed");
    }
}
