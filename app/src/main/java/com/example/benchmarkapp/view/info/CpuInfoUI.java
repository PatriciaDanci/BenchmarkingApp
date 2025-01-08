package com.example.benchmarkapp.view.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.*;
import com.example.benchmarkapp.model.info.*;

public class CpuInfoUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_info);

        TextView cpuInfoTextView = findViewById(R.id.cpuInfoTextView);

        // Retrieve CPU information using the model class
        CPUInfo cpuInfo = new CPUInfo();
        String info = cpuInfo.getCPUDetails();

        // Display the information
        cpuInfoTextView.setText(info != null ? info : "No CPU information available");
    }
}
