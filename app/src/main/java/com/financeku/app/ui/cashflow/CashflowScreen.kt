package com.financeku.app.ui.cashflow

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financeku.app.ui.components.*
import com.financeku.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashflowScreen(
    onNavigateToTransactions: () -> Unit,
    onNavigateToWallets: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToTransactionForm: (String?) -> Unit,
    viewModel: CashflowViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = "Cashflow",
            actions = {
                IconButton(onClick = { onNavigateToTransactionForm(null) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Transaction", tint = TextPrimary)
                }
            }
        )

        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorMessage(
                message = uiState.error!!,
                onRetry = { viewModel.loadAll() }
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Actions
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            QuickActionCard(
                                icon = Icons.Filled.AccountBalanceWallet,
                                label = "Wallets",
                                color = BlueAccent,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToWallets
                            )
                            QuickActionCard(
                                icon = Icons.Filled.SwapHoriz,
                                label = "Transfer",
                                color = PurpleIndicator,
                                modifier = Modifier.weight(1f),
                                onClick = onNavigateToTransfer
                            )
                        }
                    }

                    // Wallets Summary
                    item {
                        SectionHeader(
                            title = "Wallets",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (uiState.wallets.isEmpty()) {
                        item {
                            DarkCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "No wallets yet. Create one to get started.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        items(uiState.wallets) { wallet ->
                            DarkCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = BlueAccent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = wallet.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                            if (wallet.isDefault) {
                                                Text(
                                                    "Default",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = CyanAccent
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = formatCurrency(wallet.balance),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (wallet.balance >= 0) GreenIndicator else RedIndicator
                                    )
                                }
                            }
                        }
                    }

                    // Recent Transactions
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                            TextButton(onClick = onNavigateToTransactions) {
                                Text("See All", color = CyanAccent)
                            }
                        }
                    }

                    if (uiState.transactions.isEmpty()) {
                        item {
                            DarkCard(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "No transactions yet.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        items(uiState.transactions.take(10)) { transaction ->
                            DarkCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onNavigateToTransactionForm(transaction.id) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            if (transaction.type == "income") Icons.Filled.ArrowDownward
                                            else Icons.Filled.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (transaction.type == "income") GreenIndicator else RedIndicator,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = transaction.description ?: transaction.categoryName ?: "Transaction",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextPrimary,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${transaction.walletName ?: ""} • ${transaction.date}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextTertiary
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${if (transaction.type == "expense") "-" else "+"}${formatCurrency(transaction.amount)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = if (transaction.type == "expense") RedIndicator else GreenIndicator,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    DarkCard(
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
        }
    }
}

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
