package com.example.benchmarkapp.model;

import java.io.Serializable;

public class BenchmarkResult implements Serializable {
    private String timestamp;
    private int cpuScoreSingle;
    private int cpuScoreMulti;
    private int gpuScore;
    private int memoryLatencyScore;
    private int memoryBandwidthScore;

    public BenchmarkResult(String timestamp, int cpuScoreSingle, int cpuScoreMulti, int gpuScore, int memoryLatencyScore, int memoryBandwidthScore) {
        this.timestamp = timestamp;
        this.cpuScoreSingle = cpuScoreSingle;
        this.cpuScoreMulti = cpuScoreMulti;
        this.gpuScore = gpuScore;
        this.memoryLatencyScore = memoryLatencyScore;
        this.memoryBandwidthScore = memoryBandwidthScore;
    }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public int getCpuScoreMulti() {
        return cpuScoreMulti;
    }
    public void setCpuScoreMulti(int cpuScoreMulti) {
        this.cpuScoreMulti = cpuScoreMulti;
    }
    public int getCpuScoreSingle() {
        return cpuScoreSingle;
    }
    public void setCpuScoreSingle(int cpuScoreSingle) {
        this.cpuScoreSingle = cpuScoreSingle;
    }
    public int getGpuScore() { return gpuScore; }
    public void setGpuScore(int gpuScore) { this.gpuScore = gpuScore; }
    public int getMemoryLatencyScore() {
        return memoryLatencyScore;
    }
    public void setMemoryLatencyScore(int memoryLatencyScore) {
        this.memoryLatencyScore = memoryLatencyScore;
    }
    public int getMemoryBandwidthScore() {
        return memoryBandwidthScore;
    }
    public void setMemoryBandwidthScore(int memoryBandwidthScore) {
        this.memoryBandwidthScore = memoryBandwidthScore;
    }

}
