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
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.settings.DevCollectSettingActivity;
import com.bairock.hamaandroid.settings.DevVirtualSettingActivity;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.virtual.DevParam;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapterVirtual extends RecyclerView.Adapter<RecyclerAdapterVirtual.ViewHolder> {

    public static final int VALUE = 1;
    public static final int NAME = 2;

    public RecyclerAdapterVirtual.MyHandler handler;

    private LayoutInflater mInflater;
    private List<DevParam> listDevice;
    private List<RecyclerAdapterVirtual.ViewHolder> listViewHolder;
    private Context context;

    public RecyclerAdapterVirtual(Context context, List<DevParam> listDevice) {
        this.mInflater = LayoutInflater.from(context);
        this.listDevice = listDevice;
        this.context = context;
        listViewHolder = new ArrayList<>();
        handler = new RecyclerAdapterVirtual.MyHandler(this);
    }

    @Override
    public int getItemCount() {
        return listDevice == null ? 0 : listDevice.size();
    }

    @NonNull
    @Override
    public RecyclerAdapterVirtual.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAdapterVirtual.ViewHolder vh = new RecyclerAdapterVirtual.ViewHolder(mInflater.inflate(getLayout(), parent, false), context);
        listViewHolder.add(vh);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterVirtual.ViewHolder holder, int position) {
        holder.setData(listDevice.get(position));
    }

    private int getLayout(){
        return R.layout.adapter_virtual_list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Context context;
        private DevParam device;
        private TextView textName;
        private TextView textValue;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            textName = itemView.findViewById(R.id.txtName);
            textName.setSelected(true);
            textValue = itemView.findViewById(R.id.txtValue);
        }

        public void setData(DevParam device) {
            this.device = device;
            init();
            textName.setOnClickListener(view -> {
                DevVirtualSettingActivity.device = device;
                context.startActivity(new Intent(context, DevVirtualSettingActivity.class));});
        }

        private void init() {
            refreshName();
            refreshValue();
        }

        private void refreshValue() {
            textValue.setText(device.getValue());
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
        WeakReference<RecyclerAdapterVirtual> mActivity;

        MyHandler(RecyclerAdapterVirtual activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            RecyclerAdapterVirtual theActivity = mActivity.get();
            Device dev = (Device) msg.obj;
            for (RecyclerAdapterVirtual.ViewHolder vh : theActivity.listViewHolder) {
                if (vh.device == dev) {
                    switch (msg.what) {
                        case VALUE:
                            vh.refreshValue();
                            break;
                        case NAME:
                            vh.refreshName();
                            break;
                    }
                    break;
                }
            }
        }
    }
}
