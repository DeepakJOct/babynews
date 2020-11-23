package com.originprogrammers.babynews.Model.ApiManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NewsApiService {

    @GET("/trending/{langs}/{limit}/{skip}")
    Call<ResponseBody> getTrendingResponse(@Path("langs") String languages, @Path("limit") int limit, @Path("skip") int skipIntPage);

    @GET("/trending/{params}")
    Call<ResponseBody> getTrendingNewsResponse(@Path("params") String params);

}
