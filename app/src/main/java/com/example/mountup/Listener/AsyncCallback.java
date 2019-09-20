package com.example.mountup.Listener;

public interface AsyncCallback<T> {
    void onSuccess(T object);
    void onFailure(Exception e);
}
