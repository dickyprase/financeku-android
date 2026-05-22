package com.financeku.app.ui.cashflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financeku.app.ui.components.*
import com.financeku.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    onNavigateBack: () -> Unit,
    viewModel: CashflowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = "Wallets",
            onBackClick = onNavigateBack,
            actions = {
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Wallet", tint = TextPrimary)
                }
            }
        )

        // Total balance
        val totalBalance = uiState.wallets.sumOf { it.balance }
        DarkCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(totalBalance),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (uiState.wallets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.AccountBalanceWallet,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No wallets yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.wallets) { wallet ->
                    var showDeleteDialog by remember { mutableStateOf(false) }

                    DarkCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    Icons.Filled.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = BlueAccent,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = wallet.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    if (wallet.isDefault) {
                                        Text(
                                            "Default wallet",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyanAccent
                                        )
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = formatCurrency(wallet.balance),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (wallet.balance >= 0) GreenIndicator else RedIndicator
                                )
                                IconButton(
                                    onClick = { showDeleteDialog = true },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        tint = RedIndicator,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete Wallet", color = TextPrimary) },
                            text = { Text("Are you sure you want to delete '${wallet.name}'? This cannot be undone.", color = TextSecondary) },
                            containerColor = DarkSurface,
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.deleteWallet(wallet.id)
                                    showDeleteDialog = false
                                }) {
                                    Text("Delete", color = RedIndicator)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel", color = TextSecondary)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateWalletDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, balance ->
                viewModel.createWallet(name, balance, null, null, false)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun CreateWalletDialog(
    onDismiss: () -> Unit,
    onCreate: (String, Double?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balance by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Wallet", color = TextPrimary) },
        containerColor = DarkSurface,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DarkTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Wallet Name",
                    placeholder = "Enter wallet name",
                    modifier = Modifier.fillMaxWidth()
                )
                DarkTextField(
                    value = balance,
                    onValueChange = { balance = it },
                    label = "Initial Balance (optional)",
                    placeholder = "0",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, balance.toDoubleOrNull()) },
                enabled = name.isNotBlank()
            ) {
                Text("Create", color = CyanAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
