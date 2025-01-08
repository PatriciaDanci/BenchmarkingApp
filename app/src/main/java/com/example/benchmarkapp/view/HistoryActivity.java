package com.example.benchmarkapp.view;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.controller.BenchmarkController;
import com.example.benchmarkapp.model.BenchmarkResult;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    BenchmarkController benchmarkController = new BenchmarkController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TextView historyTextView = findViewById(R.id.historyTextView);

        ArrayList<BenchmarkResult> benchmarkResults =
                (ArrayList<BenchmarkResult>) getIntent().getSerializableExtra("benchmarkResults");

        if (benchmarkResults != null && !benchmarkResults.isEmpty()) {
            StringBuilder historyText = new StringBuilder();
            for (BenchmarkResult result : benchmarkResults) {
                historyText.append("Timestamp: ").append(result.getTimestamp()).append("\n");
                historyText.append("CPU Single-Core: ").append(result.getCpuScoreSingle()).append("\n");
                historyText.append("CPU Multi-Core: ").append(result.getCpuScoreMulti()).append("\n");
                historyText.append("GPU Score: ").append(result.getGpuScore()).append("\n");
                historyText.append("Memory Latency Score: ").append(result.getMemoryLatencyScore()).append("\n");
                historyText.append("Memory Bandwidth Score: ").append(result.getMemoryBandwidthScore()).append("\n");
                historyText.append("Overall Score: ").append(benchmarkController.computeOverallScore(result)).append("\n");

                historyText.append("\n\n");
            }
            historyTextView.setText(historyText.toString());
        } else {
            historyTextView.setText("No finalized benchmark history available.");
        }
    }


}
