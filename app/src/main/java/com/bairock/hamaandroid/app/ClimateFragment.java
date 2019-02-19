package com.bairock.hamaandroid.app;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.adapter.RecyclerAdapterCollect;
import com.bairock.hamaandroid.database.Config;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.MyHome;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public class ClimateFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    public static final int REFRESH_VALUE = 1;
    public static final int REFRESH_DEVICE = 2;
    public static final int CHANGE_SHOW_NAME_STYLE = 3;
    public static final int CHANGE_LAYOUT_MANAGER = 4;
    public static final int NOTIFY_ADAPTER = 5;

    public static MyHandler handler;

    private SwipeMenuRecyclerView swipeMenuRecyclerViewCollector;
    private RecyclerAdapterCollect adapterCollect;
    private List<DevCollect> listDevCollect;

    public ClimateFragment() {
        // Required empty public constructor
    }

    public static ClimateFragment newInstance(int param1) {
        ClimateFragment fragment = new ClimateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_climate, container, false);
        handler = new MyHandler(this);
        swipeMenuRecyclerViewCollector = view.findViewById(R.id.swipeMenuRecyclerViewCollector);
        setLayoutManager();
        //swipeMenuRecyclerViewCollector.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewCollector.setLongPressDragEnabled(true); // 长按拖拽，默认关闭。

        setListener();
        setPressueList();
        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
    }

    private void setLayoutManager(){
        if(Config.ins().getDevShowStyle().equals("0")) {
            swipeMenuRecyclerViewCollector.setLayoutManager(new GridLayoutManager(this.getContext(), 4));
            for(int i = 0; i < swipeMenuRecyclerViewCollector.getItemDecorationCount(); i++){
                swipeMenuRecyclerViewCollector.removeItemDecorationAt(i);
            }
            //swipeMenuRecyclerViewCollector.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT), 0);
        }else{
            swipeMenuRecyclerViewCollector.setLayoutManager(new LinearLayoutManager(this.getContext()));
            for(int i = 0; i < swipeMenuRecyclerViewCollector.getItemDecorationCount(); i++){
                swipeMenuRecyclerViewCollector.removeItemDecorationAt(i);
            }
            swipeMenuRecyclerViewCollector.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        }
    }

    private void setAdapter(){
        adapterCollect = new RecyclerAdapterCollect(this.getContext(), listDevCollect);
        swipeMenuRecyclerViewCollector.setAdapter(adapterCollect);
    }

    private void changeLayout(){
        setLayoutManager();
        setAdapter();
    }

    private void setListener() {
        swipeMenuRecyclerViewCollector.setOnItemMoveListener(onItemMoveListener);
        swipeMenuRecyclerViewCollector.setOnItemStateChangedListener(mOnItemStateChangedListener);
    }

    private void setPressueList() {
        listDevCollect = HamaApp.DEV_GROUP.findListCollectDev(true);
        Collections.sort(listDevCollect);
        for (int i = 0; i < listDevCollect.size(); i++) {
            Device device = listDevCollect.get(i);
            device.setSortIndex(i);
            device.addOnNameChangedListener(onNameChangedListener);
            device.addOnAliasChangedListener(onAliasChangedListener);
        }
        setAdapter();
    }

    private MyHome.OnNameChangedListener onNameChangedListener = new MyHome.OnNameChangedListener() {
        @Override
        public void onNameChanged(MyHome myHome, String s) {
            if(Config.ins().getDevNameShowStyle().equals("0") && null != adapterCollect){
                adapterCollect.notifyDataSetChanged();
            }
        }
    };

    private Device.OnAliasChangedListener onAliasChangedListener = new Device.OnAliasChangedListener() {
        @Override
        public void onAliasChanged(Device device, String s) {
            if(Config.ins().getDevNameShowStyle().equals("1") && null != adapterCollect){
                adapterCollect.notifyDataSetChanged();
            }
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = (viewHolder, actionState) -> {
        if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
            // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
            //viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(ElectricalCtrlFragment.this.getContext()), R.color.drag_background));
            Animation animation = AnimationUtils.loadAnimation(ClimateFragment.this.getContext(), R.anim.drag_zoomout);
            viewHolder.itemView.startAnimation(animation);
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
            // 在手松开的时候还原背景。
            //ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(BaseDragActivity.this, R.drawable.select_white));
            Animation animation = AnimationUtils.loadAnimation(ClimateFragment.this.getContext(), R.anim.drag_zoomin);
            viewHolder.itemView.startAnimation(animation);
        }
    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。

            int fromPosition = srcHolder.getAdapterPosition() - swipeMenuRecyclerViewCollector.getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - swipeMenuRecyclerViewCollector.getHeaderItemCount();

            if(Config.ins().getDevShowStyle().equals("0")) {
                //宫格
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        listDevCollect.get(i).setSortIndex(i + 1);
                        listDevCollect.get(i + 1).setSortIndex(i);
                        Collections.swap(listDevCollect, i, i + 1);
                        adapterCollect.notifyItemMoved(i, i+1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        listDevCollect.get(i).setSortIndex(i - 1);
                        listDevCollect.get(i - 1).setSortIndex(i);
                        Collections.swap(listDevCollect, i, i - 1);
                        adapterCollect.notifyItemMoved(i, i-1);
                    }
                }
            }else{
                //列表
                listDevCollect.get(fromPosition).setSortIndex(toPosition);
                listDevCollect.get(toPosition).setSortIndex(fromPosition);
                Collections.swap(listDevCollect, fromPosition, toPosition);
                adapterCollect.notifyItemMoved(fromPosition, toPosition);
            }
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

        }
    };

    private DevGroup.OnDeviceCollectionChangedListener onDeviceCollectionChangedListener = new DevGroup.OnDeviceCollectionChangedListener() {
        @Override
        public void onAdded(Device device) {
            addDev(device);
            //handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        }

        @Override
        public void onRemoved(Device device) {
            removeDev(device);
            //handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        }
    };

    private void addDev(Device device) {
        if (device instanceof DevCollect) {
            device.setSortIndex(listDevCollect.size());
            DevCollect devCollect = (DevCollect) device;
            listDevCollect.add(devCollect);
            handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        } else if (device instanceof DevHaveChild) {
            for (Device device1 : ((DevHaveChild) device).getListDev()) {
                addDev(device1);
            }
        }
    }

    private void removeDev(Device device) {
        if (device instanceof DevCollect) {
            DevCollect devCollect = (DevCollect) device;
            listDevCollect.remove(devCollect);
            handler.obtainMessage(REFRESH_VALUE).sendToTarget();
        } else if (device instanceof DevHaveChild) {
            for (Device device1 : ((DevHaveChild) device).getListDev()) {
                removeDev(device1);
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<ClimateFragment> mActivity;

        MyHandler(ClimateFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO handler
            final ClimateFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_VALUE:
                    if(null != theActivity.adapterCollect) {
                        theActivity.adapterCollect.notifyDataSetChanged();
                    }
                    break;
                case REFRESH_DEVICE:
                    theActivity.setPressueList();
                    break;
                case CHANGE_SHOW_NAME_STYLE:
                    theActivity.setAdapter();
                    break;
                case CHANGE_LAYOUT_MANAGER:
                    theActivity.changeLayout();
                    break;
                case NOTIFY_ADAPTER:
                    theActivity.adapterCollect.handler.obtainMessage(msg.arg1, msg.obj).sendToTarget();
            }

        }
    }
}
