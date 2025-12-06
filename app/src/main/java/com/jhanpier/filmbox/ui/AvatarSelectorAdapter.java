package com.jhanpier.filmbox.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class AvatarSelectorAdapter extends BaseAdapter {

    private Context context;
    private int[] avatarList;

    public AvatarSelectorAdapter(Context context, int[] avatarList) {
        this.context = context;
        this.avatarList = avatarList;
    }

    @Override
    public int getCount() { return avatarList.length; }

    @Override
    public Object getItem(int position) { return avatarList[position]; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(avatarList[position]);
        return imageView;
    }
}
