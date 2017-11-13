package com.topcoder.timobile.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.timobileapp.R;
import lombok.Getter;
import lombok.Setter;


/**
 * Basic dialog with Holo theme
 */
public class ThemedInfoDialog extends DialogFragment {

  private static final String KEY_MESSAGE = "message";
  private static final String KEY_TITLE = "title";
  private static final String KEY_POSITIVE_BUTTON_TEXT = "positive_button_text";
  private static final String KEY_NEGATIVE_BUTTON_TEXT = "negative_button_text";
  private static final String KEY_SHOW_CANCEL_BUTTON = "show_cancel_button";

  @Getter @Setter public View.OnClickListener okListener;

  @BindView(R.id.tv_title) TextView titleTv;

  @BindView(R.id.tv_message) TextView messageTv;

  @BindView(R.id.btn_ok) TextView okButton;

  boolean button;

  public static ThemedInfoDialog newInstance(String title, String message, String positiveText) {
    Bundle bundle = createBundle(title, message, positiveText);
    ThemedInfoDialog dialog = new ThemedInfoDialog();
    dialog.setArguments(bundle);
    return dialog;
  }

  protected static Bundle createBundle(String title, String message, String positiveButtonText) {
    Bundle args = new Bundle();
    args.putString(KEY_MESSAGE, message);
    args.putString(KEY_TITLE, title);
    args.putString(KEY_POSITIVE_BUTTON_TEXT, positiveButtonText);
    return args;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Applying the theme
    setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    setCancelable(true);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_themed_dialog, container, false);
    ButterKnife.bind(this, rootView);
    getDialog().setCanceledOnTouchOutside(true);
    Bundle args = getArguments();
    messageTv.setText(args.getString(KEY_MESSAGE));
    titleTv.setText(args.getString(KEY_TITLE));
    if (TextUtils.isEmpty(titleTv.getText().toString())) {
      titleTv.setVisibility(View.GONE);
    }
    button = args.getBoolean(KEY_SHOW_CANCEL_BUTTON);

    if (TextUtils.isEmpty(args.getString(KEY_POSITIVE_BUTTON_TEXT))) {
      okButton.setText(getString(android.R.string.ok));
    } else {
      okButton.setText(args.getString(KEY_POSITIVE_BUTTON_TEXT));
    }

    return rootView;
  }

  @OnClick(R.id.btn_ok) void onClickOk(View view) {
    dismiss();
    if (okListener != null) {
      okListener.onClick(view);
    }
  }
}
