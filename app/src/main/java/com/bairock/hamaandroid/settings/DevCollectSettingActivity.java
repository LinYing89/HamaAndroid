package com.bairock.hamaandroid.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.database.CollectPropertyDao;
import com.bairock.hamaandroid.database.DeviceDao;
import com.bairock.hamaandroid.settings.ValueChangeLinkageActivity;
import com.bairock.hamaandroid.settings.ValueTriggerListActivity;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.device.devcollect.CollectProperty;
import com.bairock.iot.intelDev.device.devcollect.CollectSignalSource;
import com.bairock.iot.intelDev.device.devcollect.DevCollect;
import com.bairock.iot.intelDev.device.devcollect.DevCollectSignal;

public class DevCollectSettingActivity extends AppCompatActivity {

    public static DevCollect devCollectSignal;
    private CollectProperty collectProperty;

    private TextView txtCoding;
    private EditText etxtWeiHao;
    private EditText etxtName;
    private Spinner spinnerSignalSource;
    private EditText etxtUnit;
    private TableRow tabrow_Aa_Ab;
    private EditText etxtAa;
    private EditText etxtAb;
    private TableRow tabrow_a_b;
    private EditText etxta;
    private EditText etxtb;
    private EditText etxtCalibration;
    private Button btnCalibration;
    private Button btnValueTrigged;
    private Button btnValueLinkage;
    private Button btnSave;
    private Button btnCancel;

    private ProgressDialog waitingDialog;
    private CalibrationThread calibrationThread;

    private String strFour;
    private String strTwenty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_collect_setting);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        strFour = getResources().getString(R.string.four);
        strTwenty = getResources().getString(R.string.twenty);

        findViews();
        if(null == devCollectSignal){
            finish();
            return;
        }
        collectProperty = devCollectSignal.getCollectProperty();

        init();
        setListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        devCollectSignal.setCalibrationnListener(null);
    }

    private void findViews(){
        txtCoding = findViewById(R.id.txtCoding);
        etxtWeiHao = findViewById(R.id.etxtWeiHao);
        etxtName = findViewById(R.id.etxtName);
        etxtUnit = findViewById(R.id.etxtUnit);
        etxtAa = findViewById(R.id.etxtAa);
        etxtAb = findViewById(R.id.etxtAb);
        etxta = findViewById(R.id.etxta);
        etxtb = findViewById(R.id.etxtb);
        etxtCalibration = findViewById(R.id.etxtCalibration);
        spinnerSignalSource = findViewById(R.id.spinnerSignalSource);
        btnCalibration = findViewById(R.id.btnCalibration);
        btnValueTrigged = findViewById(R.id.btnValueTrigged);
        btnValueLinkage= findViewById(R.id.btnValueLinkage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        tabrow_Aa_Ab = findViewById(R.id.tabrow_Aa_Ab);
        tabrow_a_b = findViewById(R.id.tabrow_a_b);
    }

    private void setListener(){
        spinnerSignalSource.setOnItemSelectedListener(onItemSelectedListener);
        btnCalibration.setOnClickListener(onClickListener);
        btnValueTrigged.setOnClickListener(onClickListener);
        btnValueLinkage.setOnClickListener(onClickListener);
        btnSave.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(onClickListener);
    }

    private void init(){
        txtCoding.setText(devCollectSignal.getLongCoding());
        etxtWeiHao.setText(devCollectSignal.getAlias());
        etxtName.setText(devCollectSignal.getName());
        etxtUnit.setText(collectProperty.getUnitSymbol());
        etxtAa.setText(String.valueOf(collectProperty.getLeastReferValue()));
        etxtAb.setText(String.valueOf(collectProperty.getCrestReferValue()));
        etxta.setText(String.valueOf(collectProperty.getLeastValue()));
        etxtb.setText(String.valueOf(collectProperty.getCrestValue()));
        etxtCalibration.setText(String.valueOf(collectProperty.getCalibrationValue()));
        spinnerSignalSource.setSelection(collectProperty.getCollectSrc().ordinal());

        initSourceLayout(collectProperty.getCollectSrc().ordinal());
    }

    private void initSourceLayout(int position){
        if (position == 0) {
            tabrow_Aa_Ab.setVisibility(View.VISIBLE);
            tabrow_a_b.setVisibility(View.GONE);

        } else if (position == 1) {
            tabrow_Aa_Ab.setVisibility(View.VISIBLE);
            tabrow_a_b.setVisibility(View.GONE);

        } else if (position == 2) {
            tabrow_Aa_Ab.setVisibility(View.VISIBLE);
            tabrow_a_b.setVisibility(View.VISIBLE);

        } else if (position == 3) {
            tabrow_Aa_Ab.setVisibility(View.GONE);
            tabrow_a_b.setVisibility(View.GONE);

        }
    }

    private void save(){
        boolean updateDev = false;
        boolean updatePropety = false;

        String alias = etxtWeiHao.getText().toString();
        String name = etxtName.getText().toString();
        if(!alias.equals(devCollectSignal.getAlias())){
            updateDev = true;
            devCollectSignal.setAlias(alias);
        }
        if(!name.equals(devCollectSignal.getName())){
            updateDev = true;
            devCollectSignal.setName(name);
        }

        String unit = etxtUnit.getText().toString();
        float Aa = 0;
        try {
            Aa = Float.parseFloat(etxtAa.getText().toString());
        }catch (Exception e){e.printStackTrace();}
        float Ab = 0;
        try {
            Ab = Float.parseFloat(etxtAb.getText().toString());
        }catch (Exception e){e.printStackTrace();}
        float a = 0;
        try {
            a = Float.parseFloat(etxta.getText().toString());
        }catch (Exception e){e.printStackTrace();}
        float b = 0;
        try {
            b = Float.parseFloat(etxtb.getText().toString());
        }catch (Exception e){e.printStackTrace();}
        int collectSrc = spinnerSignalSource.getSelectedItemPosition();
        if(collectProperty.getCollectSrc().ordinal() != spinnerSignalSource.getSelectedItemPosition()){
            updatePropety = true;
            collectProperty.setCollectSrc(CollectSignalSource.values()[collectSrc]);
        }

        if(!unit.equals(collectProperty.getUnitSymbol())){
            updatePropety = true;
            collectProperty.setUnitSymbol(unit);
        }
        if(Aa != collectProperty.getLeastReferValue()){
            updatePropety = true;
            collectProperty.setLeastReferValue(Aa);
        }
        if(Ab != collectProperty.getCrestReferValue()){
            updatePropety = true;
            collectProperty.setCrestReferValue(Ab);
        }
        if(a != collectProperty.getLeastValue()){
            updatePropety = true;
            collectProperty.setLeastValue(a);
        }
        if(b != collectProperty.getCrestValue()){
            updatePropety = true;
            collectProperty.setCrestValue(b);
        }

        if(updateDev){
            DeviceDao deviceDao = DeviceDao.get(this);
            deviceDao.update(devCollectSignal);
        }
        if(updatePropety){
            CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(this);
            collectPropertyDao.update(collectProperty);
        }

        collectProperty = null;
        devCollectSignal = null;
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position == 1){
                etxta.setText(strFour);
                etxtb.setText(strTwenty);
            }
            initSourceLayout(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void showCalibrationDialog(){
        waitingDialog = new ProgressDialog(this);
        waitingDialog.setTitle("请稍等");
        waitingDialog.setMessage("设置标定中...");
        waitingDialog.setIndeterminate(true);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    private View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.btnCalibration:
                String value = etxtCalibration.getText().toString();
                if(value.isEmpty()){
                    Snackbar.make(btnCalibration, "标定值不可为空!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                try{
                    float fValue = Float.parseFloat(value);
                    if(fValue < 0 || fValue > 10){
                        Toast.makeText(this, "标定值范围为0-10", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    devCollectSignal.setCalibrationnListener(res ->{
                        runOnUiThread(() ->{
                            Toast.makeText(this, "标定成功", Toast.LENGTH_SHORT).show();
                            if(null != waitingDialog){
                                waitingDialog.dismiss();
                            }
                        });

                        if(null != calibrationThread){
                            calibrationThread.interrupt();
                        }
                        devCollectSignal.getCollectProperty().setCalibrationValue(fValue);
                        CollectPropertyDao collectPropertyDao = CollectPropertyDao.get(this);
                        collectPropertyDao.update(collectProperty);
                    });
                    showCalibrationDialog();
                    calibrationThread = new CalibrationThread();
                    calibrationThread.start();
                    String order = devCollectSignal.createCalibrationOrder((int)fValue);
                    DevChannelBridgeHelper.getIns().sendDevOrder(devCollectSignal, order, true);
                }catch (Exception e){
                    Snackbar.make(btnCalibration, "标定值包含非法字符!", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnValueTrigged:
                ValueTriggerListActivity.collectProperty = collectProperty;
                startActivity(new Intent(this, ValueTriggerListActivity.class));
                break;
            case R.id.btnValueLinkage:
                ValueChangeLinkageActivity.DEVICE = devCollectSignal;
                startActivity(new Intent(this, ValueChangeLinkageActivity.class));
                break;
            case R.id.btnSave:
                save();
                finish();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    };

    private class CalibrationThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(10000);
                runOnUiThread(() ->{
                    Toast.makeText(DevCollectSettingActivity.this, "标定超时", Toast.LENGTH_SHORT).show();
                    if(null != waitingDialog){
                        waitingDialog.dismiss();
                    }
                    devCollectSignal.setCalibrationnListener(null);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
