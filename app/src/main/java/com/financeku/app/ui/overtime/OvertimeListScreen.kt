package com.financeku.app.ui.overtime

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
fun OvertimeListScreen(
    onNavigateToForm: (String?) -> Unit,
    viewModel: OvertimeViewModel = hiltViewModel()
) {
    val uiState by viewModel.listState.collectAsState()

    var showDisburseDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = "Overtime",
            actions = {
                IconButton(onClick = { onNavigateToForm(null) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Overtime", tint = TextPrimary)
                }
            }
        )

        // Calculation Summary
        uiState.calculation?.let { calc ->
            DarkCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Period Summary",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Hours", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text("${calc.totalHours}h", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Amount", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text(formatCurrency(calc.totalAmount), style = MaterialTheme.typography.titleMedium, color = OrangeIndicator, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                PrimaryButton(
                    text = "Disburse",
                    onClick = { showDisburseDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        when {
            uiState.isLoading -> LoadingIndicator()
            uiState.error != null -> ErrorMessage(
                message = uiState.error!!,
                onRetry = { viewModel.loadOvertimes() }
            )
            uiState.overtimes.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = TextTertiary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No overtime records",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.overtimes) { overtime ->
                        OvertimeItem(
                            overtime = overtime,
                            onEdit = { onNavigateToForm(overtime.id) },
                            onDelete = { viewModel.deleteOvertime(overtime.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDisburseDialog) {
        AlertDialog(
            onDismissRequest = { showDisburseDialog = false },
            title = { Text("Disburse Overtime", color = TextPrimary) },
            text = { Text("Are you sure you want to disburse this overtime period?", color = TextSecondary) },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = {
                    uiState.calculation?.let {
                        viewModel.disburseOvertime(it.periodStart, it.periodEnd, null)
                    }
                    showDisburseDialog = false
                }) {
                    Text("Confirm", color = CyanAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisburseDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun OvertimeItem(
    overtime: com.financeku.app.domain.model.Overtime,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    DarkCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = overtime.date,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${overtime.hours}h @ ${formatCurrency(overtime.rate)}/h",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                if (!overtime.description.isNullOrEmpty()) {
                    Text(
                        text = overtime.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(overtime.amount),
                    style = MaterialTheme.typography.titleMedium,
                    color = OrangeIndicator,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(overtime.status, style = MaterialTheme.typography.labelSmall) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (overtime.status) {
                            "pending" -> OrangeIndicator.copy(alpha = 0.1f)
                            "disbursed" -> GreenIndicator.copy(alpha = 0.1f)
                            else -> DarkSurfaceVariant
                        }
                    )
                )
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = TextSecondary)
                    }
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = RedIndicator, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Overtime", color = TextPrimary) },
            text = { Text("Are you sure you want to delete this overtime record?", color = TextSecondary) },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
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

private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount)
}
