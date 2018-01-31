package com.topcoder.timobile.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.BuildConfig;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.AuthToken;
import com.topcoder.timobile.model.request.LoginRequest;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: Harshvardhan
 * Date: 26/10/17.
 */
public class LoginActivity extends BaseActivity {

  @BindView(R.id.tvError)
  TextView tvError;
  @BindView(R.id.etEmail)
  EditText etEmail;
  @BindView(R.id.linearEmail)
  LinearLayout linearEmail;
  @BindView(R.id.etPassword)
  EditText etPassword;
  @BindView(R.id.linearPassword)
  LinearLayout linearPassword;
  @BindView(R.id.btnSignIn)
  Button btnSignIn;
  @BindView(R.id.btnFacebook)
  Button btnFacebook;
  @BindView(R.id.tvForgot)
  TextView tvForgot;
  @BindView(R.id.linearSignUp)
  LinearLayout linearSignUp;

  private String TAG = LoginActivity.class.getName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);

    this.checkAuthToken();
  }


  @OnClick({R.id.btnSignIn, R.id.btnFacebook, R.id.tvForgot, R.id.linearSignUp})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btnSignIn:
        KeyboardUtils.hideSoftInput(this);
        if (checkValidation()) {
          showLoadingDialog();
          LoginRequest loginRequest = new LoginRequest();
          loginRequest.setEmail(AppUtils.getText(etEmail));
          loginRequest.setPassword(AppUtils.getText(etPassword));
          apiService.signin(loginRequest).subscribe(this::onTokenFetched, this::onFailure);
        } else {
          showError(null);
        }
        break;
      case R.id.btnFacebook:
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.API_URL + "/api/v1/auth/facebook"));
        startActivity(myIntent);
        break;
      case R.id.tvForgot:
        String email = etEmail.getText().toString();
        if (!RegexUtils.isEmail(email)) {
          ToastUtils.showShort("You must enter a valid email address");
          return;
        }
        showLoadingDialog();
        apiService.initiateForgotPassword(email).subscribe(voidResponse -> {
          cancelLoadingDialog();
          Intent intent = new Intent(this, ForgotPasswordActivity.class);
          intent.putExtra(AppConstants.KEY_OBJ, email);
          ActivityUtils.startActivity(intent);
        },this::onFailure);
        break;
      case R.id.linearSignUp:
        ActivityUtils.startActivity(SignupActivity.class);
        break;
    }
  }

  /**
   * on auto token fetched
   *
   * @param token the token form backend
   */
  private void onTokenFetched(AuthToken token) {
    cancelLoadingDialog();
    showLoadingDialog();
    SharedPreferences sp = AppUtils.getPreferences();
    sp.edit().putString(AppConstants.TOKEN_KEY, token.getAccessToken())
        .putLong(AppConstants.TOKEN_EXPIRES_KEY, token.getAccessTokenValidUntil().getTime()).apply();
    Log.d(TAG, "onViewClicked: login request succeed. " + token);
    this.populateApiCache();
  }

  private void showError(String msg) {
    btnSignIn.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
    tvError.setText(msg == null ? getResources().getString(R.string.login_error) : msg);
    tvError.setVisibility(View.VISIBLE);
    linearEmail.setAlpha(1.0f);
    linearPassword.setAlpha(1.0f);
  }

  private void onFailure(Throwable throwable) {
    cancelLoadingDialog();
    Log.d(TAG, "onFailure: " + throwable.getMessage());
    showError(AppUtils.getErrorMessage(throwable));
  }


  /**
   * when facebook login back
   */
  @Override protected void onResume() {
    super.onResume();
    Log.d(TAG, "onResume: ");
    Uri openUri = getIntent().getData();
    if (openUri != null) {
      Log.d(TAG, "onResume: got uri = " + openUri);
      String accessToken = openUri.getQueryParameter("accessToken");
      Long accessTokenValidUntil = Long.valueOf(openUri.getQueryParameter("accessTokenValidUntil"));
      AuthToken authToken = new AuthToken();
      authToken.setAccessToken(accessToken);
      authToken.setAccessTokenValidUntil(new Date(accessTokenValidUntil));
      this.onTokenFetched(authToken);
    }
  }

  /**
   * get current user and cache it.
   */
  private void populateApiCache() {
    Log.d(TAG, "populateApiCache: populateApiCache");
    apiService.getUser().subscribe(user -> this.onSuccess(), this::onFailure);
  }

  /**
   * login success
   */
  private void onSuccess() {
    cancelLoadingDialog();
    ActivityUtils.startActivity(HomeActivity.class);
    finish();
  }

  /**
   * Check auth token, if token valid, then direct goto succeed.
   */
  private void checkAuthToken() {
    Log.d(TAG, "checkAuthToken: check token and token expires time");
    SharedPreferences sharedPreferences = AppUtils.getPreferences();
    String token = sharedPreferences.getString(AppConstants.TOKEN_KEY, null);
    long expireTimeStamp = sharedPreferences.getLong(AppConstants.TOKEN_EXPIRES_KEY, 0);
    Date accessTokenValidUntil = null;
    if (expireTimeStamp > 0) {
      accessTokenValidUntil = new Date(expireTimeStamp);
    }
    Log.d(TAG, "checkAuthToken: " + token);
    Log.d(TAG, "checkAuthToken: " + accessTokenValidUntil);
    Log.d(TAG, "checkAuthToken: " + new Date(System.currentTimeMillis()));
    if (token != null && accessTokenValidUntil.after(new Date(System.currentTimeMillis()))) {
      Log.d(TAG, "checkAuthToken: token exist and don't expires , so goto succeed.");
      showLoadingDialog();
      populateApiCache();
    }
  }

  //check validation
  private boolean checkValidation() {
    return (RegexUtils.isEmail(AppUtils.getText(etEmail)) && !StringUtils.isEmpty(AppUtils.getText(etPassword)));
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    AppUtils.hideKeyBoardWhenClickOther(ev, this);
    return super.dispatchTouchEvent(ev);
  }
}
