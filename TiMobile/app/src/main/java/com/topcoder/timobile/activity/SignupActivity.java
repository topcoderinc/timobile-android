package com.topcoder.timobile.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.topcoder.timobile.model.request.UpdatePassword;
import com.topcoder.timobile.utility.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * sign up activity
 */

public class SignupActivity extends BaseActivity {

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;

  @BindView(R.id.etFirstName) EditText etFirstName;
  @BindView(R.id.etSecondName) EditText etSecondName;
  @BindView(R.id.etEmail) EditText etEmail;
  @BindView(R.id.etNewPassword) EditText etNewPassword;
  @BindView(R.id.etConfirmPassword) EditText etConfirmPassword;

  private String TAG = SignupActivity.class.getName();


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    ButterKnife.bind(this);

    tvTitle.setText("Sign Up");
    imgClose.setImageResource(R.drawable.ic_back);
  }


  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  private String checkEmtpty(String value, String key, String errorMsg) {
    if (StringUtils.isTrimEmpty(value)) {
      errorMsg += key + " cannot be empty\n";
    }
    return errorMsg;
  }

  @OnClick(R.id.btnSignUp) void onSignUp() {
    String firstName = etFirstName.getText().toString();
    String lastName = etSecondName.getText().toString();
    String email = etEmail.getText().toString();
    String password = etNewPassword.getText().toString();
    String confirm = etConfirmPassword.getText().toString();

    String errorMsg = "";
    errorMsg = checkEmtpty(email, "Email", errorMsg);
    errorMsg = checkEmtpty(firstName, "First Name", errorMsg);
    errorMsg = checkEmtpty(lastName, "Last Name", errorMsg);
    errorMsg = checkEmtpty(password, "New Password", errorMsg);
    errorMsg = checkEmtpty(confirm, "Confirm password", errorMsg);

    if (!password.equals(confirm)) {
      errorMsg += "password must be equal with confirm password\n";
    }
    if (errorMsg.length() > 0) {
      ToastUtils.showShort(errorMsg.substring(0, errorMsg.length() - 1));
    } else {
      User user = new User();
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setEmail(email);
      user.setPassword(password);
      showLoadingDialog();

      apiService.singup(user).subscribe(u -> {
        cancelLoadingDialog();
        this.showSignupSucceedDialog();
      }, throwable -> {
        Timber.e(throwable);
        cancelLoadingDialog();
        AppUtils.showError(throwable, "Sign up user failed");
      });
    }
  }

  /**
   * show dialog when succeed
   */
  private void showSignupSucceedDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Sign up succeed");
    builder.setMessage(R.string.singup_dialog_content);
    builder.setCancelable(true);
    builder.setPositiveButton("OK", (dialog, id) -> {
      dialog.cancel();
      finish();
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
