package com.originprogrammers.babynews.Model.Listeners;

public interface RetrofitServiceListener {
    void onSuccess(String result, int pos, Throwable t);
}
