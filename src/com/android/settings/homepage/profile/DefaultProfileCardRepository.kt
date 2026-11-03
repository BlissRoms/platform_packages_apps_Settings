/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.OnAccountsUpdateListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.UserManager
import com.android.settings.R
import com.android.settingslib.Utils

class DefaultProfileCardRepository(
    private val context: Context,
    private val userManager: UserManager =
        checkNotNull(context.getSystemService(UserManager::class.java)),
    private val accountManager: AccountManager = AccountManager.get(context),
) : ProfileCardRepository {

    override fun isEligibleUser(): Boolean =
        !userManager.isGuestUser &&
            !userManager.isDemoUser &&
            !userManager.isRestrictedProfile &&
            !userManager.hasUserRestriction(UserManager.DISALLOW_MODIFY_ACCOUNTS)

    override fun userName(): String =
        userManager.userName.orEmpty().ifBlank { context.getString(R.string.user_owner) }

    override fun userIcon(): Drawable =
        Utils.getUserIcon(context, userManager, userManager.getUserInfo(context.userId))

    override fun authenticatorInfo(accountType: String): AuthenticatorInfo? {
        val description =
            accountManager.authenticatorTypes.firstOrNull { it.type == accountType }
                ?: return null
        if (description.labelId == 0) return null
        val label =
            context.packageManager.getText(description.packageName, description.labelId, null)
                ?: return null
        return AuthenticatorInfo(label, description.packageName, description.labelId)
    }

    override fun firstAccount(accountType: String): Account? =
        accountManager.getAccountsByType(accountType).firstOrNull()

    override fun resolveSystemActivity(intent: Intent): ComponentName? =
        context.packageManager
            .queryIntentActivities(intent, PackageManager.MATCH_SYSTEM_ONLY)
            .firstOrNull()
            ?.activityInfo
            ?.componentName

    override fun addAccountsListener(accountType: String, listener: OnAccountsUpdateListener) {
        accountManager.addOnAccountsUpdatedListener(
            listener,
            null,
            false,
            arrayOf(accountType),
        )
    }

    override fun removeAccountsListener(listener: OnAccountsUpdateListener) {
        accountManager.removeOnAccountsUpdatedListener(listener)
    }
}
