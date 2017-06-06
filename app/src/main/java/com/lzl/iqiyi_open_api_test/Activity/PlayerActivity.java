package com.lzl.iqiyi_open_api_test.Activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lzl.iqiyi_open_api_test.R;
import com.qiyi.video.playcore.ErrorCode;
import com.qiyi.video.playcore.IQYPlayerHandlerCallBack;
import com.qiyi.video.playcore.QiyiVideoView;

import java.util.concurrent.TimeUnit;

/**
 * Created by LZL on 2017/6/5.
 */

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private PercentRelativeLayout mplayerBottomBar;
    private QiyiVideoView videoView;
    private TextView nowData;
    private SeekBar mSeekBar;
    private TextView mTotalTime;
    private TextView mCurrentTime;
    ImageView fullScreenButton;


    final int UPDATE_PROGRESS = 1;
    final int UPDATE_PROGRESS_TIME = 1000; //1s
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);
        init();
    }

    public void init()
    {
        mplayerBottomBar = (PercentRelativeLayout)findViewById(R.id.myPlayer_bottomBar);
        nowData = (TextView)findViewById(R.id.now_data);
        fullScreenButton = (ImageView)findViewById(R.id.myPlayer_fullScreenButton);
        mTotalTime = (TextView)findViewById(R.id.totlaTimeText);
        mCurrentTime = (TextView)findViewById(R.id.currentTimeText);
        mSeekBar = (SeekBar)findViewById(R.id.myPlayer_SeekBar);
        videoView = (QiyiVideoView)findViewById(R.id.myPlayer);
        setCallBackOnPlayer();
        videoView.setOnClickListener(this);
        fullScreenButton.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private  int mProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    mProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekBar.setProgress(mProgress);
                videoView.seekTo(mProgress);

            }
        });
        videoView.setPlayData("667737400");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(videoView!=null)
        {
            videoView.start();
        }
        mPlayerHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS,UPDATE_PROGRESS_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoView!=null)
        {
            videoView.pause();
        }
        mPlayerHandler.removeMessages(UPDATE_PROGRESS);
    }

    public void setCallBackOnPlayer()
    {
        videoView.setPlayerCallBack(mPlayerCallBack);
    }
    //播放器回调接口CallBack
    IQYPlayerHandlerCallBack mPlayerCallBack = new IQYPlayerHandlerCallBack() {
        @Override
        public void OnSeekSuccess(long l) {

        }

        @Override
        public void OnWaiting(boolean b) {

        }

        @Override
        public void OnError(ErrorCode errorCode) {

        }

        @Override
        public void OnPlayerStateChanged(int i) {

        }
    };

    private Handler mPlayerHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UPDATE_PROGRESS:
                {
                    int duration = videoView.getDuration();
                    int nowProgress = videoView.getCurrentPosition();
                    if(duration>0)
                    {
                        mSeekBar.setMax(duration);
                        mSeekBar.setProgress(nowProgress);
                        mTotalTime.setText(ms2hms(duration));
                        mCurrentTime.setText(ms2hms(nowProgress));
                    }
                    mPlayerHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS,UPDATE_PROGRESS_TIME);
                    break;
                }
            }
        }
    };

    public void changeToFullScreen(){
        if(videoView != null){
            if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoView.setVideoViewSize(screenWidth, screenHeight, true);
        }else{
            videoView.setVideoViewSize(screenWidth, (int) (screenWidth * 9.0 / 16));
        }
    }


    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.myPlayer_fullScreenButton:
            {
                changeToFullScreen();
                break;
            }
            case R.id.myPlayer:
            {
                if(mplayerBottomBar.getVisibility()==View.VISIBLE)
                    mplayerBottomBar.setVisibility(View.INVISIBLE);
                else
                    mplayerBottomBar.setVisibility(View.VISIBLE);

                StringBuilder builder = new StringBuilder();
                builder.append(videoView.isPlaying())
                        .append('\n')
                        .append(videoView.getDuration()+"\n")
                        .append(videoView.getCurrentPosition()+"\n");
                nowData.setText(builder.toString());
                break;
            }
            case R.id.myPlayer_bottomBar:
            {

                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.release();
        videoView = null;
        mPlayerHandler.removeCallbacksAndMessages(null);
    }

    private String ms2hms(int millis) {
        String result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return result;
    }
}
