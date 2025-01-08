package com.example.benchmarkapp.controller.benchmarks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.benchmarkapp.R;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class MemoryBenchmark {

    private static final int ARRAY_SIZE = 1024 * 1024; //1 miliaone de elemente
    private int[] dataArray;
    private Random random;
    private Context context;
    private static final String LOG_FILE_NAME = "benchmark_results_memory.txt";

    public MemoryBenchmark(Context context) {
        this.dataArray = new int[ARRAY_SIZE];
        this.random = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            dataArray[i] = random.nextInt();
        }
        this.context = context;
    }

    public double latencyTest() {
        clearLogFile();
        long totalTime = 0;
        int numAccesses = 10000;
        int numAccessesPerIteration = 5000;

        for (int i = 0; i < numAccesses; i++) {
            long startTime = System.nanoTime();

            for (int j = 0; j < numAccessesPerIteration; j++) {
                int randomIndex = random.nextInt(ARRAY_SIZE);
                int value = dataArray[randomIndex];
            }

            long endTime = System.nanoTime();
            totalTime += endTime - startTime;

            if(i % 10 == 0) {
                double timeTaken = (endTime - startTime) / 1_000_000.0;
                logOperationTimeToFile("latencyTest", timeTaken);
            }
        }

        return (totalTime / (double) numAccesses) / 1_000_000.0;
    }

    public double bandwidthTest() {
        int dataSizeMB = 100;
        int[] dataBlock = new int[dataSizeMB * 1024 * 1024 / 4]; // 4 bytes/int
        long totalTime = 0;

        //w
        long startTime = System.nanoTime();
        for (int i = 0; i < dataBlock.length; i++) {
            dataBlock[i] = i;
        }
        long endTime = System.nanoTime();
        totalTime = endTime - startTime;

        //r
        startTime = System.nanoTime();
        for(int i = 0; i < dataBlock.length; i++) {
            int value = dataBlock[i];
        }
        endTime = System.nanoTime();
        totalTime += endTime - startTime;

        double result =  dataSizeMB / (totalTime / 1_000_000_000.0);
        return result;
    }

    public double mopsTest() {
        //folosesc BigInteger ca sa am acces la valori mai mari
        //pt numOperations 100000, mops e infinit
        //fara BigInteger, metoda e mai mult CPU-bound
        //fiind BigInteger, se da split in RAM si CPU

        BigInteger numOperations = new BigInteger("10000000");
        BigInteger result = BigInteger.ZERO;
        long totalTime = 0;
        long startTime = System.nanoTime();

        for (BigInteger i = BigInteger.ZERO; i.compareTo(numOperations) < 0; i = i.add(BigInteger.ONE)) {
            long operationStartTime = System.nanoTime();
            result = result.add(i);
            long operationEndTime = System.nanoTime();

            if(i.mod(new BigInteger("10000")).equals(BigInteger.ZERO)) {
                double timeTaken = (operationEndTime - operationStartTime) / 1_000_000.0;
                logOperationTimeToFile("mopsTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();

        totalTime = (endTime - startTime) / 1_000_000_000;

        double mops =  (double) numOperations.longValue() / totalTime;
        return mops;
    }

    public double memoryCopyTest() {
        int dataSizeMB = 10;
        int[] sourceData = new int[dataSizeMB * 1024 * 1024 / 4];
        int[] destData = new int[dataSizeMB * 1024 * 1024 / 4];

        for (int i = 0; i < sourceData.length; i++) {
            sourceData[i] = i;
        }

        long startTime = System.nanoTime();

        //source -> destination
        System.arraycopy(sourceData, 0, destData, 0, sourceData.length);

        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;

        return dataSizeMB / (totalTime / 1_000_000_000.0);
    }

    public double allocationDeallocationTest()  {
        int numAllocations = 10000;
        long totalTime = 0;
        int size = 1024 * 1024;

        for (int i = 0; i < numAllocations; i++) {
            long startTime = System.nanoTime();

            int[] tempArray = new int[size]; //alocare
            //Thread.sleep(1); //simulam ceva timp de procesare
            tempArray = null; //dealocare -> aici intra Garbage Collection ul

            long endTime = System.nanoTime();
            totalTime += endTime - startTime;

            if(i % 10 == 0) {
                double timeTaken = (endTime - startTime) / 1_000_000.0;
                logOperationTimeToFile("allocationDeallocationTest", timeTaken);
            }
        }

        //calculcam average latency pt o singura alocare/dealocare
        return (totalTime / (double) numAllocations) / 1_000_000.0;
    }

    public double memoryLatencyUnderLoadTest() {
        int loadArraySize = 2 * 1024 * 1024;
        int[] loadArray = new int[loadArraySize];
        int numAccesses = 10000;
        long totalTime = 0;
        int numAccessesPerIteration = 5000;

        //aici un background thread care face heavy load
        Thread loadThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < loadArraySize; i++) {
                    int randomIndexT = random.nextInt(loadArraySize);
                    loadArray[randomIndexT] = randomIndexT; // Non-sequential access
                }
            }
            try {
                Thread.sleep(1); //ca sa nu am CPU consumption excesiv
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        loadThread.start();

        //in main thread masuram in timp ce in background e load
        Random random = new Random();

        for (int i = 0; i < numAccesses; i++) {
            long startTime = System.nanoTime();

            for (int j = 0; j < numAccessesPerIteration; j++) {
                int randomIndex = random.nextInt(ARRAY_SIZE);
                int value = dataArray[randomIndex];
            }

            long endTime = System.nanoTime();
            totalTime += endTime - startTime;

            if(i % 100 == 0) {
                double timeTaken = (endTime - startTime) / 1_000_000.0;
                logOperationTimeToFile("memoryLatencyUnderLoadTest", timeTaken);
            }
        }

        loadThread.interrupt();
        try {
            loadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return (totalTime / (double) numAccesses) / 1_000_000.0;
    }

    public double cacheHitTest() {
        long startTime = System.nanoTime();
        int sum = 0;
        for (int i = 0; i < ARRAY_SIZE; i++) {
            long operationStartTime = System.nanoTime();
            sum += dataArray[i];
            long operationEndTime = System.nanoTime();

            if(i % 1000 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1_000_000.0;
                logOperationTimeToFile("cacheHitTest", timeTaken);
            }
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;

        //elementele alaturate in memorie sunt aduse in cache la un loc
        //simulam cache hit pt ca luam elemente vecine din memorie
    }
    public double cacheMissTest() {
        long startTime = System.nanoTime();
        int sum = 0;

        for (int i = 0; i < ARRAY_SIZE; i += 64) {
            sum += dataArray[i];
        }

        for (int i = 0; i < ARRAY_SIZE; i += 1024) {
            sum += dataArray[i];
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000.0;
        //folosim 64 ca stride -> mai multe miss uri
        //CPU da reload la locatii distante din memorie, deci cresc sansele de cache miss
    }
    public double cacheHitMissRatio() {
        double hitTime = cacheHitTest();
        double missTime = cacheMissTest();
        return hitTime / missTime;
    }

    public double matrixMultiplicationTest() {
        int size = 512;
        double[][] matrixA = new double[size][size];
        double[][] matrixB = new double[size][size];
        double[][] resultMatrix = new double[size][size];

        Random random = new Random();
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                matrixA[i][j] = random.nextDouble();
                matrixB[i][j] = random.nextDouble();
            }
        }

        long startTime = System.nanoTime();

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                long operationStartTime = System.nanoTime();
                resultMatrix[i][j] = 0;
                for(int k = 0; k < size; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
                long operationEndTime = System.nanoTime();

                if(i % 10 == 0 && j % 10 == 0) {
                    double timeTaken = (operationEndTime - operationStartTime) / 1_000_000.0;
                    logOperationTimeToFile("matrixMultiplicationTest", timeTaken);
                }
            }
        }

        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / 1_000_000_000.0;

        double flops = (2.0 * size * size * size) / elapsedTime; //inmultim cu 2 pt ca avem 2 op: adunare + inmultire

        double gflops =  flops / 1_000_000_000.0; //giga flops
        return gflops;
        //ca la CPU, test sintetic doar ca aici masuram giga flops, nu timpul
    }

    public long imageProcessingBrightnessTest() {
        InputStream imageStream = context.getResources().openRawResource(R.raw.cat);
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

        if(originalBitmap == null) {
            throw new IllegalArgumentException("Invalid image file");

        }

        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        long startTime = System.nanoTime();

        for(int y = 0; y < bitmap.getHeight(); y++) {
            for(int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);

                int red = Math.min(255, ((pixel >> 16) & 0xff) + 20);
                int green = Math.min(255, ((pixel >> 8) & 0xff) + 20);
                int blue = Math.min(255, (pixel & 0xff) + 20);

                bitmap.setPixel(x, y, (0xff << 24) | (red << 16) | (green << 8) | blue);
            }
        }
        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
        //test real-life, pe poza "Cat" din folder ul raw
    }

    public long imageProcessingGrayScaleTest() {
        InputStream imageStream = context.getResources().openRawResource(R.raw.cat);
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);

        if(originalBitmap == null) {
            throw new IllegalArgumentException("Invalid image file");
        }

        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        long startTime = System.nanoTime();

        for(int y = 0; y < bitmap.getHeight(); y++) {
            for(int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);

                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);

                bitmap.setPixel(x, y, (0xff << 24) | (gray << 16) | (gray << 8) | gray);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
        //test real-life, coloreaza o poza in gri full
    }

    private void logOperationTimeToFile(String methodName, double time) {
        try (FileOutputStream fos = context.openFileOutput(LOG_FILE_NAME, Context.MODE_APPEND);
             OutputStreamWriter writer = new OutputStreamWriter(fos)) {
            writer.write(methodName + "," + time + "\n");
        } catch (IOException e) {
            Timber.e("Error logging operation time: %s", e.getMessage());
        }
    }

    public Map<String, List<Double>> readOperationTimesFromFile() {
        Map<String, List<Double>> operationTimesMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(LOG_FILE_NAME)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Each line is "MethodName,Time"
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String methodName = parts[0];
                    double time = Double.parseDouble(parts[1]);

                    // Check if the methodName already exists, else initialize the list
                    if (!operationTimesMap.containsKey(methodName)) {
                        operationTimesMap.put(methodName, new ArrayList<>());
                    }
                    operationTimesMap.get(methodName).add(time);
                }
            }
        } catch (IOException | NumberFormatException e) {
            Timber.e("Error reading operation times file: %s", e.getMessage());
        }

        return operationTimesMap;
    }

    public void clearLogFile() {
        try (FileOutputStream fos = context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(new byte[0]); // Truncate the file
        } catch (IOException e) {
            Timber.e("Error clearing log file: %s", e.getMessage());
        }
    }

}
