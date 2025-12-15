package com.elsalakan.carcaremanager

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_starts_and_navigates_to_maintenance_screen() {

        // Check Car Screen title exists
        composeTestRule
            .onNodeWithText("🚗 CarCare Manager")
            .assertIsDisplayed()

        // Click Maintenance button
        composeTestRule
            .onNodeWithText("📋 Maintenance Log")
            .assertIsDisplayed()
            .performClick()

        // Verify Maintenance screen opened
        composeTestRule
            .onNodeWithText("Maintenance Log")
            .assertIsDisplayed()
    }
}
