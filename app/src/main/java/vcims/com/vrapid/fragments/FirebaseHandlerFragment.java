package vcims.com.vrapid.fragments;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.interfaces.FragmentCallbacks;
import vcims.com.vrapid.models.FirebaseVideoModel;
import vcims.com.vrapid.models.PPTModel;
import vcims.com.vrapid.util.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirebaseHandlerFragment extends Fragment {

    private FragmentCallbacks fragmentCallbacks;
    private String roomName;
    private FirebaseDatabase database;
    private DatabaseReference imageRef, videoRef, pptRef;
    private ChildEventListener imageChild, videoChild, pptChild;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentCallbacks = (FragmentCallbacks) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = new View(getActivity());
        view.setVisibility(View.GONE);

        parseBundleExtras();
        initFirebase();
        getFirbaseVideoForOthers();
        fragmentCallbacks.onFragmentStarted(5, true);

        return view;
    }

    private void parseBundleExtras(){
        CallData callData = CallData.getInstance(getActivity().getApplicationContext());
        roomName = callData.getRoomName();
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        imageRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(roomName).child(Constants.IMAGE_LIST);
        videoRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(roomName).child(Constants.VIDEO_LIST);
        pptRef = database.getReference(Constants.GROUP_LIST).child(Constants.GROUP_IDS).child(roomName).child(Constants.PPT_LIST);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (imageChild != null)
            imageRef.addChildEventListener(imageChild);
        if (videoChild != null)
            videoRef.addChildEventListener(videoChild);
        if(pptChild != null)
            pptRef.addChildEventListener(pptChild);
    }

    public void removeListeners() {
        if (imageChild != null)
            imageRef.removeEventListener(imageChild);
        if (videoChild != null)
            videoRef.removeEventListener(videoChild);
        if(pptChild != null)
            pptRef.removeEventListener(pptChild);
    }

    private void getFirbaseVideoForOthers() {

        imageChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseVideoModel model = dataSnapshot.getValue(FirebaseVideoModel.class);
                if(model!=null) {
                    model.type = 1;
                    if (model.status.equals("show")) {
                        fragmentCallbacks.startFirebaseShowFragment(model, false);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        videoChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseVideoModel model = dataSnapshot.getValue(FirebaseVideoModel.class);
                if(model!=null) {
                    model.type = 2;
                    if (model.status.equals("play")) {
                        fragmentCallbacks.startFirebaseShowFragment(model, false);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        pptChild = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                FirebaseVideoModel model = dataSnapshot.getValue(FirebaseVideoModel.class);
                if (model != null) {
                    model.type = 3;
                    model.name = dataSnapshot.getKey();
                    if (model.key != null && !model.key.equals("0")) {
                        ArrayList<PPTModel> pptList = new ArrayList<>();
                        for(DataSnapshot child : dataSnapshot.getChildren()){
                            try {
                                PPTModel pptModel = child.getValue(PPTModel.class);
                                pptList.add(pptModel);
                            }
                            catch (DatabaseException e){
                                e.printStackTrace();
                            }
                        }
                        model.ppt = pptList;
                        fragmentCallbacks.startFirebaseShowFragment(model, false);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onDetach() {
        super.onDetach();
        removeListeners();
        imageChild = null;
        videoChild = null;
        pptChild = null;
    }
}
