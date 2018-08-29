/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2018 The LineageOS Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.os.SystemProperties;
import android.support.annotation.VisibleForTesting;
import android.text.format.DateFormat;

import com.android.settings.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VendorSecurityPatchLevelDialogController {

    private static final String KEY_AOSP_VENDOR_SECURITY_PATCH =
            "ro.vendor.build.security_patch";

    private static final String KEY_LINEAGE_VENDOR_SECURITY_PATCH =
            "ro.lineage.build.vendor_security_patch";

    @VisibleForTesting
    static final int VENDOR_SECURITY_PATCH_LEVEL_ID = R.id.vendor_security_patch_level_value;

    private final FirmwareVersionDialogFragment mDialog;

    public VendorSecurityPatchLevelDialogController(FirmwareVersionDialogFragment dialog) {
        mDialog = dialog;
    }

    private String getVendorSecurityPatchLevel() {
        String patchLevel = SystemProperties.get(KEY_AOSP_VENDOR_SECURITY_PATCH);

        if (patchLevel.isEmpty()) {
            patchLevel = SystemProperties.get(KEY_LINEAGE_VENDOR_SECURITY_PATCH);
        }

        if (!patchLevel.isEmpty()) {
            try {
                SimpleDateFormat template = new SimpleDateFormat("yyyy-MM-dd");
                Date patchLevelDate = template.parse(patchLevel);
                String format = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMMMyyyy");
                patchLevel = DateFormat.format(format, patchLevelDate).toString();
            } catch (ParseException e) {
                // parsing failed, use raw string
            }
        }

        return patchLevel;
    }

    /**
     * Updates the build number to the dialog.
     */
    public void initialize() {
        mDialog.setText(VENDOR_SECURITY_PATCH_LEVEL_ID, getVendorSecurityPatchLevel());
    }
}
