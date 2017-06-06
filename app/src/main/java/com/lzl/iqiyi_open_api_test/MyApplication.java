package com.lzl.iqiyi_open_api_test;

import android.app.Application;

import com.qiyi.video.playcore.QiyiVideoView;

/**
 * Created by LZL on 2017/6/6.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        QiyiVideoView.init(this);
    }



}
