package vcims.com.vrapid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.varenia.kenante_chat.core.KenanteChatMessage;
import com.varenia.kenante_chat.enums.KenanteChatMessageAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import vcims.com.vrapid.models.DialogModel;
import vcims.com.vrapid.models.NotificationModel;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.ScheduleModel;
import vcims.com.vrapid.util.StaticMethods;

/**
 * Created by VCIMS-PC2 on 16-01-2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "Users_Database";
    //private static final int DATABASE_VERSION = 4;
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ALL_USERS = "allUsers";
    private static final String ID = "id";
    private static final String KID = "uid";
    //private static final String QB_ID = "qb_id";
    private static final String LOGIN = "login";
    private static final String NAME = "name";
    private static final String DNAME = "dname";
    private static final String PASSWORD = "password";
    private static final String USER_TYPE = "user_type";
    private static final String EMAIL = "email";
    private static final String ROOM_NAME = "room_name";
    private static final String LAST_SIGN_IN = "last_sign_in";
    private static final String CREATED_AT = "created_at";
    private static final String AUDIO_CODEC = "audio_codec";
    private static final String VIDEO_CODEC = "video_codec";
    private static final String RECORDING = "recording";
    private static final String RECORDING_DIR = "recording_dir";
    private static final String BITRATE = "bitrate";

    //Creating table for all users in a specific room.
    public static final String CREATE_TABLE_ALL_USERS = "create table if not exists " + TABLE_ALL_USERS + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KID + " text,"
            + LOGIN + " text,"
            + PASSWORD + " text,"
            + NAME + " text,"
            + DNAME + " text,"
            + USER_TYPE + " text,"
            + EMAIL + " text,"
            + ROOM_NAME + " text,"
            + LAST_SIGN_IN + " text,"
            + CREATED_AT + " text,"
            + AUDIO_CODEC + " text,"
            + VIDEO_CODEC + " text,"
            + RECORDING + " text,"
            + RECORDING_DIR + " text,"
            + BITRATE + " text " + ")";
    private static final String TABLE_RULE = "Rule";
    private static final String ROOM = "room";
    private static final String SEE = "see";
    private static final String TALK = "talk";
    private static final String HEAR = "hear";
    private static final String CHAT = "chat";
    private static final String SELF_VIDEO_OFF = "self_video_off";
    private static final String SELF_AUDIO_OFF = "self_audio_off";
    //Creating table for the rule.
    public static final String CREATE_TABLE_RULE = "create table if not exists " + TABLE_RULE + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ROOM + " text not null unique,"
            + SEE + " text,"
            + TALK + " text,"
            + HEAR + " text,"
            + CHAT + " text,"
            + SELF_VIDEO_OFF + " text,"
            + SELF_AUDIO_OFF + " text )";
    private static final String TABLE_RECORDINGS = "VCallRecordings";
    private static final String FILENAME = "filename";
    private static final String DIRECTORY = "directory";
    //Creating table for video recordings
    public static final String CREATE_TABLE_RECORDINGS = " create table if not exists " + TABLE_RECORDINGS + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ROOM + " text,"
            + FILENAME + " text,"
            + DIRECTORY + " text )";
    private static final String TABLE_CHAT_DIALOGS = "ChatDialogs";
    private static final String CURRENT_USER = "current_user";
    private static final String OPP_USER = "opp_user";
    private static final String DIALOG_ID = "dialog_id";
    private static final String DIALOG_BYTES = "dialog_bytes";
    //Creating table for chat dialogs
    public static final String CREATE_TABLE_CHAT_DIALOGS = " create table if not exists " + TABLE_CHAT_DIALOGS + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CURRENT_USER + " text,"
            + OPP_USER + " text,"
            + DIALOG_ID + " text not null unique)";
    private static final String TABLE_CHAT_HISTORY = "chat_history";
    private static final String ROOM_ID = "room_id";
    private static final String SENDER_ID = "sender_id";
    private static final String RECEIVER_ID = "receiver_id";
    private static final String MESSAGE = "message";
    private static final String MESSAGE_ACTION = "message_action";
    private static final String MEDIA_URL = "media_url";
    private static final String ATTACHMENT_ID = "att_id";
    private static final String ATTACHMENT_QB_URI = "att_qb_uri";
    private static final String ATTACHMENT_NAME = "att_name";
    private static final String ATTACHMENT_TYPE = "att_type";
    private static final String ATTACHMENT_LOCAL_URI = "att_local_uri";
    private static final String TIMESTAMP = "timestamp";
    //Creating table for storing chats
    public static final String CREATE_TABLE_CHAT_HISTORY = " create table if not exists " + TABLE_CHAT_HISTORY + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ROOM_ID + " text,"
            + SENDER_ID + " text,"
            + RECEIVER_ID + " text,"
            + MESSAGE + " text,"
            + MESSAGE_ACTION + " text,"
            + MEDIA_URL + " text,"
            + TIMESTAMP + " text)";
    private static final String TABLE_SCHEDULE = "schedule";
    private static final String TIMEZONE = "timezone";
    private static final String START_DATE = "start_date";
    private static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String SECURE_FLAG = "secure_flag";
    private static final String ROOM_TYPE = "room_type";
    private static final String GENDER = "gender";
    private static final String AGE = "age";
    private static final String SEC = "sec";
    private static final String USER_SHIP = "user_ship";
    private static final String ROOM_NAMES_FLAG = "room_names_flag";
    //Creating table for storing schedule
    public static final String CREATE_TABLE_SCHEDULE = " create table if not exists " + TABLE_SCHEDULE + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ROOM_ID + " integer unique,"
            + ROOM + " text,"
            + ROOM_TYPE + " text,"
            + GENDER + " text,"
            + AGE + " text,"
            + SEC + " text,"
            + USER_SHIP + " text,"
            + TIMEZONE + " text,"
            + START_DATE + " text,"
            + START_TIME + " text,"
            + END_TIME + " text,"
            + ROOM_NAMES_FLAG + " text,"
            + SECURE_FLAG + " text )";
    private static final String TABLE_ATTENDED_HISTORY = "history";
    //Creating table for storing history of projects
    public static final String CREATE_TABLE_HISTORY = " create table if not exists " + TABLE_ATTENDED_HISTORY + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ROOM + " text unique )";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TITLE = "title";
    private static final String TIME = "time";
    //Creating table for storing notifications
    public static final String CREATE_TABLE_NOTIFICATIONS = " create table if not exists " + TABLE_NOTIFICATIONS + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TITLE + " text,"
            + MESSAGE + " text,"
            + TIME + " text )";
    private static DBHelper db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (db == null) {
            db = new DBHelper(context);
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //creating table for all users in a specific room.
        sqLiteDatabase.execSQL(CREATE_TABLE_ALL_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_RULE);
        sqLiteDatabase.execSQL(CREATE_TABLE_RECORDINGS);
        sqLiteDatabase.execSQL(CREATE_TABLE_CHAT_DIALOGS);
        sqLiteDatabase.execSQL(CREATE_TABLE_CHAT_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SCHEDULE);
        sqLiteDatabase.execSQL(CREATE_TABLE_HISTORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_NOTIFICATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        /*if (i == i1) {
            Log.e(TAG, "SAME DATABASE");
        } else {
            //Switch case on older versions
            switch (i){
                case 1:
                    //New version is 2
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + SECURE_FLAG + " VARCHAR(5)");
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + ROOM_ID + " integer");
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + ROOM_NAMES_FLAG + " text");

                case 2:
                    //New version is 3
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_RULE + " ADD COLUMN " + SELF_VIDEO_OFF + " VARCHAR(5)");
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_RULE + " ADD COLUMN " + SELF_AUDIO_OFF + " VARCHAR(5)");
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + ROOM_NAMES_FLAG + " text");

                case 3:
                    //New version is 4
                    sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_SCHEDULE + " ADD COLUMN " + ROOM_NAMES_FLAG + " text");
            }
        }*/
    }

    public void insertUsers(ArrayList<KenanteUser> usersArray) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            if (usersArray != null) {
                for (int i = 0; i < usersArray.size(); i++) {
                    KenanteUser user = usersArray.get(i);
                    String query = "select * from " + TABLE_ALL_USERS + " where " + KID + " = " + user.getKid() + " AND " + ROOM_NAME + " = '" + user.getRoomName() + "'";
                    Cursor res = db.rawQuery(query, null);
                    if (res.getCount() == 0) {
                        ContentValues cv = new ContentValues();
                        cv.put(KID, user.getKid());
                        cv.put(LOGIN, user.getLogin());
                        cv.put(NAME, user.getName());
                        cv.put(DNAME, user.getDname());
                        cv.put(PASSWORD, user.getPassword());
                        cv.put(EMAIL, user.getEmail());
                        cv.put(USER_TYPE, user.getUser_type());
                        cv.put(ROOM_NAME, user.getRoomName());
                        cv.put(LAST_SIGN_IN, user.getLast_sign_in());
                        cv.put(CREATED_AT, user.getCreated_at());
                        cv.put(AUDIO_CODEC, user.getAudioCodec());
                        cv.put(VIDEO_CODEC, user.getVideoCodec());
                        cv.put(RECORDING, user.getRecording());
                        cv.put(RECORDING_DIR, user.getRecording_dir());
                        cv.put(BITRATE, user.getBitrate());
                        db.insert(TABLE_ALL_USERS, null, cv);
                        cv.clear();
                    }
                    res.close();
                }
            }
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALL_USERS, null, null);
        db.close();
    }

    public KenanteUser getUserByUID(String currentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ALL_USERS + " where " + KID + " = " + currentId;
        KenanteUser user = new KenanteUser();
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            user.setKid(res.getInt(res.getColumnIndex(KID)));
            user.setLogin(res.getString(res.getColumnIndex(LOGIN)));
            user.setPassword(res.getString(res.getColumnIndex(PASSWORD)));
            user.setUser_type(res.getInt(res.getColumnIndex(USER_TYPE)));
            user.setName(res.getString(res.getColumnIndex(NAME)));
            user.setDname(res.getString(res.getColumnIndex(DNAME)));
            user.setEmail(res.getString(res.getColumnIndex(EMAIL)));
            user.setRoomName(res.getString(res.getColumnIndex(ROOM_NAME)));
            user.setLast_sign_in(res.getString(res.getColumnIndex(LAST_SIGN_IN)));
            user.setCreated_at(res.getString(res.getColumnIndex(CREATED_AT)));
            user.setAudioCodec(res.getString(res.getColumnIndex(AUDIO_CODEC)));
            user.setVideoCodec(res.getString(res.getColumnIndex(VIDEO_CODEC)));
            Boolean isRecording = res.getInt(res.getColumnIndex(RECORDING)) > 0;
            user.setRecording(isRecording);
            user.setRecording_dir(res.getString(res.getColumnIndex(RECORDING_DIR)));
            user.setBitrate(res.getString(res.getColumnIndex(BITRATE)));
        }
        res.close();
        return user;
    }

    //Get list of all users according to room name
    public ArrayList<KenanteUser> getUsersByTag(String roomName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<KenanteUser> arrayList = new ArrayList<>();
        String query = "select * from " + TABLE_ALL_USERS + " where " + ROOM_NAME + " = '" + roomName + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() != 0) {
            if (res.moveToFirst()) {
                do {
                    KenanteUser user = new KenanteUser();
                    user.setKid(res.getInt(res.getColumnIndex(KID)));
                    user.setLogin(res.getString(res.getColumnIndex(LOGIN)));
                    user.setPassword(res.getString(res.getColumnIndex(PASSWORD)));
                    user.setUser_type(res.getInt(res.getColumnIndex(USER_TYPE)));
                    user.setName(res.getString(res.getColumnIndex(NAME)));
                    user.setDname(res.getString(res.getColumnIndex(DNAME)));
                    user.setRoomName(res.getString(res.getColumnIndex(ROOM_NAME)));
                    user.setLast_sign_in(res.getString(res.getColumnIndex(LAST_SIGN_IN)));
                    user.setCreated_at(res.getString(res.getColumnIndex(CREATED_AT)));
                    arrayList.add(user);
                } while (res.moveToNext());
            }
        }
        return arrayList;
    }

    //Get user by quickblox ID
    public KenanteUser getUserByQBID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        KenanteUser user = new KenanteUser();
        String query = "select * from " + TABLE_ALL_USERS + " where " + KID + " = " + id;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            user.setKid(res.getInt(res.getColumnIndex(KID)));
            user.setLogin(res.getString(res.getColumnIndex(LOGIN)));
            user.setPassword(res.getString(res.getColumnIndex(PASSWORD)));
            user.setUser_type(res.getInt(res.getColumnIndex(USER_TYPE)));
            user.setName(res.getString(res.getColumnIndex(NAME)));
            user.setDname(res.getString(res.getColumnIndex(DNAME)));
            user.setRoomName(res.getString(res.getColumnIndex(ROOM_NAME)));
            user.setLast_sign_in(res.getString(res.getColumnIndex(LAST_SIGN_IN)));
            user.setCreated_at(res.getString(res.getColumnIndex(CREATED_AT)));
        }
        res.close();
        db.close();
        return user;
    }

    //Get all users
    public ArrayList<KenanteUser> getAllUsers(String confRoom) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<KenanteUser> users = new ArrayList<>();
        String query = "select * from " + TABLE_ALL_USERS + " where " + ROOM_NAME + " = '" + confRoom + "'" + " GROUP BY " + KID + " ORDER BY " + ID;
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            if (res.moveToFirst()) {
                do {
                    KenanteUser user = new KenanteUser();
                    user.setKid(res.getInt(res.getColumnIndex(KID)));
                    user.setLogin(res.getString(res.getColumnIndex(LOGIN)));
                    user.setPassword(res.getString(res.getColumnIndex(PASSWORD)));
                    user.setUser_type(res.getInt(res.getColumnIndex(USER_TYPE)));
                    user.setName(res.getString(res.getColumnIndex(NAME)));
                    user.setDname(res.getString(res.getColumnIndex(DNAME)));
                    user.setRoomName(res.getString(res.getColumnIndex(ROOM_NAME)));
                    user.setLast_sign_in(res.getString(res.getColumnIndex(LAST_SIGN_IN)));
                    user.setCreated_at(res.getString(res.getColumnIndex(CREATED_AT)));
                    users.add(user);
                } while (res.moveToNext());
            }
        }
        res.close();
        return users;
    }

    public void storeRule(String roomName, ArrayList<Integer> see, ArrayList<Integer> talk, ArrayList<Integer> hear, ArrayList<Integer> chat, int selfVideoOff, int selfAudioOff) {
        try {
            //Getting values from arraylist and storing them in a string
            String s = StaticMethods.arrayListToString(see);
            String t = StaticMethods.arrayListToString(talk);
            String h = StaticMethods.arrayListToString(hear);
            String c = StaticMethods.arrayListToString(chat);

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put(ROOM, roomName);
            cv.put(SEE, s);
            cv.put(TALK, t);
            cv.put(HEAR, h);
            cv.put(CHAT, c);
            cv.put(SELF_AUDIO_OFF, selfAudioOff);
            cv.put(SELF_VIDEO_OFF, selfVideoOff);
            try {
                db.insert(TABLE_RULE, null, cv);
            } catch (SQLiteException e) {

            }
            cv.clear();
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteAllRules() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_RULE, null, null);
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteAllSchedule() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_SCHEDULE, null, null);
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void deleteRule(String roomName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_RULE + " where " + ROOM + " = '" + roomName + "'";
        db.rawQuery(query, null);
        db.close();
    }

    //Get See List
    public ArrayList<Integer> getSeeList(String room) {
        ArrayList<Integer> seeArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String see = "";
        String query = "select * from " + TABLE_RULE + " where " + ROOM + " = " + "'" + room + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            see = res.getString(res.getColumnIndex(SEE));
        }
        if (!see.equals("")) {
            String[] s = see.split(",");
            for (int i = 0; i < s.length; i++) {
                seeArray.add(Integer.valueOf(s[i]));
            }
        }
        return seeArray;
    }

    //Get Talk List
    public ArrayList<Integer> getTalkList(String room) {
        ArrayList<Integer> talkArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String talk = "";
        String query = "select * from " + TABLE_RULE + " where " + ROOM + " = " + "'" + room + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            talk = res.getString(res.getColumnIndex(TALK));
        }
        if (!talk.equals("")) {
            String[] t = talk.split(",");
            for (int i = 0; i < t.length; i++) {
                talkArray.add(Integer.valueOf(t[i]));
            }
        }
        return talkArray;
    }

    //Get Hear List
    public ArrayList<Integer> getHearList(String room) {
        ArrayList<Integer> hearArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String hear = "";
        String query = "select * from " + TABLE_RULE + " where " + ROOM + " = " + "'" + room + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            hear = res.getString(res.getColumnIndex(HEAR));
        }
        if (!hear.equals("")) {
            String[] h = hear.split(",");
            for (int i = 0; i < h.length; i++) {
                hearArray.add(Integer.valueOf(h[i]));
            }
        }
        return hearArray;
    }

    //Get Chat List
    public ArrayList<Integer> getChatList(String room) {
        ArrayList<Integer> chatArray = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String chat = "";
        String query = "select * from " + TABLE_RULE + " where " + ROOM + " = '" + room + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            chat = res.getString(res.getColumnIndex(CHAT));
        }
        if (!chat.equals("")) {
            String[] c = chat.split(",");
            for (int i = 0; i < c.length; i++) {
                chatArray.add(Integer.valueOf(c[i]));
            }
        }
        return chatArray;
    }

    //Get all the quickblox ids from see, hear, talk list
    public ArrayList<Integer> getRuleIds(String roomName) {
        ArrayList<Integer> allRuleIds = new ArrayList<>();
        ArrayList<Integer> seeList = getSeeList(roomName);
        ArrayList<Integer> hearList = getHearList(roomName);
        ArrayList<Integer> talkList = getTalkList(roomName);

        //Adding ids from see list
        for (int i = 0; i < seeList.size(); i++) {
            allRuleIds.add(seeList.get(i));
        }
        //Adding ids from hear list
        for (int i = 0; i < hearList.size(); i++) {
            if (!allRuleIds.contains(hearList.get(i)))
                allRuleIds.add(hearList.get(i));
        }
        //Adding ids from talk list
        for (int i = 0; i < talkList.size(); i++) {
            if (!allRuleIds.contains(talkList.get(i)))
                allRuleIds.add(talkList.get(i));
        }
        return allRuleIds;
    }

    public String getNameFromId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = "";
        String query = "select * from " + TABLE_ALL_USERS + " where " + KID + " = " + "'" + id + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            fullName = res.getString(res.getColumnIndex(NAME));
        }
        return fullName;
    }

    //Get moderator ids according to room name
    public ArrayList<Integer> getModeratorIds(String roomName, String currentUserType) {
        ArrayList<Integer> modIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ALL_USERS + " where " + ROOM_NAME + " = '" + roomName + "' AND " + USER_TYPE + " = '"
                + currentUserType + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                modIds.add(res.getInt(res.getColumnIndex(KID)));
            } while (res.moveToNext());
        }
        return modIds;
    }

    /*public QBUser getQBUser(int qb_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ALL_USERS + " where " + QB_ID + " = '" + String.valueOf(qb_id) + "'";
        Cursor res = db.rawQuery(query, null);
        QBUser user = new QBUser();
        if (res.moveToFirst()) {
            user.setId(Integer.valueOf(res.getString(res.getColumnIndex(QB_ID))));
            user.setLogin(res.getString(res.getColumnIndex(LOGIN)));
            user.setFullName(res.getString(res.getColumnIndex(FULL_NAME)));
            user.setPassword(res.getString(res.getColumnIndex(PASSWORD)));
            user.setEmail(res.getString(res.getColumnIndex(EMAIL)));
            user.setExternalId(res.getString(res.getColumnIndex(EXT_ID)));
            user.setFacebookId(res.getString(res.getColumnIndex(FACEBOOK)));
            user.setTwitterId(res.getString(res.getColumnIndex(TWITTER)));
            user.setTwitterDigitsId(res.getString(res.getColumnIndex(TWITTER_DIGITS)));
            StringifyArrayList tags = new StringifyArrayList();
            tags.add(res.getString(res.getColumnIndex(TAGS)));
        }
        return user;
    }*/

    //Storing information of videos recorded

    public void insertVideoFile(String currentRoom, String filename, String directory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ROOM, currentRoom);
        cv.put(FILENAME, filename);
        cv.put(DIRECTORY, directory);
        db.insert(TABLE_RECORDINGS, null, cv);
        cv.clear();
        db.close();
    }

    //Inserting Chat Dialog details

    /*public void insertChatDialog(int currentUser, int opponentUser, String dialogId, byte[] dialog){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CURRENT_USER, currentUser);
        cv.put(OPP_USER, opponentUser);
        cv.put(DIALOG_ID, dialogId);
        cv.put(DIALOG_BYTES, dialog);
        db.insert(TABLE_CHAT_DIALOGS, null, cv);
        cv.clear();
        db.close();
    }*/

    public void insertChatDialogDetails(int currentUser, int opp_user, String dialogId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(CURRENT_USER, currentUser);
        cv.put(OPP_USER, opp_user);
        cv.put(DIALOG_ID, dialogId);
        db.insert(TABLE_CHAT_DIALOGS, null, cv);
        cv.clear();
        db.close();
    }

    //Check if dialog id already exists

    public List<Object> checkChatDialogIdExists(int currentUser, int oppUser) {
        List<Object> valuesToReturn = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_CHAT_DIALOGS + " where " + CURRENT_USER + " = " + currentUser + " AND " + OPP_USER + " = " + oppUser;
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            valuesToReturn.add(true);
            if (res.moveToFirst())
                valuesToReturn.add(res.getString(res.getColumnIndex(DIALOG_ID)));
        } else {
            valuesToReturn.add(false);
            valuesToReturn.add("");
        }
        return valuesToReturn;
    }

    //Retrieving chat dialogs

    /*public QBChatDialog getChatDialog(int currentUser, int opponentUser){
        QBChatDialog chatDialog;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_CHAT_DIALOGS + " where " + ID + " = " + " ( select MAX(id) from " + TABLE_CHAT_DIALOGS + " where " + CURRENT_USER + " = " + currentUserId + " AND " + OPP_USER + " = " + oppUserId + " )";
        Cursor res = db.rawQuery(query, null);
        if(res.moveToFirst()){
            bute
            chatDialog =
        }
    }*/

    public String getLastChatDialogID(int currentUserId, int oppUserId) {

        String dialogId = "";
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = " select * from " + TABLE_CHAT_DIALOGS + " where " + USER_ID + " = " + currentUserId;
        String query = "select * from " + TABLE_CHAT_DIALOGS + " where " + ID + " = " + " ( select MAX(id) from " + TABLE_CHAT_DIALOGS + " where " + CURRENT_USER + " = " + currentUserId + " AND " + OPP_USER + " = " + oppUserId + " )";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            dialogId = res.getString(res.getColumnIndex(DIALOG_ID));
        }
        res.close();
        db.close();
        return dialogId;
    }

    //Delete all the data on Logout
    public void LogOutDelete() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> tables = new ArrayList<String>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence"))
                tables.add(tableName);
            cursor.moveToNext();
        }
        cursor.close();

        for (String tableName : tables) {
            //  db.execSQL("DROP TABLE IF EXISTS " + tableName);
            db.delete(tableName, null, null);
        }
    }

    //Check whether user exists in chat dialog table
    /*public boolean userExists(int userId){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_CHAT_DIALOGS + " where " + USER_ID + " = " + userId;
        Cursor res = db.rawQuery(query,null);
        if(res.getCount()>=1)
            return true;
        else
            return false;
    }*/

    public void insertSchedule(ScheduleModel model) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ROOM_ID, model.getId());
            cv.put(ROOM, model.getRoom());
            cv.put(ROOM_TYPE, model.getRoom_type());
            cv.put(GENDER, model.getGender());
            cv.put(AGE, model.getAge());
            cv.put(SEC, model.getSec());
            cv.put(USER_SHIP, model.getUsership());
            cv.put(TIMEZONE, model.getTimezone());
            cv.put(START_DATE, model.getSch_start_date());
            cv.put(START_TIME, model.getSch_start_time());
            cv.put(END_TIME, model.getSch_end_time());
            cv.put(SECURE_FLAG, model.getSecureFlag());
            db.insert(TABLE_SCHEDULE, null, cv);
            cv.clear();
            db.close();
        } catch (SQLiteException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void updateRoomNameFlag(String room) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ROOM_NAMES_FLAG, "1");
        db.update(TABLE_SCHEDULE, cv, ROOM + " = '" + room + "'", null);
    }

    public Boolean thisRoomUserNamesStored(String room) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_SCHEDULE + " where " + ROOM + " = '" + room + "' AND " + ROOM_NAMES_FLAG + " = '1'";
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ScheduleModel getSchedule(String roomName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ScheduleModel model = new ScheduleModel();
        String query = "select * from " + TABLE_SCHEDULE + " where " + ROOM + " = '" + roomName + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            model.setTimezone(res.getString(res.getColumnIndex(TIMEZONE)));
            model.setSch_start_date(res.getString(res.getColumnIndex(START_DATE)));
            model.setSch_start_time(res.getString(res.getColumnIndex(START_TIME)));
            model.setSch_end_time(res.getString(res.getColumnIndex(END_TIME)));
            model.setRoom_type(res.getString(res.getColumnIndex(ROOM_TYPE)));
            model.setGender(res.getString(res.getColumnIndex(GENDER)));
            model.setAge(res.getString(res.getColumnIndex(AGE)));
            model.setSec(res.getString(res.getColumnIndex(SEC)));
            model.setUsership(res.getString(res.getColumnIndex(USER_SHIP)));
        }
        res.close();
        db.close();
        return model;
    }

    public void deleteSchedule(String roomName) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_SCHEDULE + " where " + ROOM + " = '" + roomName + "'";
        db.rawQuery(query, null);
        db.close();
    }

    public ArrayList<String> getConfRooms() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> confRooms = new ArrayList<>();
        String query = "select distinct " + ROOM_NAME + " from " + TABLE_ALL_USERS;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                confRooms.add(res.getString(0));
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return confRooms;
    }

    public ArrayList<String> getAllTags() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> allTags = new ArrayList<>();
        String query = "select * from " + TABLE_RULE;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                allTags.add(res.getString(res.getColumnIndex(ROOM)));
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return allTags;
    }

    public ArrayList<DialogModel> getAllDialos() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<DialogModel> dialogs = new ArrayList<>();
        String query = "select * from " + TABLE_SCHEDULE;
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            if (res.moveToFirst()) {
                do {
                    DialogModel model = new DialogModel();
                    model.setId(res.getInt(res.getColumnIndex(ROOM_ID)));
                    model.setName(res.getString(res.getColumnIndex(ROOM)));
                    dialogs.add(model);
                } while (res.moveToNext());
            }
        }
        res.close();
        db.close();
        return dialogs;
    }

    public int getUserType(int qbId) {
        int userType = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ALL_USERS + " where " + KID + " = " + qbId;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            userType = res.getInt(res.getColumnIndex(USER_TYPE));
        }
        res.close();
        db.close();
        return userType;
    }

    public ArrayList<KenanteUser> getAscSortedUsers(String roomName) {
        ArrayList<KenanteUser> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ALL_USERS + " where " + ROOM_NAME + " = '" + roomName + "' ORDER BY " + KID;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                KenanteUser user = new KenanteUser();
                user.setKid(res.getInt(res.getColumnIndex(KID)));
                user.setLogin(res.getString(res.getColumnIndex(LOGIN)));
                user.setPassword(res.getString(res.getColumnIndex(PASSWORD)));
                user.setUser_type(res.getInt(res.getColumnIndex(USER_TYPE)));
                user.setName(res.getString(res.getColumnIndex(NAME)));
                user.setDname(res.getString(res.getColumnIndex(DNAME)));
                user.setRoomName(res.getString(res.getColumnIndex(ROOM_NAME)));
                user.setLast_sign_in(res.getString(res.getColumnIndex(LAST_SIGN_IN)));
                user.setCreated_at(res.getString(res.getColumnIndex(CREATED_AT)));
                users.add(user);
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return users;
    }

    public int getUserType(int kid, String confroom) {
        SQLiteDatabase db = this.getReadableDatabase();
        int usertype = -1;
        String query = "select * from " + TABLE_ALL_USERS + " where " + KID + " = " + kid + " AND " + ROOM_NAME + " = '" + confroom + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            usertype = res.getInt(res.getColumnIndex(USER_TYPE));
        }
        res.close();
        db.close();
        return usertype;
    }

    public HashMap<Integer, String> getNamesOfUser(String roomName, boolean longNames) {
        HashMap<Integer, String> values = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_ALL_USERS + " where " + ROOM_NAME + " = '" + roomName + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                int integer = res.getInt(res.getColumnIndex(KID));
                String name = "";
                if (longNames)
                    name = res.getString(res.getColumnIndex(DNAME));
                else
                    name = res.getString(res.getColumnIndex(NAME));
                values.put(integer, name);
            }
            while (res.moveToNext());
        }
        res.close();
        db.close();
        return values;
    }

    public HashMap<String, String> getAudioVideoStatus(String roomName) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> audioVideoStatus = new HashMap<>();
        String query = "select " + SELF_AUDIO_OFF + ", " + SELF_VIDEO_OFF + " from " + TABLE_RULE + " where " + ROOM + " = '" + roomName + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            if (res.moveToFirst()) {
                audioVideoStatus.put("audio", res.getString(res.getColumnIndex(SELF_AUDIO_OFF)));
                audioVideoStatus.put("video", res.getString(res.getColumnIndex(SELF_VIDEO_OFF)));
            }
        }
        res.close();
        db.close();
        return audioVideoStatus;
    }

    public void insertChatMessage(int senderId, int receiverId, String message, String attachmentId, String attachment_uri,
                                  String attachment_name, String attachment_type, String att_file_path, long timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!db.isOpen())
            return;
        ContentValues cv = new ContentValues();
        cv.put(SENDER_ID, senderId);
        cv.put(RECEIVER_ID, receiverId);
        cv.put(MESSAGE, message);
        cv.put(ATTACHMENT_ID, attachmentId);
        cv.put(ATTACHMENT_QB_URI, attachment_uri);
        cv.put(ATTACHMENT_NAME, attachment_name);
        cv.put(ATTACHMENT_TYPE, attachment_type);
        cv.put(ATTACHMENT_LOCAL_URI, att_file_path);
        cv.put(TIMESTAMP, timestamp);
        db.insert(TABLE_CHAT_HISTORY, null, cv);
        cv.clear();
        db.close();
    }

    public void insertChatMessage(KenanteChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!db.isOpen())
            return;
        ContentValues cv = new ContentValues();
        cv.put(ROOM_ID, chatMessage.getRoomId());
        cv.put(SENDER_ID, chatMessage.getSenderId());
        cv.put(RECEIVER_ID, chatMessage.getReceiverId());
        cv.put(MESSAGE, chatMessage.getMessage());
        //cv.put(MEDIA_URL, chatMessage.getMediaUrl());
        cv.put(MESSAGE_ACTION, chatMessage.getAction().toString());
        cv.put(TIMESTAMP, chatMessage.getTimestamp());
        db.insert(TABLE_CHAT_HISTORY, null, cv);
        cv.clear();
        db.close();
    }

    /*public void insertAttachmentLocalURI(String localUri, String fileName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ATTACHMENT_LOCAL_URI, localUri);
        db.update(TABLE_CHAT_HISTORY, cv, ATTACHMENT_NAME + " = '" + fileName + "'", null);
        cv.clear();
        db.close();
    }

    public String getAttachmentLocalURI(String fileName){
        String localUri = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_CHAT_HISTORY + " where " + ATTACHMENT_NAME + " = '" + fileName + "'";
        Cursor res = db.rawQuery(query, null);
        if(res.getCount()>0){
            if(res.moveToFirst())
                localUri = res.getString(res.getColumnIndex(ATTACHMENT_LOCAL_URI));
            if(localUri == null)
                localUri = "";
        }
        res.close();
        db.close();
        return localUri;
    }*/

    /*public ArrayList<QBChatMessage> getLastTwoChatMessages(int receiverId) {
        ArrayList<QBChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_CHAT_HISTORY + " where " + SENDER_ID + " = " +
                receiverId + " OR " + RECEIVER_ID + " = " + receiverId + " ORDER BY " + TIMESTAMP + " DESC LIMIT 2";
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                QBChatMessage message = new QBChatMessage();
                message.setSenderId(res.getInt(res.getColumnIndex(SENDER_ID)));
                message.setRecipientId(res.getInt(res.getColumnIndex(RECEIVER_ID)));
                message.setBody(res.getString(res.getColumnIndex(MESSAGE)));
                String id = res.getString(res.getColumnIndex(ATTACHMENT_ID));
                String url = res.getString(res.getColumnIndex(ATTACHMENT_QB_URI));
                String name = res.getString(res.getColumnIndex(ATTACHMENT_NAME));
                String type = res.getString(res.getColumnIndex(ATTACHMENT_TYPE));
                if(!url.equals("")) {
                    QBAttachment attachment = new QBAttachment(type);
                    attachment.setId(id);
                    attachment.setUrl(url);
                    attachment.setName(name);
                    Collection<QBAttachment> attachments = new ArrayList<>();
                    attachments.add(attachment);
                    message.setAttachments(attachments);
                }
                messages.add(message);
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return messages;
    }*/

    public ArrayList<KenanteChatMessage> getChatMessages(int receiverId, int roomId) {
        ArrayList<KenanteChatMessage> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        //Todo: Change order according to timestamp
        String query = "select * from " + TABLE_CHAT_HISTORY + " where " + ROOM_ID + " = " + roomId + " AND "
                + SENDER_ID + " = " + receiverId + " OR " + RECEIVER_ID + " = " + receiverId;

        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                int senderId = res.getInt(res.getColumnIndex(SENDER_ID));
                int recId = res.getInt(res.getColumnIndex(RECEIVER_ID));
                String message = res.getString(res.getColumnIndex(MESSAGE));
                String action = res.getString(res.getColumnIndex(MESSAGE_ACTION));
                KenanteChatMessageAction act = null;
                if(action.equals(KenanteChatMessageAction.Text.name()))
                    act = KenanteChatMessageAction.Text;
                else
                    act = KenanteChatMessageAction.Media;
                String mediaUrl = res.getString(res.getColumnIndex(MEDIA_URL));
                String timestamp = res.getString(res.getColumnIndex(TIMESTAMP));

                KenanteChatMessage chatMessage = new KenanteChatMessage(
                        roomId, senderId, recId, message, act);
                chatMessage.setTimestamp(timestamp);

                messages.add(chatMessage);
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return messages;
    }

    public void insertAttendedHistory(String roomName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ROOM, roomName);
        db.insert(TABLE_ATTENDED_HISTORY, null, cv);
        cv.clear();
        db.close();
    }

    public ArrayList<String> getAttendedHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> attended = new ArrayList<>();
        String query = " select * from " + TABLE_ATTENDED_HISTORY;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                attended.add(res.getString(res.getColumnIndex(ROOM)));
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return attended;
    }

    public void insertNotification(NotificationModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, model.getTitle());
        cv.put(MESSAGE, model.getMessage());
        cv.put(TIME, model.getTime());
        db.insert(TABLE_NOTIFICATIONS, null, cv);
        cv.clear();
        db.close();
    }

    public ArrayList<NotificationModel> getNotifications() {
        ArrayList<NotificationModel> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_NOTIFICATIONS;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            do {
                NotificationModel model = new NotificationModel();
                model.setTitle(res.getString(res.getColumnIndex(TITLE)));
                model.setMessage(res.getString(res.getColumnIndex(MESSAGE)));
                model.setTime(res.getString(res.getColumnIndex(TIME)));
                arrayList.add(model);
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return arrayList;
    }

    public String getUserTypeName(int qbID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String name = "";
        String query = " select * from " + TABLE_ALL_USERS + " WHERE " + KID + " = " + qbID;
        Cursor res = db.rawQuery(query, null);
        if (res.moveToFirst()) {
            name = res.getString(res.getColumnIndex(NAME));
        }
        res.close();
        db.close();
        return name;
    }

    public Boolean isThisRoomSecure(String room) {
        int flag = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from " + TABLE_SCHEDULE + " where " + ROOM + " = '" + room + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            if (res.moveToFirst()) {
                flag = res.getInt(res.getColumnIndex(SECURE_FLAG));
            }
        }
        if (flag == 1)
            return true;
        else
            return false;
    }

    public Set<Integer> getAllUsersIds(String roomName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Set<Integer> ids = new CopyOnWriteArraySet<>();
        String query = "select * from " + TABLE_ALL_USERS + " where " + ROOM_NAME + " = '" + roomName + "'";
        Cursor res = db.rawQuery(query, null);
        if (res.getCount() > 0) {
            if (res.moveToFirst()) {
                do {
                    int id = res.getInt(res.getColumnIndex(KID));
                    ids.add(id);
                } while (res.moveToNext());
            }
        }
        res.close();
        db.close();
        return ids;
    }

}
