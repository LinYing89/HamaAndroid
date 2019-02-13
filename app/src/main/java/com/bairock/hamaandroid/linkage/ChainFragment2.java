package com.bairock.hamaandroid.linkage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.SubChain;

import java.lang.ref.WeakReference;

public class ChainFragment2 extends LinkageBaseFragment{

    public static ChainFragment2 newInstance(int param1) {
        ChainFragment2 fragment = new ChainFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initLinkageHolder() {
        linkageHolder = HamaApp.DEV_GROUP.getChainHolder();
    }

    @Override
    public void initHandler() {
        handler = new MyHandler(this);
    }

    @Override
    public Linkage addNewLinkage(String name) {
        SubChain subChain = new SubChain();
        subChain.setName(name);
        linkageHolder.addLinkage(subChain);
        return subChain;
    }

    @Override
    public void toLinkageActivity() {
        this.getActivity().startActivity(new Intent(this.getActivity(), EditChainActivity.class));
    }

    static class MyHandler extends Handler {

        WeakReference<ChainFragment2> mActivity;
        public MyHandler(ChainFragment2 activity){
            mActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            ChainFragment2 theActivity = mActivity.get();
            if(msg.what == REFRESH_LIST){
                theActivity.adapterChain.notifyDataSetChanged();
            }
        }
    }
}
