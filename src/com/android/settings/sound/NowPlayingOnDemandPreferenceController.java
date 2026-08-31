/*
 * Copyright (C) 2026 The BlissRoms Project
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

package com.android.settings.sound;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

import java.util.List;

/**
 * Controller for the Now Playing On-Demand Lockscreen Search Button toggle.
 * Available only on Google Pixel devices with Now Playing support.
 */
public class NowPlayingOnDemandPreferenceController extends TogglePreferenceController {

    private static final String SETTING_KEY = "now_playing_on_demand";
    private static final int ON = 1;
    private static final int OFF = 0;

    private static final Intent NOW_PLAYING_SETTINGS_INTENT =
            new Intent("com.google.intelligence.sense.NOW_PLAYING_SETTINGS");

    private static final ComponentName[] NOW_PLAYING_COMPONENTS = new ComponentName[] {
            new ComponentName("com.google.android.apps.pixel.nowplaying",
                    "com.google.android.apps.pixel.nowplaying.mainscreen.NowPlayingMainActivity"),
            new ComponentName("com.google.android.as",
                    "com.google.intelligence.sense.ambientmusic.NowPlayingAmbientMusicSettingsActivity"),
            new ComponentName("com.google.android.as",
                    "com.google.intelligence.sense.ambientmusic.AmbientMusicSettingsActivity")
    };

    private final PackageManager mPackageManager;

    public NowPlayingOnDemandPreferenceController(Context context, String key) {
        super(context, key);
        mPackageManager = context.getPackageManager();
    }

    private boolean isPixelDevice() {
        return "google".equalsIgnoreCase(Build.BRAND) || "google".equalsIgnoreCase(Build.MANUFACTURER);
    }

    private boolean isNowPlayingSupported() {
        if (!isPixelDevice()) {
            return false;
        }
        List<ResolveInfo> resolved = mPackageManager.queryIntentActivities(
                NOW_PLAYING_SETTINGS_INTENT, 0);
        if (resolved != null && !resolved.isEmpty()) {
            return true;
        }
        for (ComponentName component : NOW_PLAYING_COMPONENTS) {
            Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(component);
            resolved = mPackageManager.queryIntentActivities(intent, 0);
            if (resolved != null && !resolved.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getAvailabilityStatus() {
        return isNowPlayingSupported() ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public boolean isChecked() {
        return Settings.System.getInt(mContext.getContentResolver(), SETTING_KEY, OFF) == ON;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.System.putInt(mContext.getContentResolver(), SETTING_KEY,
                isChecked ? ON : OFF);
    }

    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_sound;
    }
}
