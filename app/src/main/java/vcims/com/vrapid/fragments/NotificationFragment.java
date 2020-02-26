package vcims.com.vrapid.fragments;


import android.os.Bundle;
import android.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.models.NotificationModel;
import vcims.com.vrapid.adapter.NotificationAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }

    private RecyclerView notificationRV;
    private ArrayList<NotificationModel> notifications;
    private DBHelper db;
    private ImageButton notificationBackButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        initialize(view);
        clickListner();
        getAllNotifications();
        bindAdapter();
        long s = System.currentTimeMillis();

        return view;
    }

    private void initialize(View v){
        notifications = new ArrayList<>();
        notificationRV = v.findViewById(R.id.notificationRV);
        db = DBHelper.getInstance(getActivity());
        notificationBackButton = v.findViewById(R.id.notificationBackButton);
    }

    private void clickListner(){
        notificationBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void getAllNotifications(){
        ArrayList<NotificationModel> array = db.getNotifications();
        for(NotificationModel model : array){
            String time = model.getTime();
            //Get the difference from when the notification arrived and the current system time.
            //Store the difference in model--> time variable.
        }
    }

    private void bindAdapter(){
        NotificationAdapter adapter = new NotificationAdapter(getActivity(), notifications);
        notificationRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        notificationRV.setAdapter(adapter);
    }

}
