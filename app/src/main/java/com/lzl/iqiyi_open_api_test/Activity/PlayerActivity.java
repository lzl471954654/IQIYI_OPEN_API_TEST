package com.lzl.iqiyi_open_api_test.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lzl.iqiyi_open_api_test.Adapter.GuessListAdapter;
import com.lzl.iqiyi_open_api_test.DataClass.VideoData;
import com.lzl.iqiyi_open_api_test.HttpRequest.DataRequest;
import com.lzl.iqiyi_open_api_test.HttpRequest.ParseDataFromHttp;
import com.lzl.iqiyi_open_api_test.OtherClass.SpaceItemDecration;
import com.lzl.iqiyi_open_api_test.R;
import com.qiyi.video.playcore.ErrorCode;
import com.qiyi.video.playcore.IQYPlayerHandlerCallBack;
import com.qiyi.video.playcore.QiyiVideoView;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

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
    private ImageView fullScreenButton;
    private ImageView mPlayeButton;
    private RecyclerView mGuessList;
    private TextView mPlayerVideoTitle;
    private TextView mPlayerVideoType;
    private TextView mPlayerVideoCount;
    private RelativeLayout mPlayerContent;
    private ScrollView scrollView;

    Bundle bundle = null;
    VideoData videoData;
    DataRequest dataRequest = DataRequest.newInstance();

    private List<VideoData> videoDataLIst;

    final int UPDATE_PROGRESS = 1;
    final int UPDATE_PROGRESS_TIME = 1000; //1s
    final int REFRESH_VIDEO_POSITION = 2;

    boolean fullScreenFlag = false;

    String playId = "667737400";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity_layout);
        bundle = savedInstanceState;
        Intent intent = getIntent();
        videoData =(VideoData) intent.getExtras().getSerializable("videoData");
        if(videoData==null)
            finish();
        else
            playId = videoData.getTvId();
        init(savedInstanceState);
    }

    public void init(Bundle savedInstanceState)
    {
        initViewAndSetLisenter();
        setCallBackOnPlayer();
        if(savedInstanceState==null)
            videoView.setPlayData(playId);
        else
        {
            Log.e("savedInstaceState","\tnot null");
            playId = savedInstanceState.getString("videoId");
            videoView.setPlayData(playId);
            fullScreenFlag = savedInstanceState.getBoolean("fullScreen",false);
        }
        fullScreenSettings();
        if(!fullScreenFlag)
            loadGuessList();
    }
    public void loadGuessList()
    {
        String keyWord = videoData.getShortTitle();
        dataRequest.searchVideoNormally(keyWord, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                videoDataLIst = ParseDataFromHttp.getSearchVideoList(s);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(videoDataLIst.size()!=0)
                        {
                            refreshGuessListData();
                        }
                    }
                });
            }
        });
    }

    public void refreshGuessListData()
    {
        GuessListAdapter adapter = new GuessListAdapter(videoDataLIst,this);
        mGuessList.setLayoutManager(new LinearLayoutManager(this));
        mGuessList.setAdapter(adapter);
        mGuessList.addItemDecoration(new SpaceItemDecration(5,0,0,0));
    }


    public void initViewAndSetLisenter()
    {
        scrollView = (ScrollView)findViewById(R.id.mplayer_recommendListLayout);
        mPlayerContent = (RelativeLayout)findViewById(R.id.mplayer_content);
        mPlayerVideoTitle = (TextView)findViewById(R.id.video_title);
        mPlayerVideoType = (TextView)findViewById(R.id.video_type_data);
        mPlayerVideoCount = (TextView)findViewById(R.id.play_count_data);
        mGuessList = (RecyclerView)findViewById(R.id.player_guess_list);
        mplayerBottomBar = (PercentRelativeLayout)findViewById(R.id.myPlayer_bottomBar);
        mPlayeButton = (ImageView)findViewById(R.id.myPlayer_play_button);
        //nowData = (TextView)findViewById(R.id.now_data);
        fullScreenButton = (ImageView)findViewById(R.id.myPlayer_fullScreenButton);
        mTotalTime = (TextView)findViewById(R.id.totlaTimeText);
        mCurrentTime = (TextView)findViewById(R.id.currentTimeText);
        mSeekBar = (SeekBar)findViewById(R.id.myPlayer_SeekBar);
        videoView = (QiyiVideoView)findViewById(R.id.myPlayer);
        videoView.setOnClickListener(this);
        fullScreenButton.setOnClickListener(this);
        mPlayeButton.setOnClickListener(this);
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

        mPlayerVideoCount.setText(videoData.getPlayCountText());
        mPlayerVideoTitle.setText(videoData.getTitle());
        mPlayerVideoType.setText(videoData.getpType());
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            /*final String s = Integer.toString(i)+"\n";
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nowData.append(s);
                }
            });*/
            switch (i)
            {
                case 16:
                {
                    if(bundle!=null)
                    {
                        int currentPosition = bundle.getInt("currentDuration");
                        videoView.seekTo(currentPosition);
                        int progressMax = videoView.getDuration();
                        int nowTime = videoView.getCurrentPosition();
                        mSeekBar.setMax(progressMax);
                        mSeekBar.setProgress(nowTime);
                    }
                    break;
                }
            }
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
        fullScreenFlag = !fullScreenFlag;
        if(videoView != null){
            if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }else{
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }
    public void fullScreenSettings()
    {
        ActionBar actionBar = getSupportActionBar();
        if(!fullScreenFlag)
        {
            //fullScreenFlag = false;
            if(actionBar!=null)
                actionBar.show();
        }
        else
        {
            //fullScreenFlag = true;
            scrollView.setVisibility(View.GONE);
            if(actionBar!=null)
                actionBar.hide();
            //videoView.setVideoViewSize(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels,true);
            mPlayerContent.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels));
            mPlayerContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    |View.SYSTEM_UI_FLAG_FULLSCREEN
                    //|View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
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
            case R.id.myPlayer_play_button:
            {
                if(videoView.isPlaying())
                {
                    videoView.pause();
                    mPlayeButton.setImageResource(R.drawable.play);
                }
                else
                {
                    videoView.start();
                    mPlayeButton.setImageResource(R.drawable.pause);
                }
                break;
            }
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
                //nowData.setText(builder.toString());
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int duration = videoView.getDuration();
        int currentDuration = videoView.getCurrentPosition();
        String videoId = playId;
        outState.putString("videoId",videoId);
        outState.putInt("duration",duration);
        outState.putInt("currentDuration",currentDuration);
        outState.putBoolean("fullScreen",fullScreenFlag);
    }

    private String ms2hms(int millis) {
        String result = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return result;
    }
}
