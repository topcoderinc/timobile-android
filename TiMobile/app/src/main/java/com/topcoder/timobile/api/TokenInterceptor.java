package com.topcoder.timobile.api;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.blankj.utilcode.util.ActivityUtils;
import com.topcoder.timobile.activity.LoginActivity;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;


import java.io.IOException;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * The interceptor for ti mobile api that adds token to the authorization header.
 */

class TokenInterceptor implements Interceptor {
  /**
   * The log tag.
   */
  private String TAG = TokenInterceptor.class.getName();

  /**
   * the access token.
   */
  private String accessToken;

  /**
   * the expire time.
   */
  private Date accessTokenValidUntil;

  /**
   * the shared Preferences.
   */
  private SharedPreferences sharedPreferences;

  TokenInterceptor() {
    sharedPreferences = AppUtils.getPreferences();
  }

  /**
   * get token from sharedPreferences and check expires or not.
   */
  void checkToken() {
    accessToken = sharedPreferences.getString(AppConstants.TOKEN_KEY, null);
    long expireTimeStamp = sharedPreferences.getLong(AppConstants.TOKEN_EXPIRES_KEY, 0);
    if (expireTimeStamp == 0) {
      accessTokenValidUntil = null;
    } else {
      accessTokenValidUntil = new Date();
    }
  }


  @Override
  public Response intercept(Chain chain) throws IOException {
    checkToken();
    Request request = chain.request();
    // add the authorisation token
    Request firstRequest = request.newBuilder()
        .addHeader("Authorization", "Bearer " + accessToken)
        .addHeader("Content-Type", "application/json; charset=utf-8")
        .addHeader("Accept", "application/json")
        .build();
    Response response = chain.proceed(firstRequest);


    //check response and refresh token if needed
    if (!response.isSuccessful()) {

      if (response.code() == 401) {  // token expires
        //refresh the token
        try {
          AppUtils.getPreferences().edit().clear().apply(); // clear token
          ActivityUtils.startActivity(LoginActivity.class);
          Activity activity = ActivityUtils.getTopActivity();
          if (activity != null) {
            activity.finish();
          }
        } catch (Exception ex) {
          Log.e("", "Error refreshing access token", ex);
        }
      }
    }

    return response;

  }
}
