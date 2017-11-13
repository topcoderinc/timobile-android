package com.topcoder.timobile.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.request.LoginRequest;
import com.topcoder.timobile.utility.AppUtils;

/**
 * Author: Harshvardhan
 * Date: 26/10/17.
 */
public class LoginActivity extends BaseActivity {

  @BindView(R.id.tvError) TextView tvError;
  @BindView(R.id.etEmail) EditText etEmail;
  @BindView(R.id.linearEmail) LinearLayout linearEmail;
  @BindView(R.id.etPassword) EditText etPassword;
  @BindView(R.id.linearPassword) LinearLayout linearPassword;
  @BindView(R.id.btnSignIn) Button btnSignIn;
  @BindView(R.id.btnFacebook) Button btnFacebook;
  @BindView(R.id.tvForgot) TextView tvForgot;
  @BindView(R.id.linearSignUp) LinearLayout linearSignUp;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
  }

  @OnClick({ R.id.btnSignIn, R.id.btnFacebook, R.id.tvForgot, R.id.linearSignUp }) public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btnSignIn:
        KeyboardUtils.hideSoftInput(this);
        if (checkValidation()) {
          showLoadingDialog();
          LoginRequest loginRequest = new LoginRequest();
          loginRequest.setEmail(AppUtils.getText(etEmail));
          loginRequest.setPassword(AppUtils.getText(etPassword));
          apiService.login(loginRequest).subscribe(this::onSuccess, this::onFailure);
        } else {
          showError();
        }
        break;
      case R.id.btnFacebook:
        ToastUtils.showShort("Facebook");
        break;
      case R.id.tvForgot:
        ToastUtils.showShort("Forgot Password");
        break;
      case R.id.linearSignUp:
        ToastUtils.showShort("Sign Up");
        break;
    }
  }

  private void showError() {
    btnSignIn.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
    tvError.setVisibility(View.VISIBLE);
    linearEmail.setAlpha(1.0f);
    linearPassword.setAlpha(1.0f);
  }

  private void onFailure(Throwable throwable) {
    cancelLoadingDialog();
    showError();
  }

  private void onSuccess(String success) {
    cancelLoadingDialog();
    ActivityUtils.startActivity(HomeActivity.class);
    finish();
  }

  //check validation
  private boolean checkValidation() {
    return (RegexUtils.isEmail(AppUtils.getText(etEmail)) && !StringUtils.isEmpty(AppUtils.getText(etPassword)));
  }
}
