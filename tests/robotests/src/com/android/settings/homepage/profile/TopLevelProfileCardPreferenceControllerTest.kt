/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.lifecycle.LifecycleOwner
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.settings.R
import com.android.settings.SettingsActivity
import com.android.settings.accounts.AccountDetailDashboardFragment
import com.android.settings.core.BasePreferenceController.AVAILABLE
import com.android.settings.core.BasePreferenceController.CONDITIONALLY_UNAVAILABLE
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class TopLevelProfileCardPreferenceControllerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val repository = FakeProfileCardRepository()
    private val owner: LifecycleOwner = mock()

    private lateinit var screen: PreferenceScreen
    private lateinit var category: PreferenceCategory
    private lateinit var accountCategory: PreferenceCategory
    private lateinit var preference: Preference

    @Before
    fun setUp() {
        val manager = PreferenceManager(context)
        screen = manager.createPreferenceScreen(context)
        accountCategory = PreferenceCategory(context).apply { key = ACCOUNT_CATEGORY_KEY }
        category = PreferenceCategory(context).apply {
            key = TopLevelProfileCardPreferenceController.CATEGORY_KEY
        }
        preference = Preference(context).apply { key = KEY }
        screen.addPreference(accountCategory)
        screen.addPreference(category)
        category.addPreference(preference)
    }

    @Test
    fun availability_allConditionsMet_available() {
        assertThat(controller().availabilityStatus).isEqualTo(AVAILABLE)
    }

    @Test
    fun availability_configOff_unavailable() {
        val controller = controller(config().copy(isEnabled = false))

        assertThat(controller.availabilityStatus).isEqualTo(CONDITIONALLY_UNAVAILABLE)
    }

    @Test
    fun availability_ineligibleUser_unavailable() {
        repository.isEligible = false

        assertThat(controller().availabilityStatus).isEqualTo(CONDITIONALLY_UNAVAILABLE)
    }

    @Test
    fun availability_noAuthenticator_unavailable() {
        repository.authenticator = null

        assertThat(controller().availabilityStatus).isEqualTo(CONDITIONALLY_UNAVAILABLE)
    }

    @Test
    fun availability_emptyAccountType_unavailable() {
        val controller = controller(config().copy(accountType = ""))

        assertThat(controller.availabilityStatus).isEqualTo(CONDITIONALLY_UNAVAILABLE)
    }

    @Test
    fun displayPreference_unavailable_hidesCategory() {
        repository.authenticator = null

        controller().displayPreference(screen)

        assertThat(category.isVisible).isFalse()
    }

    @Test
    fun displayPreference_available_keepsCategoryVisible() {
        controller().displayPreference(screen)

        assertThat(category.isVisible).isTrue()
    }

    @Test
    fun displayPreference_neverTouchesAccountCategory() {
        repository.authenticator = null

        controller().displayPreference(screen)

        assertThat(accountCategory.isVisible).isTrue()
    }

    @Test
    fun updateState_withAccount_bindsNameIconAndEmail() {
        repository.account = ACCOUNT

        controller().updateState(preference)

        assertThat(preference.title.toString()).isEqualTo("Jane")
        assertThat(preference.summary.toString()).isEqualTo("a@gmail.com")
        assertThat(preference.icon).isNotNull()
    }

    @Test
    fun updateState_noAccount_showsAddAccountSummary() {
        controller().updateState(preference)

        assertThat(preference.summary.toString())
            .isEqualTo(context.getString(R.string.profile_card_add_account_summary))
    }

    @Test
    fun lifecycle_startStop_registersAndUnregistersListener() {
        val controller = controller()
        controller.displayPreference(screen)

        controller.onStart(owner)
        assertThat(repository.listeners).hasSize(1)

        controller.onStop(owner)
        assertThat(repository.listeners).isEmpty()
    }

    @Test
    fun accountRemoved_listenerRebinds_andTapAddsAccount() {
        repository.account = ACCOUNT
        val controller = controller()
        controller.displayPreference(screen)
        controller.updateState(preference)
        controller.onStart(owner)

        repository.account = null
        repository.listeners.single().onAccountsUpdated(emptyArray())

        assertThat(preference.summary.toString())
            .isEqualTo(context.getString(R.string.profile_card_add_account_summary))
        assertThat(controller.handlePreferenceTreeClick(preference)).isTrue()
        assertThat(nextStartedActivity().action).isEqualTo(Settings.ACTION_ADD_ACCOUNT)
    }

    @Test
    fun tap_accountWithResolvableIntent_launchesAccountIntent() {
        repository.account = ACCOUNT
        repository.systemActivity = ACCOUNT_COMPONENT

        controller().handlePreferenceTreeClick(preference)

        val intent = nextStartedActivity()
        assertThat(intent.action).isEqualTo(VIEW_ACCOUNT)
        assertThat(intent.component).isEqualTo(ACCOUNT_COMPONENT)
        assertThat(intent.getStringExtra("extra.accountName")).isEqualTo("a@gmail.com")
    }

    @Test
    fun tap_accountIntentNotResolvable_opensAccountDetail() {
        repository.account = ACCOUNT

        controller().handlePreferenceTreeClick(preference)

        assertOpenedAccountDetail()
    }

    @Test
    fun tap_malformedAccountIntentUri_opensAccountDetail() {
        repository.account = ACCOUNT
        repository.systemActivity = ACCOUNT_COMPONENT

        val controller = controller(config().copy(accountIntentUri = "intent:#Intent;%%%"))

        controller.handlePreferenceTreeClick(preference)

        assertOpenedAccountDetail()
    }

    @Test
    fun tap_emptyAccountIntentUri_opensAccountDetail() {
        repository.account = ACCOUNT
        repository.systemActivity = ACCOUNT_COMPONENT

        controller(config().copy(accountIntentUri = "")).handlePreferenceTreeClick(preference)

        assertOpenedAccountDetail()
    }

    @Test
    fun tap_noAccount_launchesAddAccountForType() {
        controller().handlePreferenceTreeClick(preference)

        val intent = nextStartedActivity()
        assertThat(intent.action).isEqualTo(Settings.ACTION_ADD_ACCOUNT)
        assertThat(intent.getStringArrayExtra(Settings.EXTRA_ACCOUNT_TYPES))
            .asList()
            .containsExactly(GOOGLE)
    }

    @Test
    fun tap_otherPreferenceKey_returnsFalse() {
        val other = Preference(context).apply { key = "other" }

        assertThat(controller().handlePreferenceTreeClick(other)).isFalse()
    }

    private fun controller(config: ProfileCardConfig = config()) =
        TopLevelProfileCardPreferenceController(context, KEY, repository, config)

    private fun config() =
        ProfileCardConfig(
            isEnabled = true,
            accountType = GOOGLE,
            accountIntentUri = "intent:#Intent;action=$VIEW_ACCOUNT;end",
        )

    private fun nextStartedActivity(): Intent =
        shadowOf(context as Application).nextStartedActivity

    private fun assertOpenedAccountDetail() {
        val intent = nextStartedActivity()
        assertThat(intent.getStringExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT))
            .isEqualTo(AccountDetailDashboardFragment::class.java.name)
        val args: Bundle? = intent.getBundleExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS)
        assertThat(args?.getParcelable<Account>(AccountDetailDashboardFragment.KEY_ACCOUNT))
            .isEqualTo(ACCOUNT)
        assertThat(args?.getString(AccountDetailDashboardFragment.KEY_ACCOUNT_TYPE))
            .isEqualTo(GOOGLE)
        assertThat(args?.getString(AccountDetailDashboardFragment.KEY_ACCOUNT_LABEL))
            .isEqualTo("Google")
        assertThat(args?.containsKey(AccountDetailDashboardFragment.KEY_USER_HANDLE)).isTrue()
    }

    private companion object {
        const val KEY = "top_level_profile_card"
        const val ACCOUNT_CATEGORY_KEY = "top_level_account_category"
        const val GOOGLE = "com.google"
        const val VIEW_ACCOUNT = "com.android.settings.action.VIEW_ACCOUNT"
        val ACCOUNT = Account("a@gmail.com", GOOGLE)
        val ACCOUNT_COMPONENT = ComponentName("com.example.account", "com.example.account.View")
    }
}
