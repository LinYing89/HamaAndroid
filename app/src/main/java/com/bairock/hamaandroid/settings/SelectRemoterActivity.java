package com.bairock.hamaandroid.settings;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bairock.hamaandroid.R;
import android.content.Intent;
import android.view.MenuItem;


public class SelectRemoterActivity extends AppCompatActivity {

    private ListView lvRemoter;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_remoter);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String[] remoters = getResources().getStringArray(R.array.array_remoter);

        lvRemoter = findViewById(R.id.lvRemotor);
        lvRemoter.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent();
            intent.putExtra("remoterCode", i+1);
            intent.putExtra("remoterName", remoters[i]);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }

    @Override public boolean onOptionsItemSelected(MenuItem item){

        if(item.getItemId() == android.R.id.home){
            setResult(Activity.RESULT_CANCELED, null);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
