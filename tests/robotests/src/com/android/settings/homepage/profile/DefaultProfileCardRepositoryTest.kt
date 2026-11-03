/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AuthenticatorDescription
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.UserInfo
import android.os.UserManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.settings.R
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class DefaultProfileCardRepositoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val accountManager = AccountManager.get(context)
    private val userManager: UserManager = mock {
        on { getUserName() } doReturn "Jane"
        on { getUserInfo(any()) } doReturn UserInfo(0, "Jane", 0)
    }

    private lateinit var repository: DefaultProfileCardRepository

    @Before
    fun setUp() {
        repository = DefaultProfileCardRepository(context, userManager, accountManager)
    }

    @Test
    fun isEligibleUser_regularUser_true() {
        assertThat(repository.isEligibleUser()).isTrue()
    }

    @Test
    fun isEligibleUser_guest_false() {
        whenever(userManager.isGuestUser).thenReturn(true)

        assertThat(repository.isEligibleUser()).isFalse()
    }

    @Test
    fun isEligibleUser_demoUser_false() {
        whenever(userManager.isDemoUser).thenReturn(true)

        assertThat(repository.isEligibleUser()).isFalse()
    }

    @Test
    fun isEligibleUser_restrictedProfile_false() {
        whenever(userManager.isRestrictedProfile).thenReturn(true)

        assertThat(repository.isEligibleUser()).isFalse()
    }

    @Test
    fun isEligibleUser_disallowModifyAccounts_false() {
        whenever(userManager.hasUserRestriction(UserManager.DISALLOW_MODIFY_ACCOUNTS))
            .thenReturn(true)

        assertThat(repository.isEligibleUser()).isFalse()
    }

    @Test
    fun userName_returnsUserManagerName() {
        assertThat(repository.userName()).isEqualTo("Jane")
    }

    @Test
    fun userName_blank_fallsBackToOwner() {
        whenever(userManager.userName).thenReturn(" ")

        assertThat(repository.userName()).isEqualTo(context.getString(R.string.user_owner))
    }

    @Test
    fun userIcon_neverNull() {
        assertThat(repository.userIcon()).isNotNull()
    }

    @Test
    fun authenticatorInfo_registered_returnsLabelAndPackage() {
        shadowOf(accountManager).addAuthenticator(
            AuthenticatorDescription(GOOGLE, context.packageName, R.string.user_owner, 0, 0, 0)
        )

        val info = repository.authenticatorInfo(GOOGLE)

        assertThat(info).isEqualTo(
            AuthenticatorInfo(
                label = context.getString(R.string.user_owner),
                packageName = context.packageName,
                labelResId = R.string.user_owner,
            )
        )
    }

    @Test
    fun authenticatorInfo_notRegistered_returnsNull() {
        assertThat(repository.authenticatorInfo(GOOGLE)).isNull()
    }

    @Test
    fun authenticatorInfo_noLabel_returnsNull() {
        shadowOf(accountManager).addAuthenticator(
            AuthenticatorDescription(GOOGLE, context.packageName, 0, 0, 0, 0)
        )

        assertThat(repository.authenticatorInfo(GOOGLE)).isNull()
    }

    @Test
    fun firstAccount_returnsFirstOfType() {
        shadowOf(accountManager).addAccount(Account("a@gmail.com", GOOGLE))
        shadowOf(accountManager).addAccount(Account("b@gmail.com", GOOGLE))
        shadowOf(accountManager).addAccount(Account("c@example.com", "com.other"))

        assertThat(repository.firstAccount(GOOGLE)).isEqualTo(Account("a@gmail.com", GOOGLE))
    }

    @Test
    fun firstAccount_none_returnsNull() {
        assertThat(repository.firstAccount(GOOGLE)).isNull()
    }

    @Test
    fun resolveSystemActivity_systemApp_returnsComponent() {
        val intent = Intent(VIEW_ACCOUNT)
        val component = ComponentName("com.example.account", "com.example.account.View")
        shadowOf(context.packageManager).addActivityIfNotPresent(component)
        shadowOf(context.packageManager).addIntentFilterForActivity(
            component,
            android.content.IntentFilter(VIEW_ACCOUNT).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
            },
        )
        markSystemApp(component)

        assertThat(repository.resolveSystemActivity(intent)).isEqualTo(component)
    }

    @Test
    fun resolveSystemActivity_noMatch_returnsNull() {
        assertThat(repository.resolveSystemActivity(Intent(VIEW_ACCOUNT))).isNull()
    }

    private fun markSystemApp(component: ComponentName) {
        val activityInfo: ActivityInfo =
            context.packageManager.getActivityInfo(component, 0)
        activityInfo.applicationInfo.flags =
            activityInfo.applicationInfo.flags or ApplicationInfo.FLAG_SYSTEM
        shadowOf(context.packageManager).addOrUpdateActivity(activityInfo)
    }

    private companion object {
        const val GOOGLE = "com.google"
        const val VIEW_ACCOUNT = "com.android.settings.action.VIEW_ACCOUNT"
    }
}
