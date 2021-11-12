/*
 * Copyright (C) 2021 The ArrowOS Project
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

package com.android.settings.display;

import android.content.Context;
import android.os.SystemProperties;
import android.os.UserManager;

import static android.os.UserHandle.myUserId;
import static android.os.UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS;

import static com.android.settingslib.RestrictedLockUtilsInternal.hasBaseUserRestriction;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;

public class CombinedSignalIconsPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin {

    private static final String KEY_SHOW_COMBINED_STATUS_BAR_SIGNAL_ICONS = "show_combined_status_bar_signal_icons";
    private static final String COMBINED_SIGNAL_ICONS = "flag_combined_status_bar_signal_icons";
    private static final String COMBINED_SIGNAL_ICONS_PROP =
            "persist.systemui." + COMBINED_SIGNAL_ICONS;

    private final UserManager mUserManager;
    private final boolean mIsSecondaryUser;

    public CombinedSignalIconsPreferenceController(Context context) {
        super(context);
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        mIsSecondaryUser = !mUserManager.isAdminUser();
    }

    @Override
    public String getPreferenceKey() {
        return KEY_SHOW_COMBINED_STATUS_BAR_SIGNAL_ICONS;
    }

    @Override
    public boolean isAvailable() {
        final boolean isPrefAllowedOnDevice = SystemProperties.getBoolean(COMBINED_SIGNAL_ICONS_PROP, false);
        final boolean isPrefAllowedForUser = !mIsSecondaryUser
                && !Utils.isWifiOnly(mContext)
                && !hasBaseUserRestriction(mContext, DISALLOW_CONFIG_MOBILE_NETWORKS, myUserId());
        return isPrefAllowedForUser && isPrefAllowedOnDevice;
    }
}