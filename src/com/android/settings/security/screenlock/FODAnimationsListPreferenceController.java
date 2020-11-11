/*
 * Copyright (C) 2020 ArrowOS
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

package com.android.settings.security.screenlock;

import android.content.Context;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;

public class FODAnimationsListPreferenceController extends AbstractPreferenceController {

    private static final String FOD_ANIMATIONS_LIST = "fod_recognizing_animation_list";

    public FODAnimationsListPreferenceController(Context context) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return mContext.getResources().getBoolean(R.bool.config_showFODAnimationSettings);
    }

    @Override
    public String getPreferenceKey() {
        return FOD_ANIMATIONS_LIST;
    }
}
