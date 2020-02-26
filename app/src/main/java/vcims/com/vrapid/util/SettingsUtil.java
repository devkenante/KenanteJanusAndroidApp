package vcims.com.vrapid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import vcims.com.vrapid.R;

/**
 * QuickBlox team
 */
public class SettingsUtil {

    private static final String TAG = SettingsUtil.class.getSimpleName();

    /*private static void setSettingsForMultiCall(int users) {
        //Changed settings from default to lowest for better performance.
        *//*if (users <= 4) {
            setDefaultVideoQuality();
        } else {*//*
            QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.width);
            QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.QBGA_VIDEO.height);
            QBRTCMediaConfig.setVideoHWAcceleration(false);
            QBRTCMediaConfig.setVideoCodec(null);
        //}
    }

    public static void setSettingsStrategy(int users, Context context) {
        setCommonSettings(context);
        if (users==1) {
            setSettingsFromPreferences(context);
        } else {
            setSettingsForMultiCall(users);
        }
    }

    private static void setCommonSettings(Context context) {
        QBRTCMediaConfig.setAudioCodec(QBRTCMediaConfig.AudioCodec.ISAC);
        Log.v(TAG, "audioCodec = " + QBRTCMediaConfig.getAudioCodec());
        QBRTCMediaConfig.setUseBuildInAEC(true);
        Log.v(TAG, "setUseBuildInAEC = " + QBRTCMediaConfig.isUseBuildInAEC());
        QBRTCMediaConfig.setAudioProcessingEnabled(true);
        Log.v(TAG, "isAudioProcessingEnabled = " + QBRTCMediaConfig.isAudioProcessingEnabled());
        QBRTCMediaConfig.setUseOpenSLES(false);
        Log.v(TAG, "isUseOpenSLES = " + QBRTCMediaConfig.isUseOpenSLES());
        //QBRTCMediaConfig.setAudioStartBitrate(256000);
    }

    private static void setSettingsFromPreferences(Context context) {

        // Check HW codec flag.
        QBRTCMediaConfig.setVideoHWAcceleration(true);

        // Get video resolution from settings.
        setVideoQuality(0);
        Log.v(TAG, "resolution = " + QBRTCMediaConfig.getVideoHeight() + "x" + QBRTCMediaConfig.getVideoWidth());

        // Get start bitrate.
        int startBitrate = 0;
        Log.e(TAG, "videoStartBitrate =: " + startBitrate);
        QBRTCMediaConfig.setVideoStartBitrate(startBitrate);
        Log.v(TAG, "videoStartBitrate = " + QBRTCMediaConfig.getVideoStartBitrate());

        int videoCodecItem = 0;
        for (QBRTCMediaConfig.VideoCodec codec : QBRTCMediaConfig.VideoCodec.values()) {
            if (codec.ordinal() == videoCodecItem) {
                Log.e(TAG, "videoCodecItem =: " + codec.getDescription());
                QBRTCMediaConfig.setVideoCodec(codec);
                Log.v(TAG, "videoCodecItem = " + QBRTCMediaConfig.getVideoCodec());
                break;
            }
        }
        // Get camera fps from settings.
        QBRTCMediaConfig.setVideoFps(0);
        Log.v(TAG, "cameraFps = " + QBRTCMediaConfig.getVideoFps());
    }

    public static void configRTCTimers(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        long answerTimeInterval = getPreferenceInt(sharedPref, context,
                R.string.pref_answer_time_interval_key,
                R.string.pref_answer_time_interval_default_value);
        QBRTCConfig.setAnswerTimeInterval(answerTimeInterval);
        Log.e(TAG, "answerTimeInterval = " + answerTimeInterval);

        int disconnectTimeInterval = getPreferenceInt(sharedPref, context,
                R.string.pref_disconnect_time_interval_key,
                R.string.pref_disconnect_time_interval_default_value);
        QBRTCConfig.setDisconnectTime(disconnectTimeInterval);
        Log.e(TAG, "disconnectTimeInterval = " + disconnectTimeInterval);

        long dialingTimeInterval = getPreferenceInt(sharedPref, context,
                R.string.pref_dialing_time_interval_key,
                R.string.pref_dialing_time_interval_default_value);
        QBRTCConfig.setDialingTimeInterval(dialingTimeInterval);
        Log.e(TAG, "dialingTimeInterval = " + dialingTimeInterval);
    }

    private static void setVideoQuality(int resolutionItem) {
        if (resolutionItem != -1) {
            setVideoFromLibraryPreferences(resolutionItem);
        } else {
            setDefaultVideoQuality();
        }
    }

    private static void setDefaultVideoQuality() {
        QBRTCMediaConfig.setVideoWidth(QBRTCMediaConfig.VideoQuality.VGA_VIDEO.width);
        QBRTCMediaConfig.setVideoHeight(QBRTCMediaConfig.VideoQuality.VGA_VIDEO.height);
    }

    private static void setVideoFromLibraryPreferences(int resolutionItem) {
        for (QBRTCMediaConfig.VideoQuality quality : QBRTCMediaConfig.VideoQuality.values()) {
            if (quality.ordinal() == resolutionItem) {
                Log.e(TAG, "resolution =: " + quality.height + ":" + quality.width);
                QBRTCMediaConfig.setVideoHeight(quality.height);
                QBRTCMediaConfig.setVideoWidth(quality.width);
            }
        }
    }

    private static String getPreferenceString(SharedPreferences sharedPref, Context context, int strResKey, int strResDefValue) {
        return sharedPref.getString(context.getString(strResKey), context.getString(strResDefValue));
    }

    private static String getPreferenceString(SharedPreferences sharedPref, Context context, int strResKey, String strResDefValue) {
        return sharedPref.getString(context.getString(strResKey), strResDefValue);
    }

    public static int getPreferenceInt(SharedPreferences sharedPref, Context context, int strResKey, int strResDefValue) {
        return sharedPref.getInt(context.getString(strResKey), Integer.valueOf(context.getString(strResDefValue)));
    }

    private static boolean getPreferenceBoolean(SharedPreferences sharedPref, Context context, int StrRes, int strResDefValue) {
        return sharedPref.getBoolean(context.getString(StrRes), Boolean.valueOf(context.getString(strResDefValue)));
    }*/
}
