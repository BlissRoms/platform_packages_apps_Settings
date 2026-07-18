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

package com.android.settings.deviceinfo.bliss

import android.content.Context
import android.os.SystemProperties
import androidx.preference.Preference
import com.android.settings.R
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.metadata.PersistentPreference
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.metadata.SensitivityLevel
import com.android.settingslib.preference.PreferenceBinding

class BlissVersionDetailPreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "bliss_version"

    override val purpose: Int
        get() = R.string.bliss_version

    override val title: Int
        get() = R.string.bliss_version

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        SystemProperties.get("ro.bliss.version", context.getString(R.string.bliss_version_default))

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY
}

class BlissCodenamePreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "bliss_codename"

    override val purpose: Int
        get() = R.string.bliss_codename

    override val title: Int
        get() = R.string.bliss_codename

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        SystemProperties.get("ro.bliss.codename", context.getString(R.string.bliss_build_default))

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY
}

class BlissDevicePreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "bliss_device"

    override val purpose: Int
        get() = R.string.bliss_device

    override val title: Int
        get() = R.string.bliss_device

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        SystemProperties.get("ro.bliss.device", context.getString(R.string.bliss_build_default))

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY
}

class BlissMaintainerPreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "maintainer"

    override val purpose: Int
        get() = R.string.maintainer

    override val title: Int
        get() = R.string.maintainer

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        context.getString(R.string.bliss_maintainer)

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY
}

class BlissBuildTypePreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "bliss_build_type"

    override val purpose: Int
        get() = R.string.bliss_build_type

    override val title: Int
        get() = R.string.bliss_build_type

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        SystemProperties.get("ro.bliss.build.status", context.getString(R.string.bliss_build_default))

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY
}

class BlissBuildDatePreference :
    PersistentPreference<String>, PreferenceMetadata, PreferenceSummaryProvider, PreferenceBinding {

    override val key: String
        get() = "bliss_build_date"

    override val purpose: Int
        get() = R.string.bliss_build_date

    override val title: Int
        get() = R.string.bliss_build_date

    override val indexable
        get() = false

    override val supportsWrite = false

    override val valueType = String::class.javaObjectType

    override fun storage(context: Context): KeyValueStore = createSummaryStorage(context, key)

    override fun getSummary(context: Context): CharSequence? =
        SystemProperties.get("ro.build.date", context.getString(R.string.bliss_build_default))

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.isSelectable = false
        preference.isCopyingEnabled = true
    }

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY
}
