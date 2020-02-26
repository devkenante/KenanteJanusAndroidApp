package vcims.com.vrapid.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.models.FirebaseVideoModel;
import vcims.com.vrapid.models.PPTModel;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.RuleModel;
import vcims.com.vrapid.models.ScheduleModel;
import vcims.com.vrapid.models.UserId;
import vcims.com.vrapid.retrofit.Communicator;
import vcims.com.vrapid.retrofit.RetrofitInterface;
import vcims.com.vrapid.retrofit.serverresponse.SyncUsersSR;
import vcims.com.vrapid.retrofit.serverresponse.UserLoginSR;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.NetworkConnectionCheck;
import vcims.com.vrapid.util.SharedPref;
import vcims.com.vrapid.util.StaticMethods;
import vcims.com.vrapid.util.ThreadExecuter;

import static vcims.com.vrapid.util.StaticMethods.getImei;

public class SetupCallWithLink extends AppCompatActivity implements NetworkConnectionCheck.OnConnectivityChangedListener {

    private static final String TAG = SetupCallWithLink.class.getSimpleName();
    private final int START = 1, VERIFIED = 2;
    private final int NO_PERMISSION = 1, INIT_CALL_INTERRUPTED = 2, INIT_CALL = 3;
    private String mobileNumber = "", password = "", dialogId = "", roomName = "", scheduleFlag = "", fcmId = "", imei = "", url = "";
    private int callId = 0;
    //Action flag - 1 (permission) 2 (internet) 3 (retry call)
    private int actionFlag = 0;
    private Boolean isInitCallOngoing = false, permissionsGiven = false, handleJoinButtonStarted = false;

    //Views
    private LinearLayout dynamicLinkStatusLL;
    private FrameLayout dynamicLinkPermissionFL;
    private TextView dynamicLinkStatusTV, dynamicLinkUserNameTV, actionButton, actionText;
    private ProgressBar progressBar;

    //Components
    private SharedPref sharedPref;
    private DBHelper db;
    private KenanteUser currentUser;
    private CallData callData;
    private ValueEventListener value;
    private BroadcastReceiver receiver;
    private NetworkConnectionCheck networkConnectionCheck;
    private Uri callUri = null;

    //Permissions
    private String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    public static void start(Context context, Uri dynamicUri) {
        Intent intent = new Intent(context, SetupCallWithLink.class);
        intent.putExtra(Constants.EXTRA_DYNAMIC_LINK_URI, dynamicUri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        doReceivingWorkHere();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkConnectionCheck.registerListener(this);
        }
        if (StaticMethods.hasPermissions(this, PERMISSIONS))
            permissionsGiven = true;
        else
            permissionsGiven = false;

        if (permissionsGiven) {
            if (actionFlag == INIT_CALL_INTERRUPTED && !isInitCallOngoing) {
                if (StaticMethods.isNetworkAvailable(this)) {
                    isInitCallOngoing = true;
                    runOnUiThread(() -> handleUI(INIT_CALL));
                    initComponents(START);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkConnectionCheck.unregisterListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_call_with_link);

        getIntentExtras();
        initViews();
        if (StaticMethods.hasPermissions(this, PERMISSIONS)) {
            permissionsGiven = true;
            if (StaticMethods.isNetworkAvailable(this)) {
                runOnUiThread(() -> {
                    isInitCallOngoing = true;
                    handleUI(INIT_CALL);
                    initComponents(START);
                });

            } else
                runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
        } else {
            runOnUiThread(() -> handleUI(NO_PERMISSION));
            StaticMethods.askPermission(this, PERMISSIONS);
        }
        clickListener();

    }

    private void initComponents(int type) {
        switch (type) {
            case START:
                isInitCallOngoing = true;
                db = DBHelper.getInstance(this);
                sharedPref = SharedPref.getInstance();
                sharedPref.LOGIN_STATUS = false;
                sharedPref.updateSetting();
                db.deleteAllSchedule();
                db.deleteAllRules();
                if (mobileNumber.equals("") && password.equals("") && callId == 0)
                    getFirebaseDynamicLinkValue();
                else {
                    progressBar.setProgress(1);
                    initComponents(VERIFIED);
                    new StartLogin(url, mobileNumber, password, imei, fcmId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                break;

            case VERIFIED:
                initRequiredData();
                break;
        }
    }

    private void getIntentExtras() {
        callUri = getIntent().getParcelableExtra(Constants.EXTRA_DYNAMIC_LINK_URI);
    }

    private void initViews() {
        dynamicLinkStatusLL = findViewById(R.id.dynamicLinkStatusLL);
        dynamicLinkPermissionFL = findViewById(R.id.dynamicLinkPermissionFL);
        dynamicLinkUserNameTV = findViewById(R.id.dynamicLinkUserNameTV);
        dynamicLinkStatusTV = dynamicLinkStatusLL.findViewWithTag("statusTV");
        actionText = dynamicLinkPermissionFL.findViewWithTag("actionText");
        actionButton = dynamicLinkPermissionFL.findViewWithTag("actionButton");
        progressBar = dynamicLinkStatusLL.findViewWithTag("progressBar");
        progressBar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkConnectionCheck = new NetworkConnectionCheck(getApplication());
        }
    }

    private void clickListener() {
        actionButton.setOnClickListener(view -> {
            if (actionFlag == NO_PERMISSION)
                StaticMethods.askPermission(SetupCallWithLink.this, PERMISSIONS);
            else if (actionFlag == INIT_CALL_INTERRUPTED) {
                if (StaticMethods.hasPermissions(this, PERMISSIONS)) {
                    permissionsGiven = true;
                    if (StaticMethods.isNetworkAvailable(this)) {
                        if (!isInitCallOngoing) {
                            runOnUiThread(() -> {
                                isInitCallOngoing = true;
                                handleUI(INIT_CALL);
                                initComponents(START);
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            handleUI(INIT_CALL_INTERRUPTED);
                            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        });
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                } else {
                    runOnUiThread(() -> handleUI(NO_PERMISSION));
                    StaticMethods.askPermission(this, PERMISSIONS);
                }
            }
        });
    }

    private void handleUI(int type) {
        switch (type) {
            case NO_PERMISSION:

                actionFlag = NO_PERMISSION;
                isInitCallOngoing = false;
                dynamicLinkPermissionFL.setVisibility(View.VISIBLE);
                dynamicLinkStatusTV.setText("You don't have the required permissions to start this call.");
                actionText.setText("Please allow permissions");
                actionButton.setText("Allow Permission");
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
                dynamicLinkUserNameTV.setText("");

                break;

            case INIT_CALL_INTERRUPTED:

                actionFlag = INIT_CALL_INTERRUPTED;
                isInitCallOngoing = false;
                dynamicLinkPermissionFL.setVisibility(View.VISIBLE);
                dynamicLinkStatusTV.setText("Some error occurred while connecting your call. Make sure you are connected to the Internet.");
                actionText.setText("Call Interrupted");
                actionButton.setText("Try Again");
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
                dynamicLinkUserNameTV.setText("");

                break;

            case INIT_CALL:

                actionFlag = INIT_CALL;
                dynamicLinkPermissionFL.setVisibility(View.GONE);
                actionText.setText("");
                actionButton.setText("");

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                dynamicLinkStatusTV.setText(getString(R.string.loading));

                break;

        }
    }

    private void getFirebaseDynamicLinkValue() {

        FirebaseDynamicLinks.getInstance().getDynamicLink(callUri).addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {

                Uri deepLink = null;
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.getLink();
                    String value = deepLink.toString().split("/")[5];
                    byte[] finalval = Base64.decode(value, Base64.DEFAULT);
                    String[] rawData = new String(finalval, StandardCharsets.UTF_8).split("-");
                    mobileNumber = rawData[0];
                    password = rawData[1];
                    callId = Integer.valueOf(rawData[2]);
                    progressBar.setProgress(1);
                    initComponents(VERIFIED);
                    new StartLogin(url, mobileNumber, password, imei, fcmId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
            }
        });
    }

    private void initRequiredData() {
        url = Constants.LOGIN_URL;
        fcmId = FirebaseInstanceId.getInstance().getToken();
        imei = getImei(SetupCallWithLink.this);
        callData = CallData.getInstance(getApplicationContext());
    }

    private void getCurrentUserDetails() {
        //Store the details of the current user in Shared Preferences.
        KenanteUser currentUser = db.getUserByUID(sharedPref.USER_ID);
        sharedPref.QB_ID = currentUser.getKid();
        sharedPref.LOGIN = currentUser.getLogin();
        sharedPref.PASSWORD = currentUser.getPassword();
        sharedPref.FULLNAME = currentUser.getName();
        dynamicLinkUserNameTV.setText(currentUser.getName());
        sharedPref.EMAIL = currentUser.getEmail();
        sharedPref.USER_TYPE = currentUser.getUser_type();
        sharedPref.LAST_SIGN_IN = currentUser.getLast_sign_in();
        sharedPref.CREATED_AT = currentUser.getCreated_at();
        sharedPref.updateSetting();
    }

    private void doReceivingWorkHere() {
        IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.MAIN");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (SetupCallWithLink.this.getPackageName().equals(intent.getStringExtra("android.intent.action.MAIN"))) {
                    Boolean status = intent.getBooleanExtra("status", false);
                    if (status) {
                        //subscribeUserForNotifications();
                        progressBar.setProgress(4);

                        sharedPref.LOGIN_STATUS = true;
                        sharedPref.TAGS = roomName;
                        sharedPref.updateSetting();

                        //new StoreNames(sharedPref.USER_ID, roomName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    else
                        runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
                }
            }
        };
        registerReceiver(receiver, intentFilter);
    }

    /*private void subscribeUserForNotifications() {
        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(QBEnvironment.PRODUCTION);
        String deviceId = getDeviceId();
        subscription.setDeviceUdid(deviceId);
        String regId = FirebaseInstanceId.getInstance().getToken();
        subscription.setRegistrationID(regId);
        QBPushNotifications.createSubscription(subscription).performAsync(new QBEntityCallback<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> qbSubscriptions, Bundle bundle) {
                for (QBSubscription subscription : qbSubscriptions) {
                    if (subscription.getDevice().getId() != null)
                        if (subscription.getDevice().getId().equals(deviceId))
                            sharedPref.SUBSCRIPTION_ID = subscription.getId().toString();
                }
                //Store names of users in database according to their usertype
                progressBar.setProgress(5);
                new StoreNames(sharedPref.USER_ID, roomName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onError(QBResponseException e) {
                runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
            }
        });
    }*/

    private void getThisCallData() {
        Log.e("ConfDetailsFragment", "Getting call data");
        callData.removeAllElements();
        callData.setCurrentUserId(currentUser.getKid());
        callData.setCurrentUserType(currentUser.getUser_type());
        callData.setRoomName(sharedPref.TAGS);

        Runnable callOpponentDataRunnable = this::getCallOpponentData;
        Runnable getFirebaseDataRunnable = this::getFirebaseData;

        Thread thread1 = new Thread();
        ThreadExecuter.runButNotOn(callOpponentDataRunnable, thread1);
        Thread thread2 = new Thread();
        ThreadExecuter.runButNotOn(getFirebaseDataRunnable, thread2);
    }

    private void getCallOpponentData() {
        Log.i(TAG, "Getting call opponent data");
        if (callData.getUsersToSubsribe().size() == 0) {
            DBHelper db = DBHelper.getInstance(this);
            ArrayList<ArrayList<Integer>> mainArray = StaticMethods.getPublishersToSubscribe("", db);
            if (mainArray != null && mainArray.size() > 0) {
                callData.setUsersToSubsribe(mainArray.get(0));
                callData.setSeeUsers(mainArray.get(1));
                callData.setHearUsers(mainArray.get(2));
                callData.setChatUsers(mainArray.get(3));
                callData.setTalkUsers(mainArray.get(4));

                HashMap<String, String> audioVideoStatus = db.getAudioVideoStatus(sharedPref.TAGS);
                HashMap<Integer, String> longNames = db.getNamesOfUser(sharedPref.TAGS, true);
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
        progressBar.setProgress(6);
        /*if (callData.getFirebaseData().size() == 0) {

        }*/
    }

    private void getFirebaseData() {
        Log.i(TAG, "Getting firebase data");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(sharedPref.TAGS);
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
                                callData.setFirebaseData(sharedPref.TAGS, model);
                            }
                        }
                    } else if (child.getKey().equals("video_list")) {
                        Iterable<DataSnapshot> list = child.getChildren();
                        for (DataSnapshot c : list) {
                            FirebaseVideoModel model = c.getValue(FirebaseVideoModel.class);
                            if (model != null) {
                                model.type = 2;
                                callData.setFirebaseData(sharedPref.TAGS, model);
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
                                callData.setFirebaseData(sharedPref.TAGS, model);
                            }
                        }
                    }
                }
                Log.i(TAG, "Got all firebase data");
                databaseRef.removeEventListener(value);
                progressBar.setProgress(7);
                handleJoinButton();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ConfDetailsFragment", "Firebase callback error");
                Toast.makeText(SetupCallWithLink.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseRef.addValueEventListener(value);
    }

    private void handleJoinButton() {
        if(handleJoinButtonStarted)
            return;
        Log.i(TAG, "Handling join button");
        handleJoinButtonStarted = true;
        startCall();
    }

    private void startCall(){

    }

    /*private void startCall(String dialogId) {
        ConferenceClient client = ConferenceClient.getInstance(SetupCallWithLink.this);
        QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        client.createSession(currentUser.getQb_id(), conferenceType, new ConferenceEntityCallback<ConferenceSession>() {
            @Override
            public void onSuccess(ConferenceSession session) {
                progressBar.setProgress(8);
                runOnUiThread(() -> {
                    dynamicLinkStatusTV.setText("Completed");
                    //progressBar.setVisibility(View.GONE);
                });
                webRtcSessionManager.setCurrentSession(session);
                db.insertAttendedHistory(sharedPref.TAGS);
                Boolean isSecure = scheduleFlag.equals("1");
                CallActivity.start(SetupCallWithLink.this, dialogId, 5, isSecure, true);
                finish();
            }

            @Override
            public void onError(WsException responseException) {
                runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
            }
        });
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Boolean allPermissionsGranted = true;
        if (requestCode == Constants.REQUEST_PERMISSION_KEY) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    allPermissionsGranted = false;
            }
        }
        if (!allPermissionsGranted) {
            handleUI(NO_PERMISSION);
            permissionsGiven = false;
        } else {
            permissionsGiven = true;
            dynamicLinkPermissionFL.setVisibility(View.GONE);
            if (StaticMethods.isNetworkAvailable(this)) {
                runOnUiThread(() -> {
                    isInitCallOngoing = true;
                    handleUI(INIT_CALL);
                    initComponents(START);
                });

            } else
                runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
        }
    }

    @Override
    public void onAvailable() {
        if (permissionsGiven)
            if (actionFlag == INIT_CALL_INTERRUPTED && !isInitCallOngoing) {
                isInitCallOngoing = true;
                runOnUiThread(() -> {
                    isInitCallOngoing = true;
                    handleUI(INIT_CALL);
                    initComponents(START);
                });

            }
    }

    @Override
    public void onLosing(int maxMsToLive) {

    }

    @Override
    public void onLost() {
        if (isInitCallOngoing) {
            isInitCallOngoing = false;
            runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
        }
    }

    @Override
    public void onUnavailable() {

    }

    public class StartLogin extends AsyncTask<String, String, String> {

        String url, mobile, password, imei, fcmId;

        public StartLogin(String url, String mobile, String password, String imei, String fcmId) {
            this.url = url;
            this.mobile = mobile;
            this.password = password;
            this.imei = imei;
            this.fcmId = fcmId;
        }

        @Override
        protected String doInBackground(String... strings) {

            Communicator communicator = new Communicator();
            RetrofitInterface service = communicator.initialization();
            Call<UserLoginSR> call = service.loginUser(mobile, password, imei, fcmId);
            call.enqueue(new Callback<UserLoginSR>() {
                @Override
                public void onResponse(Call<UserLoginSR> call, Response<UserLoginSR> response) {

                    /*On success of login
                    1. Get details of current user.
                    2. Get details of all the other users present in same room.*/

                    int status = response.body().getStatus();
                    if (status == 1) {
                        //Login Successful
                        UserId userId = response.body().getUserId();
                        String current_user_id = userId.getUser_id();
                        sharedPref.USER_ID = current_user_id;
                        sharedPref.updateSetting();

                        //ConnectActivity.start(instance);
                        //Handle all the work of ConnectActivity on LoginActivity
                        progressBar.setProgress(2);
                        new SyncUsers(sharedPref.USER_ID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    } else if (status == 2) {
                        Toast.makeText(SetupCallWithLink.this, getString(R.string.already_logged_in), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SetupCallWithLink.this, getString(R.string.failed_login_error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserLoginSR> call, Throwable t) {
                    runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
                }
            });

            return "";
        }
    }

    public class SyncUsers extends AsyncTask<String, String, String> {

        String id;

        public SyncUsers(String id) {
            this.id = id;
        }

        @Override
        protected String doInBackground(String... strings) {

            Communicator communicator = new Communicator();
            RetrofitInterface service = communicator.initialization();
            Call<SyncUsersSR> call = service.syncUsers(id);
            call.enqueue(new Callback<SyncUsersSR>() {
                @Override
                public void onResponse(Call<SyncUsersSR> call, Response<SyncUsersSR> response) {

                    if (response.body() == null)
                        return;
                    int status = response.body().getStatus();
                    if (status == 1) {
                        //Success in getting list.
                        //Store all the users in database.
                        ArrayList<KenanteUser> users = response.body().getResponse();
                        db = DBHelper.getInstance(getApplicationContext());
                        if (users != null) {
                            if (users.size() != 0) {
                                db.insertUsers(users);
                            }
                        }
                        //Now get details of current user and store it in shared preferences.

                        ArrayList<RuleModel> model = response.body().getRuleModel();

                        //Get Rule and store it in database
                        for (int i = 0; i < model.size(); i++) {
                            ArrayList<Integer> see = model.get(i).getSee();
                            ArrayList<Integer> talk = model.get(i).getTalk();
                            ArrayList<Integer> hear = model.get(i).getHear();
                            ArrayList<Integer> chat = model.get(i).getChat();
                            ScheduleModel schedule = model.get(i).getRoom_schedule();
                            int selfVideoOff = model.get(i).getSelf_video_off();
                            int selfAudioOff = model.get(i).getSelf_audio_off();
                            //Storing rule in database.
                            db.storeRule(schedule.getRoom(), see, talk, hear, chat, selfVideoOff, selfAudioOff);
                            db.insertSchedule(schedule);
                            if (model.get(i).getRoom_schedule().getId() == callId) {
                                roomName = model.get(i).getRoom_schedule().getRoom();
                                scheduleFlag = model.get(i).getRoom_schedule().getSecureFlag();
                            }
                        }
                        getCurrentUserDetails();
                        progressBar.setProgress(3);
                    }
                }

                @Override
                public void onFailure(Call<SyncUsersSR> call, Throwable t) {
                    runOnUiThread(() -> handleUI(INIT_CALL_INTERRUPTED));
                }
            });

            return "";
        }
    }

}
