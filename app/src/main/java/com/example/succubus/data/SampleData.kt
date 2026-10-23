package com.example.succubus.data

import com.example.succubus.model.BarRecipe
import com.example.succubus.model.BarTab
import com.example.succubus.model.PaymentRecord
import com.example.succubus.model.PaymentType
import com.example.succubus.model.Product
import com.example.succubus.model.ProductCategory
import com.example.succubus.model.RecipeIngredient
import com.example.succubus.model.TabOrderItem
import com.example.succubus.model.TabStatus

object SampleData {
    val initialProducts = listOf(
        // Signature Cocktails
        Product(
            id = "cocktail-1",
            name = "Succubus Temptation",
            category = ProductCategory.COCKTAILS,
            price = 16.0,
            costPrice = 3.80,
            stock = 45,
            minStockThreshold = 10,
            unit = "Cocktail",
            volume = "Coupe",
            sku = "SUC-SIG-01",
            description = "Spiced dark rum, blackberry liqueur, blood orange, flamed rosemary garnish"
        ),
        Product(
            id = "cocktail-2",
            name = "Crimson Velvet",
            category = ProductCategory.COCKTAILS,
            price = 15.0,
            costPrice = 3.20,
            stock = 38,
            minStockThreshold = 10,
            unit = "Cocktail",
            volume = "Nick & Nora",
            sku = "SUC-SIG-02",
            description = "Bourbon, sweet vermouth, ruby port, aromatic spiced bitters"
        ),
        Product(
            id = "cocktail-3",
            name = "Smoked Old Fashioned",
            category = ProductCategory.COCKTAILS,
            price = 18.0,
            costPrice = 4.10,
            stock = 25,
            minStockThreshold = 8,
            unit = "Cocktail",
            volume = "Rocks Glass",
            sku = "SUC-CLA-01",
            description = "Aged rye whiskey, demerara syrup, Angostura bitters, charred oak smoke"
        ),
        Product(
            id = "cocktail-4",
            name = "Nightlife Espresso Martini",
            category = ProductCategory.COCKTAILS,
            price = 15.0,
            costPrice = 2.90,
            stock = 50,
            minStockThreshold = 12,
            unit = "Cocktail",
            volume = "Coupe",
            sku = "SUC-CLA-02",
            description = "Vodka, fresh brewed double espresso, Kahlúa, Madagascar vanilla"
        ),
        Product(
            id = "cocktail-5",
            name = "Spicy Mezcalita",
            category = ProductCategory.COCKTAILS,
            price = 16.0,
            costPrice = 3.60,
            stock = 18,
            minStockThreshold = 10,
            unit = "Cocktail",
            volume = "Rocks Glass",
            sku = "SUC-SIG-03",
            description = "Artisanal Mezcal, Cointreau, fresh lime, habanero agave, smoked black salt rim"
        ),
        Product(
            id = "cocktail-6",
            name = "French 75 Royal",
            category = ProductCategory.COCKTAILS,
            price = 17.0,
            costPrice = 4.50,
            stock = 8, // Low stock on purpose
            minStockThreshold = 10,
            unit = "Cocktail",
            volume = "Flute",
            sku = "SUC-CLA-03",
            description = "Botanical gin, fresh lemon juice, simple syrup, Brut Champagne float"
        ),

        // Spirits & Whiskey Bottles / Pours
        Product(
            id = "spirit-1",
            name = "Macallan 12 Double Cask",
            category = ProductCategory.SPIRITS,
            price = 18.0,
            costPrice = 6.20,
            stock = 6, // Low stock
            minStockThreshold = 8,
            unit = "Pour (50ml)",
            volume = "700ml Bottle",
            sku = "SPR-WHS-01",
            description = "Highland single malt scotch whisky"
        ),
        Product(
            id = "spirit-2",
            name = "Don Julio 1942 Añejo",
            category = ProductCategory.SPIRITS,
            price = 32.0,
            costPrice = 12.0,
            stock = 4, // Low stock
            minStockThreshold = 5,
            unit = "Pour (50ml)",
            volume = "750ml Bottle",
            sku = "SPR-TEQ-01",
            description = "Ultra-premium small-batch tequila"
        ),
        Product(
            id = "spirit-3",
            name = "Grey Goose Vodka",
            category = ProductCategory.SPIRITS,
            price = 12.0,
            costPrice = 3.50,
            stock = 14,
            minStockThreshold = 10,
            unit = "Pour (50ml)",
            volume = "1000ml Bottle",
            sku = "SPR-VOD-01",
            description = "French distilled winter wheat vodka"
        ),
        Product(
            id = "spirit-4",
            name = "Hendrick's Botanical Gin",
            category = ProductCategory.SPIRITS,
            price = 13.0,
            costPrice = 3.80,
            stock = 16,
            minStockThreshold = 8,
            unit = "Pour (50ml)",
            volume = "700ml Bottle",
            sku = "SPR-GIN-01",
            description = "Infused with cucumber and rose petal essence"
        ),

        // Wine & Champagne
        Product(
            id = "wine-1",
            name = "Veuve Clicquot Yellow Label Brut",
            category = ProductCategory.WINES,
            price = 125.0,
            costPrice = 48.0,
            stock = 9,
            minStockThreshold = 6,
            unit = "Bottle",
            volume = "750ml",
            sku = "WIN-CHM-01",
            description = "Iconic French Champagne, crisp acidity, fine persistent mousse"
        ),
        Product(
            id = "wine-2",
            name = "Caymus Napa Valley Cabernet",
            category = ProductCategory.WINES,
            price = 160.0,
            costPrice = 65.0,
            stock = 11,
            minStockThreshold = 5,
            unit = "Bottle",
            volume = "750ml",
            sku = "WIN-RED-01",
            description = "Rich, dark fruit, cocoa, and velvet tannins"
        ),
        Product(
            id = "wine-3",
            name = "Whispering Angel Rosé",
            category = ProductCategory.WINES,
            price = 68.0,
            costPrice = 24.0,
            stock = 22,
            minStockThreshold = 8,
            unit = "Bottle",
            volume = "750ml",
            sku = "WIN-ROS-01",
            description = "Côtes de Provence, pale pink, peach and mineral notes"
        ),

        // Beer & Cider
        Product(
            id = "beer-1",
            name = "Stella Artois Draft",
            category = ProductCategory.BEERS,
            price = 8.5,
            costPrice = 2.10,
            stock = 65,
            minStockThreshold = 20,
            unit = "Pint (500ml)",
            volume = "Keg",
            sku = "BER-DFT-01",
            description = "Crisp European pilsner lager"
        ),
        Product(
            id = "beer-2",
            name = "Guinness Draught Stout",
            category = ProductCategory.BEERS,
            price = 9.0,
            costPrice = 2.40,
            stock = 42,
            minStockThreshold = 15,
            unit = "Pint (500ml)",
            volume = "Keg",
            sku = "BER-DFT-02",
            description = "Irish dry stout with creamy nitrogen head"
        ),
        Product(
            id = "beer-3",
            name = "Corona Extra",
            category = ProductCategory.BEERS,
            price = 7.5,
            costPrice = 1.80,
            stock = 48,
            minStockThreshold = 24,
            unit = "Bottle",
            volume = "330ml",
            sku = "BER-BTL-01",
            description = "Mexican pale lager served with fresh lime wedge"
        ),

        // Shots & Shooters
        Product(
            id = "shot-1",
            name = "Succubus Devil Kiss Shot",
            category = ProductCategory.SHOTS,
            price = 8.0,
            costPrice = 1.90,
            stock = 80,
            minStockThreshold = 20,
            unit = "Shot (30ml)",
            volume = "Layered",
            sku = "SHT-SUC-01",
            description = "Grenadine, spiced rum, and flaming cinnamon liquor"
        ),
        Product(
            id = "shot-2",
            name = "B-52 Layered Shooter",
            category = ProductCategory.SHOTS,
            price = 9.0,
            costPrice = 2.20,
            stock = 60,
            minStockThreshold = 15,
            unit = "Shot (30ml)",
            volume = "Layered",
            sku = "SHT-CLA-01",
            description = "Kahlúa, Baileys Irish Cream, Grand Marnier"
        ),

        // Bar Bites & Food
        Product(
            id = "food-1",
            name = "Truffle Parmesan Fries",
            category = ProductCategory.KITCHEN,
            price = 12.0,
            costPrice = 2.50,
            stock = 35,
            minStockThreshold = 10,
            unit = "Serving",
            volume = "Plate",
            sku = "KIT-TAP-01",
            description = "Crispy hand-cut fries, white truffle oil, shaved parmesan, garlic aioli"
        ),
        Product(
            id = "food-2",
            name = "Wagyu Beef Sliders (3pcs)",
            category = ProductCategory.KITCHEN,
            price = 19.0,
            costPrice = 5.50,
            stock = 24,
            minStockThreshold = 8,
            unit = "Plate",
            volume = "3 pieces",
            sku = "KIT-TAP-02",
            description = "Caramelized onion jam, aged cheddar, brioche buns, truffle mayo"
        ),
        Product(
            id = "food-3",
            name = "Artisan Charcuterie Board",
            category = ProductCategory.KITCHEN,
            price = 28.0,
            costPrice = 9.00,
            stock = 15,
            minStockThreshold = 5,
            unit = "Board",
            volume = "For 2-3",
            sku = "KIT-TAP-03",
            description = "Prosciutto di Parma, Spanish chorizo, truffle pecorino, brie, fig jam, crackers"
        ),

        // Mixers & Soft Drinks
        Product(
            id = "mixer-1",
            name = "Fever-Tree Mediterranean Tonic",
            category = ProductCategory.MIXERS,
            price = 4.5,
            costPrice = 1.10,
            stock = 45,
            minStockThreshold = 20,
            unit = "Bottle",
            volume = "200ml",
            sku = "MIX-TON-01",
            description = "Premium tonic with essential herbal oils"
        ),
        Product(
            id = "mixer-2",
            name = "San Pellegrino Sparkling Water",
            category = ProductCategory.MIXERS,
            price = 6.0,
            costPrice = 1.50,
            stock = 30,
            minStockThreshold = 15,
            unit = "Bottle",
            volume = "750ml",
            sku = "MIX-WTR-01",
            description = "Italian sparkling mineral water"
        )
    )

    val initialRecipes = listOf(
        BarRecipe(
            id = "rec-1",
            name = "Succubus Temptation",
            category = "Signature House Cocktail",
            glassware = "Coupe Glass (Chilled)",
            ice = "Neat / Chilled",
            method = "Hard Shake & Double Fine Strain",
            garnish = "Sprig of Fresh Rosemary (Flamed with Bar Torch) & Black Salt Half-Rim",
            prepTime = "2.5 mins",
            difficulty = "Artisanal",
            flavorProfile = listOf("Velvety", "Smoky Herb", "Blackberry Tart", "Dark Spice"),
            ingredients = listOf(
                RecipeIngredient("Spiced Dark Rum (Kraken or Sailor Jerry)", "45 ml"),
                RecipeIngredient("Crème de Mûre (Blackberry Liqueur)", "20 ml"),
                RecipeIngredient("Fresh Blood Orange Juice", "25 ml"),
                RecipeIngredient("Fresh Lime Juice", "15 ml"),
                RecipeIngredient("Demerara Sugar Syrup (2:1)", "10 ml"),
                RecipeIngredient("Angostura Bitters", "2 dashes")
            ),
            instructions = listOf(
                "Chill coupe glass in bar freezer or with ice water while preparing.",
                "Rim one half of the chilled glass with lime and lightly dust with black lava salt.",
                "Add dark rum, blackberry liqueur, blood orange, lime, and syrup into shaker tin.",
                "Fill tin completely with large solid ice cubes. Shake vigorously for 12 seconds.",
                "Double fine strain through a Hawthorne and tea mesh strainer into the chilled coupe.",
                "Gently smack fresh rosemary sprig to express oils, flame tip with mini torch, and perch on rim."
            ),
            menuPrice = 16.0,
            bartenderTip = "For VIP guests, add a mist of Absinthe before pouring."
        ),
        BarRecipe(
            id = "rec-2",
            name = "Crimson Velvet",
            category = "Signature Cocktail",
            glassware = "Nick & Nora Glass",
            ice = "Neat",
            method = "Stir with Bar Spoon for 30 Seconds & Julep Strain",
            garnish = "Brandied Maraschino Cherry & Orange Peel Zest",
            prepTime = "2 mins",
            difficulty = "Medium",
            flavorProfile = listOf("Silky", "Rich Bourbon", "Ruby Port Sweetness", "Complex Herbal"),
            ingredients = listOf(
                RecipeIngredient("Bourbon Whiskey (Woodford Reserve or Maker's)", "50 ml"),
                RecipeIngredient("Sweet Italian Vermouth (Carpano Antica)", "25 ml"),
                RecipeIngredient("Ruby Port Wine", "15 ml"),
                RecipeIngredient("Campari", "10 ml"),
                RecipeIngredient("Orange Bitters", "2 dashes")
            ),
            instructions = listOf(
                "Add all spirits and bitters into a heavy crystal mixing glass.",
                "Fill 3/4 full with cracked clean ice cubes.",
                "Stir smoothly with Japanese bar spoon for 30 seconds until ice-cold and diluted perfectly.",
                "Strain into a chilled Nick & Nora glass using a Julep strainer.",
                "Express the essential oils from a wide orange peel over the surface, drop in a Maraschino cherry."
            ),
            menuPrice = 15.0,
            bartenderTip = "Stir clockwise with smooth wrist motion to avoid aerating the drink."
        ),
        BarRecipe(
            id = "rec-3",
            name = "Smoked Old Fashioned",
            category = "Classic Speakeasy",
            glassware = "Heavy Rocks Glass",
            ice = "Single King Clear Ice Block (5x5cm)",
            method = "Built & Smoked under Glass Cloche",
            garnish = "Wide Orange Swath & Maraschino Cherry",
            prepTime = "3 mins",
            difficulty = "Artisanal",
            flavorProfile = listOf("Oak Smoke", "Caramelized Sugar", "Aromatic Bitters", "Bold Rye"),
            ingredients = listOf(
                RecipeIngredient("Rye Whiskey (Bulleit Rye or WhistlePig)", "60 ml"),
                RecipeIngredient("Demerara Syrup", "10 ml"),
                RecipeIngredient("Angostura Bitters", "3 dashes"),
                RecipeIngredient("Orange Bitters", "1 dash"),
                RecipeIngredient("Charred Oak Wood Chips", "Pinch (for smoke cloche)")
            ),
            instructions = listOf(
                "Place crystal rocks glass on smoke board. Light oak wood chips in smoking gun.",
                "Fill smoking cloche with dense aromatic smoke and cover glass for 45 seconds.",
                "In separate mixing beaker, stir rye whiskey, syrup, and bitters with ice.",
                "Lift cloche with theatrical release for guest. Place single crystal clear King Cube inside glass.",
                "Strain cocktail over King Cube, express orange peel across the rim, and rest on top."
            ),
            menuPrice = 18.0,
            bartenderTip = "Guests love photographing the cloche smoke release! Keep glasses dry before smoking."
        ),
        BarRecipe(
            id = "rec-4",
            name = "Nightlife Espresso Martini",
            category = "Late Night Classic",
            glassware = "Coupe Glass",
            ice = "Neat (Aerated Froth)",
            method = "Vigorous Aerated Shake",
            garnish = "Three Roasted Coffee Beans (Health, Wealth, Happiness)",
            prepTime = "2 mins",
            difficulty = "Easy",
            flavorProfile = listOf("Roasted Coffee", "Velvety Crema", "Sweet Vanilla", "Crisp Vodka"),
            ingredients = listOf(
                RecipeIngredient("Vodka (Grey Goose or Ketel One)", "45 ml"),
                RecipeIngredient("Kahlúa Coffee Liqueur", "25 ml"),
                RecipeIngredient("Fresh Hot Espresso Shot", "30 ml"),
                RecipeIngredient("Vanilla Sugar Syrup", "10 ml")
            ),
            instructions = listOf(
                "Pull a fresh double espresso right before shaking so the natural oils produce dense crema foam.",
                "Add all ingredients to shaker tin with high-density ice cubes.",
                "Shake as hard and fast as possible for 15 seconds to emulsify the crema.",
                "Double fine strain immediately into a chilled coupe glass.",
                "Watch the foam settle into a thick velvet head. Float three coffee beans in the center."
            ),
            menuPrice = 15.0,
            bartenderTip = "Fresh espresso is key. Cold brew cannot produce the authentic crema head."
        ),
        BarRecipe(
            id = "rec-5",
            name = "Succubus Devil Kiss Shot",
            category = "Nightclub Shooter",
            glassware = "Heavy Shot Glass",
            ice = "Neat",
            method = "Layered Bar Spoon Float",
            garnish = "Cinnamon Sugar Rim & Flaming Float",
            prepTime = "1.5 mins",
            difficulty = "Medium",
            flavorProfile = listOf("Sweet Pomegranate", "Warm Spiced Rum", "Fiery Cinnamon"),
            ingredients = listOf(
                RecipeIngredient("Pomegranate Grenadine", "10 ml"),
                RecipeIngredient("Spiced Dark Rum", "15 ml"),
                RecipeIngredient("Overproof Cinnamon Spirit (Fireball or Overproof Rum)", "15 ml")
            ),
            instructions = listOf(
                "Pour grenadine directly to bottom of shot glass.",
                "Invert bar spoon against inside edge of glass; gently float spiced rum on top.",
                "Float the overproof cinnamon spirit on top layer.",
                "Light gently for celebratory toasts, blow out right before consuming shot."
            ),
            menuPrice = 8.0,
            bartenderTip = "Always ensure surface is clear of spills before lighting."
        )
    )

    val initialTabs = listOf(
        BarTab(
            id = "tab-101",
            customerName = "Gary Murambiwa",
            customerPhone = "+1 555-019-8234",
            tableName = "VIP Lounge 1",
            openedAt = System.currentTimeMillis() - 7200000, // 2 hours ago
            items = listOf(
                TabOrderItem("item-1", "cocktail-1", "Succubus Temptation", 16.0, 2),
                TabOrderItem("item-2", "wine-1", "Veuve Clicquot Yellow Label Brut", 125.0, 1),
                TabOrderItem("item-3", "food-3", "Artisan Charcuterie Board", 28.0, 1)
            ),
            advancePaid = 200.0, // Advance paid upfront by Gary
            advanceMethod = "Card",
            status = TabStatus.OPEN,
            notes = "Celebrating anniversary. Prefers flamed rosemary.",
            payments = listOf(
                PaymentRecord(
                    id = "pay-1",
                    tabId = "tab-101",
                    customerName = "Gary Murambiwa",
                    amount = 200.0,
                    method = "Card",
                    type = PaymentType.ADVANCE_DEPOSIT,
                    timestamp = System.currentTimeMillis() - 7200000,
                    notes = "Advance deposit to open VIP Lounge tab"
                )
            )
        ),
        BarTab(
            id = "tab-102",
            customerName = "Sarah Jenkins",
            customerPhone = "+1 555-014-9912",
            tableName = "Bar Stool 4",
            openedAt = System.currentTimeMillis() - 3600000, // 1 hour ago
            items = listOf(
                TabOrderItem("item-4", "cocktail-4", "Nightlife Espresso Martini", 15.0, 2),
                TabOrderItem("item-5", "cocktail-3", "Smoked Old Fashioned", 18.0, 1),
                TabOrderItem("item-6", "food-1", "Truffle Parmesan Fries", 12.0, 1)
            ),
            advancePaid = 50.0, // $50 advance deposit
            advanceMethod = "Cash",
            status = TabStatus.OPEN,
            notes = "Sitting at corner bar. Waiting for friends.",
            payments = listOf(
                PaymentRecord(
                    id = "pay-2",
                    tabId = "tab-102",
                    customerName = "Sarah Jenkins",
                    amount = 50.0,
                    method = "Cash",
                    type = PaymentType.ADVANCE_DEPOSIT,
                    timestamp = System.currentTimeMillis() - 3600000,
                    notes = "Advance deposit for bar stool tab"
                )
            )
        ),
        BarTab(
            id = "tab-103",
            customerName = "Marcus Vance",
            customerPhone = "+1 555-018-4421",
            tableName = "Table 7 (Patio)",
            openedAt = System.currentTimeMillis() - 1800000, // 30 min ago
            items = listOf(
                TabOrderItem("item-7", "beer-1", "Stella Artois Draft", 8.5, 4),
                TabOrderItem("item-8", "food-2", "Wagyu Beef Sliders (3pcs)", 19.0, 2)
            ),
            advancePaid = 100.0, // $100 advance
            advanceMethod = "Transfer",
            status = TabStatus.OPEN,
            notes = "Corporate table account",
            payments = listOf(
                PaymentRecord(
                    id = "pay-3",
                    tabId = "tab-103",
                    customerName = "Marcus Vance",
                    amount = 100.0,
                    method = "Transfer",
                    type = PaymentType.ADVANCE_DEPOSIT,
                    timestamp = System.currentTimeMillis() - 1800000,
                    notes = "Advance wire deposit"
                )
            )
        )
    )

    val availableTables = listOf(
        "Bar Stool 1", "Bar Stool 2", "Bar Stool 3", "Bar Stool 4", "Bar Stool 5",
        "Table 1", "Table 2", "Table 3", "Table 4", "Table 5", "Table 6", "Table 7 (Patio)",
        "VIP Lounge 1", "VIP Lounge 2", "VIP Booth 3", "Private Terrace"
    )
}
