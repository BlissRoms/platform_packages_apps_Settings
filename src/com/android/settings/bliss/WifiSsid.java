/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.bliss;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;
import com.android.settings.Utils;
import com.android.settings.bliss.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class WifiSsid extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "WifiSsid";

    private static final String KEY_STATUS_BAR_SSID = "wifi_status_bar_ssid";
    private static final String PREF_STATUS_BAR_SSID_COLOR = "wifi_status_bar_ssid_color";
    private static final String PREF_STATUS_BAR_SSID_SIZE = "wifi_status_bar_ssid_size";
    private static final String PREF_STATUS_BAR_SSID_FONT_STYLE = "wifi_status_bar_ssid_font_style";

    private SwitchPreference mStatusBarSsid;
    private ColorPickerPreference mStatusBarSsidColor;
    private SeekBarPreference mStatusBarSsidSize;
    private ListPreference mStatusBarSsidFontStyle;

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.WIFI_ADVANCED;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_ssid);
        
        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarSsid = (SwitchPreference) findPreference(KEY_STATUS_BAR_SSID);
        mStatusBarSsid.setChecked(Settings.System.getInt(resolver,
            Settings.System.WIFI_STATUS_BAR_SSID, 0) == 1);
        mStatusBarSsid.setOnPreferenceChangeListener(this);

        mStatusBarSsidColor =
            (ColorPickerPreference) findPreference(PREF_STATUS_BAR_SSID_COLOR);
        int intColor = Settings.System.getInt(resolver,
                Settings.System.WIFI_STATUS_BAR_SSID_COLOR, 0xffffffff);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusBarSsidColor.setSummary(hexColor);
        mStatusBarSsidColor.setNewPreviewColor(intColor);
        mStatusBarSsidColor.setOnPreferenceChangeListener(this);

        mStatusBarSsidSize = (SeekBarPreference) findPreference(PREF_STATUS_BAR_SSID_SIZE);
        mStatusBarSsidSize.setValue(Settings.System.getInt(resolver,
                Settings.System.WIFI_STATUS_BAR_SSID_SIZE, 14));
        mStatusBarSsidSize.setOnPreferenceChangeListener(this);

        mStatusBarSsidFontStyle = (ListPreference) findPreference(PREF_STATUS_BAR_SSID_FONT_STYLE);
        mStatusBarSsidFontStyle.setValue(Integer.toString(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.WIFI_STATUS_BAR_SSID_FONT_STYLE, 0)));
        mStatusBarSsidFontStyle.setSummary(mStatusBarSsidFontStyle.getEntry());
        mStatusBarSsidFontStyle.setOnPreferenceChangeListener(this);

        updateSsidOptions();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        boolean value;
        
        if (preference == mStatusBarSsid) {
			value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.WIFI_STATUS_BAR_SSID,
                    value ? 1 : 0);
            updateSsidOptions();
            return true;
        } else if (preference == mStatusBarSsidColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.WIFI_STATUS_BAR_SSID_COLOR, intHex);
            return true;
        } else if (preference == mStatusBarSsidSize) {
            int width = ((Integer)newValue).intValue();
            Settings.System.putInt(resolver,
                    Settings.System.WIFI_STATUS_BAR_SSID_SIZE, width);
            return true;
        } else if (preference == mStatusBarSsidFontStyle) {
            int val = Integer.parseInt((String) newValue);
            int index = mStatusBarSsidFontStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.WIFI_STATUS_BAR_SSID_FONT_STYLE, val);
            mStatusBarSsidFontStyle.setSummary(mStatusBarSsidFontStyle.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void updateSsidOptions() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.WIFI_STATUS_BAR_SSID, 0) == 0) {
            mStatusBarSsidColor.setEnabled(false);
            mStatusBarSsidSize.setEnabled(false);
            mStatusBarSsidFontStyle.setEnabled(false);
        } else {
            mStatusBarSsidColor.setEnabled(true);
            mStatusBarSsidSize.setEnabled(true);
            mStatusBarSsidFontStyle.setEnabled(true);
        }
    }
}
