package vcims.com.vrapid.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.ScheduleModel;

/**
 * Created by VCIMS-PC2 on 03-01-2018.
 */

public class StaticMethods {

    //Converting ArrayList<Integer> to String
    public static String arrayListToString(ArrayList<Integer> arrayList) {
        String s = "";
        for (int i = 0; i < arrayList.size(); i++) {
            if (i == 0) {
                s = String.valueOf(arrayList.get(i));
            } else {
                s = s + "," + String.valueOf(arrayList.get(i));
            }
        }
        return s;
    }

    //Converting String to ArrayList<Integer>
    public static ArrayList<Integer> stringToArrayList(String string) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        String[] s = string.split("//,");
        for (int i = 0; i < s.length; i++) {
            arrayList.add(Integer.valueOf(s[i]));
        }
        return arrayList;
    }

    //Filter users according to see, talk, hear, chat model.
    public static ArrayList<Integer> filterUsers(ArrayList<Integer> allUsers, String roomName, DBHelper db) {
        ArrayList<Integer> filteredUsers = new ArrayList<>();


        ArrayList<Integer> seeArrayList = db.getSeeList(roomName);
        ArrayList<Integer> talkArrayList = db.getTalkList(roomName);
        ArrayList<Integer> hearArrayList = db.getHearList(roomName);
        /*ArrayList<Integer> chatArrayList = db.get(roomName);*/

        //See
        for (int i = 0; i < seeArrayList.size(); i++) {
            if (allUsers.contains(seeArrayList.get(i))) {
                filteredUsers.add(seeArrayList.get(i));
            }
        }

        //Talk
        for (int i = 0; i < talkArrayList.size(); i++) {
            if (allUsers.contains(talkArrayList.get(i))) {
                filteredUsers.add(talkArrayList.get(i));
            }
        }

        //Hear
        for (int i = 0; i < hearArrayList.size(); i++) {
            if (allUsers.contains(hearArrayList.get(i))) {
                filteredUsers.add(hearArrayList.get(i));
            }
        }

        /*//Chat
        for(int i = 0; i<chat.size(); i++){
            if(allUsers.contains(talkArrayList.get(i))){
                filteredUsers.add(talkArrayList.get(i));
            }
        }*/
        Set<Integer> set = new HashSet<>();
        set.addAll(filteredUsers);
        filteredUsers.clear();
        filteredUsers.addAll(set);

        return filteredUsers;
    }

    //To check whether internet is available or not
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            boolean connected = networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected();
            return connected;

        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
            return false;
        }
    }

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_kk-mm-ss");
        Calendar c = Calendar.getInstance();
        return sdf.format(c.getTime());
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void shareApp(Context context) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = context.getString(R.string.share_text);
        String shareSub = "Kenante";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

   /* public static void loadChatHistory(QBChatDialog dialog, int skipPagination,
                                       final QBEntityCallback<ArrayList<QBChatMessage>> callback) {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setSkip(skipPagination);
        customObjectRequestBuilder.setLimit(Constants.CHAT_HISTORY_ITEMS_PER_PAGE);
        customObjectRequestBuilder.sortDesc(Constants.CHAT_HISTORY_ITEMS_SORT_FIELD);

        QBRestChatService.getDialogMessages(dialog, customObjectRequestBuilder).performAsync(
                new QbEntityCallbackWrapper<ArrayList<QBChatMessage>>(callback) {
                    @Override
                    public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {

                        callback.onSuccess(qbChatMessages, bundle);

                        *//*Set<Integer> userIds = new HashSet<>();
                        for (QBChatMessage message : qbChatMessages) {
                            userIds.add(message.getSenderId());
                        }

                        if (!userIds.isEmpty()) {
                            getUsersFromMessages(qbChatMessages, userIds, callback);
                        } else {
                            callback.onSuccess(qbChatMessages, bundle);
                        }*//*
                        // Not calling super.onSuccess() because
                        // we're want to load chat users before triggering the callback
                    }

                    @Override
                    public void onError(QBResponseException error) {
                        super.onError(error);
                        callback.onError(error);
                    }
                });
    }

    private static void creatingDialog(Activity activity, ArrayList<Integer> occupantsToAdd, int currentUser, int opponentUser, int currentIteration, int lastIteration) {
        QBChatDialog dialog = DialogUtils.buildDialog("Dialog", QBDialogType.PRIVATE, occupantsToAdd);
        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                String dialogId = qbChatDialog.getDialogId();
                DBHelper db = DBHelper.getInstance(activity);
                db.insertChatDialogDetails(currentUser, opponentUser, dialogId);
                if (currentIteration == lastIteration) {
                    //Send Broadcast to Dialogs Activity to proceed further
                    Intent i = new Intent("android.intent.action.MAIN");
                    activity.sendBroadcast(i);
                }
                //new StoreHistory(activity, currentIteration, lastIteration, qbChatDialog).execute();
            }

            @Override
            public void onError(QBResponseException e) {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    public static String getDeviceId() {
        String deviceId;
        deviceId = UUID.randomUUID().toString();
        return deviceId;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void askPermission(Activity activity, String[] PERMISSIONS) {
        if (!hasPermissions(activity, PERMISSIONS)) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, Constants.REQUEST_PERMISSION_KEY);
        }
    }


    public static Typeface getTypeFaceRobotoBlack(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), "font/roboto_black.ttf");
    }

    public static Typeface getTypeFaceRobotoBold(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), "font/roboto_bold.ttf");
    }

    public static Typeface getTypeFaceRobotoLight(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), "font/roboto_light.ttf");
    }

    public static Typeface getTypeFaceRobotoMedium(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), "font/roboto_medium.ttf");
    }

    public static Typeface getTypeFaceRobotoRegular(Context context) {
        return Typeface.createFromAsset(context.getResources().getAssets(), "font/roboto_regular.ttf");
    }

    public static void changeActivityTitleTypeFace(Activity activity, int code) {
        //Code = 1 for Title
        //Code = 2 for Subtitle
        if (activity.getActionBar() != null) {
            SpannableString string = new SpannableString("");
            if (code == 1)
                string = new SpannableString(activity.getActionBar().getTitle());
            else if (code == 2)
                string = new SpannableString(activity.getActionBar().getSubtitle());
            string.setSpan(new CustomTypefaceSpan("", Typeface.createFromAsset(activity.getResources().getAssets(), "font/roboto_black.ttf")), 0, string.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (code == 1)
                activity.getActionBar().setTitle(string);
            else
                activity.getActionBar().setSubtitle(string);
        }
    }

    public static String getImei(Activity activity) {
        String imei = "";
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                imei = telephonyManager.getImei();
            }
        }
        return imei;
    }

    @SuppressLint("MissingPermission")
    public static Hashtable<String, String> listCalendarId(Context c) {

        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars = CalendarContract.Calendars.CONTENT_URI;

        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

        if (managedCursor.moveToFirst()) {
            String calName;
            String calID;
            int cont = 0;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);

            Hashtable<String, String> calenderIds = new Hashtable<>();
            do {
                calName = managedCursor.getString(nameCol);
                calID = managedCursor.getString(idCol);
                if (!calenderIds.contains(calName))
                    calenderIds.put(calName, calID);
                cont++;
            } while (managedCursor.moveToNext());
            managedCursor.close();

            return calenderIds;
        }

        return null;
    }

    public static Boolean addCalendarEvent(Context context, String roomName, long startDate, long endDate, String calendarId) {

        Boolean successful = false;

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId); // XXX pick)
        values.put(CalendarContract.Events.TITLE, "Reminder for " + roomName);
        values.put(CalendarContract.Events.DTSTART, startDate);
        values.put(CalendarContract.Events.DTEND, endDate);
        values.put(CalendarContract.Events.HAS_ALARM, true);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, CalendarContract.Calendars.CALENDAR_TIME_ZONE);
        values.put(CalendarContract.Events.DESCRIPTION, roomName);

        @SuppressLint("MissingPermission") final Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        long dbId = Long.parseLong(uri.getLastPathSegment());

        //Now create a reminder and attach to the reminder

        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, dbId);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_DEFAULT);
        reminders.put(CalendarContract.Reminders.MINUTES, 0);


        @SuppressLint("MissingPermission") final Uri reminder = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminders);

        int added = Integer.parseInt(reminder.getLastPathSegment());

        if (added > 0) {
            successful = true;
            Intent view = new Intent(Intent.ACTION_VIEW);
            view.setData(uri); // enter the uri of the event not the reminder

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            } else {
                view.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            }
            //view the event in calendar
            context.startActivity(view);
        } else
            successful = false;

        return successful;
    }

    //Adding Calendar Event and Reminder

    public static Boolean getEventStatus(Context context, String room) {
        Boolean status = false;

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String[] mProjection = {CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.DESCRIPTION};
        String selection = CalendarContract.Events.DESCRIPTION + " = '" + room + "'";

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            cursor = cr.query(uri, mProjection, selection, null, null);
            if (cursor.getCount() > 0)
                status = true;
            cursor.close();
        }

        return status;
    }

    public static ArrayList<ArrayList<Integer>> getPublishersToSubscribe(String roomName, DBHelper db) {

        if (roomName.equals(""))
            return null;
        ArrayList<Integer> seeList = db.getSeeList(roomName);
        ArrayList<Integer> chatList = db.getChatList(roomName);
        ArrayList<Integer> hearList = db.getHearList(roomName);
        ArrayList<Integer> talkList = db.getTalkList(roomName);

        ArrayList<Integer> commonUsers = new ArrayList<>(seeList);
        //commonUsers.addAll(chatList);
        commonUsers.addAll(hearList);

        Set<Integer> hs = new HashSet<>();
        hs.addAll(commonUsers);
        commonUsers.clear();
        commonUsers.addAll(hs);

        ArrayList<ArrayList<Integer>> mainArray = new ArrayList<>();
        mainArray.add(0, commonUsers);
        mainArray.add(1, seeList);
        mainArray.add(2, hearList);
        mainArray.add(3, chatList);
        mainArray.add(4, talkList);

        return mainArray;
    }

    public static int getWidth(int parentWidth, int size) {
        if (size == 1 || size == 2)
            return parentWidth;
        else if (size == 3 || size == 4 || size == 5 || size == 6)
            return (Integer) parentWidth / 2;
        else if (size == 7 || size == 8 || size == 9)
            return (Integer) parentWidth / 3;
        else if (size > 9)
            return (Integer) parentWidth / 4;

        return 0;
    }

    public static int getHeight(int parentHeight, int size) {
        if (size == 1)
            return parentHeight;
        else if (size == 2 || size == 3 || size == 4)
            return (Integer) parentHeight / 2;
        else if (size == 5 || size == 6)
            return (Integer) parentHeight / 3;
        else if (size == 7 || size == 8 || size == 9)
            return (Integer) parentHeight / 3;
        else if (size > 9)
            return (Integer) parentHeight / 4;

        return 0;
    }

    public static String getDialogScheduleDate(DBHelper db, String roomName) throws ParseException {
        String returnDate = "";
        ScheduleModel schedule = db.getSchedule(roomName);
        DateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String inputDate = schedule.getSch_start_date();
        Date scheduleStartDate = inputDateFormat.parse(inputDate);
        String outputDate = outputDateFormat.format(scheduleStartDate);

        Date dateTime = Calendar.getInstance().getTime();
        String date = inputDateFormat.format(dateTime);
        Date cD = inputDateFormat.parse(date);

        long difference = scheduleStartDate.getTime() - cD.getTime();
        if (difference < 0) {
            returnDate = "Expired";
        } else {
            long hours = TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS);
            if (hours < 24)
                returnDate = outputDate + " Today";
            else if (hours > 24 && hours < 48)
                returnDate = outputDate + " Tomorrow";
            else
                returnDate = outputDate + "(Upcoming)";
        }

        return returnDate;
    }

    public static Date getDialogSchTime(DBHelper db, String roomName) throws ParseException {
        ScheduleModel schedule = db.getSchedule(roomName);
        DateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");
        String inputTime = schedule.getSch_start_time();
        Date date = inputTimeFormat.parse(inputTime);
        return date;
    }

    public static String getDialogScheduleTime(DBHelper db, String roomName, boolean startEndCheck) throws ParseException {
        /*If startEndCheck = true, get start time
        If startEndCheck = false, get end time*/
        String returnTime = "";
        ScheduleModel schedule = db.getSchedule(roomName);
        DateFormat inputTimeFormat = new SimpleDateFormat("HH:mm");
        DateFormat outputTimeFormat = new SimpleDateFormat("hh:mm a");
        String inputTime = "";
        if (startEndCheck)
            inputTime = schedule.getSch_start_time();
        else
            inputTime = schedule.getSch_end_time();
        Date date = inputTimeFormat.parse(inputTime);
        String outputTime = outputTimeFormat.format(date);
        returnTime = outputTime;
        return returnTime;
    }

    public static long getScheduleDates(DBHelper db, String roomName, int code) throws ParseException {
        long returnValue = 0;
        ScheduleModel model = db.getSchedule(roomName);
        if (code == 1) {
            //For start date
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String sD = model.getSch_start_date() + " " + model.getSch_start_time();
            Date startDate = formatter.parse(sD);
            returnValue = startDate.getTime();
        } else if (code == 2) {
            //For end date
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String eD = model.getSch_start_date() + " " + model.getSch_end_time();
            Date endDate = formatter.parse(eD);
            returnValue = endDate.getTime();
        }
        return returnValue;
    }

    public static Date getStartDate(DBHelper db, String roomName) throws ParseException {

        ScheduleModel schedule = db.getSchedule(roomName);
        DateFormat localInputTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String gmt = getGMTFormat(schedule.getTimezone());
        localInputTimeFormat.setTimeZone(TimeZone.getTimeZone(gmt));
        String abc = "";
        String date = schedule.getSch_start_date();
        String time = schedule.getSch_start_time();
        abc = date + " " + time;
        Date d1 = localInputTimeFormat.parse(abc);

        return d1;
    }

    public static Date getEndDate(DBHelper db, String roomName) throws ParseException {

        ScheduleModel schedule = db.getSchedule(roomName);
        DateFormat inputTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String gmt = getGMTFormat(schedule.getTimezone());
        inputTimeFormat.setTimeZone(TimeZone.getTimeZone(gmt));
        String date = schedule.getSch_end_time();
        Date d = inputTimeFormat.parse(date);

        return d;
    }

    private static String getGMTFormat(String gmt) {
        if (gmt == null)
            return "";
        String returnValue = "GMT", first = "", second = "";
        if (gmt.contains("-")) {
            returnValue = returnValue + "-";
        } else if (gmt.contains("+")) {
            returnValue = returnValue + "+";
            gmt.replace("+", "");
        } else
            returnValue = returnValue + "+";

        String[] s1 = gmt.split("\\.");

        if (s1.length > 0) {
            if (s1[0].length() == 1)
                first = "0" + s1[0];
        }
        if (s1.length > 1) {
            if (s1[1].length() == 1) {
                second = s1[1] + "0";
            }
        } else {
            second = "00";
        }
        returnValue = returnValue + first + ":" + second;
        return returnValue;
    }

    public static HashMap<String, String> getSchedule(Activity activity, DBHelper db, String roomName) throws ParseException {

        HashMap<String, String> schedule = new HashMap<>();
        String time = "", day = "", month_date = "", day_night = "";
        Date d = getStartDate(db, roomName);
        //Getting time
        int hours = d.getHours();
        int minutes = d.getMinutes();

        DateFormat timeFormat = null;
        if(activity.getBaseContext().getResources().getConfiguration().locale.getLanguage().equals("th"))
            timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        else
            timeFormat = new SimpleDateFormat("hh:mm aa");
        time = timeFormat.format(d);
        schedule.put("time", time);

        //Getting date
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        day = dayFormat.format(d);
        schedule.put("day", day);

        //Getting month and date
        DateFormat monthFormat = new SimpleDateFormat("MMM");
        month_date = monthFormat.format(d);
        DateFormat dateFormat = new SimpleDateFormat("dd");
        month_date = month_date + " " + dateFormat.format(d);
        month_date = checkIfSameDay(month_date, db, roomName);
        schedule.put("month", month_date);

        //Day_Night Code
        //1 for day
        //2 for night
        if (hours >= 6 && hours < 18)
            day_night = "1";
        else
            day_night = "2";
        schedule.put("day_night", day_night);

        return schedule;
    }

    public static HashMap<String, String> getHistoryFormattedTime(DBHelper db, String roomName) throws ParseException {

        HashMap<String, String> schedule = new HashMap<>();
        String year = "", day = "", month_date = "", day_night = "";
        Date d = getStartDate(db, roomName);
        //Getting time
        int hours = d.getHours();
        int minutes = d.getMinutes();

        DateFormat timeFormat = new SimpleDateFormat("yyyy");
        year = timeFormat.format(d);
        schedule.put("year", year);

        //Getting date
        DateFormat dayFormat = new SimpleDateFormat("EEEE");
        day = dayFormat.format(d);
        schedule.put("day", day);

        //Getting month and date
        DateFormat monthFormat = new SimpleDateFormat("MMM");
        month_date = monthFormat.format(d);
        DateFormat dateFormat = new SimpleDateFormat("dd");
        month_date = month_date + " " + dateFormat.format(d);
        schedule.put("month", month_date);

        Date endDate = getEndDate(db, roomName);
        long difference = endDate.getTime() - d.getTime();
        long min = TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);

        long h = min / 60;
        long m = min % 60;
        schedule.put("duration", String.valueOf(h) + ":" + String.valueOf(m) + " Hours");

        return schedule;
    }

    private static String checkIfSameDay(String month_date, DBHelper db, String roomName) throws ParseException {
        DateFormat inputTimeFormat = new SimpleDateFormat("yyyy-MM-dd");
        inputTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentD = inputTimeFormat.format(Calendar.getInstance().getTime());
        Date currentDate = inputTimeFormat.parse(currentD);

        ScheduleModel schedule = db.getSchedule(roomName);
        String date = schedule.getSch_start_date();
        Date start = inputTimeFormat.parse(date);

        long difference = start.getTime() - currentDate.getTime();
        long hours = TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS);

        if (currentDate.equals(start)) {
            return "Today";
        } else if (hours == 24) {
            return "Tomorrow";
        }
        return month_date;
    }

    public static HashMap<String, ArrayList<KenanteUser>> getDifferentUsersList(DBHelper db, String confRoom) {
        HashMap<String, ArrayList<KenanteUser>> mainHashMap = new HashMap<>();
        ArrayList<KenanteUser> admin = new ArrayList<>();
        ArrayList<KenanteUser> moderator = new ArrayList<>();
        ArrayList<KenanteUser> respondent = new ArrayList<>();
        ArrayList<KenanteUser> client = new ArrayList<>();
        ArrayList<KenanteUser> translator = new ArrayList<>();
        ArrayList<KenanteUser> transcriber = new ArrayList<>();
        ArrayList<KenanteUser> allUsers = db.getAllUsers(confRoom);

        for (KenanteUser user : allUsers) {
            switch (user.getUser_type()) {
                case Constants.ADMIN:
                    admin.add(user);
                    break;

                case Constants.MODERATOR:
                    moderator.add(user);
                    break;

                case Constants.RESPONDENT:
                    respondent.add(user);
                    break;

                case Constants.CLIENT:
                    client.add(user);
                    break;

                case Constants.TRANSLATOR:
                    translator.add(user);
                    break;

                case Constants.RECORDER:
                    transcriber.add(user);
                    break;
            }
        }

        mainHashMap.put(Constants.ADMIN_TEXT, admin);
        mainHashMap.put(Constants.MODERATOR_TEXT, moderator);
        mainHashMap.put(Constants.RESPONDENT_TEXT, respondent);
        mainHashMap.put(Constants.CLIENT_TEXT, client);
        mainHashMap.put(Constants.TRANSLATOR_TEXT, translator);
        mainHashMap.put(Constants.RECORDER_TEXT, transcriber);
        return mainHashMap;
    }

    public static void setListViewDynamicHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        //check adapter if null
        if (adapter == null) {
            return;
        }
        int height = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
        layoutParams.height = height + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(layoutParams);
        listView.requestLayout();
    }

    public static void setExpandableListViewDynamicHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /*public static class makeChatDialogs extends AsyncTask<String, String, String> {

        Activity activity;
        List<Integer> chatUsers;

        public makeChatDialogs(Activity activity, List<Integer> chatUsers) {
            this.activity = activity;
            this.chatUsers = chatUsers;
        }

        @Override
        protected String doInBackground(String... strings) {

            SharedPref sharedPref = SharedPref.getInstance();
            QBUser currentUser = sharedPref.getCurrentUser();
            int currentUserQBId = currentUser.getId();
            if (chatUsers.size() != 0) {
                for (int i = 0; i < chatUsers.size(); i++) {
                    new AddDialogIdToDatabase(activity, currentUserQBId, chatUsers.get(i), i, chatUsers.size() - 1).execute();
                }
            } else {
                //Send Broadcast to Dialogs Activity to proceed further
                Intent i = new Intent("android.intent.action.MAIN");
                activity.sendBroadcast(i);
            }


            return "";

        }
    }

    public static class AddDialogIdToDatabase extends AsyncTask<String, String, String> {

        Activity activity;
        int currentUser, opponentUser, currentIteration, lastIteration;

        public AddDialogIdToDatabase(Activity activity, int currentUser, int opponentUser, int currentIteration, int lastIteration) {
            this.activity = activity;
            this.currentUser = currentUser;
            this.opponentUser = opponentUser;
            this.currentIteration = currentIteration;
            this.lastIteration = lastIteration;
        }

        @Override
        protected String doInBackground(String... strings) {

            ArrayList<Integer> occupantsToAdd = new ArrayList<>();
            occupantsToAdd.add(currentUser);
            occupantsToAdd.add(opponentUser);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    creatingDialog(activity, occupantsToAdd, currentUser, opponentUser, currentIteration, lastIteration);
                }
            });

            return "";
        }
    }*/

    /*public static void downloadFile(Activity activity, InputStream inputStream, String fileName, FullChatAdapter.ViewHolder holder) {
        File storageDirectory = new File(Environment.getExternalStorageDirectory(), Constants.APP_NAME);
        if (!storageDirectory.exists()) {
            if (!storageDirectory.mkdirs()) {
                Log.d(Constants.APP_NAME, "Oops! Failed to create "
                        + Constants.APP_NAME + " directory");
            }
        }
        String fileDir = Environment.getExternalStorageDirectory() + "/" + Constants.APP_NAME + "/" + fileName;
        try {
            OutputStream outputStream = new FileOutputStream(fileDir);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            //Download done
            //Now update this file local url to database
            DBHelper db = DBHelper.getInstance(activity.getApplicationContext());
            db.insertAttachmentLocalURI(fileDir, fileName);
            activity.runOnUiThread(() -> {
                holder.getAttachmentPB().setVisibility(View.GONE);
                holder.getAttachmentDownloadedIV().setVisibility(View.VISIBLE);
                holder.setFileUrl(fileDir);
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    /*public static Drawable getTintedDrawable(@NonNull Context context, @NonNull Drawable inputDrawable, @ColorInt int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(inputDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN);
        return wrapDrawable;
    }*/

}