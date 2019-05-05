package com.example.qichaoqun.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * 自定义videoview控件
 */
public class MyViewVideo extends VideoView {
    public MyViewVideo(Context context) {
        this(context,null);
    }

    public MyViewVideo(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyViewVideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 实现系统的onMeuser方法
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    /**
     *设置视频的宽和高
     */
    public void setHeightAndWidth(int width,int height){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }


}
