package com.example.succubus.model

enum class TabStatus {
    OPEN,
    CLOSED,
    SETTLED
}

enum class PaymentType {
    ADVANCE_DEPOSIT,
    ADDITIONAL_ADVANCE,
    TAB_SETTLEMENT,
    CREDIT_CLEARANCE,
    ADVANCE_REFUND
}

data class TabOrderItem(
    val id: String,
    val productId: String,
    val name: String,
    val unitPrice: Double,
    val quantity: Int = 1,
    val notes: String = "",
    val addedAt: Long = System.currentTimeMillis()
) {
    val totalPrice: Double get() = unitPrice * quantity
}

data class PaymentRecord(
    val id: String,
    val tabId: String,
    val customerName: String,
    val amount: Double,
    val method: String, // "Cash", "Card", "Transfer", "Store Credit"
    val type: PaymentType,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)

data class BarTab(
    val id: String,
    val customerName: String,
    val customerPhone: String = "",
    val tableName: String, // e.g. "Bar Stool 3", "Table 4", "VIP Lounge 1"
    val openedAt: Long = System.currentTimeMillis(),
    val closedAt: Long? = null,
    val items: List<TabOrderItem> = emptyList(),
    val advancePaid: Double = 0.0,
    val advanceMethod: String = "Cash",
    val status: TabStatus = TabStatus.OPEN,
    val notes: String = "",
    val payments: List<PaymentRecord> = emptyList()
) {
    val subtotal: Double get() = items.sumOf { it.totalPrice }
    
    // Tax calculated at standard rate (configurable via settings)
    fun calculateTax(taxRatePercent: Double): Double = subtotal * (taxRatePercent / 100.0)
    
    fun calculateTotal(taxRatePercent: Double, serviceRatePercent: Double): Double {
        val tax = calculateTax(taxRatePercent)
        val service = subtotal * (serviceRatePercent / 100.0)
        return subtotal + tax + service
    }
    
    // Balance difference compared to advance paid:
    // positive = customer owes remaining balance
    // negative = customer has unspent advance deposit credit
    fun calculateBalance(taxRatePercent: Double, serviceRatePercent: Double): Double {
        val total = calculateTotal(taxRatePercent, serviceRatePercent)
        return total - advancePaid
    }
    
    fun remainingAdvance(taxRatePercent: Double, serviceRatePercent: Double): Double {
        val bal = calculateBalance(taxRatePercent, serviceRatePercent)
        return if (bal < 0) -bal else 0.0
    }
    
    fun amountOwed(taxRatePercent: Double, serviceRatePercent: Double): Double {
        val bal = calculateBalance(taxRatePercent, serviceRatePercent)
        return if (bal > 0) bal else 0.0
    }
}
