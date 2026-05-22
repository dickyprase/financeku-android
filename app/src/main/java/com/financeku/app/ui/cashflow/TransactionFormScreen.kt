package com.financeku.app.ui.cashflow

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.financeku.app.ui.components.*
import com.financeku.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    transactionId: String?,
    onNavigateBack: () -> Unit,
    viewModel: CashflowViewModel = hiltViewModel()
) {
    val formState by viewModel.transactionFormState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedWalletId by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }
    var walletExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    val isEditing = transactionId != null

    LaunchedEffect(formState.success) {
        if (formState.success) {
            viewModel.resetTransactionFormState()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = if (isEditing) "Edit Transaction" else "Add Transaction",
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
                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == "expense",
                        onClick = { type = "expense" },
                        label = { Text("Expense") },
                        leadingIcon = if (type == "expense") {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedIndicator.copy(alpha = 0.2f),
                            selectedLabelColor = RedIndicator,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "income",
                        onClick = { type = "income" },
                        label = { Text("Income") },
                        leadingIcon = if (type == "income") {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenIndicator.copy(alpha = 0.2f),
                            selectedLabelColor = GreenIndicator,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = "Amount",
                    placeholder = "0",
                    leadingIcon = Icons.Filled.AttachMoney,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = "Date",
                    placeholder = "YYYY-MM-DD",
                    leadingIcon = Icons.Filled.CalendarToday,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wallet dropdown
                ExposedDropdownMenuBox(
                    expanded = walletExpanded,
                    onExpandedChange = { walletExpanded = it }
                ) {
                    DarkTextField(
                        value = uiState.wallets.find { it.id == selectedWalletId }?.name ?: "",
                        onValueChange = {},
                        label = "Wallet",
                        placeholder = "Select wallet",
                        leadingIcon = Icons.Filled.AccountBalanceWallet,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = walletExpanded,
                        onDismissRequest = { walletExpanded = false }
                    ) {
                        uiState.wallets.forEach { wallet ->
                            DropdownMenuItem(
                                text = { Text(wallet.name) },
                                onClick = {
                                    selectedWalletId = wallet.id
                                    walletExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Category dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    DarkTextField(
                        value = uiState.categories.find { it.id == selectedCategoryId }?.name ?: "",
                        onValueChange = {},
                        label = "Category",
                        placeholder = "Select category",
                        leadingIcon = Icons.Filled.Category,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        uiState.categories
                            .filter { it.type == type }
                            .forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        categoryExpanded = false
                                    }
                                )
                            }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description (Optional)",
                    placeholder = "Add a note",
                    leadingIcon = Icons.Filled.Description,
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (formState.error != null) {
                    Text(
                        text = formState.error!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = RedIndicator,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                PrimaryButton(
                    text = if (isEditing) "Update" else "Save",
                    onClick = {
                        val amountVal = amount.toDoubleOrNull() ?: return@PrimaryButton
                        if (isEditing) {
                            viewModel.updateTransaction(
                                transactionId!!, amountVal, type,
                                description.ifBlank { null }, date,
                                selectedWalletId, selectedCategoryId.ifBlank { null }
                            )
                        } else {
                            viewModel.createTransaction(
                                amountVal, type,
                                description.ifBlank { null }, date,
                                selectedWalletId, selectedCategoryId.ifBlank { null }
                            )
                        }
                    },
                    enabled = !formState.isLoading && amount.isNotBlank() && date.isNotBlank() && selectedWalletId.isNotBlank(),
                    isLoading = formState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
