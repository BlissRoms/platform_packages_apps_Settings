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

package com.android.settings.widget;

import android.content.Context;

import androidx.preference.PreferenceScreen;

import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

public class LottiePreferenceController extends BasePreferenceController implements
        LifecycleObserver, OnStart, OnStop {

    private LottiePreference mLottiePreference;

    public LottiePreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return mLottiePreference != null && mLottiePreference.isAnimationAvailable() ?
                AVAILABLE_UNSEARCHABLE : UNSUPPORTED_ON_DEVICE;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        mLottiePreference = screen.findPreference(getPreferenceKey());
        super.displayPreference(screen);
    }

    @Override
    public void onStart() {
        if (mLottiePreference != null) {
            mLottiePreference.onViewVisible();
        }
    }

    @Override
    public void onStop() {
        if (mLottiePreference != null) {
            mLottiePreference.onViewInvisible();
        }
    }
}
