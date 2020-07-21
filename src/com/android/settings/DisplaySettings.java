/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;

import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.BrightnessLevelPreferenceController;
import com.android.settings.display.CameraGesturePreferenceController;
import com.android.settings.display.ForceDarkPreferenceController;
import com.android.settings.display.LiftToWakePreferenceController;
import com.android.settings.display.NightDisplayPreferenceController;
import com.android.settings.display.NightModePreferenceController;
import com.android.settings.display.ScreenSaverPreferenceController;
import com.android.settings.display.ShowOperatorNamePreferenceController;
import com.android.settings.display.TapToWakePreferenceController;
import com.android.settings.display.ThemePreferenceController;
import com.android.settings.display.TimeoutLockscreenPreferenceController;
import com.android.settings.display.TimeoutPreferenceController;
import com.android.settings.display.VrDisplayPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import com.bliss.support.preferences.GlobalSettingListPreference;

import lineageos.hardware.LineageHardwareManager;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class DisplaySettings extends DashboardFragment {
    private static final String TAG = "DisplaySettings";

    public static final String KEY_PROXIMITY_ON_WAKE = "proximity_on_wake";

    private static final String KEY_LOCKSCREEN_TIMEOUT = "lockscreen_timeout";
    private static final String KEY_SCREEN_TIMEOUT = "screen_timeout";
    private static final String KEY_HIGH_TOUCH_SENSITIVITY = "high_touch_sensitivity_enable";
    private static final String KEY_REFRESH_RATE_SETTING = "refresh_rate_setting";
    private static final String KEY_DISPLAY_CUSTOMISATION_CATEGORY = "display_customisation_category";

    private GlobalSettingListPreference mVariableRefreshRate;
    private PreferenceCategory mDisplayCust;

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DISPLAY;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.display_settings;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final PreferenceScreen prefScreen = getPreferenceScreen();

        mVariableRefreshRate = (GlobalSettingListPreference) prefScreen.findPreference(KEY_REFRESH_RATE_SETTING);
        mDisplayCust = (PreferenceCategory) prefScreen.findPreference(KEY_DISPLAY_CUSTOMISATION_CATEGORY);
        boolean hasVariableRefreshRate =
            getContext().getResources().getBoolean(com.android.internal.R.bool.config_hasVariableRefreshRate);

        if (!hasVariableRefreshRate) {
            mDisplayCust.removePreference(mVariableRefreshRate);
        } else {
            mDisplayCust.addPreference(mVariableRefreshRate);
            int defVarRateSetting = getContext().getResources().getInteger(
                 com.android.internal.R.integer.config_defaultVariableRefreshRateSetting);
            int mVarRateSetting = Settings.Global.getInt(getContext().getContentResolver(),
                 Settings.Global.REFRESH_RATE_SETTING, defVarRateSetting);
            mVariableRefreshRate.setValue(String.valueOf(mVarRateSetting));
        }
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_display;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new CameraGesturePreferenceController(context));
        controllers.add(new ForceDarkPreferenceController(context));
        controllers.add(new LiftToWakePreferenceController(context));
        controllers.add(new NightDisplayPreferenceController(context));
        controllers.add(new NightModePreferenceController(context));
        controllers.add(new ScreenSaverPreferenceController(context));
        controllers.add(new TapToWakePreferenceController(context));
        controllers.add(new TimeoutLockscreenPreferenceController(context, KEY_LOCKSCREEN_TIMEOUT));
        controllers.add(new TimeoutPreferenceController(context, KEY_SCREEN_TIMEOUT));
        controllers.add(new VrDisplayPreferenceController(context));
        controllers.add(new ShowOperatorNamePreferenceController(context));
        controllers.add(new ThemePreferenceController(context));
        controllers.add(new BrightnessLevelPreferenceController(context, lifecycle));
        return controllers;
    }

    public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                        boolean enabled) {
                    final ArrayList<SearchIndexableResource> result = new ArrayList<>();

                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.display_settings;
                    result.add(sir);
                    return result;
                }

                @Override
                public List<String> getNonIndexableKeys(Context context) {
                    List<String> keys = super.getNonIndexableKeys(context);
                    LineageHardwareManager hardware = LineageHardwareManager.getInstance(context);
                    if (!context.getResources().getBoolean(
                            org.lineageos.platform.internal.R.bool.config_proximityCheckOnWake)) {
                        keys.add(KEY_PROXIMITY_ON_WAKE);
                    }
                    if (!hardware.isSupported(
                            LineageHardwareManager.FEATURE_HIGH_TOUCH_SENSITIVITY)) {
                        keys.add(KEY_HIGH_TOUCH_SENSITIVITY);
                    }
                    boolean hasVariableRefreshRate =
                        context.getResources().getBoolean(com.android.internal.R.bool.config_hasVariableRefreshRate);
                    if (!hasVariableRefreshRate) {
                        keys.add(KEY_REFRESH_RATE_SETTING);
                    }

                    return keys;
                }

                @Override
                public List<AbstractPreferenceController> createPreferenceControllers(
                        Context context) {
                    return buildPreferenceControllers(context, null);
                }
            };
}
