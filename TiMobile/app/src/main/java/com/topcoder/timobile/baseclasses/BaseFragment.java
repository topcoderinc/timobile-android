/*
 * Copyright (c) 2016.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.topcoder.timobile.baseclasses;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.topcoder.timobile.activity.HomeActivity;
import com.topcoder.timobile.api.ApiService;
import com.topcoder.timobile.dialogs.LoadingDialog;
import javax.inject.Inject;

/**
 * @author Harsh
 * @version 1.0
 */

public abstract class BaseFragment extends Fragment {
  @Inject protected ApiService apiService;

  private LoadingDialog loadingDialog;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BaseApplication.getInstance().getApiComponent().inject(this);
  }

  public void setTitle(@StringRes int title) {
    getActivity().setTitle(title);
  }

  /**
   * switch fragment
   * @param fragment fragment
   * @param addToBackStack addToBackStack
   */
  public void switchFragment(Fragment fragment, boolean addToBackStack) {
    ((HomeActivity) getActivity()).switchFragment(fragment, addToBackStack);
  }

  protected void cancelLoadingDialog() {
    if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
      loadingDialog.getDialog().dismiss();
    }
  }

  /**
   * show loading dialog
   * @param stringResId text
   */
  protected void showLoadingDialog(int stringResId) {
    loadingDialog = LoadingDialog.newInstance();
    loadingDialog.show(getActivity().getSupportFragmentManager(), null);
    loadingDialog.setCancelListener(view -> cancelLoadingDialog());
  }

  /**
   * custom tab layout font
   * @param tabLayout tab layout
   */
  protected void changeTabsFont(TabLayout tabLayout) {

    ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
    int tabsCount = vg.getChildCount();
    Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-SemiBold.ttf");
    for (int j = 0; j < tabsCount; j++) {
      ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
      int tabChildsCount = vgTab.getChildCount();
      for (int i = 0; i < tabChildsCount; i++) {
        View tabViewChild = vgTab.getChildAt(i);
        if (tabViewChild instanceof TextView) {
          ((TextView) tabViewChild).setTypeface(custom_font);
          ((TextView) tabViewChild).setAllCaps(false);
        }
      }
    }
  }
}
