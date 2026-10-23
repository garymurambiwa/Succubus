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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
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
import com.example.succubus.model.BarRecipe
import com.example.succubus.model.BarTab
import com.example.succubus.model.Product
import com.example.succubus.model.ProductCategory
import com.example.succubus.model.RecipeIngredient
import com.example.succubus.model.TabStatus
import com.example.succubus.ui.theme.BarBlack
import com.example.succubus.ui.theme.BarBorder
import com.example.succubus.ui.theme.BarCardBg
import com.example.succubus.ui.theme.BarCardElevated
import com.example.succubus.ui.theme.BarDarkSurface
import com.example.succubus.ui.theme.SpeakeasyGold
import com.example.succubus.ui.theme.SpeakeasyGoldLight
import com.example.succubus.ui.theme.StatusSuccess
import com.example.succubus.ui.theme.SuccubusRed
import com.example.succubus.ui.theme.TextIvory
import com.example.succubus.ui.theme.TextMuted
import com.example.succubus.ui.theme.TextSubtle
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val recipes by repository.recipes.collectAsState()
    val tabs by repository.tabs.collectAsState()
    val settings by repository.settings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var showAddRecipeDialog by remember { mutableStateOf(false) }
    var recipeToAddToTab by remember { mutableStateOf<BarRecipe?>(null) }

    val categories = listOf("All", "Signature House Cocktail", "Signature Cocktail", "Classic Speakeasy", "Late Night Classic", "Nightclub Shooter")

    val filteredRecipes = recipes.filter { r ->
        val matchesQuery = searchQuery.isBlank() ||
                r.name.contains(searchQuery, ignoreCase = true) ||
                r.ingredients.any { it.name.contains(searchQuery, ignoreCase = true) } ||
                r.flavorProfile.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesCategory = selectedCategory == null || selectedCategory == "All" || r.category == selectedCategory

        matchesQuery && matchesCategory
    }

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
                    text = "COCKTAIL SPECS & RECIPES",
                    color = SpeakeasyGoldLight,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "${recipes.size} Curated Recipes • Bartender Specs & Build Guides",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { showAddRecipeDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccubusRed,
                    contentColor = TextIvory
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add Recipe", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search ingredients, whiskey, rum, flavor...", color = TextSubtle, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SpeakeasyGold, modifier = Modifier.size(18.dp)) },
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

        // Category filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = (selectedCategory == null && cat == "All") || selectedCategory == cat
                CategoryChip(
                    label = cat,
                    isSelected = isSelected,
                    onClick = { selectedCategory = if (cat == "All") null else cat }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recipes List
        if (filteredRecipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = TextSubtle, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No recipes match your search", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredRecipes, key = { it.id }) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        currency = settings.currencySymbol,
                        onRingUpToTab = { recipeToAddToTab = recipe }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // --- DIALOG: RING UP RECIPE TO ACTIVE TAB ---
    if (recipeToAddToTab != null) {
        val rec = recipeToAddToTab!!
        val activeTabs = tabs.filter { it.status == TabStatus.OPEN }

        AlertDialog(
            onDismissRequest = { recipeToAddToTab = null },
            containerColor = BarCardElevated,
            title = {
                Text(
                    text = "Ring Up '${rec.name}' to Tab",
                    color = SpeakeasyGoldLight,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Price: ${settings.currencySymbol}${String.format("%.2f", rec.menuPrice)}",
                        color = SpeakeasyGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Select which open tab to charge this drink to:", color = TextMuted, fontSize = 12.sp)

                    if (activeTabs.isEmpty()) {
                        Text(
                            text = "No open tabs currently active. Open a tab in the 'Tabs & Credit' section first.",
                            color = StatusSuccess,
                            fontSize = 12.sp
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(activeTabs) { tab ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BarDarkSurface)
                                        .clickable {
                                            val prod = Product(
                                                id = rec.associatedProductId ?: ("cocktail-" + rec.id),
                                                name = rec.name,
                                                category = ProductCategory.COCKTAILS,
                                                price = rec.menuPrice,
                                                costPrice = 3.50,
                                                stock = 100
                                            )
                                            repository.addItemToTab(tab.id, prod, 1, "Prepared from recipe specs")
                                            recipeToAddToTab = null
                                        }
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(tab.customerName, color = TextIvory, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(tab.tableName, color = SpeakeasyGoldLight, fontSize = 11.sp)
                                    }
                                    Text("Add Drink", color = SuccubusRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { recipeToAddToTab = null }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // --- DIALOG: ADD NEW CUSTOM RECIPE ---
    if (showAddRecipeDialog) {
        NewRecipeDialog(
            currency = settings.currencySymbol,
            onDismiss = { showAddRecipeDialog = false },
            onSave = { newRec ->
                repository.addRecipe(newRec)
                showAddRecipeDialog = false
            }
        )
    }
}

@Composable
fun RecipeCard(
    recipe: BarRecipe,
    currency: String,
    onRingUpToTab: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BarCardBg),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, SpeakeasyGold.copy(alpha = 0.35f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Category tag and Menu Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF261D2C))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = recipe.category.uppercase(),
                        color = SpeakeasyGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "$currency${String.format("%.2f", recipe.menuPrice)}",
                    color = SpeakeasyGoldLight,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cocktail Title
            Text(
                text = recipe.name,
                color = TextIvory,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            // Flavor profile chips
            if (recipe.flavorProfile.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    recipe.flavorProfile.forEach { flavor ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(BarDarkSurface)
                                .border(0.6.dp, BarBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(flavor, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Glassware, Ice, Method badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(BarDarkSurface)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("GLASSWARE", color = TextSubtle, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(recipe.glassware, color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("ICE", color = TextSubtle, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(recipe.ice, color = TextIvory, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("METHOD", color = TextSubtle, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                    Text(recipe.method, color = SpeakeasyGold, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ingredients List
            Text("INGREDIENTS & SPECS:", color = SpeakeasyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            recipe.ingredients.forEach { ing ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "• ${ing.name}", color = TextIvory, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text(text = ing.amount, color = SpeakeasyGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Garnish
            Text(
                text = "Garnish: ${recipe.garnish}",
                color = TextMuted,
                fontSize = 11.5.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            // Instructions Accordion / preview
            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = BarBorder, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text("METHOD / STEPS:", color = TextSubtle, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            recipe.instructions.forEachIndexed { i, step ->
                Text(
                    text = "${i + 1}. $step",
                    color = TextIvory.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }

            if (recipe.bartenderTip.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF261D1A))
                        .border(0.8.dp, Color(0xFF5A3E26), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "💡 Bartender Pro Tip: ${recipe.bartenderTip}",
                        color = SpeakeasyGoldLight,
                        fontSize = 10.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Ring Up to Tab" Button
            Button(
                onClick = onRingUpToTab,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccubusRed,
                    contentColor = TextIvory
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LocalBar, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ring Up this Cocktail to Customer Tab", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun NewRecipeDialog(
    currency: String,
    onDismiss: () -> Unit,
    onSave: (BarRecipe) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Signature Cocktail") }
    var glassware by remember { mutableStateOf("Coupe Glass") }
    var ice by remember { mutableStateOf("Neat / Chilled") }
    var method by remember { mutableStateOf("Shake & Strain") }
    var garnish by remember { mutableStateOf("") }
    var ingredientsText by remember { mutableStateOf("Bourbon (50ml)\nSweet Vermouth (25ml)\nBitters (2 dashes)") }
    var instructionsText by remember { mutableStateOf("1. Add ingredients to mixing glass with ice\n2. Stir for 30s\n3. Strain into glass") }
    var priceText by remember { mutableStateOf("15.00") }
    var tip by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BarCardElevated,
        title = {
            Text("Add Custom Bar Recipe", color = SpeakeasyGoldLight, fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Cocktail / Drink Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category (Signature, Classic, Shot)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = glassware,
                            onValueChange = { glassware = it },
                            label = { Text("Glassware") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Price ($currency)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ice,
                            onValueChange = { ice = it },
                            label = { Text("Ice Specification") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                        OutlinedTextField(
                            value = method,
                            onValueChange = { method = it },
                            label = { Text("Method (Shake/Stir)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = customDialogTextFieldColors()
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = garnish,
                        onValueChange = { garnish = it },
                        label = { Text("Garnish") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customDialogTextFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = ingredientsText,
                        onValueChange = { ingredientsText = it },
                        label = { Text("Ingredients (One per line)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        colors = customDialogTextFieldColors()
                    )
                }
                item {
                    OutlinedTextField(
                        value = instructionsText,
                        onValueChange = { instructionsText = it },
                        label = { Text("Step-by-Step Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4,
                        colors = customDialogTextFieldColors()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val parsedIngredients = ingredientsText.lines()
                            .filter { it.isNotBlank() }
                            .map { line ->
                                val parts = line.split("(")
                                val ingName = parts[0].trim()
                                val amount = if (parts.size > 1) parts[1].replace(")", "").trim() else ""
                                RecipeIngredient(ingName, amount)
                            }

                        val parsedSteps = instructionsText.lines().filter { it.isNotBlank() }
                        val price = priceText.toDoubleOrNull() ?: 15.0

                        val newRec = BarRecipe(
                            id = "rec-" + UUID.randomUUID().toString().take(6),
                            name = name,
                            category = category,
                            glassware = glassware,
                            ice = ice,
                            method = method,
                            garnish = garnish,
                            ingredients = parsedIngredients,
                            instructions = parsedSteps,
                            menuPrice = price,
                            bartenderTip = tip
                        )
                        onSave(newRec)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SuccubusRed)
            ) {
                Text("Save Recipe", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextMuted)
            }
        }
    )
}
