package com.example.qichaoqun.mobileplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.bean.InternetVideoBean;
import com.example.qichaoqun.mobileplayer.bean.VideoBean;

import org.xutils.x;

import java.util.ArrayList;

public class InternetVideoListAdapter extends BaseAdapter {

    private ArrayList<VideoBean> mList = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater = null;

    public InternetVideoListAdapter(Context context,ArrayList<VideoBean> list){
        mContext = context;
        mList = list;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mLayoutInflater.inflate(R.layout.internet_video_item,null);
        ImageView corverImage = convertView.findViewById(R.id.internet_video_cover);
        TextView videoName = convertView.findViewById(R.id.internet_video_name);
        TextView videoDesc = convertView.findViewById(R.id.internet_video_desc);

        //得到带有数据的对对象
        VideoBean videoBean = mList.get(position);
        x.image().bind(corverImage,videoBean.getCoverImg());
        videoName.setText(videoBean.getMovieName());
        videoDesc.setText(videoBean.getVideoTitle());
        return convertView;
    }
}
