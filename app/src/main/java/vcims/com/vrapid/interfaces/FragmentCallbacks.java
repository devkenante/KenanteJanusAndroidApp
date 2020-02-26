package vcims.com.vrapid.interfaces;

import vcims.com.vrapid.models.FirebaseVideoModel;

public interface FragmentCallbacks {

    //Full Screen Chat
    void onChangeChatOpponent(int opponentId);

    void onStartChat(boolean start);

    void localVideoExists();

    void startFirebaseShowFragment(FirebaseVideoModel model, Boolean isMod);

    void onFragmentStarted(int type, Boolean started);

  /*  void onTranslatorPresent(Boolean present);

    void onRemoveFragmentFromStack();

    void onCameraSwitched(Boolean frontCamera);*/

}
