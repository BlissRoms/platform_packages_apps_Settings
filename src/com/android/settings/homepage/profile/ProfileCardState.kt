/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account

sealed interface ProfileCardState {
    data class WithAccount(val account: Account) : ProfileCardState

    data object AddAccount : ProfileCardState
}
