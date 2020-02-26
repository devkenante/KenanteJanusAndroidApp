package vcims.com.vrapid.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import vcims.com.vrapid.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BriefFragment extends Fragment {

    private final String TAG = BriefFragment.class.getSimpleName();
    private WebView briefWebView;
    private ImageButton briefBackButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_brief, container, false);

        initialize(v);
        clickListener();
        loadWebView();

        return v;
    }

    private void initialize(View v){
        briefWebView = v.findViewById(R.id.briefWebView);
        briefBackButton = v.findViewById(R.id.briefBackButton);
    }

    private void clickListener(){

        briefBackButton.setOnClickListener(view -> {
            if(briefWebView.canGoBack())
                briefWebView.goBack();
            else
                getFragmentManager().popBackStackImmediate();
        });

    }

    private void loadWebView(){
        WebSettings webSettings = briefWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        briefWebView.setWebViewClient(new WebViewClient());
        briefWebView.loadUrl("https://www.kenante.com/khome/clogin");
    }

}
