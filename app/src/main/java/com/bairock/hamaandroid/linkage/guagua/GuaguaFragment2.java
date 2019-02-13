package com.bairock.hamaandroid.linkage.guagua;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.linkage.LinkageBaseFragment;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.SubChain;

import java.lang.ref.WeakReference;

public class GuaguaFragment2 extends LinkageBaseFragment {
    @Override
    public void initLinkageHolder(){
        linkageHolder = HamaApp.DEV_GROUP.getGuaguaHolder();
    }

    @Override
    public Linkage addNewLinkage(String name) {
        SubChain subChain = new SubChain();
        subChain.setName(name);
        linkageHolder.addLinkage(subChain);
        return subChain;
    }

    @Override public void toLinkageActivity() {
        this.startActivity(new Intent(this.getContext(), EditGuaguaActivity.class));
    }

    @Override public void initHandler() {
        handler = new MyHandler(this);
    }

    @Override public String getDefaultHeadName(){
        return "呱呱";
    }

    static class MyHandler extends Handler {
        private WeakReference<GuaguaFragment2> mActivity;
        public MyHandler(GuaguaFragment2 activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            GuaguaFragment2 theActivity = mActivity.get();
            if(msg.what == REFRESH_LIST){
                theActivity.adapterChain.notifyDataSetChanged();
            }
        }
    }

    public static GuaguaFragment2 newInstance(int param1){
        GuaguaFragment2 fragment = new GuaguaFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
}
