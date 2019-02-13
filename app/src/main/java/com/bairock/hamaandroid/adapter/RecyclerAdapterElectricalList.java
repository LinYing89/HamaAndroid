package com.bairock.hamaandroid.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.media.Media;
import com.bairock.hamaandroid.settings.DevSwitchAttributeSettingActivity;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;
import com.bairock.iot.intelDev.device.IStateDev;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterElectricalList extends RecyclerView.Adapter<RecyclerAdapterElectricalList.ViewHolder>{

    private Context context;

    public static final int AUTO = 0;
    public static final int GEAR_NEED_TO_AUTO = 1;
    public static final int STATE = 2;
    public static final int NAME = 3;

    public static int colorNormal = 0;
    public static int colorOn = 0;
    public static int colorGear = Color.parseColor("#1E90FF");
    public static int colorGearNot = Color.BLACK;
    public static MyHandler handler = null;

    private LayoutInflater mInflater = null;
    private List<Device> listDevice;
    private List<ViewHolder> listViewHolder = new ArrayList<>();

    public RecyclerAdapterElectricalList(Context context, List<Device> listDevice){
        mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        handler = new MyHandler(this);
        colorNormal = ContextCompat.getColor(context, R.color.back_fort);
        colorOn = ContextCompat.getColor(context, R.color.state_kai);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder vh = new ViewHolder(mInflater.inflate(R.layout.adapter_electrical_list, viewGroup, false), context);
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(listDevice.get(i));
    }

    @Override
    public int getItemCount() {
        return listDevice.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView, Context context){
            super(itemView);
            textName.setOnClickListener(view -> {
                if (DevSwitchAttributeSettingActivity.device == null) {
                    DevSwitchAttributeSettingActivity.device = device;
                    context.startActivity(new Intent(context, DevSwitchAttributeSettingActivity.class));
                }
            });
            txtGearToAuto.setOnClickListener(view ->{device.setGear(Gear.ZIDONG);});
            btnOn.setOnClickListener(view -> {
                startAnim(view);
                device.setGear(Gear.KAI);
                refreshGear();
                HamaApp.sendOrder(device, ((IStateDev)device).getTurnOnOrder(), true);
            });
            btnAuto.setOnClickListener(view -> {
                startAnim(view);
                device.setGear(Gear.ZIDONG);
                refreshGear();
            });
            btnOff.setOnClickListener(view -> {
                startAnim(view);
                device.setGear(Gear.GUAN);
                refreshGear();
                HamaApp.sendOrder(device, ((IStateDev)device).getTurnOffOrder(), true);
            });
        }

        Device device;
        private View viewRoot = itemView;
        private TextView textName = itemView.findViewById(R.id.txtName);
        private TextView txtGearToAuto = itemView.findViewById(R.id.txtGearToAuto);
        private Button btnOn = itemView.findViewById(R.id.btnOn);
        private Button btnAuto = itemView.findViewById(R.id.btnAuto);
        private Button btnOff = itemView.findViewById(R.id.btnOff);

        private void startAnim(View v){
            Animation animation = AnimationUtils.loadAnimation(HamaApp.HAMA_CONTEXT, R.anim.ele_btn_state_zoomin);
            v.startAnimation(animation);
            if (Config.ins().isCtrlRing()) {
                Media.get().playCtrlRing();
            }
        }

        public void setData(Device device) {
            this.device = device;
            init();
        }

        private void init() {
            refreshName();
            refreshState();
            refreshGear();
            refreshGearToAuto();
        }

        private void refreshState() {
            if (!device.isNormal()) {
                textName.setTextColor(HamaApp.abnormalColorId);
            } else {
                textName.setTextColor(colorNormal);
                if(device.isKaiState()){
                    //btnOn.setBackgroundResource(R.drawable.sharp_btn_switch_on)
                    viewRoot.setBackgroundColor(colorOn);
                }else{
                    viewRoot.setBackgroundColor(Color.TRANSPARENT);
                    //btnOn.setBackgroundResource(R.drawable.sharp_btn_switch_off)
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
            if (device.isGearNeedToAuto()) {
                txtGearToAuto.setVisibility(View.VISIBLE);
            } else {
                txtGearToAuto.setVisibility(View.GONE);
            }
        }

        private void refreshGear(){
            switch (device.getGear()){
                case KAI:
                    btnOn.setTextColor(colorGear);
                    btnAuto.setTextColor(colorGearNot);
                    btnOff.setTextColor(colorGearNot);
                    break;
                case GUAN:
                    btnOn.setTextColor(colorGearNot);
                    btnAuto.setTextColor(colorGearNot);
                    btnOff.setTextColor(colorGear);
                    break;
                    default:
                        btnOn.setTextColor(colorGearNot);
                        btnAuto.setTextColor(colorGear);
                        btnOff.setTextColor(colorGearNot);
                        break;
            }
        }
    }

    public static class MyHandler extends Handler{
        WeakReference<RecyclerAdapterElectricalList> mActivity;

        public MyHandler(RecyclerAdapterElectricalList activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterElectricalList theActivity = mActivity.get();
            Device dev = (Device)msg.obj;
            for (ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what){
                        case AUTO : vh.refreshGear(); break;
                        case STATE : vh.refreshState(); break;
                        case NAME : vh.refreshName(); break;
                        case GEAR_NEED_TO_AUTO : vh.refreshGearToAuto(); break;
                    }
                    break;
                }
            }
        }
    }
}
