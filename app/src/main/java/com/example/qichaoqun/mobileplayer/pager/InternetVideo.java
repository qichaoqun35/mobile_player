package com.example.qichaoqun.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.activity.MyVideoPlayerActivity;
import com.example.qichaoqun.mobileplayer.adapter.InternetVideoListAdapter;
import com.example.qichaoqun.mobileplayer.base.BasePager;
import com.example.qichaoqun.mobileplayer.bean.InternetVideoBean;
import com.example.qichaoqun.mobileplayer.bean.VideoBean;
import com.example.qichaoqun.mobileplayer.constant.MyContant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

public class InternetVideo extends BasePager {

    /**
     * 使用这种方式来完成对组件的初始化
     * 但是前提是要 x.view().inject(this,view); 将布局与xutils3 相结合
     * 才能使用
     */
    @ViewInject(R.id.internet_video_list)
    private ListView mListView;

    @ViewInject(R.id.internet_video_text)
    private TextView mTextView;

    @ViewInject(R.id.internet_video_progress)
    private ProgressBar mProgressBar;

    /**
     * 创建用于保存网络视频信息的对象的集合
     */
    ArrayList<VideoBean> mList = null;

    public InternetVideo(Context context) {
        super(context);
    }

    @Override
    public View inintView() {
        View view = View.inflate(context, R.layout.internet_video_layout,null);
        //将试图和xutils向关联,this是当前类对象，不是context
        x.view().inject(this,view);
        //在初始化控件时对listview进行条目的监听
        mListView.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //点击条目时跳转到播放视频的页面，开始播放视频
            Intent intent = new Intent(context, MyVideoPlayerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList",mList);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);
        }
    }

    @Override
    public void inintDate() {
        super.inintDate();
        RequestParams params = new RequestParams(MyContant.VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //将加载到的数据进行解析，并且进行与组件向结合
                getJsonData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("出错了", "onError: "+ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i("联网取消", "onCancelled: "+cex.getMessage());
            }

            @Override
            public void onFinished() {
                Log.i("联网完成了", "onFinished: ");
            }
        });
    }

    /**
     * 解析json数据并且把数据封装到集合中
     * @param result json类型的字符串
     */
    private void getJsonData(String result) {
         mList = resolveData(result);
         //设置listview的适配器
        if(mList != null && mList.size() > 0){
            //有数据进行数据与listview的适配,对其进行数据的设置
            InternetVideoListAdapter internetVideoListAdapter =
                    new InternetVideoListAdapter(context,mList);
            mListView.setAdapter(internetVideoListAdapter);
            //将TextView设置为不显示
            mTextView.setVisibility(View.INVISIBLE);
        }else{
            //没有数据，将textview设置为没有联网，或者是没有数据
            mTextView.setVisibility(View.VISIBLE);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * 使用系统json解析方式对加载到的json字符串解析到集合
     * @param result json字符串
     * @return 带有解析信息的集合
     */
    private ArrayList<VideoBean> resolveData(String result) {
        ArrayList<VideoBean> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");
            for(int i = 0;i < jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                VideoBean videoBean = new VideoBean();
                String movieName = jsonObject1.optString("movieName");
                videoBean.setMovieName(movieName);
                String videoTitle = jsonObject1.optString("videoTitle");
                videoBean.setVideoTitle(videoTitle);
                String corverImg = jsonObject1.optString("coverImg");
                videoBean.setCoverImg(corverImg);
                String videoUri = jsonObject1.optString("hightUrl");
                Log.i("视频播放的路径：：：：", "resolveData: "+jsonObject1.optString("hightUrl"));
                videoBean.setUrl(videoUri);
                list.add(videoBean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
