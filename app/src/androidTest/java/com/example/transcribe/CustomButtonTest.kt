package com.example.transcribe

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.transcribe.components.CustomButton
import junit.framework.TestCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CustomButtonTest {
    private val TEXT_DISPLAY = "button text"
    private val buttonMatcher = hasText(TEXT_DISPLAY) and hasClickAction()

    @get:Rule
    var rule = createComposeRule()

    @Test
    fun `button displays text and executes function when clicked`() {
        var sampleStateToTest = false
        rule.setContent {
            CustomButton(
                text = TEXT_DISPLAY,
                onClick = { sampleStateToTest = true }
            )
        }
        rule.onNode(buttonMatcher).assertExists().performClick()
        TestCase.assertTrue(sampleStateToTest)
    }
}