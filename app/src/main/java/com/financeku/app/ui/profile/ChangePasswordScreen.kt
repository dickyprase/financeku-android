package com.financeku.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeku.app.ui.components.*
import com.financeku.app.ui.theme.*

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.changePasswordState) {
        if (uiState.changePasswordState is ChangePasswordState.Success) {
            viewModel.resetChangePasswordState()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = "Change Password",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DarkCard(modifier = Modifier.fillMaxWidth()) {
                DarkTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Current Password",
                    placeholder = "Enter current password",
                    leadingIcon = Icons.Filled.Lock,
                    trailingIcon = {
                        IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                            Icon(
                                if (showCurrentPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "New Password",
                    placeholder = "Enter new password",
                    leadingIcon = Icons.Filled.LockOpen,
                    trailingIcon = {
                        IconButton(onClick = { showNewPassword = !showNewPassword }) {
                            Icon(
                                if (showNewPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm New Password",
                    placeholder = "Confirm new password",
                    leadingIcon = Icons.Filled.Lock,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = confirmPassword.isNotEmpty() && newPassword != confirmPassword,
                    errorMessage = if (confirmPassword.isNotEmpty() && newPassword != confirmPassword) "Passwords don't match" else null,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.changePasswordState is ChangePasswordState.Error) {
                    Text(
                        text = (uiState.changePasswordState as ChangePasswordState.Error).message,
                        style = MaterialTheme.typography.bodySmall,
                        color = RedIndicator,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                PrimaryButton(
                    text = "Change Password",
                    onClick = {
                        viewModel.changePassword(currentPassword, newPassword, confirmPassword)
                    },
                    enabled = uiState.changePasswordState !is ChangePasswordState.Loading &&
                            currentPassword.isNotBlank() &&
                            newPassword.isNotBlank() &&
                            newPassword == confirmPassword,
                    isLoading = uiState.changePasswordState is ChangePasswordState.Loading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
