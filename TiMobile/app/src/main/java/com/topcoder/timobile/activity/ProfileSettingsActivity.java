package com.topcoder.timobile.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
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
import com.topcoder.timobile.model.UserPreference;
import com.topcoder.timobile.model.event.UserUpdateEvent;
import com.topcoder.timobile.utility.AppConstants;
import com.topcoder.timobile.utility.AppUtils;
import com.topcoder.timobile.utility.ImagePicker;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

public class ProfileSettingsActivity extends BaseActivity {
  private static final int REQUEST_CODE_CHOOSE = 1111;
  public static final int REQUEST_TAKE_PHOTO = 1112;


  @BindView(R.id.imgClose) ImageView imgClose;
  @BindView(R.id.tvTitle) TextView tvTitle;
  @BindView(R.id.profileImage) CircleImageView profileImage;
  @BindView(R.id.tvChangePhoto) TextView tvChangePhoto;
  @BindView(R.id.tvEdit) TextView tvEdit;
  @BindView(R.id.etSecondName) EditText etSecondName;
  @BindView(R.id.etFirstName) EditText etFirstName;
  @BindView(R.id.etEmail) EditText etEmail;
  @BindView(R.id.etPasword) EditText etPasword;
  @BindView(R.id.rlPassword) RelativeLayout rlPassword;

  @BindView(R.id.llOptionRoot1) LinearLayout llOptionRoot1;
  @BindView(R.id.tvOption1) TextView tvOption1;
  @BindView(R.id.tgOption1) SwitchCompat tgOption1;

  @BindView(R.id.llOptionRoot2) LinearLayout llOptionRoot2;
  @BindView(R.id.tvOption2) TextView tvOption2;
  @BindView(R.id.tgOption2) SwitchCompat tgOption2;

  private boolean isEdit;
  private String TAG = ProfileSettingsActivity.class.getName();
  private List<UserPreference> userPreferences;
  private TransferUtility transferUtility;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile_settings);
    ButterKnife.bind(this);
    transferUtility = AppUtils.getTransferUtility(this);
    tvTitle.setText(R.string.profile_settings);
    imgClose.setImageResource(R.drawable.ic_back);
    tvEdit.setVisibility(View.VISIBLE);
    User user = apiService.getCurrentUser();
    initValue(user);
    apiService.getCurrentUserPreference().subscribe(this::userPreferenceFetched, this::onError);
  }

  /**
   * userPreferences fetched
   *
   * @param userPreferences the userPreferences
   */
  private void userPreferenceFetched(List<UserPreference> userPreferences) {
    this.userPreferences = userPreferences;
    List<LinearLayout> roots = Arrays.asList(llOptionRoot1, llOptionRoot2);
    List<TextView> labels = Arrays.asList(tvOption1, tvOption2);
    List<SwitchCompat> toggles = Arrays.asList(tgOption1, tgOption2);
    for (int i = userPreferences.size(); i < 2; i++) {
      roots.get(i).setVisibility(View.GONE);
    }
    for (int i = 0; i < userPreferences.size() && i < 2; i++) {
      UserPreference userPreference = userPreferences.get(i);
      labels.get(i).setText(userPreference.getPreferenceOption().getValue());
      if (userPreference.getSelected() != null) {
        toggles.get(i).setChecked(userPreference.getSelected());
      } else {
        toggles.get(i).setChecked(userPreference.getPreferenceOption().getDefaultValue());
      }

      toggles.get(i).setOnCheckedChangeListener((compoundButton, b) -> {
        Log.d(TAG, "userPreferenceFetched: " + b);
        userPreference.setSelected(b);
        apiService.updateUserPreference(userPreference.getId(), userPreference)
            .subscribe(p -> {
              Log.d(TAG, "userPreferenceFetched: update succeed");
            }, this::onError);
      });
    }
  }


  /**
   * http request send error
   *
   * @param throwable the exception
   */
  private void onError(Throwable throwable) {
    Timber.e(throwable);
    AppUtils.showError(throwable, getString(R.string.error));
  }


  public void initValue(User user) {

    etFirstName.setText(user.getFirstName());
    etSecondName.setText(user.getLastName());
    etEmail.setText(user.getEmail());

    etPasword.setText("password");
    if (user.getProfilePhotoURL() != null) {
      GlideApp.with(this).load(user.getProfilePhotoURL()).into(profileImage);
    }
  }

  @OnClick(R.id.tvEdit) void onEdit() {
    boolean localIsEdit = !isEdit;
    etFirstName.setEnabled(localIsEdit);
    etSecondName.setEnabled(localIsEdit);
    etEmail.setEnabled(localIsEdit);
    etPasword.setEnabled(false); // password cannot edit directly
    tgOption1.setEnabled(localIsEdit);
    tgOption2.setEnabled(localIsEdit);
    if (localIsEdit) {
      tvEdit.setText(R.string.save);
      tvChangePhoto.setVisibility(View.VISIBLE);
      isEdit = !isEdit;
    } else {
      User oldUser = apiService.getCurrentUser();
      User newUser = new User();
      String firstName = etFirstName.getText().toString();
      String lastName = etSecondName.getText().toString();
      String email = etEmail.getText().toString();

      if (!RegexUtils.isEmail(email)) {
        ToastUtils.showShort("email value is invalid");
        return;
      }
      if (StringUtils.isTrimEmpty(firstName)) {
        ToastUtils.showShort("first name cannot be empty");
        return;
      }
      if (StringUtils.isTrimEmpty(lastName)) {
        ToastUtils.showShort("last name cannot be empty");
        return;
      }

      boolean noNeedUpdate = oldUser.getFirstName().equals(firstName)
          && oldUser.getLastName().equals(lastName)
          && oldUser.getEmail().equals(email);
      if (noNeedUpdate) {
        updateUserSucceed(oldUser);
      } else {
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        updateUser(oldUser.getId(), newUser);
      }
    }
  }

  /**
   * update user with new values
   *
   * @param id      the user id
   * @param newUser the new User
   */
  private void updateUser(Long id, User newUser) {
    showLoadingDialog();
    apiService.updateUser(id, newUser).subscribe(u -> {
      cancelLoadingDialog();
      ToastUtils.showShort("User profile update succeed");
      this.updateUserSucceed(u);
    }, throwable -> {
      cancelLoadingDialog();
      this.onError(throwable);
    });
  }

  private void updateUserSucceed(User user) {
    Log.d(TAG, "updateUserSucceed: " + user);
    isEdit = !isEdit;
    tvEdit.setText(R.string.edit);
    tvChangePhoto.setVisibility(View.INVISIBLE);
    initValue(user);
    EventBus.getDefault().post(new UserUpdateEvent());
  }

  @OnClick(R.id.rlPassword) void onUpdatePassword() {
    ActivityUtils.startActivity(UpdatePasswordActivity.class);
  }

  @OnClick(R.id.tvChangePhoto) void onPhoto() {
    showDialog();
  }

  @OnClick(R.id.imgClose) void onClose() {
    finish();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }


  /**
   * open gallery for image pick
   */
  private void openGallery() {
    Intent chooseImageIntent = ImagePicker.getPickImageIntent(this, false);
    startActivityForResult(chooseImageIntent, REQUEST_CODE_CHOOSE);
  }

  private String getProfileImageName() {
    return "profile/" + apiService.getCurrentUser().getId() + "-" + System.currentTimeMillis() + ".png";
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case REQUEST_CODE_CHOOSE:
        if (data != null && data.getData() != null) {
          Timber.d("here uri " + data.getData());
          String fileName = getProfileImageName();
          try {
            File file = createFileFromUri(data.getData(), "ti-mobile" + System.currentTimeMillis());
            upload(file, fileName);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        break;
      case REQUEST_TAKE_PHOTO:
        if (data != null && data.getSerializableExtra("profileFile") != null) {
          File file = (File) data.getSerializableExtra("profileFile");
          Log.d(TAG, "onActivityResult: " + file);
          upload(file, getProfileImageName());
        }
        break;
      default:
        super.onActivityResult(requestCode, resultCode, data);
        break;
    }
  }

  /**
   * create file from uri
   *
   * @param uri       the file uri
   * @param objectKey the file name
   * @return
   * @throws IOException
   */
  private File createFileFromUri(Uri uri, String objectKey) throws Exception {
    InputStream is = getContentResolver().openInputStream(uri);
    File file = new File(getCacheDir(), objectKey);
    if (!file.createNewFile()) {
      throw new IllegalArgumentException("cannot create new file");
    }
    FileOutputStream fos = new FileOutputStream(file);
    byte[] buf = new byte[2046];
    int read;
    while ((read = is.read(buf)) != -1) {
      fos.write(buf, 0, read);
    }
    fos.flush();
    fos.close();
    return file;
  }


  /**
   * upload files to aws s3
   *
   * @param file      the file
   * @param objectKey the object key
   */
  private void upload(File file, final String objectKey) {
    Log.d(TAG, "upload: start upload");
    TransferObserver transferObserver = transferUtility.upload(
        AppConstants.BUCKET_NAME,
        objectKey,
        file,
        new ObjectMetadata(),
        CannedAccessControlList.PublicRead
    );
    showLoadingDialog();
    transferObserver.setTransferListener(new TransferListener() {
      @Override
      public void onStateChanged(int id, TransferState state) {
        if (TransferState.COMPLETED.equals(state)) {
          cancelLoadingDialog();
          Log.d(TAG, "onStateChanged: upload COMPLETED");
          String url = String.format("https://s3.%s.amazonaws.com/%s/%s",
              AppConstants.AWS_S3_REGION, AppConstants.BUCKET_NAME, objectKey);
          User newUser = new User();
          newUser.setProfilePhotoURL(url);

          // send to backend
          updateUser(apiService.getCurrentUser().getId(), newUser);
        }
      }

      @Override
      public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
      }

      @Override
      public void onError(int id, Exception ex) {
        ex.printStackTrace();
        cancelLoadingDialog();
        AppUtils.showError(ex, "upload profile image failed.");
      }
    });

  }


  /**
   * permission check for external storage
   */
  private void permissionCheck() {
    Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
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
      startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    });

    gallery.setOnClickListener(v -> {
      // Close dialog
      dialog.dismiss();
      permissionCheck();
    });
  }

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    AppUtils.hideKeyBoardWhenClickOther(ev, this);
    return super.dispatchTouchEvent(ev);
  }
}
