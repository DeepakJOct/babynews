package com.originprogrammers.babynews.Model.Listeners;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitListener implements Callback<ResponseBody> {

    private RetrofitServiceListener listener;
    private Context context;
    private ProgressDialog dialog;

    public RetrofitListener(RetrofitServiceListener listener,
                            final String title,
                            boolean showProgress,
                            final Context activity) {
        this.listener = listener;
        this.context = activity;
        if(showProgress) {
            ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(activity);
                    if(title != null && title.length() > 0) {
                        dialog.setTitle(title);
                    } else {
                        dialog.setMessage("Loading...");
                    }
                    dialog.show();
                }
            });

        }

    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        try {
            if(dialog != null)
                dialog.hide();
            if(!response.isSuccessful()) {
                Log.d("Response-->", "fail--> " + response.message());
                Log.d("Response-->", "responsecode--> " + response.code() + "---" + response.message());
                if(response.code() == 503) {
                    if(context != null) {
                        //show maintenance screen or error screen
                    }
                } else {
                    listener.onSuccess(response.message(), 2, null);
                }
            } else {
                String res = response.body().toString();
                Log.d("Resposne-->", res);
                listener.onSuccess(res, 0, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Response-->", "fail--> " + e.toString());
            listener.onSuccess("", 2, null);
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        if(dialog != null) dialog.hide();
        listener.onSuccess("", 1, null);
        t.printStackTrace();
    }
}
