package com.example.ui.screens.auth

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CampLanguage
import com.example.ui.components.CampLogoImage
import com.example.ui.theme.*
import com.example.ui.viewmodel.CampViewModel
import kotlinx.coroutines.launch

/**
 * Dedicated Camp Staff & Official Login / Sign-Up Screen.
 * Strictly gates the application so that no camp data, dashboard, or modules
 * can be accessed without valid authentication ("login ছাড়া ব্যবহার করা যাবে না").
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    campViewModel: CampViewModel,
    language: CampLanguage,
    onLanguageToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // 0 = Login, 1 = Sign Up
    var authMode by remember { mutableIntStateOf(0) }

    // Login Form State (strictly empty by default — user must authenticate)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Sign Up Form State
    var signUpFullName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPhone by remember { mutableStateOf("") }
    var signUpCustomPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }
    var signUpRole by remember { mutableStateOf("VOLUNTEER") }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }

    // Forgot Password / Email Password Dispatch Dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }

    // Sign Up Success Notification Dialog
    var showSignUpSuccessDialog by remember { mutableStateOf(false) }
    var generatedPasswordForUser by remember { mutableStateOf("") }
    var registeredUserEmail by remember { mutableStateOf("") }

    val errorMessage by campViewModel.loginErrorMessage.collectAsStateWithLifecycle()
    val isAuthenticating by campViewModel.isAuthenticating.collectAsStateWithLifecycle()
    val isSigningUp by campViewModel.isSigningUp.collectAsStateWithLifecycle()

    val isBangla = language == CampLanguage.BANGLA

    val rolesList = listOf(
        "STATION_MANAGER" to if (isBangla) "স্টেশন ম্যানেজার (৬ স্টেশন রোটেশন প্রধান)" else "Station Manager (6-Station Rotation Lead)",
        "STATION_MODERATOR" to if (isBangla) "স্টেশন মডারেটর (স্টেশন ১–৬)" else "Station Moderator",
        "GROUP_LEADER" to if (isBangla) "গ্রুপ লিডার (৬টি মূল গ্রুপ)" else "Group Leader",
        "MENTOR" to if (isBangla) "সাব-গ্রুপ ইনচার্জ (২৪টি সাব-গ্রুপ)" else "Subgroup In-charge / Mentor",
        "JUDGE" to if (isBangla) "প্রতিযোগিতা বিচারক (২১টি ইভেন্ট)" else "Competition Judge",
        "MEDICAL" to if (isBangla) "মেডিকেল ও ডেন্টাল টিম (স্টেশন-৬)" else "Medical & Dental Team",
        "LOGISTICS" to if (isBangla) "উপহার, খাবার ও লজিস্টিকস কর্মকর্তা" else "Gift, Food & Logistics Officer",
        "BULLETIN_EDITOR" to if (isBangla) "ক্যাম্প বুলেটিন ও মিডিয়া সম্পাদক" else "Bulletin & Media Editor",
        "VOLUNTEER" to if (isBangla) "ভলান্টিয়ার / রেজিস্ট্রেশন অফিসার" else "Volunteer / Registration Officer",
        "CAMP_CHIEF" to if (isBangla) "ক্যাম্প প্রধান / প্রোগ্রাম চেয়ারম্যান" else "Camp Chief / Chairman",
        "SUPER_ADMIN" to if (isBangla) "ক্যাম্প সুপার অ্যাডমিন / আইটি কন্ট্রোল" else "Camp Super Admin"
    )

    val officialQuickRoles = listOf(
        Triple("স্টেশন ম্যানেজার", "stationmanager@lsc.org", "আবদুল মোহাইমিন নাঈম"),
        Triple("ক্যাম্প প্রধান", "chief@lsc.org", "এয়ার কমোডোর শওকত আলী"),
        Triple("মেডিকেল (S6)", "medical@lsc.org", "ডা. তাওহীদ"),
        Triple("প্রধান বিচারক", "judge@lsc.org", "আনিসুর রহমান খন্দকার"),
        Triple("গ্রুপ লিডার", "leader@lsc.org", "আয়াতুল্লাহ নাহিদ"),
        Triple("উপহার ও খাবার", "logistics@lsc.org", "মোহাম্মাদ আতাউল্লাহ"),
        Triple("বুলেটিন", "bulletin@lsc.org", "আশরাফুল ইসলাম"),
        Triple("সুপার অ্যাডমিন", "admin@lsc.org", "ড. রেদওয়ানুর রহমান")
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("login_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF064E3B)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
            ) {
                // Top Row: Language Toggle & Mandatory Lock Indicator
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "লগইন বাধ্যতামূলক" else "Login Mandatory",
                                    color = Color(0xFFFCA5A5),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Language Toggle Button
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .testTag("login_lang_toggle_btn")
                                .clickable { onLanguageToggle() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = "Language",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "English" else "বাংলা",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Brand Hero & App Title
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CampLogoImage(
                            size = 76.dp,
                            elevation = 8.dp,
                            modifier = Modifier.testTag("login_logo")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBangla) "লাইফ স্কিলস ক্যাম্পিং ২০২৬" else "Life Skills Camping 2026",
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (isBangla)
                                "নিজকে গড়ো, গড়তে শেখাও • ২২–২৬ অক্টোবর ২০২৬ • ওয়ামী বাংলাদেশ"
                            else
                                "WAMY Bangladesh • 22–26 Oct 2026 • Rangamati Waterfront Resort",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            color = LscGoldLight.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, LscGoldSecondary.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = if (isBangla)
                                    "⚠️ অনুমতি ব্যতীত প্রবেশ নিষেধ (login ছাড়া ব্যবহার করা যাবে না)"
                                else
                                    "⚠️ Unauthorized access prohibited (Login mandatory)",
                                color = LscGoldLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Auth Form Card (with Login / Sign-Up Tabs)
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Tab Selector: [লগইন (Sign In)] | [সাইন আপ (Sign Up)]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (authMode == 0) LscEmeraldPrimary else Color.Transparent)
                                        .clickable {
                                            authMode = 0
                                            campViewModel.clearLoginError()
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("tab_login"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Login,
                                            contentDescription = null,
                                            tint = if (authMode == 0) Color.White else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBangla) "লগইন (Sign In)" else "Sign In",
                                            color = if (authMode == 0) Color.White else Color.Gray,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (authMode == 1) LscEmeraldPrimary else Color.Transparent)
                                        .clickable {
                                            authMode = 1
                                            campViewModel.clearLoginError()
                                        }
                                        .padding(vertical = 10.dp)
                                        .testTag("tab_signup"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.PersonAdd,
                                            contentDescription = null,
                                            tint = if (authMode == 1) Color.White else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBangla) "সাইন আপ (Sign Up)" else "Sign Up",
                                            color = if (authMode == 1) Color.White else Color.Gray,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Error Message Banner
                            AnimatedVisibility(
                                visible = errorMessage != null,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                        .testTag("login_error_banner")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = "Error",
                                            tint = Color(0xFFB91C1C),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = errorMessage ?: "",
                                            color = Color(0xFFB91C1C),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // MODE 0: LOGIN
                            if (authMode == 0) {
                                Text(
                                    text = if (isBangla) "আপনার অ্যাকাউন্টে প্রবেশ করুন" else "Sign in to your account",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LscSlateDark
                                )

                                Text(
                                    text = if (isBangla)
                                        "দ্রুত লগইনের জন্য নিচে আপনার বিভাগীয় পদবিতে ট্যাপ করে ইমেইল নির্বাচন করুন ও পাসওয়ার্ড দিন"
                                    else
                                        "Tap your department role chip below to fill official email, then enter password",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                                )

                                // 1-Tap Official Role Email Quick-Select Strip
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp)
                                ) {
                                    items(officialQuickRoles) { (roleTitle, roleEmail, personName) ->
                                        val isSelected = email.equals(roleEmail, ignoreCase = true)
                                        Surface(
                                            color = if (isSelected) LscEmeraldPrimary else Color(0xFFF8FAFC),
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(
                                                1.dp,
                                                if (isSelected) LscEmeraldPrimary else Color(0xFFCBD5E1)
                                            ),
                                            modifier = Modifier.clickable {
                                                email = roleEmail
                                                campViewModel.clearLoginError()
                                            }
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "⚡ $roleTitle",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else LscSlateDark
                                                )
                                                Text(
                                                    text = personName,
                                                    fontSize = 9.sp,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }

                                // Email Field
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        campViewModel.clearLoginError()
                                    },
                                    label = { Text(if (isBangla) "অফিসিয়াল ইমেইল" else "Official Email") },
                                    placeholder = { Text("stationmanager@lsc.org") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = "Email",
                                            tint = LscEmeraldPrimary
                                        )
                                    },
                                    trailingIcon = {
                                        if (email.isNotEmpty()) {
                                            IconButton(onClick = { email = "" }) {
                                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LscEmeraldPrimary,
                                        focusedLabelColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_email_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Password Field
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        campViewModel.clearLoginError()
                                    },
                                    label = { Text(if (isBangla) "পাসওয়ার্ড / পিন কোড" else "Password / Passcode") },
                                    placeholder = { Text("••••••••") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Password",
                                            tint = LscEmeraldPrimary
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                                tint = Color.Gray
                                            )
                                        }
                                    },
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            focusManager.clearFocus()
                                            coroutineScope.launch {
                                                campViewModel.login(email, password)
                                            }
                                        }
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LscEmeraldPrimary,
                                        focusedLabelColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input")
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Helper Links Row: Forgot Password & Sign Up shortcut
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            forgotEmailInput = email
                                            showForgotPasswordDialog = true
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.testTag("login_forgot_password_btn")
                                    ) {
                                        Text(
                                            text = if (isBangla) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot password?",
                                            fontSize = 11.sp,
                                            color = LscEmeraldPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    TextButton(
                                        onClick = {
                                            authMode = 1
                                            campViewModel.clearLoginError()
                                        },
                                        contentPadding = PaddingValues(0.dp),
                                        modifier = Modifier.testTag("login_go_to_signup_btn")
                                    ) {
                                        Text(
                                            text = if (isBangla) "আগে সাইন আপ করুন" else "Sign up first",
                                            fontSize = 11.sp,
                                            color = Color(0xFF2563EB),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Login Button
                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        coroutineScope.launch {
                                            campViewModel.login(email, password)
                                        }
                                    },
                                    enabled = !isAuthenticating,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("login_submit_btn")
                                ) {
                                    if (isAuthenticating) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = if (isBangla) "যাচাই করা হচ্ছে..." else "Verifying...",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Login,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isBangla) "লগইন করুন (Enter App)" else "Sign In (Enter App)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                // MODE 1: SIGN UP WITH EMAIL PASSWORD DISPATCH
                                Text(
                                    text = if (isBangla) "ক্যাম্প স্টাফ রেজিস্ট্রেশন (Sign Up)" else "Staff Registration (Sign Up)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LscSlateDark
                                )

                                Text(
                                    text = if (isBangla)
                                        "আগে সাইন আপ সম্পন্ন করবেন, আপনার ইমেলে পাসওয়ার্ড পাঠানো হবে, এরপর সেই পাসওয়ার্ড দিয়ে লগইন করবেন।"
                                    else
                                        "Sign up first, your security password will be sent to your email, then login with it.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                )

                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF93C5FD)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MailOutline,
                                            contentDescription = null,
                                            tint = Color(0xFF1D4ED8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isBangla)
                                                "📩 নিয়ম: সাইন আপ বাটনে চাপ দিলে একটি পাসওয়ার্ড তৈরি করে আপনার ইমেলে পাঠানো হবে।"
                                            else
                                                "📩 Rule: Upon signing up, a password will be generated and dispatched to your email.",
                                            fontSize = 11.sp,
                                            color = Color(0xFF1E3A8A),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = signUpFullName,
                                    onValueChange = {
                                        signUpFullName = it
                                        campViewModel.clearLoginError()
                                    },
                                    label = { Text(if (isBangla) "পূর্ণ নাম (Full Name)" else "Full Name") },
                                    placeholder = { Text(if (isBangla) "যেমন: আবদুল মোহাইমিন নাঈম" else "e.g. Abdul Mohaimin Nayeem") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = LscEmeraldPrimary)
                                    },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LscEmeraldPrimary,
                                        focusedLabelColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_fullname_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = signUpEmail,
                                    onValueChange = {
                                        signUpEmail = it
                                        campViewModel.clearLoginError()
                                    },
                                    label = { Text(if (isBangla) "আপনার ইমেইল ঠিকানা (Email)" else "Email Address") },
                                    placeholder = { Text("staff@lsc.org") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = LscEmeraldPrimary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LscEmeraldPrimary,
                                        focusedLabelColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_email_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                ExposedDropdownMenuBox(
                                    expanded = isRoleDropdownExpanded,
                                    onExpandedChange = { isRoleDropdownExpanded = !isRoleDropdownExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = rolesList.find { it.first == signUpRole }?.second ?: signUpRole,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(if (isBangla) "ক্যাম্প পদবী / দায়িত্ব" else "Camp Role") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Badge, contentDescription = null, tint = LscEmeraldPrimary)
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded)
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = LscEmeraldPrimary,
                                            focusedLabelColor = LscEmeraldPrimary
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                            .testTag("signup_role_selector")
                                    )

                                    ExposedDropdownMenu(
                                        expanded = isRoleDropdownExpanded,
                                        onDismissRequest = { isRoleDropdownExpanded = false }
                                    ) {
                                        rolesList.forEach { (roleKey, roleLabel) ->
                                            DropdownMenuItem(
                                                text = { Text(roleLabel, fontSize = 12.sp) },
                                                onClick = {
                                                    signUpRole = roleKey
                                                    isRoleDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = signUpPhone,
                                    onValueChange = { signUpPhone = it },
                                    label = { Text(if (isBangla) "মোবাইল নম্বর (Phone)" else "Mobile Phone") },
                                    placeholder = { Text("017XXXXXXXX") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = LscEmeraldPrimary)
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LscEmeraldPrimary,
                                        focusedLabelColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_phone_input")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = signUpCustomPassword,
                                    onValueChange = {
                                        signUpCustomPassword = it
                                        campViewModel.clearLoginError()
                                    },
                                    label = {
                                        Text(
                                            if (isBangla) "পাসওয়ার্ড (ঐচ্ছিক — খালি রাখলে অটো তৈরি হবে)"
                                            else "Password (Optional — leave blank to auto-generate)"
                                        )
                                    },
                                    placeholder = {
                                        Text(
                                            if (isBangla) "কমপক্ষে ৪ অক্ষর অথবা খালি রাখুন"
                                            else "Min 4 chars or leave blank for Lsc#XXXX"
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = LscEmeraldPrimary)
                                    },
                                    trailingIcon = {
                                        IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                            Icon(
                                                imageVector = if (signUpPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle password",
                                                tint = Color.Gray
                                            )
                                        }
                                    },
                                    visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LscEmeraldPrimary,
                                        focusedLabelColor = LscEmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("signup_password_input")
                                )

                                AnimatedVisibility(visible = errorMessage != null) {
                                    Surface(
                                        color = Color(0xFFFEF2F2),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = null,
                                                tint = Color(0xFFDC2626),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = errorMessage.orEmpty(),
                                                color = Color(0xFFB91C1C),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        coroutineScope.launch {
                                            val (success, generatedPass, _) = campViewModel.signUp(
                                                fullName = signUpFullName,
                                                email = signUpEmail,
                                                role = signUpRole,
                                                phone = signUpPhone,
                                                customPassword = signUpCustomPassword
                                            )
                                            if (success) {
                                                generatedPasswordForUser = generatedPass
                                                registeredUserEmail = signUpEmail.trim().lowercase()
                                                showSignUpSuccessDialog = true

                                                try {
                                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                                        data = Uri.parse("mailto:$registeredUserEmail")
                                                        putExtra(Intent.EXTRA_SUBJECT, "Life Skills Camping 2026 - আপনার লগইন পাসওয়ার্ড")
                                                        putExtra(
                                                            Intent.EXTRA_TEXT,
                                                            """
                                                            প্রিয় $signUpFullName,
                                                            
                                                            লাইফ স্কিলস ক্যাম্পিং ২০২৬ (WAMY Bangladesh)-এ আপনার রেজিস্ট্রেশন সফল হয়েছে।
                                                            
                                                            ইমেইল: $registeredUserEmail
                                                            ভূমিকা: $signUpRole
                                                            পাসওয়ার্ড: $generatedPass
                                                            """.trimIndent()
                                                        )
                                                    }
                                                    context.startActivity(Intent.createChooser(emailIntent, "Send password via Email"))
                                                } catch (_: Exception) {}
                                            }
                                        }
                                    },
                                    enabled = !isSigningUp,
                                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("signup_submit_btn")
                                ) {
                                    if (isSigningUp) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isBangla) "অ্যাকাউন্ট তৈরি হচ্ছে..." else "Creating account...")
                                    } else {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isBangla) "সাইন আপ ও পাসওয়ার্ড সংরক্ষণ" else "Sign Up & Save Password",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                TextButton(
                                    onClick = {
                                        authMode = 0
                                        campViewModel.clearLoginError()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isBangla) "পূর্বেই সাইন আপ করেছেন? এখানে লগইন করুন" else "Already signed up? Login here",
                                        color = LscEmeraldPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isBangla)
                                    "নিরাপত্তা নীতিমালা: নিবন্ধিত ইমেইল ও বৈধ পাসওয়ার্ড ছাড়া অ্যাপের কোনো মডিউলে প্রবেশ করা যাবে না। (রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট, শফিপুর, গাজীপুর)"
                                else
                                    "Security Policy: Access to all camp modules requires a registered email and valid password.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSignUpSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSignUpSuccessDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LscEmeraldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(30.dp))
                }
            },
            title = {
                Text(
                    text = if (isBangla) "রেজিস্ট্রেশন সফল ও পাসওয়ার্ড প্রস্তুত!" else "Registration Successful!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isBangla)
                            "আপনার অ্যাকাউন্ট ($registeredUserEmail) সফলভাবে তৈরি হয়েছে।"
                        else
                            "Your account ($registeredUserEmail) has been created.",
                        fontSize = 12.sp,
                        color = LscSlateDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isBangla) "আপনার লগইন পাসওয়ার্ড:" else "Your Login Password:",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = generatedPasswordForUser,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    color = LscEmeraldPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(android.content.ClipboardManager::class.java)
                                        clipboard?.setPrimaryClip(
                                            android.content.ClipData.newPlainText("LSC Password", generatedPasswordForUser)
                                        )
                                        Toast.makeText(
                                            context,
                                            if (isBangla) "পাসওয়ার্ড কপি করা হয়েছে!" else "Password copied!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Password",
                                        tint = LscEmeraldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignUpSuccessDialog = false
                        authMode = 0
                        email = registeredUserEmail
                        password = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    modifier = Modifier.testTag("signup_success_login_now_btn")
                ) {
                    Text(if (isBangla) "লগইন ফর্মে যান" else "Go to Login Form")
                }
            }
        )
    }

    if (showForgotPasswordDialog) {
        var isResetting by remember { mutableStateOf(false) }
        var resetResultMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            icon = {
                Icon(Icons.Default.LockReset, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(32.dp))
            },
            title = {
                Text(
                    text = if (isBangla) "ইমেলে পাসওয়ার্ড পাঠান / রিসেট" else "Send Password to Email",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isBangla)
                            "আপনার নিবন্ধিত ইমেইল প্রদান করুন। নতুন সিকিউরিটি পাসওয়ার্ড তৈরি করে আপনার ইমেলে পাঠানো হবে।"
                        else
                            "Enter your registered email. A new security password will be sent to your email.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        label = { Text(if (isBangla) "ইমেইল" else "Email") },
                        placeholder = { Text("user@lsc.org") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forgot_password_email_input")
                    )

                    if (resetResultMsg != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = resetResultMsg.orEmpty(),
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isResetting = true
                            val (success, newPass) = campViewModel.requestPasswordToEmail(forgotEmailInput)
                            isResetting = false
                            if (success) {
                                resetResultMsg = if (isBangla)
                                    "✅ নতুন পাসওয়ার্ড আপনার ইমেলে পাঠানো হয়েছে।"
                                else
                                    "✅ A new password has been dispatched to your email."

                                try {
                                    val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:${forgotEmailInput.trim()}")
                                        putExtra(Intent.EXTRA_SUBJECT, "LSC Camp 2026 - পাসওয়ার্ড রিসেট")
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "আপনার নতুন ক্যাম্প লগইন পাসওয়ার্ড: $newPass"
                                        )
                                    }
                                    context.startActivity(Intent.createChooser(mailIntent, "Send Email"))
                                } catch (_: Exception) {}

                                email = forgotEmailInput.trim().lowercase()
                                password = ""
                                showForgotPasswordDialog = false
                            } else {
                                Toast.makeText(context, newPass, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    enabled = !isResetting && forgotEmailInput.isNotBlank(),
                    modifier = Modifier.testTag("send_email_password_confirm_btn")
                ) {
                    Text(if (isBangla) "ইমেলে পাঠান" else "Send to Email")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text(if (isBangla) "বাতিল" else "Cancel")
                }
            }
        )
    }
}
