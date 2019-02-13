package com.bairock.hamaandroid.settings;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.adapter.AdapterValueChangeLinkage;
import com.bairock.hamaandroid.database.DeviceLinkageDao;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.device.DeviceLinkage;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * 采集设备值变化连锁设置界面
 */
public class ValueChangeLinkageActivity extends AppCompatActivity {

    public static Device DEVICE;
    private SwipeMenuRecyclerView lvDeviceLinkage;

    private AdapterValueChangeLinkage adapterCondition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_change_linkage);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lvDeviceLinkage = findViewById(R.id.lvDeviceLinkage);
        lvDeviceLinkage.setLayoutManager(new LinearLayoutManager(this));
        lvDeviceLinkage.setSwipeMenuCreator(swipeMenuConditionCreator);
//        lvDeviceLinkage.setSwipeItemClickListener(linkageSwipeItemClickListener);
        lvDeviceLinkage.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener);

        setListViewCondition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search_device, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
            case R.id.action_add_device :
                DeviceLinkage deviceLinkage = new DeviceLinkage();
                deviceLinkage.setSourceDevice(DEVICE);
                DeviceLinkageDao.get(this).add(deviceLinkage);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DEVICE = null;
    }

    private void setListViewCondition() {
        adapterCondition = new AdapterValueChangeLinkage(this, DEVICE.getListDeviceLinkage());
        lvDeviceLinkage.setAdapter(adapterCondition);
    }

    private SwipeMenuCreator swipeMenuConditionCreator = ((p0, swipeRightMenu, p1)  -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        SwipeMenuItem deleteItem = new SwipeMenuItem(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);
    });

    private SwipeMenuItemClickListener linkageSwipeMenuItemClickListener = (menuBridge -> {
        menuBridge.closeMenu();
        int menuPosition = menuBridge.getPosition();
    });
}
