package com.example.benchmarkapp.view;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.benchmarkapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemoryChartActivity extends AppCompatActivity {
    private LineChart memoryLineChart;
    private static final int MAX_DATA_POINTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_chart);

        memoryLineChart = findViewById(R.id.memoryLineChart);

        Map<String, List<Double>> operationTimes = OperationTimesManager.getInstance().getOperationTimes();

        if (operationTimes != null) {
            showBenchmarkChart(operationTimes);
        }
    }

    private void showBenchmarkChart(Map<String, List<Double>> operationTimes) {
        if (operationTimes == null || operationTimes.isEmpty()) {
            System.out.println("No data available for chart.");
            return;
        }

        List<LineDataSet> dataSets = new ArrayList<>();
        int colorIndex = 0;
        int[] colors = {0xFF6200EE, 0xFF3700B3, 0xFF03DAC5, 0xFF018786, 0xFFFF5722, 0xFFC2185B, 0xFFE91E63, 0xFF9C27B0, 0xFF673AB7, 0xFF3F51B5, 0xFF2196F3, 0xFF03A9F4, 0xFF00BCD4, 0xFF009688, 0xFF4CAF50, 0xFF8BC34A, 0xFFCDDC39, 0xFFFFEB3B, 0xFFFFC107, 0xFFFF9800, 0xFFFF5722, 0xFF795548, 0xFF9E9E9E, 0xFF607D8B};

        for (Map.Entry<String, List<Double>> entry : operationTimes.entrySet()) {
            String methodName = entry.getKey();
            List<Double> times = entry.getValue();

            List<Double> limitedTimes = times.size() > MAX_DATA_POINTS
                    ? times.subList(0, MAX_DATA_POINTS)
                    : times;

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < limitedTimes.size(); i++) {
                entries.add(new Entry(i, limitedTimes.get(i).floatValue()));
            }

            if (!entries.isEmpty()) {
                LineDataSet dataSet = new LineDataSet(entries, methodName);
                dataSet.setLineWidth(2f);
                dataSet.setColor(colors[colorIndex % colors.length]);
                dataSet.setDrawCircles(false);
                dataSets.add(dataSet);
                colorIndex++;
            }
        }

        LineData lineData = new LineData();
        for (LineDataSet set : dataSets) {
            lineData.addDataSet(set);
        }

        memoryLineChart.setData(lineData);
        memoryLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        memoryLineChart.getDescription().setEnabled(false);
        memoryLineChart.getLegend().setEnabled(false); // Disable the default legend
        memoryLineChart.animateX(1500);
        memoryLineChart.invalidate();

        populateLegend(dataSets);
    }

    private void populateLegend(List<LineDataSet> dataSets) {
        LinearLayout memoryLegendContainer = findViewById(R.id.memoryLegendContainer);
        memoryLegendContainer.removeAllViews(); // Clear any existing views

        for (LineDataSet dataSet : dataSets) {
            TextView legendItem = new TextView(this);
            legendItem.setText(dataSet.getLabel());
            legendItem.setTextSize(14f);
            legendItem.setPadding(20, 0, 20, 0); // Add some spacing
            legendItem.setTextColor(dataSet.getColor());

            memoryLegendContainer.addView(legendItem);
        }
    }
}
