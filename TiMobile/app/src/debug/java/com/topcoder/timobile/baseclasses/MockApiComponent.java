package com.topcoder.timobile.baseclasses;

import com.topcoder.timobile.api.MockApiModule;
import dagger.Component;
import javax.inject.Singleton;


@Singleton
@Component(modules = {MockApiModule.class})
interface MockApiComponent extends ApiComponent {
}
