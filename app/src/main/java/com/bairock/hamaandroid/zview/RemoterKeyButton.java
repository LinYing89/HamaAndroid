package com.bairock.hamaandroid.zview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.Constant;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;

public class RemoterKeyButton extends android.support.v7.widget.AppCompatButton {

    private RemoterKey remoterKey;

    public RemoterKeyButton(Context context) {
        super(context);
        init();
    }
    public RemoterKeyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    public RemoterKeyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        int width = Constant.getRemoterKeyWidth();
        setWidth(width);
        setHeight(width);
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.sharp_btn_switch_off);
        if(null != remoterKey){
            setText(remoterKey.getName());
        }
    }

    public RemoterKey getRemoterKey() {
        return remoterKey;
    }

    public void setRemoterKey(RemoterKey remoterKey) {
        this.remoterKey = remoterKey;
        if(null != remoterKey){
            setText(remoterKey.getName());
        }
    }
}
