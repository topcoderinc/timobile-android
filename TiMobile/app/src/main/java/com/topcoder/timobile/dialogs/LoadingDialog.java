package com.topcoder.timobile.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.timobileapp.R;
import lombok.Getter;
import lombok.Setter;

/**
 * loading custom dialog
 */
public class LoadingDialog extends DialogFragment {

  @Getter @Setter private View.OnClickListener cancelListener;

  public static LoadingDialog newInstance() {
    return new LoadingDialog();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.progress, container, true);

    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    if (getDialog().getWindow() != null) {
      WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      getDialog().getWindow().setAttributes(params);
    }

    setCancelable(false);

    return view;
  }
}
