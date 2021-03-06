package com.bairock.hamaandroid.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.bairock.hamaandroid.database.EffectDao;
import com.bairock.iot.intelDev.device.DevStateHelper;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.linkage.Effect;

import java.util.List;

public class RecyclerAdapterEffect extends RecyclerView.Adapter<RecyclerAdapterEffect.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Effect> listEffect;

    public RecyclerAdapterEffect(Context context, List<Effect> listEffect, boolean showSwitch) {
        this.mInflater = LayoutInflater.from(context);
        this.listEffect = listEffect;
    }

    @Override
    public int getItemCount() {
        return listEffect == null ? 0 : listEffect.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_list_effect, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(listEffect.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private Effect effect;
        private TextView txtDevice;
        private TextView txtValue;

        ViewHolder(View itemView){
            super(itemView);
            txtDevice = itemView.findViewById(R.id.txtDevice);
            txtValue  = itemView.findViewById(R.id.txtValue);
        }

        public void setData(Effect effect) {
            this.effect = effect;
            init();
        }

        private void init(){
            if(effect.getDevice().isDeleted()){
                txtDevice.setTextColor(Color.RED);
            }else{
                txtDevice.setTextColor(Color.BLACK);
            }
            txtDevice.setText(effect.getDevice().getName());
            String value = "";
            if(effect.getDevice() instanceof IStateDev){
                if(effect.getDsId().equals(DevStateHelper.DS_GUAN)){
                    value = "关";
                }else{
                    value = "开";
                }
            }else {
                value = effect.getEffectContent();
            }
            txtValue.setText(value);
        }

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    effect.setDsId(DevStateHelper.DS_KAI);
                }else{
                    effect.setDsId(DevStateHelper.DS_GUAN);
                }
                EffectDao effectDao = EffectDao.get(HamaApp.HAMA_CONTEXT);
                effectDao.update(effect, null);
            }
        };
    }
}
