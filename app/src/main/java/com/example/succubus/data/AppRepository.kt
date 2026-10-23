package com.example.succubus.data

import android.content.Context
import com.example.succubus.model.BarRecipe
import com.example.succubus.model.BarTab
import com.example.succubus.model.PaymentRecord
import com.example.succubus.model.PaymentType
import com.example.succubus.model.Product
import com.example.succubus.model.TabOrderItem
import com.example.succubus.model.TabStatus
import com.example.succubus.model.VenueSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AppRepository(private val context: Context) {

    private val _settings = MutableStateFlow(VenueSettings())
    val settings: StateFlow<VenueSettings> = _settings.asStateFlow()

    private val _products = MutableStateFlow(SampleData.initialProducts)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _tabs = MutableStateFlow(SampleData.initialTabs)
    val tabs: StateFlow<List<BarTab>> = _tabs.asStateFlow()

    private val _recipes = MutableStateFlow(SampleData.initialRecipes)
    val recipes: StateFlow<List<BarRecipe>> = _recipes.asStateFlow()

    private val _paymentLedger = MutableStateFlow<List<PaymentRecord>>(
        SampleData.initialTabs.flatMap { it.payments }
    )
    val paymentLedger: StateFlow<List<PaymentRecord>> = _paymentLedger.asStateFlow()

    // ----------------------------------------------------
    // INVENTORY MANAGEMENT
    // ----------------------------------------------------
    fun addProduct(
        name: String,
        category: com.example.succubus.model.ProductCategory,
        price: Double,
        costPrice: Double,
        initialStock: Int,
        minStockThreshold: Int,
        unit: String,
        volume: String,
        sku: String,
        description: String
    ): Product {
        val newProduct = Product(
            id = "prod-" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            category = category,
            price = price,
            costPrice = costPrice,
            stock = initialStock,
            minStockThreshold = minStockThreshold,
            unit = unit.ifBlank { "Bottle" },
            volume = volume.ifBlank { "750ml" },
            sku = if (sku.isNotBlank()) sku.trim() else "SKU-" + (1000..9999).random(),
            description = description.trim()
        )
        _products.value = listOf(newProduct) + _products.value
        return newProduct
    }

    fun updateProduct(updated: Product) {
        _products.value = _products.value.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun deleteProduct(productId: String) {
        _products.value = _products.value.filter { it.id != productId }
    }

    fun adjustStock(productId: String, delta: Int) {
        _products.value = _products.value.map {
            if (it.id == productId) {
                val newStock = (it.stock + delta).coerceAtLeast(0)
                it.copy(stock = newStock)
            } else it
        }
    }

    fun setStock(productId: String, exactStock: Int) {
        _products.value = _products.value.map {
            if (it.id == productId) {
                it.copy(stock = exactStock.coerceAtLeast(0))
            } else it
        }
    }

    // ----------------------------------------------------
    // TAB & ADVANCE CREDIT MANAGEMENT
    // ----------------------------------------------------
    fun openTab(
        customerName: String,
        customerPhone: String,
        tableName: String,
        advanceAmount: Double,
        advanceMethod: String,
        notes: String
    ): BarTab {
        val tabId = "tab-" + (100..999).random()
        val initialPayments = mutableListOf<PaymentRecord>()
        
        if (advanceAmount > 0) {
            initialPayments.add(
                PaymentRecord(
                    id = "pay-" + UUID.randomUUID().toString().take(8),
                    tabId = tabId,
                    customerName = customerName,
                    amount = advanceAmount,
                    method = advanceMethod,
                    type = PaymentType.ADVANCE_DEPOSIT,
                    notes = "Advance deposit to open $tableName"
                )
            )
        }

        val newTab = BarTab(
            id = tabId,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            tableName = tableName,
            advancePaid = advanceAmount,
            advanceMethod = advanceMethod,
            status = TabStatus.OPEN,
            notes = notes.trim(),
            payments = initialPayments
        )

        _tabs.value = listOf(newTab) + _tabs.value
        _paymentLedger.value = initialPayments + _paymentLedger.value
        return newTab
    }

    fun addItemToTab(tabId: String, product: Product, quantity: Int = 1, notes: String = "") {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                val existingIndex = tab.items.indexOfFirst { it.productId == product.id && it.notes == notes }
                val newItems = tab.items.toMutableList()
                if (existingIndex >= 0) {
                    val existing = newItems[existingIndex]
                    newItems[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
                } else {
                    newItems.add(
                        TabOrderItem(
                            id = "item-" + UUID.randomUUID().toString().take(8),
                            productId = product.id,
                            name = product.name,
                            unitPrice = product.price,
                            quantity = quantity,
                            notes = notes
                        )
                    )
                }
                tab.copy(items = newItems)
            } else tab
        }
        // Deduct inventory stock
        adjustStock(product.id, -quantity)
    }

    fun removeItemFromTab(tabId: String, itemId: String) {
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                val item = tab.items.find { it.id == itemId }
                if (item != null) {
                    adjustStock(item.productId, item.quantity)
                }
                tab.copy(items = tab.items.filter { it.id != itemId })
            } else tab
        }
    }

    fun addAdvanceToTab(tabId: String, additionalAdvance: Double, method: String, notes: String) {
        if (additionalAdvance <= 0) return
        var targetCustomer = ""
        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                targetCustomer = tab.customerName
                val payment = PaymentRecord(
                    id = "pay-" + UUID.randomUUID().toString().take(8),
                    tabId = tabId,
                    customerName = tab.customerName,
                    amount = additionalAdvance,
                    method = method,
                    type = PaymentType.ADDITIONAL_ADVANCE,
                    notes = if (notes.isNotBlank()) notes else "Additional advance deposit"
                )
                _paymentLedger.value = listOf(payment) + _paymentLedger.value
                tab.copy(
                    advancePaid = tab.advancePaid + additionalAdvance,
                    payments = tab.payments + payment
                )
            } else tab
        }
    }

    /**
     * Clear customer credit / settle balance:
     * If customer owes balance (> 0): they pay the balance using method ("Cash", "Card", etc.)
     * If customer has remaining advance (< 0): refund excess or close tab with refund record
     */
    fun clearCustomerCredit(
        tabId: String,
        settlementMethod: String,
        notes: String = ""
    ): Boolean {
        var found = false
        val currentSettings = _settings.value

        _tabs.value = _tabs.value.map { tab ->
            if (tab.id == tabId) {
                found = true
                val balance = tab.calculateBalance(currentSettings.taxRatePercent, currentSettings.serviceChargePercent)
                val paymentType = if (balance > 0) PaymentType.CREDIT_CLEARANCE else PaymentType.ADVANCE_REFUND
                val paymentAmount = kotlin.math.abs(balance)

                val payment = PaymentRecord(
                    id = "pay-" + UUID.randomUUID().toString().take(8),
                    tabId = tabId,
                    customerName = tab.customerName,
                    amount = paymentAmount,
                    method = settlementMethod,
                    type = paymentType,
                    notes = if (notes.isNotBlank()) notes else {
                        if (balance > 0) "Cleared outstanding customer balance of $${String.format("%.2f", balance)}"
                        else "Refunded unspent advance credit of $${String.format("%.2f", paymentAmount)}"
                    }
                )

                _paymentLedger.value = listOf(payment) + _paymentLedger.value

                tab.copy(
                    status = TabStatus.CLOSED,
                    closedAt = System.currentTimeMillis(),
                    payments = tab.payments + payment
                )
            } else tab
        }
        return found
    }

    // ----------------------------------------------------
    // RECIPES
    // ----------------------------------------------------
    fun addRecipe(recipe: BarRecipe) {
        _recipes.value = listOf(recipe) + _recipes.value
    }

    fun deleteRecipe(recipeId: String) {
        _recipes.value = _recipes.value.filter { it.id != recipeId }
    }

    // ----------------------------------------------------
    // VENUE SETTINGS & BRANDING
    // ----------------------------------------------------
    fun updateSettings(newSettings: VenueSettings) {
        _settings.value = newSettings
    }

    fun updateVenueNameAndTagline(name: String, tagline: String) {
        _settings.value = _settings.value.copy(
            venueName = name.trim(),
            tagline = tagline.trim()
        )
    }

    fun updateLogoStyle(style: com.example.succubus.model.LogoStyle, customUri: String = "") {
        _settings.value = _settings.value.copy(
            logoStyle = style,
            customLogoUri = customUri
        )
    }

    fun updateTaxAndService(taxPercent: Double, servicePercent: Double, currency: String) {
        _settings.value = _settings.value.copy(
            taxRatePercent = taxPercent,
            serviceChargePercent = servicePercent,
            currencySymbol = currency
        )
    }
}
