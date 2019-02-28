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
import com.bairock.hamaandroid.adapter.RecyclerAdapterElectrical3;
import com.bairock.hamaandroid.adapter.RecyclerAdapterElectricalList;
import com.bairock.hamaandroid.database.Config;
import com.bairock.iot.intelDev.device.DevHaveChild;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.IStateDev;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.MyHome;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ElectricalCtrlFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    public static final int REFRESH_ELE_STATE = 1;
    public static final int REFRESH_ELE = 2;
    public static final int CHANGE_SHOW_NAME_STYLE = 3;
    public static final int CHANGE_LAYOUT_MANAGER = 4;
    public static final int NOTIFY_ADAPTER = 5;
    public static MyHandler handler;

    private RecyclerAdapterElectrical3 adapterElectrical;
    private RecyclerAdapterElectricalList adapterElectricalList;
    private SwipeMenuRecyclerView swipeMenuRecyclerViewElectrical;

    private List<Device> listIStateDev = new ArrayList<>();

    public ElectricalCtrlFragment() {
        // Required empty public constructor
    }

    public static ElectricalCtrlFragment newInstance(int sectionNumber2) {
        ElectricalCtrlFragment fragment = new ElectricalCtrlFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, sectionNumber2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewRoot = inflater.inflate(R.layout.fragment_electrical_ctrl, container, false);
        handler = new MyHandler(this);
        swipeMenuRecyclerViewElectrical = viewRoot.findViewById(R.id.swipeMenuRecyclerViewElectrical);
        setLayoutManager();
        setGridViewElectrical();
        swipeMenuRecyclerViewElectrical.setLongPressDragEnabled(true);
        swipeMenuRecyclerViewElectrical.setOnItemMoveListener(onItemMoveListener);
        swipeMenuRecyclerViewElectrical.setOnItemStateChangedListener(mOnItemStateChangedListener);
        HamaApp.DEV_GROUP.addOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
        return viewRoot;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler = null;
        HamaApp.DEV_GROUP.removeOnDeviceCollectionChangedListener(onDeviceCollectionChangedListener);
    }

    private void setLayoutManager(){
        if(Config.ins().getDevShowStyle().equals("0")) {
            swipeMenuRecyclerViewElectrical.setLayoutManager(new GridLayoutManager(this.getContext(), 6));
//            swipeMenuRecyclerViewElectrical.invalidateItemDecorations();
            for(int i = 0; i < swipeMenuRecyclerViewElectrical.getItemDecorationCount(); i++){
                swipeMenuRecyclerViewElectrical.removeItemDecorationAt(i);
            }
            //swipeMenuRecyclerViewElectrical.addItemDecoration(new DefaultItemDecoration(Color.TRANSPARENT));
        }else{
            swipeMenuRecyclerViewElectrical.setLayoutManager(new LinearLayoutManager(this.getContext()));
//            swipeMenuRecyclerViewElectrical.invalidateItemDecorations();
            for(int i = 0; i < swipeMenuRecyclerViewElectrical.getItemDecorationCount(); i++){
                swipeMenuRecyclerViewElectrical.removeItemDecorationAt(i);
            }
            swipeMenuRecyclerViewElectrical.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        }
    }

    private void setAdapter(){
        if(Config.ins().getDevShowStyle().equals("0")) {
            adapterElectrical = new RecyclerAdapterElectrical3(this.getContext(), listIStateDev);
            swipeMenuRecyclerViewElectrical.setAdapter(adapterElectrical);
        }else{
            adapterElectricalList = new RecyclerAdapterElectricalList(Objects.requireNonNull(this.getContext()), listIStateDev);
            swipeMenuRecyclerViewElectrical.setAdapter(adapterElectricalList);
        }
    }

    private void changeLayout(){
        setLayoutManager();
        setAdapter();
    }

    public void setGridViewElectrical() {
        if(null != HamaApp.DEV_GROUP) {
            listIStateDev = HamaApp.DEV_GROUP.findListIStateDev(true);
            Collections.sort(listIStateDev);
            for(int i = 0; i < listIStateDev.size(); i++){
                Device device = listIStateDev.get(i);
                device.setSortIndex(i);
                device.addOnNameChangedListener(onNameChangedListener);
                device.addOnAliasChangedListener(onAliasChangedListener);
            }
            setAdapter();
        }
    }

    private MyHome.OnNameChangedListener onNameChangedListener = new MyHome.OnNameChangedListener() {
        @Override
        public void onNameChanged(MyHome myHome, String s) {
            if(Config.ins().getDevNameShowStyle().equals("0") && null != adapterElectrical){
                adapterElectrical.notifyDataSetChanged();
            }
        }
    };
    private Device.OnAliasChangedListener onAliasChangedListener = new Device.OnAliasChangedListener() {
        @Override
        public void onAliasChanged(Device device, String s) {
            if(Config.ins().getDevNameShowStyle().equals("1") && null != adapterElectrical){
                adapterElectrical.notifyDataSetChanged();
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
            Animation animation= AnimationUtils.loadAnimation(ElectricalCtrlFragment.this.getContext(),R.anim.drag_zoomout);
            viewHolder.itemView.startAnimation(animation);
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
            // 在手松开的时候还原背景。
            //ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(BaseDragActivity.this, R.drawable.select_white));
            Animation animation= AnimationUtils.loadAnimation(ElectricalCtrlFragment.this.getContext(),R.anim.drag_zoomin);
            viewHolder.itemView.startAnimation(animation);

        }
    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 真实的Position：通过ViewHolder拿到的position都需要减掉HeadView的数量。

            int fromPosition = srcHolder.getAdapterPosition() - swipeMenuRecyclerViewElectrical.getHeaderItemCount();
            int toPosition = targetHolder.getAdapterPosition() - swipeMenuRecyclerViewElectrical.getHeaderItemCount();

            if(Config.ins().getDevShowStyle().equals("0")) {
                //宫格
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        listIStateDev.get(i).setSortIndex(i + 1);
                        listIStateDev.get(i + 1).setSortIndex(i);
                        Collections.swap(listIStateDev, i, i + 1);
                        adapterElectrical.notifyItemMoved(i, i+1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        listIStateDev.get(i).setSortIndex(i - 1);
                        listIStateDev.get(i - 1).setSortIndex(i);
                        Collections.swap(listIStateDev, i, i - 1);
                        adapterElectrical.notifyItemMoved(i, i-1);
                    }
                }
            }else{
                //列表
                listIStateDev.get(fromPosition).setSortIndex(toPosition);
                listIStateDev.get(toPosition).setSortIndex(fromPosition);
                Collections.swap(listIStateDev, fromPosition, toPosition);
                adapterElectricalList.notifyItemMoved(fromPosition, toPosition);
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
            //handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }

        @Override
        public void onRemoved(Device device) {
            removeDev(device);
            //handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }
    };

    private void addDev(Device device){
        if(device instanceof IStateDev && device.isVisibility()){
            device.setSortIndex(listIStateDev.size());
            listIStateDev.add(device);
            handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                addDev(device1);
            }
        }
    }

    private void removeDev(Device device){
        if(device instanceof IStateDev){
            listIStateDev.remove(device);
            handler.obtainMessage(REFRESH_ELE_STATE).sendToTarget();
        }else if(device instanceof DevHaveChild){
            for(Device device1 : ((DevHaveChild)device).getListDev()){
                removeDev(device1);
            }
        }
    }

    public static class MyHandler extends Handler {
        WeakReference<ElectricalCtrlFragment> mActivity;

        MyHandler(ElectricalCtrlFragment activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ElectricalCtrlFragment theActivity = mActivity.get();
            switch (msg.what) {
                case REFRESH_ELE_STATE:
                    if(Config.ins().getDevShowStyle().equals("0")) {
                        if(null != theActivity.adapterElectrical) {
                            theActivity.adapterElectrical.notifyDataSetChanged();
                        }
                    }else{
                        if(null != theActivity.adapterElectricalList) {
                            theActivity.adapterElectricalList.notifyDataSetChanged();
                        }
                    }
                    break;
                case REFRESH_ELE :
                    theActivity.setGridViewElectrical();
                    break;
                case CHANGE_SHOW_NAME_STYLE:
                    theActivity.setAdapter();
                    break;
                case CHANGE_LAYOUT_MANAGER:
                    theActivity.changeLayout();
                    break;
                case NOTIFY_ADAPTER:
                    if(Config.ins().getDevShowStyle().equals("0")) {
                        if(null != theActivity.adapterElectrical) {
                            theActivity.adapterElectrical.handler.obtainMessage(msg.arg1, msg.obj).sendToTarget();
                        }
                    }else{
                        if(null != theActivity.adapterElectricalList) {
                            theActivity.adapterElectricalList.handler.obtainMessage(msg.arg1, msg.obj).sendToTarget();
                        }
                    }
            }
        }
    }
}
