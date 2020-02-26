package vcims.com.vrapid.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.varenia.kenante_chat.core.KenanteChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import vcims.com.vrapid.R;
import vcims.com.vrapid.adapter.BottomChatUsersAdapter;
import vcims.com.vrapid.adapter.NewChatAdapter;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.interfaces.ActionOnFragment;
import vcims.com.vrapid.interfaces.FragmentCallbacks;
import vcims.com.vrapid.models.ChatModel;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.OnSwipeTouchListener;
import vcims.com.vrapid.util.SharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class BottomChatFragment extends Fragment implements BottomChatUsersAdapter.OnAdapterEvent {

    private final int FULL_CHAT_FRAGMENT = 3;
    private ActionOnFragment actionOnFragment;
    private SharedPref sharedPref;
    private DBHelper db;
    private RecyclerView bottomChatRV, bottomChatTopUsersRV;
    private List<KenanteChatMessage> messages;
    private ArrayList<ChatModel> users;
    private NewChatAdapter adapter;
    private BottomChatUsersAdapter usersAdapter;
    private FragmentCallbacks fragmentCallbacks;
    private int currentOpponent;
    private ImageButton enlargeChatIB;
    private FrameLayout bottomChatFL;
    private HashMap<Integer, String> longNames;
    private SparseArray<BottomChatUsersAdapter.ViewHolder> usersHolders;
    private Set<Integer> currentChatParticipants = new CopyOnWriteArraySet<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionOnFragment = (ActionOnFragment) context;
        fragmentCallbacks = (FragmentCallbacks) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bottom_chat, container, false);

        initialize(v);
        clickListener();
        bindUsersAdapter();
        bindChatAdapter();

        return v;
    }

    @SuppressLint("UseSparseArrays")
    private void initialize(View v) {
        db = DBHelper.getInstance(getActivity());
        sharedPref = SharedPref.getInstance();
        bottomChatTopUsersRV = v.findViewById(R.id.bottomChatTopUsersRV);
        bottomChatRV = v.findViewById(R.id.bottomChatRV);
        users = new ArrayList<>();
        messages = new ArrayList<>();
        enlargeChatIB = v.findViewById(R.id.enlargeChatIB);
        bottomChatFL = v.findViewById(R.id.bottomChatFL);
        CallData callData = CallData.getInstance(getActivity().getApplicationContext());
        longNames = callData.getLongNames();
        usersHolders = new SparseArray<>();
    }

    @SuppressWarnings("ClickableViewAccessibility")
    private void clickListener() {
        enlargeChatIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersAdapter.removeAll();
                clearAllHoldersMsgIndicator();
                bottomChatFL.setVisibility(View.GONE);
                Bundle extra = new Bundle();
                extra.putInt(Constants.EXTRA_OPPONENT_ID, currentOpponent);
                actionOnFragment.startFragment(FULL_CHAT_FRAGMENT, extra);
            }
        });
        bottomChatFL.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                removeThisUserAndContinue();
                clearAllHoldersMsgIndicator();
                usersAdapter.removeAll();
            }
        });
        bottomChatRV.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeRight() {
                hideThisFragment();
            }
        });
    }

    private void bindUsersAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bottomChatTopUsersRV.setLayoutManager(layoutManager);
        usersAdapter = new BottomChatUsersAdapter(getActivity(), users);
        usersAdapter.setAdapterListener(this);
        bottomChatTopUsersRV.setAdapter(usersAdapter);
    }

    private void addNewUser(int userid) {
        if (usersAdapter.doesUserExist(userid))
            return;
        ChatModel model = new ChatModel();
        model.setUserId(userid);
        String name = longNames.get(userid);
        model.setName(name);
        usersAdapter.add(model);
    }

    private void bindChatAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        bottomChatRV.setLayoutManager(layoutManager);
        adapter = new NewChatAdapter(getActivity(), messages, CallData.getInstance(getActivity().getApplicationContext()).getCurrentUserId());
        bottomChatRV.setAdapter(adapter);
    }

    public void processMessage(KenanteChatMessage qbChatMessage, Integer integer) {
        if (bottomChatFL.getVisibility() == View.GONE) {
            bottomChatFL.setVisibility(View.VISIBLE);
            fragmentCallbacks.onStartChat(true);
        }
        addNewUser(integer);
        if (currentChatParticipants.size() == 0) {
            //Initialize chat in activity
            currentOpponent = integer;
            fragmentCallbacks.onChangeChatOpponent(integer);
            currentChatParticipants.add(integer);
        } else {
            if (!currentChatParticipants.contains(integer)) {
                //Notify activity that more chats exist
                currentChatParticipants.add(integer);
            }
        }
        //Showing message
        if (integer == currentOpponent || integer == CallData.getInstance(getActivity().getApplicationContext()).getCurrentUserId()) {
            showMessage(qbChatMessage);
        }
        if (integer != currentOpponent)
            handleNewMessageIndicator(integer, true);
    }

    public void showMessage(KenanteChatMessage message) {
        if (adapter != null) {
            adapter.addItem(message);
            scrollMessageListDown();
        }
    }

    private void scrollMessageListDown() {
        bottomChatRV.scrollToPosition(messages.size() - 1);
    }

    private void removeThisUserAndContinue() {
        resetAdapter();
        bottomChatFL.setVisibility(View.GONE);
        fragmentCallbacks.onStartChat(false);
    }

    private void resetAdapter() {
        adapter.removeAll();
    }

    public void userDisconnected(int userId) {
        if (usersHolders.get(userId) == null)
            return;
        setUserViewFocused(0);
        handleNewMessageIndicator(userId, false);
        usersAdapter.remove(userId);
        usersHolders.remove(userId);
        if (usersAdapter.getItemCount() == 0) {
            currentChatParticipants.clear();
            removeThisUserAndContinue();
            usersHolders.clear();
        } else {
            //Setting this user as current chatting user
            if (usersAdapter.getItemCount() > 0) {
                ChatModel userToChat = users.get(0);
                int userIdToChat = userToChat.getUserId();
                currentOpponent = userIdToChat;
                fragmentCallbacks.onChangeChatOpponent(userIdToChat);
                handleNewMessageIndicator(userIdToChat, false);
                resetAdapter();
                //Todo: Uncomment this code
                //messages = db.getLastTwoChatMessages(userIdToChat);
                Collections.reverse(messages);
                adapter.addAll(messages);
            }
        }
    }

    @Override
    public void onBindLastViewHolder(int position, BottomChatUsersAdapter.ViewHolder holder) {
        int userId = usersAdapter.getUserId(position);
        usersHolders.put(userId, holder);
        if (userId != currentOpponent)
            handleNewMessageIndicator(userId, true);
        else {
            setUserViewFocused(0);
            setUserViewFocused(userId);
        }
    }

    @Override
    public void onItemClick(int position) {
        ChatModel model = users.get(position);
        int userId = model.getUserId();
        if (currentOpponent == userId)
            return;
        resetAdapter();
        currentOpponent = userId;
        fragmentCallbacks.onChangeChatOpponent(userId);
        //Todo: Uncomment this code
        //messages = db.getLastTwoChatMessages(userId);
        Collections.reverse(messages);
        adapter.addAll(messages);
        handleNewMessageIndicator(userId, false);
        setUserViewFocused(0);
        setUserViewFocused(userId);
    }

    private void handleNewMessageIndicator(int userId, Boolean toShow) {
        BottomChatUsersAdapter.ViewHolder holder = usersHolders.get(userId);
        if (holder != null) {
            if (toShow) {
                int newMessages = Integer.valueOf(holder.getNewMessageNotiTV().getText().toString());
                newMessages++;
                holder.getNewMessageNotiTV().setText(String.valueOf(newMessages));
                holder.getNewMessageNotiTV().setVisibility(View.VISIBLE);
            } else {
                holder.getNewMessageNotiTV().setText("0");
                holder.getNewMessageNotiTV().setVisibility(View.GONE);
            }
        }
    }

    private void clearAllHoldersMsgIndicator() {
        if (usersHolders != null) {
            if (usersHolders.size() > 0)
                for (int i = 0; i < usersHolders.size(); i++) {
                    BottomChatUsersAdapter.ViewHolder holder = usersHolders.get(usersHolders.keyAt(i));
                    if (holder != null) {
                        holder.getNewMessageNotiTV().setText("0");
                        holder.getNewMessageNotiTV().setVisibility(View.GONE);
                        holder.getBottomChatUserTV().setTextColor(Color.parseColor("#bcbcbc"));
                        holder.getBottomChatUserLL().setBackground(getActivity().getResources().getDrawable(R.drawable.bottom_chat_user_deactive));
                    }
                }
            usersHolders.clear();
        }
    }

    private void setUserViewFocused(int userId) {
        if (usersHolders != null)
            if (usersHolders.size() > 0)
                for (int i = 0; i < usersHolders.size(); i++) {
                    int key = usersHolders.keyAt(i);
                    BottomChatUsersAdapter.ViewHolder holder = usersHolders.get(key);
                    if (holder != null)
                        if (userId != 0) {
                            if (userId == key) {
                                holder.getBottomChatUserLL().setBackground(getActivity().getResources().getDrawable(R.drawable.bottom_chat_user_active));
                                holder.getBottomChatUserTV().setTextColor(Color.parseColor("#ffffff"));
                            }
                        } else {
                            holder.getBottomChatUserLL().setBackground(getActivity().getResources().getDrawable(R.drawable.bottom_chat_user_deactive));
                            holder.getBottomChatUserTV().setTextColor(Color.parseColor("#bcbcbc"));
                        }
                }
    }

    public boolean isBottomChatVisible() {
        return bottomChatFL.getVisibility() == View.VISIBLE;
    }

    public void hideThisFragment() {
        removeThisUserAndContinue();
        clearAllHoldersMsgIndicator();
        usersAdapter.removeAll();
    }

}
