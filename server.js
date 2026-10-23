const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = 3000;
const HOST = "0.0.0.0";

// In-Memory Data Store (Initialized with same rich data as Android app)
let settings = {
  venueName: "SUCCUBUS",
  tagline: "Cocktails & Nightlife • Private Events • Special Experiences",
  logoStyle: "ORIGINAL_BANNER",
  currencySymbol: "$",
  taxRatePercent: 8.5,
  serviceChargePercent: 10.0,
  defaultAdvanceDeposit: 50.0,
  stationName: "Main Bar - Station 1",
  bartenderName: "Gary (Bar Manager)"
};

let products = [
  {
    id: "cocktail-1",
    name: "Succubus Temptation",
    category: "Cocktails",
    price: 16.0,
    costPrice: 3.80,
    stock: 45,
    minStockThreshold: 10,
    unit: "Cocktail",
    volume: "Coupe",
    sku: "SUC-SIG-01",
    description: "Spiced dark rum, blackberry liqueur, blood orange, flamed rosemary garnish"
  },
  {
    id: "cocktail-2",
    name: "Crimson Velvet",
    category: "Cocktails",
    price: 15.0,
    costPrice: 3.20,
    stock: 38,
    minStockThreshold: 10,
    unit: "Cocktail",
    volume: "Nick & Nora",
    sku: "SUC-SIG-02",
    description: "Bourbon, sweet vermouth, ruby port, aromatic spiced bitters"
  },
  {
    id: "cocktail-3",
    name: "Smoked Old Fashioned",
    category: "Cocktails",
    price: 18.0,
    costPrice: 4.10,
    stock: 25,
    minStockThreshold: 8,
    unit: "Cocktail",
    volume: "Rocks Glass",
    sku: "SUC-CLA-01",
    description: "Aged rye whiskey, demerara syrup, Angostura bitters, charred oak smoke"
  },
  {
    id: "cocktail-4",
    name: "Nightlife Espresso Martini",
    category: "Cocktails",
    price: 15.0,
    costPrice: 2.90,
    stock: 50,
    minStockThreshold: 12,
    unit: "Cocktail",
    volume: "Coupe",
    sku: "SUC-CLA-02",
    description: "Vodka, fresh brewed double espresso, Kahlúa, Madagascar vanilla"
  },
  {
    id: "spirit-1",
    name: "Macallan 12 Double Cask",
    category: "Spirits & Whiskey",
    price: 18.0,
    costPrice: 6.20,
    stock: 6,
    minStockThreshold: 8,
    unit: "Pour (50ml)",
    volume: "700ml Bottle",
    sku: "SPR-WHS-01",
    description: "Highland single malt scotch whisky"
  },
  {
    id: "wine-1",
    name: "Veuve Clicquot Yellow Label Brut",
    category: "Wine & Champagne",
    price: 125.0,
    costPrice: 48.0,
    stock: 9,
    minStockThreshold: 6,
    unit: "Bottle",
    volume: "750ml",
    sku: "WIN-CHM-01",
    description: "Iconic French Champagne, crisp acidity, fine persistent mousse"
  },
  {
    id: "beer-1",
    name: "Stella Artois Draft",
    category: "Beer & Cider",
    price: 8.5,
    costPrice: 2.10,
    stock: 65,
    minStockThreshold: 20,
    unit: "Pint (500ml)",
    volume: "Draft Keg",
    sku: "BER-DFT-01",
    description: "Crisp European pilsner lager"
  },
  {
    id: "shot-1",
    name: "Succubus Devil Kiss Shot",
    category: "Shots & Shooters",
    price: 8.0,
    costPrice: 1.90,
    stock: 80,
    minStockThreshold: 20,
    unit: "Shot (30ml)",
    volume: "Layered",
    sku: "SHT-SUC-01",
    description: "Grenadine, spiced rum, and flaming cinnamon liquor"
  },
  {
    id: "food-1",
    name: "Truffle Parmesan Fries",
    category: "Bar Bites & Food",
    price: 12.0,
    costPrice: 2.50,
    stock: 35,
    minStockThreshold: 10,
    unit: "Serving",
    volume: "Plate",
    sku: "KIT-TAP-01",
    description: "Crispy hand-cut fries, white truffle oil, shaved parmesan, garlic aioli"
  }
];

let tabs = [
  {
    id: "tab-101",
    customerName: "Gary Murambiwa",
    customerPhone: "+1 555-019-8234",
    tableName: "VIP Lounge 1",
    openedAt: Date.now() - 7200000,
    advancePaid: 200.0,
    advanceMethod: "Card",
    status: "OPEN",
    notes: "Celebrating anniversary. Prefers flamed rosemary.",
    items: [
      { id: "item-1", productId: "cocktail-1", name: "Succubus Temptation", unitPrice: 16.0, quantity: 2 },
      { id: "item-2", productId: "wine-1", name: "Veuve Clicquot Yellow Label Brut", unitPrice: 125.0, quantity: 1 }
    ]
  },
  {
    id: "tab-102",
    customerName: "Sarah Jenkins",
    customerPhone: "+1 555-014-9912",
    tableName: "Bar Stool 4",
    openedAt: Date.now() - 3600000,
    advancePaid: 50.0,
    advanceMethod: "Cash",
    status: "OPEN",
    notes: "Sitting at corner bar. Waiting for friends.",
    items: [
      { id: "item-3", productId: "cocktail-4", name: "Nightlife Espresso Martini", unitPrice: 15.0, quantity: 2 },
      { id: "item-4", productId: "food-1", name: "Truffle Parmesan Fries", unitPrice: 12.0, quantity: 1 }
    ]
  }
];

let recipes = [
  {
    id: "rec-1",
    name: "Succubus Temptation",
    category: "Signature House Cocktail",
    glassware: "Coupe Glass (Chilled)",
    ice: "Neat / Chilled",
    method: "Hard Shake & Double Fine Strain",
    garnish: "Flamed Rosemary & Black Lava Salt Rim",
    prepTime: "2.5 mins",
    ingredients: [
      "Spiced Dark Rum (45ml)",
      "Crème de Mûre / Blackberry Liqueur (20ml)",
      "Fresh Blood Orange Juice (25ml)",
      "Fresh Lime Juice (15ml)",
      "Demerara Syrup (10ml)",
      "Angostura Bitters (2 dashes)"
    ],
    instructions: [
      "Chill coupe glass in freezer.",
      "Half-rim glass with lime and black lava salt.",
      "Add all spirits and juices to shaker tin with large solid ice.",
      "Shake vigorously for 12 seconds.",
      "Double strain into coupe. Flame rosemary sprig with bar torch over the drink."
    ],
    menuPrice: 16.0,
    bartenderTip: "Mist coupe with Absinthe before pouring for VIP service."
  },
  {
    id: "rec-2",
    name: "Crimson Velvet",
    category: "Signature Cocktail",
    glassware: "Nick & Nora Glass",
    ice: "Neat",
    method: "Stir over Crystal Ice for 30 Seconds",
    garnish: "Brandied Maraschino Cherry & Orange Peel Zest",
    prepTime: "2 mins",
    ingredients: [
      "Bourbon Whiskey (50ml)",
      "Sweet Italian Vermouth (25ml)",
      "Ruby Port Wine (15ml)",
      "Campari (10ml)",
      "Orange Bitters (2 dashes)"
    ],
    instructions: [
      "Add spirits and bitters into mixing beaker with cracked ice.",
      "Stir smoothly with bar spoon for 30 seconds.",
      "Julep strain into chilled Nick & Nora glass.",
      "Express orange twist oils over the glass and drop in cherry."
    ],
    menuPrice: 15.0,
    bartenderTip: "Do not shake; gentle stirring keeps the crimson hue crystal clear."
  },
  {
    id: "rec-3",
    name: "Smoked Old Fashioned",
    category: "Classic Speakeasy",
    glassware: "Heavy Crystal Rocks",
    ice: "King Clear Ice Block (5x5cm)",
    method: "Built & Smoked under Cloche",
    garnish: "Wide Orange Swath & Luxardo Cherry",
    prepTime: "3 mins",
    ingredients: [
      "Rye Whiskey (60ml)",
      "Demerara Syrup (10ml)",
      "Angostura Bitters (3 dashes)",
      "Orange Bitters (1 dash)",
      "Charred Oak Wood Chips"
    ],
    instructions: [
      "Light oak chips in smoking gun and fill glass cloche over glass for 45 seconds.",
      "Stir whiskey and bitters with ice in mixing glass.",
      "Place King Cube in glass, strain drink, and express orange oil."
    ],
    menuPrice: 18.0,
    bartenderTip: "Release cloche table-side for dramatic aroma."
  }
];

function getHtml() {
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>${settings.venueName} - Cocktails & Nightlife Bar Management</title>
  <meta name="description" content="Succubus Bar & Restaurant Management - POS, Inventory, Tabs with Advances, and Cocktail Specs">
  <meta property="og:title" content="${settings.venueName}">
  <meta property="og:description" content="${settings.tagline}">
  <script src="https://cdn.tailwindcss.com"></script>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700;800;900&family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    body {
      font-family: 'Plus Jakarta Sans', sans-serif;
      background-color: #09070a;
      color: #f7f3ea;
      margin: 0;
      padding: 0;
      -webkit-font-smoothing: antialiased;
    }
    .font-brand {
      font-family: 'Cinzel', serif;
    }
    .gold-gradient-text {
      background: linear-gradient(135deg, #DE9B47 0%, #F7C873 50%, #DE9B47 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
    .gold-border {
      border: 1px solid rgba(222, 155, 71, 0.35);
    }
    .crimson-glow {
      box-shadow: 0 0 25px rgba(229, 21, 40, 0.25);
    }
  </style>
</head>
<body class="min-h-screen bg-[#09070A] text-[#F7F3EA] pb-24">

  <!-- TOP BRAND BANNER -->
  <header class="bg-[#0D0A0F] border-b border-[#382F40] sticky top-0 z-40 px-4 py-3 shadow-lg">
    <div class="max-w-6xl mx-auto flex items-center justify-between">
      <div class="flex items-center space-x-3">
        <!-- Succubus Devil Heart Logo -->
        <div class="relative w-10 h-10 flex items-center justify-center bg-[#151118] border border-[#DE9B47]/40 rounded-lg p-1">
          <svg class="w-8 h-8 text-[#EE1528]" viewBox="0 0 100 100" fill="none" stroke="currentColor">
            <!-- Horns -->
            <path d="M38,28 L32,16 L42,24 Z" fill="#EE1528" stroke="none" />
            <path d="M62,28 L68,16 L58,24 Z" fill="#EE1528" stroke="none" />
            <!-- Heart -->
            <path d="M50,30 C44,20 32,20 26,28 C18,38 20,54 32,66 C40,74 47,80 50,83 C53,80 60,74 68,66 C80,54 82,38 74,28 C68,20 56,20 50,30 Z" stroke="#EE1528" stroke-width="4" stroke-linecap="round" stroke-linejoin="round" />
            <!-- Tail -->
            <path d="M50,83 C48,88 52,94 60,93 C66,92 70,87 69,81" stroke="#EE1528" stroke-width="3.5" stroke-linecap="round" />
            <path d="M69,81 L74,84 L70,76 Z" fill="#EE1528" stroke="none" />
          </svg>
        </div>
        <div>
          <h1 id="brandVenueName" class="font-brand font-black text-xl tracking-[0.25em] gold-gradient-text leading-none">${settings.venueName}</h1>
          <p id="brandTagline" class="text-[10px] text-[#A69EA9] tracking-wider mt-0.5 truncate max-w-xs md:max-w-md">${settings.tagline}</p>
        </div>
      </div>

      <div class="flex items-center space-x-2">
        <span class="hidden sm:inline-block px-2.5 py-1 text-xs font-semibold rounded bg-[#1C1722] text-[#E5A93C] border border-[#382F40]">
          ${settings.stationName}
        </span>
        <a href="/download/app-debug.apk" class="px-3 py-1.5 text-xs font-bold rounded-lg bg-[#E51528] text-white hover:bg-[#FF2A3F] transition-all flex items-center space-x-1.5 shadow-md">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4"></path></svg>
          <span>Get Android APK</span>
        </a>
      </div>
    </div>
  </header>

  <!-- NAVIGATION TABS -->
  <nav class="bg-[#120E15] border-b border-[#2D2433] px-4 py-2 sticky top-[61px] z-30">
    <div class="max-w-6xl mx-auto flex space-x-2 overflow-x-auto pb-1 scrollbar-none">
      <button onclick="switchTab('tabs')" id="nav-tabs" class="nav-btn px-4 py-2 rounded-lg text-sm font-bold bg-[#E51528] text-white flex items-center space-x-2">
        <span>💳 Tabs & Advances</span>
        <span id="badge-tabs" class="bg-black/40 text-white text-[11px] px-1.5 py-0.5 rounded-full font-bold">2</span>
      </button>
      <button onclick="switchTab('inventory')" id="nav-inventory" class="nav-btn px-4 py-2 rounded-lg text-sm font-bold bg-[#1C1722] text-[#B8ADB9] hover:text-white flex items-center space-x-2">
        <span>📦 Inventory & Products</span>
        <span id="badge-low" class="bg-[#F39C12] text-black text-[10px] px-1.5 py-0.5 rounded-full font-bold">2 Low</span>
      </button>
      <button onclick="switchTab('pos')" id="nav-pos" class="nav-btn px-4 py-2 rounded-lg text-sm font-bold bg-[#1C1722] text-[#B8ADB9] hover:text-white">
        ⚡ Bar POS Terminal
      </button>
      <button onclick="switchTab('recipes')" id="nav-recipes" class="nav-btn px-4 py-2 rounded-lg text-sm font-bold bg-[#1C1722] text-[#B8ADB9] hover:text-white">
        🍸 Cocktail Specs
      </button>
      <button onclick="switchTab('settings')" id="nav-settings" class="nav-btn px-4 py-2 rounded-lg text-sm font-bold bg-[#1C1722] text-[#B8ADB9] hover:text-white">
        ⚙️ Settings & Logo
      </button>
    </div>
  </nav>

  <!-- MAIN CONTAINER -->
  <main class="max-w-6xl mx-auto p-4">

    <!-- ========================================== -->
    <!-- SECTION 1: TABS & ADVANCE CREDIT           -->
    <!-- ========================================== -->
    <section id="section-tabs" class="space-y-4">
      <!-- KPI Row -->
      <div class="grid grid-cols-2 md:grid-cols-4 gap-3">
        <div class="bg-[#1C1722] p-3.5 rounded-xl border border-[#382F40]">
          <p class="text-[11px] text-[#A69EA9] font-bold uppercase tracking-wider">Advances Held</p>
          <p id="kpi-advances" class="text-xl font-black text-[#F7C873] mt-0.5">$250.00</p>
          <p class="text-[10px] text-green-400">Pre-authorized deposits</p>
        </div>
        <div class="bg-[#1C1722] p-3.5 rounded-xl border border-[#382F40]">
          <p class="text-[11px] text-[#A69EA9] font-bold uppercase tracking-wider">Active Tab Bills</p>
          <p id="kpi-bills" class="text-xl font-black text-white mt-0.5">$216.50</p>
          <p class="text-[10px] text-[#A69EA9]">Incl. Tax & Service</p>
        </div>
        <div class="bg-[#1C1722] p-3.5 rounded-xl border border-[#382F40]">
          <p class="text-[11px] text-[#A69EA9] font-bold uppercase tracking-wider">Unsettled Debtor Balances</p>
          <p id="kpi-owed" class="text-xl font-black text-[#EE1528] mt-0.5">$0.00</p>
          <p class="text-[10px] text-[#A69EA9]">Exceeding advance</p>
        </div>
        <div class="bg-[#1C1722] p-3.5 rounded-xl border border-[#382F40] flex flex-col justify-center">
          <button onclick="openModal('modal-open-tab')" class="w-full py-2.5 px-3 rounded-lg bg-[#E51528] hover:bg-[#FF2A3F] text-white text-xs font-bold transition-all shadow-md flex items-center justify-center space-x-1">
            <span>+ Open Tab with Advance</span>
          </button>
        </div>
      </div>

      <!-- Tabs List Header -->
      <div class="flex items-center justify-between pt-2">
        <h2 class="text-base font-bold text-[#F7C873] font-brand tracking-wider">ACTIVE GUEST TABS & ADVANCES</h2>
        <span class="text-xs text-[#A69EA9]">Live order tracking vs deposit</span>
      </div>

      <!-- Dynamic Tabs Container -->
      <div id="tabs-container" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Rendered by JS -->
      </div>
    </section>

    <!-- ========================================== -->
    <!-- SECTION 2: INVENTORY & PRODUCT MANAGEMENT  -->
    <!-- ========================================== -->
    <section id="section-inventory" class="hidden space-y-4">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-[#1C1722] p-4 rounded-xl border border-[#382F40]">
        <div>
          <h2 class="text-lg font-bold text-[#F7C873] font-brand tracking-wider">BAR & CELLAR INVENTORY</h2>
          <p class="text-xs text-[#A69EA9]">Live stock counts, wholesale costs, poured units & alerts</p>
        </div>
        <button onclick="openModal('modal-add-product')" class="px-4 py-2.5 rounded-lg bg-[#E51528] text-white text-xs font-bold hover:bg-[#FF2A3F] transition-all flex items-center justify-center space-x-1.5 shadow-md">
          <span>+ Add Product Manually</span>
        </button>
      </div>

      <!-- Search & Filters -->
      <div class="flex flex-col sm:flex-row gap-2">
        <input type="text" id="inv-search" oninput="renderInventory()" placeholder="Search product, SKU, whiskey, cocktail..." class="flex-1 bg-[#151118] border border-[#382F40] rounded-lg px-3.5 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        <select id="inv-cat-filter" onchange="renderInventory()" class="bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-[#F7C873] focus:outline-none focus:border-[#DE9B47]">
          <option value="ALL">All Categories</option>
          <option value="Cocktails">Cocktails</option>
          <option value="Spirits & Whiskey">Spirits & Whiskey</option>
          <option value="Wine & Champagne">Wine & Champagne</option>
          <option value="Beer & Cider">Beer & Cider</option>
          <option value="Shots & Shooters">Shots & Shooters</option>
          <option value="Bar Bites & Food">Bar Bites & Food</option>
        </select>
      </div>

      <!-- Products Grid -->
      <div id="inventory-container" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
        <!-- Rendered by JS -->
      </div>
    </section>

    <!-- ========================================== -->
    <!-- SECTION 3: POS TERMINAL                    -->
    <!-- ========================================== -->
    <section id="section-pos" class="hidden space-y-4">
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <!-- Menu Items Grid (2 cols on large) -->
        <div class="lg:col-span-2 space-y-3">
          <div class="flex items-center justify-between bg-[#1C1722] p-3 rounded-lg border border-[#382F40]">
            <span class="text-xs font-bold text-[#F7C873] uppercase">Quick Tap Order Pad</span>
            <span class="text-xs text-[#A69EA9]">Tap any item to add to bill</span>
          </div>
          <div id="pos-items-grid" class="grid grid-cols-2 sm:grid-cols-3 gap-2.5">
            <!-- Rendered by JS -->
          </div>
        </div>

        <!-- POS Cart Drawer (1 col) -->
        <div class="bg-[#1C1722] p-4 rounded-xl border border-[#DE9B47]/40 h-fit space-y-3">
          <div class="flex items-center justify-between border-b border-[#382F40] pb-2">
            <h3 class="font-bold text-sm text-white">Current Order Cart</h3>
            <button onclick="clearCart()" class="text-xs text-[#A69EA9] hover:text-[#EE1528]">Clear</button>
          </div>
          <div id="pos-cart-items" class="space-y-2 max-h-60 overflow-y-auto pr-1">
            <p class="text-xs text-[#7E7385] italic text-center py-6">Tap products to build an order</p>
          </div>
          <div class="border-t border-[#382F40] pt-3 space-y-1.5 text-xs">
            <div class="flex justify-between text-[#A69EA9]">
              <span>Subtotal:</span>
              <span id="pos-subtotal">$0.00</span>
            </div>
            <div class="flex justify-between text-[#A69EA9]">
              <span>Tax (8.5%):</span>
              <span id="pos-tax">$0.00</span>
            </div>
            <div class="flex justify-between text-base font-bold text-[#F7C873] pt-1">
              <span>Total:</span>
              <span id="pos-total">$0.00</span>
            </div>
          </div>
          <div class="grid grid-cols-2 gap-2 pt-2">
            <button onclick="checkoutCart('cash')" class="py-2.5 bg-[#DE9B47] text-black font-bold text-xs rounded-lg hover:bg-[#F7C873] transition-all">
              Quick Cash
            </button>
            <button onclick="checkoutCart('tab')" class="py-2.5 bg-[#EE1528] text-white font-bold text-xs rounded-lg hover:bg-[#FF2A3F] transition-all">
              Send to Tab
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- ========================================== -->
    <!-- SECTION 4: RECIPES                         -->
    <!-- ========================================== -->
    <section id="section-recipes" class="hidden space-y-4">
      <div class="flex items-center justify-between bg-[#1C1722] p-4 rounded-xl border border-[#382F40]">
        <div>
          <h2 class="text-lg font-bold text-[#F7C873] font-brand tracking-wider">COCKTAIL SPECS & HOUSE RECIPES</h2>
          <p class="text-xs text-[#A69EA9]">Glassware, ice specifications, jigger measurements, and build methods</p>
        </div>
      </div>
      <div id="recipes-container" class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- Rendered by JS -->
      </div>
    </section>

    <!-- ========================================== -->
    <!-- SECTION 5: SETTINGS & LOGO                 -->
    <!-- ========================================== -->
    <section id="section-settings" class="hidden space-y-4">
      <div class="bg-[#1C1722] p-5 rounded-xl border border-[#382F40] space-y-4 max-w-2xl mx-auto">
        <h2 class="text-lg font-bold text-[#F7C873] font-brand tracking-wider border-b border-[#382F40] pb-2">VENUE BRANDING & APP ICON SETTINGS</h2>
        
        <!-- Logo Preview Banner -->
        <div>
          <label class="block text-xs font-bold text-[#A69EA9] mb-1.5 uppercase">Live Brand Logo Preview</label>
          <div class="w-full bg-[#09070A] border-2 border-[#DE9B47] rounded-xl p-6 text-center shadow-lg relative overflow-hidden">
            <div class="w-16 h-16 mx-auto mb-2 text-[#EE1528]">
              <svg viewBox="0 0 100 100" fill="none" stroke="currentColor" class="w-full h-full drop-shadow-[0_0_12px_rgba(238,21,40,0.4)]">
                <path d="M38,28 L32,16 L42,24 Z" fill="#EE1528" stroke="none" />
                <path d="M62,28 L68,16 L58,24 Z" fill="#EE1528" stroke="none" />
                <path d="M50,30 C44,20 32,20 26,28 C18,38 20,54 32,66 C40,74 47,80 50,83 C53,80 60,74 68,66 C80,54 82,38 74,28 C68,20 56,20 50,30 Z" stroke="#EE1528" stroke-width="4.5" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M50,83 C48,88 52,94 60,93 C66,92 70,87 69,81" stroke="#EE1528" stroke-width="3.5" stroke-linecap="round" />
                <path d="M69,81 L74,84 L70,76 Z" fill="#EE1528" stroke="none" />
              </svg>
            </div>
            <h3 id="previewVenueName" class="font-brand font-black text-2xl tracking-[0.25em] gold-gradient-text">${settings.venueName}</h3>
            <p id="previewTagline" class="text-xs text-[#EDE1CE] tracking-widest mt-1">${settings.tagline}</p>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Venue / Bar Name</label>
            <input type="text" id="setting-name" value="${settings.venueName}" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Currency Symbol</label>
            <input type="text" id="setting-currency" value="${settings.currencySymbol}" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
        </div>

        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Subtitle / Tagline</label>
          <input type="text" id="setting-tagline" value="${settings.tagline}" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        </div>

        <div class="grid grid-cols-2 sm:grid-cols-3 gap-3">
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Tax Rate (%)</label>
            <input type="number" step="0.1" id="setting-tax" value="${settings.taxRatePercent}" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Service (%)</label>
            <input type="number" step="0.1" id="setting-service" value="${settings.serviceChargePercent}" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Default Advance ($)</label>
            <input type="number" id="setting-advance" value="${settings.defaultAdvanceDeposit}" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
        </div>

        <button onclick="saveSettings()" class="w-full py-3 bg-[#EE1528] text-white font-bold text-sm rounded-lg hover:bg-[#FF2A3F] transition-all shadow-md">
          Save Settings & Update Branding
        </button>
      </div>
    </section>

  </main>

  <!-- ========================================== -->
  <!-- MODALS                                     -->
  <!-- ========================================== -->

  <!-- Modal: Open Tab with Advance -->
  <div id="modal-open-tab" class="hidden fixed inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
    <div class="bg-[#1C1722] border border-[#DE9B47]/50 rounded-2xl max-w-md w-full p-5 space-y-4 shadow-2xl">
      <div class="flex justify-between items-center border-b border-[#382F40] pb-2">
        <h3 class="font-bold text-[#F7C873] text-base">Open Customer Tab with Advance</h3>
        <button onclick="closeModal('modal-open-tab')" class="text-[#A69EA9] hover:text-white">✕</button>
      </div>
      <div class="space-y-3">
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Customer Name *</label>
          <input type="text" id="tab-cust-name" placeholder="e.g. Gary Murambiwa, Marcus..." class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        </div>
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Table / Seating Area</label>
          <select id="tab-table" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
            <option>Bar Stool 1</option><option>Bar Stool 2</option><option>Bar Stool 3</option><option>Bar Stool 4</option>
            <option>Table 1</option><option>Table 2</option><option>Table 4</option><option>Table 7 (Patio)</option>
            <option>VIP Lounge 1</option><option>VIP Lounge 2</option><option>VIP Booth 3</option>
          </select>
        </div>
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Advance Deposit Amount ($) *</label>
          <div class="grid grid-cols-4 gap-2 mb-2">
            <button type="button" onclick="document.getElementById('tab-advance').value=20" class="py-1 text-xs bg-[#151118] border border-[#382F40] rounded text-[#F7C873] font-bold">$20</button>
            <button type="button" onclick="document.getElementById('tab-advance').value=50" class="py-1 text-xs bg-[#151118] border border-[#382F40] rounded text-[#F7C873] font-bold">$50</button>
            <button type="button" onclick="document.getElementById('tab-advance').value=100" class="py-1 text-xs bg-[#151118] border border-[#382F40] rounded text-[#F7C873] font-bold">$100</button>
            <button type="button" onclick="document.getElementById('tab-advance').value=200" class="py-1 text-xs bg-[#151118] border border-[#382F40] rounded text-[#F7C873] font-bold">$200</button>
          </div>
          <input type="number" id="tab-advance" value="50" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        </div>
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Payment Method</label>
          <select id="tab-method" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
            <option>Cash</option>
            <option>Card / POS</option>
            <option>Transfer / Wire</option>
          </select>
        </div>
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Notes</label>
          <input type="text" id="tab-notes" placeholder="e.g. VIP guests, birthday" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        </div>
      </div>
      <div class="flex space-x-2 pt-2">
        <button onclick="closeModal('modal-open-tab')" class="flex-1 py-2 text-xs text-[#A69EA9] bg-[#151118] rounded-lg font-bold">Cancel</button>
        <button onclick="submitNewTab()" class="flex-1 py-2 text-xs text-white bg-[#EE1528] rounded-lg font-bold hover:bg-[#FF2A3F]">Open Tab</button>
      </div>
    </div>
  </div>

  <!-- Modal: Add Product Manually -->
  <div id="modal-add-product" class="hidden fixed inset-0 bg-black/80 backdrop-blur-sm z-50 flex items-center justify-center p-4">
    <div class="bg-[#1C1722] border border-[#DE9B47]/50 rounded-2xl max-w-md w-full p-5 space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto">
      <div class="flex justify-between items-center border-b border-[#382F40] pb-2">
        <h3 class="font-bold text-[#F7C873] text-base">Add Product Manually to Inventory</h3>
        <button onclick="closeModal('modal-add-product')" class="text-[#A69EA9] hover:text-white">✕</button>
      </div>
      <div class="space-y-3">
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Product / Drink Name *</label>
          <input type="text" id="prod-name" placeholder="e.g. Hennessy VSOP, Don Julio..." class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        </div>
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Category</label>
          <select id="prod-cat" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
            <option>Cocktails</option>
            <option>Spirits & Whiskey</option>
            <option>Wine & Champagne</option>
            <option>Beer & Cider</option>
            <option>Shots & Shooters</option>
            <option>Bar Bites & Food</option>
          </select>
        </div>
        <div class="grid grid-cols-2 gap-2">
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Menu Price ($) *</label>
            <input type="number" step="0.5" id="prod-price" placeholder="15.00" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Cost Price ($)</label>
            <input type="number" step="0.5" id="prod-cost" placeholder="4.00" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
        </div>
        <div class="grid grid-cols-2 gap-2">
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Current Stock *</label>
            <input type="number" id="prod-stock" value="12" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Min Alert Threshold</label>
            <input type="number" id="prod-min" value="8" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
        </div>
        <div class="grid grid-cols-2 gap-2">
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Unit Type</label>
            <input type="text" id="prod-unit" value="Bottle" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
          <div>
            <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Volume</label>
            <input type="text" id="prod-vol" value="750ml" class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
          </div>
        </div>
        <div>
          <label class="block text-xs font-semibold text-[#A69EA9] mb-1">Description</label>
          <input type="text" id="prod-desc" placeholder="Tasting notes, garnish or origin..." class="w-full bg-[#151118] border border-[#382F40] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#DE9B47]">
        </div>
      </div>
      <div class="flex space-x-2 pt-2">
        <button onclick="closeModal('modal-add-product')" class="flex-1 py-2 text-xs text-[#A69EA9] bg-[#151118] rounded-lg font-bold">Cancel</button>
        <button onclick="submitNewProduct()" class="flex-1 py-2 text-xs text-white bg-[#EE1528] rounded-lg font-bold hover:bg-[#FF2A3F]">Save Product</button>
      </div>
    </div>
  </div>

  <!-- SCRIPT -->
  <script>
    let productsData = ${JSON.stringify(products)};
    let tabsData = ${JSON.stringify(tabs)};
    let recipesData = ${JSON.stringify(recipes)};
    let cart = [];

    function switchTab(tabId) {
      document.querySelectorAll('section').forEach(s => s.classList.add('hidden'));
      document.getElementById('section-' + tabId).classList.remove('hidden');

      document.querySelectorAll('.nav-btn').forEach(btn => {
        btn.classList.remove('bg-[#E51528]', 'text-white');
        btn.classList.add('bg-[#1C1722]', 'text-[#B8ADB9]');
      });
      const activeBtn = document.getElementById('nav-' + tabId);
      activeBtn.classList.remove('bg-[#1C1722]', 'text-[#B8ADB9]');
      activeBtn.classList.add('bg-[#E51528]', 'text-white');

      if (tabId === 'tabs') renderTabs();
      if (tabId === 'inventory') renderInventory();
      if (tabId === 'pos') renderPos();
      if (tabId === 'recipes') renderRecipes();
    }

    function renderTabs() {
      const container = document.getElementById('tabs-container');
      const activeTabs = tabsData.filter(t => t.status === 'OPEN');

      let totalAdvances = 0;
      let totalBills = 0;
      let totalOwed = 0;

      container.innerHTML = activeTabs.map(tab => {
        const subtotal = tab.items.reduce((sum, i) => sum + (i.unitPrice * i.quantity), 0);
        const tax = subtotal * 0.085;
        const service = subtotal * 0.10;
        const total = subtotal + tax + service;
        const balance = total - tab.advancePaid;

        totalAdvances += tab.advancePaid;
        totalBills += total;
        if (balance > 0) totalOwed += balance;

        const isOwed = balance > 0;
        const isCredit = balance < 0;

        return \`
          <div class="bg-[#1C1722] border \${isOwed ? 'border-[#EE1528]/60' : 'border-[#DE9B47]/30'} rounded-xl p-4 shadow-lg flex flex-col justify-between space-y-3">
            <div>
              <div class="flex justify-between items-start">
                <div>
                  <span class="text-[10px] font-bold px-2 py-0.5 rounded bg-[#2D2433] text-[#F7C873] border border-[#DE9B47]/30">\${tab.tableName}</span>
                  <h3 class="font-bold text-white text-base mt-1.5">\${tab.customerName}</h3>
                  <p class="text-[11px] text-[#A69EA9]">\${tab.notes || 'Open tab'}</p>
                </div>
                <span class="text-[10px] font-bold px-2 py-0.5 rounded bg-emerald-950 text-emerald-400 border border-emerald-800">OPEN</span>
              </div>

              <!-- Item List -->
              <div class="bg-[#151118] p-2.5 rounded-lg mt-3 space-y-1 text-xs">
                \${tab.items.length === 0 ? '<p class="text-[#7E7385] italic">No drinks ordered yet</p>' : ''}
                \${tab.items.map(item => \`
                  <div class="flex justify-between text-[#EDE1CE]">
                    <span>\${item.quantity}x \${item.name}</span>
                    <span class="font-bold text-[#F7C873]">$\${(item.unitPrice * item.quantity).toFixed(2)}</span>
                  </div>
                \`).join('')}
              </div>
            </div>

            <!-- Financials Summary -->
            <div class="border-t border-[#2D2433] pt-2.5 space-y-2">
              <div class="flex justify-between text-xs text-[#A69EA9]">
                <span>Advance Deposit Paid (\${tab.advanceMethod}):</span>
                <span class="font-bold text-[#F7C873]">$\${tab.advancePaid.toFixed(2)}</span>
              </div>
              <div class="flex justify-between text-xs text-[#A69EA9]">
                <span>Order Total (w/ tax & svc):</span>
                <span class="font-bold text-white">$\${total.toFixed(2)}</span>
              </div>

              <!-- Net Balance Display -->
              <div class="flex justify-between items-center bg-[#151118] p-2 rounded-lg border border-[#382F40]">
                <span class="text-xs font-bold \${isOwed ? 'text-[#EE1528]' : 'text-emerald-400'}">
                  \${isOwed ? 'OUTSTANDING BALANCE OWED:' : 'REMAINING ADVANCE CREDIT:'}
                </span>
                <span class="text-sm font-black \${isOwed ? 'text-[#EE1528]' : 'text-emerald-400'}">
                  $\${Math.abs(balance).toFixed(2)}
                </span>
              </div>

              <!-- Action Buttons -->
              <div class="grid grid-cols-2 gap-2 pt-1">
                <button onclick="addDrinkPrompt('\${tab.id}')" class="py-2 bg-[#2D2433] text-white hover:bg-[#382F40] rounded-lg text-xs font-bold transition-all">
                  + Add Drink
                </button>
                <button onclick="clearCustomerCredit('\${tab.id}')" class="py-2 \${isOwed ? 'bg-[#EE1528] text-white hover:bg-[#FF2A3F]' : 'bg-[#DE9B47] text-black hover:bg-[#F7C873]'} rounded-lg text-xs font-bold transition-all">
                  \${isOwed ? 'Clear Credit / Settle' : 'Refund Advance & Close'}
                </button>
              </div>
            </div>
          </div>
        \`;
      }).join('');

      document.getElementById('kpi-advances').innerText = '$' + totalAdvances.toFixed(2);
      document.getElementById('kpi-bills').innerText = '$' + totalBills.toFixed(2);
      document.getElementById('kpi-owed').innerText = '$' + totalOwed.toFixed(2);
      document.getElementById('badge-tabs').innerText = activeTabs.length;
    }

    function clearCustomerCredit(tabId) {
      const tab = tabsData.find(t => t.id === tabId);
      if (!tab) return;
      const subtotal = tab.items.reduce((sum, i) => sum + (i.unitPrice * i.quantity), 0);
      const total = subtotal * 1.185;
      const balance = total - tab.advancePaid;

      const confirmMsg = balance > 0 
        ? \`Clear customer credit of $\${balance.toFixed(2)} for \${tab.customerName} via Cash/Card?\`
        : \`Refund remaining unspent advance deposit of $\${Math.abs(balance).toFixed(2)} to \${tab.customerName} and close tab?\`;

      if (confirm(confirmMsg)) {
        tab.status = 'CLOSED';
        renderTabs();
        alert(\`Customer tab closed and credit settled for \${tab.customerName}!\`);
      }
    }

    function addDrinkPrompt(tabId) {
      const tab = tabsData.find(t => t.id === tabId);
      const prod = productsData[0]; // defaults to Succubus Temptation
      tab.items.push({
        id: 'item-' + Date.now(),
        productId: prod.id,
        name: prod.name,
        unitPrice: prod.price,
        quantity: 1
      });
      prod.stock = Math.max(0, prod.stock - 1);
      renderTabs();
      alert(\`Added 1x \${prod.name} to \${tab.customerName}'s tab!\`);
    }

    function renderInventory() {
      const query = document.getElementById('inv-search').value.toLowerCase();
      const cat = document.getElementById('inv-cat-filter').value;
      const container = document.getElementById('inventory-container');

      const filtered = productsData.filter(p => {
        const matchesQuery = p.name.toLowerCase().includes(query) || p.sku.toLowerCase().includes(query);
        const matchesCat = cat === 'ALL' || p.category === cat;
        return matchesQuery && matchesCat;
      });

      let lowCount = productsData.filter(p => p.stock <= p.minStockThreshold).length;
      document.getElementById('badge-low').innerText = lowCount + ' Low';

      container.innerHTML = filtered.map(p => {
        const isLow = p.stock <= p.minStockThreshold;
        return \`
          <div class="bg-[#1C1722] border \${isLow ? 'border-[#F39C12]' : 'border-[#382F40]'} rounded-xl p-3.5 space-y-2.5 shadow">
            <div class="flex justify-between items-start">
              <div>
                <span class="text-[9px] font-bold px-1.5 py-0.5 rounded bg-[#2D2433] text-[#F7C873] uppercase">\${p.category}</span>
                <h4 class="font-bold text-white text-sm mt-1">\${p.name}</h4>
                <p class="text-[11px] text-[#A69EA9]">\${p.unit} (\${p.volume}) • \${p.sku}</p>
              </div>
              <span class="text-sm font-bold text-[#F7C873]">$\${p.price.toFixed(2)}</span>
            </div>
            <div class="flex items-center justify-between bg-[#151118] p-2 rounded-lg text-xs">
              <span class="font-bold \${isLow ? 'text-[#F39C12]' : 'text-emerald-400'}">
                \${p.stock} in stock \${isLow ? '(Low Alert)' : ''}
              </span>
              <div class="flex space-x-1">
                <button onclick="adjustStock('\${p.id}', 1)" class="px-2 py-0.5 bg-[#2D2433] text-white rounded font-bold hover:bg-[#382F40]">+1</button>
                <button onclick="adjustStock('\${p.id}', 6)" class="px-2 py-0.5 bg-[#2D2433] text-white rounded font-bold hover:bg-[#382F40]">+6</button>
              </div>
            </div>
          </div>
        \`;
      }).join('');
    }

    function adjustStock(prodId, delta) {
      const p = productsData.find(item => item.id === prodId);
      if (p) {
        p.stock = Math.max(0, p.stock + delta);
        renderInventory();
      }
    }

    function renderPos() {
      const container = document.getElementById('pos-items-grid');
      container.innerHTML = productsData.map(p => \`
        <div onclick="addToCart('\${p.id}')" class="bg-[#1C1722] border border-[#382F40] hover:border-[#DE9B47] rounded-xl p-3 cursor-pointer transition-all flex flex-col justify-between shadow">
          <div>
            <span class="text-[9px] font-bold text-[#DE9B47] uppercase">\${p.category}</span>
            <h4 class="font-bold text-white text-xs mt-1 line-clamp-2">\${p.name}</h4>
          </div>
          <div class="flex justify-between items-center mt-3">
            <span class="text-xs font-bold text-[#F7C873]">$\${p.price.toFixed(2)}</span>
            <span class="w-6 h-6 rounded-full bg-[#EE1528] text-white flex items-center justify-center text-xs font-bold">+</span>
          </div>
        </div>
      \`).join('');
    }

    function addToCart(prodId) {
      const p = productsData.find(item => item.id === prodId);
      const existing = cart.find(c => c.id === prodId);
      if (existing) {
        existing.quantity++;
      } else {
        cart.push({ id: p.id, name: p.name, price: p.price, quantity: 1 });
      }
      renderCart();
    }

    function renderCart() {
      const container = document.getElementById('pos-cart-items');
      if (cart.length === 0) {
        container.innerHTML = '<p class="text-xs text-[#7E7385] italic text-center py-6">Tap products to build an order</p>';
        document.getElementById('pos-subtotal').innerText = '$0.00';
        document.getElementById('pos-tax').innerText = '$0.00';
        document.getElementById('pos-total').innerText = '$0.00';
        return;
      }

      let subtotal = 0;
      container.innerHTML = cart.map(item => {
        subtotal += item.price * item.quantity;
        return \`
          <div class="flex justify-between items-center bg-[#151118] p-2 rounded text-xs">
            <span class="truncate max-w-[130px]">\${item.quantity}x \${item.name}</span>
            <span class="font-bold text-[#F7C873]">$\${(item.price * item.quantity).toFixed(2)}</span>
          </div>
        \`;
      }).join('');

      const tax = subtotal * 0.085;
      const total = subtotal + tax;

      document.getElementById('pos-subtotal').innerText = '$' + subtotal.toFixed(2);
      document.getElementById('pos-tax').innerText = '$' + tax.toFixed(2);
      document.getElementById('pos-total').innerText = '$' + total.toFixed(2);
    }

    function clearCart() {
      cart = [];
      renderCart();
    }

    function checkoutCart(method) {
      if (cart.length === 0) return alert('Cart is empty');
      if (method === 'cash') {
        alert('Payment collected via Cash! Inventory updated.');
        clearCart();
      } else {
        const openTabs = tabsData.filter(t => t.status === 'OPEN');
        if (openTabs.length === 0) return alert('No open tabs');
        const target = openTabs[0];
        cart.forEach(c => {
          target.items.push({ id: 'item-' + Date.now(), name: c.name, unitPrice: c.price, quantity: c.quantity });
        });
        alert(\`Charged order to \${target.customerName}'s tab!\`);
        clearCart();
        renderTabs();
      }
    }

    function renderRecipes() {
      const container = document.getElementById('recipes-container');
      container.innerHTML = recipesData.map(r => \`
        <div class="bg-[#1C1722] border border-[#DE9B47]/40 rounded-xl p-4 shadow-lg space-y-3">
          <div class="flex justify-between items-start">
            <div>
              <span class="text-[10px] font-bold px-2 py-0.5 rounded bg-[#2D2433] text-[#F7C873]">\${r.category}</span>
              <h3 class="font-bold text-white text-base font-brand mt-1">\${r.name}</h3>
            </div>
            <span class="text-sm font-bold text-[#F7C873]">$\${r.menuPrice.toFixed(2)}</span>
          </div>
          <div class="grid grid-cols-3 gap-2 bg-[#151118] p-2.5 rounded-lg text-xs text-[#A69EA9]">
            <div><span class="text-[9px] uppercase font-bold block">Glass</span><span class="text-white font-semibold">\${r.glassware}</span></div>
            <div><span class="text-[9px] uppercase font-bold block">Ice</span><span class="text-white font-semibold">\${r.ice}</span></div>
            <div><span class="text-[9px] uppercase font-bold block">Method</span><span class="text-[#F7C873] font-semibold">\${r.method}</span></div>
          </div>
          <div class="text-xs space-y-1">
            <p class="font-bold text-[#F7C873] text-[11px] uppercase">Ingredients:</p>
            \${r.ingredients.map(ing => '<p class="text-[#EDE1CE]">• ' + ing + '</p>').join('')}
          </div>
          <div class="text-xs text-[#A69EA9] italic border-t border-[#382F40] pt-2">
            Garnish: \${r.garnish}
          </div>
          <button onclick="ringUpRecipe('\${r.name}', \${r.menuPrice})" class="w-full py-2 bg-[#EE1528] text-white text-xs font-bold rounded-lg hover:bg-[#FF2A3F] transition-all">
            Ring Up Cocktail to Active Tab
          </button>
        </div>
      \`).join('');
    }

    function ringUpRecipe(name, price) {
      const openTabs = tabsData.filter(t => t.status === 'OPEN');
      if (openTabs.length === 0) return alert('Please open a customer tab first in Tabs & Advances.');
      const tab = openTabs[0];
      tab.items.push({ id: 'item-' + Date.now(), name: name, unitPrice: price, quantity: 1 });
      alert(\`Ring up confirmed: \${name} added to \${tab.customerName}'s tab!\`);
    }

    function openModal(id) { document.getElementById(id).classList.remove('hidden'); }
    function closeModal(id) { document.getElementById(id).classList.add('hidden'); }

    function submitNewTab() {
      const name = document.getElementById('tab-cust-name').value.trim();
      const table = document.getElementById('tab-table').value;
      const advance = parseFloat(document.getElementById('tab-advance').value) || 0;
      const method = document.getElementById('tab-method').value;
      const notes = document.getElementById('tab-notes').value.trim();

      if (!name) return alert('Customer name is required');
      tabsData.unshift({
        id: 'tab-' + Date.now(),
        customerName: name,
        tableName: table,
        advancePaid: advance,
        advanceMethod: method,
        status: 'OPEN',
        notes: notes,
        items: []
      });

      closeModal('modal-open-tab');
      renderTabs();
    }

    function submitNewProduct() {
      const name = document.getElementById('prod-name').value.trim();
      const cat = document.getElementById('prod-cat').value;
      const price = parseFloat(document.getElementById('prod-price').value) || 0;
      const cost = parseFloat(document.getElementById('prod-cost').value) || 0;
      const stock = parseInt(document.getElementById('prod-stock').value) || 0;
      const min = parseInt(document.getElementById('prod-min').value) || 8;
      const unit = document.getElementById('prod-unit').value || 'Bottle';
      const vol = document.getElementById('prod-vol').value || '750ml';
      const desc = document.getElementById('prod-desc').value || '';

      if (!name || price <= 0) return alert('Valid product name and price required');

      productsData.unshift({
        id: 'prod-' + Date.now(),
        name: name,
        category: cat,
        price: price,
        costPrice: cost,
        stock: stock,
        minStockThreshold: min,
        unit: unit,
        volume: vol,
        sku: 'SUC-' + Math.floor(100 + Math.random() * 900),
        description: desc
      });

      closeModal('modal-add-product');
      renderInventory();
    }

    function saveSettings() {
      const name = document.getElementById('setting-name').value.trim();
      const tagline = document.getElementById('setting-tagline').value.trim();
      const currency = document.getElementById('setting-currency').value.trim();

      if (name) {
        document.getElementById('brandVenueName').innerText = name;
        document.getElementById('previewVenueName').innerText = name;
      }
      if (tagline) {
        document.getElementById('brandTagline').innerText = tagline;
        document.getElementById('previewTagline').innerText = tagline;
      }
      alert('Venue branding & settings updated successfully!');
    }

    // Init
    renderTabs();
  </script>
</body>
</html>`;
}

const server = http.createServer((req, res) => {
  const parsedUrl = new URL(req.url, `http://${req.headers.host}`);
  const pathname = parsedUrl.pathname;

  // Serve APK download
  if (pathname === "/download/app-debug.apk") {
    const apkPath = path.join(__dirname, "app/build/outputs/apk/debug/app-debug.apk");
    if (fs.existsSync(apkPath)) {
      const stat = fs.statSync(apkPath);
      res.writeHead(200, {
        "Content-Type": "application/vnd.android.package-archive",
        "Content-Length": stat.size,
        "Content-Disposition": 'attachment; filename="Succubus-Bar-Release.apk"'
      });
      return fs.createReadStream(apkPath).pipe(res);
    } else {
      res.writeHead(404, { "Content-Type": "text/plain" });
      return res.end("APK build not found yet.");
    }
  }

  // Serve static assets from public/
  if (pathname.startsWith("/public/") || pathname.startsWith("/assets/")) {
    const safePath = path.join(__dirname, pathname);
    if (fs.existsSync(safePath) && fs.statSync(safePath).isFile()) {
      const ext = path.extname(safePath).toLowerCase();
      const mimeTypes = {
        ".png": "image/png",
        ".jpg": "image/jpeg",
        ".jpeg": "image/jpeg",
        ".webp": "image/webp",
        ".svg": "image/svg+xml",
        ".css": "text/css",
        ".js": "application/javascript"
      };
      res.writeHead(200, { "Content-Type": mimeTypes[ext] || "application/octet-stream" });
      return fs.createReadStream(safePath).pipe(res);
    }
  }

  // Fallback / root: Serve the main web application
  res.writeHead(200, { "Content-Type": "text/html; charset=utf-8" });
  res.end(getHtml());
});

server.listen(PORT, HOST, () => {
  console.log(`[Succubus Dev Server] running on http://${HOST}:${PORT}`);
});
