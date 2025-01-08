package com.example.benchmarkapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.view.info.*;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // CPU Information Button
        Button cpuInfoButton = findViewById(R.id.cpuInfoButton);
        cpuInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, CpuInfoUI.class);
            startActivity(intent);
        });

        // GPU Information Button
        Button gpuInfoButton = findViewById(R.id.gpuInfoButton);
        gpuInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, GpuInfoUI.class);
            startActivity(intent);
        });

        // Memory Information Button
        Button memoryInfoButton = findViewById(R.id.memoryInfoButton);
        memoryInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, MemoryInfoUI.class);
            startActivity(intent);
        });

        // Battery Information Button
        Button batteryInfoButton = findViewById(R.id.batteryInfoButton);
        batteryInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, BatteryInfoUI.class);
            startActivity(intent);
        });

        // OS Information Button
        Button osInfoButton = findViewById(R.id.osInfoButton);
        osInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, OSInfoUI.class);
            startActivity(intent);
        });

        // Device Information Button
        Button deviceInfoButton = findViewById(R.id.deviceInfoButton);
        deviceInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(InfoActivity.this, DeviceInfoUI.class);
            startActivity(intent);
        });

       }
}
