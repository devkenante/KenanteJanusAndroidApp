package vcims.com.vrapid.util;

/**
 * Created by VCIMS-PC2 on 04-01-2018.
 */

public class Constants {

    //Quickblox keys
    /*public static final String APP_ID1 = "66113";
    public static final String AUTH_KEY1 = "4XtCdEK8n22qnVY";
    public static final String AUTH_SECRET1 = "GLmN2dy9LBZWGeD";
    public static final String ACCOUNT_KEYY1 = "rHtCZtii2h3S4fdNBx7o";
    public static final String API_DOMAIN1 = "https://api.quickblox.com";
    public static final String CHAT_DOMAIN1 = "chat.quickblox.com";
    public static final String JANUS_SERVER_URL = "wss://janusdev.quickblox.com:8989";
    public static final String JANUS_SERVER_URL = "wss://janus.quickblox.com:8989";
    public static final String JANUS_PROTOCOL = "janus-protocol";
    public static final String JANUS_PLUGIN = "janus.plugin.videoroom";*/

    public static final int REQUEST_PERMISSION_KEY = 1;
    public static final String QB_USERS_FOR_DIALOG = "qb_users";
    public static final String PROGRESS_DIALOG = "p_dialog";

    public static final String EXTRA_DIALOG_ID = "dialog_id";
    public static final String EXTRA_CHAT_DIALOG_ID = "chat_dialog_id";
    public static final String EXTRA_DIALOG_OCCUPANTS = "dialog_occupants";
    public static final String EXTRA_AS_LISTENER = "dialog_listener";
    public static final String EXTRA_USER_TYPE = "user_type";
    public static final String EXTRA_QB_OCCUPANTS_IDS = "qb_occupants_ids";
    public static final String EXTRA_OPPONENT_ID = "opponent_id";
    public static final String EXTRA_PUBLISHED_USERS_ARRAY = "published_array";
    public static final String CURRENT_PRESENT_USERS = "current_present_users";
    public static final String EXTRA_SEE_ARRAY = "see_array";
    public static final String EXTRA_CHAT_ARRAY = "chat_array";
    public static final String EXTRA_HEAR_ARRAY = "hear_array";
    public static final String EXTRA_VIDEO_SHOW_WIDTH = "video_show_width";
    public static final String EXTRA_VIDEO_SHOW_HEIGHT = "video_show_height";
    public static final String EXTRA_USER_NAME = "user_name";
    public static final String EXTRA_FIREBASE_LIST = "firebase_list";
    public static final String EXTRA_FB_LIST_ITEM = "fb_list_item";
    public static final String EXTRA_FB_VIDEO_CLOSE = "fb_video_close";
    public static final String EXTRA_TIME = "time";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_DATE = "date";
    public static final String USER_NAMES = "names";
    public static final String EXTRA_GROUP_ONGOING = "ongoing_group";
    public static final String EXTRA_IS_ROOM_SECURE = "is_room_secure";
    public static final String EXTRA_DYNAMIC_LINK_ENTRY = "is_dynamic_link_entry";

    public static final String EXTRA_FVIDEO_MODEL = "firebase_video";
    public static final String EXTRA_IS_MOD = "is_mod";

    public static final String EXTRA_DYNAMIC_LINK_URI = "dynamic_call_uri";

    public static final String APP_NAME = "Kenante";
    public static final String LOGIN_STATUS = "LOGIN_STATUS";
    public static final String USER_ID = "USER_ID";
    public static final String QB_ID = "QB_ID";
    public static final String LOGIN = "LOGIN";
    public static final String FULLNAME = "FULLNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String EMAIL = "EMAIL";
    public static final String EXT_ID = "EXT_ID";
    public static final String FACEBOOK = "FACEBOOK";
    public static final String TWITTER = "TWITTER";
    public static final String TWITTER_DIGITS = "TWITTER_DIGITS";
    public static final String TAGS = "TAGS";
    public static final String LAST_SIGN_IN = "LAST_SIGN_IN";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String APP_ID = "APP_ID";
    public static final String AUTH_KEY = "AUTH_KEY";
    public static final String AUTH_SECRET = "AUTH_SECRET";
    public static final String ACCOUNT_KEY = "ACCOUNT_KEY";
    public static final String API_DOMAIN = "API_DOMAIN";
    public static final String CHAT_DOMAIN = "CHAT_DOMAIN";
    public static final String JANUS_SERVER = "JANUS_SERVER";
    public static final String JANUS_PROTOCOL = "JANUS_PROTOCOL";
    public static final String JANUS_PLUGIN = "JANUS_PLUGIN";

    public static final String USER_TYPE = "USER_TYPE";
    public static final String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
    public static final String CALL_STARTED = "CALL_STARTED";

    public static final String PREF_LANGUAGE = "pref_language";

    public static final int MODERATOR = 2;
    public static final int RESPONDENT = 1;
    public static final int TRANSLATOR = 3;
    public static final int CLIENT = 4;
    //ADMIN will also record Moderator Screen
    public static final int ADMIN = 0;
    //RECORDER will record Client Screen
    public static final int RECORDER = 10;
    public static final String isIncomingCall = "incoming";

    public static final String RESPONDENT_TEXT = "Respondent";
    public static final String MODERATOR_TEXT = "Moderator";
    public static final String CLIENT_TEXT = "Client";
    public static final String TRANSLATOR_TEXT = "Translator";
    public static final String ADMIN_TEXT = "Admin";
    public static final String RECORDER_TEXT = "Transcriber";

    //Web Services
    public static final String MAIN_URL = "https://vareniacims.com/";
    //public static final String LOGIN_URL = MAIN_URL + "vcimsweb/wsr.php?tasks=vcuserlogin";
    //public static final String LOGIN_URL = MAIN_URL + "vcimsweb/wsr.php?tasks=vcuserlogintest";
    public static final String LOGIN_URL = MAIN_URL + "vcimsweb/wsr.php?tasks=vcuserlogindev";

    //public static final String SYNC_USERS = MAIN_URL + "vcimsweb/wsr.php?tasks=vcusertaglist";
    //public static final String SYNC_USERS = MAIN_URL + "vcimsweb/wsr.php?tasks=vcusertaglisttest";
    public static final String SYNC_USERS = MAIN_URL + "vcimsweb/wsr.php?tasks=vcusertaglistdev";

    public static final String LOGOUT_URL = MAIN_URL + "vcimsweb/wsr.php?tasks=vcuserlogout";

    public static final String ICE_FAILED_REASON = "ICE failed";

    public static final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    public static final int CHAT_HISTORY_ITEMS_PER_PAGE = 20;
    public static final String CHAT_HISTORY_ITEMS_SORT_FIELD = "date_sent";

    public static final String STATUS = "status";
    public static final String KEY = "key";

    //Network Callbacks
    public static final String ON_AVAILABLE = "onAvailable";
    public static final String ON_LOSING = "onLosing";
    public static final String ON_LOST = "onLost";
    public static final String ON_UNAVAILABLE = "onUnavailable";
    public static final String ON_CAPABILITIES_CHANGED = "onCapabilitiesChanged";
    public static final String ON_LINK_PROPERTIES_CHANGED = "onLinkPropertiesChanged";
    public static final String EXTRA_MAX_MS_TO_LIVE = "max_ms_to_live";

    //New Fragment TAGS
    public static final String CONF_DETAILS_FRAG = "conf_details";
    public static final String ADD_USERS_FRAG = "add_users";
    public static final String VIDEO_SHOW_FRAG = "video_show";
    public static final String BOTTOM_CHAT_FRAG = "bottom_chat";
    public static final String FULL_SCREEN_FRAG = "full_screen";
    public static final String ENLARGE_VIDEO_FRAG = "enlarge_video";
    public static final String RECORD_FRAG = "record";
    public static final String FIREBASE_HANDLER_FRAG = "firebase_hanlder";
    public static final String FIREBASE_SHOW_FRAG = "firebase_show";
    public static final String BRIEF_FRAG = "brief_frag";
    public static final String HISTORY_FRAG = "history";
    public static final String FAQ_FRAG = "faq";
    public static final String CHANGE_LANGUAGE_FRAG = "change_language";
    public static final String NOTIFICATION_FRAG = "notification";
    public static final String USERS_LIST_FRAG = "users_list";
    public static final String FILE_OPENER_FRAG = "file_opener";
    public static final String PIP_FRAG = "pip_frag";

    //Firebase Show Fragment
    public static final String GROUP_LIST = "group_list";
    public static final String GROUP_IDS = "group_ids";
    public static final String VIDEO_LIST = "video_list";
    public static final String IMAGE_LIST = "image_list";
    public static final String PPT_LIST = "ppt_list";

    public static final String DIALOG_ID = "dialogId";
    public static final String OCCUPANTS = "occupants";
    public static final String ROOM = "room";

  /*  //QB Constants
    public static final String opponent_pending = "opponent_pending";
    public static final String new_connection = "new_connection";
    public static final String opponent_closed = "opponent_closed";
    public static final String text_status_connect = "text_status_connect";
    public static final String text_status_checking = "text_status_checking";
    public static final String text_status_disconnected = "text_status_disconnected";
    public static final String text_status_no_answer = "text_status_no_answer";
    public static final String text_status_rejected = "text_status_rejected";
    public static final String text_status_hang_up = "text_status_hang_up";*/

    //New Kenante Janus Constants
    public static final String ROOM_ID = "room_id";
    public static final String KID = "k_user_id";
    public static final String NAME = "name";
    public static final String DNAME = "dname";
    public static final String AUDIO_CODEC = "audio_codec";
    public static final String VIDEO_CODEC = "video_codec";
    public static final String RECORDING = "recording";
    public static final String RECORDING_DIR = "recording_fir";
    public static final String BITRATE = "bitrate";
    public static final String JANUS_SERVER_URL = "wss://dev.kenante.com:8989/janus";
    public static final String JANUS_PROTOCOL_VAL = "janus-protocol";
    public static final String JANUS_PLUGIN_VAL = "janus.plugin.videoroom";
    public static final String CHAT_END_POINT = "wss://dev.kenante.com/";

}