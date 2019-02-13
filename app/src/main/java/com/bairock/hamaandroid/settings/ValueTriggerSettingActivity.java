package com.bairock.hamaandroid.settings;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.bairock.hamaandroid.R;
import com.bairock.hamaandroid.database.ValueTriggerDao;
import com.bairock.iot.intelDev.device.CompareSymbol;
import com.bairock.iot.intelDev.device.devcollect.ValueTrigger;

import static com.bairock.hamaandroid.settings.ValueTriggerListActivity.collectProperty;

public class ValueTriggerSettingActivity extends AppCompatActivity {

    public static ValueTrigger valueTrigger;
    TextView txtDevice;
    Spinner spinner;
    EditText etxtValue;
    private EditText etxtMessage;
    Button btnSave;
    Button btnCancel;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_trigger_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setListener();
        init();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        valueTrigger = null;
    }

    public void findViews(){
        txtDevice = findViewById(R.id.txtDevice);
        spinner = findViewById(R.id.spinnerSymbol);
        etxtValue = findViewById(R.id.etxtValue);
        etxtMessage = findViewById(R.id.etxtMessage);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    public void setListener(){
        btnSave.setOnClickListener (view -> {
            int collectSrc = spinner.getSelectedItemPosition();
            CompareSymbol compareSymbol = CompareSymbol.values()[collectSrc];
            valueTrigger.setCompareSymbol(compareSymbol);
            valueTrigger.setTriggerValue(Float.valueOf(etxtValue.getText().toString()));
            valueTrigger.setMessage(etxtMessage.getText().toString());
            ValueTriggerDao.get(this).update(valueTrigger);
            finish();
        });
        btnCancel.setOnClickListener(view -> {
            finish();
        });
    }

    public void init(){
        txtDevice.setText(valueTrigger.getCollectProperty().getDevCollect().getName());
        spinner.setSelection(valueTrigger.getCompareSymbol().ordinal());
        etxtValue.setText(String.valueOf(valueTrigger.getTriggerValue()));
        etxtMessage.setText(valueTrigger.getMessage());
    }
}
