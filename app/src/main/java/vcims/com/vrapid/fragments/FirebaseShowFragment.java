package vcims.com.vrapid.fragments;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import vcims.com.vrapid.R;
import vcims.com.vrapid.activities.CallActivity;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.interfaces.ActionOnFragment;
import vcims.com.vrapid.models.FirebaseVideoModel;
import vcims.com.vrapid.models.PPTModel;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.NewSharedPref;
import vcims.com.vrapid.util.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirebaseShowFragment extends Fragment implements View.OnTouchListener {

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    //For Zoom
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;
    private final String TAG = FirebaseShowFragment.class.getSimpleName();
    private final int FIREBASE_SHOW_FRAGMENT = 6;
    private final String VIDEO_PLAY = "play", VIDEO_PAUSE = "pause", VIDEO_STOP = "stop", IMAGE_SHOW = "show", IMAGE_HIDE = "hide", PPT_IMAGE_SHOW = "ppt_image_show", PPT_IMAGE_HIDE = "ppt_image_hide";
    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    int mode = NONE;
    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    private ActionOnFragment actionOnFragment;
    private FirebaseVideoModel videoModel;
    private VideoView videoView;
    private ImageView videoPlay, videoPause, firebaseIV, pptLeftIV, pptRightIV, closeFirebaseFragment;
    private ProgressBar videoLoadingPG;
    private LinearLayout pptImageChangeLL;
    private int currentUserType = -1, playVideoAt;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private View.OnClickListener onClickListener;
    private Boolean first, isPptFirst;
    private ValueEventListener valueEventListener;
    private DisplayMetrics metrics;
    private CallActivity callActivity;
    private ImageView rotateFirebaseMediaIV;
    private Boolean isPortrait = false;
    private ArrayList<PPTModel> pptList = new ArrayList<>();
    private Integer currentPptImage = null;
    private CallData callData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionOnFragment = (ActionOnFragment) context;
        callActivity = (CallActivity) context;
        callActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firebase_show, container, false);

        parseBundleExtras();
        initViewsAndComponents(view);
        clickListener();
        initDatabase();
        updateUI();
        getChangeInDatabase();

        return view;
    }

    private void parseBundleExtras() {
        Bundle extras = getArguments();
        if (extras != null) {
            videoModel = (FirebaseVideoModel) extras.getSerializable(Constants.EXTRA_FVIDEO_MODEL);
            first = extras.getBoolean(Constants.EXTRA_IS_MOD, false);
            isPptFirst = extras.getBoolean(Constants.EXTRA_IS_MOD, false);
            if (videoModel.ppt != null && videoModel.ppt.size() > 0)
                pptList = videoModel.ppt;
        }
    }

    private void initViewsAndComponents(View view) {
        videoView = view.findViewById(R.id.videoView);
        firebaseIV = view.findViewById(R.id.firebaseIV);
        pptLeftIV = view.findViewById(R.id.pptLeftIV);
        pptRightIV = view.findViewById(R.id.pptRightIV);
        videoPlay = view.findViewById(R.id.videoPlay);
        videoPause = view.findViewById(R.id.videoPause);
        closeFirebaseFragment = view.findViewById(R.id.closeFirebaseFragment);
        videoLoadingPG = view.findViewById(R.id.videoLoadingPG);
        pptImageChangeLL = view.findViewById(R.id.pptImageChangeLL);
        rotateFirebaseMediaIV = view.findViewById(R.id.rotateFirebaseMediaIV);
        callData = CallData.getInstance(getActivity());
        currentUserType = callData.getCurrentUserType();
        metrics = getResources().getDisplayMetrics();
        database = FirebaseDatabase.getInstance();
    }

    private void initDatabase() {
        if (videoModel.type == 1)
            databaseRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(callData.getRoomName()).child(Constants.IMAGE_LIST).child(videoModel.name);
        else if (videoModel.type == 2)
            databaseRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(callData.getRoomName()).child(Constants.VIDEO_LIST).child(videoModel.name);
        else if (videoModel.type == 3)
            databaseRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(callData.getRoomName()).child(Constants.PPT_LIST).child(videoModel.name);
    }

    private void updateUI() {
        if (videoModel.type == 1) {
            videoLoadingPG.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            firebaseIV.setVisibility(View.VISIBLE);
            if (currentUserType == Constants.MODERATOR)
                closeFirebaseFragment.setVisibility(View.VISIBLE);
            else
                closeFirebaseFragment.setVisibility(View.GONE);
        } else if (videoModel.type == 2) {
            setVideoViewParamters();
            checkVideoPlayingStatus();
            videoView.setBackgroundColor(Color.BLACK);
            videoView.setZOrderMediaOverlay(true);
            videoView.setVisibility(View.VISIBLE);
            firebaseIV.setVisibility(View.GONE);
            if (currentUserType == Constants.MODERATOR) {
                videoPlay.setVisibility(View.GONE);
                videoPause.setVisibility(View.VISIBLE);
                closeFirebaseFragment.setVisibility(View.VISIBLE);
                videoPlay.setEnabled(false);
                videoPause.setEnabled(false);
            } else {
                videoPlay.setVisibility(View.GONE);
                videoPause.setVisibility(View.GONE);
                closeFirebaseFragment.setVisibility(View.GONE);
            }
        } else if (videoModel.type == 3) {
            videoView.setVisibility(View.GONE);
            firebaseIV.setVisibility(View.VISIBLE);
            if (currentUserType == Constants.MODERATOR) {
                pptImageChangeLL.setVisibility(View.VISIBLE);
                closeFirebaseFragment.setVisibility(View.VISIBLE);
            } else
                closeFirebaseFragment.setVisibility(View.GONE);
        }
    }

    private void getChangeInDatabase() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (videoModel.type == 1) {
                    FirebaseVideoModel model = dataSnapshot.getValue(FirebaseVideoModel.class);
                    if (model != null)
                        switch (model.status) {
                            case IMAGE_SHOW:
                                if (first) {
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.STATUS, IMAGE_HIDE);
                                    databaseRef.updateChildren(map1);
                                } else
                                    setImageAction(IMAGE_SHOW, model);
                                break;

                            case IMAGE_HIDE:
                                if (first) {
                                    first = false;
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.STATUS, IMAGE_SHOW);
                                    databaseRef.updateChildren(map1);
                                } else
                                    setImageAction(IMAGE_HIDE, model);
                                break;
                        }
                } else if (videoModel.type == 2) {
                    FirebaseVideoModel model = dataSnapshot.getValue(FirebaseVideoModel.class);
                    if (model != null)
                        switch (model.status) {
                            case VIDEO_PLAY:
                                if (first) {
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.STATUS, VIDEO_STOP);
                                    databaseRef.updateChildren(map1);
                                } else
                                    setVideoAction(VIDEO_PLAY);
                                break;

                            case VIDEO_PAUSE:
                                setVideoAction(VIDEO_PAUSE);
                                break;

                            case VIDEO_STOP:
                                if (first) {
                                    first = false;
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.STATUS, VIDEO_PLAY);
                                    databaseRef.updateChildren(map1);
                                } else
                                    setVideoAction(VIDEO_STOP);
                                break;
                        }
                } else if (videoModel.type == 3) {
                    FirebaseVideoModel model = dataSnapshot.getValue(FirebaseVideoModel.class);
                    if (model != null) {
                        String urlToShow = model.key;
                        if (urlToShow != null && urlToShow.length() > 0) {
                            if (first) {
                                first = false;

                                if(!urlToShow.equals("0")) {
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.KEY, "0");
                                    map1.put(Constants.STATUS, IMAGE_SHOW);
                                    databaseRef.updateChildren(map1);
                                }
                                else{
                                    isPptFirst = false;
                                    pptLeftIV.setEnabled(false);
                                    currentPptImage = 0;
                                    String url = pptList.get(currentPptImage).url;
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.KEY, url);
                                    map1.put(Constants.STATUS, IMAGE_SHOW);
                                    databaseRef.updateChildren(map1);
                                }
                            } else {
                                if (isPptFirst) {
                                    isPptFirst = false;
                                    pptLeftIV.setEnabled(false);
                                    currentPptImage = 0;
                                    String url = pptList.get(currentPptImage).url;
                                    HashMap<String, Object> map1 = new HashMap<>();
                                    map1.put(Constants.KEY, url);
                                    map1.put(Constants.STATUS, IMAGE_SHOW);
                                    databaseRef.updateChildren(map1);
                                } else {
                                    if (urlToShow.equals("0") && model.status.equals(IMAGE_HIDE)) {
                                        setImageAction(PPT_IMAGE_HIDE, null);
                                    } else {
                                        FirebaseVideoModel fModel = new FirebaseVideoModel();
                                        fModel.url = urlToShow;
                                        setImageAction(PPT_IMAGE_SHOW, fModel);
                                        for(int i = 0; i < pptList.size(); i++){
                                            PPTModel ppt = pptList.get(i);
                                            if(ppt.url.equals(urlToShow)){
                                                currentPptImage = pptList.indexOf(ppt);
                                            }
                                        }
                                        if(currentPptImage == 0) {
                                            pptLeftIV.setEnabled(false);
                                            pptRightIV.setEnabled(true);
                                        }else if(currentPptImage == pptList.size() - 1) {
                                            pptRightIV.setEnabled(false);
                                            pptLeftIV.setEnabled(true);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void clickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.videoPlay:
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put(Constants.STATUS, VIDEO_PLAY);
                        databaseRef.updateChildren(map1);
                        break;

                    case R.id.videoPause:
                        HashMap<String, Object> map2 = new HashMap<>();
                        map2.put(Constants.STATUS, VIDEO_PAUSE);
                        databaseRef.updateChildren(map2);
                        break;

                    case R.id.closeFirebaseFragment:

                        if (videoModel.type == 1) {
                            //Image
                            first = false;
                            HashMap<String, Object> map4 = new HashMap<>();
                            map4.put(Constants.STATUS, IMAGE_HIDE);
                            databaseRef.updateChildren(map4);
                        } else if (videoModel.type == 2) {
                            //Video
                            first = false;
                            HashMap<String, Object> map3 = new HashMap<>();
                            map3.put(Constants.STATUS, VIDEO_STOP);
                            databaseRef.updateChildren(map3);
                        } else if (videoModel.type == 3) {
                            //PPT
                            first = false;
                            isPptFirst = false;
                            HashMap<String, Object> map3 = new HashMap<>();
                            map3.put(Constants.KEY, "0");
                            map3.put(Constants.STATUS, IMAGE_HIDE);
                            databaseRef.updateChildren(map3);
                        }

                        break;

                    case R.id.rotateFirebaseMediaIV:

                        if (!isPortrait) {
                            isPortrait = true;
                            callActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            isPortrait = false;
                            callActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }

                        break;

                    case R.id.pptLeftIV:
                        if (currentPptImage > 0) {
                            currentPptImage--;

                            String urlToShow = pptList.get(currentPptImage).url;
                            HashMap<String, Object> map3 = new HashMap<>();
                            map3.put(Constants.KEY, urlToShow);
                            databaseRef.updateChildren(map3);

                            if (currentPptImage == 0) {
                                pptLeftIV.setEnabled(false);
                            }
                            if (currentPptImage < pptList.size() - 1)
                                pptRightIV.setEnabled(true);
                        }
                        break;

                    case R.id.pptRightIV:

                        if (currentPptImage < pptList.size() - 1) {
                            currentPptImage++;

                            String urlToShow = pptList.get(currentPptImage).url;
                            HashMap<String, Object> map3 = new HashMap<>();
                            map3.put(Constants.KEY, urlToShow);
                            databaseRef.updateChildren(map3);

                            if (currentPptImage > 0) {
                                pptLeftIV.setEnabled(true);
                            }
                            if (currentPptImage == pptList.size() - 1) {
                                pptRightIV.setEnabled(false);
                            }

                        }

                        break;

                }
            }
        };
        videoPlay.setOnClickListener(onClickListener);
        videoPause.setOnClickListener(onClickListener);
        closeFirebaseFragment.setOnClickListener(onClickListener);
        firebaseIV.setOnTouchListener(this);
        rotateFirebaseMediaIV.setOnClickListener(onClickListener);
        pptLeftIV.setOnClickListener(onClickListener);
        pptRightIV.setOnClickListener(onClickListener);
    }

    private void setVideoViewParamters() {
        if (videoModel.url == null)
            return;
        String url = videoModel.url;
        Uri uri = Uri.parse(url);
        videoView.requestFocus();
        videoView.setVideoURI(uri);
    }

    private void setVideoAction(String status) {
        switch (status) {
            case VIDEO_PLAY:
                if (currentUserType == Constants.MODERATOR) {
                    videoPause.setVisibility(View.VISIBLE);
                    videoPlay.setVisibility(View.GONE);

                }
                videoView.start();
                break;

            case VIDEO_PAUSE:
                if (currentUserType == Constants.MODERATOR) {
                    videoPause.setVisibility(View.GONE);
                    videoPlay.setVisibility(View.VISIBLE);

                }
                videoView.pause();
                break;

            case VIDEO_STOP:
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoView.stopPlayback();
                    }
                });
                actionOnFragment.hideFragment(FIREBASE_SHOW_FRAGMENT);
                break;
        }
    }

    private void setImageAction(String status, FirebaseVideoModel model) {
        switch (status) {
            case IMAGE_SHOW:
                String url = model.url;
                Picasso.with(getActivity()).load(url).into(firebaseIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        videoLoadingPG.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
                break;

            case IMAGE_HIDE:
                firebaseIV.setVisibility(View.GONE);
                actionOnFragment.hideFragment(FIREBASE_SHOW_FRAGMENT);
                break;

            case PPT_IMAGE_SHOW:
                String url1 = model.url;
                Picasso.with(getActivity()).load(url1).into(firebaseIV, new Callback() {
                    @Override
                    public void onSuccess() {
                        videoLoadingPG.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
                break;

            case PPT_IMAGE_HIDE:
                //firebaseIV.setVisibility(View.GONE);
                actionOnFragment.hideFragment(FIREBASE_SHOW_FRAGMENT);
                break;
        }
    }

    private void checkVideoPlayingStatus() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.setBackgroundColor(Color.TRANSPARENT);
                videoLoadingPG.setVisibility(View.GONE);
                videoPlay.setEnabled(true);
                videoPause.setEnabled(true);
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(getActivity(), getString(R.string.video_finished), Toast.LENGTH_SHORT).show();
                actionOnFragment.hideFragment(FIREBASE_SHOW_FRAGMENT);
            }
        });

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Toast.makeText(getActivity(), getString(R.string.error_while_playing_video), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (valueEventListener != null)
            databaseRef.addValueEventListener(valueEventListener);
        if (videoView != null) {
            videoView.seekTo(playVideoAt);
            videoView.start();
            videoLoadingPG.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (valueEventListener != null)
            databaseRef.removeEventListener(valueEventListener);
        if (videoView != null) {
            videoView.pause();
            playVideoAt = videoView.getCurrentPosition();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (valueEventListener != null)
            databaseRef.removeEventListener(valueEventListener);
        valueEventListener = null;
        videoView = null;
        callActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView imageView = (ImageView) v;
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                matrix.set(imageView.getImageMatrix());
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        imageView.setImageMatrix(matrix);

        return true;
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d(TAG, sb.toString());
    }

}
