package com.topcoder.timobile.baseclasses;

import com.topcoder.timobile.api.ApiModule;
import dagger.Component;
import javax.inject.Singleton;

/**
 * Dagger Component for {@link com.topcoder.timobile.api.ApiService} injection. Any classes that need
 * access to ApiService should add an inject method declaration to this interface with themselves
 * as the parameter.
 */
@Singleton @Component(modules = { ApiModule.class }) public interface ApiComponent {

  void inject(BaseActivity baseActivity);

  void inject(BaseFragment baseFragment);
}
