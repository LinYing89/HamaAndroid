package com.bairock.hamaandroid.communication;

import com.bairock.hamaandroid.app.ClimateFragment;
import com.bairock.hamaandroid.app.ElectricalCtrlFragment;
import com.bairock.hamaandroid.app.WelcomeActivity;
import com.bairock.hamaandroid.settings.SearchActivity;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;

/**
 *
 * Created by 44489 on 2018/3/5.
 */

public class MyOnDevHaveChildeOnCollectionChangedListener implements DevHaveChild.OnDeviceCollectionChangedListener {
    @Override
    public void onAdded(Device device) {
        refreshUi();
        WelcomeActivity.setDeviceListener(device, new MyOnStateChangedListener(), new MyOnGearChangedListener(), new MyOnCtrlModelChangedListener());
    }

    @Override
    public void onRemoved(Device device) {
        refreshUi();
    }

    private void refreshUi(){
        if(null != SearchActivity.handler){
            SearchActivity.handler.obtainMessage(SearchActivity.handler.UPDATE_LIST).sendToTarget();
        }
        if(null != ElectricalCtrlFragment.handler){
            ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
        }
        if(null != ClimateFragment.handler){
            ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
        }
    }
}
