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
import android.widget.TextView;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.settings.DevCollectSettingActivity;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterCollect extends RecyclerView.Adapter<RecyclerAdapterCollect.ViewHolder> {

    public static final int STATE = 0;
    public static final int VALUE = 1;
    public static final int NAME = 2;
    public static final int SRC_NAME = 4;
    public static final int SIMULATOR = 5;
    public static final int SYMBOL = 6;

    public MyHandler handler;

    private LayoutInflater mInflater;
    private List<DevCollect> listDevice;
    private List<ViewHolder> listViewHolder;
    private static int colorNoraml;
    private Context context;

    public RecyclerAdapterCollect(Context context, List<DevCollect> listDevice) {
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        this.context = context;
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
        ViewHolder vh = new ViewHolder(mInflater.inflate(getLayout(), parent, false), context);
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    private int getLayout(){
        if(Config.ins().getDevShowStyle().equals("0")){
            return R.layout.adapter_collect;
        }else{
            return R.layout.adapter_collect_list;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private DevCollect device;
        private TextView textName;
        private TextView textState;
        private TextView textSymbol;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            textName = itemView.findViewById(R.id.txtName);
            textName.setSelected(true);
            textState = itemView.findViewById(R.id.txtValue);
            textSymbol = itemView.findViewById(R.id.txtSymbol);
        }

        public void setData(DevCollect device) {
            this.device = device;
            init();
            textName.setOnClickListener(view -> {
                DevCollectSettingActivity.devCollectSignal = (DevCollectSignal) device;
                context.startActivity(new Intent(context, DevCollectSettingActivity.class));
            });
        }

        private void init() {
            refreshName();
            refreshValue();
            refreshState();
            refreshSymbol();
        }

        private void refreshValue() {
            if (device.getCollectProperty().getCollectSrc() == CollectSignalSource.SWITCH) {
                if (device.getCollectProperty().getCurrentValue() != null) {
                    if (device.getCollectProperty().getCurrentValue() == 1) {
                        textState.setText("开");
                    } else {
                        textState.setText("关");
                    }
                } else {
                    textState.setText("?");
                }
            } else {
                textState.setText(String.valueOf(device.getCollectProperty().getCurrentValue()));
            }
        }

        private void refreshState() {
            if (!device.isNormal()) {
                textName.setTextColor(HamaApp.abnormalColorId);
            } else {
                textName.setTextColor(colorNoraml);
            }
        }

        private void refreshSymbol() {
            textSymbol.setText(device.getCollectProperty().getUnitSymbol());
        }

        private void refreshName() {
            if(Config.ins().getDevNameShowStyle().equals("0")) {
                textName.setText(device.getName());
            }else{
                textName.setText(device.getAlias());
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<RecyclerAdapterCollect> mActivity;

        MyHandler(RecyclerAdapterCollect activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterCollect theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case STATE:
                            vh.refreshState();
                            break;
                        case VALUE:
                            vh.refreshValue();
                            break;
                        case NAME:
                            vh.refreshName();
                            break;
                        case SYMBOL:
                            vh.refreshSymbol();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
