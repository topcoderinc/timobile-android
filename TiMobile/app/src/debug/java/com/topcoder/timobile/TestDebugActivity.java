package com.topcoder.timobile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.ExpandableListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.timobileapp.R;
import com.topcoder.timobile.adapter.FAQAdapter;
import com.topcoder.timobile.baseclasses.BaseActivity;
import com.topcoder.timobile.model.FAQSectionModel;
import com.topcoder.timobile.utility.ImagePicker;
import java.util.List;
import timber.log.Timber;

/**
 * Author: Harshvardhan
 * Date: 31/10/17
 */

public class TestDebugActivity extends BaseActivity {

  @BindView(R.id.expandListView) ExpandableListView mExpandableList;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_faq);
    ButterKnife.bind(this);
    apiService.getFAQs().subscribe(this::onSuccess, this::onError);
    mExpandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
      // Keep track of previous expanded parent
      int previousGroup = -1;

      @Override
      public void onGroupExpand(int groupPosition) {
        // Collapse previous parent if expanded.
        if ((previousGroup != -1) && (groupPosition != previousGroup)) {
          mExpandableList.collapseGroup(previousGroup);
        }
        previousGroup = groupPosition;
      }
    });
    permissionCheck();
  }

  /**
   * permission check for external storage and audio check
   */
  private void permissionCheck() {
    Dexter.withActivity(this)
        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
        .withListener(new MultiplePermissionsListener() {
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
            new AlertDialog.Builder(TestDebugActivity.this).setTitle(R.string.permission_rationale_title)
                .setMessage(R.string.permission_rationale_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    token.cancelPermissionRequest();
                  }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    token.continuePermissionRequest();
                  }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                  @Override public void onDismiss(DialogInterface dialog) {
                    token.cancelPermissionRequest();
                  }
                })
                .show();
          }
        })
        .check();
  }

  private void onError(Throwable throwable) {
    Timber.d(throwable);
  }

  private void onSuccess(List<FAQSectionModel> faqSectionModels) {
    Timber.d("here sections: " + faqSectionModels.size() + " child: " + faqSectionModels.get(0).getModelList().size());
    mExpandableList.setAdapter(new FAQAdapter(this, faqSectionModels));
  }

  private void openGallery() {

    Intent chooseImageIntent = ImagePicker.getPickImageIntent(this, false);
    startActivityForResult(chooseImageIntent, 111);
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(Activity.RESULT_OK ==resultCode){
      switch (requestCode) {
        case 111:
          Timber.d("here uri " + data.getData());
          break;
        default:
          super.onActivityResult(requestCode, resultCode, data);
          break;
      }
    }
  }
}
