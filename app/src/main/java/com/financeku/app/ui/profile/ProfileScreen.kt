package com.financeku.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeku.app.ui.components.*
import com.financeku.app.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToChangePassword: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Header Card
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.name.take(1).uppercase(),
                        style = MaterialTheme.typography.displayMedium,
                        color = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Role badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = CyanAccent.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = uiState.role.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanAccent,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.AccountBalanceWallet,
                iconColor = BlueAccent,
                value = "${uiState.walletCount}",
                label = "Wallets"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Receipt,
                iconColor = GreenIndicator,
                value = "${uiState.transactionCount}",
                label = "Transactions"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.AccessTime,
                iconColor = OrangeIndicator,
                value = "${uiState.overtimeCount}",
                label = "Overtime"
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Account Section
        SectionHeader(title = "Account")
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column {
                MenuListItem(
                    icon = Icons.Filled.Person,
                    iconColor = BlueAccent,
                    title = "Edit Profile",
                    subtitle = "Name, salary, meal allowance",
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    onClick = { /* navigate to edit profile */ }
                )
                HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
                MenuListItem(
                    icon = Icons.Filled.Lock,
                    iconColor = PurpleIndicator,
                    title = "Change Password",
                    subtitle = "Update your password",
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    onClick = onNavigateToChangePassword
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Preferences Section
        SectionHeader(title = "Preferences")
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column {
                MenuListItem(
                    icon = Icons.Filled.DarkMode,
                    iconColor = YellowIndicator,
                    title = "Dark Mode",
                    subtitle = "Currently active",
                    trailingIcon = null,
                    onClick = { /* toggle theme */ }
                )
                HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
                MenuListItem(
                    icon = Icons.Filled.Notifications,
                    iconColor = CyanAccent,
                    title = "Notifications",
                    subtitle = "Manage alerts",
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Section
        SectionHeader(title = "About")
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column {
                MenuListItem(
                    icon = Icons.Filled.Info,
                    iconColor = TextSecondary,
                    title = "App Version",
                    subtitle = "1.0.0",
                    trailingIcon = null,
                    onClick = { }
                )
                HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)
                MenuListItem(
                    icon = Icons.Filled.Description,
                    iconColor = TextSecondary,
                    title = "Terms & Privacy",
                    trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        SecondaryButton(
            text = "Logout",
            onClick = {
                viewModel.logout()
                onLogout()
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
