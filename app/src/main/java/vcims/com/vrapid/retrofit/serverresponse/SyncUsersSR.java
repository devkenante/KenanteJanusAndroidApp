package vcims.com.vrapid.retrofit.serverresponse;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import vcims.com.vrapid.models.KenanteUser;
import vcims.com.vrapid.models.RuleModel;

/**
 * Created by VCIMS-PC2 on 23-01-2018.
 */

public class SyncUsersSR {

    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private ArrayList<RuleModel> ruleModel;
    @SerializedName("response")
    private ArrayList<KenanteUser> response;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<RuleModel> getRuleModel() {
        return ruleModel;
    }

    public void setRuleModel(ArrayList<RuleModel> ruleModel) {
        this.ruleModel = ruleModel;
    }

    public ArrayList<KenanteUser> getResponse() {
        return response;
    }

    public void setResponse(ArrayList<KenanteUser> response) {
        this.response = response;
    }

}
