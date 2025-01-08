package com.example.benchmarkapp.view.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.info.OSInfo;

public class OSInfoUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os_info);

        // Initialize OSInfo and fetch details
        OSInfo osInfo = new OSInfo();
        String osDetails = osInfo.getOSDetails();

        // Display OS information in the TextView
        TextView osInfoTextView = findViewById(R.id.osInfoTextView);
        osInfoTextView.setText(osDetails);
    }
}
