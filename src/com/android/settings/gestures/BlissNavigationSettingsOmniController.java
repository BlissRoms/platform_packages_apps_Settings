package com.android.settings.gestures;

import android.content.Context;
import android.provider.Settings;

import com.android.settings.core.TogglePreferenceController;

public class BlissNavigationSettingsOmniController extends TogglePreferenceController {
    @Override
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override
    public boolean isSliceable() {
        return false;
    }

    public BlissNavigationSettingsOmniController(Context context, String str) {
        super(context, str);
    }

    @Override
    public boolean isChecked() {
        return Settings.Secure.getInt(
                        this.mContext.getContentResolver(),
                        "search_all_entrypoints_enabled",
                        this.mContext
                                        .getResources()
                                        .getBoolean(
                                                com.android.internal.R.bool
                                                        .config_searchAllEntrypointsEnabledDefault)
                                ? 1
                                : 0)
                == 1;
    }

    @Override
    public boolean setChecked(boolean isSearchAllEntrypointsEnabled) {
        return Settings.Secure.putInt(
                this.mContext.getContentResolver(),
                "search_all_entrypoints_enabled",
                isSearchAllEntrypointsEnabled ? 1 : 0);
    }

    @Override
    public int getAvailabilityStatus() {
        return BlissButtonNavigationSettingsAssistController.isFlagEnabled()
                ? AVAILABLE
                : UNSUPPORTED_ON_DEVICE;
    }
}
