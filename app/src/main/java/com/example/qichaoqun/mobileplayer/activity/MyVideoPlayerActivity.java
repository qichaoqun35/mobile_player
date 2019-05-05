package com.example.qichaoqun.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.bean.VideoBean;
import com.example.qichaoqun.mobileplayer.utils.Utils;
import com.example.qichaoqun.mobileplayer.view.MyViewVideo;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyVideoPlayerActivity extends Activity implements View.OnClickListener {

    public static final int FLAG = 1;
    public static final int TEN = 10;
    public static final int TWENTY = 20;
    public static final int THIRTY = 40;
    public static final int SIXTY = 60;
    public static final int EIGHTY = 80;
    public static final int HUNDRAY = 100;
    public static final int MESSAGE = 2;
    public static final int DEFAULT_SCREEN = 1;
    public static final int FULL_SCREEN = 2;
    private Uri mUri = null;
    private Utils mUtils = null;
    private MyViewVideo mVideoView;
    private TextView controllerVideoName;
    private ImageView controllerVideoBattery;
    private TextView controllerVideoTiem;
    private Button controllerVideoVioce;
    private SeekBar controllerVideoVioceSeekbar;
    private Button controllerVideoAbout;
    private TextView controllerVideoStartTime;
    private SeekBar controllerVideoTimeSeekbar;
    private TextView controllerVideoTotalTime;
    private Button controlerVideoButtonExt;
    private Button controlerVideoButtonPre;
    private Button controlerVideoButtonPause;
    private Button controlerVideoButtonNext;
    private Button controlerVideoButtonScreen;
    private MyBorderCast mMyBorderCast;
    private ArrayList<VideoBean> mList;
    private int mPosition;
    private int mMyPosition;
    private GestureDetector mGestureDetector;
    private boolean isHidden = true;
    private RelativeLayout mRelativeLayout;
    private boolean isFullScreen = true;
    private int mScreenWidth = 0;
    private int mScreenHeiht = 0;
    private int currentVoice = 0;
    private int maxVoice = 0;
    private AudioManager mAudioManager;
    private boolean isNoVoice = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_player_layout);
        //初始化所有的控件
        findViews();
        //获取传过来的播放路径
        getData();
        //设置视频的播放的路径
        setData();
        //得到屏幕的宽和高
        getScreenSize();
        //得到系统的声音
        getSystemVoice();
        //注册电量的广播
        registBoradcast();
        //播放前的准备，监听系统播放器是否完成准备
        mVideoView.setOnPreparedListener(new MyOnPreparedListener());
        //设置系统播放器播放出错时的动作处理
        mVideoView.setOnErrorListener(new MyOnErrorListener());
        //设置播放完成时的动作
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());
        //设置seekbar改动是的播放进度
        controllerVideoTimeSeekbar.setOnSeekBarChangeListener(new MyOnTimeSeekBarChangeListener());
        //注册手势识别器
        mGestureDetector = new GestureDetector(new MySimpleOnGestureListener());
        //对音量的seekbar进行监听
        controllerVideoVioceSeekbar.setOnSeekBarChangeListener(new MyOnVoiceSeekBarChangeListener());
    }

    /**
     * 初始化各种组件，并且为每个组件设置监听
     */
    private void findViews() {

        mVideoView = (MyViewVideo) findViewById(R.id.video_player);
        controllerVideoName = (TextView) findViewById(R.id.controller_video_name);
        controllerVideoBattery = (ImageView) findViewById(R.id.controller_video_battery);
        controllerVideoTiem = (TextView) findViewById(R.id.controller_video_tiem);
        controllerVideoVioce = (Button) findViewById(R.id.controller_video_vioce);
        controllerVideoVioceSeekbar = (SeekBar) findViewById(R.id.controller_video_vioce_seekbar);
        controllerVideoAbout = (Button) findViewById(R.id.controller_video_about);
        controllerVideoStartTime = (TextView) findViewById(R.id.controller_video_start_time);
        controllerVideoTimeSeekbar = (SeekBar) findViewById(R.id.controller_video_time_seekbar);
        controllerVideoTotalTime = (TextView) findViewById(R.id.controller_video_total_time);
        controlerVideoButtonExt = (Button) findViewById(R.id.controler_video_button_ext);
        controlerVideoButtonPre = (Button) findViewById(R.id.controler_video_button_pre);
        controlerVideoButtonPause = (Button) findViewById(R.id.controler_video_button_pause);
        controlerVideoButtonNext = (Button) findViewById(R.id.controler_video_button_next);
        controlerVideoButtonScreen = (Button) findViewById(R.id.controler_video_button_screen);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.video_controller);
        mRelativeLayout.setVisibility(View.INVISIBLE);
        mUtils = new Utils();

        controllerVideoVioce.setOnClickListener(this);
        controllerVideoAbout.setOnClickListener(this);
        controlerVideoButtonExt.setOnClickListener(this);
        controlerVideoButtonPre.setOnClickListener(this);
        controlerVideoButtonPause.setOnClickListener(this);
        controlerVideoButtonNext.setOnClickListener(this);
        controlerVideoButtonScreen.setOnClickListener(this);
    }

    /**
     * 实现组建的监听动作
     * @param v 视图，即组件
     */
    @Override
    public void onClick(View v) {
        if (v == controlerVideoButtonExt) {
            //返回按键，之间返回
            this.finish();
        } else if (v == controlerVideoButtonPre) {
            //播放上一个视频
            setPreVideo();
        } else if (v == controlerVideoButtonPause) {
            //获取当前播放状态，如果播放中设置按钮背景为播放，否则设置为暂停
            setPauseAndStart();
        } else if (v == controlerVideoButtonNext) {
            //设置下一个播放的视频
            setNextVideo();
        } else if (v == controlerVideoButtonScreen) {
            //设置全屏或者是半屏
            setDefaultAndFull();
        }else if(v == controllerVideoVioce){
            //设置声音的开或者是关
            setVoiceOpenOrClose();
        }else if(v == controllerVideoAbout){
            //控制面板中的关于按键
        }
        //完成一个动作之后重新发送退出控制面板的消息
        mHandler.removeMessages(MESSAGE);
        mHandler.sendEmptyMessageDelayed(MESSAGE, 4000);
    }

    /**
     * 获取传递过来的数据
     * 如果是别的应用调用这个播放界面，会使用uri传递播放地址
     * 如果是应用本身会使用集合进行传递播放列表
     * 所以uri和只能使用一个
     * 只能是一个为空另一个不为空
     */
    private void getData() {
        mUri = getIntent().getData();
        mList = (ArrayList<VideoBean>) getIntent().getSerializableExtra("videoList");
        mPosition = getIntent().getIntExtra("position", 0);
        mMyPosition = mPosition;
    }

    /**
     * 设置视频的播放的路径
     */
    private void setData() {
        if (mUri != null) {
            //设置播放的路径
            mVideoView.setVideoURI(mUri);
            //设置播放的名称
            controllerVideoName.setText(mUri.toString());
            //设置按钮不可以点击
            controlerVideoButtonPre.setEnabled(false);
            controlerVideoButtonNext.setEnabled(false);
        } else if (mList != null && mList.size() > 0) {
            VideoBean bean = mList.get(mPosition);
            mVideoView.setVideoPath(bean.getUrl());
            //设置播放的名称
            controllerVideoName.setText(bean.getName());
            if (mList.size() == 1) {
                controlerVideoButtonPre.setEnabled(false);
                controlerVideoButtonNext.setEnabled(false);
            }
        } else {
            Toast.makeText(this, "播放出错了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 得到屏幕的尺寸
     */
    private void getScreenSize() {
        //得到屏幕的宽和高
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeiht = displayMetrics.heightPixels;
    }

    /**
     * 设置系统的音量，并且让seekbar显示
     */
    private void getSystemVoice() {
        //获取系统的音量，并且改变系统的音量
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        controllerVideoVioceSeekbar.setMax(maxVoice);
        controllerVideoVioceSeekbar.setProgress(currentVoice);
    }



    /**
     * 设置前一个播放的视频
     */
    private void setPreVideo() {
        if (mUri != null) {
            Toast.makeText(this, "已经是最前方了呦。。。", Toast.LENGTH_SHORT).show();
        } else if (mList != null && mList.size() > 0) {
            if (mMyPosition == 0) {
                Toast.makeText(this, "已经是最前方了呦。。。", Toast.LENGTH_SHORT).show();
            } else {
                mVideoView.setVideoPath(mList.get(mMyPosition).getUrl());
                controllerVideoName.setText(mList.get(mMyPosition).getName());
                mMyPosition--;
            }
        }
    }

    /**
     * 设置下一个播放的视频
     */
    private void setNextVideo() {
        if (mUri != null) {
            Toast.makeText(this, "已经是尽头了呦。。。", Toast.LENGTH_SHORT).show();
        } else if (mList !=  null && mList.size() > 0) {
            if (mMyPosition == mList.size()) {
                Toast.makeText(this, "已经是尽头了呦。。。", Toast.LENGTH_SHORT).show();
            } else {
                mVideoView.setVideoPath(mList.get(mMyPosition).getUrl());
                controllerVideoName.setText(mList.get(mMyPosition).getName());
                mMyPosition++;
            }
        }
    }

    /**
     * 设置声音的开和关
     */
    private void setVoiceOpenOrClose() {
        if(isNoVoice){
            updateVoice(4);
            controllerVideoVioce.setBackgroundResource(R.drawable.btn_voice_pressed);
            isNoVoice = false;
        }else{
            updateVoice(0);
            controllerVideoVioce.setBackgroundResource(R.drawable.btn_voice_normal);
            isNoVoice = true;
        }
    }

    /**
     * 设置播放或者是暂停
     */
    private void setPauseAndStart() {
        if (mVideoView.isPlaying()) {
            //正在播放则要暂停，将背景换为暂停
            mVideoView.pause();
            controlerVideoButtonPause.setBackgroundResource(R.drawable.controller_video_start_selector);
        } else {
            mVideoView.start();
            controlerVideoButtonPause.setBackgroundResource(R.drawable.controller_video_pause_selector);
        }
    }

    /**
     * 发送消息处理个中事件
     * 包括获取当前的播放位置
     * 隐藏控制面板
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FLAG:
                    //获取当前播放的位置
                    int currnetTime = mVideoView.getCurrentPosition();
                    //更新进度条的位置
                    controllerVideoTimeSeekbar.setProgress(currnetTime);
                    //跟新文本显示的位置
                    controllerVideoStartTime.setText(mUtils.stringForTime(currnetTime));
                    //显示系统时间在播放器上
                    controllerVideoTiem.setText(getSystemTime());
                    //清空当前消息重新发送
                    mHandler.removeMessages(FLAG);
                    //再次放消息更新进度
                    mHandler.sendEmptyMessageDelayed(FLAG, 1000);
                    break;
                case MESSAGE:
                    //隐藏控制面板
                    hiddenController();
                default:
                    break;
            }
        }
    };

    /**
     * 更新seekbar上的音量
     * @param progress 当前seekbar上的进度
     */
    private void updateVoice(int progress) {
        //flags == 1说明会显示系统的调节音量大小的组件，0 则是不会的
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
        controllerVideoVioceSeekbar.setProgress(progress);
        currentVoice = progress;
    }

    /**
     * 设置全屏或者是半屏
     */
    private void setDefaultAndFull() {
        //双击屏幕
        if (isFullScreen) {
            //变为默认
            setScreenSize(DEFAULT_SCREEN);
        } else {
            //变为全屏
            setScreenSize(FULL_SCREEN);
        }
    }

    /**
     * 设置全屏或者是默认的屏幕
     * @param screenSize 根据不同的参数来判断是设置为哪一种屏幕
     */
    private void setScreenSize(int screenSize) {
        switch (screenSize) {
            case 1:
                //将屏幕尺寸变为默认
                //真是的视频的宽和高
                int videoWidth = mVideoView.getWidth();
                int videoHeight = mVideoView.getHeight();
                //屏幕的宽和高
                int width = mScreenWidth;
                int height = mScreenHeiht;
                // for compatibility, we adjust size based on aspect ratio
                if (videoWidth * height < width * videoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * videoWidth / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * videoHeight / videoWidth;
                }
                mVideoView.setHeightAndWidth(width,height);
                //将按钮状态改变
                controlerVideoButtonScreen.setBackgroundResource(R.drawable.btn_full_screen_normal);
                isFullScreen = false;
                break;
            case 2:
                //将屏幕尺寸变大
                mVideoView.setHeightAndWidth(mScreenWidth,mScreenHeiht);
                //将按钮状态改变
                controlerVideoButtonScreen.setBackgroundResource(R.drawable.btn_default_screen_pressed);
                isFullScreen = true;
                break;
            default:
                break;
        }
    }

    /**
     * 注册广播用于接收系统电量
     */
    private void registBoradcast() {
        //注册广播用于接收系统电量的信息
        mMyBorderCast = new MyBorderCast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mMyBorderCast, intentFilter);
    }

    /**
     * 获取系统时间
     * @return 时间的字符串格式
     */
    public String getSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 用于接收广播的实体类
     * 主要负责电量信息的处理
     */
    class MyBorderCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            if (level <= TEN) {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_0);
            } else if (level <= TWENTY) {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_10);
            } else if (level <= THIRTY) {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_20);
            } else if (level <= SIXTY) {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_40);
            } else if (level <= EIGHTY) {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_60);
            } else if (level <= HUNDRAY) {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_80);
            } else {
                controllerVideoBattery.setImageResource(R.drawable.ic_battery_100);
            }
        }
    }

    @Override
    protected void onDestroy() {
        //注销广播，在调用父类方法之前
        if (mMyBorderCast != null) {
            unregisterReceiver(mMyBorderCast);
            mMyBorderCast = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸监听与手势识别器向结合
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void showController() {
        mRelativeLayout.setVisibility(View.VISIBLE);
        isHidden = false;
    }

    /**
     * 隐藏控制面板
     */
    private void hiddenController() {
        mRelativeLayout.setVisibility(View.INVISIBLE);
        isHidden = true;
    }
    /**
     * 监听系统音量的改变
     * 通过监听视同音量键的改变
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice--;
            updateVoice(currentVoice);
            mHandler.removeMessages(MESSAGE);
            mHandler.sendEmptyMessageDelayed(MESSAGE,4000);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice++;
            updateVoice(currentVoice);
            mHandler.removeMessages(MESSAGE);
            mHandler.sendEmptyMessageDelayed(MESSAGE,4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 准备播放的实体类的方法
     */
    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
            //获取播放时长
            int time = mVideoView.getDuration();
            //将播放时长进行设置到seekbar和时长text view
            controllerVideoTimeSeekbar.setMax(time);
            controllerVideoTotalTime.setText(mUtils.stringForTime(time));
            mHandler.sendEmptyMessage(FLAG);
            //将视频播放设置为全屏播放
            mVideoView.setHeightAndWidth(mScreenWidth, mScreenHeiht);
        }
    }

    /**
     * 当播放出错时的处理类
     */
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Toast.makeText(MyVideoPlayerActivity.this,
                    "播放出错了喔。。", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * 监听播放完成的内部类
     */
    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            //判断是否是第一个或者是最后一个
            //如果不是自动播放下一个
            if (mMyPosition == mList.size() - 1) {
                Toast.makeText(MyVideoPlayerActivity.this,
                        "已经是尽头了呦。。。", Toast.LENGTH_SHORT).show();
                mVideoView.pause();
                controlerVideoButtonPause.setBackgroundResource(
                        R.drawable.controller_video_start_selector);
            } else {
                mVideoView.setVideoPath(mList.get(mMyPosition + 1).getUrl());
                controllerVideoName.setText(mList.get(mMyPosition + 1).getName());
                mMyPosition++;
            }
        }
    }

    /**
     * 监听seekbar动作的内部类
     */
    class MyOnTimeSeekBarChangeListener implements OnSeekBarChangeListener {

        //当用户将seekbar滑动到这个位置时就是调用这个方法，此时fromUser的值为真
        //当是自动更新的状态时fromUser的值false
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mVideoView.seekTo(progress);
            }
        }
        //当手指点击时的位置
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //手指触碰时取消发送消息
            mHandler.removeMessages(MESSAGE);
        }
        //当手指点击结束时的位置
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //手指离开时重新发送消息
            mHandler.sendEmptyMessageDelayed(MESSAGE, 4000);
        }
    }

    /**
     * 监听手势动作的内部类
     */
    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public void onLongPress(MotionEvent e) {
            //长按屏幕
            setPauseAndStart();
            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            //设置全屏或者是半屏
            setDefaultAndFull();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //单击屏幕
            if (isHidden) {
                showController();
                mHandler.sendEmptyMessageDelayed(MESSAGE, 4000);
            } else {
                hiddenController();
                mHandler.removeMessages(MESSAGE);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    /**
     * 实现视频声音控制的seekbar的动作
     */
    class MyOnVoiceSeekBarChangeListener implements OnSeekBarChangeListener{
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser){
                updateVoice(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeMessages(MESSAGE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.sendEmptyMessageDelayed(MESSAGE,4000);
        }
    }
}
