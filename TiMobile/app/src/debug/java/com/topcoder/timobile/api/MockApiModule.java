package com.topcoder.timobile.api;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class MockApiModule {

    @Provides
    @Singleton
    ApiService providesApiService() {
        return new MockApiService();
    }
}
