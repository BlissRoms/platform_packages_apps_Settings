/*
 * SPDX-FileCopyrightText: The BlissRoms Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.homepage.profile

import android.content.Context
import android.content.res.XmlResourceParser
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.settings.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.xmlpull.v1.XmlPullParser

@RunWith(AndroidJUnit4::class)
class ProfileCardHomepageXmlTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun expressive_profileCategoryIsFirstAndHoldsCard() {
        assertProfileCard(R.xml.top_level_settings_expressive, accountCategoryOrder = -90)
    }

    @Test
    fun expressiveDesktop_profileCategoryIsFirstAndHoldsCard() {
        assertProfileCard(R.xml.top_level_settings_expressive_desktop, accountCategoryOrder = -140)
    }

    @Test
    fun legacy_profileCategoryIsFirstAndHoldsCard() {
        assertProfileCard(R.xml.top_level_settings, accountCategoryOrder = -140)
    }

    private fun assertProfileCard(xmlRes: Int, accountCategoryOrder: Int) {
        val elements = parse(xmlRes)
        val category = elements.single { it.key == CATEGORY_KEY }
        val card = elements.single { it.key == CARD_KEY }
        val accountCategory = elements.single { it.key == ACCOUNT_CATEGORY_KEY }
        val categoryOrders = elements.filter { it.depth == CATEGORY_DEPTH }.mapNotNull { it.order }

        assertThat(category.order).isEqualTo(-150)
        assertThat(categoryOrders.min()).isEqualTo(-150)
        assertThat(card.parentKey).isEqualTo(CATEGORY_KEY)
        assertThat(card.controller).isEqualTo(CONTROLLER)
        assertThat(accountCategory.order).isEqualTo(accountCategoryOrder)
    }

    private fun parse(xmlRes: Int): List<Element> {
        val parser: XmlResourceParser = context.resources.getXml(xmlRes)
        val keys = ArrayDeque<String?>()
        val elements = mutableListOf<Element>()
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            when (parser.eventType) {
                XmlPullParser.START_TAG -> {
                    val key = parser.getAttributeValue(ANDROID_NS, "key")
                    elements +=
                        Element(
                            key = key,
                            parentKey = keys.lastOrNull(),
                            depth = parser.depth,
                            order = parser.getAttributeValue(ANDROID_NS, "order")?.toInt(),
                            controller = parser.getAttributeValue(SETTINGS_NS, "controller"),
                        )
                    keys.addLast(key)
                }
                XmlPullParser.END_TAG -> keys.removeLast()
            }
        }
        return elements
    }

    private data class Element(
        val key: String?,
        val parentKey: String?,
        val depth: Int,
        val order: Int?,
        val controller: String?,
    )

    private companion object {
        const val CATEGORY_KEY = TopLevelProfileCardPreferenceController.CATEGORY_KEY
        const val CARD_KEY = "top_level_profile_card"
        val CONTROLLER: String = TopLevelProfileCardPreferenceController::class.java.name
        const val ACCOUNT_CATEGORY_KEY = "top_level_account_category"
        const val CATEGORY_DEPTH = 2
        const val ANDROID_NS = "http://schemas.android.com/apk/res/android"
        const val SETTINGS_NS = "http://schemas.android.com/apk/res-auto"
    }
}
