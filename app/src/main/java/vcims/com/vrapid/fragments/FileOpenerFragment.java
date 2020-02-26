package vcims.com.vrapid.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import vcims.com.vrapid.R;
import vcims.com.vrapid.interfaces.ActionOnFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FileOpenerFragment extends Fragment {

    private final int FILE_OPENER_FRAGMENT = 10;
    private FrameLayout fileOpenerFL;
    private ImageView enlargedPictureIV, closeFileOpener;
    private WebView documentWV;
    private ProgressBar fileOpenerPB;
    private ActionOnFragment actionOnFragment;
    private TextView fileNameTV;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionOnFragment = (ActionOnFragment) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_file_opener, container, false);

        initialize(view);
        clickListener();

        return view;
    }

    private void initialize(View view){
        fileOpenerFL = view.findViewById(R.id.fileOpenerFL);
        enlargedPictureIV = view.findViewById(R.id.enlargedPictureIV);
        closeFileOpener = view.findViewById(R.id.closeFileOpener);
        documentWV = view.findViewById(R.id.documentWV);
        documentWV.getSettings().setJavaScriptEnabled(true);
        fileOpenerPB = view.findViewById(R.id.fileOpenerPB);
        fileNameTV = view.findViewById(R.id.fileNameTV);
    }

    private void clickListener(){
        closeFileOpener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionOnFragment.hideFragment(FILE_OPENER_FRAGMENT);
            }
        });
    }

    public void handleVisibility(Boolean show){
        if(show){
            fileOpenerFL.setVisibility(View.VISIBLE);
        }
        else{
            documentWV.setVisibility(View.GONE);
            fileOpenerFL.setVisibility(View.GONE);
        }
    }

    /*public void openFile(String type, QBChatMessage chatMessage, String fileURI, String fileName){
        if(type.equals("img")){
            fileOpenerPB.setVisibility(View.VISIBLE);
            documentWV.setVisibility(View.GONE);
            enlargedPictureIV.setVisibility(View.VISIBLE);
            if (chatMessage.getAttachments() != null)
                for (QBAttachment attachment : chatMessage.getAttachments()) {
                    String url = attachment.getUrl();
                    fileNameTV.setText(attachment.getName());
                    Picasso.with(getActivity()).load(url).into(enlargedPictureIV, new Callback() {
                        @Override
                        public void onSuccess() {
                            fileOpenerPB.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            actionOnFragment.hideFragment(FILE_OPENER_FRAGMENT);
                            fileOpenerPB.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), getString(R.string.failed_loading_image), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        }
        else if(type.equals("doc") || type.equals("docx") || type.equals("pdf") || type.equals("ppt")){
            enlargedPictureIV.setVisibility(View.GONE);
            fileOpenerPB.setVisibility(View.VISIBLE);
            fileNameTV.setText(fileName);
            documentWV.loadUrl("https://docs.google.com/viewer?embedded=true&url=" + fileURI);
            documentWV.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return false;
                }
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    fileOpenerPB.setVisibility(View.GONE);
                    documentWV.setVisibility(View.VISIBLE);
                }
            });
        }
    }*/

}
