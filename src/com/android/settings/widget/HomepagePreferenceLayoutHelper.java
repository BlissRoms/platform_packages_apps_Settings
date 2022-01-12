/*
 * Copyright (C) 2022 The Android Open Source Project
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

import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settings.R;
import com.android.settings.flags.Flags;

/** Helper for homepage preference to manage layout. */
public class HomepagePreferenceLayoutHelper {

    private View mIcon;
    private View mText;
    private boolean mIconVisible = true;
    private int mIconPaddingStart = -1;
    private int mTextPaddingStart = -1;

    /** The interface for managing preference layouts on homepage */
    public interface HomepagePreferenceLayout {
        /** Returns a {@link HomepagePreferenceLayoutHelper}  */
        HomepagePreferenceLayoutHelper getHelper();
    }

    public HomepagePreferenceLayoutHelper(Preference preference) {
       /** empty function */
    }

    /** Sets whether the icon should be visible */
    public void setIconVisible(boolean visible) {
    }

    /** Sets the icon padding start */
    public void setIconPaddingStart(int paddingStart) {
    }

    /** Sets the text padding start */
    public void setTextPaddingStart(int paddingStart) {
    }

    void onBindViewHolder(PreferenceViewHolder holder) {
    }
}
