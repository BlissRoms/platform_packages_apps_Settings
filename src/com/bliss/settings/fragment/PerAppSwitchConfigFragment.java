/*
 * Copyright (C) 2023-2024 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.bliss.settings.fragment;

import android.content.Context;
import android.util.Pair;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.SwitchPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

import com.bliss.settings.widget.AppSwitchPreference;

public abstract class PerAppSwitchConfigFragment extends BasePerAppConfigFragment {

    private final LinkedHashMap<String, Boolean> mPkgCheckState = new LinkedHashMap<>();

    @Override
    protected Preference createAppPreference(Context prefContext, Pair<String, String> appData) {
        final SwitchPreference pref = new AppSwitchPreference(prefContext);
        pref.setIcon(getIcon(appData.second));
        pref.setTitle(appData.first);
        pref.setSummary(appData.second);

        final boolean checked = isChecked(appData.second);
        pref.setChecked(checked);
        mPkgCheckState.put(appData.second, checked);

        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final boolean ret = onSetChecked(appData.second, (Boolean) newValue);
                if (ret) {
                    mPkgCheckState.put(appData.second, (Boolean) newValue);
                    onCheckedListUpdated(generateCheckedPkgList());
                }
                return ret;
            }
        });

        return pref;
    }

    private List<String> generateCheckedPkgList() {
        final List<String> ret = new ArrayList<>();
        for (String pkg : mPkgCheckState.keySet()) {
            if (mPkgCheckState.get(pkg)) {
                ret.add(pkg);
            }
        }
        return ret;
    }

    protected abstract boolean isChecked(String packageName);

    protected abstract boolean onSetChecked(String packageName, boolean checked);

    protected abstract void onCheckedListUpdated(List<String> pkgList);
}
