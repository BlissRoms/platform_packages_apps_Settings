package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BlissCpuPreferenceController extends BasePreferenceController {

    private static final String KEY_CPU_INFO = "cpu_info";

    private static final Map<String, String> CPU_MODELS = new HashMap<>();
    static {
        // Snapdragon 8 Series
        CPU_MODELS.put("SM8150", "Snapdragon 855 / 855+");
        CPU_MODELS.put("SM8250", "Snapdragon 865 / 865+");
        CPU_MODELS.put("SM8350", "Snapdragon 888 / 888+");
        CPU_MODELS.put("SM8450", "Snapdragon 8 Gen 1");
        CPU_MODELS.put("SM8475", "Snapdragon 8+ Gen 1");
        CPU_MODELS.put("SM8550", "Snapdragon 8 Gen 2");
        CPU_MODELS.put("SM8650", "Snapdragon 8 Gen 3");
        
        // Snapdragon 7 Series
        CPU_MODELS.put("SM7125", "Snapdragon 720G");
        CPU_MODELS.put("SM7150", "Snapdragon 730 / 730G");
        CPU_MODELS.put("SM7225", "Snapdragon 750G");
        CPU_MODELS.put("SM7250", "Snapdragon 765 / 765G");
        CPU_MODELS.put("SM7315", "Snapdragon 778G");
        CPU_MODELS.put("SM7325", "Snapdragon 778G / 778G+");
        CPU_MODELS.put("SM7450", "Snapdragon 7 Gen 1");
        CPU_MODELS.put("SM7475", "Snapdragon 7+ Gen 2");
        
        // Snapdragon 6 Series
        CPU_MODELS.put("SM6115", "Snapdragon 662");
        CPU_MODELS.put("SM6125", "Snapdragon 665");
        CPU_MODELS.put("SM6150", "Snapdragon 675");
        CPU_MODELS.put("SM6225", "Snapdragon 680");
        CPU_MODELS.put("SM6375", "Snapdragon 695");
    }

    public BlissCpuPreferenceController(Context context, String key) {
        super(context, key);
    }

    public BlissCpuPreferenceController(Context context) {
        this(context, KEY_CPU_INFO);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        return getCpuInfo();
    }

    private String getCpuInfo() {
        String rawCpu = Build.HARDWARE;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            String manufacturer = Build.SOC_MANUFACTURER;
            String model = Build.SOC_MODEL;
            if (!TextUtils.isEmpty(manufacturer) && !TextUtils.isEmpty(model) && !manufacturer.equals(Build.UNKNOWN)) {
                rawCpu = manufacturer + " " + model;
            }
        }
        
        if (Build.HARDWARE.equals(rawCpu)) {
            // Fallback to /proc/cpuinfo
            try {
                BufferedReader br = new BufferedReader(new FileReader("/proc/cpuinfo"));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("model name") || line.startsWith("Hardware")) {
                        rawCpu = line.split(":")[1].trim();
                        if (line.startsWith("model name")) {
                            // If we found 'model name', it's usually an x86/x64 CPU (Intel/AMD) with the full name,
                            // we can break immediately.
                            break;
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                Log.e("BlissCpuController", "Could not read /proc/cpuinfo", e);
            }
        }
        
        return formatCpuString(rawCpu);
    }

    private String formatCpuString(String raw) {
        if (raw == null) return "Unknown";
        
        // Check lookup map first
        String lookup = raw.replace("QTI", "").replace("Qualcomm", "").trim();
        if (CPU_MODELS.containsKey(lookup)) {
            return "Qualcomm " + CPU_MODELS.get(lookup);
        }
        
        // Fallback for unknown/unmapped processors
        String formatted = raw;
        formatted = formatted.replace("QTI", "Qualcomm");
        formatted = formatted.replace("SM", "Snapdragon ");
        formatted = formatted.replace("SD", "Snapdragon ");
        formatted = formatted.replace("MTK", "MediaTek");
        formatted = formatted.replace("  ", " "); // Remove double spaces
        return formatted.trim();
    }
}
