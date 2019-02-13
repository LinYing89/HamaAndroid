package com.bairock.hamaandroid.zview;

import android.content.Context;

import com.bairock.iot.intelDev.device.remoter.RemoterKey;

public class DragRemoterKeyButton extends RemoterKeyButton {

    public DragRemoterKeyButton(Context context) {
        super(context);
        RemoterKey remoterKey = new RemoterKey();
        remoterKey.setLocationX(10);
        remoterKey.setLocationY(10);
        setRemoterKey(remoterKey);
    }

    @Override
    public CharSequence getText() {
        return getRemoterKey().getName();
    }

    public void layoutBtn(){
        int localX = getRemoterKey().getLocationX();
        int localY = getRemoterKey().getLocationY();
        layout(localX, localY, localX + getWidth(), localY + getHeight());
    }
}
