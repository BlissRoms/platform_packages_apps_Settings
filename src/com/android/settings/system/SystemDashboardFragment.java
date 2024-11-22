/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.settings.system;

import android.app.Activity;
import android.app.settings.SettingsEnums;
import android.os.Bundle;
import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

import androidx.annotation.Nullable;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

@SearchIndexable
public class SystemDashboardFragment extends DashboardFragment {

    private static final String TAG = "SystemDashboardFrag";
    private List<AbstractPreferenceController> mPreferenceControllers = new ArrayList<>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final PreferenceScreen screen = getPreferenceScreen();
        // We do not want to display an advanced button if only one setting is hidden
        if (getVisiblePreferenceCount(screen) == screen.getInitialExpandedChildrenCount() + 1) {
            screen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
        }
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.SETTINGS_SYSTEM_CATEGORY;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.system_dashboard_fragment;
    }

    @Override
    public int getHelpResource() {
        return R.string.help_url_system_dashboard;
    }

    private int getVisiblePreferenceCount(PreferenceGroup group) {
        int visibleCount = 0;
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            final Preference preference = group.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                visibleCount += getVisiblePreferenceCount((PreferenceGroup) preference);
            } else if (preference.isVisible()) {
                visibleCount++;
            }
        }
        return visibleCount;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (Utils.isMonkeyRunning()) {
            mPreferenceControllers = new ArrayList<>();
            return null;
        }
        mPreferenceControllers = buildPreferenceControllers(context, getActivity(),
                getSettingsLifecycle(), this);
        return mPreferenceControllers;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context,
            @Nullable Activity activity, @Nullable Lifecycle lifecycle,
            @Nullable SystemDashboardFragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new GameDefaultFrameRatePreferenceController(context));
        return controllers;
    }

    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.system_dashboard_fragment);
}
