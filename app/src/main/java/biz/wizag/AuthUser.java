package biz.wizag;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 12/04/2018.
 */

public class AuthUser {

    @SerializedName("token_type")
    @Expose
    private String tokenType;

    @SerializedName("expires_in")
    @Expose
    private Float expiresIn;

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    @SerializedName("error")
    @Expose
    private String error;

    @SerializedName("message")
    @Expose
    private String message;


    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Float getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Float expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
