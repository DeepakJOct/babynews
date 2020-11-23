package com.originprogrammers.babynews.Model.ApiManager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.originprogrammers.babynews.Model.Listeners.ResultListener;
import com.originprogrammers.babynews.Model.Listeners.RetrofitListener;
import com.originprogrammers.babynews.Model.Listeners.RetrofitServiceListener;
import com.originprogrammers.babynews.Model.models.NewsDetails;
import com.originprogrammers.babynews.utils.CommonUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ApiCallManager {

    public static final ApiCallManager ourInstance = new ApiCallManager();

    public static ApiCallManager newInstance() {
        return ourInstance;
    }

    public ApiCallManager() {
    }

    public void getNewsTrendingResponse(final Context context, String params, final ResultListener resultListener) {
        if(CommonUtils.checkInternetConnection(context)) {
            Call<ResponseBody> responseBodyCall = RetrofitClient.create().getTrendingNewsResponse(params);
            responseBodyCall.enqueue(new RetrofitListener(new RetrofitServiceListener() {
                @Override
                public void onSuccess(String result, int pos, Throwable t) {
                    if(pos == 0) {
                        Log.d("Response-->", result + "");
                        parseNewsTrendingResponse(context, result, resultListener);
                    } else {
                        CommonUtils.showToast(context, "Something went wrong!!");
                    }


                }
            }, "null", true, context));
        }
    }

    private void parseNewsTrendingResponse(Context context, String result, ResultListener resultListener) {
//        JSONArray resultArray = new JSONArray(result);
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<List<NewsDetails>>() {}.getType();
        List<NewsDetails> newsDetailsList = gson.fromJson(result, type);
        resultListener.getResult(newsDetailsList, true);
    }
}
