package com.example.qichaoqun.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.qichaoqun.mobileplayer.base.BasePager;

public class InternetMusic extends BasePager {
    public TextView mTextView;

    public InternetMusic(Context context) {
        super(context);
    }

    @Override
    public View inintView() {
        mTextView = new TextView(context);
        mTextView.setTextSize(25);
        mTextView.setTextColor(Color.RED);
        mTextView.setGravity(Gravity.CENTER);
        return mTextView;
    }

    @Override
    public void inintDate() {
        super.inintDate();
        //mTextView.setText("本地视频");
    }
}
