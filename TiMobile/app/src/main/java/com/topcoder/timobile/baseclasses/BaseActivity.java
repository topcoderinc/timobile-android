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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.topcoder.timobile.api.ApiService;
import com.topcoder.timobile.dialogs.LoadingDialog;
import javax.inject.Inject;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * @author Harsh
 * @version 1.0
 */

/**
 * Helper activity that provides API interaction
 */
public abstract class BaseActivity extends AppCompatActivity {

  @Inject public ApiService apiService;
  private LoadingDialog loadingDialog;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BaseApplication.getInstance().getApiComponent().inject(this);
  }

  protected void cancelLoadingDialog() {
    if (loadingDialog != null && loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
      loadingDialog.getDialog().dismiss();
    }
  }

  protected void showLoadingDialog() {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    loadingDialog = LoadingDialog.newInstance();
    ft.add(loadingDialog, null);
    ft.commitAllowingStateLoss();
    loadingDialog.setCancelListener(view -> cancelLoadingDialog());
  }

  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }
}
