package com.lzl.iqiyi_open_api_test.HttpRequest;

/**
 * Created by LZL on 2017/6/1.
 */
public class ChannelData {
    private String id;
    private String name;
    private String desc;

    @Override
    public String toString() {
        return id+"\t"+name+"\t"+desc+"\n";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
