package vcims.com.vrapid.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by VCIMS-PC2 on 22-01-2018.
 */

public class UserId {

    @SerializedName("user_id")
    String user_id;
    @SerializedName("app_id")
    String app_id;
    @SerializedName("auth_key")
    String auth_key;
    @SerializedName("auth_secret")
    String auth_secret;
    @SerializedName("account_key")
    String acc_key;
    @SerializedName("api_domain")
    String api_domain;
    @SerializedName("chat_domain")
    String chat_domain;
    @SerializedName("janu_server")
    String janu_server;
    @SerializedName("janu_protocol")
    String janu_protocol;
    @SerializedName("janu_plugin")
    String janu_plugin;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getAuth_key() {
        return auth_key;
    }

    public void setAuth_key(String auth_key) {
        this.auth_key = auth_key;
    }

    public String getAuth_secret() {
        return auth_secret;
    }

    public void setAuth_secret(String auth_secret) {
        this.auth_secret = auth_secret;
    }

    public String getAcc_key() {
        return acc_key;
    }

    public void setAcc_key(String acc_key) {
        this.acc_key = acc_key;
    }

    public String getApi_domain() {
        return api_domain;
    }

    public void setApi_domain(String api_domain) {
        this.api_domain = api_domain;
    }

    public String getChat_domain() {
        return chat_domain;
    }

    public void setChat_domain(String chat_domain) {
        this.chat_domain = chat_domain;
    }

    public String getJanu_server() {
        return janu_server;
    }

    public void setJanu_server(String janu_server) {
        this.janu_server = janu_server;
    }

    public String getJanu_protocol() {
        return janu_protocol;
    }

    public void setJanu_protocol(String janu_protocol) {
        this.janu_protocol = janu_protocol;
    }

    public String getJanu_plugin() {
        return janu_plugin;
    }

    public void setJanu_plugin(String janu_plugin) {
        this.janu_plugin = janu_plugin;
    }
}
