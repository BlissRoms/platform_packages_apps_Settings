/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account
import android.accounts.OnAccountsUpdateListener
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable

class FakeProfileCardRepository : ProfileCardRepository {
    var isEligible = true
    var name = "Jane"
    var authenticator: AuthenticatorInfo? = AuthenticatorInfo("Google", "com.example.auth", 1)
    var account: Account? = null
    var systemActivity: ComponentName? = null
    val listeners = mutableListOf<OnAccountsUpdateListener>()

    override fun isEligibleUser() = isEligible

    override fun userName() = name

    override fun userIcon(): Drawable = ColorDrawable()

    override fun authenticatorInfo(accountType: String) = authenticator

    override fun firstAccount(accountType: String) = account

    override fun resolveSystemActivity(intent: Intent) = systemActivity

    override fun addAccountsListener(accountType: String, listener: OnAccountsUpdateListener) {
        listeners += listener
    }

    override fun removeAccountsListener(listener: OnAccountsUpdateListener) {
        listeners -= listener
    }
}
