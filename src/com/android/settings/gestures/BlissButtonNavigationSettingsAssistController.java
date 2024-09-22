package com.android.settings.gestures;

import android.content.Context;
import android.provider.DeviceConfig;

import com.android.settings.gestures.ButtonNavigationSettingsAssistController;

public class BlissButtonNavigationSettingsAssistController
        extends ButtonNavigationSettingsAssistController {
    static final String LONG_PRESS_HOME_BUTTON_TO_SEARCH = "long_press_home_button_to_search";
    static final String NAMESPACE_LAUNCHER = "launcher";

    public BlissButtonNavigationSettingsAssistController(Context context, String str) {
        super(context, str);
    }

    @Override
    public int getAvailabilityStatus() {
        if (isFlagEnabled()) {
            return 3;
        }
        return super.getAvailabilityStatus();
    }

    public static boolean isFlagEnabled() {
        return DeviceConfig.getBoolean(NAMESPACE_LAUNCHER, LONG_PRESS_HOME_BUTTON_TO_SEARCH, true);
    }
}
