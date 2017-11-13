package com.topcoder.timobile.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import com.timobileapp.R;

/**
 * Author: Harshvardhan
 * Date: 02/11/17
 */

/**
 * simple trading card dialog
 */
public class TradingCardDialog extends DialogFragment {

  @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.dialog_trading_cards, container, true);

    getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

    if (getDialog().getWindow() != null) {
      WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
      params.height = WindowManager.LayoutParams.MATCH_PARENT;
      params.width = WindowManager.LayoutParams.MATCH_PARENT;
      getDialog().getWindow().setAttributes(params);
    }
    setCancelable(true);

    return view;
  }
}

