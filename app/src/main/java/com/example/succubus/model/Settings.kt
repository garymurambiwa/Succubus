package com.example.succubus.model

enum class LogoStyle {
    ORIGINAL_BANNER, // The uploaded gold & red heart Succubus banner
    DEVIL_HEART_EMBLEM, // Minimalist crimson devil heart & gold ring
    MODERN_MINIMAL, // Modern typography with crimson neon accent
    CUSTOM_URL // Custom image URI or URL
}

data class VenueSettings(
    val venueName: String = "SUCCUBUS",
    val tagline: String = "Cocktails & Nightlife • Private Events • Special Experiences",
    val logoStyle: LogoStyle = LogoStyle.ORIGINAL_BANNER,
    val customLogoUri: String = "",
    val currencySymbol: String = "$",
    val taxRatePercent: Double = 8.5,
    val serviceChargePercent: Double = 10.0,
    val defaultAdvanceDeposit: Double = 50.0,
    val stationName: String = "Main Bar - Station 1",
    val bartenderName: String = "Gary (Bar Manager)",
    val enableLowStockAlerts: Boolean = true
)
