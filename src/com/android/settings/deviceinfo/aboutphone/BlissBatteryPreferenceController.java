package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import androidx.preference.Preference;

import com.android.settings.core.BasePreferenceController;
import com.android.internal.os.PowerProfile;

public class BlissBatteryPreferenceController extends BasePreferenceController {

    private static final String KEY_BATTERY_INFO = "battery_info";

    public BlissBatteryPreferenceController(Context context, String key) {
        super(context, key);
    }

    public BlissBatteryPreferenceController(Context context) {
        this(context, KEY_BATTERY_INFO);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        return getBatteryCapacity() + " mAh";
    }

    private int getBatteryCapacity() {
        PowerProfile powerProfile = new PowerProfile(mContext);
        double capacity = powerProfile.getBatteryCapacity();
        
        // Round to nearest multiples of 500s or 100s for display
        // Most batteries are like 4500, 5000, 5100, etc.
        int roundedCapacity = (int) Math.round(capacity / 100.0) * 100;
        
        return roundedCapacity;
    }
}
