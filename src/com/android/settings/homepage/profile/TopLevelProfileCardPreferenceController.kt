/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account
import android.accounts.OnAccountsUpdateListener
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_USER
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settings.accounts.AccountDetailDashboardFragment
import com.android.settings.activityembedding.ActivityEmbeddingRulesController
import com.android.settings.core.BasePreferenceController
import com.android.settings.core.SubSettingLauncher
import com.android.settings.homepage.profile.ProfileCardState.AddAccount
import com.android.settings.homepage.profile.ProfileCardState.WithAccount

class TopLevelProfileCardPreferenceController
@VisibleForTesting
internal constructor(
    context: Context,
    key: String,
    private val repository: ProfileCardRepository,
    private val config: ProfileCardConfig,
) : BasePreferenceController(context, key), DefaultLifecycleObserver {

    constructor(
        context: Context,
        key: String,
    ) : this(context, key, DefaultProfileCardRepository(context), ProfileCardConfig.from(context))

    private var preference: Preference? = null

    private val accountsListener = OnAccountsUpdateListener { preference?.let(::updateState) }

    override fun getAvailabilityStatus(): Int =
        if (
            config.isEnabled &&
                config.accountType.isNotBlank() &&
                repository.isEligibleUser() &&
                repository.authenticatorInfo(config.accountType) != null
        ) {
            AVAILABLE
        } else {
            CONDITIONALLY_UNAVAILABLE
        }

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        preference = screen.findPreference(preferenceKey)
        screen.findPreference<PreferenceGroup>(CATEGORY_KEY)?.isVisible = isAvailable
    }

    override fun updateState(preference: Preference) {
        super.updateState(preference)
        preference.title = repository.userName()
        preference.icon = repository.userIcon()
        preference.summary =
            when (val state = currentState()) {
                is WithAccount -> state.account.name
                AddAccount -> mContext.getString(R.string.profile_card_add_account_summary)
            }
    }

    override fun onStart(owner: LifecycleOwner) {
        if (isAvailable) repository.addAccountsListener(config.accountType, accountsListener)
    }

    override fun onStop(owner: LifecycleOwner) {
        repository.removeAccountsListener(accountsListener)
    }

    override fun handlePreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key != preferenceKey) return false
        when (val state = currentState()) {
            is WithAccount -> openAccount(state.account)
            AddAccount -> addAccount()
        }
        return true
    }

    private fun currentState(): ProfileCardState =
        repository.firstAccount(config.accountType)?.let(::WithAccount) ?: AddAccount

    private fun openAccount(account: Account) {
        val accountIntent = accountIntent(account)
        if (accountIntent != null) {
            launchBesideHomepage(accountIntent)
        } else {
            openAccountDetail(account)
        }
    }

    private fun accountIntent(account: Account): Intent? {
        if (config.accountIntentUri.isBlank()) return null
        val intent =
            runCatching { Intent.parseUri(config.accountIntentUri, Intent.URI_INTENT_SCHEME) }
                .onFailure { Log.w(TAG, "Invalid config_account_intent_uri", it) }
                .getOrNull() ?: return null
        val component = repository.resolveSystemActivity(intent) ?: return null
        return intent.setComponent(component).putExtra(EXTRA_ACCOUNT_NAME, account.name)
    }

    private fun openAccountDetail(account: Account) {
        val authenticator = repository.authenticatorInfo(account.type) ?: return addAccount()
        val user = mContext.user
        val arguments =
            Bundle().apply {
                putParcelable(AccountDetailDashboardFragment.KEY_ACCOUNT, account)
                putParcelable(AccountDetailDashboardFragment.KEY_USER_HANDLE, user)
                putString(AccountDetailDashboardFragment.KEY_ACCOUNT_TYPE, account.type)
                putString(
                    AccountDetailDashboardFragment.KEY_ACCOUNT_LABEL,
                    authenticator.label.toString(),
                )
                putInt(
                    AccountDetailDashboardFragment.KEY_ACCOUNT_TITLE_RES,
                    authenticator.labelResId,
                )
                putParcelable(EXTRA_USER, user)
            }
        SubSettingLauncher(mContext)
            .setDestination(AccountDetailDashboardFragment::class.java.name)
            .setArguments(arguments)
            .setTitleRes(authenticator.packageName, authenticator.labelResId)
            .setSourceMetricsCategory(metricsCategory)
            .launch()
    }

    private fun addAccount() {
        launchBesideHomepage(
            Intent(Settings.ACTION_ADD_ACCOUNT)
                .putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf(config.accountType))
        )
    }

    private fun launchBesideHomepage(intent: Intent) {
        val component = intent.component ?: repository.resolveSystemActivity(intent)
        if (component != null) {
            ActivityEmbeddingRulesController.registerTwoPanePairRuleForSettingsHome(
                mContext,
                component,
                intent.action,
                false,
                true,
                false,
            )
        }
        mContext.startActivity(intent)
    }

    companion object {
        const val CATEGORY_KEY = "top_level_profile_category"
        private const val TAG = "ProfileCardController"
        private const val EXTRA_ACCOUNT_NAME = "extra.accountName"
    }
}
