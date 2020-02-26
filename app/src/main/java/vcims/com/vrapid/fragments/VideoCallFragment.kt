package vcims.com.vrapid.fragments


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.kenante.video.core.KenanteMediaStreamManager
import com.kenante.video.core.KenanteSession
import com.kenante.video.enums.KenanteBitrate
import com.kenante.video.enums.MediaType
import com.kenante.video.interfaces.KenanteMediaStreamEventListener
import com.kenante.video.interfaces.UserCallEventListener
import com.kenante.video.media.KenanteAudioTrack
import com.kenante.video.media.KenanteVideoTrack
import com.kenante.video.view.KenanteSurfaceView
import org.webrtc.RendererCommon
import vcims.com.vrapid.R
import vcims.com.vrapid.activities.CallActivity
import vcims.com.vrapid.handler_classes.CallData
import vcims.com.vrapid.interfaces.FragmentCallbacks
import vcims.com.vrapid.util.Constants
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class VideoCallFragment : Fragment(), UserCallEventListener, KenanteMediaStreamEventListener {

    var TAG = VideoCallFragment::class.java.simpleName
    var fragmentCallbacks: FragmentCallbacks? = null
    var videoViews = SparseArray<View>()
    var recyclerViewWidth = 0
    var recyclerViewHeight = 0
    var kenanteSession = KenanteSession.getInstance()
    var callData: CallData? = null
    var videoClickLisntener: View.OnClickListener? = null
    var audioClickListener: View.OnClickListener? = null
    var activeHearUsers: MutableSet<Int>? = mutableSetOf()
    var audioVideoStatus: HashMap<String, String>? = null
    var videoTrackMap: SparseArray<KenanteVideoTrack>? = null
    var isTranslatorPresent = false
    var activeRemoteUsers: MutableSet<Int>? = mutableSetOf()

    // Views
    var confCallOnePersonView: View? = null
    var videoGrid: GridLayout? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentCallbacks = context as FragmentCallbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video_call, container, false)

        parseExtras()
        init(view)
        clickListener()
        fragmentCallbacks!!.onFragmentStarted(2, true)

        return view
    }

    private fun parseExtras() {
        recyclerViewWidth = arguments?.getInt(Constants.EXTRA_VIDEO_SHOW_WIDTH)!!
        recyclerViewHeight = arguments?.getInt(Constants.EXTRA_VIDEO_SHOW_HEIGHT)!!
    }

    private fun init(view: View) {
        confCallOnePersonView = view.findViewById(R.id.conf_call_one_person_view)
        videoGrid = view.findViewById(R.id.video_grid)
        kenanteSession.registerUserCallEventListener(this)
        kenanteSession.registerMediaStreamEventListener(this)
        callData = CallData.getInstance(activity?.applicationContext)
        audioVideoStatus = callData!!.audioVideoStatus
    }

    private fun clickListener() {
        videoClickLisntener = View.OnClickListener {
            val videoTrack = getVideoTracks().get(callData!!.currentUserId)
            if (videoTrack != null) {
                if (videoTrack.enabled()) {
                    videoTrack.setEnabled(false)
                    it!!.background = resources.getDrawable(R.drawable.video_control_icon_disabled)
                } else {
                    videoTrack.setEnabled(true)
                    it!!.background = resources.getDrawable(R.drawable.video_control_icon_enabled)
                }
            }
        }

        audioClickListener = View.OnClickListener {
            val userId = Integer.valueOf(it!!.contentDescription.toString())
            var status = false
            val audioTrack = KenanteMediaStreamManager.GetManager().getAudioTrack(userId)
            if (audioTrack != null) {
                status = !audioTrack.enabled()
                if (status) {
                    audioTrack.setEnabled(true)
                } else
                    audioTrack.setEnabled(false)
            }
            toggleVoiceIcon(userId, status, it)
        }
    }

    private fun toggleVoiceIcon(userId: Int, enable: Boolean, view: View) {
        if (enable) {
            view.background = activity!!.resources.getDrawable(R.drawable.ic_voice_enable)
            if (!activeHearUsers?.contains(userId)!!) activeHearUsers?.add(userId)
        } else {
            view.background = activity!!.resources.getDrawable(R.drawable.ic_voice_disable)
            //voiceControl.setBackground(getResources().getDrawable(R.drawable.ic_voice_disable));
            if (activeHearUsers?.contains(userId)!!) activeHearUsers?.remove(userId)
        }
    }

    override fun onUserAvailable(userId: Int) {
        Log.e(TAG, "User with id $userId joined the call")
        kenanteSession.configureUser(userId, audio = true, video = true, bitrate = KenanteBitrate.low)
        kenanteSession.subscribeToPublisher(userId)
    }

    override fun onUserConnectedToCall(userId: Int) {
        Log.e(TAG, "onUserConnectedToCall called with id: $userId")
        //Connection status
        if (userId == callData!!.currentUserId)
            return

        if (userId != callData!!.currentUserId)
            activeRemoteUsers?.add(userId)

        if (callData!!.seeUsers.contains(userId)) {
            makeUI(userId)
            if (videoViews[userId] != null)
                videoViews[userId].findViewWithTag<View>("connectionStatusTV").background = activity!!.resources.getDrawable(R.drawable.user_connected)

        }

        updateUsersList(userId, true)
        updateOnePersonView()
        updateUserConnectionStatus(callData!!.longNames!![userId], 1)
    }

    private fun updateUsersList(userId: Int, active: Boolean) {
        val usersListFragment = activity!!.supportFragmentManager.findFragmentByTag(Constants.USERS_LIST_FRAG) as UsersListFragment?
        if (active) {
            if (usersListFragment != null) if (usersListFragment.isAdded) usersListFragment.changeStatus(userId, true)
        } else {
            if (usersListFragment != null) if (usersListFragment.isAdded) usersListFragment.changeStatus(userId, false)
        }
    }

    private fun updateOnePersonView() {
        if (activeRemoteUsers!!.size > 0) {
            if (confCallOnePersonView?.visibility == View.VISIBLE)
                confCallOnePersonView?.visibility = View.GONE
        } else {
            if (confCallOnePersonView?.visibility != View.VISIBLE)
                confCallOnePersonView?.visibility = View.VISIBLE
        }
    }

    override fun onUserConnectionClosed(userId: Int) {
        Log.e(TAG, "onUserConnectionClosed called with id: $userId")
        if (activeHearUsers!!.contains(userId)) activeHearUsers!!.remove(userId)

        if (activeRemoteUsers!!.contains(userId))
            activeRemoteUsers?.remove(userId)

        removeUI(userId)

        updateOnePersonView()
        updateUsersList(userId, false)
        updateUserConnectionStatus(callData!!.longNames!![userId], 3)
    }

    private fun updateUserConnectionStatus(s: String?, i: Int) {
        (activity as CallActivity).showUserConnectionStatusToast(s, i)
    }

    override fun onUserDisconnectedFromCall(userId: Int) {
        Log.e(TAG, "onUserDisconnectedFromCall called with id: $userId")
        //Connection status

        if (videoViews[userId] != null)
            videoViews[userId].findViewWithTag<View>("connectionStatusTV").background = activity!!.resources.getDrawable(R.drawable.user_disconneted)

        updateUserConnectionStatus(callData!!.longNames!![userId], 2)
    }

    override fun onLocalAudioStream(audioTrack: KenanteAudioTrack) {
        Log.e(TAG, "onLocalAudioStream call with id: ${audioTrack.userId}")
        if (audioVideoStatus?.get("audio") == "1") {
            // Remove this audio track
        }

    }

    override fun onLocalVideoStream(videoTrack: KenanteVideoTrack) {
        Log.e(TAG, "onLocalVideoStream call with id: ${videoTrack.userId}")

        (activity as CallActivity).showLoadingScreen(false, null)

        if (audioVideoStatus?.get("video") == "1") {
            // Remove this video track
        } else {
            if (callData!!.currentUserType != Constants.TRANSLATOR) {
                fragmentCallbacks!!.localVideoExists()
                getVideoTracks().put(videoTrack.userId, videoTrack)
                makeUI(videoTrack.userId)
            }
        }
    }

    override fun onMediaStartedFlowing(userId: Int, mediaType: MediaType) {
        Log.e(TAG, "onMediaStartedFlowing call with id: $userId and media: $mediaType")
    }

    override fun onMediaStoppedFlowing(userId: Int, mediaType: MediaType) {
        Log.e(TAG, "onMediaStoppedFlowing call with id: $userId and media: $mediaType")
    }

    override fun onRemoteAudioStream(audioTrack: KenanteAudioTrack) {
        Log.e(TAG, "onRemoteAudioStream call with id: ${audioTrack.userId}")
        if (callData!!.hearUsers.contains(audioTrack.userId)) {
            activeHearUsers!!.add(audioTrack.userId)
        }
    }

    override fun onRemoteVideoStream(videoTrack: KenanteVideoTrack) {
        Log.e(TAG, "onRemoteVideoStream call with id: ${videoTrack.userId}")

        if (callData!!.seeUsers.contains(videoTrack.userId)) {
            getVideoTracks().put(videoTrack.userId, videoTrack)
        }

        if (videoViews.get(videoTrack.userId) != null) {
            //Attach video stream
            attachVideoStream(videoTrack)
        }
    }

    private fun getVideoTracks(): SparseArray<KenanteVideoTrack> {
        if (videoTrackMap == null) {
            videoTrackMap = SparseArray()
        }
        return videoTrackMap!!
    }

    fun makeUI(id: Int) {
        if (videoViews.get(id) == null) {
            val view = layoutInflater.inflate(R.layout.show_video_row, null)
            videoViews.put(id, view)
            makeUiAccordingToUser(id, view)
            view.contentDescription = "$id"
            videoGrid?.addView(view)
            resizeAllViews()
            changeGridLayoutParameters(videoViews.size())
        } else {
            if (callData?.currentUserId == id) {
                if ((activity as CallActivity).isScreenSharing) {

                } else {

                }
                val v = videoViews[id]
                val kenanteSurfaceView: KenanteSurfaceView = v.findViewWithTag("videoShowView")
                KenanteMediaStreamManager.GetManager().getVideoTrack(id)?.addSink(kenanteSurfaceView)
            }
        }
    }

    fun attachVideoStream(videoTrack: KenanteVideoTrack) {
        val view = videoViews.get(videoTrack.userId)
        val kenanteSurfaceView: KenanteSurfaceView = view.findViewWithTag("videoShowView")

        if (videoTrack.getSink() == null) {
            videoTrack.addSink(kenanteSurfaceView)
        }
    }

    fun removeUI(id: Int) {
        if (videoViews.get(id) != null) {
            val view = videoViews.get(id)
            val surfaceViewRenderer: KenanteSurfaceView = view.findViewWithTag("videoShowView")
            surfaceViewRenderer.release()
            videoGrid?.removeView(view)
            videoViews.remove(id)
            resizeAllViews()
            changeGridLayoutParameters(videoViews.size())
        }
        if (getVideoTracks().get(id) != null) {
            getVideoTracks().get(id).removeSink(getVideoTracks().get(id).getSink())
            getVideoTracks().remove(id)
        }
    }

    private fun changeGridLayoutParameters(totalSize: Int) {
        when (totalSize) {
            in 1..2 -> {
                videoGrid?.columnCount = 1
                videoGrid?.rowCount = 2
            }
            in 3..4 -> {
                videoGrid?.columnCount = 2
                videoGrid?.rowCount = 2
            }
            in 5..6 -> {
                videoGrid?.columnCount = 2
                videoGrid?.rowCount = 3
            }
            in 7..9 -> {
                videoGrid?.columnCount = 3
                videoGrid?.rowCount = 3
            }
            in 10..16 -> {
                videoGrid?.columnCount = 4
                videoGrid?.rowCount = 4
            }
        }
    }

    fun resizeAllViews() {
        val width = getWidth(recyclerViewWidth, videoViews.size())
        val height = getHeight(recyclerViewHeight, videoViews.size())
        for (i in 0 until videoViews.size()) {
            val v = videoViews.valueAt(i)
            v.layoutParams = GridLayout.LayoutParams()
            v.layoutParams.width = width
            v.layoutParams.height = height
            v.requestLayout()
            v.requestFocus()
        }
    }

    fun getWidth(parentWidth: Int, size: Int): Int {
        return when {
            size in 1..2 -> parentWidth
            size in 3..6 -> parentWidth / 2
            size in 7..9 -> parentWidth / 3
            size > 9 -> parentWidth / 4
            else -> 0
        }
    }

    fun getHeight(parentHeight: Int, size: Int): Int {
        return when {
            size == 1 -> parentHeight
            size in 2..4 -> parentHeight / 2
            size in 5..6 -> parentHeight / 3
            size in 7..9 -> parentHeight / 3
            size > 9 -> parentHeight / 4
            else -> 0
        }
    }

    private fun makeUiAccordingToUser(userId: Int, v: View) {
        if (userId == callData?.currentUserId) {
            v.findViewWithTag<View>("connectionStatusTV").visibility = View.GONE
        }
        //Video Control UI
        val videoControl = v.findViewWithTag<ImageButton>("videoControl")
        videoControl.contentDescription = userId.toString() + ""
        videoControl.setOnClickListener(videoClickLisntener)
        if (userId != callData?.currentUserId) {
            videoControl.visibility = View.GONE
        }
        //Name of user UI
        (v.findViewWithTag<View>("videoShowUserNameTV") as TextView).text = callData!!.longNames[userId]

        //Attaching Video
        val kenanteSurfaceView: KenanteSurfaceView = v.findViewWithTag("videoShowView")
        kenanteSurfaceView.setZOrderMediaOverlay(false)
        val isMirror = userId == callData?.currentUserId

        kenanteSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
        kenanteSurfaceView.setMirror(isMirror)
        kenanteSurfaceView.requestLayout()

        //Attach video stream
        val videoTrack = getVideoTracks().get(userId)
        videoTrack?.addSink(kenanteSurfaceView)

        //Audio Control UI
        val audioControl = v.findViewWithTag<ImageButton>("videoVoiceControl")
        audioControl.contentDescription = userId.toString() + ""
        audioControl.setOnClickListener(audioClickListener)
        if (callData!!.hearUsers.contains(userId) && callData!!.currentUserType == Constants.MODERATOR || callData!!.currentUserType == Constants.CLIENT) {
            if (userId == callData?.currentUserId) {

                val audioTrack = KenanteMediaStreamManager.GetManager().getAudioTrack(userId)
                if (audioTrack != null) {
                    if (audioTrack.enabled()) audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_enable) else audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_disable)
                }

                /*if (currentSession != null) if (currentSession.getMediaStreamManager() != null) {
                    val audioTrack: QBRTCAudioTrack = currentSession.getMediaStreamManager().getLocalAudioTrack()
                    if (audioTrack != null) {
                        if (audioTrack.enabled()) audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_enable) else audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_disable)
                    }
                }*/
            } else {

                val audioTrack = KenanteMediaStreamManager.GetManager().getAudioTrack(userId)
                if (audioTrack != null) {
                    if (callData!!.currentUserType == Constants.CLIENT) { //TODO: Handle client block
                        if (isTranslatorPresent) { //Translator is present and has joined before moderator and respondent
                            /*if (allUsersTypes.get(userId) == Constants.MODERATOR || allUsersTypes.get(userId) == Constants.RESPONDENT) {
                                if (!isTranslatorAudioPaused) {
                                    audioTrack.pauseAudio()
                                    audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_disable)
                                } else {
                                    audioTrack.resumeAudio()
                                    audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_enable)
                                }
                            }*/
                        } else {
                            //Translator either doesn't exist or hasn't yet joined
                            // Continue normal operation
                            if (audioTrack.enabled()) audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_enable) else audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_disable)
                        }
                    } else {
                        if (audioTrack.enabled()) audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_enable) else audioControl.background = activity!!.resources.getDrawable(R.drawable.ic_voice_disable)
                    }
                }
            }
        } else audioControl.visibility = View.GONE
    }

    fun clearAllObjects() {
        for (i in 0 until videoViews.size()) {
            val v = videoViews.valueAt(i)
            if (v != null) {
                v.findViewWithTag<KenanteSurfaceView>("videoShowView")?.release()
            }
        }
        videoGrid?.removeAllViews()
        videoViews.clear()
        removeVideoTrackRenderers()
        removeAllListeners()
    }

    fun removeVideoTrackRenderers() {
        val videoTracks = getVideoTracks()
        for (i in 0..videoTracks.size()) {
            if (videoTracks.size() > 0) {
                val track = videoTracks.valueAt(i)
                track?.removeSink(track.getSink())
            } else {
                break
            }
        }
    }

    fun removeAllListeners() {
        kenanteSession.unregisterUserCallEventListener(this)
        kenanteSession.unregisterMediaStreamEventListener(this)
    }

    fun cameraSwitch(isCameraFront: Boolean) {
        val v = videoViews[callData!!.currentUserId]
        if (v != null) {
            val kenanteSurfaceView: KenanteSurfaceView = v.findViewWithTag("videoShowView")
            kenanteSurfaceView.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            kenanteSurfaceView.setMirror(isCameraFront)
            kenanteSurfaceView.requestLayout()
        }
    }

}
