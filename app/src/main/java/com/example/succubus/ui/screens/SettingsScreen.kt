package com.example.succubus.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.succubus.model.LogoStyle
import com.example.succubus.model.VenueSettings
import com.example.succubus.ui.components.SuccubusBrandBanner
import com.example.succubus.ui.theme.BarBlack
import com.example.succubus.ui.theme.BarBorder
import com.example.succubus.ui.theme.BarCardBg
import com.example.succubus.ui.theme.BarDarkSurface
import com.example.succubus.ui.theme.SpeakeasyGold
import com.example.succubus.ui.theme.SpeakeasyGoldLight
import com.example.succubus.ui.theme.StatusSuccess
import com.example.succubus.ui.theme.SuccubusRed
import com.example.succubus.ui.theme.TextIvory
import com.example.succubus.ui.theme.TextMuted
import com.example.succubus.ui.theme.TextSubtle

@Composable
fun SettingsScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val currentSettings by repository.settings.collectAsState()

    var venueName by remember { mutableStateOf(currentSettings.venueName) }
    var tagline by remember { mutableStateOf(currentSettings.tagline) }
    var selectedLogoStyle by remember { mutableStateOf(currentSettings.logoStyle) }
    var customLogoUri by remember { mutableStateOf(currentSettings.customLogoUri) }
    var currencySymbol by remember { mutableStateOf(currentSettings.currencySymbol) }
    var taxRateText by remember { mutableStateOf(currentSettings.taxRatePercent.toString()) }
    var serviceChargeText by remember { mutableStateOf(currentSettings.serviceChargePercent.toString()) }
    var defaultAdvanceText by remember { mutableStateOf(currentSettings.defaultAdvanceDeposit.toInt().toString()) }
    var stationName by remember { mutableStateOf(currentSettings.stationName) }
    var bartenderName by remember { mutableStateOf(currentSettings.bartenderName) }

    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Temporary settings preview
    val previewSettings = remember(venueName, tagline, selectedLogoStyle, customLogoUri) {
        VenueSettings(
            venueName = venueName.ifBlank { "SUCCUBUS" },
            tagline = tagline,
            logoStyle = selectedLogoStyle,
            customLogoUri = customLogoUri
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BarBlack)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VENUE BRANDING & SETTINGS",
                        color = SpeakeasyGoldLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Customize App Name, Logo, Tax Rates & Station",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        val tax = taxRateText.toDoubleOrNull() ?: 8.5
                        val service = serviceChargeText.toDoubleOrNull() ?: 10.0
                        val advance = defaultAdvanceText.toDoubleOrNull() ?: 50.0

                        val updated = currentSettings.copy(
                            venueName = venueName.trim().ifBlank { "SUCCUBUS" },
                            tagline = tagline.trim(),
                            logoStyle = selectedLogoStyle,
                            customLogoUri = customLogoUri.trim(),
                            currencySymbol = currencySymbol.trim().ifBlank { "$" },
                            taxRatePercent = tax,
                            serviceChargePercent = service,
                            defaultAdvanceDeposit = advance,
                            stationName = stationName.trim(),
                            bartenderName = bartenderName.trim()
                        )
                        repository.updateSettings(updated)
                        saveSuccessMessage = "Settings and Logo successfully updated!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        if (saveSuccessMessage != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B3D2B))
                        .border(1.dp, StatusSuccess, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(saveSuccessMessage!!, color = TextIvory, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // --- BRANDING & LOGO SECTION ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, SpeakeasyGold.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "APP LOGO & BRAND IDENTITY",
                        color = SpeakeasyGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Live Logo Preview Banner
                    Text("Live Header & Logo Preview:", color = TextSubtle, fontSize = 11.sp)
                    SuccubusBrandBanner(
                        settings = previewSettings,
                        height = 140.dp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Change Venue Name
                    OutlinedTextField(
                        value = venueName,
                        onValueChange = { venueName = it },
                        label = { Text("Venue / Bar Name") },
                        placeholder = { Text("SUCCUBUS") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )

                    // Change Tagline
                    OutlinedTextField(
                        value = tagline,
                        onValueChange = { tagline = it },
                        label = { Text("Subtitle / Tagline") },
                        placeholder = { Text("Cocktails & Nightlife • Private Events • Special Experiences") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Logo Selection Options
                    Text("Select App Icon & Logo Style:", color = SpeakeasyGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    LogoStyleOption(
                        title = "Succubus Artboard Logo (Uploaded Attached Image)",
                        subtitle = "Gold border, midnight black canvas, glowing crimson devil heart",
                        isSelected = selectedLogoStyle == LogoStyle.ORIGINAL_BANNER,
                        onClick = { selectedLogoStyle = LogoStyle.ORIGINAL_BANNER }
                    )

                    LogoStyleOption(
                        title = "Devil Heart Vector Emblem",
                        subtitle = "High-definition crimson horns & tail vector with gold serif typography",
                        isSelected = selectedLogoStyle == LogoStyle.DEVIL_HEART_EMBLEM,
                        onClick = { selectedLogoStyle = LogoStyle.DEVIL_HEART_EMBLEM }
                    )

                    LogoStyleOption(
                        title = "Modern Speakeasy Minimalist",
                        subtitle = "Sleek compact logo optimized for high speed mobile bars",
                        isSelected = selectedLogoStyle == LogoStyle.MODERN_MINIMAL,
                        onClick = { selectedLogoStyle = LogoStyle.MODERN_MINIMAL }
                    )

                    LogoStyleOption(
                        title = "Custom Logo File / Device Storage",
                        subtitle = "Load from local phone storage or custom image path",
                        isSelected = selectedLogoStyle == LogoStyle.CUSTOM_URL,
                        onClick = { selectedLogoStyle = LogoStyle.CUSTOM_URL }
                    )

                    if (selectedLogoStyle == LogoStyle.CUSTOM_URL) {
                        OutlinedTextField(
                            value = customLogoUri,
                            onValueChange = { customLogoUri = it },
                            label = { Text("Custom Image Path or URI") },
                            placeholder = { Text("/sdcard/Download/logo.png") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }
            }
        }

        // --- BAR FINANCIALS & STATION ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = BarCardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, BarBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "RATES, CURRENCY & BAR STATION",
                        color = SpeakeasyGoldLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currencySymbol,
                            onValueChange = { currencySymbol = it },
                            label = { Text("Currency Symbol") },
                            placeholder = { Text("$") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )

                        OutlinedTextField(
                            value = defaultAdvanceText,
                            onValueChange = { defaultAdvanceText = it },
                            label = { Text("Default Advance ($currencySymbol)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = taxRateText,
                            onValueChange = { taxRateText = it },
                            label = { Text("Sales Tax Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )

                        OutlinedTextField(
                            value = serviceChargeText,
                            onValueChange = { serviceChargeText = it },
                            label = { Text("Service Gratuity (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = stationName,
                            onValueChange = { stationName = it },
                            label = { Text("Bar Station Name") },
                            placeholder = { Text("Main Bar Station 1") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )

                        OutlinedTextField(
                            value = bartenderName,
                            onValueChange = { bartenderName = it },
                            label = { Text("Bartender In-Charge") },
                            placeholder = { Text("Gary Murambiwa") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun LogoStyleOption(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF261D2B) else BarDarkSurface)
            .border(
                1.dp,
                if (isSelected) SuccubusRed else BarBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .border(2.dp, if (isSelected) SuccubusRed else TextMuted, RoundedCornerShape(9.dp))
                .padding(3.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SuccubusRed)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                color = if (isSelected) SpeakeasyGoldLight else TextIvory,
                fontSize = 12.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
