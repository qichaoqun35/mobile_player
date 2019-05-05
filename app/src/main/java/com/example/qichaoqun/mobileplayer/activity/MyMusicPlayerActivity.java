package com.example.qichaoqun.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.qichaoqun.mobileplayer.IMyMusicService;
import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.service.MyMusicService;
import com.example.qichaoqun.mobileplayer.utils.SetSharedPreference;
import com.example.qichaoqun.mobileplayer.utils.Utils;

public class MyMusicPlayerActivity extends Activity implements View.OnClickListener {

    public static final int HANDLER_MESSAGE = 1;
    private IMyMusicService mService;
    private int mPosition;
    private TextView musicName;
    private TextView musicArtist;
    private SeekBar musicPlayerSeekbar;
    private TextView musicPlayerTime;
    private Button musicPlayerMode;
    private Button musicPlayerPre;
    private Button musicPlayerPause;
    private Button musicPlayerNext;
    private Button musicPlayerLyric;
    private Utils mUtils = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_palyer_layout);
        //获取上文传递过来的参数
        getContextData();
        //初始化控件和设置监听事件
        findViews();
        //绑定服务
        myBindService();
        //接收广播
        receiverBorderCast();
    }


    /**
     * 用于获取上文传递的数据
     */
    private void getContextData() {
        Intent intent = getIntent();
        mPosition = intent.getIntExtra("position", 0);
        mUtils = new Utils();
    }

    private void findViews() {
        musicName = (TextView) findViewById(R.id.music_name);
        musicArtist = (TextView) findViewById(R.id.music_artist);
        musicPlayerSeekbar = (SeekBar) findViewById(R.id.music_player_seekbar);
        musicPlayerTime = (TextView) findViewById(R.id.music_player_time);
        musicPlayerMode = (Button) findViewById(R.id.music_player_mode);
        musicPlayerPre = (Button) findViewById(R.id.music_player_pre);
        musicPlayerPause = (Button) findViewById(R.id.music_player_pause);
        musicPlayerNext = (Button) findViewById(R.id.music_player_next);
        musicPlayerLyric = (Button) findViewById(R.id.music_player_lyric);

        musicPlayerMode.setOnClickListener(this);
        musicPlayerPre.setOnClickListener(this);
        musicPlayerPause.setOnClickListener(this);
        musicPlayerNext.setOnClickListener(this);
        musicPlayerLyric.setOnClickListener(this);
        //设置seekbar的可以拖动更新进度
        musicPlayerSeekbar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    @Override
    public void onClick(View v) {
        if (v == musicPlayerMode) {
            //设置播放模式
            setPlayerMode();
        } else if (v == musicPlayerPre) {
            //播放上一个音频
            try {
                mService.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == musicPlayerPause) {
            //暂停音乐的播放
            if (mService != null) {
                try {
                    if (mService.isPlaying()) {
                        //如果当前音乐正在播放
                        mService.pause();
                        musicPlayerPause.setBackgroundResource(R.drawable.music_palyer_start_paly_selector);
                    } else {
                        //如果当前音乐没有播放
                        mService.start();
                        musicPlayerPause.setBackgroundResource(R.drawable.music_palyer_pause_paly_selector);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == musicPlayerNext) {
            //播放下一个音频
            try {
                mService.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == musicPlayerLyric) {
            // Handle clicks for musicPlayerLyric
        }
    }

    /**
     * 获取播放的模式，并且设置播放的模式
     */
    private void setPlayerMode() {
        try {
            //获取当前的播放模式
            int playMode = mService.getPlayMode();
            //如果当前是顺序播放
            if (playMode == MyMusicService.NORMAL) {
                //设置为单曲循环
                playMode = MyMusicService.SINGLE;
            } else if (playMode == MyMusicService.SINGLE) {
                //如果是单曲循环则设置为随机播放
                playMode = MyMusicService.ALL;
            } else if (playMode == MyMusicService.ALL) {
                //如果是随机播放则设置为顺序播放
                playMode = MyMusicService.NORMAL;
            } else {
                //以上都不是则设置为默认的顺序播放
                playMode = MyMusicService.NORMAL;
            }
            //更新播放的状态
            mService.setPlayMode(playMode);
            //更新播放图标
            setPlayerModeImag(playMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void setPlayerModeImag(int playMode) {
        //如果当前是顺序播放
        if (playMode == MyMusicService.NORMAL) {
            musicPlayerMode.setBackgroundResource(R.drawable.music_palyer_normal_player_selector);
        } else if (playMode == MyMusicService.SINGLE) {
            musicPlayerMode.setBackgroundResource(R.drawable.music_palyer_single_player_selector);
        } else if (playMode == MyMusicService.ALL) {
            musicPlayerMode.setBackgroundResource(R.drawable.music_palyer_all_player_selector);
        } else {
            musicPlayerMode.setBackgroundResource(R.drawable.music_palyer_normal_player_selector);
        }
    }

    /**
     * 用于绑定服务
     */
    private void myBindService() {
        Intent intent = new Intent(this, MyMusicService.class);
        intent.setAction("android.qichaoqun.action.RESPOND_VIA_MESSAGE");
        bindService(intent, con, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    private ServiceConnection con = new ServiceConnection() {
        //当服务绑定成功的时候回回调这个方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //获取AIDL的实体对象，用于操作service中的方法
            mService = IMyMusicService.Stub.asInterface(iBinder);
            if (mService != null) {
                try {
                    mService.openMusic(mPosition);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                if (mService != null) {
                    mService.stop();
                    mService = null;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 注册广播并且接收广播
     */
    private void receiverBorderCast() {
        //注册广播
        MyBorderCast myBorderCast = new MyBorderCast();
        //设置接收的广播的类型
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyMusicService.BORDERCAST);
        registerReceiver(myBorderCast, intentFilter);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_MESSAGE:
                    try {
                        //更新进度条
                        musicPlayerTime.setText(mUtils.stringForTime(mService.getCurrentPosition())
                                + "/" + mUtils.stringForTime(mService.getDuration()));
                        //设置进度条的当前进度
                        musicPlayerSeekbar.setProgress(mService.getCurrentPosition());
                        //循环发送消息更新进度条,并且移除以往的消息重新发送
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE, 1000);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * 接收广播的处理
     */
    public class MyBorderCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mService != null) {
                try {
                    //设置播放音频的名称和演唱者
                    musicName.setText(mService.getName());
                    musicArtist.setText(mService.getArtist());
                    //设置进度条的最大值
                    musicPlayerSeekbar.setMax(mService.getDuration());
                    //发消息更新进度条
                    mHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE, 1000);
                    //设置播放的模式
                    int playMode = SetSharedPreference.getPlayerMode(context);
                    mService.setPlayMode(playMode);
                    setPlayerModeImag(playMode);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 设置seekbar的拖动
     */
    public class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    seekBar.setProgress(progress);
                    mService.setCurrentProgress(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

}
