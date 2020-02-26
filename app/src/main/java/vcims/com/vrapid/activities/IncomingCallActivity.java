package vcims.com.vrapid.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.models.FirebaseVideoModel;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.SharedPref;
import vcims.com.vrapid.util.StaticMethods;

public class IncomingCallActivity extends AppCompatActivity {

    private final String TAG = "IncomingCallActivity";
    private String dialogId, occupants, roomName;
    private DBHelper db;
    private SharedPref sharedPref;
    private View.OnClickListener onClickListener;
    private ImageView startCall, endCall;
    private ProgressDialog dialog;
    private MediaPlayer mp;
    private Boolean userPickedUpCall = false;
    private Vibrator vibrator;
    private CallData callData;
    private KenanteUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        startRingTone();
        checkIfUserInteracted();
        parseIntentExtras();
        initViewsAndComponents();
        clickListener();

    }

    private void startRingTone() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(this, notification);
        mp.start();
        startVibration();
        //Call has started status flag
    }

    private void startVibration() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 5000, 10000, 15000, 20000, 25000, 30000};
        assert vibrator != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }

    private void stopRingTone() {
        mp.release();
    }

    private void parseIntentExtras() {
        dialogId = getIntent().getStringExtra(Constants.DIALOG_ID);
        occupants = getIntent().getStringExtra(Constants.OCCUPANTS);
        roomName = getIntent().getStringExtra(Constants.ROOM);
    }

    private void initViewsAndComponents() {
        startCall = findViewById(R.id.startCall);
        endCall = findViewById(R.id.endCall);
        dialog = new ProgressDialog(this);
        db = DBHelper.getInstance(this);
        sharedPref = SharedPref.getInstance();
        sharedPref.CALL_STARTED = true;
        sharedPref.TAGS = roomName;
        sharedPref.updateSetting();
    }

    private void clickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.startCall:
                        if (StaticMethods.isNetworkAvailable(IncomingCallActivity.this)) {
                            if (hasPermission()) {
                                stopRingTone();
                                if (vibrator != null)
                                    vibrator.cancel();
                                userPickedUpCall = true;
                                startCallAlert();
                                getThisCallData();
                            }
                        } else {
                            Toast.makeText(IncomingCallActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.endCall:
                        stopRingTone();
                        Toast.makeText(IncomingCallActivity.this, getString(R.string.start_dynamic_call), Toast.LENGTH_SHORT).show();
                        sharedPref.CALL_STARTED = false;
                        sharedPref.updateSetting();
                        finish();
                        break;
                }
            }
        };
        startCall.setOnClickListener(onClickListener);
        endCall.setOnClickListener(onClickListener);
    }

    private void startConference() {

        /*ConferenceClient client = ConferenceClient.getInstance(getApplicationContext());
        QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        client.createSession(currentUser.getId(), conferenceType, new ConferenceEntityCallback<ConferenceSession>() {
            @Override
            public void onSuccess(ConferenceSession session) {
                webRtcSessionManager.setCurrentSession(session);
                Log.d(TAG, "IncomingCallActivity setCurrentSession onSuccess() session getCurrentUserID= " + session.getCurrentUserID());
                CallActivity.start(IncomingCallActivity.this, dialogId, Integer.valueOf(occupants), false, false);
                dialog.cancel();
                finish();
            }

            @Override
            public void onError(WsException responseException) {
                Log.e(TAG, responseException.getMessage());
                Toast.makeText(IncomingCallActivity.this, getString(R.string.call_error), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog.isShowing())
            dialog.cancel();
        if (vibrator != null)
            vibrator.cancel();
    }


    private void checkIfUserInteracted() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!userPickedUpCall) {
                    sharedPref.CALL_STARTED = false;
                    sharedPref.updateSetting();
                    stopRingTone();
                    finish();
                }
            }
        }, 30000);
    }

    private Boolean hasPermission() {
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE
        };
        if (StaticMethods.hasPermissions(this, PERMISSIONS))
            return true;
        else
            StaticMethods.askPermission(this, PERMISSIONS);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_KEY) {
            if ((grantResults.length > 0) && grantResults[0] + grantResults[1] + grantResults[2] + grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                handleStartButton();
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
        }
    }

    private void startCallAlert() {
        dialog.setMessage("Starting call. Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void getThisCallData() {
        Log.e("ConfDetailsFragment", "Getting call data");
        callData = CallData.getInstance(getApplicationContext());
        callData.setCurrentUserId(user.getKid());
        callData.setCurrentUserType(user.getUser_type());
        callData.setRoomName(sharedPref.TAGS);
        new GetCallOpponentsIds(sharedPref.TAGS, callData).execute();
    }

    private void handleStartButton(){
        startConference();
    }

    class GetCallOpponentsIds extends AsyncTask<String, String, Boolean> {

        String roomName;
        CallData callData;

        public GetCallOpponentsIds(String roomName, CallData callData) {
            this.roomName = roomName;
            this.callData = callData;
        }

        @Override
        protected Boolean doInBackground(String... strings) {

            if (callData.getUsersToSubsribe().size() == 0) {
                Log.e("ConfDetailsFragment", "Getting users to subscribe");
                DBHelper db = DBHelper.getInstance(IncomingCallActivity.this);
                ArrayList<ArrayList<Integer>> mainArray = StaticMethods.getPublishersToSubscribe("", db);
                if (mainArray != null && mainArray.size() > 0) {
                    callData.setUsersToSubsribe(mainArray.get(0));
                    callData.setSeeUsers(mainArray.get(1));
                    callData.setHearUsers(mainArray.get(2));
                    callData.setChatUsers(mainArray.get(3));

                    HashMap<Integer, String> longNames = db.getNamesOfUser(sharedPref.TAGS, true);
                    //HashMap<Integer, String> shortNames = db.getNamesOfUser(sharedPref.TAGS, false);
                    callData.setLongNames(longNames);
                    //callData.setShortNames(shortNames);

                    HashMap<Integer, String> dialogIdHashMap = new HashMap<>();
                    for(Integer opponentId : mainArray.get(3)){
                        String dialogId = db.getLastChatDialogID(callData.getCurrentUserId(), opponentId);
                        dialogIdHashMap.put(opponentId, dialogId);
                    }
                    //callData.setOpponentsPrivateDialogs(dialogIdHashMap);
                }
            }
            if (callData.getFirebaseData().size() == 0) {
                Log.e("ConfDetailsFragment", "Getting firebase data");
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean)
                new GetFirebaseDetails(callData).execute();
            else {
                cancel(true);
                handleStartButton();
            }
        }
    }

    class GetFirebaseDetails extends AsyncTask<String, String, String> {

        CallData callData;
        ValueEventListener value;

        public GetFirebaseDetails(CallData callData) {
            this.callData = callData;
        }

        @Override
        protected String doInBackground(String... strings) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS);
            value = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("ConfDetailsFragment", "Firebase callback received");
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    for (DataSnapshot ch : children) {
                        Iterable<DataSnapshot> childrentwo = ch.getChildren();
                        for (DataSnapshot child : childrentwo) {
                            if (child.getKey().equals("image_list")) {
                                Iterable<DataSnapshot> list = child.getChildren();
                                for (DataSnapshot c : list) {
                                    FirebaseVideoModel model = c.getValue(FirebaseVideoModel.class);
                                    if (model != null) {
                                        model.type = 1;
                                        callData.setFirebaseData(ch.getKey(), model);
                                    }
                                }
                            } else if (child.getKey().equals("video_list")) {
                                Iterable<DataSnapshot> list = child.getChildren();
                                for (DataSnapshot c : list) {
                                    FirebaseVideoModel model = c.getValue(FirebaseVideoModel.class);
                                    if (model != null) {
                                        model.type = 2;
                                        callData.setFirebaseData(ch.getKey(), model);
                                    }
                                }
                            }
                        }
                    }
                    databaseRef.removeEventListener(value);
                    handleStartButton();
                    cancel(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ConfDetailsFragment", "Firebase callback error");
                    Toast.makeText(IncomingCallActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
            databaseRef.addValueEventListener(value);

            return null;
        }
    }

}
