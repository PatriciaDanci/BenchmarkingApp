package com.example.benchmarkapp.model.info;

import android.os.Build;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CPUInfo {

    /**
     * Retrieves CPU details for the current device.
     * @return A formatted string containing CPU details.
     */
    public String getCPUDetails() {
        StringBuilder cpuDetails = new StringBuilder();

        cpuDetails.append("Vendor: ").append(getCPUVendor()).append("\n");
        cpuDetails.append("Model: ").append(getCPUModel()).append("\n");

        cpuDetails.append("Processor: ").append(getProcessorInfo()).append("\n");

        cpuDetails.append("Cores: ").append(Runtime.getRuntime().availableProcessors()).append("\n");

        cpuDetails.append("Clock Speed: ").append(getCPUClockSpeed()).append(" MHz\n");

        cpuDetails.append("CPU ABI: ").append(Build.CPU_ABI).append("\n");
        cpuDetails.append("CPU ABI2: ").append(Build.CPU_ABI2).append("\n");

        return cpuDetails.toString();
    }

    /**
     * Retrieves the CPU vendor from /proc/cpuinfo.
     * @return Vendor name or "Unknown" if not found.
     */
    private String getCPUVendor() {
        return readFromCpuInfo("Hardware");
    }

    /**
     * Retrieves the CPU model from /proc/cpuinfo.
     * @return Model name or "Unknown" if not found.
     */
    private String getCPUModel() {
        return readFromCpuInfo("model name");
    }

    /**
     * Retrieves the CPU clock speed in MHz from /proc/cpuinfo.
     * @return Clock speed or "Unknown" if not found.
     */
    private String getCPUClockSpeed() {
        return readFromCpuInfo("cpu MHz");
    }

    /**
     * Retrieves the processor name from /proc/cpuinfo.
     * @return Processor name or "Unknown" if not found.
     */
    private String getProcessorInfo() {
        return readFromCpuInfo("Processor");
    }

    /**
     * Reads specific information from /proc/cpuinfo.
     * @param key The key to search for (e.g., "model name").
     * @return The value associated with the key or "Unknown".
     */
    private String readFromCpuInfo(String key) {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(key.toLowerCase())) {
                    return line.split(":")[1].trim();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }
}
