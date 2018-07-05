/*
 * Copyright (C) 2024 The Android Open Source Project
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

package com.android.settings.deviceinfo.firmwareversion

import android.content.Context
import android.util.Log
import androidx.preference.Preference
import com.android.settings.R
import com.android.settingslib.DeviceInfoUtils
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.metadata.PersistentPreference
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.metadata.SensitivityLevel
import com.android.settingslib.preference.PreferenceBinding
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

// LINT.IfChange
class KernelVersionPreference :
    PersistentPreference<String>,
    PreferenceMetadata,
    PreferenceSummaryProvider,
    PreferenceBinding,
    Preference.OnPreferenceClickListener {

    private var fullKernelVersion = false

    override val key: String
        get() = KEY

    override val purpose: Int
        get() = R.string.kernel_version_purpose

    override val title: Int
        get() = R.string.kernel_version

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        DeviceInfoUtils.getFormattedKernelVersion(context)

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isCopyingEnabled = true
        preference.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        if (fullKernelVersion) {
            preference.summary = DeviceInfoUtils.getFormattedKernelVersion(preference.context)
            fullKernelVersion = false
        } else {
            preference.summary = getFullKernelVersion()
            fullKernelVersion = true
        }
        return true
    }

    private fun getFullKernelVersion(): String {
        return try {
            BufferedReader(FileReader(FILENAME_PROC_VERSION), 256).use { it.readLine() }
                ?: "Unavailable"
        } catch (e: IOException) {
            Log.e(LOG_TAG, "IO Exception when getting kernel version for Device Info screen", e)
            "Unavailable"
        }
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY

    companion object {
        const val KEY = "kernel_version"
        private const val FILENAME_PROC_VERSION = "/proc/version"
        private const val LOG_TAG = "KernelVersionPreference"
    }

}
// LINT.ThenChange(KernelVersionPreferenceController.java)
