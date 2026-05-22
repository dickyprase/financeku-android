package com.financeku.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    // Auth
    data object Login : Screen("login", "Login")
    data object Register : Screen("register", "Register")

    // Main tabs
    data object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home)
    data object Overtime : Screen("overtime", "Overtime", Icons.Filled.AccessTime)
    data object Cashflow : Screen("cashflow", "Cashflow", Icons.Filled.AccountBalanceWallet)
    data object Goals : Screen("goals", "Goals", Icons.Filled.Flag)
    data object Profile : Screen("profile", "Profile", Icons.Filled.Person)

    // Sub screens
    data object OvertimeForm : Screen("overtime/form?id={id}", "Overtime Form") {
        fun createRoute(id: String? = null) = "overtime/form?id=${id ?: ""}"
    }
    data object TransactionList : Screen("transactions", "Transactions")
    data object TransactionForm : Screen("transactions/form?id={id}", "Transaction Form") {
        fun createRoute(id: String? = null) = "transactions/form?id=${id ?: ""}"
    }
    data object WalletList : Screen("wallets", "Wallets")
    data object Transfer : Screen("transfer", "Transfer")
    data object Reports : Screen("reports", "Reports")
    data object ChangePassword : Screen("change-password", "Change Password")

    companion object {
        val bottomNavItems = listOf(Dashboard, Overtime, Cashflow, Goals, Profile)
    }
}
