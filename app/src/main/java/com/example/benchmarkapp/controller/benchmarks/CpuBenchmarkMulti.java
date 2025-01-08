package com.example.benchmarkapp.controller.benchmarks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import timber.log.Timber;

public class CpuBenchmarkMulti {
    private CpuBenchmark cpuBenchmark;
    private int numCores;
    private ExecutorService executorService;
    private Context context;

    public CpuBenchmarkMulti(CpuBenchmark cpuBenchmark, Context context) {
        this.cpuBenchmark = cpuBenchmark;
        this.context = context;
        this.numCores = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(numCores);
    }
    public int getNumCores() {
        return numCores;
    }
    public long integerCalculationTestMulti() throws InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        int tasks = numCores;
        long iterationsPerTask = 100000 / tasks;

        List<Callable<BigInteger>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            tasksList.add(() -> {
                BigInteger result = BigInteger.ZERO;
                for (long i = 0; i < iterationsPerTask; i++) {
                    result = result.add(BigInteger.valueOf(i).multiply(BigInteger.valueOf(i)));
                }
                return result;
            });
        }

        List<Future<BigInteger>> results = executorService.invokeAll(tasksList);

        BigInteger totalResult = BigInteger.ZERO;
        for (Future<BigInteger> result : results) {
            totalResult = totalResult.add(result.get());
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long floatingPointCalculationTestMulti() throws InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        int tasks = numCores;
        int iterationsPerTask = 1000000 / tasks;

        List<Callable<Double>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            tasksList.add(() -> {
                double result = 0.0;
                for (int i = 0; i < iterationsPerTask; i++) {
                    result += Math.sqrt(i) * Math.PI;
                }
                return result;
            });
        }

        List<Future<Double>> results = executorService.invokeAll(tasksList);

        double totalResult = 0.0;
        for (Future<Double> result : results) {
            totalResult += result.get();
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long loopIntensityTestMulti() throws InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        int tasks = numCores;
        int outerLoopPerTask = 1000 / tasks;

        List<Callable<BigInteger>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            tasksList.add(() -> {
                BigInteger result = BigInteger.ONE;
                for (int i = 0; i < outerLoopPerTask; i++) {
                    for (int j = 0; j < 100; j++) {
                        for (int k = 0; k < 100; k++) {
                            result = result.add(BigInteger.valueOf((long) i * j * k));
                        }
                    }
                }
                return result;
            });
        }

        List<Future<BigInteger>> results = executorService.invokeAll(tasksList);

        BigInteger totalResult = BigInteger.ZERO;
        for (Future<BigInteger> result : results) {
            totalResult = totalResult.add(result.get());
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long primeNumberCalculationTestMulti() throws InterruptedException, ExecutionException {
        long startTime = System.nanoTime();
        int tasks = numCores;
        int rangePerTask = 100000 / tasks;

        List<Callable<Integer>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            int start = t * rangePerTask;
            int end = (t + 1) * rangePerTask;
            tasksList.add(() -> {
                int count = 0;
                for (int i = start; i < end; i++) {
                    if (isPrime(i)) count++;
                }
                return count;
            });
        }

        List<Future<Integer>> results = executorService.invokeAll(tasksList);

        int totalPrimes = 0;
        for (Future<Integer> result : results) {
            totalPrimes += result.get();
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public long matrixMultiplicationTestMulti() throws InterruptedException, ExecutionException {
        int size = 300;
        int[][] matrixA = new int[size][size];
        int[][] matrixB = new int[size][size];
        int[][] result = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixA[i][j] = i + j;
                matrixB[i][j] = Math.abs(i - j);
            }
        }

        long startTime = System.nanoTime();

        List<Callable<Void>> tasksList = new ArrayList<>();
        int rowsPerTask = size / numCores;

        for (int t = 0; t < numCores; t++) {
            int startRow = t * rowsPerTask;
            int endRow = (t + 1) * rowsPerTask;
            tasksList.add(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < size; j++) {
                        result[i][j] = 0;
                        for (int k = 0; k < size; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
                return null;
            });
        }

        executorService.invokeAll(tasksList);

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long fftTestMulti() throws InterruptedException, ExecutionException {
        int size = 2048;
        double[] real = new double[size];
        double[] imag = new double[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            real[i] = Math.cos(2 * Math.PI * i / size);
            imag[i] = Math.sin(2 * Math.PI * i / size);
        }

        int tasks = numCores;
        int chunkSize = size / tasks;

        List<Callable<Void>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            int start = t * chunkSize;
            int end = (t == tasks - 1) ? size : (t + 1) * chunkSize;
            tasksList.add(() -> {
                fftChunk(real, imag, start, end);
                return null;
            });
        }

        long startTime = System.nanoTime();
        executorService.invokeAll(tasksList);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1000000;
    }
    private void fftChunk(double[] real, double[] imag, int start, int end) {
        for (int i = start; i < end; i++) {
            int n = real.length;
            int bits = (int) (Math.log(n) / Math.log(2));
            int reversed = reverseBits(i, bits);
            if (i < reversed) {
                double tempReal = real[i];
                double tempImag = imag[i];
                real[i] = real[reversed];
                imag[i] = imag[reversed];
                real[reversed] = tempReal;
                imag[reversed] = tempImag;
            }
        }
    }
    private int reverseBits(int num, int bits) {
        int reversed = 0;
        for(int i = 0; i < bits; i++) {
            reversed = (reversed << 1) | (num & 1);
            num >>= 1;
        }
        return reversed;
    }

    public long convolutionTestMulti() throws InterruptedException, ExecutionException {
        int[] input = new int[1000000];
        int[] kernel = {1, -1, 1};
        int[] output = new int[input.length];
        Random random = new Random();

        for (int i = 0; i < input.length; i++) {
            input[i] = random.nextInt(100);
        }

        int tasks = numCores;
        int chunkSize = input.length / tasks;

        List<Callable<Void>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            int start = t * chunkSize;
            int end = (t == tasks - 1) ? input.length : (t + 1) * chunkSize;
            tasksList.add(() -> {
                computeConvolutionChunk(input, kernel, output, start, end);
                return null;
            });
        }

        long startTime = System.nanoTime();
        executorService.invokeAll(tasksList);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1000000;
    }
    private void computeConvolutionChunk(int[] input, int[] kernel, int[] output, int start, int end) {
        int kernelRadius = kernel.length / 2;
        for (int i = start; i < end; i++) {
            int sum = 0;
            for (int j = -kernelRadius; j <= kernelRadius; j++) {
                int inputIndex = i + j;
                if (inputIndex >= 0 && inputIndex < input.length) {
                    sum += input[inputIndex] * kernel[j + kernelRadius];
                }
            }
            output[i] = sum;
        }
    }

    public long nBodyTestMulti() throws InterruptedException, ExecutionException {
        int numParticles = 200;
        double[] posX = new double[numParticles];
        double[] posY = new double[numParticles];
        double[] posZ = new double[numParticles];
        double[] velX = new double[numParticles];
        double[] velY = new double[numParticles];
        double[] velZ = new double[numParticles];
        double[] mass = new double[numParticles];
        Random random = new Random();

        for (int i = 0; i < numParticles; i++) {
            posX[i] = random.nextDouble() * 1000 - 500;
            posY[i] = random.nextDouble() * 1000 - 500;
            posZ[i] = random.nextDouble() * 1000 - 500;
            velX[i] = random.nextDouble() * 10 - 5;
            velY[i] = random.nextDouble() * 10 - 5;
            velZ[i] = random.nextDouble() * 10 - 5;
            mass[i] = random.nextDouble() * 50 + 1;
        }

        double timeStep = 0.01;
        int tasks = numCores;
        int chunkSize = numParticles / tasks;

        long startTime = System.nanoTime();
        for (int step = 0; step < 10; step++) {
            List<Callable<Void>> tasksList = new ArrayList<>();
            for (int t = 0; t < tasks; t++) {
                int start = t * chunkSize;
                int end = (t == tasks - 1) ? numParticles : (t + 1) * chunkSize;
                tasksList.add(() -> {
                    for (int i = start; i < end; i++) {
                        double forceX = 0, forceY = 0, forceZ = 0;
                        for (int j = 0; j < numParticles; j++) {
                            if (i != j) {
                                double dx = posX[j] - posX[i];
                                double dy = posY[j] - posY[i];
                                double dz = posZ[j] - posZ[i];
                                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz + 1e-9);
                                double force = mass[i] * mass[j] / (distance * distance);
                                forceX += force * dx;
                                forceY += force * dy;
                                forceZ += force * dz;
                            }
                        }
                        velX[i] += forceX * timeStep / mass[i];
                        velY[i] += forceY * timeStep / mass[i];
                        velZ[i] += forceZ * timeStep / mass[i];
                    }
                    return null;
                });
            }
            executorService.invokeAll(tasksList);

            // Update la pozitie
            for (int i = 0; i < numParticles; i++) {
                posX[i] += velX[i] * timeStep;
                posY[i] += velY[i] * timeStep;
                posZ[i] += velZ[i] * timeStep;
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long quickSortTestMulti() throws InterruptedException, ExecutionException {
        int size = 300000;
        int[] array = new int[size];
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size);
        }

        long startTime = System.nanoTime();

        parallelQuickSort(array, 0, size - 1, numCores);

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    private void parallelQuickSort(int[] array, int low, int high, int availableThreads) throws InterruptedException, ExecutionException {
        if (low >= high) return;

        int pivotIndex = partition(array, low, high);

        if (availableThreads > 1) {
            int threadsForLeft = availableThreads / 2;
            int threadsForRight = availableThreads - threadsForLeft;

            List<Callable<Void>> tasks = new ArrayList<>();
            tasks.add(() -> {
                parallelQuickSort(array, low, pivotIndex - 1, threadsForLeft);
                return null;
            });
            tasks.add(() -> {
                parallelQuickSort(array, pivotIndex + 1, high, threadsForRight);
                return null;
            });

            executorService.invokeAll(tasks);
        } else {
            quickSort(array, low, high);
        }
    }
    private void quickSort(int[] array, int low, int high) {
        if (low >= high) return;

        int pivotIndex = partition(array, low, high);

        quickSort(array, low, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, high);
    }
    private int partition(int[] array, int low, int high) {
        int pivot = array[low + (high - low) / 2];
        int i = low, j = high;
        while (i <= j) {
            while (array[i] < pivot) i++;
            while (array[j] > pivot) j--;
            if (i <= j) {
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
                i++;
                j--;
            }
        }
        return i - 1;
    }

    public long sha256HashTestMulti() throws InterruptedException {
        int numHashes = 1000;
        int tasks = numCores;
        int hashesPerTask = numHashes / tasks;

        List<Callable<Void>> tasksList = new ArrayList<>();
        for (int t = 0; t < tasks; t++) {
            tasksList.add(() -> {
                for (int i = 0; i < hashesPerTask; i++) {
                    String randomString = generateRandomString(256);
                    sha256(randomString);
                }
                return null;
            });
        }

        long startTime = System.nanoTime();
        executorService.invokeAll(tasksList);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1000000;
    }
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes());
            StringBuilder hexString = new StringBuilder();
            for(byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public long imageProcessingTestMulti(InputStream imageStream) throws InterruptedException {
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);
        if (originalBitmap == null) {
            System.out.println("Failed to decode image stream.");
            return -1;
        }

        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int totalPixels = width * height;

        int[] pixels = new int[totalPixels];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height); //bagam toti pixelii intr-un array

        int pixelsPerThread = totalPixels / numCores;
        List<Callable<Void>> tasksList = new ArrayList<>();

        for (int i = 0; i < numCores; i++) {
            int startPixel = i * pixelsPerThread;
            int endPixel = (i == numCores - 1) ? totalPixels : (i + 1) * pixelsPerThread;

            tasksList.add(() -> {
                for (int pixelIndex = startPixel; pixelIndex < endPixel; pixelIndex++) {
                    int pixel = pixels[pixelIndex];

                    int red = Math.min(255, ((pixel >> 16) & 0xff) + 20);
                    int green = Math.min(255, ((pixel >> 8) & 0xff) + 20);
                    int blue = Math.min(255, (pixel & 0xff) + 20);

                    pixels[pixelIndex] = (0xff << 24) | (red << 16) | (green << 8) | blue;
                }
                return null;
            });
        }

        long startTime = System.nanoTime();
        executorService.invokeAll(tasksList);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        long endTime = System.nanoTime();

        System.out.println("Image processing completed. Total time: " + (endTime - startTime) / 1000000 + " ms");
        return (endTime - startTime) / 1000000;
    }

    public long compressFileTestMulti(int resourceId, List<byte[]> compressedChunks) throws Exception {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        byte[] fileData = readAllBytes(inputStream);
        int chunkSize = fileData.length / numCores;

        List<Callable<byte[]>> tasksList = new ArrayList<>();

        for (int i = 0; i < numCores; i++) {
            int start = i * chunkSize;
            int end = (i == numCores - 1) ? fileData.length : (i + 1) * chunkSize;
            byte[] chunk = Arrays.copyOfRange(fileData, start, end);

            tasksList.add(() -> {
                ByteArrayOutputStream chunkOutputStream = new ByteArrayOutputStream();
                try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(chunkOutputStream)) {
                    gzipOutputStream.write(chunk);
                }
                return chunkOutputStream.toByteArray();
            });
        }

        long startTime = System.nanoTime();
        List<Future<byte[]>> results = executorService.invokeAll(tasksList);
        long endTime = System.nanoTime();

        for (Future<byte[]> result : results) {
            compressedChunks.add(result.get());
        }

        return (endTime - startTime) / 1000000;
    }

    public long decompressFileTestMulti(List<byte[]> compressedChunks) throws Exception {
        List<Callable<byte[]>> tasksList = new ArrayList<>();

        // Decompress each chunk independently
        for (byte[] compressedChunk : compressedChunks) {
            tasksList.add(() -> {
                ByteArrayInputStream chunkInputStream = new ByteArrayInputStream(compressedChunk);
                ByteArrayOutputStream chunkOutputStream = new ByteArrayOutputStream();
                try (GZIPInputStream gzipInputStream = new GZIPInputStream(chunkInputStream)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = gzipInputStream.read(buffer)) != -1) {
                        chunkOutputStream.write(buffer, 0, len);
                    }
                }
                return chunkOutputStream.toByteArray();
            });
        }

        long startTime = System.nanoTime();
        List<Future<byte[]>> results = executorService.invokeAll(tasksList);
        long endTime = System.nanoTime();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (Future<byte[]> result : results) {
            byteArrayOutputStream.write(result.get());
        }

        return (endTime - startTime) / 1000000;
    }
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; //8KB
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public long aesEncryptionTestMulti() throws Exception {
        int numEncryptions = 1000;
        byte[] inputData = generateRandomData(256); //random 256-byte data
        SecretKey secretKey = generateAESKey();
        int tasksPerCore = numEncryptions / numCores;

        List<Callable<Void>> tasksList = new ArrayList<>();

        for (int i = 0; i < numCores; i++) {
            tasksList.add(() -> {
                for (int j = 0; j < tasksPerCore; j++) {
                    try {
                        encryptData(inputData, secretKey);
                    } catch (Exception e) {
                        throw new RuntimeException("Encryption failed: " + e.getMessage());
                    }
                }
                return null;
            });
        }

        long startTime = System.nanoTime();

        executorService.invokeAll(tasksList);

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }
    public long aesDecryptionTestMulti() throws Exception {
        int numDecryptions = 1000;
        byte[] inputData = generateRandomData(256);
        SecretKey secretKey = generateAESKey();
        byte[] encryptedData;
        try {
            encryptedData = encryptData(inputData, secretKey);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt data for decryption test: " + e.getMessage());
        }

        int tasksPerCore = numDecryptions / numCores;

        List<Callable<Void>> tasksList = new ArrayList<>();

        for (int i = 0; i < numCores; i++) {
            tasksList.add(() -> {
                for (int j = 0; j < tasksPerCore; j++) {
                    try {
                        decryptData(encryptedData, secretKey);
                    } catch (Exception e) {
                        throw new RuntimeException("Decryption failed: " + e.getMessage());
                    }
                }
                return null;
            });
        }

        long startTime = System.nanoTime();

        executorService.invokeAll(tasksList);

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }
    private byte[] generateRandomData(int size) {
        byte[] data = new byte[size];
        Random random = new Random();
        random.nextBytes(data);
        return data;
    }
    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }
    private byte[] encryptData(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }
    private byte[] decryptData(byte[] encryptedData, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    public long matrixExponentiationTestMulti() throws InterruptedException {
        int size = 100;
        int[][] baseMatrix = new int[size][size];
        int exponent = 100;

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                baseMatrix[i][j] = random.nextInt(10);
            }
        }

        long startTime = System.nanoTime();

        int[][] result = matrixExponentiationMulti(baseMatrix, exponent);

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }
    private int[][] matrixExponentiationMulti(int[][] baseMatrix, int exponent) throws InterruptedException {
        int size = baseMatrix.length;
        int[][] result = identityMatrix(size);

        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = multiplyMatricesMulti(result, baseMatrix);
            }
            baseMatrix = multiplyMatricesMulti(baseMatrix, baseMatrix);
            exponent /= 2;
        }
        return result;
    }
    private int[][] multiplyMatricesMulti(int[][] matrixA, int[][] matrixB) throws InterruptedException {
        int size = matrixA.length;
        int[][] result = new int[size][size];

        List<Callable<Void>> tasks = new ArrayList<>();
        int rowsPerCore = size / numCores;

        for (int core = 0; core < numCores; core++) {
            int startRow = core * rowsPerCore;
            int endRow = (core == numCores - 1) ? size : (core + 1) * rowsPerCore;

            tasks.add(() -> {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = 0; j < size; j++) {
                        for (int k = 0; k < size; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
                return null;
            });
        }

        executorService.invokeAll(tasks);
        return result;
    }
    private int[][] identityMatrix(int size) {
        int[][] identity = new int[size][size];
        for (int i = 0; i < size; i++) {
            identity[i][i] = 1;
        }
        return identity;
    }


}
