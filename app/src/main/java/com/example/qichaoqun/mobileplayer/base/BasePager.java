package com.example.qichaoqun.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 采用工厂模式进行构造
 * 让子类去实现该类
 * 视图的父类，可以调用inintView方法来获取视图
 * 也可以使用inintDate方法来加载数据
 * 实现视图和数据的分离
 */
public abstract class BasePager {
    public final Context context;
    public View mView;
    public boolean inintFlag = false;

    public BasePager(Context context){
        this.context = context;
        mView = inintView();
    }

    /**
     * 初始化并且返回视图
     * 子类必须要做的事情
     * @return 返回视图
     */
    public abstract View inintView();

    /**
     * 获取数据
     * 联网请求等
     */
    public void inintDate(){}
}
