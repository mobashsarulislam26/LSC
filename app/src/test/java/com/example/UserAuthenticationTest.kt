package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CampLanguage
import com.example.data.model.UserRole
import com.example.data.sample.SeedData
import com.example.ui.viewmodel.CampViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class UserAuthenticationTest {

    private lateinit var viewModel: CampViewModel

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = CampViewModel(app)
    }

    @Test
    fun testAppRequiresLoginByDefault() {
        // Must be logged out by default ("login ছাড়া ব্যবহার করা যাবে না")
        assertFalse("App must not be accessible without login", viewModel.isLoggedIn.value)
        assertNull("No user account should be active before login", viewModel.currentUserAccount.value)
    }

    @Test
    fun testLoginWithValidCredentialsSucceeds() = runBlocking {
        val adminAccount = SeedData.staffAccounts.first { it.email == "admin@lsc.org" }

        val success = viewModel.login(adminAccount.email, adminAccount.passwordHash)
        assertTrue("Login with valid credentials should succeed", success)
        assertTrue("isLoggedIn must be true after successful login", viewModel.isLoggedIn.value)
        assertNotNull("currentUserAccount must be set", viewModel.currentUserAccount.value)
        assertEquals("admin@lsc.org", viewModel.currentUserAccount.value?.email)
        assertEquals(UserRole.SUPER_ADMIN, viewModel.currentRole.value)
        assertTrue("Admin actions should be unlocked for super admin", viewModel.isAdminUnlocked.value)
    }

    @Test
    fun testLoginWithInvalidCredentialsFails() = runBlocking {
        val success = viewModel.login("admin@lsc.org", "wrongPassword123")
        assertFalse("Login with wrong password must fail", success)
        assertFalse("isLoggedIn must remain false", viewModel.isLoggedIn.value)
        assertNull(viewModel.currentUserAccount.value)
        assertNotNull("Error message should be set on invalid login", viewModel.loginErrorMessage.value)
    }

    @Test
    fun testQuickLoginAndLogout() {
        val medicalOfficer = SeedData.staffAccounts.first { it.role == "MEDICAL" }

        // Quick login as medical officer
        viewModel.quickLogin(medicalOfficer)
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals("medical@lsc.org", viewModel.currentUserAccount.value?.email)
        assertEquals(UserRole.MEDICAL_TEAM, viewModel.currentRole.value)

        // Logout
        viewModel.logout()
        assertFalse("User must be logged out after calling logout()", viewModel.isLoggedIn.value)
        assertNull("currentUserAccount must be null after logout", viewModel.currentUserAccount.value)
        assertFalse(viewModel.isAdminUnlocked.value)
    }

    @Test
    fun testEmptyCredentialsValidation() = runBlocking {
        val success = viewModel.login("", "")
        assertFalse("Empty credentials should fail", success)
        assertFalse(viewModel.isLoggedIn.value)
        assertNotNull(viewModel.loginErrorMessage.value)
    }

    @Test
    fun testSignUpGeneratesPasswordAndEnablesLogin() = runBlocking {
        val testEmail = "mobashsarul_${System.currentTimeMillis()}@gmail.com"
        val fullName = "মোবাশশেরুল ইসলাম"
        val role = "VOLUNTEER"

        // Step 1: Attempt login before sign up - must fail ("login ছাড়া ব্যবহার করা যাবে না")
        val failBeforeSignUp = viewModel.login(testEmail, "anyPass123")
        assertFalse("Unregistered user must not be allowed to login", failBeforeSignUp)
        assertFalse("App cannot be used without login", viewModel.isLoggedIn.value)

        // Step 2: User signs up first ("আগে সাইন আপ করবে")
        val (signUpSuccess, generatedPassword, msg) = viewModel.signUp(
            fullName = fullName,
            email = testEmail,
            role = role,
            phone = "01712345678"
        )

        assertTrue("Sign up must succeed", signUpSuccess)
        assertTrue("A security password must be generated for email dispatch", generatedPassword.isNotBlank())
        assertTrue("Generated password must start with Lsc# prefix", generatedPassword.startsWith("Lsc#"))
        assertEquals(testEmail, viewModel.lastRegisteredEmail.value)
        assertEquals(generatedPassword, viewModel.lastGeneratedPassword.value)

        // Step 3: Login using the password sent to email ("তারপরে login")
        val loginSuccess = viewModel.login(testEmail, generatedPassword)
        assertTrue("User must be able to login with password sent to email", loginSuccess)
        assertTrue("User is now logged in", viewModel.isLoggedIn.value)
        assertEquals(testEmail, viewModel.currentUserAccount.value?.email)
        assertEquals(fullName, viewModel.currentUserAccount.value?.fullName)
        assertEquals(UserRole.VOLUNTEER, viewModel.currentRole.value)
    }

    @Test
    fun testRequestPasswordResetToEmail() = runBlocking {
        val userEmail = "admin@lsc.org"
        val (success, newPassword) = viewModel.requestPasswordToEmail(userEmail)

        assertTrue("Password dispatch request must succeed", success)
        assertTrue("New password must be generated", newPassword.startsWith("Lsc#"))

        // Login with new password
        val loginSuccess = viewModel.login(userEmail, newPassword)
        assertTrue("User must be able to log in with newly dispatched password", loginSuccess)
        assertTrue(viewModel.isLoggedIn.value)
    }

    @Test
    fun testLoginViewModelInstantiation() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        CampApplication.ensureFirebaseInitialized(app)
        val loginVm = com.example.ui.viewmodel.LoginViewModel(app)
        assertNotNull(loginVm)
    }
}
