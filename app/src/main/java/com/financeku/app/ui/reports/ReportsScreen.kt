package com.financeku.app.ui.reports

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
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = "Reports",
            onBackClick = onNavigateBack
        )

        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorMessage(
                message = uiState.error!!,
                onRetry = { viewModel.loadReport() }
            )
            else -> {
                val report = uiState.report

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary Cards
                    item {
                        SectionHeader(title = "Monthly Summary")
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DarkCard(modifier = Modifier.weight(1f)) {
                                Icon(
                                    Icons.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = GreenIndicator,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Income",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = formatCurrency(report?.totalIncome ?: 0.0),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = GreenIndicator,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DarkCard(modifier = Modifier.weight(1f)) {
                                Icon(
                                    Icons.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = RedIndicator,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Expense",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = formatCurrency(report?.totalExpense ?: 0.0),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = RedIndicator,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Net Cashflow
                    item {
                        DarkCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Net Cashflow",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val netCashflow = report?.netCashflow ?: 0.0
                                    Text(
                                        text = "${if (netCashflow >= 0) "+" else ""}${formatCurrency(netCashflow)}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = if (netCashflow >= 0) GreenIndicator else RedIndicator,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    if ((report?.netCashflow ?: 0.0) >= 0) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                                    contentDescription = null,
                                    tint = if ((report?.netCashflow ?: 0.0) >= 0) GreenIndicator else RedIndicator,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    // By Category
                    if (!report?.byCategory.isNullOrEmpty()) {
                        item {
                            SectionHeader(
                                title = "By Category",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(report!!.byCategory) { category ->
                            DarkCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = category.categoryName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { (category.percentage / 100.0).toFloat().coerceIn(0f, 1f) },
                                            modifier = Modifier
                                                .fillMaxWidth(0.7f)
                                                .height(6.dp),
                                            color = CyanAccent,
                                            trackColor = DarkSurfaceVariant
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = formatCurrency(category.amount),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${String.format("%.1f", category.percentage)}%",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary
                                        )
                                    }
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

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
