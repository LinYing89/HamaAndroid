package com.bairock.hamaandroid.remote;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.app.HamaApp;
import com.bairock.iot.intelDev.device.remoter.RemoterKey;

import java.lang.ref.WeakReference;

public class StudyKeyActivity extends AppCompatActivity {

    public static RemoterKey remoterKey = null;
    public static final int STUDY_READY = 0;
    public static final int STUDIED = 1;
    public static final int TESTED = 2;
    public static Handler handler;
    private static WaitTask waitTask;

    private TextView txtInfo;
    private LinearLayout llBegin;
    private LinearLayout llTest;

    private Button btnStudy;
    private ProgressBar pbWait;
    private Button btnTest;
    private Button btnTestSuccess;
    private Button btnReStudy;

    private int studyProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_key);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(remoterKey.getName());
        }

        findViews();
        setListener();

        handler = new MyHandler(this);

        studyProgress = 0;
        refreshUi(studyProgress);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoterKey = null;
        handler.removeCallbacksAndMessages(null);
        handler = null;
        if(null != waitTask && waitTask.getStatus() == AsyncTask.Status.RUNNING){
            waitTask.cancel(true);
            waitTask = null;
        }
    }

    private void findViews(){
        txtInfo = findViewById(R.id.txtInfo);
        llBegin = findViewById(R.id.llBegin);
        llTest = findViewById(R.id.llTest);
        btnStudy = findViewById(R.id.btnStudy);
        pbWait = findViewById(R.id.pbWait);
        btnTest = findViewById(R.id.btnTest);
        btnTestSuccess = findViewById(R.id.btnTestSuccess);
        btnReStudy = findViewById(R.id.btnReStudy);
    }

    private void setListener(){
        btnStudy.setOnClickListener(onClickListener);
        btnTest.setOnClickListener(onClickListener);
        btnTestSuccess.setOnClickListener(onClickListener);
        btnReStudy.setOnClickListener(onClickListener);
    }

    private void refreshUi(int progress){
        if(null != waitTask && waitTask.getStatus() == AsyncTask.Status.RUNNING){
            waitTask.cancel(true);
        }
        switch (progress){
            case STUDY_READY :
                txtInfo.setText("请点击开始学习按钮");
                llBegin.setVisibility(View.VISIBLE);
                btnStudy.setVisibility(View.VISIBLE);
                btnTest.setVisibility(View.GONE);
                llTest.setVisibility(View.GONE);
                break;
            case STUDIED :
                txtInfo.setText("请点击测试按钮,测试是否可以控制");
                llBegin.setVisibility(View.VISIBLE);
                btnStudy.setVisibility(View.GONE);
                btnTest.setVisibility(View.VISIBLE);
                llTest.setVisibility(View.GONE);
                break;
            case TESTED :
                txtInfo.setText("如果测试成功请点击测试成功按钮,如果测试失败请点击重新学习按钮");
                llBegin.setVisibility(View.GONE);
                btnStudy.setVisibility(View.GONE);
                btnTest.setVisibility(View.GONE);
                llTest.setVisibility(View.VISIBLE);
                break;
                default:
                    finish();
                    break;
        }
    }

    private View.OnClickListener onClickListener = (view) -> {
        switch (view.getId()){
            case R.id.btnStudy :
                HamaApp.sendOrder(remoterKey.getRemoter().getParent(), remoterKey.createStudyKeyOrder(), true);
                btnStudy.setVisibility(View.GONE);
                txtInfo.setText("请将实体遥控器对准智能遥控器,并按一下想要学习的按键");
                showWait();
                break;
            case R.id.btnTest :
                HamaApp.sendOrder(remoterKey.getRemoter().getParent(), remoterKey.createTestKeyOrder(), true);
                btnTest.setVisibility(View.GONE);
                showWait();
                break;
            case R.id.btnTestSuccess :
                HamaApp.sendOrder(remoterKey.getRemoter().getParent(), remoterKey.createSaveKeyOrder(), true);
                llTest.setVisibility(View.GONE);
                showWait();
                break;
            case R.id.btnReStudy :
                HamaApp.sendOrder(remoterKey.getRemoter().getParent(), remoterKey.createTestKeyOrder(), true);
                studyProgress = 0;
                refreshUi(studyProgress);
                break;
        }
    };

    private void showWait(){
        //pbWait.visibility = View.VISIBLE
        waitTask = new WaitTask(this);
        waitTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //waitTask!!.ex()
    }

    private static class MyHandler extends Handler{
        WeakReference<StudyKeyActivity> weakActivity;
        public MyHandler(StudyKeyActivity activity){
            weakActivity = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StudyKeyActivity activity = weakActivity.get();
            activity.refreshUi(++activity.studyProgress);
        }
    }

    private static class WaitTask extends AsyncTask<Void, Void, Boolean>{

        WeakReference<StudyKeyActivity> weakActivity;

        WaitTask(StudyKeyActivity activity){
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void onPreExecute() {
            StudyKeyActivity myActivity = weakActivity.get();
            myActivity.pbWait.setVisibility(View.VISIBLE);
        }

        @Override
        public Boolean doInBackground(Void... voids) {
            for(int i=0; i<8; i++){
                if(isCancelled()){
                    return false;
                }
                try {
                    Thread.sleep(1000);
                }catch (Exception ex){
                    return false;
                }
            }
            return true;
        }

        @Override
        public void onPostExecute(Boolean result) {
            StudyKeyActivity myActivity = weakActivity.get();
            if(result){
                Toast.makeText(myActivity, "学习失败,智能遥控器无返回", Toast.LENGTH_SHORT).show();
                myActivity.refreshUi(myActivity.studyProgress);
                myActivity.pbWait.setVisibility(View.GONE);
            }else{
                myActivity.pbWait.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled() {
            StudyKeyActivity myActivity = weakActivity.get();
            myActivity.pbWait.setVisibility(View.GONE);
        }
    }
}
