package vcims.com.vrapid.handler_classes;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import vcims.com.vrapid.models.FirebaseVideoModel;

public class CallData {

    private static CallData callData;
    private Context context;
    private int currentUserType = -1;
    private int currentUserId = -1;
    private String currentUserName = "";
    private ArrayList<Integer> chatUsers = new ArrayList<>();
    private ArrayList<Integer> seeUsers = new ArrayList<>();
    private ArrayList<Integer> hearUsers = new ArrayList<>();
    private ArrayList<Integer> talkUsers = new ArrayList<>();
    private ArrayList<Integer> usersToSubsribe = new ArrayList<>();
    private HashMap<String, ArrayList<FirebaseVideoModel>> firebaseData = new HashMap<>();
    private HashMap<Integer, String> longNames = new HashMap<>();
    private HashMap<Integer, String> opponentsPrivateDialogs = new HashMap<>();
    private HashMap<Integer, Integer> allUsersType = new HashMap<>();
    private String roomName = "";
    private Boolean isTranslatorPresent = false;
    private HashMap<String, String> audioVideoStatus = new HashMap<>();
    private HashMap<String, Boolean> chatDialogIdsHistoryStatus = new HashMap<>();

    public CallData(Context context) {
        this.context = context;
    }

    public static CallData getInstance(Context context) {
        if (callData == null) {
            callData = new CallData(context);
        }
        return callData;
    }

    public void removeAllElements(){
        callData.getUsersToSubsribe().clear();
        callData.getSeeUsers().clear();
        callData.getHearUsers().clear();
        callData.getChatUsers().clear();
        callData.getOpponentsPrivateDialogs().clear();
        callData.getLongNames().clear();
        callData.getSeeUsers().clear();
        callData.getFirebaseData().clear();
        callData.setTranslatorPresent(false);
        callData.getAllUsersType().clear();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getCurrentUserType() {
        return currentUserType;
    }

    public void setCurrentUserType(int currentUserType) {
        this.currentUserType = currentUserType;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public ArrayList<Integer> getChatUsers() {
        return chatUsers;
    }

    public void setChatUsers(ArrayList<Integer> chatUsers) {
        this.chatUsers.addAll(chatUsers);
    }

    public ArrayList<Integer> getSeeUsers() {
        return seeUsers;
    }

    public void setSeeUsers(ArrayList<Integer> seeUsers) {
        this.seeUsers.addAll(seeUsers);
    }

    public ArrayList<Integer> getHearUsers() {
        return hearUsers;
    }

    public void setHearUsers(ArrayList<Integer> hearUsers) {
        this.hearUsers.addAll(hearUsers);
    }

    public ArrayList<Integer> getTalkUsers() {
        return talkUsers;
    }

    public void setTalkUsers(ArrayList<Integer> talkUsers) {
        this.talkUsers = talkUsers;
    }

    public ArrayList<Integer> getUsersToSubsribe() {
        return usersToSubsribe;
    }

    public void setUsersToSubsribe(ArrayList<Integer> usersToSubsribe) {
        this.usersToSubsribe.addAll(usersToSubsribe);
    }

    public void setFirebaseData(String roomName, FirebaseVideoModel model) {
        if (firebaseData.containsKey(roomName)) {
            firebaseData.get(roomName).add(model);
        } else {
            ArrayList<FirebaseVideoModel> array = new ArrayList<>();
            array.add(model);
            firebaseData.put(roomName, array);
        }
    }

    public ArrayList<FirebaseVideoModel> getFirebaseData(String roomName) {
        if (firebaseData.containsKey(roomName))
            return firebaseData.get(roomName);
        else
            return null;
    }

    public HashMap<String, ArrayList<FirebaseVideoModel>> getFirebaseData() {
        return firebaseData;
    }

    public HashMap<Integer, String> getLongNames() {
        return longNames;
    }

    public void setLongNames(HashMap<Integer, String> longNames) {
        this.longNames.putAll(longNames);
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public HashMap<Integer, String> getOpponentsPrivateDialogs() {
        return opponentsPrivateDialogs;
    }

    public void setOpponentsPrivateDialogs(HashMap<Integer, String> opponentsPrivateDialogs) {
        this.opponentsPrivateDialogs.putAll(opponentsPrivateDialogs);
    }

    public HashMap<Integer, Integer> getAllUsersType() {
        return allUsersType;
    }

    public void setAllUsersType(HashMap<Integer, Integer> allUsersType) {
        this.allUsersType = allUsersType;
    }

    public Boolean getTranslatorPresent() {
        return isTranslatorPresent;
    }

    public void setTranslatorPresent(Boolean translatorPresent) {
        isTranslatorPresent = translatorPresent;
    }

    public HashMap<String, String> getAudioVideoStatus() {
        return audioVideoStatus;
    }

    public void setAudioVideoStatus(HashMap<String, String> audioVideoStatus) {
        this.audioVideoStatus = audioVideoStatus;
    }

    public HashMap<String, Boolean> getChatDialogIdsHistoryStatus() {
        return chatDialogIdsHistoryStatus;
    }

    public void setChatDialogIdsHistoryStatus(HashMap<String, Boolean> chatDialogIdsHistoryStatus) {
        this.chatDialogIdsHistoryStatus = chatDialogIdsHistoryStatus;
    }
}
