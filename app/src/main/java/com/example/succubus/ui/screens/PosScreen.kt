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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.succubus.data.AppRepository
import com.example.succubus.model.BarTab
import com.example.succubus.model.PaymentRecord
import com.example.succubus.model.PaymentType
import com.example.succubus.model.Product
import com.example.succubus.model.ProductCategory
import com.example.succubus.model.TabOrderItem
import com.example.succubus.model.TabStatus
import com.example.succubus.ui.components.BrandCompactHeader
import com.example.succubus.ui.theme.BarBlack
import com.example.succubus.ui.theme.BarBorder
import com.example.succubus.ui.theme.BarCardBg
import com.example.succubus.ui.theme.BarCardElevated
import com.example.succubus.ui.theme.BarDarkSurface
import com.example.succubus.ui.theme.StatusDanger
import com.example.succubus.ui.theme.SpeakeasyGold
import com.example.succubus.ui.theme.SpeakeasyGoldLight
import com.example.succubus.ui.theme.StatusSuccess
import com.example.succubus.ui.theme.StatusWarning
import com.example.succubus.ui.theme.SuccubusRed
import com.example.succubus.ui.theme.TextIvory
import com.example.succubus.ui.theme.TextMuted
import com.example.succubus.ui.theme.TextSubtle
import java.util.UUID

data class CartItem(
    val product: Product,
    var quantity: Int = 1,
    var notes: String = ""
) {
    val totalPrice: Double get() = product.price * quantity
}

@Composable
fun PosScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val products by repository.products.collectAsState()
    val tabs by repository.tabs.collectAsState()
    val settings by repository.settings.collectAsState()

    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val cartItems = remember { mutableStateListOf<CartItem>() }

    var showSendToTabDialog by remember { mutableStateOf(false) }
    var showQuickSaleSuccessDialog by remember { mutableStateOf(false) }

    val activeTabs = tabs.filter { it.status == TabStatus.OPEN }

    val filteredProducts = products.filter { p ->
        val matchesSearch = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == null || p.category == selectedCategory
        matchesSearch && matchesCategory
    }

    val cartSubtotal = cartItems.sumOf { it.totalPrice }
    val cartTax = cartSubtotal * (settings.taxRatePercent / 100.0)
    val cartTotal = cartSubtotal + cartTax

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BarBlack)
            .padding(12.dp)
    ) {
        // Compact Bar Brand Header
        BrandCompactHeader(settings = settings)

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Category row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Quick drink search...", color = TextSubtle, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SpeakeasyGold, modifier = Modifier.size(16.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BarDarkSurface,
                    unfocusedContainerColor = BarDarkSurface,
                    focusedBorderColor = SpeakeasyGold,
                    unfocusedBorderColor = BarBorder,
                    focusedTextColor = TextIvory,
                    unfocusedTextColor = TextIvory
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                CategoryChip(
                    label = "All (${products.size})",
                    isSelected = selectedCategory == null,
                    onClick = { selectedCategory = null }
                )
            }
            items(ProductCategory.entries.toTypedArray()) { cat ->
                CategoryChip(
                    label = cat.displayName,
                    isSelected = selectedCategory == cat,
                    onClick = { selectedCategory = cat }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Main grid: Products catalog (takes up remaining space)
        Box(modifier = Modifier.weight(1f)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    PosProductItemCard(
                        product = product,
                        currency = settings.currencySymbol,
                        onClick = {
                            val existing = cartItems.find { it.product.id == product.id }
                            if (existing != null) {
                                existing.quantity++
                                // Force state trigger
                                val idx = cartItems.indexOf(existing)
                                cartItems[idx] = existing.copy()
                            } else {
                                cartItems.add(CartItem(product, 1))
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Order Cart & Checkout Drawer
        if (cartItems.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpeakeasyGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = SpeakeasyGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Current Order (${cartItems.sumOf { it.quantity }} items)",
                                color = TextIvory,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TextButton(
                            onClick = { cartItems.clear() },
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text("Clear", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    // Mini cart list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(cartItems) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BarDarkSurface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.product.name,
                                    color = TextIvory,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (item.quantity > 1) {
                                                item.quantity--
                                                val idx = cartItems.indexOf(item)
                                                cartItems[idx] = item.copy()
                                            } else {
                                                cartItems.remove(item)
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }

                                    Text(
                                        text = "${item.quantity}",
                                        color = SpeakeasyGoldLight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )

                                    IconButton(
                                        onClick = {
                                            item.quantity++
                                            val idx = cartItems.indexOf(item)
                                            cartItems[idx] = item.copy()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = SpeakeasyGold, modifier = Modifier.size(14.dp))
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${settings.currencySymbol}${String.format("%.2f", item.totalPrice)}",
                                        color = TextIvory,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = BarBorder, modifier = Modifier.padding(vertical = 6.dp))

                    // Totals and action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("TOTAL (INCL. TAX)", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${settings.currencySymbol}${String.format("%.2f", cartTotal)}",
                                color = SpeakeasyGoldLight,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Quick Cash Sale
                            Button(
                                onClick = {
                                    cartItems.forEach { item ->
                                        repository.adjustStock(item.product.id, -item.quantity)
                                    }
                                    cartItems.clear()
                                    showQuickSaleSuccessDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpeakeasyGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Quick Cash", color = BarBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Send to Customer Tab
                            Button(
                                onClick = { showSendToTabDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Charge to Tab", color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOG: SEND CART ITEMS TO ACTIVE TAB ---
    if (showSendToTabDialog) {
        AlertDialog(
            onDismissRequest = { showSendToTabDialog = false },
            containerColor = BarCardElevated,
            title = {
                Text(
                    text = "Select Tab to Charge Order",
                    color = SpeakeasyGoldLight,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (activeTabs.isEmpty()) {
                    Text(
                        "No open customer tabs available. Please create a tab in 'Tabs & Credit' first.",
                        color = StatusWarning,
                        fontSize = 13.sp
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(activeTabs) { tab ->
                            val balance = tab.calculateBalance(settings.taxRatePercent, settings.serviceChargePercent)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BarDarkSurface)
                                    .clickable {
                                        cartItems.forEach { cartItem ->
                                            repository.addItemToTab(
                                                tabId = tab.id,
                                                product = cartItem.product,
                                                quantity = cartItem.quantity,
                                                notes = cartItem.notes
                                            )
                                        }
                                        cartItems.clear()
                                        showSendToTabDialog = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tab.customerName, color = TextIvory, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(tab.tableName, color = SpeakeasyGoldLight, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Advance: ${settings.currencySymbol}${String.format("%.2f", tab.advancePaid)}", color = TextMuted, fontSize = 10.sp)
                                    Text(
                                        "Charge +${settings.currencySymbol}${String.format("%.2f", cartTotal)}",
                                        color = SuccubusRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSendToTabDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    if (showQuickSaleSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showQuickSaleSuccessDialog = false },
            containerColor = BarCardElevated,
            title = {
                Text("Sale Complete", color = StatusSuccess, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Payment collected and inventory counts deducted successfully.", color = TextIvory)
            },
            confirmButton = {
                Button(
                    onClick = { showQuickSaleSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SpeakeasyGold)
                ) {
                    Text("OK", color = BarBlack)
                }
            }
        )
    }
}

@Composable
fun PosProductItemCard(
    product: Product,
    currency: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BarCardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (product.isOutOfStock) StatusDanger.copy(alpha = 0.4f) else BarBorder
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !product.isOutOfStock) { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF261D2C))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        product.category.displayName.take(12),
                        color = SpeakeasyGoldLight,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${product.stock} left",
                    color = if (product.isLowStock || product.isOutOfStock) StatusWarning else TextSubtle,
                    fontSize = 9.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = product.name,
                color = if (product.isOutOfStock) TextMuted else TextIvory,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currency${String.format("%.2f", product.price)}",
                    color = SpeakeasyGoldLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (product.isOutOfStock) Color.Gray else SuccubusRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = TextIvory, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
