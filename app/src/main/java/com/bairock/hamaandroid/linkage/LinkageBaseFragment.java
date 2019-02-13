package com.bairock.hamaandroid.linkage;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.adapter.RecyclerAdapterLinkageBase;
import com.bairock.hamaandroid.app.MainActivity;
import com.bairock.hamaandroid.database.LinkageDao;
import com.bairock.hamaandroid.database.LinkageHolderDao;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.yanzhenjie.recyclerview.swipe.*;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

/**
 * A simple [Fragment] subclass.
 *
 */
public class LinkageBaseFragment extends Fragment {

    public static final String ARG_PARAM1 = "param1";
    public static final int REFRESH_LIST = 1;
    public static Linkage LINKAGE = null;

    private int param1;
    private Switch switchEnable;
    private Button btnAdd;
    public Handler handler;
    public LinkageHolder linkageHolder;
    public RecyclerAdapterLinkageBase adapterChain;;

    private SwipeMenuRecyclerView swipeMenuRecyclerViewChain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        param1 = getArguments().getInt(LinkageBaseFragment.ARG_PARAM1);
        initHandler();
        initLinkageHolder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chain, container, false);
        swipeMenuRecyclerViewChain = view.findViewById(R.id.swipeMenuRecyclerViewChain);
        swipeMenuRecyclerViewChain.setLayoutManager(new LinearLayoutManager(this.getContext()));
        swipeMenuRecyclerViewChain.addItemDecoration(new DefaultItemDecoration(Color.LTGRAY));
        swipeMenuRecyclerViewChain.setSwipeMenuCreator(swipeMenuConditionCreator);

        switchEnable = view.findViewById(R.id.switchEnable);
        btnAdd = view.findViewById(R.id.btnAdd);
        initCbEnable();
        setListener();
        setListChain();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LINKAGE = null;
    }

    public void initLinkageHolder(){}

    public void initHandler(){}

    public void initCbEnable(){
        switchEnable.setChecked(linkageHolder.isEnable());
    }

    private SwipeMenuCreator swipeMenuConditionCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        SwipeMenuItem renameItem = new SwipeMenuItem(getContext())
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange))
            .setText("重命名")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(renameItem);
        SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                .setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red_normal))
            .setText("删除")
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(deleteItem);
    };

    private void setListener() {
        switchEnable.setOnCheckedChangeListener(onCheckedChangeListener);
        btnAdd.setOnClickListener(onClickListener);

        swipeMenuRecyclerViewChain.setSwipeItemClickListener(linkageSwipeItemClickListener);
        swipeMenuRecyclerViewChain.setSwipeMenuItemClickListener(linkageSwipeMenuItemClickListener);
    }

    private void setListChain() {
        adapterChain = new RecyclerAdapterLinkageBase(getContext(), linkageHolder);
        swipeMenuRecyclerViewChain.setAdapter(adapterChain);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (view, isChecked) ->{
        linkageHolder.setEnable(isChecked);
        LinkageHolderDao.get(getContext()).update(linkageHolder);
    };

    private View.OnClickListener onClickListener = (view) ->{
        showRenameDialog(null);
    };

    private void showRenameDialog(String oldName) {
        boolean isRename = null != oldName;
        EditText editNewName = new EditText(this.getContext());
        String title;
        if(isRename){
            editNewName.setText(oldName);
            title = this.getContext().getString(R.string.rename);
        }else{
            editNewName.setText(getDefaultName());
            title = "输入名称";
        }
        new AlertDialog.Builder(this.getContext())
                .setTitle(title)
                .setView(editNewName)
                .setPositiveButton(MainActivity.strEnsure,
                        (p0, p1) -> {
                            String value = editNewName.getText().toString();
                            if (nameIsRepeat(value)) {
                                Toast.makeText(this.getContext(), "名称重复", Toast.LENGTH_SHORT).show();
                            } else {
                                if(isRename){
                                    LINKAGE.setName(value);
                                    LinkageDao.get(this.getContext()).add(LINKAGE, linkageHolder.getId());
                                    adapterChain.notifyDataSetChanged();
                                }else {
                                    Linkage linkage = addNewLinkage(value);
                                    if (null != linkage) {
                                        LinkageDao.get(this.getContext()).add(linkage, linkageHolder.getId());
                                        adapterChain.notifyDataSetChanged();
                                        LINKAGE = linkage;
                                        toLinkageActivity();
                                    }
                                }
                            }
                        }).setNegativeButton(MainActivity.strCancel, null).create().show();
    }

    //条件列表点击事件
    private SwipeItemClickListener linkageSwipeItemClickListener = (view, position) ->{
        LINKAGE = linkageHolder.getListLinkage().get(position);
        toLinkageActivity();
    };

    private String getDefaultName() {
        String name = getDefaultHeadName();
        boolean have;
        for (int i=1; i<999; i++) {
            String newName = name;
            have = false;
            newName += i;
            for (Linkage chain : linkageHolder.getListLinkage()) {
                if (chain.getName().equals(newName)) {
                    have = true;
                    break;
                }
            }
            if (!have) {
                return newName;
            }
        }
        return name;
    }

    public String getDefaultHeadName(){
        return "连锁";
    }

    private boolean nameIsRepeat(String name){
        for(Linkage linkage : linkageHolder.getListLinkage()){
            if(linkage.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public Linkage addNewLinkage(String name){
        return null;
    }

    public void toLinkageActivity(){ }

    private SwipeMenuItemClickListener linkageSwipeMenuItemClickListener = menuBridge -> {
        menuBridge.closeMenu();
        int adapterPosition = menuBridge.getAdapterPosition();
        int menuPosition = menuBridge.getPosition();
        Linkage linkage = linkageHolder.getListLinkage().get(adapterPosition);

        LINKAGE = linkage;
        switch (menuPosition){
            case 0 :
                showRenameDialog(LINKAGE.getName());
                break;
            case 1:
                linkageHolder.removeLinkage(linkage);
                linkage.setDeleted(true);
                LinkageDao linkageDevValueDao = LinkageDao.get(this.getContext());
                linkageDevValueDao.delete(linkage);
                adapterChain.notifyDataSetChanged();
                break;
        }

    };
}
