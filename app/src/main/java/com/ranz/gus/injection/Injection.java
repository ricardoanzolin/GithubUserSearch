package com.ranz.gus.injection;

import com.ranz.gus.data.UserRepository;
import com.ranz.gus.data.UserRepositoryImpl;
import com.ranz.gus.data.remote.GithubUserRestService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ricardo on 28/08/2016.
 */
public class Injection {

    private static final String BASE_URL = "https://api.github.com";
    private static OkHttpClient okHttpClient;
    private static GithubUserRestService userRestService;
    private static Retrofit retrofitInstance;


    public static UserRepository provideUserRepo() {
        return new UserRepositoryImpl(provideGithubUserRestService());
    }

    static GithubUserRestService provideGithubUserRestService() {
        if (userRestService == null) {
            userRestService = getRetrofitInstance().create(GithubUserRestService.class);
        }
        return userRestService;
    }

    static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            okHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();
        }

        return okHttpClient;
    }

    static Retrofit getRetrofitInstance() {
        if (retrofitInstance == null) {
            Retrofit.Builder retrofit = new Retrofit.Builder().client(Injection.getOkHttpClient()).baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
            retrofitInstance = retrofit.build();

        }
        return retrofitInstance;
    }

}
