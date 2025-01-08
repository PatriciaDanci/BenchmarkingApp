package com.example.benchmarkapp.model.info;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DeviceInfo {

    private final Context context;

    public DeviceInfo(Context context) {
        this.context = context;
    }

    /**
     * Retrieves detailed information about the device.
     *
     * @return A formatted string with device details.
     */
    public String getDeviceDetails() {
        StringBuilder deviceDetails = new StringBuilder();

        deviceDetails.append("Device Name: ").append(Build.MODEL).append("\n");

        deviceDetails.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");

        deviceDetails.append("Product Name: ").append(Build.PRODUCT).append("\n");

        deviceDetails.append("Host: ").append(Build.HOST).append("\n");

        deviceDetails.append("Build ID: ").append(Build.ID).append("\n");

        deviceDetails.append("Serial Number: ").append(getSerialNumber()).append("\n");

        deviceDetails.append("Screen Resolution: ").append(getScreenResolution()).append("\n");

        deviceDetails.append("Device Type: ").append(getDeviceType()).append("\n");

        deviceDetails.append("Kernel Version: ").append(getKernelVersion()).append("\n");

        return deviceDetails.toString();
    }

    /**
     * Retrieves the device serial number.
     *
     * @return Serial number or "Unavailable" if not accessible.
     */
    private String getSerialNumber() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return Build.getSerial();
            } catch (SecurityException e) {
                return "Unavailable (Permission required)";
            }
        } else {
            return Build.SERIAL;
        }
    }

    /**
     * Retrieves the screen resolution.
     *
     * @return Screen resolution as "width x height".
     */
    private String getScreenResolution() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        return width + " x " + height;
    }

    /**
     * Determines the device type (Phone/Tablet).
     *
     * @return "Tablet" or "Phone".
     */
    private String getDeviceType() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthInches = metrics.widthPixels / metrics.xdpi;
        float heightInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));

        return diagonalInches >= 7.0 ? "Tablet" : "Phone";
    }

    /**
     * Retrieves the kernel version from /proc/version.
     *
     * @return Kernel version or "Unknown" if not accessible.
     */
    private String getKernelVersion() {
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/version"))) {
            return reader.readLine();
        } catch (IOException e) {
            return "Unknown";
        }
    }
}
