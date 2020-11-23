package com.originprogrammers.babynews.Controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import com.originprogrammers.babynews.Model.ApiManager.ApiCallManager;
import com.originprogrammers.babynews.Model.Listeners.ResultListener;
import com.originprogrammers.babynews.Model.models.NewsDetails;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class DataRepository {

    public DataRepository() {
    }

    //getnews
    public static void getNewsList(Context context, final ResultListener listener) {
        String limit = "25";
        String skipPageNum = "1";
        String langs = "en";

        StringBuilder s = new StringBuilder();
        s.append("&limit=" + limit);
        s.append("&langs=" + langs);
        s.append("&skip=" + skipPageNum);
        String params = s.toString();

        ApiCallManager.newInstance().getNewsTrendingResponse(context, params, new ResultListener() {
            @Override
            public void getResult(Object object, boolean isSuccess) {
                if(isSuccess) {
                    listener.getResult(object, true);
                }
            }
        });
    }

    public static void getNewsList(final String params, final ResultListener listener) {
            //Actual Url = https://news67.p.rapidapi.com/trending?limit=25&skip=1&langs=en
            //params url must come as : trending?limit=25&skip=1&langs=en
            new AsyncTask<Void, Void, List<NewsDetails>>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected List<NewsDetails> doInBackground(Void... voids) {

                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("https://news67.p.rapidapi.com/" + params)
                                .get()
                                .addHeader("x-rapidapi-key", "05d46f7352mshdcc6c4598e11c2bp13096cjsn02b00df99525")
                                .addHeader("x-rapidapi-host", "news67.p.rapidapi.com")
                                .build();
                        ResponseBody response = client.newCall(request).execute().body();
                        if(response != null) {
                            JSONArray res = new JSONArray(response.string());
                            Gson gson = new GsonBuilder().create();
                            Type t = new TypeToken<List<NewsDetails>>() {}.getType();
                            List<NewsDetails> details = gson.fromJson(res.toString(), t);
                            Log.d("JsonNews::", "listSize=" + details.size());
                            return details;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(List<NewsDetails> newsDetails) {
                    super.onPostExecute(newsDetails);
                    listener.getResult(newsDetails, true);
                }
            }.execute();


    }
}
