package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class BlissCpuPreferenceController extends BasePreferenceController {

    private static final String TAG = "BlissCpuController";
    private static final String KEY_CPU_INFO = "cpu_info";
    private static final String CPUINFO_PATH = "/proc/cpuinfo";
    private static final String CPUINFO_MODEL_NAME = "model name";

    private static final Map<String, String> SOC_MANUFACTURER_NAMES = Map.of(
            "QTI", "Qualcomm",
            "Mediatek", "MediaTek");

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
        String name = mContext.getString(R.string.config_cpu_name);
        if (!TextUtils.isEmpty(name)) {
            return name;
        }

        name = getSocName();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }

        name = getCpuInfoModelName();
        if (!TextUtils.isEmpty(name)) {
            return name;
        }

        return mContext.getString(R.string.device_info_default);
    }

    private static String getSocName() {
        String model = Build.SOC_MODEL;
        if (isUnknown(model)) {
            return null;
        }
        String manufacturer = Build.SOC_MANUFACTURER;
        if (isUnknown(manufacturer)) {
            return model;
        }
        return SOC_MANUFACTURER_NAMES.getOrDefault(manufacturer, manufacturer) + " " + model;
    }

    private static String getCpuInfoModelName() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CPUINFO_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(CPUINFO_MODEL_NAME)) {
                    int separator = line.indexOf(':');
                    return separator < 0 ? null : line.substring(separator + 1).trim();
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not read " + CPUINFO_PATH, e);
        }
        return null;
    }

    private static boolean isUnknown(String value) {
        return TextUtils.isEmpty(value) || Build.UNKNOWN.equals(value);
    }
}
