package com.example.transcribe

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.transcribe.components.TopBar
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `topbar contains title` () {
        val testTitle = "Transcribe App"
        
        composeTestRule.setContent {
            TopBar(
                text = testTitle,
                onMenuIconClick = {}
            )
        }

        composeTestRule.onNodeWithText(testTitle).assertIsDisplayed()
    }

    @Test
    fun `topbar contains menu icon` () {
        var menuClicked = false
        
        composeTestRule.setContent {
            TopBar(
                text = "Title",
                onMenuIconClick = { menuClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("navigation")
            .assertIsDisplayed()
            .performClick()
        
        assertTrue("Menu icon click should trigger callback", menuClicked)
    }

    @Test
    fun `topbar contains custom action` () {
        val actionTag = "custom_action"
        
        composeTestRule.setContent {
            TopBar(
                text = "Title",
                onMenuIconClick = {},
                actions = {
                    Text(
                        text = "Action", 
                        modifier = Modifier.testTag(actionTag)
                    )
                }
            )
        }

        composeTestRule.onNodeWithTag(actionTag).assertIsDisplayed()
        composeTestRule.onNodeWithText("Action").assertIsDisplayed()
    }
}
