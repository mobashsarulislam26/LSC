package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firestore.FirestoreManager
import com.example.data.model.UserRole
import com.example.data.model.toUserRole
import com.example.data.repository.CampRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Authentication state representation for the app.
 */
sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val user: FirebaseUser?, val role: UserRole) : AuthState()
    data class RoleSelectionRequired(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * Base LoginViewModel handling user authentication states (Email/Password, Google Sign-In)
 * and role-based fetching/assignment with Firestore integration.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth? by lazy {
        try {
            Firebase.auth
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Firebase Auth not initialized: ${e.message}")
            null
        }
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _userRole = MutableStateFlow<UserRole>(UserRole.CAMP_CHIEF)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        checkCurrentUserState()
    }

    /**
     * Checks if a user is currently signed in and fetches their role from Firestore.
     */
    fun checkCurrentUserState() {
        val user = try { auth?.currentUser } catch (e: Exception) { null }
        if (user != null) {
            fetchUserRoleAndSetState(user)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Fetches the user role from Firestore or prompts role selection if not set.
     */
    private fun fetchUserRoleAndSetState(user: FirebaseUser) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val fetchedRole = FirestoreManager.getVerifiedUserRole(user)
                if (fetchedRole != null) {
                    _userRole.value = fetchedRole
                    _authState.value = AuthState.Authenticated(user, fetchedRole)
                } else {
                    _authState.value = AuthState.RoleSelectionRequired(user)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Failed to fetch user role: ${e.message}")
                _authState.value = AuthState.Authenticated(user, _userRole.value)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Assigns and saves selected role for the user in Firestore.
     */
    fun assignRoleAndProceed(user: FirebaseUser, role: UserRole, campName: String = "Rover Camp") {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                FirestoreManager.saveUserRole(
                    uid = user.uid,
                    email = user.email ?: "",
                    role = role,
                    campName = campName
                )
                _userRole.value = role
                _authState.value = AuthState.Authenticated(user, role)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save role: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Signs in using Email & Password via Firebase Auth with local Room SHA-256 fallback.
     */
    fun signInWithEmail(email: String, pass: String) {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = pass.trim()
        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty."
            return
        }
        val firebaseAuth = auth
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            _authState.value = AuthState.Authenticating
            try {
                if (firebaseAuth != null) {
                    try {
                        val result = firebaseAuth.signInWithEmailAndPassword(cleanEmail, cleanPass).await()
                        val user = result.user
                        if (user != null) {
                            fetchUserRoleAndSetState(user)
                            return@launch
                        }
                    } catch (e: Exception) {
                        Log.w("LoginViewModel", "Firebase Auth sign-in failed or offline: ${e.message}")
                    }
                }

                // Fallback to local Room database salted SHA-256 hash verification
                val repo = CampRepository.getInstance(getApplication())
                val authenticatedUser = repo.authenticateUser(cleanEmail, cleanPass)
                val currentFbUser = try { firebaseAuth?.currentUser } catch (_: Exception) { null }
                if (authenticatedUser != null) {
                    val role = authenticatedUser.toUserRole()
                    _userRole.value = role
                    _authState.value = AuthState.Authenticated(currentFbUser, role)
                } else {
                    _errorMessage.value = "Invalid email or password."
                    _authState.value = AuthState.Error("Invalid email or password.")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Sign in error: ${e.message}")
                _errorMessage.value = e.localizedMessage ?: "Invalid email or password."
                _authState.value = AuthState.Error(e.localizedMessage ?: "Invalid email or password.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Registers a new user with Email & Password in both Room and Firebase Auth.
     */
    fun signUpWithEmail(email: String, pass: String, selectedRole: UserRole) {
        val cleanEmail = email.trim().lowercase()
        val cleanPass = pass.trim()
        if (cleanEmail.isBlank() || cleanPass.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty."
            return
        }
        val firebaseAuth = auth
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _errorMessage.value = null
            _authState.value = AuthState.Authenticating
            try {
                val repo = CampRepository.getInstance(getApplication())
                repo.signUpUser(
                    fullName = cleanEmail.substringBefore("@"),
                    email = cleanEmail,
                    role = selectedRole.name,
                    customPassword = cleanPass
                )

                if (firebaseAuth != null) {
                    val result = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, cleanPass).await()
                    val user = result.user
                    if (user != null) {
                        assignRoleAndProceed(user, selectedRole)
                        return@launch
                    }
                }
                _userRole.value = selectedRole
                _authState.value = AuthState.Authenticated(null, selectedRole)
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Sign up error: ${e.message}")
                _errorMessage.value = e.localizedMessage ?: "Sign-up failed."
                _authState.value = AuthState.Error(e.localizedMessage ?: "Sign-up failed.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Launches Google Sign-In via Jetpack CredentialManager.
     */
    fun launchGoogleSignIn(context: Context, serverClientId: String) {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            _errorMessage.value = "Authentication service unavailable."
            _authState.value = AuthState.Unauthenticated
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, request)

                val googleIdCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val idToken = googleIdCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()

                val user = authResult.user
                if (user != null) {
                    fetchUserRoleAndSetState(user)
                } else {
                    _authState.value = AuthState.Error("Google Sign-In failed.")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Google Sign In Error: ${e.message}")
                _errorMessage.value = "Google Sign-In: ${e.localizedMessage ?: "Failed or cancelled"}"
                _authState.value = AuthState.Unauthenticated
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Signs out current Firebase user.
     */
    fun signOut() {
        auth?.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
