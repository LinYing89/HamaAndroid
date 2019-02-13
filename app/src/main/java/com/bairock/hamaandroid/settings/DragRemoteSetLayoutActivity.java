package com.bairock.hamaandroid.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.Constant;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.RemoterKeyDao;
import com.bairock.hamaandroid.zview.DragRemoterKeyButton;
import com.bairock.iot.intelDev.device.remoter.Remoter;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;
import android.graphics.drawable.BitmapDrawable;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义布局，添加按钮与改变按钮位置界面
 */
public class DragRemoteSetLayoutActivity extends AppCompatActivity implements View.OnTouchListener {

    public static Remoter REMOTER;
    private RelativeLayout layoutRoot;

    private List<DragRemoterKeyButton> listDragRemoterBtn = new ArrayList<>();
    private int lastX = 0;
    private int lastY = 0;
    private boolean longClick = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_remote_set_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        layoutRoot = findViewById(R.id.layoutRoot);
        initListButtons();
        setGridView();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_search_device, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }else if(item.getItemId() == R.id.action_add_device){
            showRenameDialog(null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
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
        rb.setOnTouchListener(this);
        return rb;
    }

    private void createAndAddDragRemoterButton(RemoterKey remoterKey){
        DragRemoterKeyButton rb = createDragRemoterButton(remoterKey);
        rb.setOnLongClickListener(view -> {
            if(longClick){
                showPopUp(view);
            }
            return false;
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

    private void showRenameDialog(DragRemoterKeyButton dragRemoterKeyButton) {
        EditText editNewName = new EditText(this);
        if(null != dragRemoterKeyButton) {
            editNewName.setText(dragRemoterKeyButton.getText());
        }
        new AlertDialog.Builder(this)
                .setTitle("输入按键名称")
                .setView(editNewName)
                .setPositiveButton(MainActivity.strEnsure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String value = editNewName.getText().toString();
                        if(REMOTER.keyNameIsExists(value)){
                            Toast.makeText(getApplicationContext(), "名称重复", Toast.LENGTH_SHORT).show();
                        }else{
                            if(null == dragRemoterKeyButton) {
                                String num = REMOTER.nextNumber();
                                if (null != num) {
                                    RemoterKey rk = new RemoterKey();
                                    rk.setNumber(num);
                                    rk.setName(value);
                                    rk.setLocationX(10);
                                    rk.setLocationY(10);
                                    REMOTER.addRemoterKey(rk);
                                    RemoterKeyDao.get(getApplicationContext()).add(rk);
                                    createAndAddDragRemoterButton(rk);
                                    addToLayout(listDragRemoterBtn.get(listDragRemoterBtn.size() - 1));
                                }
                            }else{
                                dragRemoterKeyButton.getRemoterKey().setName(value);
                                dragRemoterKeyButton.setText(value);
                                RemoterKeyDao.get(getApplicationContext()).update(dragRemoterKeyButton.getRemoterKey());
                            }
                        }
                    }
                }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    private void showPopUp(View v) {
        View layout = this.getLayoutInflater()
                .inflate(R.layout.edit_del, null);
        Button btnEdit = layout.findViewById(R.id.btnEdit);
        Button btnDel = layout.findViewById(R.id.btnDel);

        PopupWindow popupWindow = new PopupWindow(layout, Constant.dip2px(120f),
                Constant.dip2px(120f));

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        int[] location = new int[2];
        v.getLocationOnScreen(location);

        popupWindow.showAsDropDown(v);
        btnEdit.setOnClickListener(view -> {
            showRenameDialog((DragRemoterKeyButton)v);
            popupWindow.dismiss();
        });
        btnDel.setOnClickListener(view -> {
            popupWindow.dismiss();
            deleteBtn((DragRemoterKeyButton)v);
        });
    }

    private void deleteBtn(DragRemoterKeyButton dragRemoterKeyButton){
        RemoterKeyDao.get(this).delete(dragRemoterKeyButton.getRemoterKey());
        REMOTER.removeRemoterKey(dragRemoterKeyButton.getRemoterKey());
        listDragRemoterBtn.remove(dragRemoterKeyButton);
        layoutRoot.removeView(dragRemoterKeyButton);
    }

    @Override public boolean onTouch(View p0, MotionEvent p1) {
        DragRemoterKeyButton dragRemoterKeyButton = (DragRemoterKeyButton)p0;
        switch (p1.getAction()){
            case MotionEvent.ACTION_DOWN :
                lastX = (int)(p1.getRawX());
                lastY = (int)p1.getRawY();
                longClick = true;
                break;
            case MotionEvent.ACTION_MOVE :
                int dx = (int)p1.getRawX() - lastX;
                int dy = (int)p1.getRawY() - lastY;
                if (dx > 1 || dy > 1) {
                    longClick = false;
                }

                int top = p0.getTop() + dy;
                int left = p0.getLeft() + dx;

                if (top <= 0) {
                    top = 0;
                }
                if (top >= Constant.displayHeight - dragRemoterKeyButton.getHeight()) {
                    top =  Constant.displayHeight - dragRemoterKeyButton.getHeight();
                }
                if (left >=  Constant.displayWidth - dragRemoterKeyButton.getWidth()) {
                    left =  Constant.displayWidth - dragRemoterKeyButton.getWidth();
                }
                if (left <= 0) {
                    left = 0;
                }

                dragRemoterKeyButton.getRemoterKey().setLocationX(left);
                dragRemoterKeyButton.getRemoterKey().setLocationY(top);
                dragRemoterKeyButton.layoutBtn();
                // v.layout(left, top, left + iv.getWidth(), top + iv.getHeight());
                lastX = (int)(p1.getRawX());
                lastY = (int)p1.getRawY();
                break;
            case MotionEvent.ACTION_UP :
                if(!longClick){
                    RemoterKeyDao.get(this).update(dragRemoterKeyButton.getRemoterKey());
                }
                break;
        }
        return false;
    }
}
