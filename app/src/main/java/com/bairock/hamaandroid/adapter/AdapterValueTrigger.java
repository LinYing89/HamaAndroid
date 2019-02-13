package com.bairock.hamaandroid.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.hamaandroid.database.ValueTriggerDao;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;

import java.util.List;

public class AdapterValueTrigger extends RecyclerView.Adapter<AdapterValueTrigger.ViewHolder>{

    private LayoutInflater mInflater;
    private List<ValueTrigger> listValueTrigger;

    public AdapterValueTrigger(Context context, List<ValueTrigger>listValueTrigger){
        mInflater = LayoutInflater.from(context);
        this.listValueTrigger = listValueTrigger;
    }

    @NonNull
    @Override
    public AdapterValueTrigger.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_linkage_holder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterValueTrigger.ViewHolder viewHolder, int i) {
        viewHolder.setData(listValueTrigger.get(i));
    }

    @Override
    public int getItemCount() {
        return listValueTrigger.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView){
            super(itemView);
        }

        private ValueTrigger valueTrigger = null;
        private TextView txtName = itemView.findViewById(R.id.txtLinkageName);
        private Switch switchEnable = itemView.findViewById(R.id.switchEnable);

        private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                valueTrigger.setEnable(b);
                ValueTriggerDao.get(HamaApp.HAMA_CONTEXT).update(valueTrigger);
            }
        };

        public void setData(ValueTrigger valueTrigger) {
            this.valueTrigger = valueTrigger;
            init();
        }

        private void init() {
            txtName.setText(valueTrigger.getName());
            switchEnable.setChecked(valueTrigger.isEnable());
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }
}
