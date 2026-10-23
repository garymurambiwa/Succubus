package com.example.succubus.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.succubus.data.AppRepository
import com.example.succubus.data.SampleData
import com.example.succubus.model.BarTab
import com.example.succubus.model.PaymentRecord
import com.example.succubus.model.PaymentType
import com.example.succubus.model.Product
import com.example.succubus.model.TabStatus
import com.example.succubus.ui.components.DevilHeartCanvas
import com.example.succubus.ui.theme.BarBlack
import com.example.succubus.ui.theme.BarBorder
import com.example.succubus.ui.theme.BarCardBg
import com.example.succubus.ui.theme.BarCardElevated
import com.example.succubus.ui.theme.BarDarkSurface
import com.example.succubus.ui.theme.SpeakeasyGold
import com.example.succubus.ui.theme.SpeakeasyGoldLight
import com.example.succubus.ui.theme.StatusDanger
import com.example.succubus.ui.theme.StatusSuccess
import com.example.succubus.ui.theme.StatusWarning
import com.example.succubus.ui.theme.SuccubusRed
import com.example.succubus.ui.theme.TextIvory
import com.example.succubus.ui.theme.TextMuted
import com.example.succubus.ui.theme.TextSubtle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsAndCreditScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val tabs by repository.tabs.collectAsState()
    val products by repository.products.collectAsState()
    val settings by repository.settings.collectAsState()
    val ledger by repository.paymentLedger.collectAsState()

    var selectedTabFilterIndex by remember { mutableStateOf(0) } // 0: Active Tabs, 1: Debtors/Credit, 2: History/Ledger
    var searchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showOpenTabDialog by remember { mutableStateOf(false) }
    var tabToSettleCredit by remember { mutableStateOf<BarTab?>(null) }
    var tabToAddAdvance by remember { mutableStateOf<BarTab?>(null) }
    var tabToAddItem by remember { mutableStateOf<BarTab?>(null) }
    var tabForReceipt by remember { mutableStateOf<BarTab?>(null) }

    val activeTabs = tabs.filter { it.status == TabStatus.OPEN }
    val totalAdvancesHeld = activeTabs.sumOf { it.advancePaid }
    val totalActiveTabsBill = activeTabs.sumOf { it.calculateTotal(settings.taxRatePercent, settings.serviceChargePercent) }
    val totalUnsettledDebt = activeTabs.sumOf { it.amountOwed(settings.taxRatePercent, settings.serviceChargePercent) }
    val totalUnusedCredit = activeTabs.sumOf { it.remainingAdvance(settings.taxRatePercent, settings.serviceChargePercent) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BarBlack)
            .padding(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CUSTOMER TABS & CREDIT",
                    color = SpeakeasyGoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${activeTabs.size} Open Accounts • Advance Deposits & Clear Credit",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showOpenTabDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccubusRed,
                    contentColor = TextIvory
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Open Tab with Advance", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Financial KPI Summary Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(BarCardBg)
                .border(1.dp, BarBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("ADVANCES HELD", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${settings.currencySymbol}${String.format("%.2f", totalAdvancesHeld)}",
                    color = SpeakeasyGoldLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Unused: ${settings.currencySymbol}${String.format("%.2f", totalUnusedCredit)}", color = StatusSuccess, fontSize = 10.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("RUNNING ORDERS", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${settings.currencySymbol}${String.format("%.2f", totalActiveTabsBill)}",
                    color = TextIvory,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Incl. Tax & Service", color = TextMuted, fontSize = 10.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("CREDIT OWED", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${settings.currencySymbol}${String.format("%.2f", totalUnsettledDebt)}",
                    color = if (totalUnsettledDebt > 0) StatusDanger else StatusSuccess,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Owed by guests", color = TextMuted, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sub-tabs: Active Tabs, Customer Credit Ledger, All Transactions
        val tabTitles = listOf("Active Tabs (${activeTabs.size})", "Customer Credit Ledger", "Payment Ledger")
        TabRow(
            selectedTabIndex = selectedTabFilterIndex,
            containerColor = BarDarkSurface,
            contentColor = SpeakeasyGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabFilterIndex]),
                    color = SuccubusRed,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabFilterIndex == index,
                    onClick = { selectedTabFilterIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabFilterIndex == index) SpeakeasyGoldLight else TextMuted
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by customer name, table, phone...", color = TextSubtle, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SpeakeasyGold, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BarDarkSurface,
                unfocusedContainerColor = BarDarkSurface,
                focusedBorderColor = SpeakeasyGold,
                unfocusedBorderColor = BarBorder,
                focusedTextColor = TextIvory,
                unfocusedTextColor = TextIvory
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Content based on sub-tab
        when (selectedTabFilterIndex) {
            0 -> {
                // ACTIVE TABS LIST
                val filteredTabs = activeTabs.filter { tab ->
                    searchQuery.isBlank() ||
                            tab.customerName.contains(searchQuery, ignoreCase = true) ||
                            tab.tableName.contains(searchQuery, ignoreCase = true) ||
                            tab.customerPhone.contains(searchQuery, ignoreCase = true)
                }

                if (filteredTabs.isEmpty()) {
                    EmptyTabPlaceholder(
                        title = "No open customer tabs",
                        subtitle = "Tap 'Open Tab with Advance' to create a customer tab with pre-authorized deposit."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTabs, key = { it.id }) { tab ->
                            BarTabCard(
                                tab = tab,
                                currency = settings.currencySymbol,
                                taxRate = settings.taxRatePercent,
                                serviceRate = settings.serviceChargePercent,
                                onAddItems = { tabToAddItem = tab },
                                onAddAdvance = { tabToAddAdvance = tab },
                                onClearCredit = { tabToSettleCredit = tab },
                                onViewReceipt = { tabForReceipt = tab }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }

            1 -> {
                // CUSTOMER CREDIT LEDGER (Debtors & Credits Overview)
                CustomerCreditLedgerView(
                    tabs = tabs,
                    searchQuery = searchQuery,
                    currency = settings.currencySymbol,
                    taxRate = settings.taxRatePercent,
                    serviceRate = settings.serviceChargePercent,
                    onClearCredit = { tabToSettleCredit = it },
                    onViewReceipt = { tabForReceipt = it }
                )
            }

            2 -> {
                // PAYMENT & SETTLEMENT HISTORY
                PaymentLedgerHistoryView(
                    ledger = ledger,
                    searchQuery = searchQuery,
                    currency = settings.currencySymbol
                )
            }
        }
    }

    // --- DIALOG: OPEN TAB WITH ADVANCE ---
    if (showOpenTabDialog) {
        OpenTabWithAdvanceDialog(
            currency = settings.currencySymbol,
            defaultAdvance = settings.defaultAdvanceDeposit,
            onDismiss = { showOpenTabDialog = false },
            onOpen = { name, phone, table, advance, method, notes ->
                repository.openTab(
                    customerName = name,
                    customerPhone = phone,
                    tableName = table,
                    advanceAmount = advance,
                    advanceMethod = method,
                    notes = notes
                )
                showOpenTabDialog = false
            }
        )
    }

    // --- DIALOG: CLEAR CUSTOMER CREDIT / SETTLE TAB ---
    if (tabToSettleCredit != null) {
        val tab = tabToSettleCredit!!
        ClearCreditDialog(
            tab = tab,
            currency = settings.currencySymbol,
            taxRate = settings.taxRatePercent,
            serviceRate = settings.serviceChargePercent,
            onDismiss = { tabToSettleCredit = null },
            onConfirmSettlement = { method, notes ->
                repository.clearCustomerCredit(tab.id, method, notes)
                tabToSettleCredit = null
            }
        )
    }

    // --- DIALOG: ADD ADDITIONAL ADVANCE DEPOSIT ---
    if (tabToAddAdvance != null) {
        val tab = tabToAddAdvance!!
        AddAdvanceDialog(
            tab = tab,
            currency = settings.currencySymbol,
            onDismiss = { tabToAddAdvance = null },
            onConfirmAdvance = { amount, method, notes ->
                repository.addAdvanceToTab(tab.id, amount, method, notes)
                tabToAddAdvance = null
            }
        )
    }

    // --- DIALOG: ADD ITEMS / DRINKS TO ACTIVE TAB ---
    if (tabToAddItem != null) {
        val tab = tabToAddItem!!
        AddItemToTabDialog(
            tab = tab,
            products = products,
            currency = settings.currencySymbol,
            onDismiss = { tabToAddItem = null },
            onAddItem = { product, qty, notes ->
                repository.addItemToTab(tab.id, product, qty, notes)
            }
        )
    }

    // --- DIALOG: VIEW DIGITAL BILL / RECEIPT ---
    if (tabForReceipt != null) {
        val tab = tabForReceipt!!
        ReceiptBillDialog(
            tab = tab,
            settings = settings,
            onDismiss = { tabForReceipt = null }
        )
    }
}

@Composable
fun BarTabCard(
    tab: BarTab,
    currency: String,
    taxRate: Double,
    serviceRate: Double,
    onAddItems: () -> Unit,
    onAddAdvance: () -> Unit,
    onClearCredit: () -> Unit,
    onViewReceipt: () -> Unit
) {
    val totalBill = tab.calculateTotal(taxRate, serviceRate)
    val balance = tab.calculateBalance(taxRate, serviceRate)
    val isCreditOwed = balance > 0
    val isAdvanceRemaining = balance < 0
    val timeFormatted = remember(tab.openedAt) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(tab.openedAt))
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = BarCardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.2.dp,
            if (isCreditOwed) StatusDanger.copy(alpha = 0.5f) else SpeakeasyGold.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Table and Customer info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF261D2B))
                            .border(0.8.dp, SpeakeasyGold.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tab.tableName.uppercase(),
                            color = SpeakeasyGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Opened $timeFormatted",
                        color = TextSubtle,
                        fontSize = 11.sp
                    )
                }

                // Status tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (tab.status == TabStatus.OPEN) Color(0xFF1E382B) else Color(0xFF26202A))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = tab.status.name,
                        color = if (tab.status == TabStatus.OPEN) StatusSuccess else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer Name & Phone
            Text(
                text = tab.customerName,
                color = TextIvory,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            if (tab.customerPhone.isNotBlank() || tab.notes.isNotBlank()) {
                val detail = listOfNotNull(
                    tab.customerPhone.ifBlank { null },
                    tab.notes.ifBlank { null }
                ).joinToString(" • ")
                Text(
                    text = detail,
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Items on this tab summary
            if (tab.items.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BarDarkSurface)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    tab.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.name}",
                                color = TextIvory,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$currency${String.format("%.2f", item.totalPrice)}",
                                color = SpeakeasyGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BarDarkSurface)
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No drinks ordered yet on this tab", color = TextSubtle, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial breakdown bar: Advance vs Total Bill vs Net Balance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF221B27))
                    .border(1.dp, BarBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ADVANCE PAID", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "$currency${String.format("%.2f", tab.advancePaid)}",
                        color = SpeakeasyGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("via ${tab.advanceMethod}", color = TextSubtle, fontSize = 9.sp)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("BILL TOTAL", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "$currency${String.format("%.2f", totalBill)}",
                        color = TextIvory,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("${tab.items.sumOf { it.quantity }} items", color = TextSubtle, fontSize = 9.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (isCreditOwed) {
                        Text("OUTSTANDING OWED", color = StatusDanger, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "$currency${String.format("%.2f", balance)}",
                            color = StatusDanger,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text("Exceeds advance", color = StatusDanger.copy(alpha = 0.8f), fontSize = 9.sp)
                    } else if (isAdvanceRemaining) {
                        Text("ADVANCE CREDIT", color = StatusSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "$currency${String.format("%.2f", -balance)}",
                            color = StatusSuccess,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text("Remaining credit", color = StatusSuccess.copy(alpha = 0.8f), fontSize = 9.sp)
                    } else {
                        Text("BALANCE", color = SpeakeasyGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("$currency 0.00", color = SpeakeasyGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Even", color = TextSubtle, fontSize = 9.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add Drink / Item button
                OutlinedButton(
                    onClick = onAddItems,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SpeakeasyGoldLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SpeakeasyGold.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.LocalBar, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Drink", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // Add Advance button
                OutlinedButton(
                    onClick = onAddAdvance,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextIvory),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BarBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Advance", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // Clear Credit / Settle Balance
                Button(
                    onClick = onClearCredit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCreditOwed) SuccubusRed else SpeakeasyGold
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text(
                        text = if (isCreditOwed) "Clear Credit" else "Settle Tab",
                        color = if (isCreditOwed) TextIvory else BarBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Digital Receipt
                IconButton(
                    onClick = onViewReceipt,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BarDarkSurface)
                        .border(1.dp, BarBorder, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = "Receipt", tint = SpeakeasyGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CustomerCreditLedgerView(
    tabs: List<BarTab>,
    searchQuery: String,
    currency: String,
    taxRate: Double,
    serviceRate: Double,
    onClearCredit: (BarTab) -> Unit,
    onViewReceipt: (BarTab) -> Unit
) {
    val filteredTabs = tabs.filter { tab ->
        searchQuery.isBlank() ||
                tab.customerName.contains(searchQuery, ignoreCase = true) ||
                tab.tableName.contains(searchQuery, ignoreCase = true)
    }

    if (filteredTabs.isEmpty()) {
        EmptyTabPlaceholder("No credit records found", "Customer credits and tabs will be logged here.")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredTabs) { tab ->
                val balance = tab.calculateBalance(taxRate, serviceRate)
                val isDebtor = balance > 0
                val hasCredit = balance < 0

                Card(
                    colors = CardDefaults.cardColors(containerColor = BarCardBg),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDebtor) StatusDanger.copy(alpha = 0.5f) else BarBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tab.customerName,
                                    color = TextIvory,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${tab.tableName})",
                                    color = SpeakeasyGoldLight,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "Advance: $currency${String.format("%.2f", tab.advancePaid)} • Total: $currency${String.format("%.2f", tab.calculateTotal(taxRate, serviceRate))}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (tab.status == TabStatus.CLOSED) {
                                Text("SETTLED", color = TextSubtle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else if (isDebtor) {
                                Text("OWES BALANCE", color = StatusDanger, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "$currency${String.format("%.2f", balance)}",
                                    color = StatusDanger,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { onClearCredit(tab) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Clear Credit", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else if (hasCredit) {
                                Text("REMAINING ADVANCE", color = StatusSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "$currency${String.format("%.2f", -balance)}",
                                    color = StatusSuccess,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = { onClearCredit(tab) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SpeakeasyGold),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Text("Refund/Close", color = BarBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text("BALANCED ($currency 0.00)", color = SpeakeasyGold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun PaymentLedgerHistoryView(
    ledger: List<PaymentRecord>,
    searchQuery: String,
    currency: String
) {
    val filtered = ledger.filter {
        searchQuery.isBlank() ||
                it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.method.contains(searchQuery, ignoreCase = true) ||
                it.notes.contains(searchQuery, ignoreCase = true)
    }

    if (filtered.isEmpty()) {
        EmptyTabPlaceholder("No transactions recorded yet", "All advance deposits and credit clearances will show here.")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filtered) { record ->
                val timeStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(record.timestamp))
                val isPositive = record.type == PaymentType.ADVANCE_DEPOSIT ||
                        record.type == PaymentType.ADDITIONAL_ADVANCE ||
                        record.type == PaymentType.CREDIT_CLEARANCE

                Card(
                    colors = CardDefaults.cardColors(containerColor = BarCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BarBorder),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPositive) Color(0xFF1E382B) else Color(0xFF381E23)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (isPositive) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (isPositive) StatusSuccess else StatusDanger,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = record.customerName,
                                    color = TextIvory,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${record.type.name.replace('_', ' ')} • ${record.method} • $timeStr",
                                    color = TextMuted,
                                    fontSize = 10.5.sp
                                )
                                if (record.notes.isNotBlank()) {
                                    Text(
                                        text = record.notes,
                                        color = TextSubtle,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Text(
                            text = (if (isPositive) "+" else "-") + "$currency${String.format("%.2f", record.amount)}",
                            color = if (isPositive) StatusSuccess else StatusDanger,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun EmptyTabPlaceholder(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = TextSubtle,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = TextMuted,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                color = TextSubtle,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ----------------------------------------------------
// POPUPS & ACTION DIALOGS
// ----------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenTabWithAdvanceDialog(
    currency: String,
    defaultAdvance: Double,
    onDismiss: () -> Unit,
    onOpen: (
        customerName: String,
        customerPhone: String,
        tableName: String,
        advanceAmount: Double,
        advanceMethod: String,
        notes: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var selectedTable by remember { mutableStateOf(SampleData.availableTables.first()) }
    var advanceAmountText by remember { mutableStateOf(defaultAdvance.toInt().toString()) }
    var advanceMethod by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }
    var tableExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val paymentMethods = listOf("Cash", "Card / POS", "Transfer / Wire", "Mobile")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Column {
                Text(
                    text = "Open Tab with Advance Deposit",
                    color = SpeakeasyGoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "Customer pays advance deposit upfront to open tab",
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            }
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    if (errorMessage != null) {
                        Text(errorMessage!!, color = StatusDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                item {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer / Guest Name *") },
                        placeholder = { Text("e.g. Gary Murambiwa, Sarah J.") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = customDialogTextFieldColors()
                    )
                }

                item {
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Phone Number (Optional)") },
                        placeholder = { Text("+1 ...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        colors = customDialogTextFieldColors()
                    )
                }

                // Table Selector
                item {
                    ExposedDropdownMenuBox(
                        expanded = tableExpanded,
                        onExpandedChange = { tableExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTable,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bar Stool / Table Section") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tableExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = customDialogTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = tableExpanded,
                            onDismissRequest = { tableExpanded = false },
                            modifier = Modifier.background(BarCardElevated)
                        ) {
                            SampleData.availableTables.forEach { tbl ->
                                DropdownMenuItem(
                                    text = { Text(tbl, color = TextIvory) },
                                    onClick = {
                                        selectedTable = tbl
                                        tableExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Advance Deposit Amount with Quick Presets
                item {
                    Text("Advance Deposit Amount ($currency):", color = SpeakeasyGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(20, 50, 100, 200).forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (advanceAmountText == preset.toString()) SuccubusRed else BarDarkSurface)
                                    .border(1.dp, BarBorder, RoundedCornerShape(6.dp))
                                    .clickable { advanceAmountText = preset.toString() }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$currency$preset", color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = advanceAmountText,
                        onValueChange = { advanceAmountText = it },
                        label = { Text("Custom Advance Deposit ($currency)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = customDialogTextFieldColors()
                    )
                }

                // Payment method for advance
                item {
                    Text("Advance Payment Method:", color = TextMuted, fontSize = 11.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        paymentMethods.forEach { method ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (advanceMethod == method) SpeakeasyGold else BarDarkSurface)
                                    .border(0.8.dp, BarBorder, RoundedCornerShape(6.dp))
                                    .clickable { advanceMethod = method }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = method,
                                    color = if (advanceMethod == method) BarBlack else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes (e.g. VIP guest, drink preferences)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        colors = customDialogTextFieldColors()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (customerName.isBlank()) {
                        errorMessage = "Customer name is required"
                        return@Button
                    }
                    val advance = advanceAmountText.toDoubleOrNull() ?: 0.0
                    onOpen(customerName, customerPhone, selectedTable, advance, advanceMethod, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed)
            ) {
                Text("Open Tab", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun ClearCreditDialog(
    tab: BarTab,
    currency: String,
    taxRate: Double,
    serviceRate: Double,
    onDismiss: () -> Unit,
    onConfirmSettlement: (method: String, notes: String) -> Unit
) {
    val totalBill = tab.calculateTotal(taxRate, serviceRate)
    val balance = tab.calculateBalance(taxRate, serviceRate)
    val isOwed = balance > 0
    val isRefund = balance < 0
    val absBalance = kotlin.math.abs(balance)

    var settlementMethod by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Text(
                text = if (isOwed) "Clear Customer Credit / Balance" else "Settle Tab & Refund Advance",
                color = SpeakeasyGoldLight,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Customer: ${tab.customerName} (${tab.tableName})",
                    color = TextIvory,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Financial Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BarDarkSurface)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal (${tab.items.size} items):", color = TextMuted, fontSize = 12.sp)
                        Text("$currency${String.format("%.2f", tab.subtotal)}", color = TextIvory, fontSize = 12.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tax ($taxRate%) & Service ($serviceRate%):", color = TextMuted, fontSize = 12.sp)
                        Text(
                            "$currency${String.format("%.2f", totalBill - tab.subtotal)}",
                            color = TextIvory,
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Advance Deposit Paid:", color = SpeakeasyGold, fontSize = 12.sp)
                        Text("-$currency${String.format("%.2f", tab.advancePaid)}", color = SpeakeasyGold, fontSize = 12.sp)
                    }

                    Divider(color = BarBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isOwed) "Amount Customer Must Pay:" else "Remaining Advance to Refund:",
                            color = if (isOwed) StatusDanger else StatusSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$currency${String.format("%.2f", absBalance)}",
                            color = if (isOwed) StatusDanger else StatusSuccess,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Payment / Settlement method
                Text("Settlement Payment Method:", color = TextMuted, fontSize = 11.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Cash", "Card / POS", "Transfer", "Store Credit").forEach { m ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (settlementMethod == m) SuccubusRed else BarDarkSurface)
                                .border(0.8.dp, BarBorder, RoundedCornerShape(6.dp))
                                .clickable { settlementMethod = m }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = m,
                                color = TextIvory,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Settlement Notes") },
                    placeholder = { Text("e.g. Paid in full via Visa...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customDialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmSettlement(settlementMethod, notes) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOwed) SuccubusRed else SpeakeasyGold
                )
            ) {
                Text(
                    text = if (isOwed) "Clear Credit & Settle Bill" else "Refund & Close Tab",
                    color = if (isOwed) TextIvory else BarBlack,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AddAdvanceDialog(
    tab: BarTab,
    currency: String,
    onDismiss: () -> Unit,
    onConfirmAdvance: (amount: Double, method: String, notes: String) -> Unit
) {
    var advanceText by remember { mutableStateOf("50") }
    var method by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Text(
                text = "Add Advance to Tab",
                color = SpeakeasyGoldLight,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${tab.customerName} (${tab.tableName})",
                    color = TextIvory,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current advance already recorded: $currency${String.format("%.2f", tab.advancePaid)}",
                    color = TextMuted,
                    fontSize = 12.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(20, 50, 100, 200).forEach { p ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (advanceText == p.toString()) SuccubusRed else BarDarkSurface)
                                .border(1.dp, BarBorder, RoundedCornerShape(6.dp))
                                .clickable { advanceText = p.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$currency$p", color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = advanceText,
                    onValueChange = { advanceText = it },
                    label = { Text("Additional Advance ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customDialogTextFieldColors()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Cash", "Card", "Transfer").forEach { m ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (method == m) SpeakeasyGold else BarDarkSurface)
                                .clickable { method = m }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                m,
                                color = if (method == m) BarBlack else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = advanceText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onConfirmAdvance(amt, method, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed)
            ) {
                Text("Confirm Advance", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}

@Composable
fun AddItemToTabDialog(
    tab: BarTab,
    products: List<Product>,
    currency: String,
    onDismiss: () -> Unit,
    onAddItem: (product: Product, quantity: Int, notes: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedQty by remember { mutableStateOf(1) }
    var notes by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        searchQuery.isBlank() ||
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.category.displayName.contains(searchQuery, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Text(
                text = "Add Drink to ${tab.customerName}'s Tab",
                color = SpeakeasyGoldLight,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search drink...", color = TextSubtle, fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    singleLine = true,
                    colors = customDialogTextFieldColors()
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredProducts) { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(BarDarkSurface)
                                .clickable {
                                    onAddItem(prod, selectedQty, notes)
                                    onDismiss()
                                }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.name, color = TextIvory, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("${prod.category.displayName} • ${prod.stock} in stock", color = TextMuted, fontSize = 10.sp)
                            }
                            Text(
                                "$currency${String.format("%.2f", prod.price)}",
                                color = SpeakeasyGoldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextMuted)
            }
        }
    )
}

@Composable
fun ReceiptBillDialog(
    tab: BarTab,
    settings: com.example.succubus.model.VenueSettings,
    onDismiss: () -> Unit
) {
    val totalBill = tab.calculateTotal(settings.taxRatePercent, settings.serviceChargePercent)
    val balance = tab.calculateBalance(settings.taxRatePercent, settings.serviceChargePercent)
    val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(tab.openedAt))

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F0D12),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Succubus Logo Canvas on Receipt
                DevilHeartCanvas(
                    modifier = Modifier.size(42.dp),
                    color = SuccubusRed,
                    strokeWidth = 5f
                )
                Text(
                    text = settings.venueName,
                    color = SpeakeasyGoldLight,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 4.sp
                )
                Text(
                    text = settings.tagline.take(45),
                    color = TextMuted,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center
                )

                Divider(color = SpeakeasyGold.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 8.dp))

                // Metadata
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Table: ${tab.tableName}", color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(dateStr, color = TextMuted, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Guest: ${tab.customerName}", color = TextIvory, fontSize = 11.sp)
                    Text("Tab #${tab.id}", color = TextMuted, fontSize = 11.sp)
                }

                Divider(color = BarBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Line items
                tab.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.quantity}x ${item.name}", color = TextIvory, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        Text(
                            "${settings.currencySymbol}${String.format("%.2f", item.totalPrice)}",
                            color = SpeakeasyGoldLight,
                            fontSize = 12.sp
                        )
                    }
                }

                Divider(color = BarBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Subtotal, Tax, Service
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", color = TextMuted, fontSize = 11.sp)
                    Text("${settings.currencySymbol}${String.format("%.2f", tab.subtotal)}", color = TextIvory, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tax (${settings.taxRatePercent}%)", color = TextMuted, fontSize = 11.sp)
                    Text("${settings.currencySymbol}${String.format("%.2f", tab.calculateTax(settings.taxRatePercent))}", color = TextIvory, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Service (${settings.serviceChargePercent}%)", color = TextMuted, fontSize = 11.sp)
                    Text(
                        "${settings.currencySymbol}${String.format("%.2f", tab.subtotal * (settings.serviceChargePercent / 100.0))}",
                        color = TextIvory,
                        fontSize = 11.sp
                    )
                }

                Divider(color = SpeakeasyGold.copy(alpha = 0.6f), modifier = Modifier.padding(vertical = 6.dp))

                // Total Bill
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL BILL", color = SpeakeasyGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${settings.currencySymbol}${String.format("%.2f", totalBill)}", color = SpeakeasyGoldLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                // Advance Deposit
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Advance Deposit Paid", color = SpeakeasyGold, fontSize = 12.sp)
                    Text("-${settings.currencySymbol}${String.format("%.2f", tab.advancePaid)}", color = SpeakeasyGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Balance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val isOwed = balance > 0
                    Text(
                        if (isOwed) "BALANCE DUE" else "CREDIT TO REFUND",
                        color = if (isOwed) StatusDanger else StatusSuccess,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${settings.currencySymbol}${String.format("%.2f", kotlin.math.abs(balance))}",
                        color = if (isOwed) StatusDanger else StatusSuccess,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Thank you for visiting Succubus Nightlife", color = TextMuted, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SpeakeasyGold)
            ) {
                Text("Done", color = BarBlack, fontWeight = FontWeight.Bold)
            }
        }
    )
}
