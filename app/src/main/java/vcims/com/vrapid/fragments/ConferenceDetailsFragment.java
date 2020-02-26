package vcims.com.vrapid.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kenante.video.core.KenanteSession;
import com.kenante.video.interfaces.SessionEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import vcims.com.vrapid.R;
import vcims.com.vrapid.activities.CallActivity;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.models.FirebaseVideoModel;
import vcims.com.vrapid.models.PPTModel;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.ScheduleModel;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.NewSharedPref;
import vcims.com.vrapid.util.StaticMethods;
import vcims.com.vrapid.util.ThreadExecuter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConferenceDetailsFragment extends Fragment {

    private final String TAG = ConferenceDetailsFragment.class.getSimpleName();
    private final String HIDE_DETAILS = "hide_details", RESP_DETAILS = "resp_details", JOIN_CONFERENCE = "join_conference";
    private final String mURL = "https://vareniacims.com/vcimsweb/uploads/image/kenante_internet_test/internet_test_image.jpg";
    private DBHelper db;
    private KenanteUser currentUser;
    private Toolbar confDetailsToolbar;
    public Button joinConference;
    private LinearLayout viewDetails;
    private View viewLine;
    private TextView checkInternetTV;
    private String time, day, date, actionBarSubtitle = "", roomName = "";
    private LinearLayout openInternetSettings;
    private ProgressDialog progressDialog;
    private Boolean ongoingGroup = false, isSecure = false, handleJoinButtonStarted = false;
    private TextView ageRespDetails, secRespDetails, genderRespDetails, userRespDetails;
    private CallData callData;
    private ValueEventListener value;
    //private FrameLayout internetCheckFL;
    private int mTries = 1, noOfTries = 1, totalSize = 0, roomId = -1;
    private long start = 0, end = 0;
    //private Button checkInternetCross;
    private CheckInternet checkInternet;
    private Handler internetSpeedHandler = new Handler();
    private Runnable internetSpeedRunnable;
    private KenanteSession kenanteSession;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_conference_details, container, false);

        initialize(v);
        parseIntentExtras();
        if (currentUser.getUser_type() != Constants.CLIENT && currentUser.getUser_type() != Constants.ADMIN &&
                currentUser.getUser_type() != Constants.MODERATOR)
            updateUI(HIDE_DETAILS);
        else
            updateUI(RESP_DETAILS);
        updateUI(JOIN_CONFERENCE);
        clickListener();
        initInternetSpeedComponents();

        return v;
    }

    private void updateUI(String type) {
        switch (type) {
            case HIDE_DETAILS:
                viewDetails.setVisibility(View.GONE);
                viewLine.setVisibility(View.GONE);
                break;

            case RESP_DETAILS:
                ScheduleModel model = db.getSchedule(roomName);
                ageRespDetails.setText(model.getAge());
                secRespDetails.setText(model.getSec());
                if (model.getGender().equals("1"))
                    genderRespDetails.setText("Male");
                else if (model.getGender().equals("2"))
                    genderRespDetails.setText("Female");
                else if (model.getGender().equals("3"))
                    genderRespDetails.setText("Both");
                userRespDetails.setText(model.getUsership());
                break;

            case JOIN_CONFERENCE:
                if (!ongoingGroup)
                    joinConference.setVisibility(View.GONE);
                break;
        }
    }

    private void initialize(View view) {
        db = DBHelper.getInstance(getActivity().getApplicationContext());
        NewSharedPref.INSTANCE.GetSharedPreferences(getActivity().getApplicationContext());
        ageRespDetails = view.findViewById(R.id.ageRespDetails);
        secRespDetails = view.findViewById(R.id.secRespDetails);
        genderRespDetails = view.findViewById(R.id.genderRespDetails);
        userRespDetails = view.findViewById(R.id.userRespDetails);
        confDetailsToolbar = view.findViewById(R.id.confDetailsToolbar);
        joinConference = view.findViewById(R.id.joinConference);
        viewDetails = view.findViewById(R.id.viewDetails);
        viewLine = view.findViewById(R.id.viewLine);
        openInternetSettings = view.findViewById(R.id.openInternetSettings);
        progressDialog = new ProgressDialog(getActivity());
        callData = CallData.getInstance(getActivity().getApplicationContext());
        //internetCheckFL = view.findViewById(R.id.internetCheckFL);
        checkInternet = new CheckInternet(mURL);
        //checkInternetCross = (Button) internetCheckFL.getChildAt(1);
        checkInternetTV = view.findViewById(R.id.checkInternetTV);
        roomName =  NewSharedPref.INSTANCE.getStringValue(Constants.ROOM);
        roomId = NewSharedPref.INSTANCE.getIntValue(Constants.ROOM_ID);
        currentUser =  NewSharedPref.INSTANCE.getCurrentUser();
        kenanteSession = KenanteSession.Companion.getInstance();
    }

    private void parseIntentExtras() {
        Bundle bundle = getArguments();
        time = bundle.getString(Constants.EXTRA_TIME);
        day = bundle.getString(Constants.EXTRA_DAY);
        date = bundle.getString(Constants.EXTRA_DATE);
        ongoingGroup = bundle.getBoolean(Constants.EXTRA_GROUP_ONGOING);
        isSecure = bundle.getBoolean(Constants.EXTRA_IS_ROOM_SECURE);
        //chatDialogIds = bundle.getStringArrayList(Constants.EXTRA_CHAT_DIALOG_ID);
        actionBarSubtitle = time + ", " + day + " " + date;
        initActionBar();
    }

    private void initActionBar() {
        confDetailsToolbar.setTitle(roomName);
        confDetailsToolbar.setSubtitle(actionBarSubtitle);
    }

    private void clickListener() {
        confDetailsToolbar.setNavigationOnClickListener(v -> closeThisFragment());

        joinConference.setOnClickListener(v -> {
            if (StaticMethods.isNetworkAvailable(getActivity())) {
                if (hasPermission()) {
                    internetSpeedHandler.removeCallbacks(internetSpeedRunnable);
                    //checkInternetCross.performClick();
                    stopCalculatingInternetSpeed();
                    if (!checkInternet.isCancelled()) {
                        checkInternet.cancel(true);
                    }
                    startCallAlert();
                    getThisCallData();
                }
            } else
                Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        });

        openInternetSettings.setOnClickListener(v -> {
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        /*checkInternetCross.setOnClickListener(view -> {
            start = 0;
            end = 0;
            mTries = 1;
            internetSpeedHandler.removeCallbacks(internetSpeedRunnable);
            checkInternet.cancel(true);
            int startVal = internetCheckFL.getHeight();
            ValueAnimator valueAnimator = ValueAnimator.ofInt(startVal, 0);
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (int) valueAnimator.getAnimatedValue();
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, val);
                    params.gravity = Gravity.BOTTOM;
                    internetCheckFL.setLayoutParams(params);
                }
            });
            valueAnimator.start();
        });*/

    }

    private void initInternetSpeedComponents() {
        internetSpeedRunnable = () -> {
            if (!checkInternet.isCancelled()) {
                //TextView tv = (TextView) ((LinearLayout) internetCheckFL.getChildAt(0)).getChildAt(1);
                checkInternetTV.setText(checkInternetTV.getText() + "\n(Slow)");
            }
        };
        internetSpeedHandler.postDelayed(internetSpeedRunnable, 10300);
        checkInternetTV.setText("Checking Internet connection...");
        checkInternet.execute();
    }

    private void getThisCallData() {

        callData.removeAllElements();
        callData.setCurrentUserId(currentUser.getKid());
        callData.setCurrentUserType(currentUser.getUser_type());
        callData.setCurrentUserName(currentUser.getDname());
        callData.setRoomName(roomName);


        Runnable callOpponentDataRunnable = this::getCallOpponentData;
        Runnable getFirebaseDataRunnable = this::getFirebaseData;

        Thread thread1 = new Thread();
        ThreadExecuter.runButNotOn(callOpponentDataRunnable, thread1);
        Thread thread2 = new Thread();
        ThreadExecuter.runButNotOn(getFirebaseDataRunnable, thread2);
    }

    private void startCall(){

        kenanteSession.createSession(currentUser.getKid(), new SessionEventListener() {
            @Override
            public void onSuccess(@NotNull String s) {
                progressDialog.cancel();
                //kenanteSession.initCall(roomId, opponentsIds);
                handleJoinButtonStarted = false;
                CallActivity.Companion.start(getActivity());
            }

            @Override
            public void onError(@NotNull String s) {
                handleJoinButtonStarted = false;
                progressDialog.cancel();
            }
        });
    }

    /*private void startCall(String dialogId, List<Integer> occupants) {
        Log.i(TAG, "Creating session");
        ConferenceClient client = ConferenceClient.getInstance(getActivity());
        QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        client.createSession(currentUser.getQb_id(), conferenceType, new ConferenceEntityCallback<ConferenceSession>() {
            @Override
            public void onSuccess(ConferenceSession session) {
                Log.i(TAG, "Session created successfully");
                webRtcSessionManager.setCurrentSession(session);
                progressDialog.cancel();
                Log.i(TAG, "Starting call activity");
                handleJoinButtonStarted = false;
                CallActivity.start(getActivity(), dialogId, occupants.size(), isSecure, false);
                //db.insertAttendedHistory(sharedPref.TAGS);
            }

            @Override
            public void onError(WsException responseException) {
                progressDialog.cancel();
                Toast.makeText(getActivity(), responseException.getMessage(), Toast.LENGTH_SHORT).show();
                handleJoinButtonStarted = false;
            }
        });

    }*/

    private void closeThisFragment() {
        getFragmentManager().popBackStackImmediate();
    }

    private void startCallAlert() {
        progressDialog.setMessage(getString(R.string.starting_call_dialog));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void handleJoinButton() {
        if(handleJoinButtonStarted)
            return;
        Log.i(TAG, "Handling join button");
        handleJoinButtonStarted = true;
        startCall();
    }

    private Boolean hasPermission() {
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
        };
        if (StaticMethods.hasPermissions(getActivity(), PERMISSIONS))
            return true;
        else
            StaticMethods.askPermission(getActivity(), PERMISSIONS);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*if (requestCode == Constants.REQUEST_PERMISSION_KEY) {
            Boolean isPermissionGranted = false;
            for(int i = 0; i < grantResults.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    isPermissionGranted = true;
                else {
                    isPermissionGranted = false;
                    break;
                }
            }
            if (isPermissionGranted) {
                handleJoinButton();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void getCallOpponentData() {
        Log.i(TAG, "Getting call opponent data");
        if (callData.getUsersToSubsribe().size() == 0) {
            DBHelper db = DBHelper.getInstance(getActivity());
            ArrayList<ArrayList<Integer>> mainArray = StaticMethods.getPublishersToSubscribe(roomName, db);
            if (mainArray != null && mainArray.size() > 0) {
                callData.setUsersToSubsribe(mainArray.get(0));
                callData.setSeeUsers(mainArray.get(1));
                callData.setHearUsers(mainArray.get(2));
                callData.setChatUsers(mainArray.get(3));
                callData.setTalkUsers(mainArray.get(4));

                HashMap<String, String> audioVideoStatus = db.getAudioVideoStatus(roomName);
                HashMap<Integer, String> longNames = db.getNamesOfUser(roomName, true);
                //HashMap<Integer, String> shortNames = db.getNamesOfUser(sharedPref.TAGS, false);
                callData.setAudioVideoStatus(audioVideoStatus);
                callData.setLongNames(longNames);
                //callData.setShortNames(shortNames);

                /*//Chat Dialog Ids History Status
                HashMap<String, Boolean> chatDialogIdsHistoryStatus = new HashMap<>();
                for(int i = 0; i < chatDialogIds.size(); i++){
                    chatDialogIdsHistoryStatus.put(chatDialogIds.get(i), false);
                }
                callData.setChatDialogIdsHistoryStatus(chatDialogIdsHistoryStatus);

                HashMap<Integer, String> dialogIdHashMap = new HashMap<>();
                for (Integer opponentId : mainArray.get(3)) {
                    String dialogId = db.getLastChatDialogID(callData.getCurrentUserId(), opponentId);
                    dialogIdHashMap.put(opponentId, dialogId);
                }
                callData.setOpponentsPrivateDialogs(dialogIdHashMap);*/

                //Adding all users type
                HashMap<Integer, Integer> allUsersType = new HashMap<>();
                for (int i = 0; i < mainArray.get(0).size(); i++) {
                    int type = db.getUserType(mainArray.get(0).get(i));
                    if (type == Constants.TRANSLATOR)
                        callData.setTranslatorPresent(true);
                    allUsersType.put(mainArray.get(0).get(i), type);
                }
                callData.setAllUsersType(allUsersType);
            }
        }
        Log.i(TAG, "Got all call opponent data");
        /*if (callData.getFirebaseData().size() == 0) {

        }*/
    }

    private void getFirebaseData() {
        Log.i(TAG, "Getting firebase data");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(roomName);
        value = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "Got firebase data response");
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                        if (child.getKey().equals("image_list")) {
                            Iterable<DataSnapshot> list = child.getChildren();
                            for (DataSnapshot c : list) {
                                FirebaseVideoModel model = c.getValue(FirebaseVideoModel.class);
                                if (model != null) {
                                    model.type = 1;
                                    callData.setFirebaseData(roomName, model);
                                }
                            }
                        } else if (child.getKey().equals("video_list")) {
                            Iterable<DataSnapshot> list = child.getChildren();
                            for (DataSnapshot c : list) {
                                FirebaseVideoModel model = c.getValue(FirebaseVideoModel.class);
                                if (model != null) {
                                    model.type = 2;
                                    callData.setFirebaseData(roomName, model);
                                }
                            }
                        }
                        else if(child.getKey().equals("ppt_list")){
                            Iterable<DataSnapshot> list = child.getChildren();
                            for (DataSnapshot c1 : list){
                                ArrayList<PPTModel> ppt = new ArrayList<>();
                                for (DataSnapshot c2 : c1.getChildren()){
                                   try {
                                       PPTModel model = c2.getValue(PPTModel.class);
                                       if (model != null) {
                                           ppt.add(model);
                                       }
                                   }
                                   catch (DatabaseException e){
                                       e.printStackTrace();
                                   }
                                }
                                if(ppt.size()>0) {
                                    FirebaseVideoModel model = new FirebaseVideoModel();
                                    model.type = 3;
                                    model.name = c1.getKey();
                                    model.ppt = ppt;
                                    callData.setFirebaseData(roomName, model);
                                }
                            }
                        }
                }
                Log.i(TAG, "Got all firebase data");
                databaseRef.removeEventListener(value);
                handleJoinButton();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ConfDetailsFragment", "Firebase callback error");
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseRef.addValueEventListener(value);
    }

    private void calculateInternetSpeed() {
        /*ProgressBar pg = (ProgressBar) ((LinearLayout) internetCheckFL.getChildAt(0)).getChildAt(0);
        pg.setVisibility(View.GONE);
        TextView tv = (TextView) ((LinearLayout) internetCheckFL.getChildAt(0)).getChildAt(1);*/
        float timeDiff = (end - start) / 1000;
        if (timeDiff == 0)
            timeDiff = 1;
        float speed = (totalSize * 8) / timeDiff;
        speed /= 1024;
        /*Log.e("TEST", "Total size: " + String.valueOf(totalSize));
        Log.e("TEST", "Speed: " + String.valueOf(speed));*/

        String speedValue = "";
        //if (speed > 1024) {
            float newVal = speed/1024;
            DecimalFormat format = new DecimalFormat("0.00");
            newVal = Float.parseFloat(format.format(newVal));
            speedValue = newVal + " Mbps";
            if(newVal <= 3.5)
            checkInternetTV.setText("Your Internet speed is " + speedValue + " and is poor for the call.");
            else if(newVal > 3.5 && newVal <= 5.5)
                checkInternetTV.setText("Your Internet speed is " + speedValue + " and is good for the call.");
            else if(newVal > 5.5 && newVal <= 9.5)
                checkInternetTV.setText("Your Internet speed is " + speedValue + " and is very good for the call.");
            else if(newVal > 9.5)
                checkInternetTV.setText("Your Internet speed is " + speedValue + " and is excellent for the call.");
        /*} else {
            float newVal = speed;
            DecimalFormat format = new DecimalFormat("0.00");
            newVal = Float.parseFloat(format.format(newVal));
            speedValue = newVal + " Kbps";
            checkInternetTV.setText("Your Internet speed is " + speedValue + " and is poor for the call.");
            if (speed < 150) {
                checkInternetTV.setText("Your Internet speed is " + speedValue + " and is not recommended for the call.");
            }else if (speed > 150 && speed < 400) {
                checkInternetTV.setText("Your Internet speed is " + speedValue + " and is average for the call.");
            }else {
                checkInternetTV.setText("Your Internet speed is " + speedValue + " and is good for the call.");
            }
        }*/
        start = 0;
        end = 0;
        mTries = 1;
        internetSpeedHandler.removeCallbacks(internetSpeedRunnable);
    }

    private void stopCalculatingInternetSpeed(){
        start = 0;
        end = 0;
        mTries = 1;
        internetSpeedHandler.removeCallbacks(internetSpeedRunnable);
        checkInternet.cancel(true);
    }

    private class CheckInternet extends AsyncTask<String, Void, String> {

        private String mURL;

        private CheckInternet(String mURL) {
            this.mURL = mURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (start == 0) {
                start = System.currentTimeMillis();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URLConnection connection = new URL(mURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                if (mTries == noOfTries) {
                    totalSize = connection.getContentLength() * noOfTries;
                    //Log.e(TAG, connection.getContentLength() + "");
                }
                try {
                    byte[] buffer = new byte[1024];
                    while (inputStream.read(buffer) != -1) {
                        //Log.e(TAG, String.valueOf(inputStream.read(buffer)));
                    }
                } finally {
                    inputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mTries != noOfTries) {
                mTries++;
                checkInternet = new CheckInternet(mURL);
                checkInternet.execute();
            } else {
                end = System.currentTimeMillis();
                checkInternet.cancel(true);
                calculateInternetSpeed();
            }
        }
    }

    private void getLogDetails(){
        String device = Build.MANUFACTURER + " " + Build.MODEL;
        Log.i(TAG, "Device: " + device);
        TelephonyManager telephonyManager =((TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE));
        Log.i(TAG, "Operator: " +  telephonyManager.getNetworkOperatorName());
        String mobileInternetState = getMobileInternetState(telephonyManager.getDataState());
        Log.i(TAG, "Mobile Internet State: " + mobileInternetState);
        String mobileNetworkType = getMobileNetworkType(telephonyManager.getNetworkType());
        Log.i(TAG, "Mobile Network Type: " + mobileNetworkType);
    }

    private String getMobileInternetState(int state){
        String stateToReturn = "";
        switch (state) {
            case TelephonyManager.DATA_DISCONNECTED:
                stateToReturn = "DATA_DISCONNECTED";
                break;
            case TelephonyManager.DATA_CONNECTING:
                stateToReturn = "DATA_CONNECTING";
                break;
            case TelephonyManager.DATA_CONNECTED:
                stateToReturn = "DATA_CONNECTED";
                break;
            case TelephonyManager.DATA_SUSPENDED:
                stateToReturn = "DATA_SUSPENDED";
                break;
            default:
                stateToReturn = "UNKNOWN";
                break;
        }
        return stateToReturn;
    }

    private String getMobileNetworkType(int networkType){
        String typeToReturn = "";
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
                typeToReturn = "NETWORK_TYPE_CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                typeToReturn = "NETWORK_TYPE_EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                typeToReturn = "NETWORK_TYPE_EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                typeToReturn = "NETWORK_TYPE_GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                typeToReturn = "NETWORK_TYPE_HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                typeToReturn = "NETWORK_TYPE_HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                typeToReturn = "NETWORK_TYPE_IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                typeToReturn = "NETWORK_TYPE_LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                typeToReturn = "NETWORK_TYPE_UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                typeToReturn = "NETWORK_TYPE_UNKNOWN";
                break;
            default:
                typeToReturn = "Undefined Network: " + networkType;
                break;
        }
        return typeToReturn;
    }

}
