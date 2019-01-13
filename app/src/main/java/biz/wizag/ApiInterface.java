package biz.wizag;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by User on 12/04/2018.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<AuthUser> loginUser(@Field("email") String username,
                             @Field("password") String password);

}
