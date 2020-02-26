package vcims.com.vrapid.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import vcims.com.vrapid.retrofit.serverresponse.SyncUsersSR;
import vcims.com.vrapid.retrofit.serverresponse.UserLoginSR;
import vcims.com.vrapid.util.Constants;

/**
 * Created by VCIMS-PC2 on 26-09-2017.
 */

public interface RetrofitInterface {

    //Here we can declare different calls for different web services and each call will have some parameters that
    //we can send to the web services for getting data.

    @FormUrlEncoded
    @POST(Constants.LOGIN_URL)
    Call<UserLoginSR> loginUser(
            @Field("login") String mobile,
            @Field("password") String password,
            @Field("imei") String imei,
            @Field("gcmid") String gcmid
            );

    @FormUrlEncoded
    @POST(Constants.SYNC_USERS)
    Call<SyncUsersSR> syncUsers(
            @Field("uid") String user_id
    );

    @FormUrlEncoded
    @POST(Constants.LOGOUT_URL)
    Call<UserLoginSR> LogoutUser(
            @Field("userid") String userId
    );

}
