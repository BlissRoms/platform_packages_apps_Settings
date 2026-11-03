/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account
import android.accounts.OnAccountsUpdateListener
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable

data class AuthenticatorInfo(
    val label: CharSequence,
    val packageName: String,
    val labelResId: Int,
)

interface ProfileCardRepository {
    fun isEligibleUser(): Boolean

    fun userName(): String

    fun userIcon(): Drawable

    fun authenticatorInfo(accountType: String): AuthenticatorInfo?

    fun firstAccount(accountType: String): Account?

    fun resolveSystemActivity(intent: Intent): ComponentName?

    fun addAccountsListener(accountType: String, listener: OnAccountsUpdateListener)

    fun removeAccountsListener(listener: OnAccountsUpdateListener)
}
