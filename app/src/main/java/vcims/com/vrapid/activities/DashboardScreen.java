package vcims.com.vrapid.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.fragments.AddUsersFragment;
import vcims.com.vrapid.fragments.BriefFragment;
import vcims.com.vrapid.fragments.ChangeLanguageFragment;
import vcims.com.vrapid.fragments.ConferenceDetailsFragment;
import vcims.com.vrapid.fragments.FaqFragment;
import vcims.com.vrapid.fragments.HistoryFragment;
import vcims.com.vrapid.fragments.NotificationFragment;
import vcims.com.vrapid.interfaces.SendDataBack;
import vcims.com.vrapid.models.DialogModel;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.RuleModel;
import vcims.com.vrapid.models.ScheduleModel;
import vcims.com.vrapid.retrofit.Communicator;
import vcims.com.vrapid.retrofit.RetrofitInterface;
import vcims.com.vrapid.retrofit.serverresponse.SyncUsersSR;
import vcims.com.vrapid.retrofit.serverresponse.UserLoginSR;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.NetworkConnectionCheck;
import vcims.com.vrapid.util.NewSharedPref;
import vcims.com.vrapid.util.StaticMethods;
import vcims.com.vrapid.util.ThreadExecuter;
import vcims.com.vrapid.view_pager.DialogViewPager;
import static vcims.com.vrapid.util.StaticMethods.shareApp;

public class DashboardScreen extends AppCompatActivity implements DialogViewPager.OnViewPagerEventListener,
        NetworkConnectionCheck.OnConnectivityChangedListener, SendDataBack {

    private final String TAG = DashboardScreen.class.getSimpleName(), UPCOMING = "upcoming", ONGOING = "ongoing", ALL = "all";
    private final String ACTION_BAR = "action_bar", ACTION_BAR_SUBTITLE = "action_bar_subtitl", NO_GROUPS_STATE = "no_groups", HIDE_SLIDER = "hide_slider",
            SHOW_SLIDER = "show_slider", ADMIN_CREATE_GROUPS = "admin_create_groups", REMOVE_NOTIFICATION_BADGE = "remove_noti_badge",
            GROUP_EXIST = "group_exist";
    //private SharedPref sharedPref;
    private DBHelper db;
    private Toolbar dialogToolbar;
    private ViewPager viewPager;
    private DialogViewPager dialogViewPager;
    private LinearLayout sliderPoints, showDialogLL, noGroupsExistLL, noInternetLL, viewPagerLL;
    private TextView[] dots;
    private DrawerLayout drawer_layout;
    private NavigationView nav_view;
    private LinearLayout groupStartType, navFaq, navChangeLanguage, navHistory, navHelp, navBrief, navShare, navLogout;
    private TextView notiBadgeTextView, startTypeTV, sliderTV;
    private ArrayList<DialogModel> dialogsBackUpArray;
    private ImageButton refreshGroups, createGroupsIB, notiIB, navClose;
    private View.OnClickListener onClickListener;
    private NetworkConnectionCheck networkConnectionCheck;
    private HashMap<String, HashMap<String, CharSequence>> groupTimingDetails;
    private LinearLayout openInternetSettings;
    private Button addGroupButton;
    private ProgressDialog loadingAlert;
    private int viewPagerWidth = 0, viewPagerHeight = 0;
    private boolean groupsAdded = false, firstTime = true;
    private ArrayList<String> chatDialogIds = new ArrayList<>();
    private KenanteUser currentUser;
    private int currentUserType = -1;
    //private StoreNames storeNames;

    public static void start(Context context) {
        Intent i = new Intent(context, DashboardScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    //View Pager Interface Callback
    //Binding UI with dialog and schedule details
    @Override
    public void onInstantiateItem(int position, View view) {
        DialogModel dialog = dialogViewPager.getItem(position);
        makeDialogUI(dialog, view);
    }

    private void makeDialogUI(DialogModel dialog, View view) {

        ImageView am_pm_icon = view.findViewById(R.id.am_pm_icon);
        TextView dialogTime = view.findViewById(R.id.dialogTime);
        TextView dialogDay = view.findViewById(R.id.dialogDay);
        TextView dialogDate = view.findViewById(R.id.dialogDate);
        TextView confName = view.findViewById(R.id.confName);
        Button alarmButton = view.findViewById(R.id.alarmButton);
        LinearLayout dialogCardColorLL = view.findViewById(R.id.dialogCardColorLL);

        int status = dialog.getStatus();

        try {
            //This hashmap holds values of time, day, month and code
            //code = 1 for Day
            //code = 2 for Night
            HashMap<String, String> schedule = StaticMethods.getSchedule(this, db, dialog.getName());
            CharSequence time = "";

            String t1 = schedule.get("time");
            String[] t2 = t1.split(" ");
            if (t2.length > 0) {
                SpannableString ss1 = new SpannableString(t2[0]);
                ss1.setSpan(new RelativeSizeSpan(1.2f), 0, t2[0].length(), 0);
                SpannableString ss2 = new SpannableString(t2[1].toLowerCase());
                ss2.setSpan(new RelativeSizeSpan(0.7f), 0, t2[1].length(), 0);
                time = TextUtils.concat(ss1, ss2);
            }
            String code = schedule.get("day_night");
            if (status == 1 || status == 2) {
                dialogTime.setText(time);
                dialogDay.setText(schedule.get("day"));
                dialogDate.setText(schedule.get("month"));
                if (!code.equals("")) {
                    if (code.equals("1")) {
                        am_pm_icon.setBackground(getResources().getDrawable(R.drawable.am_icon));
                    } else if (code.equals("2")) {
                        dialogTime.setTextColor(Color.parseColor("#23676C"));
                        /*dialogDay.setTextColor(Color.parseColor("#909962"));
                        dialogDate.setTextColor(Color.parseColor("#909962"));
                        confName.setTextColor(Color.parseColor("#909962"));*/
                        am_pm_icon.setBackground(getResources().getDrawable(R.drawable.pm_icon));
                        dialogCardColorLL.setBackground(getResources().getDrawable(R.drawable.dialog_night_background));
                    }
                }
            } else if (status == 3) {
                dialogTime.setText(getString(R.string.expired));
                //dialogTime.setTextColor(Color.parseColor("#ccbb3c"));
                dialogDay.setText(time + ", " + schedule.get("day"));
                //dialogDay.setTextColor(Color.parseColor("#ccbb3c"));
                dialogDate.setText(schedule.get("month"));
                //dialogDate.setTextColor(Color.parseColor("#ccbb3c"));
                //confName.setTextColor(Color.parseColor("#ccbb3c"));
                am_pm_icon.setBackground(getResources().getDrawable(R.drawable.pm_icon));
                if (!code.equals("")) {
                    if (code.equals("1")) {
                        am_pm_icon.setBackground(getResources().getDrawable(R.drawable.am_icon_grey));
                    } else if (code.equals("2")) {
                        am_pm_icon.setBackground(getResources().getDrawable(R.drawable.pm_icon_grey));
                    }
                }
                alarmButton.setVisibility(View.GONE);
            }
            HashMap<String, CharSequence> values = new HashMap<>();
            values.put("time", time);
            values.put("day", schedule.get("day"));
            values.put("month", schedule.get("month"));
            groupTimingDetails.put(dialog.getName(), values);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        confName.setText(dialog.getName());

    }

    //View Pager Interface Callback
    //This is used for handling click on the viewpager item.
    @Override
    public void onItemClick(int position, View view) {
        DialogModel dialog = dialogViewPager.getItem(position);
        startLoadingAlert();

        HashMap<String, CharSequence> hashMap = groupTimingDetails.get(dialog.getName());
        String time = hashMap.get("time").toString();
        String day = hashMap.get("day").toString();
        String date = hashMap.get("month").toString();
        //Code 1 for upcoming, 2 for ongoing and 3 for expired
        int type = dialog.getStatus();

        NewSharedPref.INSTANCE.setValue(Constants.USER_TYPE, currentUserType);

        Boolean isSecure = db.isThisRoomSecure(dialog.getName());
        Boolean ongoing = type == 2;

        NewSharedPref.INSTANCE.setValue(Constants.ROOM, dialog.getName());
        NewSharedPref.INSTANCE.setValue(Constants.ROOM_ID, dialog.getId());
        startConferenceDetailsFragment(time, day, date, ongoing, isSecure);

    }

    private void startConferenceDetailsFragment(String time, String day, String date, Boolean ongoing, Boolean isSecure) {
        ConferenceDetailsFragment fragment = new ConferenceDetailsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.EXTRA_TIME, time);
        args.putString(Constants.EXTRA_DAY, day);
        args.putString(Constants.EXTRA_DATE, date);
        args.putBoolean(Constants.EXTRA_GROUP_ONGOING, ongoing);
        args.putBoolean(Constants.EXTRA_IS_ROOM_SECURE, isSecure);
        fragment.setArguments(args);

        if (loadingAlert.isShowing())
            loadingAlert.cancel();
        args.putStringArrayList(Constants.EXTRA_CHAT_DIALOG_ID, chatDialogIds);
        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, fragment, Constants.CONF_DETAILS_FRAG).addToBackStack(null).commitAllowingStateLoss();
        Log.i(TAG, "Fragment Opening");
    }

    //View Pager Interface Callback
    //Handles click of alarm button
    @Override
    public void onAddToCalender(int position) {
        DialogModel dialog = dialogViewPager.getItem(position);
        Boolean added = StaticMethods.getEventStatus(this, dialog.getName());
        //Added is true if the calendar for this room name has already been added otherwise false
        if (!added)
            addReminder(dialog, db, getApplicationContext());
        else
            Toast.makeText(this, getString(R.string.remind_already_added), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_drawer);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initialize();
        clickListener();
        if (StaticMethods.isNetworkAvailable(this)) {
            viewPagerLL.post(new Runnable() {
                @Override
                public void run() {
                    viewPagerWidth = viewPagerLL.getWidth();
                    viewPagerHeight = viewPagerLL.getHeight();
                    initViewPager();
                    initNavigationDrawer();
                    initPageListener();
                    startLoadingAlert();
                    ArrayList<DialogModel> dialogs = getDialogsToShow();
                    if (dialogs.size() != 0) {
                        updateUI(GROUP_EXIST);
                        dialogsBackUpArray.clear();
                        doDialogWork(dialogs);
                        groupsAdded = true;
                    } else {
                        loadingAlert.cancel();
                        updateUI(HIDE_SLIDER);
                        updateUI(NO_GROUPS_STATE);
                    }
                }
            });
        } else {
            onUnavailable();
            groupsAdded = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkConnectionCheck.registerListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkConnectionCheck.unregisterListener(this);
        }
    }

    private void initialize() {
        NewSharedPref.INSTANCE.INSTANCE.GetSharedPreferences(getApplicationContext());
        db = DBHelper.getInstance(getApplicationContext());
        dialogToolbar = findViewById(R.id.dialogToolbar);
        currentUser = NewSharedPref.INSTANCE.INSTANCE.getCurrentUser();
        setSupportActionBar(dialogToolbar);
        viewPager = findViewById(R.id.dialogViewPager);
        sliderPoints = findViewById(R.id.sliderPoints);
        noGroupsExistLL = findViewById(R.id.noGroupsExistLL);
        noInternetLL = findViewById(R.id.noInternetLL);
        showDialogLL = findViewById(R.id.showDialogLL);
        drawer_layout = findViewById(R.id.drawer_layout);
        nav_view = findViewById(R.id.nav_view);
        viewPagerLL = findViewById(R.id.viewPagerLL);
        groupStartType = findViewById(R.id.groupStartType);
        notiBadgeTextView = findViewById(R.id.notiBadgeTextView);
        startTypeTV = findViewById(R.id.startTypeTV);
        dialogsBackUpArray = new ArrayList<>();
        sliderTV = findViewById(R.id.sliderTV);
        refreshGroups = findViewById(R.id.refreshGroups);
        createGroupsIB = findViewById(R.id.createGroupsIB);
        notiIB = findViewById(R.id.notiIB);
        navFaq = findViewById(R.id.navFaq);
        navChangeLanguage = findViewById(R.id.navChangeLanguage);
        navHistory = findViewById(R.id.navHistory);
        navHelp = findViewById(R.id.navHelp);
        navBrief = findViewById(R.id.navBrief);
        navShare = findViewById(R.id.navShare);
        navLogout = findViewById(R.id.navLogout);
        navClose = findViewById(R.id.navClose);
        loadingAlert = new ProgressDialog(this);
        groupTimingDetails = new HashMap<>();
        openInternetSettings = findViewById(R.id.openInternetSettings);
        addGroupButton = findViewById(R.id.addGroupButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkConnectionCheck = new NetworkConnectionCheck(getApplication());
        }
        updateUI(ACTION_BAR);
        updateUI(ADMIN_CREATE_GROUPS);
    }

    private void updateUI(String uiType) {
        switch (uiType) {
            case ACTION_BAR:
                if (getSupportActionBar() != null) {

                    ActionBar actionBar = getSupportActionBar();
                    actionBar.setTitle(NewSharedPref.INSTANCE.getStringValue(Constants.NAME));

                }
                break;

            case ACTION_BAR_SUBTITLE:
                if (getSupportActionBar() != null) {
                    String type = "";
                    ActionBar actionBar = getSupportActionBar();
                    if (currentUserType == Constants.RESPONDENT)
                        type = getString(R.string.respondent);
                    else if (currentUserType == Constants.MODERATOR)
                        type = getString(R.string.moderator);
                    else if (currentUserType == Constants.TRANSLATOR)
                        type = getString(R.string.translator);
                    else if (currentUserType == Constants.CLIENT) {
                        type = getString(R.string.client);
                        navBrief.setVisibility(View.VISIBLE);
                    } else if (currentUserType == Constants.ADMIN)
                        type = getString(R.string.admin);
                    else if (currentUserType == Constants.RECORDER)
                        type = getString(R.string.trecorder);
                    else if (currentUserType == -1)
                        type = "lalala";
                    actionBar.setSubtitle(type);
                }
                break;

            case UPCOMING:
                startTypeTV.setText(getString(R.string.upcoming));
                break;

            case ONGOING:
                startTypeTV.setText(getString(R.string.ongoing));
                break;

            case ALL:
                startTypeTV.setText(getString(R.string.all));
                break;

            case GROUP_EXIST:
                noGroupsExistLL.setVisibility(View.GONE);
                viewPagerLL.setVisibility(View.VISIBLE);
                showDialogLL.setVisibility(View.VISIBLE);
                break;

            case NO_GROUPS_STATE:
                noGroupsExistLL.setVisibility(View.VISIBLE);
                viewPagerLL.setVisibility(View.GONE);
                break;

            case HIDE_SLIDER:
                sliderTV.setVisibility(View.GONE);
                break;

            case SHOW_SLIDER:
                sliderTV.setVisibility(View.VISIBLE);
                break;

            case ADMIN_CREATE_GROUPS:
                if (currentUser.getUser_type() != Constants.ADMIN) {
                    createGroupsIB.setVisibility(View.GONE);
                    addGroupButton.setVisibility(View.GONE);
                }
                break;

            case REMOVE_NOTIFICATION_BADGE:
                notiBadgeTextView.setText("");
                notiBadgeTextView.setVisibility(View.GONE);
                break;

        }
    }

    private void startLoadingAlert() {
        if (!loadingAlert.isShowing()) {
            loadingAlert.setCancelable(false);
            loadingAlert.setCanceledOnTouchOutside(false);
            loadingAlert.setMessage(getString(R.string.loading));
            loadingAlert.show();
        }
    }

    private void initNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, dialogToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();
        dialogToolbar.setNavigationIcon(R.drawable.menu_icon);
    }

    private void navigationMenuItemClickListener() {
        groupStartType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(DashboardScreen.this, groupStartType);
                menu.getMenuInflater().inflate(R.menu.group_start_type_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //For resetting view pager position to first element when new adapter binds
                        viewPager.setCurrentItem(0);
                        //Bind adapter according to upcoming, ongoing and expired
                        handleGroupStartType(item.getItemId());
                        return true;
                    }
                });
                menu.show();
            }
        });
    }

    private void clickListener() {
        navigationMenuItemClickListener();
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.createGroupsIB:
                        selectConfRoom();
                        break;

                    case R.id.notiIB:
                        updateUI(REMOVE_NOTIFICATION_BADGE);
                        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, new NotificationFragment(), Constants.NOTIFICATION_FRAG).addToBackStack(null).commitAllowingStateLoss();
                        break;

                    case R.id.navFaq:

                        drawer_layout.closeDrawer(Gravity.START);
                        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, new FaqFragment(), Constants.FAQ_FRAG).addToBackStack(null).commitAllowingStateLoss();

                        break;

                    case R.id.navChangeLanguage:

                        drawer_layout.closeDrawer(Gravity.START);
                        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, new ChangeLanguageFragment(), Constants.CHANGE_LANGUAGE_FRAG).addToBackStack(null).commitAllowingStateLoss();

                        break;

                    case R.id.navHistory:

                        drawer_layout.closeDrawer(Gravity.START);
                        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, new HistoryFragment(), Constants.HISTORY_FRAG).addToBackStack(null).commitAllowingStateLoss();

                        break;

                    case R.id.navHelp:

                        drawer_layout.closeDrawer(Gravity.START);
                        sendEmail();

                        break;

                    case R.id.navBrief:

                        drawer_layout.closeDrawer(Gravity.START);
                        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, new BriefFragment(), Constants.BRIEF_FRAG).addToBackStack(null).commitAllowingStateLoss();

                        break;

                    case R.id.navShare:

                        drawer_layout.closeDrawer(Gravity.START);
                        shareApp(DashboardScreen.this);

                        break;

                    case R.id.navLogout:
                        drawer_layout.closeDrawer(Gravity.START);
                        if (StaticMethods.isNetworkAvailable(DashboardScreen.this)) {
                            startLoadingAlert();
                            logout();
                            //new LogoutUser(NewSharedPref.INSTANCE.USER_ID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else
                            Toast.makeText(DashboardScreen.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.navClose:
                        drawer_layout.closeDrawer(Gravity.START);
                        break;

                    case R.id.openInternetSettings:

                        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        intent.setComponent(cn);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        break;

                    case R.id.addGroupButton:
                        selectConfRoom();
                        break;

                    case R.id.refreshGroups:
                        if (StaticMethods.isNetworkAvailable(DashboardScreen.this))
                            refresh();
                        else
                            Toast.makeText(DashboardScreen.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        navFaq.setOnClickListener(onClickListener);
        navChangeLanguage.setOnClickListener(onClickListener);
        navHistory.setOnClickListener(onClickListener);
        navHelp.setOnClickListener(onClickListener);
        navBrief.setOnClickListener(onClickListener);
        navShare.setOnClickListener(onClickListener);
        navLogout.setOnClickListener(onClickListener);
        navClose.setOnClickListener(onClickListener);
        createGroupsIB.setOnClickListener(onClickListener);
        notiIB.setOnClickListener(onClickListener);
        openInternetSettings.setOnClickListener(onClickListener);
        addGroupButton.setOnClickListener(onClickListener);
        refreshGroups.setOnClickListener(onClickListener);
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:helpdesk@kenante.com?subject= " + getString(R.string.facing_issue) + "&body= " + getString(R.string.write_problem));
        intent.setData(data);
        startActivity(intent);
    }

    private void startAddUsersFragment(String tags) {
        AddUsersFragment fragment = new AddUsersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TAGS, tags);
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().replace(R.id.dashBoardFL, fragment, Constants.ADD_USERS_FRAG).addToBackStack(null).commitAllowingStateLoss();
    }

    private void handleGroupStartType(int itemId) {
        dialogViewPager.removeAllItems();
        sliderPoints.removeAllViews();
        updateUI(SHOW_SLIDER);
        ArrayList<DialogModel> dialogs = new ArrayList<>();
        switch (itemId) {
            case R.id.upcoming:
                updateUI(UPCOMING);
                //dialogsBackUpArray contains all the elements which are to be shown.
                //We filter them with schedule to get upcoming, ongoing elements.
                dialogs = checkScheduleAndProceed(dialogsBackUpArray, UPCOMING);
                //This array will only contains upcoming groups.
                if (!dialogs.isEmpty()) {
                    updateUI(GROUP_EXIST);
                    addItemsToAdapter(dialogs);
                } else {
                    updateUI(HIDE_SLIDER);
                    updateUI(NO_GROUPS_STATE);
                }
                break;

            case R.id.ongoing:
                updateUI(ONGOING);
                dialogs = checkScheduleAndProceed(dialogsBackUpArray, ONGOING);
                //This array will only contains groups which are going on right now.
                if (!dialogs.isEmpty()) {
                    updateUI(GROUP_EXIST);
                    addItemsToAdapter(dialogs);
                } else {
                    updateUI(HIDE_SLIDER);
                    updateUI(NO_GROUPS_STATE);
                }
                break;

            case R.id.all:
                updateUI(ALL);
                dialogs = checkScheduleAndProceed(dialogsBackUpArray, ALL);
                //This array will only contains groups which are expired.
                if (!dialogs.isEmpty()) {
                    updateUI(GROUP_EXIST);
                    addItemsToAdapter(dialogs);
                } else {
                    updateUI(HIDE_SLIDER);
                    updateUI(NO_GROUPS_STATE);
                }
                break;
        }
    }

    //Initializing view pager
    //Passing an empty arraylist.
    //We bind data onto viewpager as we get elements.
    private void initViewPager() {
        ArrayList<DialogModel> dialogs = new ArrayList<>();
        dialogViewPager = new DialogViewPager(this, dialogs, viewPagerWidth, viewPagerHeight);
        dialogViewPager.setViewPagerListener(this);
        viewPager.setAdapter(dialogViewPager);
    }

    //This is a listener for getting which viewpager element is shown on the screen.
    private void initPageListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                DialogModel dialog = dialogViewPager.getItem(position);
                currentUserType = db.getUserType(currentUser.getKid(), dialog.getName());
                updateUI(ACTION_BAR_SUBTITLE);
            }

            @Override
            public void onPageSelected(int position) {
                //Select slider dot of this position.
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //This is adding dots on the bottom.
    //They have size equal to the number of elements of viewpager to be shown.
    private void addBottomDots(int dotsSize) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
        params.setMargins(10, 10, 10, 10);

        dots = new TextView[dotsSize];

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setBackground(getResources().getDrawable(R.drawable.slider_navigator));
            dots[i].setLayoutParams(params);
            sliderPoints.addView(dots[i]);
        }
    }

    //Changing color of dot of position same to the viewpager's current element.
    private void selectDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (i == position) {
                dots[i].setBackground(getResources().getDrawable(R.drawable.slider_navigator_active));
            } else
                dots[i].setBackground(getResources().getDrawable(R.drawable.slider_navigator));
        }
    }

    private ArrayList<DialogModel> getDialogsToShow() {
        /*ArrayList<DialogModel> arrayList = new ArrayList<>();
        ArrayList<String> tags = db.getAllTags();
        for (int i = 0; i < tags.size(); i++) {
            DialogModel dialog = new DialogModel();
            dialog.setName(tags.get(i));
            arrayList.add(dialog);
        }*/
        ArrayList<DialogModel> dialogs = db.getAllDialos();
        return dialogs;
    }

    private void doDialogWork(ArrayList<DialogModel> dialogsArray) {
        if (dialogsArray.size() > 0) {
            //This returns the last dialog of each roomname.
            //So that we have only 1 dialog to show corresponding to 1 room.
            //ArrayList<QBChatDialog> lastDialogs = getLastDialogs(dialogsArray);
            showOnGoingIfPresentElseUpComing(dialogsArray);
        }
    }

    private void showOnGoingIfPresentElseUpComing(ArrayList<DialogModel> dialogs) {
        updateUI(SHOW_SLIDER);
        updateUI(GROUP_EXIST);
        dialogsBackUpArray.addAll(dialogs);
        loadingAlert.cancel();
        ArrayList<DialogModel> dialogsToShow;
        dialogsToShow = checkScheduleAndProceed(dialogs, ONGOING);
        if (!dialogsToShow.isEmpty()) {
            addItemsToAdapter(dialogsToShow);
            updateUI(ONGOING);
        } else {
            dialogsToShow = checkScheduleAndProceed(dialogs, UPCOMING);
            if (!dialogsToShow.isEmpty()) {
                addItemsToAdapter(dialogsToShow);
                updateUI(UPCOMING);
            } else {
                dialogsToShow = checkScheduleAndProceed(dialogs, ALL);
                if (!dialogsToShow.isEmpty()) {
                    addItemsToAdapter(dialogsToShow);
                    updateUI(ALL);
                } else {
                    updateUI(HIDE_SLIDER);
                    updateUI(NO_GROUPS_STATE);
                }
            }
        }
        if (!dialogsToShow.isEmpty()) {
            if (dialogsToShow.size() == 1) {
                updateUI(HIDE_SLIDER);
                sliderPoints.removeAllViews();
            }
        }
    }

    private ArrayList<DialogModel> checkScheduleAndProceed(ArrayList<DialogModel> chatDialogs, String code) {
        ArrayList<DialogModel> dialogs = new ArrayList<>();

        for (int i = 0; i < chatDialogs.size(); i++) {
            try {
                int type = handleSchedule(chatDialogs.get(i).getName(), code);
                if (type == 1) {
                    //Upcoming
                    chatDialogs.get(i).setStatus(1);
                    dialogs.add(chatDialogs.get(i));
                } else if (type == 2) {
                    //Ongoing
                    chatDialogs.get(i).setStatus(2);
                    dialogs.add(chatDialogs.get(i));
                } else if (type == 3) {
                    //All
                    chatDialogs.get(i).setStatus(3);
                    dialogs.add(chatDialogs.get(i));
                }
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return dialogs;
    }

    //TYPE can be:
    //Upcoming, ongoing and expired
/*
    private void assignEachDialogStatus(ArrayList<QBChatDialog> dialogs) {

        for (int i = 0; i < dialogs.size(); i++) {
            try {
                int type1 = handleSchedule(dialogs.get(i).getName(), UPCOMING);
                if (type1 == 1) {
                    //UPCOMING
                    QBDialogCustomData c = new QBDialogCustomData();
                    c.putInteger("Status", 1);
                    dialogs.get(i).setCustomData(c);
                } else if (type1 == 0) {
                    int type2 = handleSchedule(dialogs.get(i).getName(), ONGOING);
                    if (type2 == 2) {
                        //ONGOING
                        QBDialogCustomData c = new QBDialogCustomData();
                        c.putInteger("Status", 2);
                        dialogs.get(i).setCustomData(c);
                    } else if (type2 == 0) {
                        int type3 = handleSchedule(dialogs.get(i).getName(), ALL);
                        if (type3 == 3) {
                            //EXPIRED
                            QBDialogCustomData c = new QBDialogCustomData();
                            c.putInteger("Status", 3);
                            dialogs.get(i).setCustomData(c);
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
*/

    private int handleSchedule(String roomName, String code) throws ParseException {

        int returnValue = 0;

        Date startDate = StaticMethods.getStartDate(db, roomName);
        Date endDate = StaticMethods.getEndDate(db, roomName);

        DateFormat inputTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        inputTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentD = inputTimeFormat.format(Calendar.getInstance().getTime());
        Date currentDate = inputTimeFormat.parse(currentD);

        switch (code) {
            case UPCOMING:
                if (currentDate.before(startDate))
                    returnValue = 1;
                break;

            case ONGOING:
                if (currentDate.after(startDate) && currentDate.before(endDate))
                    returnValue = 2;
                break;

            case ALL:
                if (currentDate.after(endDate))
                    returnValue = 3;
                else if (currentDate.before(startDate))
                    returnValue = 1;
                else if (currentDate.after(startDate) && currentDate.before(endDate))
                    returnValue = 2;
                break;

            default:
                returnValue = 0;
                break;
        }

        /*if (returnValue == 0) {
            if (currentDate.before(startDate))
                returnValue = 1;
            else if (currentDate.after(startDate) && currentDate.before(endDate))
                returnValue = 2;
            else if (currentDate.after(endDate))
                returnValue = 3;
        }*/

        return returnValue;
    }

    private void addItemsToAdapter(ArrayList<DialogModel> dialogsToShow) {
        if (dialogsToShow.size() != 1) {
            addBottomDots(dialogsToShow.size());
            selectDot(0);
        } else
            updateUI(HIDE_SLIDER);
        if (dialogsToShow != null)
            if (dialogsToShow.size() > 0) {
                dialogViewPager.addAllItem(dialogsToShow);
            }
    }

    private void logout() {
        NewSharedPref.INSTANCE.setValue(Constants.LOGIN_STATUS, false);
        NewSharedPref.INSTANCE.setValue(Constants.CALL_STARTED, false);

        db.LogOutDelete();
        if (!isFinishing())
            if (loadingAlert.isShowing())
                loadingAlert.cancel();

        LoginActivity.start(this);
    }

    private void addReminder(DialogModel dialog, DBHelper db, Context applicationContext) {
        long startDate = 0, endDate = 0;
        try {
            startDate = StaticMethods.getScheduleDates(db, dialog.getName(), 1);
            endDate = StaticMethods.getScheduleDates(db, dialog.getName(), 2);
            Hashtable<String, String> calendarIds = StaticMethods.listCalendarId(this);
            if (calendarIds.size() > 0)
                selectCalendarAndAdd(calendarIds, startDate, endDate, dialog.getName());
            else {
                Toast.makeText(this, getString(R.string.no_acc_for_calender), Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_adding_reminder), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectCalendarAndAdd(Hashtable<String, String> calendarIds, long startDate, long endDate, String roomName) {
        final String[] selectedItem = new String[1];
        selectedItem[0] = null;
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setMessage(getString(R.string.select_calendar_acc));
        View v = LayoutInflater.from(this).inflate(R.layout.calendar_accounts, null);
        alert.setView(v);
        RadioGroup rg = v.findViewById(R.id.calAccRG);
        Enumeration e = calendarIds.keys();
        while (e.hasMoreElements()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(e.nextElement().toString());
            rg.addView(rb);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = rg.findViewById(checkedId);
                selectedItem[0] = rb.getText().toString().trim();
            }
        });
        Button calCancelButton = v.findViewById(R.id.calCancelButton);
        Button calOkButton = v.findViewById(R.id.calOkButton);
        calCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        calOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItem[0] != null) {
                    if (!selectedItem[0].equals("")) {
                        String selectedCalendarId = calendarIds.get(selectedItem[0]);
                        StaticMethods.addCalendarEvent(DashboardScreen.this, roomName, startDate, endDate, selectedCalendarId);
                    }
                } else
                    Toast.makeText(DashboardScreen.this, getString(R.string.no_cal_selected), Toast.LENGTH_SHORT).show();
                alert.dismiss();
            }
        });
        alert.show();
    }

    //Internet State Callbacks
    @Override
    public void onAvailable() {
        if (firstTime)
            firstTime = false;
        else {
            showDialogLL.setVisibility(View.VISIBLE);
            noInternetLL.setVisibility(View.GONE);
            if (!groupsAdded) {
                viewPagerLL.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPagerWidth = viewPagerLL.getWidth();
                        viewPagerHeight = viewPagerLL.getHeight();
                        initViewPager();
                        ArrayList<DialogModel> dialogs = getDialogsToShow();
                        if (dialogs.size() != 0) {
                            updateUI(GROUP_EXIST);
                            dialogsBackUpArray.clear();
                            doDialogWork(dialogs);
                            groupsAdded = true;
                        } else {
                            loadingAlert.cancel();
                            updateUI(HIDE_SLIDER);
                            updateUI(NO_GROUPS_STATE);
                        }
                    }
                });
                /*initNavigationDrawer();
                initPageListener();
                startLoadingAlert();
                getDialogsToShow();*/
            }
        }
    }

    @Override
    public void onLosing(int maxMsToLive) {
        //Toast.makeText(instance, "Internet connection will go in " + maxMsToLive/1000 + " seconds", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLost() {
        showDialogLL.setVisibility(View.GONE);
        noInternetLL.setVisibility(View.VISIBLE);
        if (loadingAlert.isShowing())
            loadingAlert.cancel();
    }

    @Override
    public void onUnavailable() {
        showDialogLL.setVisibility(View.GONE);
        noInternetLL.setVisibility(View.VISIBLE);
    }

    @Override
    public void sendDataBack(boolean groupAdded) {
        if (groupAdded) {
            startLoadingAlert();
            groupTimingDetails.clear();
            dialogViewPager.removeAllItems();
            sliderPoints.removeAllViews();
            //startLoadDialogs();
            ArrayList<DialogModel> dialogs = getDialogsToShow();
            if (dialogs.size() != 0) {
                updateUI(GROUP_EXIST);
                dialogsBackUpArray.clear();
                doDialogWork(dialogs);
                groupsAdded = true;
            } else {
                loadingAlert.cancel();
                updateUI(HIDE_SLIDER);
                updateUI(NO_GROUPS_STATE);
            }
        }
    }

    private void selectConfRoom() {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.select_conf_room, null);
        alert.setView(view);
        ListView selectConfLV = view.findViewById(R.id.selectConfLV);
        ArrayList<String> confRooms = db.getConfRooms();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, confRooms);
        selectConfLV.setAdapter(adapter);
        selectConfLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                String roomName = tv.getText().toString();
                startAddUsersFragment(roomName);
                alert.cancel();
            }
        });
        alert.show();
    }

    private void refresh() {
        startLoadingAlert();
        viewPager.setCurrentItem(0);
        groupTimingDetails.clear();
        dialogViewPager.removeAllItems();
        sliderPoints.removeAllViews();
        //Resync rules and users
        db.deleteAllRules();
        db.deleteAllSchedule();
        db.deleteAllUsers();
        //Reload all users
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                syncUsers(NewSharedPref.INSTANCE.getStringValue(Constants.KID));
            }
        };
        Thread thread = new Thread();
        ThreadExecuter.runButNotOn(runnable, thread);
        //new SyncUsers(sharedPref.USER_ID).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingAlert != null) {
            if (loadingAlert.isShowing())
                loadingAlert.cancel();
            loadingAlert = null;
        }
    }

    private void syncUsers(String id) {
        Communicator communicator = new Communicator();
        RetrofitInterface service = communicator.initialization();
        Call<SyncUsersSR> call = service.syncUsers(id);
        call.enqueue(new Callback<SyncUsersSR>() {
            @Override
            public void onResponse(Call<SyncUsersSR> call, Response<SyncUsersSR> response) {

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
                    }
                    //storeNames(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //updateUI(ACTION_BAR);
                            ArrayList<DialogModel> dialogs = getDialogsToShow();
                            if (dialogs.size() != 0) {
                                updateUI(GROUP_EXIST);
                                dialogsBackUpArray.clear();
                                doDialogWork(dialogs);
                                groupsAdded = true;
                            } else {
                                loadingAlert.cancel();
                                updateUI(HIDE_SLIDER);
                                updateUI(NO_GROUPS_STATE);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<SyncUsersSR> call, Throwable t) {
                Toast.makeText(DashboardScreen.this, getString(R.string.refresh_failed), Toast.LENGTH_SHORT).show();
                if (loadingAlert.isShowing())
                    loadingAlert.cancel();
            }
        });
    }

    public void reloadActivityOnLocaleChange(String languageName) {
        getFragmentManager().popBackStack();
        //Changing menu items name
        ((TextView) navFaq.getChildAt(0)).setText(getString(R.string.faq));
        ((TextView) navChangeLanguage.getChildAt(0)).setText(getString(R.string.change_language));
        ((TextView) navBrief.getChildAt(0)).setText(getString(R.string.brief));
        ((TextView) navHistory.getChildAt(0)).setText(getString(R.string.history));
        ((TextView) navHelp.getChildAt(0)).setText(getString(R.string.help_center));
        ((TextView) navShare.getChildAt(0)).setText(getString(R.string.share));
        ((TextView) navLogout.getChildAt(0)).setText(getString(R.string.logout));
        sliderTV.setText(getString(R.string.slide_left_right));
        TextView groupExistsTv = noGroupsExistLL.findViewWithTag("no_group_exists_text");
        groupExistsTv.setText(getString(R.string.no_group_exist));
        TextView addGroupTv = noGroupsExistLL.findViewWithTag("add_group_text");
        addGroupTv.setText(getString(R.string.add_group));
        TextView noInternetTv = noInternetLL.findViewWithTag("no_internet_text");
        noInternetTv.setText(getString(R.string.no_internet_access));
        TextView internetText1Tv = noInternetLL.findViewWithTag("internet_text_1");
        internetText1Tv.setText(getString(R.string.internet_settings_one));
        TextView internetText2Tv = noInternetLL.findViewWithTag("internet_text_2");
        internetText2Tv.setText(getString(R.string.internet_settings_two));
        navigationMenuItemClickListener();
        updateUI(ACTION_BAR);
        refresh();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_PERMISSION_KEY) {
            Boolean isPermissionGranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    isPermissionGranted = true;
                else {
                    isPermissionGranted = false;
                    break;
                }
            }
            if (isPermissionGranted) {
                ConferenceDetailsFragment fragment = (ConferenceDetailsFragment) getFragmentManager().findFragmentByTag(Constants.CONF_DETAILS_FRAG);
                if (fragment != null)
                    if (fragment.isAdded())
                        fragment.joinConference.performClick();
            } else {
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
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

                    /*int status = response.body().getStatus();
                    if(status == 1){*/
                    //Logout Successful
                    /*QBPushNotifications.deleteSubscription(Integer.valueOf(sharedPref.SUBSCRIPTION_ID)).performAsync(new QBEntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                        }

                        @Override
                        public void onError(QBResponseException e) {
                        }
                    });*/
                    logout();
                }

                @Override
                public void onFailure(Call<UserLoginSR> call, Throwable t) {
                    if (loadingAlert.isShowing())
                        loadingAlert.cancel();
                    logout();
                }
            });

            return "";
        }
    }

}
