package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import androidx.preference.Preference;

import com.android.internal.os.PowerProfile;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.fuelgauge.BatteryUtils;

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
        if (capacity <= 0) {
            Intent batteryIntent = BatteryUtils.getBatteryIntent(mContext);
            int designCapacityUah = batteryIntent != null
                    ? batteryIntent.getIntExtra(BatteryManager.EXTRA_DESIGN_CAPACITY, -1)
                    : -1;
            if (designCapacityUah > 0) {
                capacity = designCapacityUah / 1000.0;
            }
        }
        return (int) Math.round(capacity);
    }
}
