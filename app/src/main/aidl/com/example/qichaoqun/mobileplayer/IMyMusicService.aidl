// IMyMusicService.aidl
package com.example.qichaoqun.mobileplayer;

// Declare any non-default types here with import statements

interface IMyMusicService {
    /**
        * 打开音乐
        */
       void openMusic(int position);

       /**
        * 播放音乐
        */
       void start();
       /**
        * 暂停音乐
        */
       void pause();

       /**
        * 停止播放音乐
        */
       void stop();
       /**
        * 得到当前播放的进度
        */
       int getCurrentPosition();
       /**
        * 得到音频的总时长
        */
       int getDuration();
       /**
        * 得到演唱者
        */
       String getArtist();
       /**
        * 得到歌曲的名字
        */
       String getName();
       /**
        * 得到歌曲的播放的路径
        */
       String getMusicUrl();
       /**
        * 播放下一个音乐
        */
       void next();
       /**
        * 播放前一个
        */
       void pre();
       /**
        * 设置播放模式
        */
       void setPlayMode(int playMode);
       /**
        * 得到当前播放的模式
        */
       int getPlayMode();
       /**
        * 判断是否在播放音频
        */
       boolean isPlaying();
       /**
            * 设置当前音乐的播放进度
            */
       void setCurrentProgress(int position);

}
