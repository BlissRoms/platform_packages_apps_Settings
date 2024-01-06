/*
 * Copyright (C) 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.bliss.settings.display.refreshrate

import android.app.settings.SettingsEnums
import android.content.Context
import androidx.fragment.app.Fragment
import com.android.settings.R
import com.android.settings.Settings.ScreenRefreshRateActivity
import com.android.settings.core.PreferenceScreenMixin
import com.android.settings.utils.makeLaunchIntent
import com.android.settingslib.metadata.PreferenceAvailabilityProvider
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.ProvidePreferenceScreen
import com.android.settingslib.metadata.preferenceHierarchy
import com.android.settingslib.metadata.preferencesapi.PreferencesApiScreen.Companion.APP_FUNCTION_UNCATEGORIZED
import com.android.settingslib.metadata.preferencesapi.preconditions.PreconditionStability
import kotlinx.coroutines.CoroutineScope

@ProvidePreferenceScreen(ScreenRefreshRateScreen.KEY)
open class ScreenRefreshRateScreen :
    PreferenceScreenMixin, PreferenceAvailabilityProvider {
    override fun tags(context: Context) = arrayOf(APP_FUNCTION_UNCATEGORIZED)

    override val key: String
        get() = KEY

    override val purpose: Int
        get() = R.string.screen_refresh_rate_title

    override val title: Int
        get() = R.string.screen_refresh_rate_title

    override val highlightMenuKey: Int
        get() = R.string.menu_key_display

    override fun getMetricsCategory() = SettingsEnums.PAGE_UNKNOWN

    override fun hasCompleteHierarchy() = false

    override fun fragmentClass(): Class<out Fragment>? = ScreenRefreshRateFragment::class.java

    override fun getLaunchIntent(context: Context, metadata: PreferenceMetadata?) =
        makeLaunchIntent(context, ScreenRefreshRateActivity::class.java, metadata?.key)

    override fun getPreferenceHierarchy(context: Context, coroutineScope: CoroutineScope) =
        preferenceHierarchy(context) {}

    override val availabilityDescription: String
        get() = "The device must support screen refresh rate settings."

    override fun getAvailabilityStability() = PreconditionStability.STABLE_UNTIL_APK_UPDATE

    override fun isAvailable(context: Context): Boolean = true

    companion object {
        const val KEY = "screen_refresh_rate"
    }
}
