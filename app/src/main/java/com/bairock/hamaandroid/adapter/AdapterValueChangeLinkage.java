package com.bairock.hamaandroid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bairock.hamaandroid.R;
import com.bairock.iot.intelDev.device.alarm.DevAlarm;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;

import java.util.List;

public class AdapterValueChangeLinkage extends RecyclerView.Adapter<AdapterValueChangeLinkage.ViewHolder>{

    private LayoutInflater mInflater;
    private List<DeviceLinkage> listDeviceLinkage;

    public AdapterValueChangeLinkage(Context context, List<DeviceLinkage> listDeviceLinkage){
        mInflater = LayoutInflater.from(context);
        this.listDeviceLinkage = listDeviceLinkage;
    }

    @NonNull
    @Override
    public AdapterValueChangeLinkage.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_value_change_device_linkage, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterValueChangeLinkage.ViewHolder viewHolder, int i) {
        viewHolder.setData(listDeviceLinkage.get(i));
    }

    @Override
    public int getItemCount() {
        return listDeviceLinkage.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView){
            super(itemView);
        }
        private DeviceLinkage deviceLinkage = null;
        private TextView txtInfo = itemView.findViewById(R.id.txtInfo);


        public void setData(DeviceLinkage deviceLinkage) {
            this.deviceLinkage = deviceLinkage;
            init();
        }

        private void init() {
            txtInfo.setText(getInfo());
        }

        private String getInfo() {
            String info = "";
            if(deviceLinkage.getSourceDevice() instanceof DevAlarm){
                info += "目标设备: " + deviceLinkage.getTargetDevice().getName() + "\n";
            }else if(deviceLinkage.getSourceDevice() instanceof DevCollect){
                String action1;
                String action2;
                if(deviceLinkage.getSwitchModel() == 1){
                    action1 = "开";
                    action2 = "关";
                }else{
                    action1 = "关";
                    action2 = "开";
                }
                info += "目标设备: " + deviceLinkage.getTargetDevice().getName() + "\n" +
                        "小于" + deviceLinkage.getValue1() + action1 + "\n" +
                        "大于" + deviceLinkage.getValue2() + action2;
            }
            return info;
        }
    }
}
