package com.bairock.hamaandroid.linkage.timing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.linkage.LinkageBaseFragment;
import com.bairock.hamaandroid.linkage.guagua.EditGuaguaActivity;
import com.bairock.hamaandroid.linkage.guagua.GuaguaFragment2;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.SubChain;
import com.bairock.iot.intelDev.linkage.timing.Timing;

import java.lang.ref.WeakReference;

public class TimingFragment2 extends LinkageBaseFragment {
    @Override
    public void initLinkageHolder(){
        linkageHolder = HamaApp.DEV_GROUP.getTimingHolder();
    }

    @Override
    public Linkage addNewLinkage(String name) {
        Timing subChain = new Timing();
        subChain.setName(name);
        linkageHolder.addLinkage(subChain);
        return subChain;
    }

    @Override public void toLinkageActivity() {
        this.startActivity(new Intent(this.getContext(), EditTimingActivity.class));
    }

    @Override public void initHandler() {
        handler = new MyHandler(this);
    }

    @Override public String getDefaultHeadName(){
        return "定时";
    }

    static class MyHandler extends Handler {
        private WeakReference<TimingFragment2> mActivity;
        public MyHandler(TimingFragment2 activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            TimingFragment2 theActivity = mActivity.get();
            if(msg.what == REFRESH_LIST){
                theActivity.adapterChain.notifyDataSetChanged();
            }
        }
    }

    public static TimingFragment2 newInstance(int param1){
        TimingFragment2 fragment = new TimingFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
}
