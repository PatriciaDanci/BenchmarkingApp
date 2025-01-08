package com.example.benchmarkapp.controller;

import com.example.benchmarkapp.R;
import com.example.benchmarkapp.model.BenchmarkResult;
import com.example.benchmarkapp.controller.benchmarks.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import timber.log.Timber;
import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLSurfaceView;

import java.util.Map;
import java.util.Random;

import javax.crypto.SecretKey;

public class BenchmarkController {

    private List<BenchmarkResult> benchmarkResults;
    private BenchmarkResult currentResult;
    private Context context;
    private CpuBenchmark cpuBenchmark;
    private MemoryBenchmark memoryBenchmark;
    public BenchmarkController(Context context) {
        this.benchmarkResults = new ArrayList<>();
        this.context = context;
        this.currentResult = new BenchmarkResult("", 0, 0, 0, 0, 0);
        this.cpuBenchmark = new CpuBenchmark(context);
        this.memoryBenchmark = new MemoryBenchmark(context);
    }

    public void finalizeBenchmark() {
        if(currentResult != null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            currentResult.setTimestamp(timestamp);
            benchmarkResults.add(currentResult);

            currentResult = new BenchmarkResult("", 0, 0, 0, 0, 0);
        } else {
            throw new IllegalStateException("No benchmark results to finalize!");
        }
    }

    public int computeOverallScore(BenchmarkResult result) {
        final int REFERENCE_THRESHOLD = 1000;

        double normalizedCpuSingle = REFERENCE_THRESHOLD - (double) result.getCpuScoreSingle();
        double normalizedCpuMulti = REFERENCE_THRESHOLD - (double) result.getCpuScoreMulti();
        double normalizedGpu = REFERENCE_THRESHOLD + (double) result.getGpuScore();
        double normalizedMemBandwidth = REFERENCE_THRESHOLD + (double) result.getMemoryBandwidthScore();
        double normalizedMemLatency = REFERENCE_THRESHOLD - (double) result.getMemoryLatencyScore();

        double overallScore = normalizedGpu +
                normalizedMemBandwidth +
                normalizedMemLatency +
                normalizedCpuSingle +
                normalizedCpuMulti;

        return (int) overallScore;
    }

    public BenchmarkResult performCpuBenchmark() {
        CpuBenchmarkMulti cpuBenchmarkMulti = new CpuBenchmarkMulti(cpuBenchmark, context);

        long integerCalcTime = cpuBenchmark.integerCalculationTest();
        long floatingPointCalcTime = cpuBenchmark.floatingPointCalculationTest();
        long loopIntensityTime = cpuBenchmark.loopIntensityTest();
        long primeCalcTime = cpuBenchmark.primeNumberCalculationTest();
        long matrixMultiplicationTime = cpuBenchmark.matrixMultiplicationTest();
        long quickSortTime = cpuBenchmark.quickSortTest();
        long FFTTime = cpuBenchmark.fftTest();
        long nBodyTime = cpuBenchmark.nBodyTest();
        long sha256Time = cpuBenchmark.sha256HashTest();
        long convolutionTime = cpuBenchmark.convolutionTest();

        long compressionTime = 0;
        long decompressionTime = 0;
        byte[] compressedData = null;

        long aesEncryptionTime = 0;
        long aesDecryptionTime = 0;
        long matrixExponentiationTime = cpuBenchmark.matrixExponentiationTest();

        try {
            aesEncryptionTime = cpuBenchmark.aesEncryptionTest();
            aesDecryptionTime = cpuBenchmark.aesDecryptionTest();
        } catch (Exception e) {
            System.out.println("AES Encryption/Decryption failed: " + e.getMessage());
        }

        try {
            compressionTime += cpuBenchmark.compressFileTest(R.raw.harrypotter1);
            compressedData = cpuBenchmark.getCompressedDataForResource(R.raw.harrypotter1);
            if (compressedData != null) {
                decompressionTime += cpuBenchmark.decompressFileTest(compressedData);
            }

            compressionTime += cpuBenchmark.compressFileTest(R.raw.cat1);
            compressedData = cpuBenchmark.getCompressedDataForResource(R.raw.cat1);
            if (compressedData != null) {
                decompressionTime += cpuBenchmark.decompressFileTest(compressedData);
            }

            compressionTime += cpuBenchmark.compressFileTest(R.raw.universe1);
            compressedData = cpuBenchmark.getCompressedDataForResource(R.raw.universe1);
            if (compressedData != null) {
                decompressionTime += cpuBenchmark.decompressFileTest(compressedData);
            }

         } catch (Exception e) {
            //Timber.e("Compression/Decompression failed: %s", e.getMessage());
            System.out.println("Compression/Decompression failed: " + e.getMessage());
        }

        long imageProcessingTime = performImageProcessingBenchmark(R.raw.cat, cpuBenchmark);

        double logSum = Math.log(integerCalcTime) + Math.log(floatingPointCalcTime) + Math.log(loopIntensityTime)
                + Math.log(primeCalcTime) + Math.log(matrixMultiplicationTime) + Math.log(quickSortTime)
                + Math.log(FFTTime) + Math.log(nBodyTime) + Math.log(sha256Time)
                + Math.log(compressionTime) + Math.log(decompressionTime) + Math.log(imageProcessingTime) + Math.log(convolutionTime)
                + Math.log(aesEncryptionTime) + Math.log(aesDecryptionTime) + Math.log(matrixExponentiationTime);

        double geometricMean = Math.exp(logSum / 16);
        long totalScore = (long) geometricMean;

        System.out.println("CPU Integer Calculation Time: " + integerCalcTime + " ms");
        System.out.println("CPU Floating-Point Calculation Time: " + floatingPointCalcTime + " ms");
        System.out.println("CPU Loop Intensity Time: " + loopIntensityTime + " ms");
        System.out.println("CPU Prime Calculation Time: " + primeCalcTime + " ms");
        System.out.println("CPU Matrix Multiplication Time: " + matrixMultiplicationTime + " ms");
        System.out.println("CPU QuickSort Time: " + quickSortTime + " ms");
        System.out.println("CPU FFT Time: " + FFTTime + " ms");
        System.out.println("CPU N-Body Calculation Time: " + nBodyTime + " ms");
        System.out.println("CPU SHA-256 Hash Calculation Time: " + sha256Time + " ms");
        System.out.println("CPU Compression Time: " + compressionTime + " ms");
        System.out.println("CPU Decompression Time: " + decompressionTime + " ms");
        System.out.println("CPU Image Processing Time: " + imageProcessingTime + " ms");
        System.out.println("CPU Convolution Time: " + convolutionTime + " ms");
        System.out.println("CPU AES Encryption Time: " + aesEncryptionTime + " ms");
        System.out.println("CPU AES Decryption Time: " + aesDecryptionTime + " ms");
        System.out.println("CPU Matrix Exponentiation Time: " + matrixExponentiationTime + " ms");

        System.out.println("\n");

        long integerCalcTimeMulti = 0;
        long floatingPointCalcTimeMulti = 0;
        long loopIntensityTimeMulti = 0;
        long primeCalcTimeMulti = 0;
        long matrixMultiplicationTimeMulti = 0;
        long quickSortTimeMulti = 0;
        long FFTTimeMulti = 0;
        long nBodyTimeMulti = 0;
        long sha256TimeMulti = 0;
        long convolutionTimeMulti = 0;
        long compressionTimeMulti = 0;
        long decompressionTimeMulti = 0;
        long imageProcessingTimeMulti = -1;
        long test1 = 1, test2 = 1, test3 = 1;
        long aesEncryptionTimeMulti = 0;
        long aesDecryptionTimeMulti = 0;
        long matrixExponentiationTimeMulti = 0;

        try {
            integerCalcTimeMulti = cpuBenchmarkMulti.integerCalculationTestMulti();
            floatingPointCalcTimeMulti = cpuBenchmarkMulti.floatingPointCalculationTestMulti();
            loopIntensityTimeMulti = cpuBenchmarkMulti.loopIntensityTestMulti();
            primeCalcTimeMulti = cpuBenchmarkMulti.primeNumberCalculationTestMulti();
            matrixMultiplicationTimeMulti = cpuBenchmarkMulti.matrixMultiplicationTestMulti();
            quickSortTimeMulti = cpuBenchmarkMulti.quickSortTestMulti();
            FFTTimeMulti = cpuBenchmarkMulti.fftTestMulti();
            nBodyTimeMulti = cpuBenchmarkMulti.nBodyTestMulti();
            sha256TimeMulti = cpuBenchmarkMulti.sha256HashTestMulti();
            convolutionTimeMulti = cpuBenchmarkMulti.convolutionTestMulti();
            matrixExponentiationTimeMulti = cpuBenchmarkMulti.matrixExponentiationTestMulti();

            List<byte[]> compressedChunksHarryPotter = new ArrayList<>();
            compressionTimeMulti += cpuBenchmarkMulti.compressFileTestMulti(R.raw.harrypotter1, compressedChunksHarryPotter);
            if (!compressedChunksHarryPotter.isEmpty()) {
                decompressionTimeMulti += cpuBenchmarkMulti.decompressFileTestMulti(compressedChunksHarryPotter);
            } else {
                decompressionTimeMulti = -1;
                test1 = 0;
            }

            List<byte[]> compressedChunksCat = new ArrayList<>();
            compressionTimeMulti += cpuBenchmarkMulti.compressFileTestMulti(R.raw.cat1, compressedChunksCat);
            if (!compressedChunksCat.isEmpty()) {
                decompressionTimeMulti += cpuBenchmarkMulti.decompressFileTestMulti(compressedChunksCat);
            } else {
                decompressionTimeMulti = -1;
                test2 = 0;
            }

            List<byte[]> compressedChunksUniverse = new ArrayList<>();
            compressionTimeMulti += cpuBenchmarkMulti.compressFileTestMulti(R.raw.universe1, compressedChunksUniverse);
            if (!compressedChunksUniverse.isEmpty()) {
                decompressionTimeMulti += cpuBenchmarkMulti.decompressFileTestMulti(compressedChunksUniverse);
            } else {
                decompressionTimeMulti = -1;
                test3 = 0;
            }

            imageProcessingTimeMulti = performImageProcessingBenchmarkMulti(R.raw.cat1, cpuBenchmarkMulti);

            aesEncryptionTimeMulti = cpuBenchmarkMulti.aesEncryptionTestMulti();
            aesDecryptionTimeMulti = cpuBenchmarkMulti.aesDecryptionTestMulti();

        } catch (Exception e) {
            System.out.println("Multi-core Compression/Decompression or other test failed: " + e.getMessage());
        }


        double logSumMulti = Math.log(integerCalcTimeMulti) + Math.log(floatingPointCalcTimeMulti) + Math.log(loopIntensityTimeMulti)
                + Math.log(primeCalcTimeMulti) + Math.log(matrixMultiplicationTimeMulti) + Math.log(quickSortTimeMulti)
                + Math.log(FFTTimeMulti) + Math.log(nBodyTimeMulti) + Math.log(sha256TimeMulti)
                + Math.log(compressionTimeMulti) + Math.log(decompressionTimeMulti) + Math.log(imageProcessingTimeMulti) + Math.log(convolutionTimeMulti)
                + Math.log(aesEncryptionTimeMulti) + Math.log(aesDecryptionTimeMulti) + Math.log(matrixExponentiationTimeMulti);

        double geometricMeanMulti = Math.exp(logSumMulti / 16);
        long totalScoreMulti = (long) geometricMeanMulti;

        System.out.println("Number of cores: " + cpuBenchmarkMulti.getNumCores());

        System.out.println("CPU Integer Calculation Time (Multi-core): " + integerCalcTimeMulti + " ms");
        System.out.println("CPU Floating-Point Calculation Time (Multi-core): " + floatingPointCalcTimeMulti + " ms");
        System.out.println("CPU Loop Intensity Time (Multi-core): " + loopIntensityTimeMulti + " ms");
        System.out.println("CPU Prime Calculation Time (Multi-core): " + primeCalcTimeMulti + " ms");
        System.out.println("CPU Matrix Multiplication Time (Multi-core): " + matrixMultiplicationTimeMulti + " ms");
        System.out.println("CPU QuickSort Time (Multi-core): " + quickSortTimeMulti + " ms");
        System.out.println("CPU FFT Time (Multi-core): " + FFTTimeMulti + " ms");
        System.out.println("CPU N-Body Calculation Time (Multi-core): " + nBodyTimeMulti + " ms");
        System.out.println("CPU SHA-256 Hash Calculation Time (Multi-core): " + sha256TimeMulti + " ms");
        System.out.println("CPU Compression Time (Multi-core): " + compressionTimeMulti + " ms");
        System.out.println("CPU Decompression Time (Multi-core): " + decompressionTimeMulti + " ms");
        System.out.println("CPU Image Processing Time (Multi-core): " + imageProcessingTimeMulti + " ms");
        System.out.println("CPU Convolution Time (Multi-core): " + convolutionTimeMulti + " ms");
        System.out.println("CPU AES Encryption Time (Multi-core): " + aesEncryptionTimeMulti + " ms");
        System.out.println("CPU AES Decryption Time (Multi-core): " + aesDecryptionTimeMulti + " ms");
        System.out.println("CPU Matrix Exponentiation Time (Multi-core): " + matrixExponentiationTimeMulti + " ms");
        System.out.println("test1: " + test1);
        System.out.println("test2: " + test2);
        System.out.println("test3: " + test3);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        BenchmarkResult result = new BenchmarkResult(timestamp, (int) totalScore, (int) totalScoreMulti, 0, 0, 0);

        //benchmarkResults.add(result);
        currentResult.setCpuScoreSingle(result.getCpuScoreSingle());
        currentResult.setCpuScoreMulti(result.getCpuScoreMulti());
        return result;
    }

    public BenchmarkResult performMemoryBenchmark() {
        //latency
        double latencyTime = memoryBenchmark.latencyTest();
        double allocationDeallocationTime = memoryBenchmark.allocationDeallocationTest();
        double memoryLatencyUnderLoadTime = memoryBenchmark.memoryLatencyUnderLoadTest();
        double cacheHitTime = memoryBenchmark.cacheHitTest();
        double cacheMissTime = memoryBenchmark.cacheMissTest();

        //bandwidth
        double bandwidth = memoryBenchmark.bandwidthTest();
        double memoryCopySpeed = memoryBenchmark.memoryCopyTest();
        double mops = memoryBenchmark.mopsTest();
        double matrixMultiplicationGflops = memoryBenchmark.matrixMultiplicationTest();

        //real-life
        long imageProcessingBrightnessTime = memoryBenchmark.imageProcessingBrightnessTest();
        long imageProcessingGrayScaleTime = memoryBenchmark.imageProcessingGrayScaleTest();

        double latencyLogSum = Math.log(latencyTime) + Math.log(allocationDeallocationTime) + Math.log(memoryLatencyUnderLoadTime)
                + Math.log(cacheHitTime) + Math.log(cacheMissTime)
                + Math.log(imageProcessingBrightnessTime) + Math.log(imageProcessingGrayScaleTime);
        double latencyGeometricMean = Math.exp(latencyLogSum / 7);

        double bandwidthLogSum = Math.log(bandwidth) + Math.log(memoryCopySpeed) + Math.log(mops) + Math.log(matrixMultiplicationGflops);
        double bandwidthGeometricMean = Math.exp(bandwidthLogSum / 4);

        //double totalScore = Math.sqrt(latencyGeometricMean * bandwidthGeometricMean);

        System.out.println("Memory Latency Time: " + latencyTime + " ms");
        System.out.println("Allocation/Deallocation Time: " + allocationDeallocationTime + " ms");
        System.out.println("Memory Latency Under Load Time: " + memoryLatencyUnderLoadTime + " ms");
        System.out.println("Cache Hit Time: " + cacheHitTime + " ms");
        System.out.println("Cache Miss Time: " + cacheMissTime + " ms");
        System.out.println("Memory Bandwidth: " + bandwidth + " MB/s");
        System.out.println("Memory Copy Speed: " + memoryCopySpeed + " MB/s");
        System.out.println("MOPS: " + mops + " operations/s");
        System.out.println("Matrix Multiplication Speed: " + matrixMultiplicationGflops + " GFLOPS");
        System.out.println("Image Processing (Brightness Adjustment) Time: " + imageProcessingBrightnessTime + " ms");
        System.out.println("Image Processing (Grayscale) Time: " + imageProcessingGrayScaleTime + " ms");
        System.out.println("latencyGeometricMean: " + latencyGeometricMean);
        System.out.println("bandwidthGeometricMean: " + bandwidthGeometricMean);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        BenchmarkResult result = new BenchmarkResult(timestamp, 0,0, 0, (int) latencyGeometricMean, (int) bandwidthGeometricMean);

        //benchmarkResults.add(result);
        currentResult.setMemoryLatencyScore(result.getMemoryLatencyScore());
        currentResult.setMemoryBandwidthScore(result.getMemoryBandwidthScore());
        return result;
    }

    public BenchmarkResult performGpuBenchmark() {
        GpuBenchmark gpuBenchmark = new GpuBenchmark(context);
        Thread gpuThread = new Thread(gpuBenchmark);
        gpuThread.start();

        try {
            gpuThread.join(); //asteptam sa fie gata benchmark-ul
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long totalFrames = gpuBenchmark.getFrameCount();
        System.out.println("Total Frames Rendered (GPU): " + totalFrames);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        BenchmarkResult result = new BenchmarkResult(timestamp, 0, 0, (int) totalFrames, 0, 0);

        //benchmarkResults.add(result);
        currentResult.setGpuScore(result.getGpuScore());
        return result;
    }

    public List<BenchmarkResult> getBenchmarkResults() {
        return benchmarkResults;
    }

    private long performImageProcessingBenchmark(int resourceId, CpuBenchmark cpuBenchmark) {
        try {
            InputStream imageStream = context.getResources().openRawResource(resourceId);
            return cpuBenchmark.imageProcessingTest(imageStream);
        } catch (Resources.NotFoundException e) {
            Timber.e("Image resource not found: %s", e.getMessage());
            System.out.println("Image resource not found: " + e.getMessage());
            return -1;
        }
    }

    private long performImageProcessingBenchmarkMulti(int resourceId, CpuBenchmarkMulti cpuBenchmarkMulti) {
        try {
            InputStream imageStream = context.getResources().openRawResource(resourceId);
            return cpuBenchmarkMulti.imageProcessingTestMulti(imageStream);
        } catch (Resources.NotFoundException e) {
            Timber.e("Image resource not found: %s", e.getMessage());
            System.out.println("Image resource not found: " + e.getMessage());
            return -3;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] generateRandomData(int size) {
        byte[] data = new byte[size];
        Random random = new Random();
        random.nextBytes(data); // Fill the byte array with random data
        return data;
    }

    public void clearBenchmarkResults() {
        benchmarkResults.clear();
    }

    public Map<String, List<Double>> getCpuOperationTimes() {
        return cpuBenchmark.readOperationTimesFromFile();
    }

    public Map<String, List<Double>> getMemoryOperationTimes() {
        return memoryBenchmark.readOperationTimesFromFile();
    }

}
