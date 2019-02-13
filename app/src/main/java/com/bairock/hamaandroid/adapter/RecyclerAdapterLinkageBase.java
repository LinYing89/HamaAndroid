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
import com.bairock.hamaandroid.database.LinkageDao;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageHolder;

public class RecyclerAdapterLinkageBase extends RecyclerView.Adapter<RecyclerAdapterLinkageBase.ViewHolder>{

    private LayoutInflater mInflater;
    private LinkageHolder linkageHolder;

    public RecyclerAdapterLinkageBase(Context context, LinkageHolder linkageHolder){
        mInflater = LayoutInflater.from(context);
        this.linkageHolder = linkageHolder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(mInflater.inflate(R.layout.adapter_linkage_holder, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setData(linkageHolder.getListLinkage().get(i));
    }

    @Override
    public int getItemCount() {
        return (linkageHolder.getListLinkage() == null) ? 0 : linkageHolder.getListLinkage().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView){
            super(itemView);
        }

        private Linkage linkage = null;
        private TextView txtLinkageName = itemView.findViewById(R.id.txtLinkageName);
        private Switch switchEnable = itemView.findViewById(R.id.switchEnable);

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (compoundButton, b) -> {
            linkage.setEnable(b);
            LinkageDao linkageDevValueDao = LinkageDao.get(HamaApp.HAMA_CONTEXT);
            linkageDevValueDao.update(linkage, null);
        };

        public void setData(Linkage linkage) {
            this.linkage = linkage;
            init();
        }

        private void init() {
            txtLinkageName.setText(linkage.getName());
            switchEnable.setChecked(linkage.isEnable());
            switchEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        }
    }
}
