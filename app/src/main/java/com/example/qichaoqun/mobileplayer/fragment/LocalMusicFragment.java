package com.example.qichaoqun.mobileplayer.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qichaoqun.mobileplayer.base.BasePager;

@SuppressLint("ValidFragment")
public class LocalMusicFragment extends Fragment {

    private BasePager mBasePager = null;
    public LocalMusicFragment(BasePager basePager){
        mBasePager = basePager;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(mBasePager != null){
            return mBasePager.mView;
        }
        return null;    }
}
