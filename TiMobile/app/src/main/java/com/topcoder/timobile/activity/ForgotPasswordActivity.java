package com.topcoder.timobile.activity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.model.request.ChangeForgotPassword;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * forgot password page
 */

public class ForgotPasswordActivity extends BaseActivity {

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;

  @BindView(R.id.etToken) EditText etToken;
  @BindView(R.id.etNewPassword) EditText etNewPassword;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    ButterKnife.bind(this);
    tvTitle.setText("Forgot password");
    imgClose.setImageResource(R.drawable.ic_back);
    this.showMailSucceedDialog();
  }


  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  /**
   * check string cannot be empty
   *
   * @param value    the value
   * @param key      the value key
   * @param errorMsg the error message
   * @return the newest error msg
   */
  private String checkEmpty(String value, String key, String errorMsg) {
    if (StringUtils.isTrimEmpty(value)) {
      errorMsg += key + " cannot be empty\n";
    }
    return errorMsg;
  }

  @OnClick(R.id.btnResetPassword) void onResetPassword() {
    String token = etToken.getText().toString();
    String newPassword = etNewPassword.getText().toString();

    String errorMsg = "";
    errorMsg = checkEmpty(token, "Forgot password token", errorMsg);
    errorMsg = checkEmpty(newPassword, "New password", errorMsg);
    if (errorMsg.length() > 0) {
      ToastUtils.showShort(errorMsg.substring(0, errorMsg.length() - 1));
    } else {
      ChangeForgotPassword changeForgotPassword = new ChangeForgotPassword();
      changeForgotPassword.setEmail(getIntent().getStringExtra(AppConstants.KEY_OBJ));
      changeForgotPassword.setForgotPasswordToken(token);
      changeForgotPassword.setNewPassword(newPassword);
      showLoadingDialog();

      apiService.changeForgotPassword(changeForgotPassword).subscribe(voidResponse -> {
        cancelLoadingDialog();
        finish();
        ToastUtils.showLong("password update succeed");
      }, throwable -> {
        cancelLoadingDialog();
        Timber.e(throwable);
        AppUtils.showError(throwable, "Change forgot password failed");
      });
    }
  }


  /**
   * show dialog that email send succeed
   */
  private void showMailSucceedDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Email successfully sent");
    builder.setMessage("An email with token already send to your email box, please check it");
    builder.setCancelable(true);
    builder.setPositiveButton("OK", (dialog, id) -> {
      dialog.cancel();
    });
    builder.create().show();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    AppUtils.hideKeyBoardWhenClickOther(ev, this);
    return super.dispatchTouchEvent(ev);
  }
}
