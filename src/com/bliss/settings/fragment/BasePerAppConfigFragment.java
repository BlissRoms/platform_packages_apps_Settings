/*
 * Copyright (C) 2023-2024 The Nameless-AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.bliss.settings.fragment;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.widget.EmptyTextSettings;

import com.android.settingslib.widget.TopIntroPreference;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class BasePerAppConfigFragment extends EmptyTextSettings {

    protected Context mContext;
    private PackageManager mPackageManager;

    /**
     * Comparator by label, if null or empty then packageName.
     */
    private static class AppComparator implements Comparator<AppData> {

        private final Collator mCollator = Collator.getInstance();

        @Override
        public final int compare(AppData a, AppData b) {
            String sa = a.label;
            if (TextUtils.isEmpty(sa)) sa = a.packageName;
            String sb = b.label;
            if (TextUtils.isEmpty(sb)) sb = b.packageName;
            return mCollator.compare(sa, sb);
        }
    }

    protected final class AppData {
        protected String label;
        protected String packageName;
        protected int uid;

        protected AppData(String label, String packageName, int uid) {
            this.label = label;
            this.packageName = packageName;
            this.uid = uid;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        mContext = getActivity();
        mPackageManager = mContext.getPackageManager();
    }

    @Override
    public void onResume() {
        super.onResume();

        final Context prefContext = getPrefContext();
        final PreferenceScreen screen = getPreferenceScreen();

        // Clear the prefs
        screen.removeAll();

        // Add TopIntroPreference if resource id is valid
        if (getTopInfoResId() > 0) {
            try {
                final String title = mContext.getResources().getString(getTopInfoResId());
                if (!TextUtils.isEmpty(title)) {
                    final TopIntroPreference topInfoPref = new TopIntroPreference(prefContext);
                    topInfoPref.setTitle(title);
                    screen.addPreference(topInfoPref);
                }
            } catch (Exception e) {}
        }

        // Rebuild the list of prefs
        final ArrayList<AppData> apps = collectApps();
        for (final AppData appData : apps) {
            screen.addPreference(createAppPreference(prefContext, appData));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText(R.string.per_app_config_empty_text);
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.PAGE_UNKNOWN;
    }

    /**
     * @return the sorted list of AppData of all applications for current user,
     * with extra system applications defined in R.array.config_perAppConfAllowedSystemApps.
     */
    private ArrayList<AppData> collectApps() {
        final ArrayList<AppData> apps = new ArrayList<>();
        final List<PackageInfo> installedPackages =
                mPackageManager.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            apps.add(new AppData(
                    pi.applicationInfo.loadLabel(mPackageManager).toString(),
                    pi.packageName, pi.applicationInfo.uid));
        }
        if (showSystemApp()) {
            final String[] systemApps = mContext.getResources().getStringArray(
                    R.array.config_perAppConfAllowedSystemApps);
            for (String app : systemApps) {
                try {
                    final PackageInfo pi = mPackageManager.getPackageInfo(app, 0);
                    apps.add(new AppData(
                            pi.applicationInfo.loadLabel(mPackageManager).toString(),
                            app, pi.applicationInfo.uid));
                } catch (NameNotFoundException e) {
                }
            }
        }
        Collections.sort(apps, new AppComparator());
        return apps;
    }

    protected Drawable getIcon(String packageName) {
        Drawable loadIcon = null;
        if (packageName != null) {
            try {
                loadIcon = mPackageManager.getApplicationIcon(packageName);
            } catch (NameNotFoundException e) {
            }
        }
        return loadIcon != null ? loadIcon : mPackageManager.getDefaultActivityIcon();
    }

    protected int getTopInfoResId() {
        return 0;
    }

    protected boolean showSystemApp() {
        return true;
    }

    protected abstract Preference createAppPreference(Context prefContext, AppData appData);
}
