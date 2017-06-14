package com.lzl.iqiyi_open_api_test.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.BoringLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.w3c.dom.Text;

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
    private TextView mPlayerProgressText;
    private TextView mPlayerBrightnessText;
    private RelativeLayout mPlayerContent;
    private ScrollView scrollView;
    private ProgressBar progressBar;
    private LinearLayout progressLayout;
    private GestureDetector gestureDetector;

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
        mPlayerBrightnessText = (TextView)findViewById(R.id.myPlayer_brightness);
        mPlayerProgressText = (TextView)findViewById(R.id.myPlayer_progress_text);
        progressLayout = (LinearLayout)findViewById(R.id.myPlayer_progress_layout);
        progressBar = (ProgressBar)findViewById(R.id.myPlayer_progressBar);
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
                videoView.seekTo(mProgress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekBar.setProgress(mProgress);
                videoView.seekTo(mProgress);

            }
        });

        createGestureDetectorListener();
        videoView.setOnTouchListener(onTouchListener);
        //progressBar.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.INVISIBLE);
        mPlayerVideoCount.setText(videoData.getPlayCountText());
        mPlayerVideoTitle.setText(videoData.getTitle());
        String type = videoData.getpType();
        if(type.equals("2"))
            mPlayerVideoType.setText("电视剧");
        else if(type.equals("3"))
            mPlayerVideoType.setText("综艺节目");
        else
            mPlayerVideoType.setText("单视频专辑");
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
        /*if(videoView.isPlaying())
            mPlayeButton.setImageResource(R.drawable.pause);
        else
            mPlayeButton.setImageResource(R.drawable.play);*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(videoView!=null)
        {
            videoView.pause();
            //mPlayeButton.setImageResource(R.drawable.play);
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
            Log.e("OnSeekSuccess!!",Long.toString(l));
        }

        @Override
        public void OnWaiting(boolean b) {
            Log.e("OnWaiting!!",Boolean.toString(b));
            final boolean wait = b;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(wait)
                        progressLayout.setVisibility(View.VISIBLE);
                        //progressBar.setVisibility(View.VISIBLE);
                    else
                        progressLayout.setVisibility(View.INVISIBLE);
                        //progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void OnError(ErrorCode errorCode) {
            Log.e("OnError!!",errorCode.name());
        }

        @Override
        public void OnPlayerStateChanged(int i) {
            Log.e("OnPlayerStateChanged!!",Long.toString(i));

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



    View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
    };

    public void createGestureDetectorListener()
    {
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.e("onDown","onDown....");
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                Log.e("showPress","showPress....");
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.e("onSingleTapUp","onSingleTapUp");
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                String s = "distance x:"+distanceX+"\tdistance y:"+distanceY+"\t"+e1.getX()+"\t"+e2.getX()+"\t"+e1.getY()+"\t"+e2.getY();
                Log.e("onScroll :\t",s);


                float x = e2.getX() - e1.getX();
                float y = e2.getY() - e1.getY();
                if(Math.abs(x)>Math.abs(y))
                {
                    mPlayerProgressText.setVisibility(View.VISIBLE);
                    mPlayerHandler.removeCallbacks(progressTextHiddenRun);
                    if(x>50)
                    {
                        int duration = videoView.getDuration();
                        int currentDuration = videoView.getCurrentPosition();
                        double percent = (x/500)*0.01;
                        int moreDuration = (int)(duration*percent);
                        if(currentDuration+moreDuration>duration)
                            currentDuration = duration;
                        else
                            currentDuration+=moreDuration;
                        mPlayerProgressText.setText(ms2hms(currentDuration));
                        mSeekBar.setProgress(currentDuration);
                        videoView.seekTo(currentDuration);
                        //Log.e("move Right ",Float.toString(x));
                    }
                    else if(x<-50)
                    {
                        int duration = videoView.getDuration();
                        int currentDuration = videoView.getCurrentPosition();
                        double percent = (Math.abs(x)/500)*0.01;
                        int lessDuration = (int)(duration*percent);
                        if(currentDuration-lessDuration<0)
                            currentDuration = 0;
                        else
                            currentDuration-=lessDuration;
                        mPlayerProgressText.setText(ms2hms(currentDuration));
                        mSeekBar.setProgress(currentDuration);
                        videoView.seekTo(currentDuration);
                    }
                    mPlayerHandler.postDelayed(progressTextHiddenRun,2000);
                }
                else
                {
                    mPlayerHandler.removeCallbacks(brightnessTextHiddenRun);
                    mPlayerBrightnessText.setVisibility(View.VISIBLE);
                    Log.e("Brightness:","\t"+getAppBrightness());
                    if(y>50)
                    {
                        int brightness = getAppBrightness();
                        double percent = (y/300)*0.01;
                        int lessLight = (int)(255*percent);
                        brightness-=lessLight;
                        if(brightness>0)
                            setAppBrightness(brightness);
                        else
                        {
                            brightness = 0;
                            setAppBrightness(brightness);
                        }
                        int progress  = (int)(((brightness)/255.0)*100.0);
                        mPlayerBrightnessText.setText("亮度："+progress+"%");
                        Log.e("light","\t"+lessLight);
                        //Log.e("move Down ",Float.toString(y));
                    }
                    else if(y<-50)
                    {
                        int brightness = getAppBrightness();
                        double percent = (Math.abs(y)/300)*0.01;
                        int moreLight = (int)(255*percent);
                        brightness+=moreLight;
                        if(brightness<=255)
                            setAppBrightness(brightness);
                        else
                        {
                            brightness = 255;
                            setAppBrightness(brightness);
                        }
                        Log.e("light","\t"+moreLight);
                        int progress  = (int)((brightness/255.0)*100.0);
                        mPlayerBrightnessText.setText("亮度："+progress+"%");
                        //Log.e("move Up ",Float.toString(y));
                    }
                    mPlayerHandler.postDelayed(brightnessTextHiddenRun,2000);
                }
                //mPlayerProgressText.setVisibility(View.INVISIBLE);

                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.e("LongPress","press....");
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x = e2.getX() - e1.getX();
                float y = e2.getY() - e1.getY();
                if(Math.abs(x)>Math.abs(y))
                {
                    if(x>50)
                    {
                        Log.e("move Right ",Float.toString(x));
                    }
                    else if(x<-50)
                    {
                        Log.e("move Left ",Float.toString(x));
                    }
                }
                else
                {
                    if(y>50)
                    {
                        Log.e("move Down ",Float.toString(y));
                    }
                    else if(y<-50)
                    {
                        Log.e("move Up ",Float.toString(y));
                    }
                }
                return false;
            }
        });
    }

    Runnable progressTextHiddenRun = new Runnable() {
        @Override
        public void run() {
            mPlayerProgressText.setVisibility(View.INVISIBLE);
        }
    };
    Runnable brightnessTextHiddenRun = new Runnable() {
        @Override
        public void run() {
            mPlayerBrightnessText.setVisibility(View.INVISIBLE);
        }
    };

    private int getAppBrightness()
    {
        int brightness = 0;
        try
        {
            brightness = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
        }catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        return brightness;
    }
    private void setAppBrightness(int brightness)
    {
        Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,brightness);
    }

}
