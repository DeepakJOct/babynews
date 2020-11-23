package com.originprogrammers.babynews.Model.ApiManager;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static NewsApiService apiService;

    public static final String URL = "https://news67.p.rapidapi.com";
    public static final String BASE_URL = "https://news67.p.rapidapi.com";
    public static final String BASE_URL1 = "?limit=25&skip=1&langs=en";




    public RetrofitClient() {
    }

    public static NewsApiService create() {
        if(apiService == null) {
            apiService = getClient(URL).create(NewsApiService.class);
        }
        return apiService;
    }

    public static NewsApiService createWithFinalUrl(String pointer) {
        if(apiService == null) {
            apiService = getClient(BASE_URL).create(NewsApiService.class);
        }
        return apiService;
    }



    public static Retrofit getClient(final String url) {
        if(retrofit == null) {
            //Use loggin interceptor to log all the requests to logcat
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY).setLevel(HttpLoggingInterceptor.Level.HEADERS);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    builder.addHeader("x-rapidapi-key", "05d46f7352mshdcc6c4598e11c2bp13096cjsn02b00df99525");
                    builder.addHeader("x-rapidapi-host", "news67.p.rapidapi.com");
                    builder.method(original.method(), original.body());
                    Request request = builder.build();
                    return chain.proceed(request);
                }
            }).readTimeout(240, TimeUnit.SECONDS)
                    .connectTimeout(240, TimeUnit.SECONDS)
                    .addInterceptor(interceptor)
                    .build();
            Log.d("RetrofitClient-->URL-->", url);

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static RequestBody getRequestBody(Map request) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(request).toString()));
    }


}
