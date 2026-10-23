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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.succubus.data.AppRepository
import com.example.succubus.model.Product
import com.example.succubus.model.ProductCategory
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val products by repository.products.collectAsState()
    val settings by repository.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }
    var filterLowStockOnly by remember { mutableStateOf(false) }

    // Dialog states
    var showAddProductDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }
    var productToAdjustStock by remember { mutableStateOf<Product?>(null) }

    val filteredProducts = products.filter { prod ->
        val matchesSearch = searchQuery.isBlank() ||
                prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.sku.contains(searchQuery, ignoreCase = true) ||
                prod.description.contains(searchQuery, ignoreCase = true)

        val matchesCategory = selectedCategory == null || prod.category == selectedCategory
        val matchesLowStock = !filterLowStockOnly || prod.isLowStock || prod.isOutOfStock

        matchesSearch && matchesCategory && matchesLowStock
    }

    val lowStockCount = products.count { it.isLowStock || it.isOutOfStock }
    val totalRetailValue = products.sumOf { it.price * it.stock }
    val totalCostValue = products.sumOf { it.costPrice * it.stock }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BarBlack)
            .padding(14.dp)
    ) {
        // Top Action Bar & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BAR & CELLAR INVENTORY",
                    color = SpeakeasyGoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${products.size} Total Items • Live Stock Tracking",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddProductDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccubusRed,
                    contentColor = TextIvory
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Product", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Inventory Value & KPI Card
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
                Text("STOCK VALUATION", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${settings.currencySymbol}${String.format("%.2f", totalRetailValue)}",
                    color = SpeakeasyGoldLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Cost: ${settings.currencySymbol}${String.format("%.2f", totalCostValue)}", color = TextMuted, fontSize = 10.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("LOW STOCK WARNINGS", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (lowStockCount > 0) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusWarning,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        "$lowStockCount items low/out",
                        color = if (lowStockCount > 0) StatusWarning else StatusSuccess,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    if (filterLowStockOnly) "Showing low only" else "Tap alert to filter",
                    color = SpeakeasyGold,
                    fontSize = 10.sp,
                    modifier = Modifier.clickable { filterLowStockOnly = !filterLowStockOnly }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, SKU, cocktail...", color = TextSubtle, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SpeakeasyGold, modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
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
                .height(50.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                CategoryChip(
                    label = "All Items (${products.size})",
                    isSelected = selectedCategory == null && !filterLowStockOnly,
                    onClick = {
                        selectedCategory = null
                        filterLowStockOnly = false
                    }
                )
            }
            item {
                CategoryChip(
                    label = "⚠️ Low Stock ($lowStockCount)",
                    isSelected = filterLowStockOnly,
                    onClick = { filterLowStockOnly = !filterLowStockOnly }
                )
            }
            items(ProductCategory.entries.toTypedArray()) { category ->
                val count = products.count { it.category == category }
                CategoryChip(
                    label = "${category.displayName} ($count)",
                    isSelected = selectedCategory == category && !filterLowStockOnly,
                    onClick = {
                        selectedCategory = category
                        filterLowStockOnly = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Product List
        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No products found",
                        color = TextMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing your filters or add a new product manually.",
                        color = TextSubtle,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductInventoryCard(
                        product = product,
                        currency = settings.currencySymbol,
                        onEdit = { productToEdit = product },
                        onDelete = { productToDelete = product },
                        onAdjustStock = { productToAdjustStock = product },
                        onQuickDelta = { delta -> repository.adjustStock(product.id, delta) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // --- ADD PRODUCT DIALOG ---
    if (showAddProductDialog) {
        ProductFormDialog(
            title = "Add Product to Inventory",
            product = null,
            currency = settings.currencySymbol,
            onDismiss = { showAddProductDialog = false },
            onSave = { name, cat, price, cost, stock, minStock, unit, vol, sku, desc ->
                repository.addProduct(
                    name = name,
                    category = cat,
                    price = price,
                    costPrice = cost,
                    initialStock = stock,
                    minStockThreshold = minStock,
                    unit = unit,
                    volume = vol,
                    sku = sku,
                    description = desc
                )
                showAddProductDialog = false
            }
        )
    }

    // --- EDIT PRODUCT DIALOG ---
    if (productToEdit != null) {
        val prod = productToEdit!!
        ProductFormDialog(
            title = "Edit Product Details",
            product = prod,
            currency = settings.currencySymbol,
            onDismiss = { productToEdit = null },
            onSave = { name, cat, price, cost, stock, minStock, unit, vol, sku, desc ->
                repository.updateProduct(
                    prod.copy(
                        name = name,
                        category = cat,
                        price = price,
                        costPrice = cost,
                        stock = stock,
                        minStockThreshold = minStock,
                        unit = unit,
                        volume = vol,
                        sku = sku,
                        description = desc
                    )
                )
                productToEdit = null
            }
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = BarCardElevated,
            titleContentColor = TextIvory,
            textContentColor = TextMuted,
            title = { Text("Delete Product?") },
            text = { Text("Are you sure you want to remove '${prod.name}' from your bar inventory? This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        repository.deleteProduct(prod.id)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // --- STOCK ADJUSTMENT DIALOG ---
    if (productToAdjustStock != null) {
        val prod = productToAdjustStock!!
        StockAdjustDialog(
            product = prod,
            onDismiss = { productToAdjustStock = null },
            onSetExactStock = { exact ->
                repository.setStock(prod.id, exact)
                productToAdjustStock = null
            },
            onAddStock = { delta ->
                repository.adjustStock(prod.id, delta)
                productToAdjustStock = null
            }
        )
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) SuccubusRed else BarDarkSurface)
            .border(
                1.dp,
                if (isSelected) SuccubusRed else BarBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) TextIvory else TextMuted,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ProductInventoryCard(
    product: Product,
    currency: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAdjustStock: () -> Unit,
    onQuickDelta: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BarCardBg),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (product.isOutOfStock) StatusDanger.copy(alpha = 0.5f)
            else if (product.isLowStock) StatusWarning.copy(alpha = 0.4f)
            else BarBorder
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Category, SKU, and Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF261E2E))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = product.category.displayName.uppercase(),
                            color = SpeakeasyGoldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    if (product.sku.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = product.sku,
                            color = TextSubtle,
                            fontSize = 10.sp
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextMuted, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusDanger.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Product Name & Description
            Text(
                text = product.name,
                color = TextIvory,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            if (product.description.isNotBlank()) {
                Text(
                    text = product.description,
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Margins row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BarDarkSurface)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("MENU PRICE", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("$currency${String.format("%.2f", product.price)}", color = SpeakeasyGoldLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("COST", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("$currency${String.format("%.2f", product.costPrice)}", color = TextMuted, fontSize = 13.sp)
                }
                Column {
                    Text("MARGIN", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${String.format("%.0f", product.profitMargin)}%", color = StatusSuccess, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("UNIT", color = TextSubtle, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${product.unit} (${product.volume})", color = TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stock Count & Fast Adjustment Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = when {
                        product.isOutOfStock -> StatusDanger
                        product.isLowStock -> StatusWarning
                        else -> StatusSuccess
                    }
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(badgeColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${product.stock} in stock",
                        color = badgeColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " (Min: ${product.minStockThreshold})",
                        color = TextSubtle,
                        fontSize = 11.sp
                    )
                }

                // Quick Restock & Adjust Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick +1
                    QuickStockButton(label = "+1") { onQuickDelta(1) }
                    Spacer(modifier = Modifier.width(4.dp))
                    // Quick +6 (Pack/Half Case)
                    QuickStockButton(label = "+6") { onQuickDelta(6) }
                    Spacer(modifier = Modifier.width(4.dp))
                    // Full Adjust Dialog
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E2436))
                            .clickable { onAdjustStock() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Tune, contentDescription = "Adjust", tint = SpeakeasyGold, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Count", color = SpeakeasyGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickStockButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(BarDarkSurface)
            .border(0.8.dp, BarBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Text(text = label, color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
    title: String,
    product: Product?,
    currency: String,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: ProductCategory,
        price: Double,
        costPrice: Double,
        stock: Int,
        minStock: Int,
        unit: String,
        volume: String,
        sku: String,
        description: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: ProductCategory.COCKTAILS) }
    var priceText by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var costPriceText by remember { mutableStateOf(product?.costPrice?.toString() ?: "") }
    var stockText by remember { mutableStateOf(product?.stock?.toString() ?: "12") }
    var minStockText by remember { mutableStateOf(product?.minStockThreshold?.toString() ?: "10") }
    var unit by remember { mutableStateOf(product?.unit ?: "Bottle") }
    var volume by remember { mutableStateOf(product?.volume ?: "750ml") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Text(
                text = title,
                color = SpeakeasyGoldLight,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = StatusDanger,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Name
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product / Drink Name *") },
                        placeholder = { Text("e.g. Succubus Temptation, Macallan 12...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )
                }

                // Category Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = customDialogTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier.background(BarCardElevated)
                        ) {
                            ProductCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName, color = TextIvory) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Price and Cost Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Menu Price ($currency) *") },
                            placeholder = { Text("15.00") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                        OutlinedTextField(
                            value = costPriceText,
                            onValueChange = { costPriceText = it },
                            label = { Text("Cost Price ($currency)") },
                            placeholder = { Text("3.50") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }

                // Stock and Min Threshold Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { stockText = it },
                            label = { Text("Current Stock *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                        OutlinedTextField(
                            value = minStockText,
                            onValueChange = { minStockText = it },
                            label = { Text("Min Alert Level") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }

                // Unit & Volume
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit Type") },
                            placeholder = { Text("Bottle, Shot, Pint") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                        OutlinedTextField(
                            value = volume,
                            onValueChange = { volume = it },
                            label = { Text("Size / Volume") },
                            placeholder = { Text("750ml, 330ml") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }

                // SKU / Barcode
                item {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("SKU / Barcode (Optional)") },
                        placeholder = { Text("SUC-BAR-101") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )
                }

                // Description
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Recipe Notes") },
                        placeholder = { Text("Spices, tasting notes, brand specifics...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = customDialogTextFieldColors()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Product name cannot be empty"
                        return@Button
                    }
                    val price = priceText.toDoubleOrNull()
                    if (price == null || price < 0) {
                        errorMessage = "Please enter a valid price"
                        return@Button
                    }
                    val cost = costPriceText.toDoubleOrNull() ?: 0.0
                    val stock = stockText.toIntOrNull() ?: 0
                    val minStock = minStockText.toIntOrNull() ?: 10

                    onSave(
                        name,
                        selectedCategory,
                        price,
                        cost,
                        stock,
                        minStock,
                        unit,
                        volume,
                        sku,
                        description
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed)
            ) {
                Text("Save to Inventory", fontWeight = FontWeight.Bold)
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
fun StockAdjustDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSetExactStock: (Int) -> Unit,
    onAddStock: (Int) -> Unit
) {
    var exactStockText by remember { mutableStateOf(product.stock.toString()) }
    var addStockText by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("Delivery / Restock") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Text(
                text = "Adjust Stock: ${product.name}",
                color = SpeakeasyGoldLight,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Current on-hand count: ${product.stock} ${product.unit}s",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                // Quick presets
                Text("Quick Add Bottles / Units:", color = TextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 3, 6, 12, 24).forEach { qty ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BarDarkSurface)
                                .border(1.dp, SpeakeasyGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .clickable { onAddStock(qty) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+$qty", color = SpeakeasyGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Custom Exact Count
                OutlinedTextField(
                    value = exactStockText,
                    onValueChange = { exactStockText = it },
                    label = { Text("Or Set Exact Stock Count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = customDialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = exactStockText.toIntOrNull()
                    if (count != null) {
                        onSetExactStock(count)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed)
            ) {
                Text("Update Stock", fontWeight = FontWeight.Bold)
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
fun customDialogTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = BarDarkSurface,
    unfocusedContainerColor = BarDarkSurface,
    focusedBorderColor = SpeakeasyGold,
    unfocusedBorderColor = BarBorder,
    focusedLabelColor = SpeakeasyGold,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = TextIvory,
    unfocusedTextColor = TextIvory
)
