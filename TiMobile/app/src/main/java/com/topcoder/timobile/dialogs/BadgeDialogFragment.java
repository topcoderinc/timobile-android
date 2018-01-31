package com.topcoder.timobile.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.google.gson.Gson;
import com.timobileapp.R;
import com.topcoder.timobile.glide.GlideApp;
import com.topcoder.timobile.model.Badge;
import com.topcoder.timobile.utility.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

/**
 * show simple badge dialog
 */
public class BadgeDialogFragment extends DialogFragment {

  @BindView(R.id.imgBadge) ImageView imgBadge;
  @BindView(R.id.tvBadgeTitle) TextView tvBadgeTitle;
  @BindView(R.id.tvBadgeDescription) TextView tvBadgeDescription;

  Unbinder unbinder;
  private Badge badge;
  private static Gson gson = new Gson();


  public static BadgeDialogFragment newInstance(Badge badge) {
    BadgeDialogFragment dialog = new BadgeDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putString(AppConstants.KEY_OBJ, gson.toJson(badge));
    dialog.setArguments(bundle);
    return dialog;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    badge = gson.fromJson(getArguments().getString(AppConstants.KEY_OBJ), Badge.class);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_badge, container, true);
    unbinder = ButterKnife.bind(this, view);

    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    if (getDialog().getWindow() != null) {
      WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes(params);
    }
    setCancelable(true);

    tvBadgeTitle.setText(badge.getName());
    tvBadgeDescription.setText(badge.getDescription());
    GlideApp.with(ActivityUtils.getTopActivity())
        .load(badge.getImageUrl()).placeholder(R.drawable.ic_active_badge).into(imgBadge);
    return view;
  }

  @Override public void onDestroy() {
    unbinder.unbind();
    super.onDestroy();
  }
}