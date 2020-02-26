package vcims.com.vrapid.models;

import com.google.gson.annotations.SerializedName;

public class ScheduleModel {

    @SerializedName("id")
    int id;
    @SerializedName("room")
    String room;
    @SerializedName("timezone")
    String timezone;
    @SerializedName("sch_start_date")
    String sch_start_date;
    @SerializedName("sch_start_time")
    String sch_start_time;
    @SerializedName("sch_end_time")
    String sch_end_time;
    @SerializedName("room_type")
    String room_type;
    @SerializedName("gender")
    String gender;
    @SerializedName("age")
    String age;
    @SerializedName("sec")
    String sec;
    @SerializedName("usership")
    String usership;
    @SerializedName("secure_flag")
    String secureFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getSch_start_date() {
        return sch_start_date;
    }

    public void setSch_start_date(String sch_start_date) {
        this.sch_start_date = sch_start_date;
    }

    public String getSch_start_time() {
        return sch_start_time;
    }

    public void setSch_start_time(String sch_start_time) {
        this.sch_start_time = sch_start_time;
    }

    public String getSch_end_time() {
        return sch_end_time;
    }

    public void setSch_end_time(String sch_end_time) {
        this.sch_end_time = sch_end_time;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getUsership() {
        return usership;
    }

    public void setUsership(String usership) {
        this.usership = usership;
    }

    public String getSecureFlag() {
        return secureFlag;
    }

    public void setSecureFlag(String secureFlag) {
        this.secureFlag = secureFlag;
    }
}
