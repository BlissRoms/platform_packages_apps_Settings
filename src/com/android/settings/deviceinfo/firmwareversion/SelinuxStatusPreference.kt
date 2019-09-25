/*
 * Copyright (C) 2024-2026 The BlissRoms Project
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
import android.os.SELinux
import androidx.preference.Preference
import com.android.settings.R
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.metadata.PersistentPreference
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.metadata.SensitivityLevel
import com.android.settingslib.preference.PreferenceBinding

class SelinuxStatusPreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = KEY

    override val purpose: Int
        get() = R.string.selinux_status

    override val title: Int
        get() = R.string.selinux_status

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? {
        return if (!SELinux.isSELinuxEnabled()) {
            context.getString(R.string.selinux_status_disabled)
        } else if (!SELinux.isSELinuxEnforced()) {
            context.getString(R.string.selinux_status_permissive)
        } else {
            context.getString(R.string.selinux_status_enforcing)
        }
    }

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY

    companion object {
        const val KEY = "selinux_status"
    }
}
