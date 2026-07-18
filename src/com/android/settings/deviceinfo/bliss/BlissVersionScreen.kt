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

import android.app.settings.SettingsEnums
import android.content.Context
import android.os.SystemProperties
import androidx.fragment.app.Fragment
import com.android.settings.R
import com.android.settings.core.PreferenceScreenMixin
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.metadata.ProvidePreferenceScreen
import com.android.settingslib.metadata.preferenceHierarchy
import com.android.settingslib.metadata.preferencesapi.PreferencesApiScreen.Companion.APP_FUNCTION_UNCATEGORIZED
import kotlinx.coroutines.CoroutineScope

@ProvidePreferenceScreen(BlissVersionScreen.KEY)
open class BlissVersionScreen :
    PreferenceScreenMixin,
    PreferenceSummaryProvider {
    override fun tags(context: Context) = arrayOf(APP_FUNCTION_UNCATEGORIZED)

    override val key: String
        get() = KEY

    override val purpose: Int
        get() = R.string.bliss_version

    override val title: Int
        get() = R.string.bliss_version

    override val keywords: Int
        get() = R.string.keywords_bliss_version

    override val highlightMenuKey: Int
        get() = R.string.menu_key_about_device

    override fun getMetricsCategory() = SettingsEnums.DIALOG_FIRMWARE_VERSION

    override fun hasCompleteHierarchy() = true

    override fun fragmentClass(): Class<out Fragment>? = BlissVersionSettings::class.java

    override fun getSummary(context: Context): CharSequence? =
        SystemProperties.get("ro.bliss.version", context.getString(R.string.bliss_version_default))

    override fun getPreferenceHierarchy(context: Context, coroutineScope: CoroutineScope) =
        preferenceHierarchy(context) {
            +BlissVersionDetailPreference()
            +BlissCodenamePreference()
            +BlissDevicePreference()
            +BlissMaintainerPreference()
            +BlissBuildTypePreference()
            +BlissBuildDatePreference()
        }

    companion object {
        const val KEY = "bliss_version"
    }
}
