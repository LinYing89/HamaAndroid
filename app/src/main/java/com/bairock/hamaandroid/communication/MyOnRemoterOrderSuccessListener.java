package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.remote.StudyKeyActivity;
import com.bairock.iot.intelDev.device.remoter.RemoterContainer;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;

public class MyOnRemoterOrderSuccessListener implements RemoterContainer.OnRemoterOrderSuccessListener {

    @Override
    public void onRemoterOrderSuccess(RemoterKey remoterKey) {
        if(null != StudyKeyActivity.remoterKey){
            if(null != StudyKeyActivity.handler) {
                StudyKeyActivity.handler.obtainMessage().sendToTarget();
            }
        }
    }
}