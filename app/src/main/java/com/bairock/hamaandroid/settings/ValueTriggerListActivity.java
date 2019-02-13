package com.bairock.hamaandroid.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.adapter.AdapterValueTrigger;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.ValueTriggerDao;
import com.bairock.hamaandroid.settings.ValueTriggerSettingActivity;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

public class ValueTriggerListActivity extends AppCompatActivity {

    public static CollectProperty collectProperty;

    private SwipeMenuRecyclerView lvValueTrigger;
    private AdapterValueTrigger adapterValueTrigger;

    private ValueTrigger valueTrigger;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_trigger_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        lvValueTrigger = findViewById(R.id.lvValueTrigger);

        lvValueTrigger.setLayoutManager(new LinearLayoutManager(this));
        lvValueTrigger.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        lvValueTrigger.setSwipeMenuCreator(swipeMenuConditionCreator);

        lvValueTrigger.setSwipeItemClickListener(linkageSwipeItemClickListener);
        lvValueTrigger.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener);

        setListTrigger();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
                break;
            case R.id.action_add_device :
                showRenameDialog(null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        collectProperty = null;
    }

    private void setListTrigger() {
        adapterValueTrigger = new AdapterValueTrigger(this, collectProperty.getListValueTrigger());
        lvValueTrigger.setAdapter(adapterValueTrigger);
    }

    private SwipeMenuCreator swipeMenuConditionCreator = ((p0, swipeRightMenu, p1) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        SwipeMenuItem renameItem = new SwipeMenuItem(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                .setText("重命名")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(renameItem);
        SwipeMenuItem deleteItem = new SwipeMenuItem(this)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.red_normal))
                .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);
    });

    private void showRenameDialog(String oldName) {
        boolean isRename = null != oldName;
        EditText editNewName = new EditText(this);
        String title;
        if(isRename){
            editNewName.setText(oldName);
            title = this.getString(R.string.rename);
        }else{
            title = "输入名称";
        }
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(editNewName)
                .setPositiveButton(MainActivity.strEnsure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value = editNewName.getText().toString();
                        if (isRename) {
                            valueTrigger.setName(value);
                            ValueTriggerDao.get(getApplicationContext()).update(valueTrigger);
                            adapterValueTrigger.notifyDataSetChanged();
                        } else {
                            ValueTrigger valueTrigger = new ValueTrigger();
                            valueTrigger.setName(value);
                            valueTrigger.setEnable(true);
                            collectProperty.addValueTrigger(valueTrigger);
                            ValueTriggerDao.get(getApplicationContext()).add(valueTrigger);
                            adapterValueTrigger.notifyDataSetChanged();

                            ValueTriggerSettingActivity.valueTrigger = valueTrigger;
                            startActivity(new Intent(getApplicationContext(), ValueTriggerSettingActivity.class));
                        }
                    }
                }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private SwipeItemClickListener linkageSwipeItemClickListener = ((p0, position) -> {
        ValueTriggerSettingActivity.valueTrigger = collectProperty.getListValueTrigger().get(position);
        startActivity(new Intent(this, ValueTriggerSettingActivity.class));
    });

    private SwipeMenuItemClickListener linkageSwipeMenuItemClickListener = (menuBridge -> {
        menuBridge.closeMenu();
        int adapterPosition = menuBridge.getAdapterPosition();
        int menuPosition = menuBridge.getPosition();
        valueTrigger = collectProperty.getListValueTrigger().get(adapterPosition);

        switch (menuPosition){
            case 0 :
                showRenameDialog(valueTrigger.getName());
                break;
            case 1 :
                //删除
                ValueTriggerDao.get(this).delete(valueTrigger);
                collectProperty.getListValueTrigger().remove(valueTrigger);
                adapterValueTrigger.notifyDataSetChanged();
                break;
        }
    });
}
