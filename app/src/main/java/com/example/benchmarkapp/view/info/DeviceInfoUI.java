package com.example.benchmarkapp.view.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.info.DeviceInfo;

public class DeviceInfoUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        TextView deviceInfoTextView = findViewById(R.id.deviceInfoTextView);

        // Fetch and display device information
        DeviceInfo deviceInfo = new DeviceInfo(this);
        String details = deviceInfo.getDeviceDetails();
        deviceInfoTextView.setText(details);
    }
}
