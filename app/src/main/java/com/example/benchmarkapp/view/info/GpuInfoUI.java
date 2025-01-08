package com.example.benchmarkapp.view.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.info.GPUInfo;

public class GpuInfoUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpu_info);

        TextView gpuInfoTextView = findViewById(R.id.gpuInfoTextView);

        // Fetch GPU information
        GPUInfo gpuInfo = new GPUInfo();
        String details = gpuInfo.getGPUDetails();

        // Display details in the TextView
        gpuInfoTextView.setText(details);
    }
}
