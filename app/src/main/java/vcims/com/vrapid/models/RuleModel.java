package vcims.com.vrapid.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by VCIMS-PC2 on 14-02-2018.
 */

public class RuleModel {

    @SerializedName("see")
    ArrayList<Integer> see;
    @SerializedName("talk")
    ArrayList<Integer> talk;
    @SerializedName("hear")
    ArrayList<Integer> hear;
    @SerializedName("chat")
    ArrayList<Integer> chat;
    @SerializedName("self_video_off")
    int self_video_off;
    @SerializedName("self_audio_off")
    int self_audio_off;
    @SerializedName("room_schedule")
    ScheduleModel room_schedule;

    public ArrayList<Integer> getSee() {
        return see;
    }

    public void setSee(ArrayList<Integer> see) {
        this.see = see;
    }

    public ArrayList<Integer> getTalk() {
        return talk;
    }

    public void setTalk(ArrayList<Integer> talk) {
        this.talk = talk;
    }

    public ArrayList<Integer> getHear() {
        return hear;
    }

    public void setHear(ArrayList<Integer> hear) {
        this.hear = hear;
    }

    public ArrayList<Integer> getChat() {
        return chat;
    }

    public void setChat(ArrayList<Integer> chat) {
        this.chat = chat;
    }

    public int getSelf_video_off() {
        return self_video_off;
    }

    public void setSelf_video_off(int self_video_off) {
        this.self_video_off = self_video_off;
    }

    public int getSelf_audio_off() {
        return self_audio_off;
    }

    public void setSelf_audio_off(int self_audio_off) {
        this.self_audio_off = self_audio_off;
    }

    public ScheduleModel getRoom_schedule() {
        return room_schedule;
    }

    public void setRoom_schedule(ScheduleModel room_schedule) {
        this.room_schedule = room_schedule;
    }
}
