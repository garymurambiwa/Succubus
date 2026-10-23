package com.example.succubus.model

data class RecipeIngredient(
    val name: String,
    val amount: String, // e.g. "50 ml", "25 ml", "3 dashes"
    val isOptional: Boolean = false
)

data class BarRecipe(
    val id: String,
    val name: String,
    val category: String, // "Signature", "Classic", "Shooter", "Mocktail"
    val glassware: String, // "Coupe Glass", "Rocks Glass (King Cube)", "Highball", "Flute", "Shot Glass"
    val ice: String, // "Clear Block Cube", "Cubed Ice", "Crushed Ice", "Neat / No Ice"
    val method: String, // "Shake & Double Strain", "Stir over Ice & Strain", "Build over Ice", "Muddle & Churn"
    val garnish: String, // "Flamed Orange Twist", "Luxardo Cherry", "Fresh Mint Sprig", "Salt / Chili Rim"
    val prepTime: String = "2 mins",
    val difficulty: String = "Medium",
    val flavorProfile: List<String> = emptyList(), // e.g. "Smoky", "Sweet", "Citrus"
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val menuPrice: Double = 14.0,
    val bartenderTip: String = "",
    val associatedProductId: String? = null
)
