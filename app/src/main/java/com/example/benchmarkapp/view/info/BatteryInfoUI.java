package com.example.benchmarkapp.view.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.info.BatteryInfo;

public class BatteryInfoUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_info);

        // Initialize BatteryInfo and fetch details
        BatteryInfo batteryInfo = new BatteryInfo(this);
        String batteryDetails = batteryInfo.getBatteryDetails();

        // Display battery information in the TextView
        TextView batteryInfoTextView = findViewById(R.id.batteryInfoTextView);
        batteryInfoTextView.setText(batteryDetails);
    }
}
