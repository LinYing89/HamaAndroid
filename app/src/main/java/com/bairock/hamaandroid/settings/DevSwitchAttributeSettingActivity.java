package com.bairock.hamaandroid.settings;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bairock.hamaandroid.R;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.device.Gear;

public class DevSwitchAttributeSettingActivity extends AppCompatActivity {

    public static Device device;
    private Toolbar toolbar;
    private TextView txtDevCoding;
    private EditText etxtAlisa;
    private EditText etxtName;
    private RadioGroup rgGear;
    private RadioButton rbGearKai;
    private RadioButton rbGearGuan;
    private RadioButton rbGearZiDong;
    private Button btnSave;
    private Button btnCancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_switch_attribute_setting);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        init();
        setListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        device = null;
    }

    public void init(){
        txtDevCoding = findViewById(R.id.txtDevCoding);
        etxtAlisa = findViewById(R.id.etxtAlisa);
        etxtName = findViewById(R.id.etxtName);
        rbGearKai = findViewById(R.id.rbGearKai);
        rbGearGuan = findViewById(R.id.rbGearGuan);
        rbGearZiDong = findViewById(R.id.rbGearZiDong);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        rgGear = findViewById(R.id.rgGear);

            txtDevCoding.setText(device.getLongCoding());
            etxtAlisa.setText(device.getAlias());
            etxtName.setText(device.getName());
            switch (device.getGear()){
                case KAI:
                    rbGearKai.setChecked(true);
                    break;
                case GUAN:
                    rbGearGuan.setChecked(true);
                    break;
                    default:
                        rbGearZiDong.setChecked(true);
                        break;
            }
    }

    public void setListener(){
        btnSave.setOnClickListener(view -> {
            String alias = etxtAlisa.getText().toString();
            String name = etxtName.getText().toString();
            if(device.getAlias().equals(alias)){
                device.setAlias(alias);
            }
            if(device.getName().equals(name)){
                device.setName(name);
            }
            Gear gear;
            switch (rgGear.getCheckedRadioButtonId()){
                case R.id.rbGearKai :
                    gear = Gear.KAI;
                    break;
                case R.id.rbGearGuan :
                    gear = Gear.GUAN;
                    break;
                    default:
                        gear = Gear.ZIDONG;
                        break;
            }

            if(device.getGear() != gear){
                device.setGear(gear);
            }

            finish();
        });

        btnCancel.setOnClickListener(view -> {finish();});
    }
}
