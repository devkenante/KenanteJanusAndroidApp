package vcims.com.vrapid.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.RuleModel;
import vcims.com.vrapid.models.ScheduleModel;
import vcims.com.vrapid.models.UserId;
import vcims.com.vrapid.retrofit.Communicator;
import vcims.com.vrapid.retrofit.RetrofitInterface;
import vcims.com.vrapid.retrofit.serverresponse.SyncUsersSR;
import vcims.com.vrapid.retrofit.serverresponse.UserLoginSR;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.NewSharedPref;
import vcims.com.vrapid.util.StaticMethods;

import static vcims.com.vrapid.util.StaticMethods.askPermission;
import static vcims.com.vrapid.util.StaticMethods.getImei;
import static vcims.com.vrapid.util.StaticMethods.hasPermissions;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private EditText mobileLoginET, passLoginET;
    private View.OnClickListener onClickListener;
    private Button loginImageButton;
    private ProgressDialog dialog;
    private DBHelper db;
    private ImageView termsAndConditionsIV;
    private Boolean termsAndConditionsAccepted = false;
    private TextView termsAndConditionsTV, contactUsTV;
    private String[] PERMISSIONS = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    private String url = "", gcmId = "", imei = "";
    private ImageView mobileIcon, passwordIcon;
    private View mobViewLine, passViewLine;
    //Design UI Views
    private FrameLayout loginMainFL;
    private LinearLayout loginTopLL, loginMiddleLL, loginMobileLL, loginPasswordLL, loginBottomLL, loginButtonLL;
    private ImageView loginTopIV;
    private TextView loginTopTV;
    //Designing components
    private int totalWidth = 0, totalHeight = 0, mobPassHeight = 0;

    public static void start(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //instance = LoginActivity.this;
        if (db == null)
            db = DBHelper.getInstance(getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Method calling
        initViewsAndComponents();
        clickListener();
        changeEditTextStyleOnFocus();

    }

    private void initViewsAndComponents() {
        // Getting ids of views and initializing components
        //instance = LoginActivity.this;
        mobileLoginET = findViewById(R.id.mobileLoginET);
        passLoginET = findViewById(R.id.passLoginET);
        loginImageButton = findViewById(R.id.loginImageButton);
        termsAndConditionsIV = findViewById(R.id.termsAndConditionsIV);
        termsAndConditionsTV = findViewById(R.id.termsAndConditionsTV);
        contactUsTV = findViewById(R.id.contactUsTV);
        mobileIcon = findViewById(R.id.mobileIcon);
        passwordIcon = findViewById(R.id.passwordIcon);
        mobViewLine = findViewById(R.id.mobViewLine);
        passViewLine = findViewById(R.id.passViewLine);
        NewSharedPref.INSTANCE.GetSharedPreferences(getApplicationContext());
        dialog = new ProgressDialog(LoginActivity.this);
        db = DBHelper.getInstance(getApplicationContext());
        loginMainFL = findViewById(R.id.loginMainFL);
        loginTopLL = findViewById(R.id.loginTopLL);
        loginTopIV = findViewById(R.id.loginTopIV);
        loginTopTV = findViewById(R.id.loginTopTV);
        loginMiddleLL = findViewById(R.id.loginMiddleLL);
        loginMobileLL = findViewById(R.id.loginMobileLL);
        loginPasswordLL = findViewById(R.id.loginPasswordLL);
        loginBottomLL = findViewById(R.id.loginBottomLL);
        loginButtonLL = findViewById(R.id.loginButtonLL);
        loginMainFL.post(() -> {
            totalWidth = loginMainFL.getWidth();
            totalHeight = loginMainFL.getHeight();
            mobPassHeight = loginMobileLL.getHeight();
            designUI();
        });
    }

    private void designUI() {
        //Outer views height
        int screenWidth = totalWidth;
        int screenHeight = totalHeight;
        int topLLHeight = screenHeight * 3 / 10;
        int middleLLHeight = screenHeight * 3 / 10;
        int bottomLLHeight = screenHeight * 4 / 10;
        LinearLayout.LayoutParams topLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, topLLHeight);
        //topLLParams.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams middleLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, middleLLHeight);
        //middleLLParams.gravity = Gravity.CENTER;
        middleLLParams.setMargins(screenWidth / 8, 0, screenWidth / 8, 0);
        LinearLayout.LayoutParams bottomLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bottomLLHeight);
        //bottomLLParams.gravity = Gravity.CENTER;
        loginTopLL.setLayoutParams(topLLParams);
        loginMiddleLL.setLayoutParams(middleLLParams);
        loginBottomLL.setLayoutParams(bottomLLParams);

        //Inner views height
        //Top LL
        LinearLayout.LayoutParams loginTopIVParams = new LinearLayout.LayoutParams(screenWidth * 3 / 7, topLLHeight / 2);
        loginTopIVParams.setMargins(0, topLLHeight / 6, 0, 0);
        loginTopIV.setLayoutParams(loginTopIVParams);

        LinearLayout.LayoutParams loginTopTVParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loginTopTVParams.setMargins(0, topLLHeight / 10, 0, 0);
        loginTopTV.setLayoutParams(loginTopTVParams);

        //Middle LL
        LinearLayout.LayoutParams loginMobPassLLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, mobPassHeight);
        loginMobPassLLParams.setMargins(0, mobPassHeight, 0, 0);
        loginMobileLL.setLayoutParams(loginMobPassLLParams);
        loginPasswordLL.setLayoutParams(loginMobPassLLParams);

        //Bottom LL
        //Setting login button width and height
        if (totalHeight < 1000)
            loginImageButton.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 6, screenWidth / 6));
        else
            loginImageButton.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 5, screenWidth / 5));

        LinearLayout.LayoutParams loginButtonLLParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loginButtonLLParams.setMargins(0, bottomLLHeight / 7, 0, bottomLLHeight / 7);
        loginButtonLL.setLayoutParams(loginButtonLLParams);
    }

    private void clickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.loginImageButton:

                        if (StaticMethods.isNetworkAvailable(LoginActivity.this)) {
                            if (validateEntries()) {
                                if (termsAndConditionsAccepted) {
                                    if (hasPermissions(LoginActivity.this, PERMISSIONS)) {
                                        url = Constants.LOGIN_URL;
                                        gcmId = FirebaseInstanceId.getInstance().getToken();
                                        imei = getImei(LoginActivity.this);
                                        new StartLogin(url, mobileLoginET.getText().toString().trim(), passLoginET.getText().toString().trim(), imei, gcmId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        dialog.setMessage(getString(R.string.try_logging));
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    } else {
                                        askPermission(LoginActivity.this, PERMISSIONS);
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.accept_terms_and_conditions), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case R.id.termsAndConditionsIV:

                        if (!termsAndConditionsAccepted) {
                            termsAndConditionsAccepted = true;
                            termsAndConditionsIV.setBackground(getResources().getDrawable(R.drawable.checked_box_icon));
                        } else {
                            termsAndConditionsAccepted = false;
                            termsAndConditionsIV.setBackground(getResources().getDrawable(R.drawable.unchecked_box_icon));
                        }

                        break;

                    case R.id.termsAndConditionsTV:

                        TermsAndConditions.start(LoginActivity.this);

                        break;

                    case R.id.contactUsTV:

                        sendEmail();

                        break;
                }
            }
        };
        loginImageButton.setOnClickListener(onClickListener);
        termsAndConditionsIV.setOnClickListener(onClickListener);
        termsAndConditionsTV.setOnClickListener(onClickListener);
        contactUsTV.setOnClickListener(onClickListener);
    }

    private void changeEditTextStyleOnFocus() {
        mobileLoginET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mobileLoginET.setHintTextColor(Color.parseColor("#8CC500"));
                    mobileLoginET.setTextColor(Color.parseColor("#8CC500"));
                    mobileIcon.setImageResource(R.drawable.mobile_icon_focus);
                    mobViewLine.setBackgroundColor(Color.parseColor("#8CC500"));
                } else {
                    mobileLoginET.setHintTextColor(Color.parseColor("#ECEBEC"));
                    mobileLoginET.setTextColor(Color.parseColor("#ECEBEC"));
                    mobileIcon.setImageResource(R.drawable.mobile_icon);
                    mobViewLine.setBackgroundColor(Color.parseColor("#ECEBEC"));
                }
            }
        });

        passLoginET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    passLoginET.setHintTextColor(Color.parseColor("#8CC500"));
                    passLoginET.setTextColor(Color.parseColor("#8CC500"));
                    passwordIcon.setImageResource(R.drawable.password_icon_focus);
                    passViewLine.setBackgroundColor(Color.parseColor("#8CC500"));
                } else {
                    passLoginET.setHintTextColor(Color.parseColor("#ECEBEC"));
                    passLoginET.setTextColor(Color.parseColor("#ECEBEC"));
                    passwordIcon.setImageResource(R.drawable.password_icon);
                    passViewLine.setBackgroundColor(Color.parseColor("#ECEBEC"));
                }
            }
        });
    }

    private boolean validateEntries() {
        //Validate mobile and password before checking for login
        if (mobileLoginET.getText().toString().trim().isEmpty() && passLoginET.getText().toString().trim().isEmpty()) {
            mobileLoginET.setHintTextColor(Color.parseColor("#EB2416"));
            mobileLoginET.setTextColor(Color.parseColor("#EB2416"));
            mobileIcon.setImageResource(R.drawable.mobile_error_icon);
            mobViewLine.setBackgroundColor(Color.parseColor("#EB2416"));

            passLoginET.setHintTextColor(Color.parseColor("#EB2416"));
            passLoginET.setTextColor(Color.parseColor("#EB2416"));
            passwordIcon.setImageResource(R.drawable.password_error_icon);
            passViewLine.setBackgroundColor(Color.parseColor("#EB2416"));
            Toast.makeText(this, getString(R.string.empty_login_id_and_pass), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mobileLoginET.getText().toString().trim().length() > 10 && passLoginET.getText().toString().trim().length() < 6){
            mobileLoginET.setHintTextColor(Color.parseColor("#EB2416"));
            mobileLoginET.setTextColor(Color.parseColor("#EB2416"));
            mobileIcon.setImageResource(R.drawable.mobile_error_icon);
            mobViewLine.setBackgroundColor(Color.parseColor("#EB2416"));

            passLoginET.setHintTextColor(Color.parseColor("#EB2416"));
            passLoginET.setTextColor(Color.parseColor("#EB2416"));
            passwordIcon.setImageResource(R.drawable.password_error_icon);
            passViewLine.setBackgroundColor(Color.parseColor("#EB2416"));
            Toast.makeText(this, getString(R.string.wrong_access_id_and_pass), Toast.LENGTH_SHORT).show();
        }
        else if (mobileLoginET.getText().toString().length() > 10) {
            mobileLoginET.setHintTextColor(Color.parseColor("#EB2416"));
            mobileLoginET.setTextColor(Color.parseColor("#EB2416"));
            mobileIcon.setImageResource(R.drawable.mobile_error_icon);
            mobViewLine.setBackgroundColor(Color.parseColor("#EB2416"));
            Toast.makeText(this, getString(R.string.invalid_mobile_error), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passLoginET.getText().toString().length() < 6) {
            passLoginET.setHintTextColor(Color.parseColor("#EB2416"));
            passLoginET.setTextColor(Color.parseColor("#EB2416"));
            passwordIcon.setImageResource(R.drawable.password_error_icon);
            passViewLine.setBackgroundColor(Color.parseColor("#EB2416"));
            Toast.makeText(this, getString(R.string.invalid_pass_error), Toast.LENGTH_SHORT).show();
            return false;
        }

        mobileLoginET.setHintTextColor(Color.parseColor("#8CC500"));
        mobileLoginET.setTextColor(Color.parseColor("#8CC500"));
        mobileIcon.setImageResource(R.drawable.mobile_icon_focus);
        mobViewLine.setBackgroundColor(Color.parseColor("#8CC500"));

        passLoginET.setHintTextColor(Color.parseColor("#8CC500"));
        passLoginET.setTextColor(Color.parseColor("#8CC500"));
        passwordIcon.setImageResource(R.drawable.password_icon_focus);
        passViewLine.setBackgroundColor(Color.parseColor("#8CC500"));
        return true;
    }

    private void setWrongMobilePasswordUI() {
        mobileLoginET.setHintTextColor(Color.parseColor("#EB2416"));
        mobileLoginET.setTextColor(Color.parseColor("#EB2416"));
        mobileIcon.setImageResource(R.drawable.mobile_error_icon);
        mobViewLine.setBackgroundColor(Color.parseColor("#EB2416"));

        passLoginET.setHintTextColor(Color.parseColor("#EB2416"));
        passLoginET.setTextColor(Color.parseColor("#EB2416"));
        passwordIcon.setImageResource(R.drawable.password_error_icon);
        passViewLine.setBackgroundColor(Color.parseColor("#EB2416"));
    }

    private void getCurrentUserDetails() {
        //Store the details of the current user in Shared Preferences.
        KenanteUser currentUser = db.getUserByUID(NewSharedPref.INSTANCE.getStringValue(Constants.KID));
        NewSharedPref.INSTANCE.storeCurrentUser(currentUser);

    }

    private void startDialogsActivity() {
        NewSharedPref.INSTANCE.setValue(Constants.LOGIN_STATUS, true);
        DashboardScreen.start(LoginActivity.this);
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:helpdesk@kenante.com?subject= Facing Issue" + "&body= Write your problem");
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_PERMISSION_KEY:
                if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] + grantResults[2]) == PackageManager.PERMISSION_GRANTED) {
                    url = Constants.LOGIN_URL;
                    gcmId = FirebaseInstanceId.getInstance().getToken();
                    imei = getImei(LoginActivity.this);
                    new StartLogin(url, mobileLoginET.getText().toString().trim(), passLoginET.getText().toString().trim(), imei, gcmId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    dialog.setMessage(getString(R.string.try_logging));
                    dialog.setCancelable(false);
                    dialog.show();
                }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) {
            db.close();
            db = null;
        }
    }

    private void logout() {
        NewSharedPref.INSTANCE.setValue(Constants.LOGIN_STATUS, false);
        NewSharedPref.INSTANCE.clear(this);

        db.LogOutDelete();
    }

    //Login
    public class StartLogin extends AsyncTask<String, String, String> {

        String url, mobile, password, imei, gcmId;

        public StartLogin(String url, String mobile, String password, String imei, String gcmId) {
            this.url = url;
            this.mobile = mobile;
            this.password = password;
            this.imei = imei;
            this.gcmId = gcmId;
            Log.i(TAG, "Start login service");
        }

        @Override
        protected String doInBackground(String... strings) {

            Communicator communicator = new Communicator();
            RetrofitInterface service = communicator.initialization();
            Call<UserLoginSR> call = service.loginUser(mobile, password, imei, gcmId);
            call.enqueue(new Callback<UserLoginSR>() {
                @Override
                public void onResponse(Call<UserLoginSR> call, Response<UserLoginSR> response) {

                    /*On success of login
                    1. Get details of current user.
                    2. Get details of all the other users present in same room.*/

                    int status = response.body().getStatus();
                    if (status == 1) {
                        Log.i(TAG, "Got login response successful");
                        //Login Successful
                        UserId userId = response.body().getUserId();

                        NewSharedPref.INSTANCE.setValue(Constants.KID, userId.getUser_id());

                        //ConnectActivity.start(instance);
                        //Handle all the work of ConnectActivity on LoginActivity

                        new SyncUsers(userId.getUser_id()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        /*LoginActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });*/
                    } else if (status == 2) {
                        Toast.makeText(LoginActivity.this, getString(R.string.already_logged_in), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.wrong_access_id_and_pass), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                }

                @Override
                public void onFailure(Call<UserLoginSR> call, Throwable t) {
                    setWrongMobilePasswordUI();
                    t.printStackTrace();
                    new LogoutUser(NewSharedPref.INSTANCE.getStringValue(Constants.KID)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    Toast.makeText(LoginActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            return "";
        }
    }

    public class SyncUsers extends AsyncTask<String, String, String> {

        String id;

        public SyncUsers(String id) {
            this.id = id;
            Log.i(TAG, "Start sync users service");
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
                        Log.i(TAG, "Got sync users successful response");
                        //Success in getting list.
                        //Store all the users in database.
                        ArrayList<KenanteUser> users = response.body().getResponse();
                        db = DBHelper.getInstance(getApplicationContext());
                        if (users != null) {
                            if (users.size() != 0) {
                                Log.i(TAG, "sync users - Inserting users");
                                db.insertUsers(users);
                                Log.i(TAG, "sync users - Users inserted");
                            }
                        }
                        //Now get details of current user and store it in shared preferences.

                        ArrayList<RuleModel> model = response.body().getRuleModel();

                        Log.i(TAG, "Starting insertion of room schedule. Total rooms - " + model.size());
                        //Get Rule and store it in database
                        for (int i = 0; i < model.size(); i++) {
                            //Log.i(TAG, "Storing data for room: " + model.get(i).getRoom_schedule().getRoom());
                            ArrayList<Integer> see = model.get(i).getSee();
                            ArrayList<Integer> talk = model.get(i).getTalk();
                            ArrayList<Integer> hear = model.get(i).getHear();
                            ArrayList<Integer> chat = model.get(i).getChat();
                            ScheduleModel schedule = model.get(i).getRoom_schedule();
                            int selfVideoOff = model.get(i).getSelf_video_off();
                            int selfAudioOff = model.get(i).getSelf_audio_off();
                            //Storing rule in database.
                            if(schedule!=null) {
                                db.storeRule(schedule.getRoom(), see, talk, hear, chat, selfVideoOff, selfAudioOff);
                                db.insertSchedule(schedule);
                            }
                            //Log.i(TAG, "Data inserted for room: " + model.get(i).getRoom_schedule().getRoom());
                        }
                        Log.i(TAG, "Room schedule successfully inserted");
                        getCurrentUserDetails();
                        startDialogsActivity();
                    }
                }

                @Override
                public void onFailure(Call<SyncUsersSR> call, Throwable t) {
                    setWrongMobilePasswordUI();
                    new LogoutUser(NewSharedPref.INSTANCE.getStringValue(Constants.KID)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    Toast.makeText(LoginActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });

            return "";
        }
    }

    private class LogoutUser extends AsyncTask<String, String, String> {

        private String userId;

        public LogoutUser(String userId) {
            this.userId = userId;
        }

        @Override
        protected String doInBackground(String... strings) {

            Communicator communicator = new Communicator();
            RetrofitInterface service = communicator.initialization();
            Call<UserLoginSR> call = service.LogoutUser(userId);
            call.enqueue(new Callback<UserLoginSR>() {
                @Override
                public void onResponse(Call<UserLoginSR> call, Response<UserLoginSR> response) {
                    logout();
                }

                @Override
                public void onFailure(Call<UserLoginSR> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(LoginActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                }
            });

            return "";
        }
    }

}
