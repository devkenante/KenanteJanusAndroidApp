package vcims.com.vrapid.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.varenia.kenante_chat.core.KenanteChatMessage;
import com.varenia.kenante_chat.core.KenanteChatSession;
import com.varenia.kenante_chat.interfaces.KenanteChatHistoryEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vcims.com.vrapid.R;
import vcims.com.vrapid.adapter.FullChatAdapter;
import vcims.com.vrapid.adapter.FullChatTopUsersAdapter;
import vcims.com.vrapid.database.DBHelper;
import vcims.com.vrapid.handler_classes.CallData;
import vcims.com.vrapid.interfaces.ActionOnFragment;
import vcims.com.vrapid.interfaces.FragmentCallbacks;
import vcims.com.vrapid.models.ChatModel;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.NewSharedPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class FullScreenChatFragment extends Fragment implements FullChatTopUsersAdapter.OnAdapterEvents, FullChatAdapter.OnChatEvents {

    private final String TAG = FullScreenChatFragment.class.getSimpleName();
    private final int FILE_OPENER_FRAGMENT = 10, FULL_CHAT_FRAGMENT = 3;
    private ActionOnFragment actionOnFragment;
    private FragmentCallbacks fragmentCallbacks;
    private DBHelper db;
    private ArrayList<Integer> currentPresentUsers;
    private RecyclerView fullChatUsersRV, fullChatRV;
    private ArrayList<ChatModel> chatUsersArray;
    private ImageButton closeFullChatIB;
    private int currentUserId, currentOpponentId = 0, firstBindedUserId = 0;
    private String currentDialogId = "";
    private FullChatAdapter fullChatAdapter;
    private ArrayList<KenanteChatMessage> allMessages;
    private FullChatTopUsersAdapter userAdapter;
    private HashMap<Integer, String> names;
    private FrameLayout fullScreenChatFL;
    private SparseArray<FullChatTopUsersAdapter.ViewHolder> usersHolders;
    private ProgressBar chatLoadingPB;
    private Boolean gettingChatDialogIdAndHistoryDownloading = false, fragOpenedFirstTime = false;
    private int currentSelectedItemPosition = -1;
    private int roomId = -1;
    private SparseBooleanArray historyDownloaded = new SparseBooleanArray();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        actionOnFragment = (ActionOnFragment) context;
        fragmentCallbacks = (FragmentCallbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseBundleExtras();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_screen_chat, container, false);

        initialize(view);
        clickListener();
        bindChatArray();
        getUsersToBind();
        bindUsersList();

        return view;
    }

    private void parseBundleExtras() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            currentPresentUsers = bundle.getIntegerArrayList(Constants.CURRENT_PRESENT_USERS);
        }
        CallData callData = CallData.getInstance(getActivity());
        currentPresentUsers = callData.getChatUsers();
        currentUserId = callData.getCurrentUserId();
        names = callData.getLongNames();
    }


    private void initialize(View view) {
        db = DBHelper.getInstance(getActivity());
        fullChatUsersRV = view.findViewById(R.id.fullChatUsersRV);
        fullChatRV = view.findViewById(R.id.fullChatRV);
        chatLoadingPB = view.findViewById(R.id.chatLoadingPB);
        chatUsersArray = new ArrayList<>();
        closeFullChatIB = view.findViewById(R.id.closeFullChatIB);
        allMessages = new ArrayList<>();
        fullScreenChatFL = view.findViewById(R.id.fullScreenChatFL);
        usersHolders = new SparseArray<>();
        roomId = NewSharedPref.INSTANCE.getIntValue(Constants.ROOM_ID);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clickListener() {
        closeFullChatIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Hide this fragment
                actionOnFragment.hideFragment(FULL_CHAT_FRAGMENT);
            }
        });
    }

    private void getUsersToBind() {
        for (Integer userId : currentPresentUsers) {
            ChatModel model = new ChatModel();
            model.setName(names.get(userId));
            model.setUserId(userId);
            chatUsersArray.add(model);
            historyDownloaded.put(userId, false);
        }
    }

    private void bindUsersList() {
        fullChatUsersRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        userAdapter = new FullChatTopUsersAdapter(getActivity(), chatUsersArray, true);
        userAdapter.setAdapterListener(this);
        fullChatUsersRV.setAdapter(userAdapter);
    }

    private void bindChatArray() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        fullChatRV.setLayoutManager(layoutManager);
        fullChatAdapter = new FullChatAdapter(getActivity(), allMessages, currentUserId, db);
        fullChatAdapter.setChatListener(this);
        fullChatRV.setAdapter(fullChatAdapter);
    }

    @Override
    public void onBindLastViewHolder(int position, FullChatTopUsersAdapter.ViewHolder holder) {
        ChatModel chatModel = chatUsersArray.get(position);
        usersHolders.put(chatModel.getUserId(), holder);
        if (position == 0 && fragOpenedFirstTime) {
            fragOpenedFirstTime = false;
            if (currentSelectedItemPosition == -1) {
                currentSelectedItemPosition = position;
                firstBindedUserId = chatModel.getUserId();
                onUserItemClick(position);
                setFocusedItem(chatModel.getUserId(), true);
            } else {
                onUserItemClick(currentSelectedItemPosition);
                setFocusedItem(currentSelectedItemPosition, true);
            }
        }
        if (position == currentSelectedItemPosition) {
            setFocusedItem(chatModel.getUserId(), true);
        }
    }

    @Override
    public void onUserItemClick(int position) {
        if (gettingChatDialogIdAndHistoryDownloading) {
            Toast.makeText(getActivity(), getString(R.string.loading), Toast.LENGTH_SHORT).show();
            return;
        }
        ChatModel model = chatUsersArray.get(position);
        if (currentOpponentId != 0 && currentOpponentId == model.getUserId() && !fragOpenedFirstTime)
            return;
        currentOpponentId = model.getUserId();

        fragOpenedFirstTime = false;
        //Check if history is downloaded
        if (!historyDownloaded.get(currentOpponentId)) {
            fragmentCallbacks.onStartChat(false);
            chatLoadingPB.setVisibility(View.VISIBLE);
            gettingChatDialogIdAndHistoryDownloading = true;
            getChatHistory(position);
        } else {
            currentSelectedItemPosition = position;
            restartAdapter();
            setFocusedItem(0, null);
            setFocusedItem(currentOpponentId, true);
            fragmentCallbacks.onStartChat(true);
            allMessages = db.getChatMessages(currentOpponentId, roomId);
            if (allMessages.size() == 0) {
                Toast.makeText(getActivity(), getString(R.string.no_chat_with_user), Toast.LENGTH_SHORT).show();
            } else {
                fullChatAdapter.addMessages(allMessages);
                scrollMessageListDown();
            }
            fragmentCallbacks.onChangeChatOpponent(currentOpponentId);
        }

    }

    public void processMessage(KenanteChatMessage kenanteMessage, Integer integer) {
        if (currentOpponentId == integer || currentUserId == integer)
            showMessage(kenanteMessage);
    }

    public void showMessage(KenanteChatMessage message) {
        if (fullChatAdapter != null) {
            fullChatAdapter.addItem(message);
            scrollMessageListDown();
        }
    }

    private void scrollMessageListDown() {
        fullChatRV.scrollToPosition(allMessages.size() - 1);
    }

    private void restartAdapter() {
        if (fullChatAdapter.getItemCount() > 0)
            fullChatAdapter.removeAllItems();
    }

    /*public void handleNewUser(int userId, Boolean add) {
        if (add) {
            //First check if this user already exists
            if (!checkIfUserExistsInAdapter(userId)) {
                ChatModel model = new ChatModel();
                model.setUserId(userId);
                model.setName(shortNames.get(userId));
                userAdapter.addUser(model);
            }
        } else {
            if (checkIfUserExistsInAdapter(userId)) {
                int indexToRemove = userAdapter.getIndexOfItem(userId);
                userAdapter.removeUser(indexToRemove);
                if (userId == currentOpponentId) {
                    restartAdapter();
                    currentOpponentId = 0;
                    setFocusedItem(currentOpponentId, false);
                    fragmentCallbacks.onStartChat(false);
                }
            }
            if (currentPresentUsers.size() == 0) {
                firstBindedUserId = 0;
                fragmentCallbacks.onStartChat(false);
                fragmentCallbacks.onRemoveFragmentFromStack();
                handleFullChatVisibility(2, 0);
            }
        }
    }*/

    private Boolean checkIfUserExistsInAdapter(int userId) {
        if (userAdapter.doesUserExist(userId))
            return true;
        else
            return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fullChatAdapter = null;
        userAdapter = null;
    }

    public void handleFullChatVisibility(int code, int opponentId) {
        if (code == 1) {
            fragOpenedFirstTime = true;
            fullScreenChatFL.setVisibility(View.VISIBLE);
            if (opponentId != 0) {
                int position = -1;
                for (int i = 0; i < chatUsersArray.size(); i++) {
                    ChatModel model = chatUsersArray.get(i);
                    if (model.getUserId() == opponentId)
                        position = i;
                }
                if (position != -1) {
                    onUserItemClick(position);
                    setFocusedItem(opponentId, true);
                }
            } else if (firstBindedUserId != 0) {
                onUserItemClick(currentSelectedItemPosition);
                setFocusedItem(currentOpponentId, true);
                fullChatUsersRV.scrollToPosition(currentSelectedItemPosition);
            }
        } else {
            restartAdapter();
            currentOpponentId = 0;
            setFocusedItem(0, null);
            fullScreenChatFL.setVisibility(View.GONE);
        }
    }

    private void setFocusedItem(int userId, Boolean setFocused) {
        //if userId = 0 then set all items to not focused
        //if userId != 0 then set only that item to focused
        if (usersHolders != null)
            if (usersHolders.size() > 0) {
                if (userId != 0) {
                    FullChatTopUsersAdapter.ViewHolder holder = usersHolders.get(userId);
                    if (holder != null) {
                        //Set focused true
                        if (setFocused)
                            holder.getTriangleIV().setVisibility(View.VISIBLE);
                        else
                            holder.getTriangleIV().setVisibility(View.INVISIBLE);
                    }
                } else
                    for (int i = 0; i < usersHolders.size(); i++) {
                        FullChatTopUsersAdapter.ViewHolder holder = usersHolders.get(usersHolders.keyAt(i));
                        if (holder != null)
                            //Remove focus from all items
                            holder.getTriangleIV().setVisibility(View.INVISIBLE);
                    }
            }
    }

    @Override
    public void onChatItemClick(int type, int position, FullChatAdapter.ViewHolder holder) {
        //type = 1 for image click
        //type = 2 for document click

    }

    private void getChatHistory(int position) {
        KenanteChatSession chatSession = KenanteChatSession.Companion.getInstance();
        chatSession.getChatHistory(new KenanteChatHistoryEventListener() {
            @Override
            public void onSuccess(@NotNull ArrayList<KenanteChatMessage> messages, int userId) {
                Log.e(TAG, "User id: " + userId + " and messages: " + messages.toString());
                for(KenanteChatMessage chatMessage : messages){
                    insertIntoDatabase(chatMessage);
                }
                currentSelectedItemPosition = position;
                restartAdapter();
                setFocusedItem(0, null);
                setFocusedItem(currentOpponentId, true);
                fragmentCallbacks.onStartChat(true);
                allMessages = messages;
                if (allMessages.size() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.no_chat_with_user), Toast.LENGTH_SHORT).show();
                } else {
                    fullChatAdapter.addMessages(allMessages);
                    scrollMessageListDown();
                }
                historyDownloaded.put(userId, true);
                chatLoadingPB.setVisibility(View.GONE);
                gettingChatDialogIdAndHistoryDownloading = false;
                fragmentCallbacks.onChangeChatOpponent(currentOpponentId);
            }

            @Override
            public void onError(@NotNull String reason) {

            }
        }, roomId, currentOpponentId);
    }

    /*private void openFile(String type, QBChatMessage chatMessage, String fileLocation, String fileName) {
        actionOnFragment.startFragment(FILE_OPENER_FRAGMENT, null);
        FileOpenerFragment fileOpenerFragment = (FileOpenerFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Constants.FILE_OPENER_FRAG);
        if (fileOpenerFragment != null)
            if (fileOpenerFragment.isAdded()) {
                fileOpenerFragment.openFile(type, chatMessage, fileLocation, fileName);
            }
    }*/

    private void insertIntoDatabase(KenanteChatMessage chatMessage){
        db.insertChatMessage(chatMessage);
    }

}
