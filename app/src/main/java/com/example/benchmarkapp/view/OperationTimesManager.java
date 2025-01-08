package com.example.benchmarkapp.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperationTimesManager {
    private static OperationTimesManager instance;
    private Map<String, List<Double>> operationTimes;

    private OperationTimesManager() {
        operationTimes = new HashMap<>();
    }

    public static OperationTimesManager getInstance() {
        if (instance == null) {
            instance = new OperationTimesManager();
        }
        return instance;
    }

    public void setOperationTimes(Map<String, List<Double>> operationTimes) {
        this.operationTimes = operationTimes;
    }

    public Map<String, List<Double>> getOperationTimes() {
        return operationTimes;
    }
}