package vcims.com.vrapid.fcm;

import android.content.Intent;
import android.os.PowerManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import vcims.com.vrapid.activities.IncomingCallActivity;
import vcims.com.vrapid.util.Constants;
import vcims.com.vrapid.util.SharedPref;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    SharedPref sharedPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        initComponents();
        if(sharedPref.USER_TYPE != Constants.ADMIN) {
            wakeUpScreen();
            if (!callStarted()) {
                Intent i = new Intent(this, IncomingCallActivity.class);
                Map<String, String> pushData = remoteMessage.getData();
                String dialogId = pushData.get("dialog_id");
                String occupants = pushData.get("occupants");
                String roomName = pushData.get("room_name");
                i.putExtra(Constants.DIALOG_ID, dialogId);
                i.putExtra(Constants.OCCUPANTS, occupants);
                i.putExtra(Constants.ROOM, roomName);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Call Has Started
                startActivity(i);
            }
            /*else{
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Incoming call", Toast.LENGTH_SHORT).show();
                    }
                });

            }*/
        }
    }

    private void initComponents(){
        sharedPref = SharedPref.getInstance();
    }

    private Boolean callStarted(){
        Boolean callStarted = false;
        SharedPref sharedPref = SharedPref.getInstance();
        callStarted = sharedPref.CALL_STARTED;
        //TODO
        return callStarted;
    }

    private void wakeUpScreen(){
        PowerManager.WakeLock screenLock =((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP,"TAG"); screenLock.acquire();//later screenLock.release();
    }

}
