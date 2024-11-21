/*
 * Copyright (C) 2019 The LineageOS Project
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

package com.android.settings.deviceinfo.bliss;

import android.content.Context;
import android.os.SystemProperties;
import android.content.res.Resources;
import android.text.TextUtils;

import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class BlissMaintainerPreferenceController extends BasePreferenceController {

    private static final String TAG = "BlissMaintainerPreferenceController";
    private static final String KEY_BLISS_BUILD_STATUS_PROP = "ro.bliss.build.status";

    public BlissMaintainerPreferenceController(Context context, String key) {
        super(context, key);
    }

    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        String buildStatus = getBuildStatus();
        String maintainer = mContext.getResources().getString(R.string.bliss_maintainer);

        if (!TextUtils.isEmpty(buildStatus) && !buildStatus.equals(mContext.getString(R.string.unknown))) {
            return buildStatus + " by " + maintainer;        }

        return mContext.getString(R.string.unknown);
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);

        String buildStatus = getBuildStatus();

        if ("OFFICIAL".equalsIgnoreCase(buildStatus)) {
            preference.setIcon(R.drawable.maintainer_official);
        } else if ("UNOFFICIAL".equalsIgnoreCase(buildStatus)) {
            preference.setIcon(R.drawable.maintainer_unofficial);
        } else {
            preference.setIcon(R.drawable.maintainer_unofficial);
        }
    }

    private String getBuildStatus() {
        String buildStatus = SystemProperties.get(KEY_BLISS_BUILD_STATUS_PROP, null);

        if ("OFFICIAL".equalsIgnoreCase(buildStatus) || "UNOFFICIAL".equalsIgnoreCase(buildStatus)) {
            return buildStatus;
        }

        return mContext.getString(R.string.unknown);
    }
}
