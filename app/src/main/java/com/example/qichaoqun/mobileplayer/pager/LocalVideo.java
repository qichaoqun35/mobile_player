package com.example.qichaoqun.mobileplayer.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.activity.MyVideoPlayerActivity;
import com.example.qichaoqun.mobileplayer.adapter.LocalVideoListAdapter;
import com.example.qichaoqun.mobileplayer.base.BasePager;
import com.example.qichaoqun.mobileplayer.bean.VideoBean;

import java.util.ArrayList;

public class LocalVideo extends BasePager implements AdapterView.OnItemClickListener {

    public static final int FLAG = 10;
    private ListView mListView;
    private TextView mTextView;
    private ProgressBar mProgressBar;
    private ArrayList<VideoBean> videoList = null;


    public LocalVideo(Context context) {
        super(context);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == FLAG){
                //加载数据完成，判断是否有数据
                if(videoList != null && videoList.size() > 0){
                    //有数据，进行数据和view适配
                    LocalVideoListAdapter localVideoListAdapter = new LocalVideoListAdapter(context,videoList);
                    mListView.setAdapter(localVideoListAdapter);
                    mTextView.setVisibility(View.INVISIBLE);
                }else{
                    //没有数据，显示出没有数据的 textview
                    mTextView.setVisibility(View.VISIBLE);
                }
                //将进度条进行隐藏
                mProgressBar.setVisibility(View.GONE);
            }
        }
    };

    /**
     * 初始化视图，此处实现视图的适配
     * @return 视图 View
     */
    @Override
    public View inintView() {
       View view = View.inflate(context,R.layout.local_video_layout,null);
       mListView = view.findViewById(R.id.local_video_list);
       //设置list view的监听事件
       mListView.setOnItemClickListener(this);
       mTextView = view.findViewById(R.id.local_video_text);
       mProgressBar = view.findViewById(R.id.local_video_progress);
       return view;
    }

    /**
     * 使用该方法用来加载数据
     * 用来加载本地数据或者是联网数据
     * 该方法会在对象初始化时调用，之后会适配view
     */
    @Override
    public void inintDate() {
        super.inintDate();
        //获取本地数据
        getLocalData();
    }

    /**
     * 用于加载本地视频资源
     */
    private void getLocalData() {
        videoList = new ArrayList<>();
        //开启线程，实现不同加载数据
        new Thread(){
            @Override
            public void run() {
                super.run();
                //开始使用内容提供者加载数据
                ContentResolver contentResolver = context.getContentResolver();
                //获取游标进行数据查询,添加要查询的路径和内容
                //创建uri和要查询的内容的数组
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[]content = {
                        //视频名称
                        MediaStore.Video.Media.DISPLAY_NAME,
                        //视频的时长
                        MediaStore.Video.Media.DURATION,
                        //视频的大小
                        MediaStore.Video.Media.SIZE,
                        //视频的存储路径
                        MediaStore.Video.Media.DATA,
                        //艺术家
                        MediaStore.Video.Media.ARTIST
                };
                Cursor cursor = contentResolver.query(uri,content,null,null,null);
                while(cursor.moveToNext()){
                    VideoBean videoBean = new VideoBean();
                    String name = cursor.getString(0);
                    videoBean.setName(name);
                    Long time = cursor.getLong(1);
                    videoBean.setTime(time);
                    Long size = cursor.getLong(2);
                    videoBean.setSize(size);
                    String url = cursor.getString(3);
                    videoBean.setUrl(url);
                    String artist = cursor.getString(4);
                    videoBean.setArtist(artist);
                    videoList.add(videoBean);
                }
                cursor.close();
                //在线程加载完成之后，使用handler发送消息
                Message message = Message.obtain();
                message.what = 10;
                message.obj = 100;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //将路径传入并且去，调用自己写的播放器
        Intent intent = new Intent(context,MyVideoPlayerActivity.class);
        //利用bundle传递自己的播放列表的集合
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoList",videoList);
        intent.putExtras(bundle);
        intent.putExtra("position",position);
        context.startActivity(intent);
    }
}
