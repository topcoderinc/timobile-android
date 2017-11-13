package com.topcoder.timobile.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.timobileapp.R;
import com.topcoder.timobile.baseclasses.BaseFragment;
import java.util.List;

/**
 * Author: Harshvardhan
 * Date: 27/10/17
 */

public class AreaFragment extends BaseFragment {

  Unbinder unbinder;

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_area, container, false);
    unbinder = ButterKnife.bind(this, view);
    return view;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    permissionCheck();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
  }

  /**
   * locaiton permission check
   */
  private void permissionCheck() {
    Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
      @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
        if (report.areAllPermissionsGranted()) {
          //code for access current location
        } else {
          SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(getActivity().findViewById(android.R.id.content), R.string.all_permissions_denied_feedback)
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
        new AlertDialog.Builder(getActivity()).setTitle(R.string.permission_rationale_title)
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
}
