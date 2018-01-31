package com.topcoder.timobile.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.timobileapp.BuildConfig;
import dagger.Module;
import dagger.Provides;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Dagger module for providing {@link ApiService}. For the purpose of this prototype, it
 * provides a {@link ApiServiceImpl}, which can be easily replaced in the future.
 */
@Module public class ApiModule {

  @Provides @Singleton ApiService providesApiService() {
    return new ApiServiceImpl(getRealApi());
  }

  private ApiService getRealApi() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(30, TimeUnit.SECONDS);
    builder.readTimeout(30, TimeUnit.SECONDS);
    builder.writeTimeout(30, TimeUnit.SECONDS);

    // Add headers
    builder.interceptors().add(new TokenInterceptor());

    // Logging
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    builder.interceptors().add(interceptor);


    Gson gson = new GsonBuilder().create();

    RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io());

    Retrofit retrofit = new Retrofit.Builder().baseUrl(BuildConfig.API_URL)
        .client(builder.build())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(rxAdapter)
        .build();

    return retrofit.create(ApiService.class);
  }
}
