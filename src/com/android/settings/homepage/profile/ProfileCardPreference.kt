/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.android.settings.R

class ProfileCardPreference
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : Preference(context, attrs) {

    init {
        layoutResource = R.layout.bliss_homepage_profile_card
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        (holder.findViewById(R.id.profile_card_name) as? TextView)?.text = title
    }
}
