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

import android.app.Application;
import com.blankj.utilcode.util.Utils;
import com.timobileapp.BuildConfig;
import com.timobileapp.R;
import lombok.Getter;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author Harsh
 * @version 1.0
 */

public class BaseApplication extends Application {

  private static BaseApplication instance;

  @Getter protected ApiComponent apiComponent;

  public static BaseApplication getInstance() {
    return instance;
  }

  @Override public void onCreate() {
    super.onCreate();
    instance = this;
    //utils init
    Utils.init(this);
    if (BuildConfig.DEBUG) {
      //for log
      Timber.plant(new Timber.DebugTree());
    }

    //init custom font
    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
            .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
            .setFontAttrId(R.attr.fontPath)
            .build());

    apiComponent = DaggerApiComponent.create();
  }
}
