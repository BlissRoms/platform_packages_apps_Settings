/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.settings.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileCardPreferenceTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun onBindViewHolder_bindsTitleToOwnNameView() {
        val preference = ProfileCardPreference(context).apply { title = "Jane" }
        val holder = bind(preference)

        assertThat(holder.findViewById(R.id.profile_card_name)).isInstanceOf(TextView::class.java)
        assertThat((holder.findViewById(R.id.profile_card_name) as TextView).text.toString())
            .isEqualTo("Jane")
    }

    @Test
    fun layout_hasNoStandardTitleView() {
        val holder = bind(ProfileCardPreference(context))

        assertThat(holder.findViewById(android.R.id.title)).isNull()
    }

    @Test
    fun onBindViewHolder_titleChanged_rebindsName() {
        val preference = ProfileCardPreference(context).apply { title = "Jane" }
        val holder = bind(preference)

        preference.title = "Alex"
        preference.onBindViewHolder(holder)

        assertThat((holder.findViewById(R.id.profile_card_name) as TextView).text.toString())
            .isEqualTo("Alex")
    }

    private fun bind(preference: ProfileCardPreference): PreferenceViewHolder {
        val view = LayoutInflater.from(context).inflate(preference.layoutResource, null)
        return PreferenceViewHolder.createInstanceForTests(view).also {
            preference.onBindViewHolder(it)
        }
    }
}
