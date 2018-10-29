/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.gestures;

import static android.provider.Settings.Secure.TORCH_POWER_BUTTON_GESTURE;
import static android.provider.Settings.Secure.CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.util.omni.OmniUtils;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.widget.VideoPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;

public class DoubleTapPowerFlashlightPreferenceController extends BasePreferenceController
        implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener,
        LifecycleObserver, OnResume, OnPause, OnCreate, OnSaveInstanceState {

    /*private static final String PREF_KEY_VIDEO = "gesture_prevent_ringing_video";
    @VisibleForTesting
    static final String KEY_VIDEO_PAUSED = "key_video_paused";

    private VideoPreference mVideoPreference;
    @VisibleForTesting
    boolean mVideoPaused;*/

    private final String SECURE_KEY_CAMERA = CAMERA_DOUBLE_TAP_POWER_GESTURE_DISABLED; 
    private final String SECURE_KEY = TORCH_POWER_BUTTON_GESTURE;
    private final int OFF = 0;
    private final int DOUBLE_TAP_FLASHLIGHT = 1;
    private final int LONGPRESS_FLASHLIGHT = 2;

    public DoubleTapPowerFlashlightPreferenceController(Context context, String key) {
        super(context, key);
    }

    @Override
    public int getAvailabilityStatus() {
        return OmniUtils.deviceHasFlashlight(mContext)
                ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        /*if (isAvailable()) {
            mVideoPreference = (VideoPreference) screen.findPreference(getVideoPrefKey());
        }*/
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            if (preference instanceof ListPreference) {
                ListPreference pref = (ListPreference) preference;
                int value = Settings.Secure.getInt(
                        mContext.getContentResolver(), SECURE_KEY, OFF);
                switch (value) {
                    case DOUBLE_TAP_FLASHLIGHT:
                        pref.setValue(String.valueOf(value));
                        break;
                    case LONGPRESS_FLASHLIGHT:
                        pref.setValue(String.valueOf(value));
                        break;
                    default:
                        pref.setValue(String.valueOf(OFF));
                }
            }
        }
    }

    @Override
    public CharSequence getSummary() {
        int value = Settings.Secure.getInt(
                mContext.getContentResolver(), SECURE_KEY, OFF);
        int summary;
        switch (value) {
            case DOUBLE_TAP_FLASHLIGHT:
                summary = R.string.torch_power_button_gesture_dt;
                break;
            case LONGPRESS_FLASHLIGHT:
                summary = R.string.torch_power_button_gesture_lp;
                break;
            default:
                summary = R.string.torch_power_button_gesture_none;
        }
        return mContext.getString(summary);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //mVideoPaused = savedInstanceState.getBoolean(KEY_VIDEO_PAUSED, false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putBoolean(KEY_VIDEO_PAUSED, mVideoPaused);
    }

    @Override
    public void onPause() {
        /*if (mVideoPreference != null) {
            mVideoPaused = mVideoPreference.isVideoPaused();
            mVideoPreference.onViewInvisible();
        }*/
    }

    @Override
    public void onResume() {
        /*if (mVideoPreference != null) {
            mVideoPreference.onViewVisible(mVideoPaused);
        }*/
    }

    /*protected String getVideoPrefKey() {
        return PREF_KEY_VIDEO;
    }*/

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int mTorchPowerButtonValue = Integer.parseInt((String) newValue);
        Settings.Secure.putInt(mContext.getContentResolver(),
                SECURE_KEY, mTorchPowerButtonValue);
        preference.setSummary(getSummary());
        if (mTorchPowerButtonValue == 1) {
            // if doubletap for torch is enabled, switch off double tap for camera
            Settings.Secure.putInt(mContext.getContentResolver(),
                    SECURE_KEY_CAMERA, 1/*camera gesture is disabled when 1*/);
        }

        return true;
    }
}
