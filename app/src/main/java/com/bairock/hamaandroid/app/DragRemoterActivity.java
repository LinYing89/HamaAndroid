package com.bairock.hamaandroid.app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.database.Config;
import com.bairock.hamaandroid.media.Media;
import com.bairock.hamaandroid.remote.StudyKeyActivity;
import com.bairock.hamaandroid.zview.DragRemoterKeyButton;
import com.bairock.iot.intelDev.device.remoter.Remoter;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;

import java.util.List;

public class DragRemoterActivity extends AppCompatActivity {

    public static Remoter REMOTER;

    private RelativeLayout layoutRoot;
    private List<DragRemoterKeyButton> listDragRemoterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_remoter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        layoutRoot = findViewById(R.id.layoutRoot);
        initListButtons();
        setGridView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        REMOTER = null;
    }

    private void initListButtons() {
        for (RemoterKey remoterKey : REMOTER.getListRemoterKey()) {
            createAndAddDragRemoterButton(remoterKey);
        }
    }

    private DragRemoterKeyButton createDragRemoterButton(RemoterKey remoterKey){
        DragRemoterKeyButton rb = new DragRemoterKeyButton(this);
        rb.setRemoterKey(remoterKey);
        return rb;
    }

    private void createAndAddDragRemoterButton(RemoterKey remoterKey){
        DragRemoterKeyButton rb = createDragRemoterButton(remoterKey);
        rb.setOnLongClickListener(view -> {
            showPopUp(view);
            return false;
        });
        rb.setOnClickListener(view -> {
            if (Config.ins().isCtrlRing()) {
                Media.get().playCtrlRing();
            }
            RemoterKey remoterKey1 = ((DragRemoterKeyButton)view).getRemoterKey();
            HamaApp.sendOrder(remoterKey1.getRemoter().getParent(), remoterKey1.createCtrlKeyOrder(), true);
        });
        listDragRemoterBtn.add(rb);
    }

    private void setGridView() {
        layoutRoot.removeAllViews();
        for (DragRemoterKeyButton cb : listDragRemoterBtn) {
            addToLayout(cb);
        }
    }

    private void addToLayout(DragRemoterKeyButton cb) {
        int width = Constant.getRemoterKeyWidth();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                width, width);
        if (cb.getRemoterKey().getLocationY() >= Constant.displayHeight - width) {
            cb.getRemoterKey().setLocationY(Constant.displayHeight - width);
        }
        if (cb.getRemoterKey().getLocationX() >=  Constant.displayWidth - width) {
            cb.getRemoterKey().setLocationX(Constant.displayWidth - width);
        }

        layoutParams.topMargin = cb.getRemoterKey().getLocationY();
        layoutParams.leftMargin = cb.getRemoterKey().getLocationX();
        cb.setLayoutParams(layoutParams);
        layoutRoot.addView(cb);
    }

    private void showPopUp(View v) {
        DragRemoterKeyButton rb = (DragRemoterKeyButton)v;
        Button btnStudy = new Button(this);
        btnStudy.setText("学习");
        PopupWindow popupWindow = new PopupWindow(btnStudy, Constant.dip2px(100f),  Constant.dip2px(46f));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        popupWindow.showAsDropDown(v);
        btnStudy.setOnClickListener(view -> {
            popupWindow.dismiss();
            StudyKeyActivity.remoterKey = rb.getRemoterKey();
            startActivity(new Intent(this, StudyKeyActivity.class));
        });
    }
}
