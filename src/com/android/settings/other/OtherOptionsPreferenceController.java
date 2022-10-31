/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2022 The Calyx Institute
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

package com.android.settings.other;

import android.content.Context;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.core.AbstractPreferenceController;

/**
 * This controller is used handle changes for the primary switch in the developer options page.
 *
 * All Preference Controllers that are a part of the developer options page should inherit this
 * class.
 */
public abstract class OtherOptionsPreferenceController extends AbstractPreferenceController {

    protected Preference mPreference;

    public OtherOptionsPreferenceController(Context context) {
        super(context);
    }

    /**
     * Child classes should override this method to create custom logic for hiding preferences.
     *
     * @return true if the preference is to be displayed.
     */
    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = screen.findPreference(getPreferenceKey());
    }

}
