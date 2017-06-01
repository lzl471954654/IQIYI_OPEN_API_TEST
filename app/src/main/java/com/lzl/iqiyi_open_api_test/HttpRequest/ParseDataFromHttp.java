package com.lzl.iqiyi_open_api_test.HttpRequest;

import com.lzl.iqiyi_open_api_test.DataClass.VideoData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by LZL on 2017/6/1.
 */
public class ParseDataFromHttp {

    public static List<VideoData> getChannelVideoList(String res)
    {
        List<VideoData> list = new LinkedList<>();
        try
        {
            JSONObject object = new JSONObject(res);
            if (object.getInt("code")!=100000)
                return list;
            object = object.getJSONObject("data");
            JSONArray array = object.getJSONArray("video_list");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                VideoData videoData = new VideoData();
                videoData.setId(jsonObject.getString("id"));
                videoData.setTitle(jsonObject.getString("title"));
                videoData.setShortTitle(jsonObject.getString("short_title"));
                videoData.setImg(jsonObject.getString("img"));
                videoData.setSns_score(jsonObject.getString("sns_score"));
                videoData.setPlayCount(jsonObject.getString("play_count"));
                videoData.setPlayCountText(jsonObject.getString("play_count_text"));
                videoData.setAlbumId(jsonObject.getString("a_id"));
                videoData.setTvId(jsonObject.getString("tv_id"));
                String vip = jsonObject.getString("is_vip");
                boolean is_vip = false;
                if(vip.equals("1"))
                    is_vip = true;
                videoData.setVip(is_vip);
                videoData.setType(jsonObject.getString("type"));
                videoData.setpType(jsonObject.getString("p_type"));
                videoData.setDataTimeStamp(jsonObject.getString("data_timestamp"));
                videoData.setDataFormat(jsonObject.getString("data_format"));
                videoData.setTotalNum(jsonObject.getString("total_num"));
                videoData.setUpDateNum(jsonObject.getString("update_num"));
                list.add(videoData);
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally {
            return list;
        }
    }
    public static List<ChannelData> getChannelList(String res)
    {
        List<ChannelData> list = new LinkedList<>();
        try
        {
            JSONObject object = new JSONObject(res);
            if (object.getInt("code")!=100000)
                return list;
            JSONArray array = object.getJSONArray("data");
            for(int i = 0;i<array.length();i++)
            {
                JSONObject jsonObject = array.getJSONObject(i);
                ChannelData channelData = new ChannelData();
                channelData.setId(jsonObject.getString("id"));
                channelData.setName(jsonObject.getString("name"));
                channelData.setDesc(jsonObject.getString("desc"));
            }
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        finally {
            return list;
        }
    }
}
