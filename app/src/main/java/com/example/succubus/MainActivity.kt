package com.example.succubus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.succubus.data.AppRepository
import com.example.succubus.model.TabStatus
import com.example.succubus.ui.screens.InventoryScreen
import com.example.succubus.ui.screens.PosScreen
import com.example.succubus.ui.screens.RecipesScreen
import com.example.succubus.ui.screens.SettingsScreen
import com.example.succubus.ui.screens.TabsAndCreditScreen
import com.example.succubus.ui.theme.BarBlack
import com.example.succubus.ui.theme.BarCardElevated
import com.example.succubus.ui.theme.BarDarkSurface
import com.example.succubus.ui.theme.SpeakeasyGold
import com.example.succubus.ui.theme.SpeakeasyGoldLight
import com.example.succubus.ui.theme.StatusWarning
import com.example.succubus.ui.theme.SuccubusRed
import com.example.succubus.ui.theme.SuccubusTheme
import com.example.succubus.ui.theme.TextIvory
import com.example.succubus.ui.theme.TextMuted

enum class AppNavDestination(val title: String, val icon: ImageVector) {
    POS("POS Order", Icons.Default.PointOfSale),
    TABS("Tabs & Credit", Icons.Default.AccountBalanceWallet),
    INVENTORY("Inventory", Icons.Default.Inventory2),
    RECIPES("Recipes", Icons.Default.MenuBook),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = AppRepository(applicationContext)

        setContent {
            SuccubusTheme {
                SuccubusMainApp(repository = repository)
            }
        }
    }
}

@Composable
fun SuccubusMainApp(repository: AppRepository) {
    var currentDestination by remember { mutableStateOf(AppNavDestination.TABS) }

    val products by repository.products.collectAsState()
    val tabs by repository.tabs.collectAsState()

    val openTabsCount = tabs.count { it.status == TabStatus.OPEN }
    val lowStockCount = products.count { it.isLowStock || it.isOutOfStock }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(BarBlack),
        bottomBar = {
            NavigationBar(
                containerColor = BarDarkSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                AppNavDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            when (destination) {
                                AppNavDestination.TABS -> {
                                    if (openTabsCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = SuccubusRed) {
                                                    Text("$openTabsCount", color = TextIvory, fontSize = 9.sp)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                destination.icon,
                                                contentDescription = destination.title,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.title,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                AppNavDestination.INVENTORY -> {
                                    if (lowStockCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = StatusWarning) {
                                                    Text("$lowStockCount", color = BarBlack, fontSize = 9.sp)
                                                }
                                            }
                                        ) {
                                            Icon(
                                                destination.icon,
                                                contentDescription = destination.title,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            destination.icon,
                                            contentDescription = destination.title,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                else -> {
                                    Icon(
                                        destination.icon,
                                        contentDescription = destination.title,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SpeakeasyGoldLight,
                            selectedTextColor = SpeakeasyGoldLight,
                            indicatorColor = BarCardElevated,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                AppNavDestination.POS -> PosScreen(repository = repository)
                AppNavDestination.TABS -> TabsAndCreditScreen(repository = repository)
                AppNavDestination.INVENTORY -> InventoryScreen(repository = repository)
                AppNavDestination.RECIPES -> RecipesScreen(repository = repository)
                AppNavDestination.SETTINGS -> SettingsScreen(repository = repository)
            }
        }
    }
}