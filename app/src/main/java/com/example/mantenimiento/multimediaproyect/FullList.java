package com.example.mantenimiento.multimediaproyect;

import java.util.ArrayList;

public class FullList {

    public FullList()
    {
        arrayList=new ArrayList<VideoItemList>();

        arrayList.add(new VideoItemList("twitch_128x128","PLG","http://www.ebookfrenzy.com/android_book/movie.mp4"));
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
