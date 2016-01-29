package com.example.mantenimiento.multimediaproyect;

import java.util.ArrayList;

public class FullList {

    public FullList()
    {
        arrayList=new ArrayList<VideoItemList>();

        arrayList.add(new VideoItemList("twitch_128x128","PLG","http://www.ebookfrenzy.com/android_book/movie.mp4"));
        arrayList.add(new VideoItemList("twitch_128x128","TWITCH","http://usher.justin.tv/api/channel/hls/pgl.m3u8?allow_source=true&token=%7B%22user_id%22%3Anull%2C%22channel%22%3A%22pgl%22%2C%22expires%22%3A1454088601%2C%22chansub%22%3A%7B%22view_until%22%3A1924905600%2C%22restricted_bitrates%22%3A%5B%5D%7D%2C%22private%22%3A%7B%22allowed_to_view%22%3Atrue%7D%2C%22privileged%22%3Afalse%2C%22source_restricted%22%3Afalse%7D&sig=3b184ca8b96721f259a8b5649af0e95c1b5f1284"));
    }

    private static ArrayList<VideoItemList> arrayList;

    public FullList(ArrayList<VideoItemList> arrayList)
    {
        this.arrayList=arrayList;
    }

    public static ArrayList<VideoItemList> getArrayList()
    {
        return arrayList;
    }

    public static void setArrayList(ArrayList<VideoItemList> array)
    {
        arrayList=array;
    }

    public static void addItem(VideoItemList item)
    {
        arrayList.add(item);
    }

}
