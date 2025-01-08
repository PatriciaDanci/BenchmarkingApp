package com.example.benchmarkapp.view;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.controller.BenchmarkController;
import com.example.benchmarkapp.model.BenchmarkResult;
import com.github.mikephil.charting.BuildConfig;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    private TextView cpuResultText, gpuResultText, memoryResultText, overallScoreText;
    private BenchmarkController benchmarkController;
    private ExecutorService executorService;
    private Handler mainHandler;
    private ProgressBar progressBar;
    private Button cpuBenchmarkButton, gpuBenchmarkButton, memoryBenchmarkButton, historyButton, infoButton, finalizeBenchmarkButton;

    private boolean cpuCompleted = false;
    private boolean gpuCompleted = false;
    private boolean memoryCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);

        benchmarkController = new BenchmarkController(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        cpuBenchmarkButton = findViewById(R.id.cpuBenchmarkButton);
        gpuBenchmarkButton = findViewById(R.id.gpuBenchmarkButton);
        memoryBenchmarkButton = findViewById(R.id.memoryBenchmarkButton);
        historyButton = findViewById(R.id.historyButton);
        infoButton = findViewById(R.id.infoButton);
        finalizeBenchmarkButton = findViewById(R.id.finalizeBenchmarkButton);

        cpuResultText = findViewById(R.id.cpuResultText);
        gpuResultText = findViewById(R.id.gpuResultText);
        memoryResultText = findViewById(R.id.memoryResultText);
        progressBar = findViewById(R.id.progressBar);
        overallScoreText = findViewById(R.id.overallScoreText);

        cpuBenchmarkButton.setOnClickListener(v -> {
            overallScoreText.setText("Overall Score: Not computed");
            cpuResultText.setText("CPU: Benchmark in progress...");
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            executorService.execute(() -> {
                BenchmarkResult cpuResult = benchmarkController.performCpuBenchmark();

                mainHandler.post(() -> {
                    cpuResultText.setText("CPU: Single-Core Score " + cpuResult.getCpuScoreSingle() +
                            "\nCPU: Multi-Core Score " + cpuResult.getCpuScoreMulti());
                    Toast.makeText(MainActivity.this, "CPU Benchmark completed!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    cpuCompleted = true;
                    checkAllBenchmarksCompleted();
                    enableButtons();

                    executorService.execute(() -> {
                        Map<String, List<Double>> operationTimes = benchmarkController.getCpuOperationTimes();
                        OperationTimesManager.getInstance().setOperationTimes(operationTimes);

                        mainHandler.post(() -> {
                            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle("View Chart?")
                                    .setMessage("The CPU benchmark has completed. Would you like to view the chart?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        Intent intent = new Intent(MainActivity.this, CpuChartActivity.class);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        });
                    });
                });
            });
        });

        gpuBenchmarkButton.setOnClickListener(v -> {
            overallScoreText.setText("Overall Score: Not computed");
            gpuResultText.setText("GPU: Benchmark in progress...");
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            executorService.execute(() -> {
                BenchmarkResult gpuResult = benchmarkController.performGpuBenchmark();
                mainHandler.post(() -> {
                    gpuResultText.setText("GPU: Score " + gpuResult.getGpuScore());
                    Toast.makeText(MainActivity.this, "GPU Benchmark completed!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    gpuCompleted = true;
                    checkAllBenchmarksCompleted();
                    enableButtons();
                });
            });
        });

        memoryBenchmarkButton.setOnClickListener(v -> {
            overallScoreText.setText("Overall Score: Not computed");
            memoryResultText.setText("Memory: Benchmark in progress...");
            progressBar.setVisibility(View.VISIBLE);
            disableButtons();

            executorService.execute(() -> {
                BenchmarkResult memoryResult = benchmarkController.performMemoryBenchmark();

                mainHandler.post(() -> {
                    memoryResultText.setText("Memory: Latency score " + memoryResult.getMemoryLatencyScore() +
                            "\nMemory: Bandwidth score " + memoryResult.getMemoryBandwidthScore());
                    Toast.makeText(MainActivity.this, "Memory Benchmark completed!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);

                    memoryCompleted = true;
                    checkAllBenchmarksCompleted();
                    enableButtons();

                    executorService.execute(() -> {
                        Map<String, List<Double>> operationTimes = benchmarkController.getMemoryOperationTimes();
                        OperationTimesManager.getInstance().setOperationTimes(operationTimes);

                        mainHandler.post(() -> {
                            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle("View Chart?")
                                    .setMessage("The Memory benchmark has completed. Would you like to view the chart?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        Intent intent = new Intent(MainActivity.this, MemoryChartActivity.class);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("No", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        });
                    });
                });
            });
        });

        finalizeBenchmarkButton.setOnClickListener(v -> {
            benchmarkController.finalizeBenchmark();
            int overallScore = benchmarkController.computeOverallScore(benchmarkController.getBenchmarkResults().get(benchmarkController.getBenchmarkResults().size() - 1));
            overallScoreText.setText("Overall Score: " + overallScore);
            cpuResultText.setText("CPU: Not benchmarked");
            gpuResultText.setText("GPU: Not benchmarked");
            memoryResultText.setText("Memory: Not benchmarked");
            Toast.makeText(this, "Overall Score: " + overallScore, Toast.LENGTH_LONG).show();
            finalizeBenchmarkButton.setEnabled(false); // Reset
            resetBenchmarks();
        });


        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("benchmarkResults", new ArrayList<>(benchmarkController.getBenchmarkResults()));
            startActivity(intent);
        });

        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InfoActivity.class);
            startActivity(intent);
        });
    }

    private void disableButtons() {
        cpuBenchmarkButton.setEnabled(false);
        gpuBenchmarkButton.setEnabled(false);
        memoryBenchmarkButton.setEnabled(false);
        historyButton.setEnabled(false);
        infoButton.setEnabled(false);
    }

    private void enableButtons() {
        cpuBenchmarkButton.setEnabled(true);
        gpuBenchmarkButton.setEnabled(true);
        memoryBenchmarkButton.setEnabled(true);
        historyButton.setEnabled(true);
        infoButton.setEnabled(true);
    }

    private void checkAllBenchmarksCompleted() {
        if (cpuCompleted && gpuCompleted && memoryCompleted) {
            finalizeBenchmarkButton.setEnabled(true);
        }
    }

    private void resetBenchmarks() {
        cpuCompleted = false;
        gpuCompleted = false;
        memoryCompleted = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
