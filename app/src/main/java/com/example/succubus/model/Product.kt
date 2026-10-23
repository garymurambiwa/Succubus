package com.example.succubus.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SportsBar
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.ui.graphics.vector.ImageVector

enum class ProductCategory(val displayName: String, val icon: ImageVector) {
    COCKTAILS("Cocktails", Icons.Default.LocalBar),
    SPIRITS("Spirits & Whiskey", Icons.Default.Liquor),
    WINES("Wine & Champagne", Icons.Default.WineBar),
    BEERS("Beer & Cider", Icons.Default.SportsBar),
    SHOTS("Shots & Shooters", Icons.Default.ElectricBolt),
    MIXERS("Soft Drinks & Mixers", Icons.Default.LocalDrink),
    KITCHEN("Bar Bites & Food", Icons.Default.Restaurant),
    GARNISHES("Syrups & Garnishes", Icons.Default.Eco)
}

data class Product(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val price: Double,
    val costPrice: Double,
    val stock: Int,
    val minStockThreshold: Int = 10,
    val unit: String = "Bottle",
    val volume: String = "750ml",
    val sku: String = "",
    val description: String = "",
    val isAvailable: Boolean = true
) {
    val isLowStock: Boolean get() = stock <= minStockThreshold && stock > 0
    val isOutOfStock: Boolean get() = stock <= 0
    val profitMargin: Double get() = if (price > 0) ((price - costPrice) / price) * 100 else 0.0
}
