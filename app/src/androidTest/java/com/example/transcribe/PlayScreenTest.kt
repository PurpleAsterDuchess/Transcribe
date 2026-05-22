package com.example.transcribe

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import com.example.transcribe.components.TopBar
import com.example.transcribe.data.*
import com.example.transcribe.screens.play.PlayScreen
import com.example.transcribe.screens.play.PlayViewModel
import com.example.transcribe.ui.theme.TranscribeTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(AuthModule::class, RepositoryModule::class)
class PlayScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var transcriptionRepo: TranscriptionRepository

    @Inject
    lateinit var userRepo: UserRepo

    @Inject
    lateinit var authRepo: AuthRepo

    private val testTranscription = Transcription(
        title = "Test Title",
        author = "Test Author",
        fileUri = "file://test/uri",
        userId = "user123"
    ).apply { id = "song123" }

    private val testUser = User(
        uid = "user123",
        firstName = "John",
        surname = "Doe"
    )

    private val mockNavController = mock(NavController::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()

        runBlocking {
            whenever(transcriptionRepo.getById("song123")).thenReturn(testTranscription)
            whenever(authRepo.getUserId()).thenReturn("user123")
            whenever(userRepo.getById("user123")).thenReturn(testUser)
            whenever(userRepo.getUserFlow(any())).thenReturn(flowOf(testUser))
            whenever(transcriptionRepo.getAll()).thenReturn(flowOf(emptyList()))
        }
    }

    @Test
    fun playScreen_displaysTranscriptionDetails() {
        setContent()

        // Wait for data to load and ViewModel to update the author
        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithText("Test Title").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
        // PlayViewModel updates author to "John Doe" based on user repo
        composeTestRule.onNodeWithText("Author: John Doe", substring = true).assertIsDisplayed()

        runBlocking {
            verify(userRepo, atLeastOnce()).addRecentTranscription(eq("user123"), any())
            verify(transcriptionRepo, atLeastOnce()).edit(any())
        }
    }

    @Test
    fun playScreen_entersEditMode() {
        setContent()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithContentDescription("edit").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithContentDescription("edit").onFirst().performClick()

        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        
        // CustomTextField values
        composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
    }

    @Test
    fun playScreen_savesEdits() {
        setContent()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithContentDescription("edit").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithContentDescription("edit").onFirst().performClick()

        val newTitle = "Updated Title"
        composeTestRule.onNodeWithText("Test Title").performTextReplacement(newTitle)
        
        composeTestRule.onNodeWithText("Save").performClick()

        runBlocking {
            verify(transcriptionRepo, atLeastOnce()).edit(argThat { title == newTitle })
        }
        
        composeTestRule.onNodeWithText(newTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
    }

    @Test
    fun playScreen_deletesTranscription() {
        setContent()

        composeTestRule.waitUntil(5000) {
            composeTestRule.onAllNodesWithContentDescription("edit").fetchSemanticsNodes().isNotEmpty()
        }

        // Click Delete icon (second "edit" content description)
        composeTestRule.onAllNodesWithContentDescription("edit").onLast().performClick()

        runBlocking {
            verify(transcriptionRepo).delete("song123")
            verify(userRepo).removeRecentTranscription("user123", "song123")
        }
        verify(mockNavController).popBackStack()
    }

    private fun setContent() {
        composeTestRule.setContent {
            TranscribeTheme {
                PlayScreen(
                    songId = "song123",
                    navController = mockNavController,
                    context = composeTestRule.activity.applicationContext
                )
            }
        }
    }
}
