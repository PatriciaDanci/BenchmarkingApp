package com.example.benchmarkapp.model.info;

import android.os.Build;

public class OSInfo {

    /**
     * Retrieves Operating System details for the current device.
     * @return A formatted string containing OS details.
     */
    public String getOSDetails() {
        StringBuilder osDetails = new StringBuilder();

        osDetails.append("OS Version: ").append(System.getProperty("os.version")).append("\n");
        osDetails.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        osDetails.append("Release: ").append(Build.VERSION.RELEASE).append("\n");
        osDetails.append("Codename: ").append(getCodename(Build.VERSION.SDK_INT)).append("\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            osDetails.append("Security Patch Level: ").append(Build.VERSION.SECURITY_PATCH).append("\n");
        }
        osDetails.append("Bootloader: ").append(Build.BOOTLOADER).append("\n");

        return osDetails.toString();
    }

    private String getCodename(int apiLevel) {
        switch (apiLevel) {
            case Build.VERSION_CODES.LOLLIPOP: // API Level 21
                return "Lollipop (Android 5.0)";
            case Build.VERSION_CODES.LOLLIPOP_MR1: // API Level 22
                return "Lollipop (Android 5.1)";
            case Build.VERSION_CODES.M: // API Level 23
                return "Marshmallow (Android 6)";
            case Build.VERSION_CODES.N: // API Level 24
                return "Nougat (Android 7.0)";
            case Build.VERSION_CODES.N_MR1: // API Level 25
                return "Nougat (Android 7.1)";
            case Build.VERSION_CODES.O: // API Level 26
                return "Oreo (Android 8.0)";
            case Build.VERSION_CODES.O_MR1: // API Level 27
                return "Oreo (Android 8.1)";
            case Build.VERSION_CODES.P: // API Level 28
                return "Pie (Android 9)";
            case Build.VERSION_CODES.Q: // API Level 29
                return "Android 10";
            case Build.VERSION_CODES.R: // API Level 30
                return "Android 11";
            case Build.VERSION_CODES.S: // API Level 31
                return "Snow Cone (Android 12)";
            case Build.VERSION_CODES.S_V2: // API Level 32
                return "Snow Cone v2 (Android 12L)";
            case Build.VERSION_CODES.TIRAMISU: // API Level 33
                return "Tiramisu (Android 13)";
            case 34: // API Level 34
                return "Upside Down Cake (Android 14)";
            case 35: // API Level 35
                return "Vanilla Ice Cream (Android 15)";
            default:
                return apiLevel > 35 ? "Future Android Version" : "Unknown Codename";
        }
    }

}
