package com.topcoder.timobile.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.blankj.utilcode.util.ActivityUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.User;
import com.topcoder.timobile.model.event.PhotoEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.ImagePicker;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class ProfileSettingsActivity extends BaseActivity {
  private static final int REQUEST_CODE_CHOOSE = 1111;
  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.profileImage) CircleImageView profileImage;
  @BindView(R.id.tvChangePhoto) TextView tvChangePhoto;
  @BindView(R.id.tvEdit) TextView tvEdit;
  @BindView(R.id.etName) EditText etName;
  @BindView(R.id.etEmail) EditText etEmail;
  @BindView(R.id.etPasword) EditText etPasword;
  @BindView(R.id.switchThirdParty) SwitchCompat switchThirdParty;
  @BindView(R.id.switchFreeAdmission) SwitchCompat switchFreeAdmission;

  private boolean isEdit;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_settings);
    ButterKnife.bind(this);
    EventBus.getDefault().register(this);
    tvTitle.setText(R.string.profile_settings);
    imgClose.setImageResource(R.drawable.ic_back);
    tvEdit.setVisibility(View.VISIBLE);
    User user = getIntent().getParcelableExtra(AppConstants.KEY_OBJ);
    etName.setText(user.getName());
    etEmail.setText(user.getEmail());
    etPasword.setText(user.getPassword());
    GlideApp.with(this).load(user.getProfileImage()).into(profileImage);
  }

  @OnClick(R.id.tvEdit) void onEdit() {
    isEdit = !isEdit;
    etName.setEnabled(isEdit);
    etEmail.setEnabled(isEdit);
    etPasword.setEnabled(isEdit);
    switchFreeAdmission.setEnabled(isEdit);
    switchThirdParty.setEnabled(isEdit);
    if (isEdit) {
      tvEdit.setText(R.string.save);
      tvChangePhoto.setVisibility(View.VISIBLE);
    } else {
      tvEdit.setText(R.string.edit);
      tvChangePhoto.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick(R.id.tvChangePhoto) void onPhoto() {
    showDialog();
  }

  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);
  }

  @Subscribe public void onEvent(PhotoEvent event) {
    GlideApp.with(this).load(event.getPath()).into(profileImage);
  }

  /**
   * open gallery for image pick
   */
  private void openGallery() {
    Intent chooseImageIntent = ImagePicker.getPickImageIntent(this, false);
    startActivityForResult(chooseImageIntent, REQUEST_CODE_CHOOSE);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_CODE_CHOOSE:
        Timber.d("here uri " + data.getData());
        GlideApp.with(this).load(data.getData()).into(profileImage);
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }

  /**
   * permission check for external storage
   */
  private void permissionCheck() {
    Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
      @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.areAllPermissionsGranted()) {
          openGallery();
        } else {
          SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(findViewById(android.R.id.content), R.string.all_permissions_denied_feedback)
              .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
              .withCallback(new Snackbar.Callback() {
                @Override public void onShown(Snackbar snackbar) {
                  super.onShown(snackbar);
                }

                @Override public void onDismissed(Snackbar snackbar, int event) {
                  super.onDismissed(snackbar, event);
                }
              })
              .build();
        }
      }

      @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, final PermissionToken token) {
        new AlertDialog.Builder(ProfileSettingsActivity.this).setTitle(R.string.permission_rationale_title)
            .setMessage(R.string.permission_rationale_message)
            .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
              dialog.dismiss();
              token.cancelPermissionRequest();
            })
            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
              dialog.dismiss();
              token.continuePermissionRequest();
            })
            .setOnDismissListener(dialog -> token.cancelPermissionRequest())
            .show();
      }
    }).check();
  }

  /**
   * show chooser dialog
   */
  private void showDialog() {
    // Create custom dialog object
    final Dialog dialog = new Dialog(this);
    // Include dialog.xml file
    dialog.setContentView(R.layout.dialog_image_picker);

    // set values for custom dialog components - text, image and button
    TextView camera = (TextView) dialog.findViewById(R.id.tvCamera);
    TextView gallery = (TextView) dialog.findViewById(R.id.tvGallery);

    dialog.show();

    camera.setOnClickListener(v -> {
      // Close dialog
      dialog.dismiss();
      Intent intent = new Intent(ProfileSettingsActivity.this, CameraActivity.class);
      intent.putExtra(AppConstants.KEY_BOOl, true);
      ActivityUtils.startActivity(intent);
    });

    gallery.setOnClickListener(v -> {
      // Close dialog
      dialog.dismiss();
      permissionCheck();
    });
  }
}
