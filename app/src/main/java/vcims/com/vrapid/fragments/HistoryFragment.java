package vcims.com.vrapid.fragments;


import android.app.Fragment;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import vcims.com.vrapid.R;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.models.HistoryModel;
import vcims.com.vrapid.adapter.HistoryAdapter;
import vcims.com.vrapid.util.StaticMethods;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    private RecyclerView historyRV;
    private LinearLayout historyGroupStartType;
    private TextView historyStartTypeTV;
    private String type;
    private ArrayList<HistoryModel> listToShow, allList, attendedList, expiredList;
    private DBHelper db;
    private HistoryAdapter adapter;
    private ImageButton historyBackButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        initialize(v);
        clickListener();
        getAllAttendedList();
        getAllExpiredList();
        getAllList();
        initAdapter();

        return v;
    }

    private void initialize(View v) {
        historyRV = v.findViewById(R.id.historyRV);
        historyGroupStartType = v.findViewById(R.id.historyGroupStartType);
        historyStartTypeTV = v.findViewById(R.id.historyStartTypeTV);
        attendedList = new ArrayList<>();
        allList = new ArrayList<>();
        expiredList = new ArrayList<>();
        listToShow = new ArrayList<>();
        db = DBHelper.getInstance(getActivity());
        historyBackButton = v.findViewById(R.id.historyBackButton);
    }

    private void clickListener() {
        historyGroupStartType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(getActivity(), historyGroupStartType);
                menu.getMenuInflater().inflate(R.menu.history_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        type = item.getTitle().toString();
                        updateUI("type");
                        showHistoryBasedOnType(item.getItemId());
                        return true;
                    }
                });
                menu.show();
            }
        });

        historyBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void updateUI(String type) {
        switch (type) {
            case "type":
                historyStartTypeTV.setText(this.type);
                break;
        }
    }

    private void showHistoryBasedOnType(int id){
        switch (id){
            case R.id.attended:

                adapter.removeAll();
                adapter.addAll(attendedList);

                break;

            case R.id.expired:

                adapter.removeAll();
                adapter.addAll(expiredList);

                break;

            case R.id.all:

                adapter.removeAll();
                adapter.addAll(allList);

                break;
        }
    }

    private void getAllAttendedList(){
        //All attended
        ArrayList<String> attendedRooms = db.getAttendedHistory();
        for(String roomName : attendedRooms){
            HistoryModel historyModel = new HistoryModel();
            historyModel.setConfName(roomName);
            try {
                HashMap<String, String> schedule = StaticMethods.getHistoryFormattedTime(db, roomName);
                historyModel.setDate(schedule.get("day") + " " + schedule.get("month") + " " + schedule.get("year"));
                historyModel.setDuration(schedule.get("duration"));
                historyModel.setStatus("1");
                attendedList.add(historyModel);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllExpiredList(){
        ArrayList<String> alLRooms = db.getAllTags();
        for(int i = 0; i<alLRooms.size(); i++) {
            for (HistoryModel model : attendedList)
                if (model.getConfName().equals(alLRooms.get(i)))
                    alLRooms.remove(i);
        }
        //All attended rooms now removed
        for(String roomName : alLRooms){
            try {
                Date endDate = StaticMethods.getEndDate(db, roomName);
                DateFormat inputTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                inputTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String currentD = inputTimeFormat.format(Calendar.getInstance().getTime());
                Date currentDate = inputTimeFormat.parse(currentD);
                if(currentDate.after(endDate)){
                    //This group has expired
                    HashMap<String, String> schedule = StaticMethods.getHistoryFormattedTime(db, roomName);
                    HistoryModel historyModel = new HistoryModel();
                    historyModel.setConfName(roomName);
                    historyModel.setDate(schedule.get("day") + " " + schedule.get("month") + " " + schedule.get("year"));
                    historyModel.setDuration(schedule.get("duration"));
                    historyModel.setStatus("2");
                    expiredList.add(historyModel);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void getAllList(){
        allList.addAll(attendedList);
        allList.addAll(expiredList);
    }

    private void initAdapter(){
        listToShow.addAll(allList);
        adapter = new HistoryAdapter(getActivity(), listToShow);
        historyRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        historyRV.setAdapter(adapter);
    }

}
