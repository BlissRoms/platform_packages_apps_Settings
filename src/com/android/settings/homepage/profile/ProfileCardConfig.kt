/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.content.Context
import com.android.settings.R

data class ProfileCardConfig(
    val isEnabled: Boolean,
    val accountType: String,
    val accountIntentUri: String,
) {
    companion object {
        fun from(context: Context): ProfileCardConfig =
            context.resources.run {
                ProfileCardConfig(
                    isEnabled = getBoolean(R.bool.config_show_profile_card_in_homepage),
                    accountType = getString(R.string.config_profile_card_account_type),
                    accountIntentUri = getString(R.string.config_account_intent_uri),
                )
            }
    }
}
