package com.example.qichaoqun.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.example.qichaoqun.mobileplayer.IMyMusicService;
import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.activity.MyMusicPlayerActivity;
import com.example.qichaoqun.mobileplayer.bean.VideoBean;
import com.example.qichaoqun.mobileplayer.utils.SetSharedPreference;

import java.io.IOException;
import java.util.ArrayList;

public class MyMusicService extends Service {

    public static final String BORDERCAST = "com.qichaoqun.MUSIC_PLAYER";
    public static final int NORMAL = 1;
    public static final int SINGLE = 2;
    public static final int ALL = 3;
    private int playMode = NORMAL;
    /**
     * 在服务中绑定AIDL对象
     */
    private IMyMusicService.Stub mStub = new IMyMusicService.Stub() {

        //获取service类的对象
        MyMusicService mService = MyMusicService.this;

        @Override
        public void openMusic(int position) throws RemoteException {
            mService.openMusic(position);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void start() throws RemoteException {
            mService.start();
        }

        @Override
        public void pause() throws RemoteException {
            mService.pause();
        }

        @Override
        public void stop() throws RemoteException {
            mService.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return mService.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return mService.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return mService.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return mService.getName();
        }

        @Override
        public String getMusicUrl() throws RemoteException {
            return mService.getMusicUrl();
        }

        @Override
        public void next() throws RemoteException {
            mService.next();
        }

        @Override
        public void pre() throws RemoteException {
            mService.pre();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            mService.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return mService.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mService.isPlaying();
        }

        @Override
        public void setCurrentProgress(int position) {
            mService.setCurrentProgress(position);
        }
    };
    private ArrayList<VideoBean> musicList;
    private int mPosition;
    private MediaPlayer mMediaPlayer;
    private VideoBean mVideoBean;
    private NotificationManager mNotificationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        //获取音频数据
        getLocalData();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mStub;
    }

    /**
     * 打开音乐
     */
    private void openMusic(int position) {
        mPosition = position;
        if (musicList != null && musicList.size() > 0) {
            mVideoBean = musicList.get(position);
            //将当前的Mediaplayer对象置位空
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            }
            try {
                mMediaPlayer = new MediaPlayer();
                //播放前的准备工作
                mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                //当一个音频播放完成的时候
                mMediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                //当播放出错的时候
                mMediaPlayer.setOnErrorListener(new MyOnErrorListener());
                //将播放音频的路径给 Mediaplayer
                mMediaPlayer.setDataSource(mVideoBean.getUrl());
                //告诉Mediaplayer已经准备好了
                mMediaPlayer.prepareAsync();

                if(playMode == SINGLE){
                    mMediaPlayer.setLooping(true);
                }else{
                    mMediaPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 播放音乐
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void start() {
        //开始播放音频
        mMediaPlayer.start();
        setNotification();
    }

    /**
     * 设置notification通知栏
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setNotification() {
        //当音乐开始播放的时候设置状态栏的通知栏
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        //跳转的意图
        Intent intent = new Intent(this, MyMusicPlayerActivity.class);
        intent.putExtra("Notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("影音播放")
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentText("正在播放" + getName())
                .setContentIntent(pendingIntent)
                .build();
        mNotificationManager.notify(1, notification);
    }

    /**
     * 暂停音乐
     */
    private void pause() {
        mMediaPlayer.pause();
    }

    /**
     * 停止播放音乐
     */
    private void stop() {
        mMediaPlayer.stop();
        mNotificationManager.cancel(1);
    }

    /**
     * 得到当前播放的进度
     */
    private int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 得到音频的总时长
     */
    private int getDuration() {
        return mMediaPlayer.getDuration();
    }

    /**
     * 得到演唱者
     */
    private String getArtist() {
        return mVideoBean.getArtist();
    }

    /**
     * 得到歌曲的名字
     */
    private String getName() {
        return mVideoBean.getName();
    }

    /**
     * 得到歌曲的播放的路径
     */
    private String getMusicUrl() {
        return mVideoBean.getUrl();
    }

    /**
     * 播放下一个音乐
     */
    private void next() {
        //根据当前的模式设置下一个位置的音频
        setNextPosition();
        //根据当前的模式和下标位置去设置播放音频
        setNextAudio();
    }

    private void setNextAudio() {
        int playMode = getPlayMode();
        if(playMode == NORMAL){
            if(mPosition < musicList.size()){
                openMusic(mPosition);
            }else{
                mPosition = musicList.size()-1;
            }
        }else if(playMode == SINGLE){
            openMusic(mPosition);
        }else if(playMode == ALL){
            openMusic(mPosition);
        }else{
            if(mPosition < musicList.size()){
                openMusic(mPosition);
            }else{
                mPosition = musicList.size()-1;
            }
        }
    }

    private void setNextPosition() {
        int playMode = getPlayMode();
        if(playMode == NORMAL){
            mPosition++;
        }else if(playMode == SINGLE){
            mPosition++;
            if(mPosition >= musicList.size()){
                mPosition = 0;
            }
        }else if(playMode == ALL){
            mPosition++;
            if(mPosition >= musicList.size()){
                mPosition = 0;
            }
        }else{
            mPosition++;
        }
    }


    /**
     * 播放前一个
     */
    private void pre() {
        //1.根据当前的播放模式，设置上一个的位置
        setPrePosition();
        //2.根据当前的播放模式和下标位置去播放音频
        openPreAudio();
    }

    private void openPreAudio() {
        int playMode = getPlayMode();
        if(playMode == NORMAL){
            if(mPosition >= 0){
                openMusic(mPosition);
            }else{
                mPosition = 0;
            }
        }else if(playMode == SINGLE){
            openMusic(mPosition);
        }else if(playMode == ALL){
            openMusic(mPosition);
        }else{
            if(mPosition >= 0){
                openMusic(mPosition);
            }else{
                mPosition = 0;
            }
        }
    }

    private void setPrePosition() {
        int playMode = getPlayMode();
        if(playMode == NORMAL){
            mPosition--;
        }else if(playMode == SINGLE){
            mPosition--;
            if(mPosition < 0){
                mPosition = musicList.size()-1;
            }
        }else if(playMode == ALL){
            mPosition--;
            if(mPosition < 0){
                mPosition = musicList.size()-1;
            }
        }else{
            mPosition--;
        }
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode) {
        this.playMode = playMode;
        //通过sharedpreference保存起来
        SetSharedPreference.setPlayerMode(this,playMode);
    }

    /**
     * 得到当前播放的模式
     */
    private int getPlayMode() {
        return playMode;
    }

    /**
     * 判断是否在播放音频
     */
    private boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * 设置当前音乐的播放进度
     */
    private void setCurrentProgress(int position) {
        mMediaPlayer.seekTo(position);
    }


    //得到音乐播放的列表
    private void getLocalData() {
        musicList = new ArrayList<>();
        //开启线程，实现不同加载数据
        new Thread() {
            @Override
            public void run() {
                super.run();
                //开始使用内容提供者加载数据
                ContentResolver contentResolver = getContentResolver();
                //获取游标进行数据查询,添加要查询的路径和内容
                //创建uri和要查询的内容的数组
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] content = {
                        //视频名称
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        //视频的时长
                        MediaStore.Audio.Media.DURATION,
                        //视频的大小
                        MediaStore.Audio.Media.SIZE,
                        //视频的存储路径
                        MediaStore.Audio.Media.DATA,
                        //艺术家
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = contentResolver.query(uri, content, null, null, null);
                while (cursor.moveToNext()) {
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
                    musicList.add(videoBean);
                }
                cursor.close();
            }
        }.start();
    }

    public class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPrepared(MediaPlayer mp) {
            //当准备好之后开始通知播放页面设置相关的信息
            notifyMusicActivity(BORDERCAST);
            start();
        }
    }

    private void notifyMusicActivity(String bordercast) {
        Intent intent = new Intent(bordercast);
        sendBroadcast(intent);
    }

    public class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    public class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next();
            return true;
        }
    }


}
