package com.lzl.iqiyi_open_api_test.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lzl.iqiyi_open_api_test.R;
import com.qiyi.video.playcore.QiyiVideoView;

/**
 * Created by LZL on 2017/6/5.
 */

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{
    PercentRelativeLayout mplayerBottomBar;
    QiyiVideoView videoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);
        init();
    }

    public void init()
    {
        mplayerBottomBar = (PercentRelativeLayout)findViewById(R.id.myPlayer_bottomBar);
        videoView = (QiyiVideoView)findViewById(R.id.myPlayer);

        videoView.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.myPlayer:
            {
                if(mplayerBottomBar.getVisibility()==View.VISIBLE)
                    mplayerBottomBar.setVisibility(View.INVISIBLE);
                else
                    mplayerBottomBar.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.myPlayer_bottomBar:
            {

                break;
            }
        }
    }
}
