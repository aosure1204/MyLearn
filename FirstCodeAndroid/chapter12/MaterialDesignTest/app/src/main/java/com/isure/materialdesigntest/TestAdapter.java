package com.isure.materialdesigntest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TestAdapter extends BaseAdapter {
    private Context mContext;
    private List<Fruit> mFruitList;

    public TestAdapter(List<Fruit> fruitList, Context context) {
        mFruitList = fruitList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mFruitList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFruitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView fruitImage;
        TextView fruitName;
        if(convertView == null) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.fruit_item, parent, false);
            fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            fruitName = (TextView) view.findViewById(R.id.fruit_name);
            ViewHolder viewHodler = new ViewHolder(fruitImage, fruitName);
            convertView = view;
            convertView.setTag(viewHodler);
        } else {
            ViewHolder viewHodler = (ViewHolder) convertView.getTag();
            fruitImage = viewHodler.fruitImage;
            fruitName = viewHodler.fruitName;
        }

        Fruit fruit = mFruitList.get(position);
        fruitImage.setImageResource(fruit.getImageId());
        fruitName.setText(fruit.getName());

        return convertView;
    }

    static class ViewHolder {
        ImageView fruitImage;
        TextView fruitName;

        public ViewHolder(ImageView fruitImage, TextView fruitName) {
            this.fruitImage = fruitImage;
            this.fruitName = fruitName;
        }
    }
}
