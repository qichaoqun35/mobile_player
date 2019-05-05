package com.example.qichaoqun.mobileplayer.tools;

import android.app.Activity;

import java.util.Stack;

public class ActManger {

    /**
     * activity 管理器，只要将activity加入到 该类对象中就能管理所有的activity
     *
     */

    private static Stack<Activity> activityStack;
    private static ActManger instance;


    public static ActManger getAppManger(){
        if(instance == null){
            instance = new ActManger();
        }
        return instance;
    }

    /**
     * 初始化activity管理堆栈
     */
    private void inintActivityStack(){
        if(activityStack == null){
            activityStack = new Stack<Activity>();
        }
    }

    /**
     * 添加activity进入activity堆栈中
     */
    public void addActivity(Activity activity){
        inintActivityStack();
        activityStack.add(activity);
    }

    /**
     * 得到当前activity
     * @return
     */
    public Activity currentActivity(){
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前activity
     * @param activity
     */
    public void finishActivity(Activity activity){
        if(activity != null){
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束最近的activity
     */
    public void finishActivity(){
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 清除掉所有的堆栈中的activity
     */
    public void finishAllActivity(){
        for(int i = 0;i < activityStack.size();i++){
            if(null != activityStack.get(i)){
                Activity activity = activityStack.get(i);
                if(!activity.isFinishing()){
                    activity.finish();
                }
            }
        }
        activityStack.clear();
    }
}
