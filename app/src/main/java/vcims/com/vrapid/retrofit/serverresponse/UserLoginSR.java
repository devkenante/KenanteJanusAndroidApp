package vcims.com.vrapid.retrofit.serverresponse;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import vcims.com.vrapid.models.UserId;

/**
 * Created by VCIMS-PC2 on 22-01-2018.
 */

public class UserLoginSR {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("response")
    UserId userId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
