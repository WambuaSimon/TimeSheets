package biz.wizag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/04/2018.
 */

public class AuthUser {


    @SerializedName("token")
    @Expose
    private String accessToken;




    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


}
