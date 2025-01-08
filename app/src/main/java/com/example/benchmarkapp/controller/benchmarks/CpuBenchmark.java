package com.example.benchmarkapp.controller.benchmarks;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import android.content.Context;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import timber.log.Timber;

public class CpuBenchmark {
    private Context context;
    private static final String LOG_FILE_NAME = "benchmark_results_cpu.txt";
    public CpuBenchmark(Context context) {
        this.context = context;
    }

    public long integerCalculationTest() {
        clearLogFile();
        long startTime = System.nanoTime();
        BigInteger result = BigInteger.ZERO;

        for (long i = 0; i < 100000; i++) {
            long operationStartTime = System.nanoTime();
            result = result.add(BigInteger.valueOf(i).multiply(BigInteger.valueOf(i)));
            long operationEndTime = System.nanoTime();

            if(i % 1000 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                logOperationTimeToFile("IntegerCalculationTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }


    public long floatingPointCalculationTest() {
        long startTime = System.nanoTime();
        double result = 0.0;

        for (int i = 0; i < 1000000; i++) {
            long operationStartTime = System.nanoTime();
            result += Math.sqrt(i) * Math.PI;
            long operationEndTime = System.nanoTime();

            if(i % 10000 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                logOperationTimeToFile("FloatingPointCalculationTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }


    public long loopIntensityTest() {
        long startTime = System.nanoTime();
        BigInteger result = BigInteger.ONE;

        for (int i = 0; i < 1000; i++) {
            long iterationStartTime = System.nanoTime();
            for (int j = 0; j < 100; j++) {
                for (int k = 0; k < 100; k++) {
                    result = result.add(BigInteger.valueOf((long) i * j * k));
                }
            }
            long iterationEndTime = System.nanoTime();

            if(i % 10 == 0) {
                double timeTaken = (iterationEndTime - iterationStartTime) / 1000000.0;
                logOperationTimeToFile("LoopIntensityTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }


    public long primeNumberCalculationTest() {
        long startTime = System.nanoTime();
        int count = 0;
        for (int i = 2; i < 100000; i++) {
            long operationStartTime = System.nanoTime();
            if (isPrime(i)) {
                count++;
            }
            long operationEndTime = System.nanoTime();

            if(i % 1000 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                logOperationTimeToFile("PrimeNumberCalculationTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
        //test sintetic, verifica daca un numar este prim; tot pt workload si speed
    }
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public long matrixMultiplicationTest() {
        int size = 300;
        int[][] matrixA = new int[size][size];
        int[][] matrixB = new int[size][size];
        int[][] result = new int[size][size];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                matrixA[i][j] = i + j;
                matrixB[i][j] = Math.abs(i - j);
            }
        }

        long startTime = System.nanoTime();

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                long operationStartTime = System.nanoTime();
                result[i][j] = 0;
                for(int k = 0; k < size; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];

                }
                long operationEndTime = System.nanoTime();

                if(i % 10 == 0 && j % 10 == 0) {
                    double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                    logOperationTimeToFile("MatrixMultiplicationTest", timeTaken);
                }
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
        //test sintetic care mimeaza real-life application;
        //matrixmultiplication se foloseste in ML, AI, etc
        //poate fi folosit si pt a testa memoria
    }

    private void quickSort(int[] array, int low, int high) {
        if (array == null || array.length == 0) return;
        if (low >= high) return;

        int middle = low + (high - low) / 2;
        int pivot = array[middle];

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

        if (low < j) quickSort(array, low, j);
        if (high > i) quickSort(array, i, high);
    }
    public long quickSortTest() {
        int size = 300000;
        int[] array = new int[size];

        for(int i = 0; i < size; i++) {
            array[i] = (int) (Math.random() * size);
        }

        long startTime = System.nanoTime();
        quickSort(array, 0, size - 1);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1000000;
        //test sintetic pt workload si speed;
        //valorile din array sunt random
    }

    public long fftTest() {
        int size = 2048; //FFT requires the Array length to be a power of 2
        double[] real = new double[size];
        double[] imag = new double[size];

        for(int i = 0; i < size; i++) {
            real[i] = Math.cos(2 * Math.PI * i / size);
            imag[i] = Math.sin(2 * Math.PI * i / size);
        }

        long startTime = System.nanoTime();
        fft(real, imag);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1000000;
    }
    private void fft(double[] real, double[] imag) {
        int n = real.length;

        int bits = (int) (Math.log(n) / Math.log(2));
        for(int i = 0; i < n; i++) {
            int reversed = reverseBits(i, bits);
            if(i < reversed) {
                double tempReal = real[i];
                double tempImag = imag[i];
                real[i] = real[reversed];
                imag[i] = imag[reversed];
                real[reversed] = tempReal;
                imag[reversed] = tempImag;
            }
        }

        for(int size = 2; size <= n; size *= 2) {
            double angle = -2 * Math.PI / size;
            double wReal = Math.cos(angle);
            double wImag = Math.sin(angle);

            for(int i = 0; i < n; i += size) {
                double uReal = 1;
                double uImag = 0;

                for(int j = 0; j < size / 2; j++) {
                    int evenIndex = i + j;
                    int oddIndex = i + j + size / 2;

                    double tempReal = uReal * real[oddIndex] - uImag * imag[oddIndex];
                    double tempImag = uReal * imag[oddIndex] + uImag * real[oddIndex];

                    real[oddIndex] = real[evenIndex] - tempReal;
                    imag[oddIndex] = imag[evenIndex] - tempImag;
                    real[evenIndex] += tempReal;
                    imag[evenIndex] += tempImag;

                    double uTempReal = uReal * wReal - uImag * wImag;
                    uImag = uReal * wImag + uImag * wReal;
                    uReal = uTempReal;
                }
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

    public long convolutionTest() {
        int[] input = new int[1000000];
        int[] kernel = {1, -1, 1};
        int[] output = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            input[i] = (int) (Math.random() * 100);
        }

        long startTime = System.nanoTime();
        computeConvolutionSingle(input, kernel, output);
        long endTime = System.nanoTime();

        return (endTime - startTime) / 1000000;
    }
    private void computeConvolutionSingle(int[] input, int[] kernel, int[] output) {
        int kernelRadius = kernel.length / 2;
        for (int i = 0; i < input.length; i++) {
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

    public long nBodyTest() {
        int numParticles = 200;
        double[] posX = new double[numParticles];
        double[] posY = new double[numParticles];
        double[] posZ = new double[numParticles];
        double[] velX = new double[numParticles];
        double[] velY = new double[numParticles];
        double[] velZ = new double[numParticles];
        double[] mass = new double[numParticles];

        for(int i = 0; i < numParticles; i++) {
            posX[i] = Math.random() * 1000 - 500;
            posY[i] = Math.random() * 1000 - 500;
            posZ[i] = Math.random() * 1000 - 500;
            velX[i] = Math.random() * 10 - 5;
            velY[i] = Math.random() * 10 - 5;
            velZ[i] = Math.random() * 10 - 5;
            mass[i] = Math.random() * 50 + 1;
        }

        double timeStep = 0.01;

        long startTime = System.nanoTime();

        for(int step = 0; step < 10; step++) {
            //long operationStartTime = System.nanoTime();
            for(int i = 0; i < numParticles; i++) {
                double forceX = 0;
                double forceY = 0;
                double forceZ = 0;

                for(int j = 0; j < numParticles; j++) {
                    if(i != j) {
                        double dx = posX[j] - posX[i];
                        double dy = posY[j] - posY[i];
                        double dz = posZ[j] - posZ[i];
                        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz + 1e-9); // 1e-9 pt div/0
                        double force = mass[i] * mass[j] / (distance * distance); // Gravitational force


                        forceX += force * dx;
                        forceY += force * dy;
                        forceZ += force * dz;
                    }
                }

                velX[i] += forceX * timeStep / mass[i];
                velY[i] += forceY * timeStep / mass[i];
                velZ[i] += forceZ * timeStep / mass[i];
            }

            for(int i = 0; i < numParticles; i++) {
                posX[i] += velX[i] * timeStep;
                posY[i] += velY[i] * timeStep;
                posZ[i] += velZ[i] * timeStep;
            }
            //long operationEndTime = System.nanoTime();

            //double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
            //logOperationTimeToFile("NBodyTest", timeTaken);
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long sha256HashTest() {
        int numHashes = 1000;
        String testString = generateRandomString(256);

        List<Double> methodTimes = new ArrayList<>();
        long startTime = System.nanoTime();

        for(int i = 0; i < numHashes; i++) {
            long operationStartTime = System.nanoTime();
            sha256(testString);
            long operationEndTime = System.nanoTime();

            if(i % 10 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                logOperationTimeToFile("SHA256HashTest", timeTaken);
            }
        }

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

    public long imageProcessingTest(InputStream imageStream) {
        Bitmap originalBitmap = BitmapFactory.decodeStream(imageStream);
        if (originalBitmap == null) {
            throw new IllegalArgumentException("Invalid image input stream.");
        }

        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

        long startTime = System.nanoTime();

        for (int y = 0; y < bitmap.getHeight(); y++) {

            for (int x = 0; x < bitmap.getWidth(); x++) {
                int pixel = bitmap.getPixel(x, y);

                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                red = Math.min(255, red + 20);
                green = Math.min(255, green + 20);
                blue = Math.min(255, blue + 20);

                bitmap.setPixel(x, y, (0xff << 24) | (red << 16) | (green << 8) | blue);
            }

        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;

        //test real-life care modifica luminozitatea imaginii
        //aplicat in Controller pe poza "cat" din folderul raw
    }

    public long compressFileTest(int resourceId) throws Exception {
        InputStream inputStream = context.getResources().openRawResource(resourceId);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        long startTime = System.nanoTime();

        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                gzipOutputStream.write(buffer, 0, len);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }
    public byte[] getCompressedDataForResource(int resourceId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resourceId);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    gzipOutputStream.write(buffer, 0, len);
                }
            }

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            Timber.e("Failed to compress resource %d: %s", resourceId, e.getMessage());
            return null;
        }
    }
    public long decompressFileTest(byte[] compressedData) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        long startTime = System.nanoTime();

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1000000;
    }

    public long aesEncryptionTest() throws Exception {
        int numEncryptions = 1000;
        byte[] inputData = generateRandomData(256);
        SecretKey secretKey = generateAESKey();

        long startTime = System.nanoTime();

        for (int i = 0; i < numEncryptions; i++) {
            long operationStartTime = System.nanoTime();
            encryptData(inputData, secretKey);
            long operationEndTime = System.nanoTime();

            if(i % 10 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                logOperationTimeToFile("AESEncryptionTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
    }
    public long aesDecryptionTest() throws Exception {
        int numDecryptions = 1000;
        byte[] inputData = generateRandomData(256);
        SecretKey secretKey = generateAESKey();
        byte[] encryptedData = encryptData(inputData, secretKey);

        long startTime = System.nanoTime();

        for (int i = 0; i < numDecryptions; i++) {
            long operationStartTime = System.nanoTime();
            decryptData(encryptedData, secretKey);
            long operationEndTime = System.nanoTime();

            if(i % 10 == 0) {
                double timeTaken = (operationEndTime - operationStartTime) / 1000000.0;
                logOperationTimeToFile("AESDecryptionTest", timeTaken);
            }
        }

        long endTime = System.nanoTime();
        return (endTime - startTime) / 1_000_000;
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

    public long matrixExponentiationTest() {
        int size = 100;
        int[][] baseMatrix = new int[size][size];
        int exponent = 4;

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                baseMatrix[i][j] = random.nextInt(10);
            }
        }

        long startTime = System.nanoTime();

        int[][] result = matrixExponentiation(baseMatrix, exponent);

        long endTime = System.nanoTime();

        return (endTime - startTime) / 1_000_000;
    }
    private int[][] matrixExponentiation(int[][] baseMatrix, int exponent) {
        int size = baseMatrix.length;
        int[][] result = identityMatrix(size);

        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = multiplyMatrices(result, baseMatrix);
            }
            baseMatrix = multiplyMatrices(baseMatrix, baseMatrix);
            exponent /= 2;
        }
        return result;
    }
    private int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        int size = matrixA.length;
        int[][] result = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        return result;
    }
    private int[][] identityMatrix(int size) {
        int[][] identity = new int[size][size];
        for (int i = 0; i < size; i++) {
            identity[i][i] = 1;
        }
        return identity;
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


