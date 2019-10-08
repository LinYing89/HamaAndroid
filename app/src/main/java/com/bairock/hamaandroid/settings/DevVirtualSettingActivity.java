package com.bairock.hamaandroid.settings;

import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bairock.hamaandroid.R;
import com.bairock.iot.intelDev.device.Device;

public class DevVirtualSettingActivity extends AppCompatActivity {

    public static Device device;
    private Toolbar toolbar;
    private TextView txtDevCoding;
    private EditText etxtName;
    private EditText etxtValue;
    private Button btnSave;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_virtual_setting);
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
        etxtName = findViewById(R.id.etxtName);
        etxtValue = findViewById(R.id.etxtValue);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        txtDevCoding.setText(device.getLongCoding());
        etxtName.setText(device.getName());
        etxtValue.setText(device.getValue());
    }

    public void setListener(){
        btnSave.setOnClickListener(view -> {
            String name = etxtName.getText().toString();
            String value = etxtValue.getText().toString();
            device.setName(name);
            device.setValue(value);
            finish();
        });

        btnCancel.setOnClickListener(view -> {finish();});
    }
}
