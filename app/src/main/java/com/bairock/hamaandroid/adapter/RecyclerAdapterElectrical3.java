package com.bairock.hamaandroid.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bairock.hamaandroid.app.DragRemoterActivity;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.media.Media;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.settings.DevSwitchAttributeSettingActivity;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.device.remoter.Remoter;
import com.bairock.iot.intelDev.order.DeviceOrder;
import com.bairock.iot.intelDev.order.OrderType;
import com.bairock.iot.intelDev.user.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterElectrical3 extends RecyclerView.Adapter<RecyclerAdapterElectrical3.ViewHolder> {

    public static final int AUTO = 0;
    public static final int GEAR_NEED_TO_AUTO = 1;
    public static final int STATE = 2;
    public static final int NAME = 3;

    static int colorNoraml;
    private Context context;

    public MyHandler handler;

    private LayoutInflater mInflater;
    private List<Device> listDevice;
    private List<ViewHolder> listViewHolder;

    public RecyclerAdapterElectrical3(Context context, List<Device> listDevice) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        listViewHolder = new ArrayList<>();
        handler = new MyHandler(this);
        colorNoraml = context.getResources().getColor(R.color.back_fort);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewHolder vh = new ViewHolder(mInflater.inflate(R.layout.adapter_electrical_card, parent, false), context);
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Device device;
        private TextView textName;
        private TextView txtGearToAuto;
        private Button btnState;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            txtGearToAuto = itemView.findViewById(R.id.txtGearToAuto);
            txtGearToAuto.setOnClickListener(v -> device.setGear(Gear.ZIDONG));
            textName.setSelected(true);
            textName.setOnClickListener(view -> {
                if(DevSwitchAttributeSettingActivity.device == null) {
                    DevSwitchAttributeSettingActivity.device = device;
                    context.startActivity(new Intent(context, DevSwitchAttributeSettingActivity.class));
                }
            });
            btnState = itemView.findViewById(R.id.btnState);
            btnState.setOnClickListener(view -> {
                Animation animation= AnimationUtils.loadAnimation(HamaApp.HAMA_CONTEXT,R.anim.ele_btn_state_zoomin);
                view.startAnimation(animation);
                if(Config.ins().isCtrlRing()) {
                    Media.get().playCtrlRing();
                }
                if(device instanceof Remoter){
                    DragRemoterActivity.REMOTER = (Remoter)device;
                    context.startActivity(new Intent(context, DragRemoterActivity.class));
                    return;
                }
                IStateDev dev = (IStateDev) device;
                switch (device.getGear()) {
                    case UNKNOW:
                    case ZIDONG:
                        if (device.isKaiState()) {
                            toGuanGear();
                            HamaApp.sendOrder(device, dev.getTurnOffOrder(), OrderType.CTRL_DEV, true);
                        } else {
                            toKaiGear();
                            HamaApp.sendOrder(device, dev.getTurnOnOrder(), OrderType.CTRL_DEV, true);
                        }
                        break;
                    case KAI:
                        toGuanGear();
                        HamaApp.sendOrder(device, dev.getTurnOffOrder(), OrderType.CTRL_DEV, true);
                        break;
                    default:
                        toKaiGear();
                        HamaApp.sendOrder(device, dev.getTurnOnOrder(), OrderType.CTRL_DEV, true);
                        break;
                }
            });
        }

        public void setData(Device device) {
            this.device = device;
            if(device instanceof Remoter){
                txtGearToAuto.setVisibility(View.GONE);
                btnState.setText("");
            }
            init();
        }

        private void init() {
            refreshName();
            refreshState();
            if(!(device instanceof Remoter)){
                refreshBntStateText(device.getGear());
                refreshGearToAuto();
            }
        }

        private void toKaiGear(){
            device.setGear(Gear.KAI);
        }

        private void toGuanGear(){
            device.setGear(Gear.GUAN);
        }

        private void refreshState() {
            if (!device.isNormal()) {
                textName.setTextColor(HamaApp.abnormalColorId);
            } else {
                textName.setTextColor(colorNoraml);
                refreshBtnState();
            }
        }

        private void refreshBntStateText(Gear gear) {
            switch (gear) {
                case KAI:
                    btnState.setText("O");
                    break;
                case GUAN:
                    btnState.setText("S");
                    break;
                default:
                    btnState.setText("A");
                    break;
            }
        }

        private void refreshBtnState() {
            if(!(device instanceof Remoter)) {
                if (device.isKaiState()) {
                    btnState.setBackgroundResource(R.drawable.sharp_btn_switch_on);
                } else {
                    btnState.setBackgroundResource(R.drawable.sharp_btn_switch_off);
                }
            }
        }

        private void refreshName() {
            if(Config.ins().getDevNameShowStyle().equals("0")) {
                textName.setText(device.getName());
            }else{
                textName.setText(device.getAlias());
            }
        }

        private void refreshGearToAuto() {
            if(device.isGearNeedToAuto()) {
                txtGearToAuto.setVisibility(View.VISIBLE);
            }else{
                txtGearToAuto.setVisibility(View.GONE);
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterElectrical3> mActivity;

        MyHandler(RecyclerAdapterElectrical3 activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterElectrical3 theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case AUTO:
                            vh.refreshBntStateText(dev.getGear());
                            break;
                        case STATE:
                            vh.refreshState();
                            break;
                        case NAME:
                            vh.refreshName();
                            break;
                        case GEAR_NEED_TO_AUTO:
                            vh.refreshGearToAuto();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
