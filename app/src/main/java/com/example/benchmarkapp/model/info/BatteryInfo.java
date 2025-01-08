package com.example.benchmarkapp.model.info;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryInfo {

    private final Context context;

    public BatteryInfo(Context context) {
        this.context = context;
    }

    /**
     * Retrieves battery details for the current device.
     * @return A formatted string containing battery details.
     */
    public String getBatteryDetails() {
        StringBuilder batteryDetails = new StringBuilder();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        if (batteryStatus != null) {
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float) scale) * 100;

            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

            int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            String chargingSource = getChargingSource(chargePlug);

            batteryDetails.append("Battery Level: ").append((int) batteryPct).append("%\n");
            batteryDetails.append("Charging Status: ").append(isCharging ? "Charging" : "Not Charging").append("\n");
            batteryDetails.append("Charging Source: ").append(chargingSource).append("\n");
            batteryDetails.append("Battery Health: ").append(getBatteryHealth(batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1))).append("\n");
            batteryDetails.append("Voltage: ").append(batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)).append(" mV\n");
            batteryDetails.append("Temperature: ").append(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0).append(" °C\n");
        } else {
            batteryDetails.append("Battery details not available.");
        }

        return batteryDetails.toString();
    }

    private String getChargingSource(int chargePlug) {
        switch (chargePlug) {
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB";
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC Adapter";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "Wireless";
            default:
                return "Unknown";
        }
    }

    private String getBatteryHealth(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "Overheat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified Failure";
            default:
                return "Unknown";
        }
    }
}
