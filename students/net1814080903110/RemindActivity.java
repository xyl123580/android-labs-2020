package edu.hzuapps.androidlabs.net;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;


public class RemindActivity extends Activity {

    private TabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        Intent intent = getIntent();
        // android:id="@android:id/tabhost"
        tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("tabTime").setIndicator(" ").setContent(R.id.tab_time));
        tabHost.addTab(tabHost.newTabSpec("tabAlarm").setIndicator("闹钟").setContent(R.id.tab_alarm));
        tabHost.addTab(tabHost.newTabSpec("tabTimer").setIndicator(" ").setContent(R.id.tab_timer));
}
}