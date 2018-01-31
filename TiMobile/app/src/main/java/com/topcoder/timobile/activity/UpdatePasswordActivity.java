package com.topcoder.timobile.activity;


import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.request.UpdatePassword;
import com.topcoder.timobile.utility.AppUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Update user password activity
 */

public class UpdatePasswordActivity extends BaseActivity {

  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.etOldPassword) EditText etOldPassword;
  @BindView(R.id.etNewPassword) EditText etNewPassword;
  @BindView(R.id.etConfirmPassword) EditText etConfirmPassword;

  private String TAG = UpdatePasswordActivity.class.getName();


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_update_password);
    ButterKnife.bind(this);

    tvTitle.setText(R.string.change_passwod_title);
    imgClose.setImageResource(R.drawable.ic_back);
  }


  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  @OnClick(R.id.btnUpdatePassword) void onUpdatePassword() {
    String oldPassword = etOldPassword.getText().toString();
    String newPassword = etNewPassword.getText().toString();
    String confirmPassword = etConfirmPassword.getText().toString();

    if (StringUtils.isTrimEmpty(oldPassword)) {
      ToastUtils.showShort("old password cannot be empty");
      return;
    }
    if (StringUtils.isTrimEmpty(newPassword)) {
      ToastUtils.showShort("new password cannot be empty");
      return;
    }

    if (StringUtils.isTrimEmpty(confirmPassword)) {
      ToastUtils.showShort("confirm password cannot be empty");
      return;
    }
    if (!newPassword.equals(confirmPassword)) {
      ToastUtils.showShort("confirm password must be equal with new password");
    }

    UpdatePassword updatePassword = new UpdatePassword();
    updatePassword.setOldPassword(oldPassword);
    updatePassword.setNewPassword(newPassword);
    showLoadingDialog();
    apiService.updatePassword(updatePassword).subscribe(voidResponse -> {
      finish();
      ToastUtils.showShort("password update succeed");
      cancelLoadingDialog();
    }, throwable -> {
      cancelLoadingDialog();
      Timber.e(throwable);
      AppUtils.showError(throwable, "update password failed");
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    AppUtils.hideKeyBoardWhenClickOther(ev, this);
    return super.dispatchTouchEvent(ev);
  }
}
