package vcims.com.vrapid.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import androidx.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.varenia.kenante_core.core.KenanteSettings;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.SharedPref;

/**
 * Created by VCIMS-PC2 on 04-01-2018.
 */

public class AppController extends Application {

    private static AppController instance;
    private final String TAG = AppController.class.getSimpleName();
    SharedPref sharedPref;

    public static AppController getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = AppController.this;
        initSharedPref();
        initPrefLanguage();
        KenanteSettings.Companion.getInstance().init(this);
        KenanteSettings.Companion.getInstance().setVideoEndPoint(Constants.JANUS_SERVER_URL);
        KenanteSettings.Companion.getInstance().setChatEndPoint(Constants.CHAT_END_POINT);
    }

    private void initSharedPref() {
        SharedPref.createInstance(instance);
        sharedPref = SharedPref.getInstance();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initPrefLanguage() {
        String locale = sharedPref.PREF_LANGUAGE;
        if (locale == null)
            return;
        Locale newLocale;
        if (locale.contains("_")) {
            String[] loc = locale.split("_");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                newLocale = new Locale.Builder().setLanguage(loc[0]).setRegion(loc[1]).build();
            } else {
                newLocale = new Locale(loc[0], loc[1]);
            }
        } else
            newLocale = new Locale(locale);
        Locale.setDefault(newLocale);

        Resources res = getBaseContext().getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(newLocale);
        getBaseContext().createConfigurationContext(config);
    }

}
