/*
 * Copyright (C) 2023-2024 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.bliss.settings.fragment;

import android.content.Context;
import android.util.Pair;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import java.util.List;

import com.bliss.settings.widget.AppListPreference;

public abstract class PerAppListConfigFragment extends BasePerAppConfigFragment {

    @Override
    protected Preference createAppPreference(Context prefContext, Pair<String, String> appData) {
        final ListPreference pref = new AppListPreference(prefContext);
        pref.setIcon(getIcon(appData.second));
        pref.setTitle(appData.first);
        pref.setDialogTitle(appData.first);

        int size = getEntries().size();

        CharSequence[] entries = new CharSequence[size];
        CharSequence[] values = new CharSequence[size];
        for (int i = 0; i < size; ++i) {
            entries[i] = getEntries().get(i);
            values[i] = String.valueOf(getValues().get(i));
        }
        pref.setEntries(entries);
        pref.setEntryValues(values);

        final String value = String.valueOf(getCurrentValue(appData.second));
        CharSequence summary = "-1";
        for (int i = 0; i < size; ++i) {
            if (values[i].equals(value)) {
                summary = entries[i];
                break;
            }
        }
        pref.setValue(value);
        pref.setSummary(summary);

        pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String newValueStr = String.valueOf(newValue);
                onValueChanged(appData.second, Integer.parseInt(newValueStr));
                CharSequence[] entries = pref.getEntries();
                CharSequence[] values = pref.getEntryValues();
                CharSequence summary = entries[0];
                for (int i = 0; i < entries.length; ++i) {
                    if (values[i].equals(newValueStr)) {
                        summary = entries[i];
                        break;
                    }
                }
                pref.setSummary(summary);
                return true;
            }
        });

        return pref;
    }

    protected abstract List<String> getEntries();

    protected abstract List<Integer> getValues();

    protected abstract int getCurrentValue(String packageName);

    protected abstract void onValueChanged(String packageName, int value);
}
