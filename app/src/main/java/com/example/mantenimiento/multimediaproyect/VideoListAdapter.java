package com.example.mantenimiento.multimediaproyect;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nonek on 29/01/2016.
 */
public abstract class VideoListAdapter extends BaseAdapter {

    ImageView imageView;
    TextView textViewTitle,textViewSubTitle;

    Context context;
    ArrayList data;
    private static LayoutInflater inflater = null;

    VideoItemList vil;

    public VideoListAdapter(Context context, ArrayList data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.video_item_list, null);
        }

        onEntrada(data.get(position), vi);

        return vi;
    }

    public abstract void onEntrada(Object entrada, View view);

}
