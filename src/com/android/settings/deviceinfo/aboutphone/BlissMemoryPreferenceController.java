package com.android.settings.deviceinfo.aboutphone;

import android.app.ActivityManager;
import android.content.Context;
import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;

public class BlissMemoryPreferenceController extends BasePreferenceController {

    private static final String KEY_MEMORY_INFO = "memory_info";

    public BlissMemoryPreferenceController(Context context, String key) {
        super(context, key);
    }

    public BlissMemoryPreferenceController(Context context) {
        this(context, KEY_MEMORY_INFO);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        return getTotalRam() + " GB RAM";
    }

    private int getTotalRam() {
        ActivityManager am = mContext.getSystemService(ActivityManager.class);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        if (am != null) {
            am.getMemoryInfo(memInfo);
            double ramInGb = (double) memInfo.totalMem / (1024.0 * 1024.0 * 1024.0);
            
            // Round to standard integer sizes
            // Use ceil to ensure 7.2 GB becomes 8 GB
            int roundedRam = (int) Math.ceil(ramInGb);
            
            // For standard sizes that might fall slightly short (e.g. 5.8 GB -> 6GB) Math.ceil handles it perfectly.
            // Even an 8GB device reporting 7.1GB available will round up to 8.
            
            return roundedRam > 0 ? roundedRam : 1;
        }
        return 0;
    }
}
