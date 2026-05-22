package com.financeku.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registerState) {
        if (registerState is AuthUiState.Success) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.displayMedium,
            color = CyanAccent
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign up to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        DarkTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            placeholder = "Enter your name",
            leadingIcon = Icons.Filled.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        DarkTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "Enter your email",
            leadingIcon = Icons.Filled.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DarkTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Min. 6 characters",
            leadingIcon = Icons.Filled.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = TextSecondary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        DarkTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            placeholder = "Re-enter password",
            leadingIcon = Icons.Filled.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = confirmPassword.isNotEmpty() && confirmPassword != password,
            errorMessage = if (confirmPassword.isNotEmpty() && confirmPassword != password) "Passwords don't match" else null
        )

        if (registerState is AuthUiState.Error) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = (registerState as AuthUiState.Error).message,
                style = MaterialTheme.typography.bodySmall,
                color = RedIndicator
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Create Account",
            onClick = { viewModel.register(name, email, password, confirmPassword) },
            isLoading = registerState is AuthUiState.Loading,
            enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6 && password == confirmPassword
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            TextButton(onClick = onNavigateToLogin) {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyanAccent
                )
            }
        }
    }
}
