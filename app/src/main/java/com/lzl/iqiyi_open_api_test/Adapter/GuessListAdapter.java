package com.lzl.iqiyi_open_api_test.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lzl.iqiyi_open_api_test.DataClass.VideoData;
import com.lzl.iqiyi_open_api_test.HttpRequest.DataRequest;
import com.lzl.iqiyi_open_api_test.HttpRequest.ParseDataFromHttp;
import com.lzl.iqiyi_open_api_test.R;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by LZL on 2017/6/8.
 */

public class GuessListAdapter extends RecyclerView.Adapter<GuessListAdapter.GuessViewHolder> {
    private List<VideoData> mVideoDataList;
    private DataRequest dataRequest;
    private Activity activity;

    public GuessListAdapter(List<VideoData> list,Activity activity)
    {
        this.activity = activity;
        mVideoDataList = list;
        dataRequest = DataRequest.newInstance();
    }
    public static class GuessViewHolder extends RecyclerView.ViewHolder
    {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemDes;
        TextView itemCount;
        public GuessViewHolder(View view)
        {
            super(view);
            itemImage = (ImageView)view.findViewById(R.id.guess_item_image);
            itemTitle = (TextView)view.findViewById(R.id.guess_item_title);
            itemDes = (TextView)view.findViewById(R.id.guess_item_des);
            itemCount = (TextView)view.findViewById(R.id.guess_item_play_count_text);
        }
    }

    @Override
    public void onBindViewHolder(final GuessViewHolder holder, int position) {
        VideoData videoData = mVideoDataList.get(position);
        holder.itemDes.setText(videoData.getShortTitle());
        holder.itemTitle.setText(videoData.getTitle());
        holder.itemCount.setText(videoData.getPlayCountText());
        dataRequest.getPic(videoData.getImg(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Bitmap bitmap = ParseDataFromHttp.getPicFromResponse(response);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemImage.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    @Override
    public GuessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.guess_list_item,parent,false);
        GuessViewHolder viewHolder = new GuessViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mVideoDataList.size();
    }
}
