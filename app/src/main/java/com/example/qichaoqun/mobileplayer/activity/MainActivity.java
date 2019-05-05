package com.example.qichaoqun.mobileplayer.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.qichaoqun.mobileplayer.R;
import com.example.qichaoqun.mobileplayer.base.BasePager;
import com.example.qichaoqun.mobileplayer.fragment.LocalMusicFragment;
import com.example.qichaoqun.mobileplayer.fragment.LocalVideoFragment;
import com.example.qichaoqun.mobileplayer.fragment.NetMusicFragment;
import com.example.qichaoqun.mobileplayer.fragment.NetVideoFragment;
import com.example.qichaoqun.mobileplayer.pager.InternetMusic;
import com.example.qichaoqun.mobileplayer.pager.InternetVideo;
import com.example.qichaoqun.mobileplayer.pager.LocalMusic;
import com.example.qichaoqun.mobileplayer.pager.LocalVideo;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends Activity {

    /**
     * 获取底部导航栏的监听事件
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.local_video:
                    setFragment(0);
                    return true;
                case R.id.local_music:
                    setFragment(1);
                    return true;
                case R.id.internrt_video:
                    setFragment(2);
                    return true;
                case R.id.internrt_music:
                    setFragment(3);
                    return true;
                default:
            }
            return false;
        }
    };

    private ArrayList<BasePager> mList;
    private LocalVideoFragment mLocalVideoFragment = null;
    private LocalMusicFragment mLocalMusicFragment = null;
    private NetVideoFragment mNetVideoFragment = null;
    private NetMusicFragment mNetMusicFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置底部导航栏
        setBottomNavigation();

        mList = new ArrayList<>();
        //本地视频播放的实例化对象
        mList.add(new LocalVideo(this));
        //本地音乐播放的实例化对象
        mList.add(new LocalMusic(this));
        //网络视频播放的实例化对象
        mList.add(new InternetVideo(this));
        //网络音乐播放的实例化对象
        mList.add(new InternetMusic(this));
        //开启先加载的页面
        setFragment(0);
    }

    /**
     * 创建Fragment
     *
     * @param flag 标识符，根据不同的内容区创建不同的Fragment
     *             使用此方法完美解决，导航栏闪退的问题，和视频中fragment的使用问题
     *             推荐使用下面的这种方法去动态的创建fragment
     */
    public void setFragment(int flag) {
        final int position = flag;
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //隐藏所有的fragment
        hiddenAllFragment(transaction);
        switch (flag) {
            case 0:
                if (mLocalVideoFragment == null) {
                    mLocalVideoFragment = new LocalVideoFragment(getBasePager(position));
                    transaction.add(R.id.frame_layout, mLocalVideoFragment);
                } else {
                    transaction.show(mLocalVideoFragment);
                }
                break;
            case 1:
                if (mLocalMusicFragment == null) {
                    mLocalMusicFragment = new LocalMusicFragment(getBasePager(position));
                    transaction.add(R.id.frame_layout, mLocalMusicFragment);
                } else {
                    transaction.show(mLocalMusicFragment);
                }
                break;
            case 2:
                if (mNetVideoFragment == null) {
                    mNetVideoFragment = new NetVideoFragment(getBasePager(position));
                    transaction.add(R.id.frame_layout, mNetVideoFragment);
                } else {
                    transaction.show(mNetVideoFragment);
                }
                break;
            case 3:
                if (mNetMusicFragment == null) {
                    mNetMusicFragment = new NetMusicFragment(getBasePager(position));
                    transaction.add(R.id.frame_layout, mNetMusicFragment);
                } else {
                    transaction.show(mNetMusicFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 根据位置获得相对应的页面的实例化对象
     *
     * @param position 当前页面的位置
     * @return 实例化对象
     */
    private BasePager getBasePager(int position) {
        BasePager basePager = mList.get(position);
        //加载数据，病根据标志符来判断是否再次加载数据
        //如果为false则不再加载数据，为true则会加载数据
        if (basePager != null && !basePager.inintFlag) {
            //获取实例化对象的数据，从此开始加载数据
            basePager.inintDate();
            basePager.inintFlag = true;
        }
        return basePager;
    }

    @SuppressLint("RestrictedApi")
    private void setBottomNavigation() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);

            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
                itemView.setShiftingMode(false);
                itemView.setChecked(itemView.getItemData().isChecked());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(mListener);
    }

    /**
     * 隐藏所有的fragment，用于点击底部导航栏时进行的判断
     *
     * @param fragmentTransaction fragment管理器
     */
    private void hiddenAllFragment(FragmentTransaction fragmentTransaction) {
        if (mLocalVideoFragment != null) {
            fragmentTransaction.hide(mLocalVideoFragment);
        }
        if (mLocalMusicFragment != null) {
            fragmentTransaction.hide(mLocalMusicFragment);
        }
        if (mNetVideoFragment != null) {
            fragmentTransaction.hide(mNetVideoFragment);
        }
        if (mNetMusicFragment != null) {
            fragmentTransaction.hide(mNetMusicFragment);
        }
    }
}
