package com.android.settings.deviceinfo.aboutphone;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settings.R;

public class BlissGridPreferenceController extends BasePreferenceController {

    public BlissGridPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        LayoutPreference preference = screen.findPreference(getPreferenceKey());
        if (preference != null) {
            setupGrid(preference);
        }
    }

    private void setupGrid(LayoutPreference preference) {
        TextView androidVersion = preference.findViewById(R.id.text_android_version);
        TextView selinuxStatus = preference.findViewById(R.id.text_selinux);
        TextView securityPatch = preference.findViewById(R.id.text_security_patch);
        TextView buildStatus = preference.findViewById(R.id.text_build_status);

        if (androidVersion != null) {
            androidVersion.setText(Build.VERSION.RELEASE);
            androidVersion.getRootView().findViewById(R.id.box_android_version).setOnClickListener(v -> {
                new com.android.settings.core.SubSettingLauncher(mContext)
                        .setDestination("com.android.settings.deviceinfo.firmwareversion.FirmwareVersionSettings")
                        .setSourceMetricsCategory(com.android.internal.logging.nano.MetricsProto.MetricsEvent.DEVICEINFO)
                        .launch();
            });
        }

        if (selinuxStatus != null) {
            boolean isEnforcing = SystemProperties.get("ro.boot.selinux", "1").equals("1");
            selinuxStatus.setText(isEnforcing ? "Enforcing" : "Permissive");
        }

        if (securityPatch != null) {
            String patch = DeviceInfoUtils.getSecurityPatch();
            securityPatch.setText(patch != null ? patch : mContext.getString(R.string.unknown));
        }

        if (buildStatus != null) {
            String buildType = SystemProperties.get("ro.bliss.build.status", "Unofficial");
            buildStatus.setText(buildType);
        }
    }
}
