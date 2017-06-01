package com.lzl.iqiyi_open_api_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzl.iqiyi_open_api_test.DataClass.VideoData;
import com.lzl.iqiyi_open_api_test.HttpRequest.ChannelData;
import com.lzl.iqiyi_open_api_test.HttpRequest.DataRequest;
import com.lzl.iqiyi_open_api_test.HttpRequest.ParseDataFromHttp;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button getChannelList;
    Button getChannelData;
    EditText editText;
    TextView data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getChannelData = (Button)findViewById(R.id.get_channel_data_button);
        getChannelList = (Button)findViewById(R.id.get_list_button);
        editText = (EditText)findViewById(R.id.edit_text);
        data = (TextView)findViewById(R.id.request_data_text);
        getChannelList.setOnClickListener(this);
        getChannelData.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.get_channel_data_button:
            {
                DataRequest dataRequest = DataRequest.newInstance();
                dataRequest.getChannelDataDetails(editText.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //System.out.println(response.body().string());
                        String s = response.body().string();
                       // response.body().close();
                        List<VideoData> list = ParseDataFromHttp.getChannelVideoList(s);
                        final StringBuilder builder = new StringBuilder();
                        for (VideoData videoData : list) {
                            builder.append(videoData.toString());
                        }
                        System.out.println(builder.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                data.setText(builder.toString());
                            }
                        });
                    }
                });
                break;
            }
            case R.id.get_list_button:
            {
                DataRequest dataRequest = DataRequest.newInstance();
                dataRequest.getChannelList(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //System.out.println(response.body().string());
                        String s = response.body().string();
                        //response.body().close();
                        List<ChannelData> list = ParseDataFromHttp.getChannelList(s);
                        final StringBuilder builder = new StringBuilder();
                        for(ChannelData channelData:list)
                        {
                            builder.append(channelData.toString());
                        }
                        //System.out.println(builder.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                data.setText(builder.toString());
                            }
                        });
                    }
                });
                break;
            }
        }
    }
}
