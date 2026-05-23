package com.example.transcribe

import android.util.Log
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.example.transcribe.data.AuthRepo
import com.example.transcribe.data.Response
import com.example.transcribe.data.UserRepo
import com.example.transcribe.data.TranscriptionRepository
import com.example.transcribe.data.User
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@HiltAndroidTest
class LoginScreenTests {
    val VALID_PASSWORD = "passwordpassword"
    lateinit var emailAddressTextFieldMatcher: SemanticsMatcher
    lateinit var passwordTextFieldMatcher: SemanticsMatcher
    lateinit var submitButtonMatcher: SemanticsMatcher
    lateinit var forgotPasswordButtonMatcher: SemanticsMatcher
    lateinit var signUpButtonMatcher: SemanticsMatcher

    @Inject
    lateinit var authRepo: AuthRepo

    @Inject
    lateinit var userRepo: UserRepo

    @Inject
    lateinit var transcriptionRepo: TranscriptionRepository

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var rule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
        
        // Reset mocks to ensure test isolation since they are singletons in TestAuthModule
        reset(authRepo, userRepo, transcriptionRepo)
        
        // Re-establish default behaviors after reset
        whenever(authRepo.authStateFlow).thenReturn(flowOf(null))
        whenever(transcriptionRepo.getAll()).thenReturn(flowOf(emptyList()))
        whenever(userRepo.getAll()).thenReturn(flowOf(emptyList()))

        emailAddressTextFieldMatcher = hasText(rule.activity.getString(R.string.email))
        passwordTextFieldMatcher = hasText(rule.activity.getString(R.string.password))
        submitButtonMatcher = hasText(rule.activity.getString(R.string.submit_button)) and
                hasClickAction()
        forgotPasswordButtonMatcher = hasText(rule.activity.getString(R.string.forgot_password)) and hasClickAction ()
        signUpButtonMatcher = hasText(rule.activity.getString(R.string.sign_up_button)) and
                hasClickAction()
    }

    fun performFailedLoginTest() {
        val exception = Exception("User not found")
        runBlocking {
            whenever(authRepo.signInWithEmailAndPassword(any(), any()))
                .thenReturn(Response.Failure(exception))
        }
    }

    fun performSuccessfulLogin(
        uid: String,
        email: String,
        role: UserRole
    ) {
        val mockUser = mock(FirebaseUser::class.java)
        whenever(mockUser.uid).thenReturn(uid)
        whenever(mockUser.isEmailVerified).thenReturn(true)
        whenever(authRepo.currentUser).thenReturn(mockUser)
        whenever(authRepo.getUserId()).thenReturn(uid)
        whenever(authRepo.authStateFlow).thenReturn(flowOf(mockUser))

        val user = User(uid = uid, email = email, role = role)
        whenever(userRepo.getUserFlow(uid)).thenReturn(flowOf(user))

        runBlocking {
            whenever(authRepo.signInWithEmailAndPassword(email, VALID_PASSWORD))
                .thenReturn(Response.Success)
            whenever(userRepo.getUserRole(any())).thenReturn(role)
            whenever(transcriptionRepo.getById(any())).thenReturn(null)
        }

        whenever(transcriptionRepo.getAll()).thenReturn(flowOf(emptyList()))
        whenever(userRepo.getAll()).thenReturn(flowOf(listOf(user)))

        rule.onNode(emailAddressTextFieldMatcher).performTextInput(email)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNode(submitButtonMatcher).performClick()
    }

    @Test
    fun `check the initial state of the login in page`() {
        val loginPage = hasText(rule.activity.getString(R.string.login))
        rule.onNode(loginPage).assertExists()
        rule.onNode(submitButtonMatcher).assertExists()
        rule.onNode(forgotPasswordButtonMatcher).assertExists()
        rule.onNode(signUpButtonMatcher).assertExists()
        rule.onNode(emailAddressTextFieldMatcher).assertExists()
        rule.onNode(passwordTextFieldMatcher).assertExists()
    }

    @Test
    fun `check if navigated to the sign up page when sign up button clicked`() {
        rule.onNode(signUpButtonMatcher).performClick()
        val signUpPage = hasText(rule.activity.getString(R.string.sign_up))
        rule.onNode(signUpPage).assertExists()
    }

    @Test
    fun `providing incorrect email causes validation to be displayed`() {
        val invalidEmail = "test"
        rule.onNode(emailAddressTextFieldMatcher).performTextInput(invalidEmail)
        rule.onNode(passwordTextFieldMatcher).performTextInput(VALID_PASSWORD)
        rule.onNodeWithText(rule.activity.getString((R.string.email_error_message)))
            .assertIsDisplayed()
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun `providing incorrect password causes validation to be displayed`() {
        val invalidPassword = "123"
        rule.onNode(emailAddressTextFieldMatcher).performTextInput("test@example.com")
        rule.onNode(passwordTextFieldMatcher).performTextInput(invalidPassword)
        rule.onNodeWithText(rule.activity.getString(R.string.password_error_message))
            .assertIsDisplayed()
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun `providing both incorrect email and password causes validation messages to be displayed`() {
        rule.onNode(emailAddressTextFieldMatcher).performTextInput("invalid-email")
        rule.onNode(passwordTextFieldMatcher).performTextInput("123")
        rule.onNodeWithText(rule.activity.getString(R.string.email_error_message))
            .assertIsDisplayed()
        rule.onNodeWithText(rule.activity.getString(R.string.password_error_message))
            .assertIsDisplayed()
        rule.onNode(submitButtonMatcher).assertIsDisplayed().assertIsNotEnabled()
    }
}
