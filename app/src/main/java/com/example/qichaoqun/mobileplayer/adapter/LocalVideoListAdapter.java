package com.example.qichaoqun.mobileplayer.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.bean.VideoBean;
import com.example.qichaoqun.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * 对listview进行适配器的设置
 * 要使用这种方法，使用viewHolder会出先视频加载闪退的问题
 */
public class LocalVideoListAdapter extends BaseAdapter {

    private final ArrayList<VideoBean> videoList;
    private Context context;
    private Utils utils = null;

    public LocalVideoListAdapter(Context context, ArrayList<VideoBean> videoList) {
        this.context = context;
        this.videoList = videoList;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context,R.layout.local_video_item,null);
        }
        ImageView imageView = convertView.findViewById(R.id.local_video_image);
        TextView videoName = convertView.findViewById(R.id.local_video_name);
        TextView videoTime = convertView.findViewById(R.id.local_video_time);
        TextView videoSize = convertView.findViewById(R.id.local_video_size);

        VideoBean videoBean = videoList.get(position);
        videoName.setText(videoBean.getName());
        videoTime.setText(utils.stringForTime(Math.toIntExact(videoBean.getTime())));
        videoSize.setText(android.text.format.Formatter.formatFileSize(context, videoBean.getSize()));

        return convertView;
    }
}
