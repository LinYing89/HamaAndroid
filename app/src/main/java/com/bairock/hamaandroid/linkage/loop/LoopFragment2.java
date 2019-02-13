package com.bairock.hamaandroid.linkage.loop;

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
import com.bairock.iot.intelDev.linkage.loop.ZLoop;

import java.lang.ref.WeakReference;

public class LoopFragment2 extends LinkageBaseFragment {
    @Override
    public void initLinkageHolder(){
        linkageHolder = HamaApp.DEV_GROUP.getLoopHolder();
    }

    @Override
    public Linkage addNewLinkage(String name) {
        ZLoop subChain = new ZLoop();
        subChain.setName(name);
        linkageHolder.addLinkage(subChain);
        return subChain;
    }

    @Override public void toLinkageActivity() {
        this.startActivity(new Intent(this.getContext(), EditLoopActivity.class));
    }

    @Override public void initHandler() {
        handler = new MyHandler(this);
    }

    @Override public String getDefaultHeadName(){
        return "循环";
    }

    static class MyHandler extends Handler {
        private WeakReference<LoopFragment2> mActivity;
        public MyHandler(LoopFragment2 activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            LoopFragment2 theActivity = mActivity.get();
            if(msg.what == REFRESH_LIST){
                theActivity.adapterChain.notifyDataSetChanged();
            }
        }
    }

    public static LoopFragment2 newInstance(int param1){
        LoopFragment2 fragment = new LoopFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
}
