package com.financeku.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.financeku.app.ui.components.*
import com.financeku.app.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateToReports: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "FinanceKu",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Text(
                    text = "Your financial overview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            IconButton(onClick = onNavigateToReports) {
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = "Reports",
                    tint = CyanAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Total Balance Card
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
        ) {
            Column {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatCurrency(uiState.totalBalance),
                    style = MaterialTheme.typography.displayLarge,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Grid (3 columns)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.TrendingUp,
                iconColor = GreenIndicator,
                value = formatCompact(uiState.monthlyIncome),
                label = "Income"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.TrendingDown,
                iconColor = RedIndicator,
                value = formatCompact(uiState.monthlyExpense),
                label = "Expense"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.AccessTime,
                iconColor = YellowIndicator,
                value = formatCompact(uiState.overtimePending),
                label = "Overtime"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Monthly Progress
        DarkCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 20.dp
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Monthly Budget",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = "${uiState.budgetPercentage.toInt()}%",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (uiState.budgetPercentage > 80) RedIndicator else CyanAccent
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (uiState.budgetPercentage.toFloat() / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (uiState.budgetPercentage > 80) RedIndicator else CyanAccent,
                    trackColor = DarkSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spent: ${formatCurrency(uiState.monthlyExpense)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Remaining: ${formatCurrency(uiState.monthlyIncome - uiState.monthlyExpense)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Actions
        SectionHeader(title = "Quick Actions")
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Add,
                label = "Transaction",
                color = BlueAccent
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.AccessTime,
                label = "Overtime",
                color = OrangeIndicator
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.SwapHoriz,
                label = "Transfer",
                color = PurpleIndicator
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    DarkCard(
        modifier = modifier,
        cornerRadius = 16.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return "Rp ${String.format("%,.0f", amount)}"
}

private fun formatCompact(amount: Double): String {
    return when {
        amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
        amount >= 1_000 -> String.format("%.0fK", amount / 1_000)
        else -> String.format("%.0f", amount)
    }
}
