package vcims.com.vrapid.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import vcims.com.vrapid.R;
import vcims.com.vrapid.activities.DashboardScreen;
import vcims.com.vrapid.adapter.LanguagesAdapter;
import vcims.com.vrapid.util.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeLanguageFragment extends Fragment implements LanguagesAdapter.OnEventClickListener {

    private DashboardScreen dashboard;
    private RecyclerView changeLanguageRV;
    private ImageView changeLanguageBackButton;
    private ArrayList<ChangeLanguageModel> languages;
    private SparseArray<LanguagesAdapter.CurrentViewHolder> holders;
    private Locale defaultLocale;
    private Switch lastEnabledSwitch;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DashboardScreen){
            dashboard = (DashboardScreen) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_language, container, false);

        initialize(view);
        getDefaultLocale();
        clickListener();
        initListOfLanguages();
        initAdapter();

        return view;
    }

    private void initialize(View view) {
        changeLanguageRV = view.findViewById(R.id.changeLanguageRV);
        changeLanguageBackButton = view.findViewById(R.id.changeLanguageBackButton);
        languages = new ArrayList<>();
        holders = new SparseArray<>();
    }

    private void getDefaultLocale() {
        Resources resources = getActivity().getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            defaultLocale = configuration.getLocales().get(0);
        else
            defaultLocale = configuration.locale;
    }

    private void clickListener() {
        changeLanguageBackButton.setOnClickListener(view -> getFragmentManager().popBackStackImmediate());
    }

    private void initListOfLanguages() {
        languages.add(new ChangeLanguageModel("Chinese Simplified", "zh_CN"));
        languages.add(new ChangeLanguageModel("Chinese Traditional", "zh_HK"));
        languages.add(new ChangeLanguageModel("English", "en"));
        languages.add(new ChangeLanguageModel("Filipino", "fil"));
        languages.add(new ChangeLanguageModel("Hindi", "hi"));
        languages.add(new ChangeLanguageModel("Indonesian", "in_ID"));
        languages.add(new ChangeLanguageModel("Malay", "ms"));
        languages.add(new ChangeLanguageModel("Thai", "th_TH"));
    }

    private void initAdapter() {
        LanguagesAdapter adapter = new LanguagesAdapter(getActivity(), languages, this);
        changeLanguageRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        changeLanguageRV.setAdapter(adapter);
    }

    @Override
    public void onBindItem(LanguagesAdapter.CurrentViewHolder holder, int position) {
        if (holders.valueAt(position) == null) {
            holders.put(position, holder);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String j = defaultLocale.getScript();
            Log.e("","");
        }
        String defaultLoc = "";
        if(holder.localeCode.contains("_"))
            defaultLoc = defaultLocale.getLanguage() + "_" + defaultLocale.getCountry();
        else
            defaultLoc = defaultLocale.getLanguage();

        if(holder.localeCode.equals(defaultLoc)){
            holder.changeLanguageSwitch.setChecked(true);
            lastEnabledSwitch = holder.changeLanguageSwitch;
            Toast.makeText(getActivity(), "Current language: " + holder.languageNameTV.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(LanguagesAdapter.CurrentViewHolder holder, int position) {
        if(holder.changeLanguageSwitch.isChecked())
            return;
        for (int i = 0; i < languages.size(); i++) {
            LanguagesAdapter.CurrentViewHolder h = holders.get(i);
            if (i != position)
                h.changeLanguageSwitch.setChecked(false);
            else {

                askToChangeLanguage(holder.languageNameTV.getText().toString(), holder.localeCode, h);
            }
        }
    }

    private void askToChangeLanguage(String languageName, String localeCode, LanguagesAdapter.CurrentViewHolder h){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Langauge Change");
        alert.setMessage("Change app lanugage to " + languageName + "?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> {
            dialogInterface.cancel();
            lastEnabledSwitch = h.changeLanguageSwitch;
            lastEnabledSwitch.setChecked(true);
            changeLanguage(languageName, localeCode);
        });
        alert.setNegativeButton("No", (dialogInterface, i) -> {
            lastEnabledSwitch.setChecked(true);
            dialogInterface.cancel();
        });
        alert.show();
    }

    private void changeLanguage(String languageName, String locale) {
        Locale newLocale;
        if(locale.contains("_")){
            String[] loc = locale.split("_");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                newLocale = new Locale.Builder().setLanguage(loc[0]).setRegion(loc[1]).build();
            }
            else{
                newLocale = new Locale(loc[0], loc[1]);
            }
        }
        else
            newLocale = new Locale(locale);
        Locale.setDefault(newLocale);

        Resources res = getActivity().getBaseContext().getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(newLocale);
        config.locale = newLocale;
        getActivity().getBaseContext().createConfigurationContext(config);
        getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

        SharedPref sharedPref = SharedPref.getInstance();
        sharedPref.PREF_LANGUAGE = locale;
        sharedPref.updateSetting();

        dashboard.reloadActivityOnLocaleChange(languageName);
    }

    public class ChangeLanguageModel {

        private String languageName;
        private String localeCode;

        public ChangeLanguageModel(String languageName, String localeCode) {
            this.languageName = languageName;
            this.localeCode = localeCode;
        }

        public String getLanguageName() {
            return languageName;
        }

        public void setLanguageName(String languageName) {
            this.languageName = languageName;
        }

        public String getLocaleCode() {
            return localeCode;
        }

        public void setLocaleCode(String localeCode) {
            this.localeCode = localeCode;
        }
    }

}
