package com.financeku.app.ui.overtime

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
fun OvertimeFormScreen(
    overtimeId: String?,
    onNavigateBack: () -> Unit,
    viewModel: OvertimeViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsState()
    var date by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf("") }
    var rate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isEditing = overtimeId != null

    LaunchedEffect(formState.success) {
        if (formState.success) {
            viewModel.resetFormState()
            onNavigateBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        DarkTopBar(
            title = if (isEditing) "Edit Overtime" else "Add Overtime",
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
                    value = date,
                    onValueChange = { date = it },
                    label = "Date",
                    placeholder = "YYYY-MM-DD",
                    leadingIcon = Icons.Filled.CalendarToday,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = hours,
                    onValueChange = { hours = it },
                    label = "Hours",
                    placeholder = "e.g. 2.5",
                    leadingIcon = Icons.Filled.AccessTime,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = rate,
                    onValueChange = { rate = it },
                    label = "Rate per Hour",
                    placeholder = "e.g. 50000",
                    leadingIcon = Icons.Filled.AttachMoney,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                DarkTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description (Optional)",
                    placeholder = "What did you work on?",
                    leadingIcon = Icons.Filled.Description,
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                // Preview calculation
                val hoursVal = hours.toDoubleOrNull()
                val rateVal = rate.toDoubleOrNull()
                if (hoursVal != null && rateVal != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DarkCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = DarkSurfaceVariant
                    ) {
                        Text(
                            text = "Preview",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Amount: Rp ${String.format("%,.0f", hoursVal * rateVal)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = OrangeIndicator
                        )
                    }
                }

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
                    text = if (isEditing) "Update Overtime" else "Save Overtime",
                    onClick = {
                        val h = hours.toDoubleOrNull() ?: return@PrimaryButton
                        val r = rate.toDoubleOrNull() ?: return@PrimaryButton
                        if (isEditing) {
                            viewModel.updateOvertime(overtimeId!!, date, h, r, description.ifBlank { null })
                        } else {
                            viewModel.createOvertime(date, h, r, description.ifBlank { null })
                        }
                    },
                    enabled = !formState.isLoading && date.isNotBlank() && hours.isNotBlank() && rate.isNotBlank(),
                    isLoading = formState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
