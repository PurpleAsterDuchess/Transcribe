package com.example.transcribe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import com.example.transcribe.data.*
import com.example.transcribe.screens.admin.AdminHomeScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.reset
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class AdminHomeScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var composeTestRule = createAndroidComposeRule<HiltTestActivity>()

    @Inject
    lateinit var userRepo: UserRepo

    @Inject
    lateinit var transcriptionRepo: TranscriptionRepository

    private val testUser = User(
        uid = "user123",
        firstName = "John",
        surname = "Doe",
        email = "john.doe@example.com",
        role = UserRole.USER
    )

    private val adminUser = User(
        uid = "admin456",
        firstName = "Admin",
        surname = "User",
        email = "admin@example.com",
        role = UserRole.ADMIN
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        reset(userRepo, transcriptionRepo)
        

        whenever(userRepo.getAll()).thenReturn(MutableStateFlow(emptyList()))
        whenever(transcriptionRepo.getAll()).thenReturn(MutableStateFlow(emptyList()))
    }

    private fun setContent() {
        composeTestRule.setContent {
            AdminHomeScreen(
                userRole = UserRole.ADMIN,
                navController = rememberNavController(),
                onIndexChange = {},
                onClickToEdit = {},
                context = composeTestRule.activity
            )
        }
    }

    @Test
    fun `show all users` () {
        whenever(userRepo.getAll()).thenReturn(MutableStateFlow(listOf(testUser, adminUser)))

        setContent()

        // Assert
        composeTestRule.onNodeWithText("User Management").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("john.doe@example.com", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Admin User").assertIsDisplayed()
    }

    @Test
    fun `no users`() {
        whenever(userRepo.getAll()).thenReturn(MutableStateFlow(emptyList()))

        setContent()

        composeTestRule.onNodeWithText("No users found.").assertIsDisplayed()
    }

    @Test
    fun `select user` () {
        whenever(userRepo.getAll()).thenReturn(MutableStateFlow(listOf(testUser)))

        setContent()

        composeTestRule.onNodeWithText("John Doe").performClick()

        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("john.doe@example.com", substring = true).assertIsDisplayed()
        
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun `return to homescreen`() {
        whenever(userRepo.getAll()).thenReturn(MutableStateFlow(listOf(testUser)))

        setContent()
        composeTestRule.onNodeWithText("John Doe").performClick() // Navigate to details

        composeTestRule.onNodeWithText("User Details").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        composeTestRule.onNodeWithText("User Management").assertIsDisplayed()
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
    }
}
