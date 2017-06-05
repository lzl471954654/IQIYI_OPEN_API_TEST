package com.lzl.iqiyi_open_api_test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzl.iqiyi_open_api_test.DataClass.RecommendData;
import com.lzl.iqiyi_open_api_test.DataClass.VideoData;
import com.lzl.iqiyi_open_api_test.HttpRequest.ChannelData;
import com.lzl.iqiyi_open_api_test.HttpRequest.DataRequest;
import com.lzl.iqiyi_open_api_test.HttpRequest.ParseDataFromHttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button getChannelList;
    Button getChannelData;
    Button recommendButton;
    Button getImageButton;
    Button searchButton;
    ImageView imageView;
    EditText editText;
    TextView data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getChannelData = (Button)findViewById(R.id.get_channel_data_button);
        getChannelList = (Button)findViewById(R.id.get_list_button);
        getImageButton = (Button)findViewById(R.id.getImageButton);
        searchButton = (Button)findViewById(R.id.searchButton);
        imageView = (ImageView)findViewById(R.id.image_data);
        editText = (EditText)findViewById(R.id.edit_text);
        data = (TextView)findViewById(R.id.request_data_text);
        recommendButton = (Button)findViewById(R.id.recommendButton);
        recommendButton.setOnClickListener(this);
        getChannelList.setOnClickListener(this);
        getChannelData.setOnClickListener(this);
        getImageButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick( View v) {
        switch (v.getId())
        {
            case R.id.searchButton:
            {
                System.out.println("search!");
                String s = editText.getText().toString();
                DataRequest dataRequest = DataRequest.newInstance();
                dataRequest.searchVideoNormally(s, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("Search!!!");
                        String s = response.body().string();
                        System.out.println(s);
                        final List<VideoData> list = ParseDataFromHttp.getSearchVideoList(s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder builder = new StringBuilder();
                                for (VideoData videoData : list) {
                                    builder.append(videoData);
                                }
                                data.setText(builder.toString());
                            }
                        });
                    }
                });
                break;
            }
            case R.id.getImageButton:
            {
                DataRequest dataRequest = DataRequest.newInstance();
                dataRequest.getPic("http://m.qiyipic.com/common/lego/20170605/9fc9cfd0a4264ebca49d29e058a00ebf.jpg", new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //InputStream inputStream = response.body().byteStream();
                       // final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        byte[] bytes = response.body().bytes();
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                });
                break;
            }
            case R.id.recommendButton:
            {
                System.out.println("recommendButton click!");
                DataRequest dataRequest = DataRequest.newInstance();
                dataRequest.getRecommendList(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                data.setText("Net ERROR");
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("recommendData success!");
                        final String s = response.body().string();
                        final StringBuilder builder = new StringBuilder();
                        List<RecommendData> list = ParseDataFromHttp.getRcommendDataList(s);
                        for (RecommendData recommendData : list) {
                            builder.append(recommendData);
                        }
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
            case R.id.get_channel_data_button:
            {
                DataRequest dataRequest = DataRequest.newInstance();
                dataRequest.getChannelDataDetails(editText.getText().toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
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
                        e.printStackTrace();
                        System.out.println("error list");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("success list");
                        //System.out.println(response.body().string());
                        String s = response.body().string();
                        System.out.println(s);
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
