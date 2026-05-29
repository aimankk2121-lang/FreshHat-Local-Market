package com.example.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- Data Models ---

data class Product(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val categoryId: String,
    val currentPrice: Double,
    val oldPrice: Double,
    val stock: Int,
    val descriptionEn: String,
    val descriptionBn: String,
    val emoji: String,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false,
    val rating: Float = 4.5f
)

data class MarketCategory(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val emoji: String,
    val colorHex: Long
)

data class CartItem(
    val productId: String,
    val quantity: Int
)

data class Order(
    val id: String,
    val customerName: String,
    val phoneNumber: String,
    val deliveryAddress: String,
    val paymentMethod: String, // bKash, Nagad, Cash on Delivery
    val transactionId: String,
    val items: List<CartItem>,
    val totalPrice: Double,
    val status: String, // Pending, Processing, Out for Delivery, Delivered
    val timestamp: String,
    val trackingStep: Int = 1 // 1: Placed, 2: Packing, 3: Shipped, 4: Delivered
)

data class User(
    val name: String,
    val phone: String,
    val address: String,
    val isLoggedIn: Boolean = false
)

// --- Initial Mock Data ---

object MarketDetails {
    val categories = listOf(
        MarketCategory("grocery", "Grocery", "মুদি সামগ্রী", "🥫", 0xFF4CAF50),
        MarketCategory("rice_oil", "Rice, Oil, Lentils", "চাল, ডাল, তেল", "🌾", 0xFFFF9800),
        MarketCategory("vegetables", "Vegetables", "শাক-সবজি", "🥦", 0xFF8BC34A),
        MarketCategory("fish", "Fish", "মাছ বাজার", "🐟", 0xFF00BCD4),
        MarketCategory("meat", "Meat", "মাংস", "🥩", 0xFFF44336),
        MarketCategory("fruits", "Fruits", "তাজা ফল", "🍎", 0xFFFFC107),
        MarketCategory("eggs_milk", "Eggs & Milk", "ডিম ও দুধ", "🥛", 0xFFE91E63),
        MarketCategory("drinks", "Drinks", "কোমল পানীয়", "🍹", 0xFF3F51B5),
        MarketCategory("fast_food", "Fast Food", "ঝটপট খাবার", "🍔", 0xFF9C27B0),
        MarketCategory("household", "Household Products", "গৃহস্থালী সামগ্রী", "🧼", 0xFF607D8B)
    )

    val defaultProducts = listOf(
        // Grocery
        Product(
            "gp_dal_1", "Premium Masoor Dal", "প্রিমিয়াম মসুর ডাল", "grocery",
            140.0, 160.0, 45,
            "High protein organic red lentils, directly from local village farms. Perfectly polished.",
            "উচ্চ প্রোটিনযুক্ত জৈব লাল মসুর ডাল, সরাসরি স্থানীয় খামার থেকে সংগৃহীত।", "🍛", true, false, 4.8f
        ),
        Product(
            "gp_salt_1", "ACI Pure Shobji Salt (1kg)", "এসিআই পিউর লবণ (১ কেজি)", "grocery",
            38.0, 42.0, 100,
            "Iodized edible vacuum salt to ensure healthy growth and smart flavor.",
            "আয়োডিনযুক্ত ভোজ্য লবণ যা আপনার সুস্বাস্থ্য এবং খাবারের সঠিক স্বাদ বজায় রাখবে।", "🧂", false, false, 4.3f
        ),
        Product(
            "gp_sugar_1", "Fresh Refinery Sugar (1kg)", "ফ্রেশ চিনি (১ কেজি)", "grocery",
            135.0, 145.0, 80,
            "Refined clean white cane sugar for all your household sweet recipes.",
            "সব ধরণের মিষ্টি পিঠা ও রান্নার জন্য পরিশোধিত ফ্রেশ সাদা চিনি।", "🍬", false, true, 4.4f
        ),

        // Rice, Oil, Lentils
        Product(
            "ro_rice_1", "Miniket Rice Premium (5kg)", "মিনিকেট চাল প্রিমিয়াম (৫ কেজি)", "rice_oil",
            360.0, 390.0, 20,
            "Thin, long grain premium quality Miniket Rice. Standard of Bangladeshi household meals.",
            "চিকন ও লম্বা দানার প্রিমিয়াম কোয়ালিটির মিনিকেট চাল। সেরা স্বাদের নিশ্চয়তা।", "🌾", true, false, 4.9f
        ),
        Product(
            "ro_oil_2", "Rupchanda Soyabean Oil (2L)", "রূপচাঁদা সয়াবিন তেল (২ লিটার)", "rice_oil",
            335.0, 350.0, 30,
            "Highly refined double-refined soyabean oil containing Vitamin A for your daily wellness.",
            "ভিটামিন-এ সমৃদ্ধ দ্বিগুণ পরিশোধিত সয়াবিন তেল, যা রান্নায় আনে খাঁটি স্বাদ।", "🍾", true, false, 4.7f
        ),
        Product(
            "ro_mustard_1", "Radhuni Pure Mustard Oil (500ml)", "রাধুনি খাঁটি সরিষার তেল (৫০০ মিলি)", "rice_oil",
            160.0, 175.0, 15,
            "Traditionally pressed authentic mustard oil with a punchy aroma for pickles and vashas.",
            "হাতে ভাজা সরিষা থেকে ঘানিতে ভাঙ্গানো তীব্র ঝাঁঝালো সরিষার তেল।", "🏺", false, true, 4.8f
        ),

        // Vegetables
        Product(
            "veg_potato_1", "Fresh Local Potato (1kg)", "দেশী আলু (১ কেজি)", "vegetables",
            55.0, 60.0, 120,
            "Freshly harvested local red potatoes. Excellent for curries and mashed potato (Aliter Vorta).",
            "সরাসরি মাঠ থেকে সংগৃহীত লাল গোল আলু। ভর্তা কিংবা তরকারির জন্য চমৎকার।", "🥔", true, false, 4.6f
        ),
        Product(
            "veg_onion_1", "Deshi Onion (1kg)", "দেশী লাল পেঁয়াজ (১ কেজি)", "vegetables",
            90.0, 110.0, 95,
            "High quality local Bangladeshi sharp-tasting red onions. Premium culinary base.",
            "ঝাঁঝালো এবং চমৎকার স্বাদের দেশী লাল পেঁয়াজ। রান্নার জন্য অপরিহার্য উপাদান।", "🧅", true, false, 4.5f
        ),
        Product(
            "veg_chili_1", "Green Chili (250g)", "কাঁচা মরিচ (২৫০ গ্রাম)", "vegetables",
            35.0, 45.0, 60,
            "Super spicy freshly plucked green chilies to spark up your authentic meals.",
            "গাছ থেকে তোলা তাজা এবং দারুন ঝাল সবুজ কাঁচা মরিচ।", "🌶️", false, true, 4.4f
        ),
        Product(
            "veg_tomato_1", "Red Tomato (1kg)", "পাকা টমেটো (১ কেজি)", "vegetables",
            80.0, 100.0, 40,
            "Chubby, ripe, fresh, and hand-selected tomatoes from winter produce batches.",
            "লাল টসটসে তাজা দেশী পাকা টমেটো। সালাদ কিংবা তরকারিতে সেরা স্বাদের জন্য।", "🍅", false, false, 4.5f
        ),

        // Fish
        Product(
            "fish_ruhi_1", "Fresh Ruhi Fish (1kg Size)", "তাজা রুই মাছ (১ কেজি সাইজ)", "fish",
            380.0, 420.0, 12,
            "Fresh river-caught Ruhi fish. Perfectly cleaned and scaled on request.",
            "নদীর তাজা সুস্বাদু রুই মাছ। আপনার অনুরোধ অনুযায়ী কেটে পরিষ্কার করে দেওয়া হবে।", "🐟", true, false, 4.7f
        ),
        Product(
            "fish_hilsa_1", "Padma River Hilsha (800g)", "পদ্মার তাজা ইলিশ (৮০০ গ্রাম)", "fish",
            1200.0, 1400.0, 8,
            "Iconic silver Hilsha caught fresh from Padma river. Extraordinarily aromatic and rich helper.",
            "পদ্মা নদীর আসল সুস্বাদু ডিমভর্তি বড় সাইজের রূপালী ইলিশ। রান্নায় অতুলনীয় সুবাস।", "🦈", true, true, 4.9f
        ),
        Product(
            "fish_shrimp_1", "Local Golda Chingri (500g)", "দেশী গলদা চিংড়ি (৫০০ গ্রাম)", "fish",
            450.0, 500.0, 10,
            "Sweet river freshwater jumbo prawns. Best for Daab Chingri and Malai curry.",
            "তাজা মিষ্টি জলের বড় সুস্বাদু গলদা চিংড়ি। মালাইকারির জন্য সেরা পছন্দ।", "🦐", false, false, 4.6f
        ),

        // Meat
        Product(
            "meat_beef_1", "Fresh Halal Beef (1kg)", "তাজা হালাল গরুর মাংস (১ কেজি)", "meat",
            780.0, 820.0, 15,
            "Grass-fed local bull meat. 100% Halal cut, containing ideal ratio of lean meat and bone.",
            "শতভাগ হালাল ও তাজা গরুর মাংস। সঠিক পরিমাণে হাড়-চর্বি সহ কোয়ালিটি কাট।", "🥩", true, false, 4.9f
        ),
        Product(
            "meat_chicken_1", "Local Broiler Chicken (1kg Cooled)", "ব্রয়লার মুরগী (১ কেজি তাজা)", "meat",
            195.0, 220.0, 30,
            "Freshly dressed local broiler chicken. Cleaned, gutted, and prepared under safety protocols.",
            "চামড়া ছাড়ানো পরিষ্কার করা ড্রেসড ব্রয়লার মুরগির মাংস। রান্নার জন্য প্রস্তুত।", "🍗", true, false, 4.4f
        ),
        Product(
            "meat_mutton_1", "Khashir Mangso / Mutton (1kg)", "খাসির মাংস (১ কেজি খাঁটি)", "meat",
            1100.0, 1200.0, 5,
            "Super tender premium quality young castrated goat meat safely cut by local butchers.",
            "প্রিমিয়াম কোয়ালিটি কচি পাঠার তুলতুলে নরম সুস্বাদু হালাল মাংস।", "🍖", false, true, 4.8f
        ),

        // Fruits
        Product(
            "fruits_banana_1", "Sagor Kola (1 Dozen)", "পাকা সাগর কলা (১ ডজন)", "fruits",
            110.0, 130.0, 25,
            "Sweet, nutritious, gold-yellow sagor bananas containing high potassium and immediate energy.",
            "মিষ্টি ও পুষ্টিগুণে ভরপুর সম্পূর্ণ ফরমালিনমুক্ত তাজা হলুদ সাগর কলা।", "🍌", false, false, 4.2f
        ),
        Product(
            "fruits_mango_1", "Rajshahi Himsagar Mango (1kg)", "রাজশাহীর হিমসাগর আম (১ কেজি)", "fruits",
            180.0, 210.0, 15,
            "Naturally ripened world-famous Himsagar mangoes directly sourced from Rajshahi orchards.",
            "সরাসরি রাজশাহী বাগান থেকে আনা অত্যন্ত মিষ্টি ও সুগন্ধি তাজা হিমসাগর আম।", "🥭", true, true, 4.8f
        ),
        Product(
            "fruits_jack_1", "Sweet Honey Jackfruit (Medium 1pc)", "মিষ্টি মধু কাঁঠাল (মাঝারি ১টি)", "fruits",
            220.0, 260.0, 8,
            "National fruit of Bangladesh. Highly sweet honey-dripped juicy pods in a medium green shell.",
            "বাংলাদেশের জাতীয় ফল। টাটকা অত্যন্ত মিষ্টি ও রসে ভরা মাঝারি সাইজের কাঁঠাল।", "🍈", false, false, 4.6f
        ),

        // Eggs & Milk
        Product(
            "em_egg_1", "Farm Red Eggs (1 Dozen/12pcs)", "লাল ফার্মের ডিম (১ ডজন/১২টি)", "eggs_milk",
            145.0, 155.0, 50,
            "Fresh high protein layer farm chicken red eggs. Thoroughly checked and package-cased.",
            "প্রোটিন সমৃদ্ধ তাজা ফার্মের লাল ডিম। সম্পূর্ণ বাছাই করা ও সুরক্ষিত প্যাকেজিং।", "🥚", true, false, 4.7f
        ),
        Product(
            "em_milk_1", "Aarong Pasteurized Milk (1L Bag)", "আড়ং পাস্তুরিত তরল দুধ (১ লিটার)", "eggs_milk",
            90.0, 95.0, 35,
            "Rich and wholesome pasteurized fat-standardized fresh liquid milk containing vital minerals.",
            "বাছাইকৃত খামারীদের খাঁটি তরল দুধ পাস্তুরিত প্রক্রিয়ায় প্যাকেটজাত করা।", "🥛", true, false, 4.6f
        ),

        // Drinks
        Product(
            "dr_mojo_1", "Mojo Cola (1L)", "মোজো কোমল পানীয় (১ লিটার)", "drinks",
            60.0, 65.0, 100,
            "Bangladeshi signature energetic black cola drink to keep you charged and refreshed.",
            "প্রাণবন্ত অনুভূতিতে চার্জড থাকতে দেশী আকর্ষনীয় কালো মোজো কোমল পানীয়।", "🥤", false, false, 4.3f
        ),
        Product(
            "dr_pranup_1", "Pran Up Lemon Lime (2L)", "প্রাণ আপ লেমন সোডা (২ লিটার)", "drinks",
            120.0, 130.0, 45,
            "Thirst-quenching fizzy lemon-lime beverage for hot Bangladeshi summer gatherings.",
            "উষ্ণ রৌদ্রজ্জ্বল দিনে আড্ডা জমিয়ে তুলতে তাজা লেবু ও লাইম স্লাইস সোডা ড্রিংক।", "🍋", false, false, 4.5f
        ),

        // Fast Food
        Product(
            "ff_singara_1", "Hot Potato Singara (4pcs)", "গরম আলু সিঙ্গারা (৪টি)", "fast_food",
            30.0, 40.0, 40,
            "Crispy golden triangles stuffed with spicy potato and peanuts. Ultimate snack.",
            "মচমচে এবং সুস্বাদু মসলাদার আলু পুরে ভরা খাঁটি গরম সিঙ্গারা। চায়ের পাশে সেরা।", "🥟", true, false, 4.8f
        ),
        Product(
            "ff_burger_1", "Deshi Chicken Burger", "দেশী চিকেন বার্গার", "fast_food",
            120.0, 150.0, 15,
            "Juicy local style chicken patty on a sesame bun with dynamic tomato sauce and mayo.",
            "তুলতুলে বনরুটির ফাঁকে মজাদার জ্যুসি দেশী চিকেন প্যাটি ও মেয়োনিজ ভরপুর।", "🍔", false, true, 4.5f
        ),

        // Household Products
        Product(
            "hs_vim_1", "Vim Bar Liquid/Bar (300g)", "ভিম বার ডিশওয়াশার (৩০০ গ্রাম)", "household",
            40.0, 45.0, 80,
            "Powerful lemon grease buster. Leaves all your kitchen utensils sparkling clean.",
            "লেবুর তীব্র ক্ষমতাসম্পন্ন এক নম্বর বাসন ধোয়ার সাবান যা তৈলাক্ত দাগ দূর করে।", "🧼", true, false, 4.7f
        ),
        Product(
            "hs_wheel_1", "Wheel Washing Powder (1kg)", "হুইল ওয়াশিং পাউডার (১ কেজি)", "household",
            110.0, 120.0, 50,
            "Removes tough dirt instantly with fresh fragrance. Keep clothes bright and white.",
            "কাপড়ের গভীর থেকে ময়লা পরিষ্কার করে দীর্ঘস্থায়ী সুবাস ও উজ্জ্বলতা রাখতে হুইল।", "🧹", false, false, 4.5f
        )
    )
}

// --- Persistence Local Store Manager ---

class LocalStoreManager(context: Context) {
    private val prefs = context.getSharedPreferences("FreshHatPrefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    // Adapters for Moshi
    private val productsAdapter = moshi.adapter<List<Product>>(
        Types.newParameterizedType(List::class.java, Product::class.java)
    )
    private val cartAdapter = moshi.adapter<List<CartItem>>(
        Types.newParameterizedType(List::class.java, CartItem::class.java)
    )
    private val ordersAdapter = moshi.adapter<List<Order>>(
        Types.newParameterizedType(List::class.java, Order::class.java)
    )
    private val userAdapter = moshi.adapter(User::class.java)

    // Lang State (true = Bangla, false = English)
    fun isBangla(): Boolean {
        return prefs.getBoolean("is_bangla", true) // Default is Bangla for local market feel
    }

    fun setBangla(isBn: Boolean) {
        prefs.edit().putBoolean("is_bangla", isBn).apply()
    }

    // Dark Mode state
    fun isDarkMode(): Boolean {
        return prefs.getBoolean("is_dark_mode", false)
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", isDark).apply()
    }

    // Products Access and Modification (Needed for admin panel adding products)
    fun getProducts(): List<Product> {
        val json = prefs.getString("products_list", null)
        return if (json != null) {
            try {
                productsAdapter.fromJson(json) ?: MarketDetails.defaultProducts
            } catch (e: Exception) {
                MarketDetails.defaultProducts
            }
        } else {
            MarketDetails.defaultProducts
        }
    }

    fun saveProducts(products: List<Product>) {
        prefs.edit().putString("products_list", productsAdapter.toJson(products)).apply()
    }

    fun addProduct(product: Product) {
        val currentList = getProducts().toMutableList()
        currentList.add(product)
        saveProducts(currentList)
    }

    // Cart Access and modification
    fun getCart(): List<CartItem> {
        val json = prefs.getString("cart_list", null)
        return if (json != null) {
            try {
                cartAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveCart(cart: List<CartItem>) {
        prefs.edit().putString("cart_list", cartAdapter.toJson(cart)).apply()
    }

    fun addToCart(productId: String, quantity: Int = 1) {
        val currentList = getCart().toMutableList()
        val existingIndex = currentList.indexOfFirst { it.productId == productId }
        if (existingIndex != -1) {
            val oldItem = currentList[existingIndex]
            currentList[existingIndex] = oldItem.copy(quantity = oldItem.quantity + quantity)
        } else {
            currentList.add(CartItem(productId, quantity))
        }
        saveCart(currentList)
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        val currentList = getCart().toMutableList()
        val index = currentList.indexOfFirst { it.productId == productId }
        if (index != -1) {
            if (quantity <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index] = currentList[index].copy(quantity = quantity)
            }
            saveCart(currentList)
        }
    }

    fun clearCart() {
        saveCart(emptyList())
    }

    // Orders Custom Database
    fun getOrders(): List<Order> {
        val json = prefs.getString("orders_list", null)
        return if (json != null) {
            try {
                ordersAdapter.fromJson(json) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun saveOrders(orders: List<Order>) {
        prefs.edit().putString("orders_list", ordersAdapter.toJson(orders)).apply()
    }

    fun addOrder(order: Order) {
        val currentList = getOrders().toMutableList()
        currentList.add(0, order) // Add to top (newest first)
        saveOrders(currentList)
    }

    fun updateOrderStatus(orderId: String, status: String, nextStep: Int) {
        val currentList = getOrders().map {
            if (it.id == orderId) {
                it.copy(status = status, trackingStep = nextStep)
            } else {
                it
            }
        }
        saveOrders(currentList)
    }

    // Current User Profile
    fun getUser(): User {
        val json = prefs.getString("user_profile", null)
        return if (json != null) {
            try {
                userAdapter.fromJson(json) ?: User("Aiman Chowdhury", "01867164726", "Patenga, Chattogram", true)
            } catch (e: Exception) {
                User("Aiman Chowdhury", "01867164726", "Patenga, Chattogram", true)
            }
        } else {
            User("Aiman Chowdhury", "01867164726", "Patenga, Chattogram", true)
        }
    }

    fun saveUser(user: User) {
        prefs.edit().putString("user_profile", userAdapter.toJson(user)).apply()
    }

    fun logout() {
        saveUser(User("", "", "", false))
    }
}
