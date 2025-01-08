package com.example.benchmarkapp.view.info;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.info.MemoryInfo;

public class MemoryInfoUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_info);

        // Fetch memory details and display them
        MemoryInfo memoryInfo = new MemoryInfo(this);
        String memoryDetails = memoryInfo.getMemoryDetails();

        TextView memoryInfoTextView = findViewById(R.id.memoryInfoTextView);
        memoryInfoTextView.setText(memoryDetails);
    }
}
