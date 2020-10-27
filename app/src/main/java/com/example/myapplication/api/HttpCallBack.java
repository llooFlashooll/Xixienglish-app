package com.example.myapplication.api;

public interface HttpCallBack {
    void onSuccess(String res);

    void onFailure(Exception e);
}
