package com.example.benchmarkapp.model.info;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

public class MemoryInfo {

    private final Context context;

    public MemoryInfo(Context context) {
        this.context = context;
    }

    /**
     * Retrieves memory details of the device.
     *
     * @return A formatted string containing memory information.
     */
    public String getMemoryDetails() {
        StringBuilder memoryDetails = new StringBuilder();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            long totalRam = memoryInfo.totalMem / (1024 * 1024); // Convert to MB
            long availableRam = memoryInfo.availMem / (1024 * 1024); // Convert to MB
            long usedRam = totalRam - availableRam;
            boolean lowMemory = memoryInfo.lowMemory;
            long threshold = memoryInfo.threshold / (1024 * 1024); // Convert to MB

            int memoryClass = activityManager.getMemoryClass();
            boolean largeHeap = (context.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP) != 0;

            memoryDetails.append("Total RAM: ").append(totalRam).append(" MB\n");
            memoryDetails.append("Available RAM: ").append(availableRam).append(" MB\n");
            memoryDetails.append("Used RAM: ").append(usedRam).append(" MB\n");
            memoryDetails.append("Threshold (Low Memory): ").append(threshold).append(" MB\n");
            memoryDetails.append("Low Memory State: ").append(lowMemory ? "Yes" : "No").append("\n");
            memoryDetails.append("App Memory Class: ").append(memoryClass).append(" MB\n");
            memoryDetails.append("Large Heap Enabled: ").append(largeHeap ? "Yes" : "No").append("\n");
        } else {
            memoryDetails.append("Unable to fetch memory details.");
        }

        return memoryDetails.toString();
    }
}
