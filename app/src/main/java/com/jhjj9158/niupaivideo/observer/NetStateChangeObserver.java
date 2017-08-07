package com.jhjj9158.niupaivideo.observer;


import com.jhjj9158.niupaivideo.bean.NetworkType;

/**
 * Created by oneki on 2017/5/17.
 */

public interface NetStateChangeObserver {

    void onNetDisconnected();

    void onNetConnected(NetworkType networkType);
}
