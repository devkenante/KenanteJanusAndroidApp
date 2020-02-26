package vcims.com.vrapid.fragments;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vcims.com.vrapid.R;
import vcims.com.vrapid.CustomUiClasses.CustomExpandableListView;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.interfaces.SendDataBack;
import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.adapter.AddUsersAdaper;
import vcims.com.vrapid.adapter.AddUsersExpandableAdapter;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.StaticMethods;

/**
 * A simple {@link Fragment} subclass.
 */
public class  AddUsersFragment extends Fragment implements AddUsersExpandableAdapter.ExpandableItemSelector {

    private DBHelper db;
    private String confRoom;
    private Toolbar addUsersToolbar;
    private HashMap<String, ArrayList<KenanteUser>> allUsersHashmap;
    private ListView adminLV;
    private AddUsersAdaper adminAdapter;
    private AddUsersExpandableAdapter expandableAdapter;
    private CustomExpandableListView expandableLV;
    private List<String> titles;
    private HashMap<String, ArrayList<KenanteUser>> adapterElements;
    private ArrayList<KenanteUser> selectedUsers;
    private View.OnClickListener onClickListener;
    private Button addUsersCancelButton, addUsersSaveButton;
    private ProgressDialog dialog;
    private SendDataBack sendDataBack;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adminAdapter.removeAllElements();
        adminAdapter = null;
        selectedUsers.clear();
        selectedUsers = null;
        adapterElements.clear();
        adapterElements = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        sendDataBack = (SendDataBack)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_users, container, false);

        parseBundleExtras();
        initialize(v);
        clickListener();
        removeNonExistingUsers();
        bindAdapters();
        addingAdminsToSelectedUsers();
        expandLV();

        return v;
    }

    private void parseBundleExtras() {
        Bundle bundle = getArguments();
        confRoom = bundle.getString(Constants.TAGS);
    }

    private void initialize(View v) {
        db = DBHelper.getInstance(getActivity());
        addUsersToolbar = v.findViewById(R.id.addUsersToolbar);
        adminLV = v.findViewById(R.id.adminLV);
        expandableLV = v.findViewById(R.id.expandableLV);
        allUsersHashmap = StaticMethods.getDifferentUsersList(db, confRoom);
        titles = new ArrayList<>();
        adapterElements = new HashMap<>();
        selectedUsers = new ArrayList<>();
        addUsersCancelButton = v.findViewById(R.id.addUsersCancelButton);
        addUsersSaveButton = v.findViewById(R.id.addUsersSaveButton);
        initActionBar();
    }

    private void initActionBar(){
        addUsersToolbar.setTitle(confRoom);
    }

    private void clickListener(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.addUsersCancelButton:

                        closeThisFragment();

                        break;

                    case R.id.addUsersSaveButton:

                        dialog = new ProgressDialog(getActivity());
                        dialog.setCancelable(false);
                        dialog.setMessage("Creating dialogs");
                        dialog.show();

                        break;
                }
            }
        };
        addUsersCancelButton.setOnClickListener(onClickListener);
        addUsersSaveButton.setOnClickListener(onClickListener);

        addUsersToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void removeNonExistingUsers() {
        if (allUsersHashmap.get(Constants.RESPONDENT_TEXT).size() != 0) {
            titles.add(Constants.RESPONDENT_TEXT);
            adapterElements.put(Constants.RESPONDENT_TEXT, allUsersHashmap.get(Constants.RESPONDENT_TEXT));
        }
        if (allUsersHashmap.get(Constants.MODERATOR_TEXT).size() != 0) {
            titles.add(Constants.MODERATOR_TEXT);
            adapterElements.put(Constants.MODERATOR_TEXT, allUsersHashmap.get(Constants.MODERATOR_TEXT));
        }
        if (allUsersHashmap.get(Constants.TRANSLATOR_TEXT).size() != 0) {
            titles.add(Constants.TRANSLATOR_TEXT);
            adapterElements.put(Constants.TRANSLATOR_TEXT, allUsersHashmap.get(Constants.TRANSLATOR_TEXT));
        }
        if (allUsersHashmap.get(Constants.CLIENT_TEXT).size() != 0) {
            titles.add(Constants.CLIENT_TEXT);
            adapterElements.put(Constants.CLIENT_TEXT, allUsersHashmap.get(Constants.CLIENT_TEXT));
        }
        if (allUsersHashmap.get(Constants.RECORDER_TEXT).size() != 0) {
            titles.add(Constants.RECORDER_TEXT);
            adapterElements.put(Constants.RECORDER_TEXT, allUsersHashmap.get(Constants.RECORDER_TEXT));
        }
    }

    private void bindAdapters() {
        //Admin adapter
        adminAdapter = new AddUsersAdaper(getActivity(), allUsersHashmap.get(Constants.ADMIN_TEXT));
        adminLV.setAdapter(adminAdapter);
        //Rest all adapters
        expandableLV.setExpanded(true);
        expandableAdapter = new AddUsersExpandableAdapter(getActivity(), titles, adapterElements);
        expandableAdapter.registerItemSelector(this);
        expandableLV.setAdapter(expandableAdapter);
        setDynamicHeight();
    }

    private void addingAdminsToSelectedUsers() {
        selectedUsers.addAll(allUsersHashmap.get(Constants.ADMIN_TEXT));
    }

    private void setDynamicHeight() {
        StaticMethods.setListViewDynamicHeight(adminLV);
        for (int i = 0; i < expandableAdapter.getGroupCount(); i++)
            StaticMethods.setExpandableListViewDynamicHeight(expandableLV, i);
    }

    private void closeThisFragment() {
        getFragmentManager().popBackStackImmediate();
    }


    @Override
    public void onItemSelected(int groupPosition, int childPosition, boolean selection) {
        if (selection) {
            String groupItem = titles.get(groupPosition);
            ArrayList<KenanteUser> selectedGroupItems = adapterElements.get(groupItem);
            selectedUsers.add(selectedGroupItems.get(childPosition));
        }else{
            String groupItem = titles.get(groupPosition);
            ArrayList<KenanteUser> selectedGroupItems = adapterElements.get(groupItem);
            selectedUsers.remove(selectedGroupItems.get(childPosition));
        }
    }

    private void expandLV(){
        expandableLV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //expandableLV.expandGroup(groupPosition);
                return true;
            }
        });
    }

}
