package com.example.qichaoqun.mobileplayer.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.qichaoqun.mobileplayer.R;

/**
 * 相当于是自定义的组件，让布局去实现
 * 从而达到得到布局，从而监听布局中的方法
 * 该思维是逆向思维
 * 一般是先定义控件，再去使用
 * 该方法是先去使用，再去定义控件
 * 从而达到监听布局中的控件的效果
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private View search;
    private View game;
    private View history;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //当布局加载完成的时候，设置组件的监听事件
        //注意组件列表是从0开始的
        search = getChildAt(1);
        game = getChildAt(2);
        history = getChildAt(3);

        //获取组件并且，设置监听事件
        search.setOnClickListener(this);
        game.setOnClickListener(this);
        history.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //布局中的id
            case R.id.video_search:
                Toast.makeText(mContext,"搜索",Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_game:
                Toast.makeText(mContext,"游戏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.video_history:
                Toast.makeText(mContext,"历史记录",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
