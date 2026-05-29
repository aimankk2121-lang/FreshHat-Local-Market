/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class AppScreen {
    Home,
    Cart,
    Checkout,
    Tracking,
    Account,
    Admin
}

// Translations Dictionary
object Trans {
    private val bnMap = mapOf(
        "app_name" to "ফ্রেশহাট লোকাল মার্কেট",
        "tagline" to "তাজা বাজার সরাসরি আপনার ঘরে!",
        "search_hint" to "আলু, মাছ, সরিষার তেল খুঁজুন...",
        "categories" to "বিভাগসমূহ",
        "popular_products" to "জনপ্রিয় পণ্য",
        "offers" to "ধামাকা অফার",
        "new_arrivals" to "নতুন কালেকশন",
        "add_to_cart" to "ব্যাগে রাখুন",
        "buy_now" to "অর্ডার করুন",
        "cart_title" to "শপিং ব্যাগ",
        "empty_cart" to "আপনার ব্যাগটি খালি!",
        "checkout_btn" to "অর্ডার করতে এগিয়ে যান",
        "checkout_title" to "অর্ডার সম্পন্ন করুন",
        "name" to "আপনার নাম",
        "phone" to "মোবাইল নাম্বার",
        "address" to "ডেলিভারি ঠিকানা",
        "payment" to "পেমেন্ট মাধ্যম",
        "bkash" to "বিকাশ (bKash)",
        "nagad" to "নগদ (Nagad)",
        "cod" to "ক্যাশ অন ডেলিভারি",
        "inst_bkash" to "আমাদের বিকাশ নাম্বার ০১৮৬৭১৬৪৭২৬ -এ 'সেণ্ড মানি' করে নিচে TrxID বসান:",
        "inst_nagad" to "আমাদের নগদ নাম্বার ০১৮৬৭১৬৪৭২৬ -এ 'সেণ্ড মানি' করে নিচে TrxID বসান:",
        "trx_hint" to "বিকাশ/নগদ ট্রানজেকশন আইডি দিন",
        "order_confirm" to "অর্ডার প্লেস করুন",
        "order_success" to "অর্ডার সফল হয়েছে! ধন্যবাদ!",
        "track_order" to "অর্ডার ট্র্যাকিং",
        "order_id" to "অর্ডার আইডি",
        "status" to "অবস্থা",
        "status_pending" to "অর্ডার গৃহীত হয়েছে",
        "status_packing" to "প্যাকিং চলছে",
        "status_shipped" to "ডেলিভারি পথে",
        "status_delivered" to "ডেলিভারি সম্পন্ন",
        "bazaar_manager" to "বাজার ম্যানেজার (অ্যাডমিন)",
        "add_product" to "নতুন পণ্য যুক্ত করুন",
        "p_name_en" to "পণ্যের নাম (ইংরেজী)",
        "p_name_bn" to "পণ্যের নাম (বাংলা)",
        "p_desc_en" to "পণ্যের বর্ণনা (ইংরেজী)",
        "p_desc_bn" to "পণ্যের বর্ণনা (বাংলা)",
        "price" to "মূল্য",
        "old_price" to "ডিসকাউন্ট মূল্য (ঐচ্ছিক)",
        "stock" to "স্টক সংখ্যা",
        "p_emoji" to "আইকন / ইমোজি",
        "save_product" to "পণ্য সংরক্ষণ করুন",
        "manage_orders" to "অর্ডার ম্যানেজমেন্ট",
        "change_status" to "স্ট্যাটাস পরিবর্তন করুন",
        "discount_applied" to "১০% ডিসকাউন্ট কোড 'FRESH10' সফলভাবে অ্যাপ্লাইড!",
        "delivery_charge" to "ডেলিভারি চার্জ",
        "coupon_code" to "ডিসকাউন্ট কুপন কোড",
        "login" to "লগইন / সাইনআপ",
        "reg_title" to "মোবাইল নাম্বার দিয়ে যুক্ত হোন",
        "verify_phone" to "মোবাইল ভেরিফিকেশন কোড",
        "get_started" to "শুরু করুন",
        "subtotal" to "সাবটোটাল",
        "total" to "সর্বমোট",
        "items" to "টি আইটেম",
        "stock_left" to "টি অবশিষ্ট",
        "in_stock" to "স্টকে আছে",
        "bdt" to "৳",
        "logout" to "লগআউট"
    )

    private val enMap = mapOf(
        "app_name" to "FreshHat Local Market",
        "tagline" to "Fresh bazaar straight from farmers!",
        "search_hint" to "Search potato, hilsha, rice...",
        "categories" to "Categories",
        "popular_products" to "Popular Products",
        "offers" to "Hot Discount Offers",
        "new_arrivals" to "New Arrivals",
        "add_to_cart" to "Add to Bag",
        "buy_now" to "Order Now",
        "cart_title" to "Shopping Bag",
        "empty_cart" to "Your bag is empty!",
        "checkout_btn" to "Proceed to Checkout",
        "checkout_title" to "Complete Checkout",
        "name" to "Full Name",
        "phone" to "Mobile Number",
        "address" to "Delivery Address",
        "payment" to "Payment Method",
        "bkash" to "bKash Digital Payment",
        "nagad" to "Nagad Digital Payment",
        "cod" to "Cash On Delivery (COD)",
        "inst_bkash" to "Send Money to 01867164726, then enter Transaction ID below:",
        "inst_nagad" to "Send Money to 01867164726, then enter Transaction ID below:",
        "trx_hint" to "Enter Transaction ID here",
        "order_confirm" to "Place Confirmed Order",
        "order_success" to "Order Placed Successfully!",
        "track_order" to "Track Order",
        "order_id" to "Order ID",
        "status" to "Status",
        "status_pending" to "Order Received",
        "status_packing" to "Packing Items",
        "status_shipped" to "Out for Delivery",
        "status_delivered" to "Delivered Successfully",
        "bazaar_manager" to "Bazaar Admin Panel",
        "add_product" to "Add New Product",
        "p_name_en" to "Product Name (EN)",
        "p_name_bn" to "Product Name (BN)",
        "p_desc_en" to "Description (EN)",
        "p_desc_bn" to "Description (BN)",
        "price" to "Current Price",
        "old_price" to "Old Price (Optional)",
        "stock" to "Stock Quantity",
        "p_emoji" to "Category Icon / Emoji",
        "save_product" to "Save Product to Catalog",
        "manage_orders" to "Order Operations",
        "change_status" to "Update Delivery Level",
        "discount_applied" to "10% Coupon 'FRESH10' applied successfully!",
        "delivery_charge" to "Delivery Fee",
        "coupon_code" to "Coupon Code",
        "login" to "Login / Register",
        "reg_title" to "Register with Mobile Phone",
        "verify_phone" to "Phone OTP Code",
        "get_started" to "Proceed",
        "subtotal" to "Subtotal",
        "total" to "Total Paid",
        "items" to "items",
        "stock_left" to "units left",
        "in_stock" to "In Stock",
        "bdt" to "৳",
        "logout" to "Sign Out"
    )

    fun t(key: String, isBn: Boolean): String {
        return if (isBn) bnMap[key] ?: key else enMap[key] ?: key
    }
}

@Composable
fun FreshHatMainApp() {
    val context = LocalContext.current
    val store = remember { LocalStoreManager(context) }
    val scope = rememberCoroutineScope()

    // Application Global States
    var isBn by remember { mutableStateOf(store.isBangla()) }
    var isDark by remember { mutableStateOf(store.isDarkMode()) }
    var currentScreen by remember { mutableStateOf(AppScreen.Home) }

    var productsState by remember { mutableStateOf(store.getProducts()) }
    var cartItems by remember { mutableStateOf(store.getCart()) }
    var currentUser by remember { mutableStateOf(store.getUser()) }
    var ordersList by remember { mutableStateOf(store.getOrders()) }

    var activeCategoryFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProductForDetails by remember { mutableStateOf<Product?>(null) }

    // Coupon states
    var enteredCoupon by remember { mutableStateOf("") }
    var isCouponApplied by remember { mutableStateOf(false) }

    // Notifications Overlay
    var pushNotificationMessage by remember { mutableStateOf<String?>(null) }

    // Trigger Notification Effect
    fun triggerNotification(msg: String) {
        scope.launch {
            pushNotificationMessage = msg
            delay(4000)
            pushNotificationMessage = null
        }
    }

    // Initialize/Sync states on change
    LaunchedEffect(isBn) { store.setBangla(isBn) }
    LaunchedEffect(isDark) { store.setDarkMode(isDark) }

    MyApplicationTheme(darkTheme = isDark) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // --- Brand Header App Bar ---
                HeaderBar(
                    isBn = isBn,
                    isDark = isDark,
                    cartCount = cartItems.sumOf { it.quantity },
                    onCartClick = { currentScreen = AppScreen.Cart },
                    onLangToggle = { isBn = !isBn },
                    onThemeToggle = { isDark = !isDark },
                    onProfileClick = { currentScreen = AppScreen.Account },
                    onAdminClick = { currentScreen = AppScreen.Admin }
                )

                // --- Main Container Area ---
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screenTransition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.Home -> {
                                HomeScreen(
                                    isBn = isBn,
                                    products = productsState,
                                    activeCategory = activeCategoryFilter,
                                    searchQuery = searchQuery,
                                    onCategorySelect = { activeCategoryFilter = it },
                                    onSearchChange = { searchQuery = it },
                                    onProductSelect = { selectedProductForDetails = it },
                                    onAddToCart = { prod ->
                                        store.addToCart(prod.id, 1)
                                        cartItems = store.getCart()
                                        triggerNotification("🛒 " + if (isBn) "${prod.nameBn} ব্যাগে যুক্ত করা হয়েছে!" else "${prod.nameEn} added to Shopping Bag!")
                                    },
                                    onBuyNow = { prod ->
                                        store.addToCart(prod.id, 1)
                                        cartItems = store.getCart()
                                        currentScreen = AppScreen.Cart
                                    }
                                )
                            }
                            AppScreen.Cart -> {
                                CartScreen(
                                    isBn = isBn,
                                    cartList = cartItems,
                                    products = productsState,
                                    couponApplied = isCouponApplied,
                                    couponCode = enteredCoupon,
                                    onCouponValueChange = { enteredCoupon = it },
                                    onApplyCoupon = {
                                        if (enteredCoupon.uppercase() == "FRESH10") {
                                            isCouponApplied = true
                                            triggerNotification(Trans.t("discount_applied", isBn))
                                        } else {
                                            triggerNotification(if (isBn) "ভুল কুপন কোড!" else "Invalid coupon code!")
                                        }
                                    },
                                    onQuantityChange = { prodId, q ->
                                        store.updateCartQuantity(prodId, q)
                                        cartItems = store.getCart()
                                    },
                                    onCheckoutClick = {
                                        if (currentUser.isLoggedIn) {
                                            currentScreen = AppScreen.Checkout
                                        } else {
                                            triggerNotification(if (isBn) "অর্ডার করতে দয়া করে আগে লগইন করুন" else "Please registration/login first to checkout.")
                                            currentScreen = AppScreen.Account
                                        }
                                    },
                                    onGoShop = { currentScreen = AppScreen.Home }
                                )
                            }
                            AppScreen.Checkout -> {
                                CheckoutScreen(
                                    isBn = isBn,
                                    cartList = cartItems,
                                    products = productsState,
                                    couponApplied = isCouponApplied,
                                    user = currentUser,
                                    onOrderPlaced = { order ->
                                        store.addOrder(order)
                                        store.clearCart()
                                        cartItems = emptyList()
                                        ordersList = store.getOrders()
                                        isCouponApplied = false
                                        enteredCoupon = ""
                                        triggerNotification("🎉 " + Trans.t("order_success", isBn))
                                        currentScreen = AppScreen.Tracking
                                    },
                                    onBack = { currentScreen = AppScreen.Cart }
                                )
                            }
                            AppScreen.Tracking -> {
                                TrackingScreen(
                                    isBn = isBn,
                                    orders = ordersList,
                                    products = productsState,
                                    onGoShopping = { currentScreen = AppScreen.Home }
                                )
                            }
                            AppScreen.Account -> {
                                AccountScreen(
                                    isBn = isBn,
                                    user = currentUser,
                                    orders = ordersList,
                                    products = productsState,
                                    onSaveUser = { newUser ->
                                        store.saveUser(newUser)
                                        currentUser = store.getUser()
                                        triggerNotification(if (isBn) "প্রোফাইল আপডেট হয়েছে!" else "Profile updated successfully!")
                                    },
                                    onLogout = {
                                        store.logout()
                                        currentUser = store.getUser()
                                        triggerNotification(if (isBn) "লগআউট হয়েছেন" else "Successfully signed out.")
                                    }
                                )
                            }
                            AppScreen.Admin -> {
                                AdminScreen(
                                    isBn = isBn,
                                    store = store,
                                    onProductUpdated = {
                                        productsState = store.getProducts()
                                        triggerNotification(if (isBn) "পণ্য তালিকা আপডেট করা হয়েছে!" else "Bazaar products catalog updated!")
                                    },
                                    onStatusChanged = {
                                        ordersList = store.getOrders()
                                        triggerNotification(if (isBn) "নতুন ডেলিভারি লেভেল ট্র্যাকিং আপডেট হয়েছে!" else "Delivery track parameters adjusted!")
                                    }
                                )
                            }
                        }
                    }
                }

                // --- Stylized Bottom Bar Navigation ---
                BottomNavigationBar(
                    isBn = isBn,
                    activeScreen = currentScreen,
                    cartCount = cartItems.sumOf { it.quantity },
                    onNavigate = { currentScreen = it }
                )
            }

            // --- Product Details Dialog Overlay ---
            selectedProductForDetails?.let { prod ->
                DetailsOverlay(
                    product = prod,
                    isBn = isBn,
                    onDismiss = { selectedProductForDetails = null },
                    onAddToCart = {
                        store.addToCart(prod.id, 1)
                        cartItems = store.getCart()
                        selectedProductForDetails = null
                        triggerNotification("🛒 " + if (isBn) "${prod.nameBn} ব্যাগে যুক্ত করা হয়েছে!" else "${prod.nameEn} added to Shopping Bag!")
                    },
                    onBuyNow = {
                        store.addToCart(prod.id, 1)
                        cartItems = store.getCart()
                        selectedProductForDetails = null
                        currentScreen = AppScreen.Cart
                    }
                )
            }

            // --- Animated Toast Push Notifications ---
            AnimatedVisibility(
                visible = pushNotificationMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .padding(horizontal = 20.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = FreshGreen,
                        contentColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pushNotificationMessage ?: "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ================= HEADER BAR =================
@Composable
fun HeaderBar(
    isBn: Boolean,
    isDark: Boolean,
    cartCount: Int,
    onCartClick: () -> Unit,
    onLangToggle: () -> Unit,
    onThemeToggle: () -> Unit,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    val barColor = if (isDark) CardDarkBg else Color.White
    val contentColor = if (isDark) Color.White else Color(0xFF1E293B) // slate-800
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0) // slate-200

    Surface(
        color = barColor,
        contentColor = contentColor,
        modifier = Modifier.fillMaxWidth().border(width = 0.5.dp, color = dividerColor)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(if (isDark) SoftGreen.copy(0.15f) else LightGreenBg, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👒", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Trans.t("app_name", isBn),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else FreshGreen
                        )
                        Text(
                            text = Trans.t("tagline", isBn),
                            fontSize = 10.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF64748B), // slate-500
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // System Lang Switch
                    TextButton(
                        onClick = onLangToggle,
                        colors = ButtonDefaults.textButtonColors(contentColor = if (isDark) Color.White else FreshGreen)
                    ) {
                        Text(
                            text = if (isBn) "ENG" else "বাংলা",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(1.dp, if (isDark) Color.White.copy(alpha = 0.4f) else FreshGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Night/Day Selector
                    IconButton(onClick = onThemeToggle) {
                        Text(
                            text = if (isDark) "☀️" else "🌙",
                            fontSize = 20.sp
                        )
                    }

                    // Cart Icon Shortcut
                    Box {
                        IconButton(onClick = onCartClick) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Shopping List",
                                tint = if (isDark) Color.White else Color(0xFF475569) // slate-600
                            )
                        }
                        if (cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(17.dp)
                                    .background(BkashPink, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Small Avatar Profile Icon Shortcut
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clickable(onClick = onProfileClick)
                            .size(32.dp)
                            .border(width = 1.5.dp, color = FreshGreen, shape = CircleShape)
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.profile_img_1780039480588),
                            contentDescription = "Profile Link",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }
}

// ================= BOTTOM BAR =================
@Composable
fun BottomNavigationBar(
    isBn: Boolean,
    activeScreen: AppScreen,
    cartCount: Int,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.height(68.dp)
    ) {
        val items = listOf(
            Triple(AppScreen.Home, Icons.Default.Home, if (isBn) "বাজার" else "Bazaar"),
            Triple(AppScreen.Cart, Icons.Default.ShoppingCart, if (isBn) "ব্যাগ" else "Bag"),
            Triple(AppScreen.Tracking, Icons.Default.LocationOn, if (isBn) "ট্র্যাকিং" else "Tracking"),
            Triple(AppScreen.Account, Icons.Default.Person, if (isBn) "পছন্দ" else "Account"),
            Triple(AppScreen.Admin, Icons.Default.Settings, if (isBn) "অ্যান্ডমিন" else "Admin")
        )

        items.forEach { (screen, icon, label) ->
            val selected = activeScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Box {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (selected) FreshGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (screen == AppScreen.Cart && cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 10.dp, y = (-10).dp)
                                    .size(16.dp)
                                    .background(BkashPink, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) FreshGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = SoftGreen.copy(alpha = 0.2f)
                )
            )
        }
    }
}

// ================= HOME SCREEN =================
@Composable
fun HomeScreen(
    isBn: Boolean,
    products: List<Product>,
    activeCategory: String?,
    searchQuery: String,
    onCategorySelect: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onProductSelect: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onBuyNow: (Product) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Banner Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text(Trans.t("search_hint", isBn), fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                singleLine = true
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // Bangladeshi bazaar visual banner element
            item {
                Spacer(modifier = Modifier.height(10.dp))
                BannerPromoCard(isBn = isBn)
            }

            // Categories horizontal list
            item {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = Trans.t("categories", isBn),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 12.dp)
                ) {
                    item {
                        CategoryBadge(
                            title = if (isBn) "সব সামগ্রী" else "All Bazaar",
                            emoji = "🎒",
                            selected = activeCategory == null,
                            color = FreshGreen,
                            onClick = { onCategorySelect(null) }
                        )
                    }
                    items(MarketDetails.categories) { cat ->
                        CategoryBadge(
                            title = if (isBn) cat.nameBn else cat.nameEn,
                            emoji = cat.emoji,
                            selected = activeCategory == cat.id,
                            color = Color(cat.colorHex),
                            onClick = { onCategorySelect(cat.id) }
                        )
                    }
                }
            }

            // Offers Section If showing all, or selected is offer related
            val filteredProducts = products.filter { prod ->
                val matchesCat = activeCategory == null || prod.categoryId == activeCategory
                val matchesSearch = searchQuery.isEmpty() ||
                        prod.nameEn.lowercase().contains(searchQuery.lowercase()) ||
                        prod.nameBn.contains(searchQuery) ||
                        prod.descriptionEn.lowercase().contains(searchQuery.lowercase()) ||
                        prod.descriptionBn.contains(searchQuery)
                matchesCat && matchesSearch
            }

            if (activeCategory == null && searchQuery.isEmpty()) {
                // Flash offers row
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = Trans.t("offers", isBn),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BkashPink
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(end = 10.dp)
                    ) {
                        val discountItems = products.filter { it.oldPrice > it.currentPrice }
                        items(discountItems) { item ->
                            OfferItemCard(
                                product = item,
                                isBn = isBn,
                                onClick = { onProductSelect(item) },
                                onAddToCart = { onAddToCart(item) }
                            )
                        }
                    }
                }
            }

            // Main Product Grid Label
            item {
                Spacer(modifier = Modifier.height(20.dp))
                val titleLabel = if (activeCategory != null) {
                    val found = MarketDetails.categories.find { it.id == activeCategory }
                    if (isBn) found?.nameBn ?: "" else found?.nameEn ?: ""
                } else {
                    Trans.t("popular_products", isBn)
                }
                Text(
                    text = "$titleLabel (${filteredProducts.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(filteredProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        ProductGridItem(
                            product = pair[0],
                            isBn = isBn,
                            onClick = { onProductSelect(pair[0]) },
                            onAddToCart = { onAddToCart(pair[0]) },
                            onBuyNow = { onBuyNow(pair[0]) }
                        )
                    }

                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProductGridItem(
                                product = pair[1],
                                isBn = isBn,
                                onClick = { onProductSelect(pair[1]) },
                                onAddToCart = { onAddToCart(pair[1]) },
                                onBuyNow = { onBuyNow(pair[1]) }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🥬", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isBn) "কোনো পণ্য পাওয়া যায়নি!" else "No products matching your search!",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Banner Promo Element
@Composable
fun BannerPromoCard(isBn: Boolean) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(FreshGreen, SoftGreen)
                    )
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(BkashPink, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBn) "ভাউচার কুপন: FRESH10" else "Promo: FRESH10",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn) "আজকের তাজা দেশী বাজার" else "Fresh Local Bazaar Delivered",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (isBn) "১০% প্রথম অর্ডারে কুপন ডিসকাউন্ট" else "10% Super Discount on Home grocery",
                        color = Color.White.copy(alpha = 0.9f) ,
                        fontSize = 11.sp
                    )
                }
                Text("🚜🥦", fontSize = 54.sp)
            }
        }
    }
}

// Horizontal scroll Badges
@Composable
fun CategoryBadge(
    title: String,
    emoji: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) color else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) color else MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        tonalElevation = if (selected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// Horizontal Flash Offer Cards
@Composable
fun OfferItemCard(
    product: Product,
    isBn: Boolean,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, BkashPink.copy(0.3f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(LightGreenBg.copy(0.4f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 38.sp)
                // Percent OFF
                val percentage = (((product.oldPrice - product.currentPrice) / product.oldPrice) * 100).toInt()
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .background(BkashPink, RoundedCornerShape(bottomEnd = 12.dp, topStart = 8.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text("$percentage% ${if (isBn) "ছাড়" else "OFF"}", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isBn) product.nameBn else product.nameEn,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${Trans.t("price_symbol", isBn)} ${product.currentPrice.toInt()}",
                    fontSize = 12.sp,
                    color = FreshGreen,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "${product.oldPrice.toInt()}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onAddToCart,
                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                contentPadding = PaddingValues(horizontal = 6.dp),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                Text(Trans.t("add_to_cart", isBn), fontSize = 10.sp, color = Color.White)
            }
        }
    }
}

// Grid layout matching real platform
@Composable
fun ProductGridItem(
    product: Product,
    isBn: Boolean,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Visual element
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(LightGreenBg.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 48.sp)
                if (product.isBestSeller) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(NagadOrange, RoundedCornerShape(bottomStart = 8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBn) "সেরা বিক্রয়" else "Best Choice",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rating Stars
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = NagadOrange, modifier = Modifier.size(11.dp))
                Text(product.rating.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 2.dp))
                Spacer(modifier = Modifier.width(6.dp))
                val isLow = product.stock <= 5
                Text(
                    text = if (isLow) "${product.stock}${Trans.t("stock_left", isBn)}" else Trans.t("in_stock", isBn),
                    fontSize = 9.sp,
                    color = if (isLow) BkashPink else FreshGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBn) product.nameBn else product.nameEn,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = if (isBn) product.descriptionBn else product.descriptionEn,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp,
                modifier = Modifier.height(26.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            LogisticalPriceRow(product = product, isBn = isBn)

            Spacer(modifier = Modifier.height(8.dp))

            // Dual Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                OutlinedButton(
                    onClick = onAddToCart,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FreshGreen),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(Trans.t("add_to_cart", isBn), fontSize = 10.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBuyNow,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                ) {
                    Text(Trans.t("buy_now", isBn), fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LogisticalPriceRow(product: Product, isBn: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "${Trans.t("price_symbol", isBn)} ${product.currentPrice.toInt()}",
                fontSize = 14.sp,
                color = FreshGreen,
                fontWeight = FontWeight.ExtraBold
            )
            if (product.oldPrice > product.currentPrice) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${product.oldPrice.toInt()}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
            }
        }
    }
}

// ================= CART SCREEN =================
@Composable
fun CartScreen(
    isBn: Boolean,
    cartList: List<CartItem>,
    products: List<Product>,
    couponApplied: Boolean,
    couponCode: String,
    onCouponValueChange: (String) -> Unit,
    onApplyCoupon: () -> Unit,
    onQuantityChange: (String, Int) -> Unit,
    onCheckoutClick: () -> Unit,
    onGoShop: () -> Unit
) {
    if (cartList.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🛒", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = Trans.t("empty_cart", isBn),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onGoShop,
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(if (isBn) "চলুন বাজার করি" else "Go Shopping", color = Color.White)
            }
        }
    } else {
        val mappedItems = cartList.mapNotNull { cartItem ->
            val p = products.find { it.id == cartItem.productId }
            if (p != null) cartItem to p else null
        }

        val subtotal = mappedItems.sumOf { (cartItem, prod) -> prod.currentPrice * cartItem.quantity }
        val discount = if (couponApplied) subtotal * 0.1 else 0.0
        val total = subtotal - discount + 40.0 // 40 Taka Delivery fee

        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = Trans.t("cart_title", isBn) + " (${cartList.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(14.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                items(mappedItems) { (cartItem, prod) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(LightGreenBg.copy(0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(prod.emoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isBn) prod.nameBn else prod.nameEn,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${Trans.t("price_symbol", isBn)} ${prod.currentPrice.toInt()}",
                                    fontSize = 12.sp,
                                    color = FreshGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Quantity selectors
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(1.dp, Color.Gray.copy(0.3f), RoundedCornerShape(16.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { onQuantityChange(prod.id, cartItem.quantity - 1) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                                }
                                Text(
                                    text = cartItem.quantity.toString(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { onQuantityChange(prod.id, cartItem.quantity + 1) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                                }
                            }
                        }
                    }
                }

                // Coupon implementation section
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = Trans.t("coupon_code", isBn),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = couponCode,
                                    onValueChange = onCouponValueChange,
                                    placeholder = { Text("e.g. FRESH10", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = onApplyCoupon,
                                    colors = ButtonDefaults.buttonColors(containerColor = NagadOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Text(if (isBn) "প্রয়োগ" else "Apply", color = Color.White)
                                }
                            }
                            if (couponApplied) {
                                Text(
                                    text = "✓ FRESH10 (10% OFF) Applied",
                                    color = FreshGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Price totalizations
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        PriceLine(Trans.t("subtotal", isBn), subtotal, isBn)
                        if (discount > 0) {
                            PriceLine(if (isBn) "১০% কুপন ডিসকাউন্ট" else "10% Coupon Discount", -discount, isBn, labelColor = BkashPink)
                        }
                        PriceLine(Trans.t("delivery_charge", isBn), 40.0, isBn)
                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(Trans.t("total", isBn), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            Text(
                                text = "${Trans.t("price_symbol", isBn)} ${total.toInt()}",
                                fontSize = 18.sp,
                                color = FreshGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Action Button
            Button(
                onClick = onCheckoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(50.dp)
            ) {
                Text(Trans.t("checkout_btn", isBn), fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PriceLine(label: String, valRaw: Double, isBn: Boolean, labelColor: Color = Color.Unspecified) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = if (labelColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface.copy(0.7f) else labelColor)
        val sign = if (valRaw < 0) "- " else ""
        Text(
            text = "$sign${Trans.t("price_symbol", isBn)} ${Math.abs(valRaw).toInt()}",
            fontSize = 12.sp,
            color = if (valRaw < 0) BkashPink else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ================= CHECKOUT SCREEN =================
@Composable
fun CheckoutScreen(
    isBn: Boolean,
    cartList: List<CartItem>,
    products: List<Product>,
    couponApplied: Boolean,
    user: User,
    onOrderPlaced: (Order) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(user.name.ifEmpty { "Aiman Chowdhury" }) }
    var phone by remember { mutableStateOf(user.phone.ifEmpty { "01867164726" }) }
    var address by remember { mutableStateOf(user.address.ifEmpty { if (isBn) "পতেঙ্গা সমুদ্র সৈকত এলাকা, চট্টগ্রাম" else "Patenga Beach Road, Chattogram" }) }

    // COD vs Digital Payment Settings
    var selectedPaymentType by remember { mutableStateOf("COD") } // COD, bKash, Nagad
    var transactionIdInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf<String?>(null) }

    val mappedItems = cartList.mapNotNull { cartItem ->
        val p = products.find { it.id == cartItem.productId }
        if (p != null) cartItem to p else null
    }
    val subtotal = mappedItems.sumOf { (cartItem, prod) -> prod.currentPrice * cartItem.quantity }
    val discount = if (couponApplied) subtotal * 0.1 else 0.0
    val total = subtotal - discount + 40.0 // 40 Tk delivery fee

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Custom Back Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = FreshGreen)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = Trans.t("checkout_title", isBn),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FreshGreen
            )
        }
            Spacer(modifier = Modifier.height(10.dp))
            Text(if (isBn) "১. গ্রাহকের ডেলিভারি তথ্য" else "1. Delivery Information", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(Trans.t("name", isBn)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(Trans.t("phone", isBn)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(Trans.t("address", isBn)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(18.dp))
            Text(if (isBn) "২. পেমেন্ট মাধ্যম নির্ধারণ" else "2. Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // COD Option Card
            PaymentOptionRow(
                title = Trans.t("cod", isBn),
                logoText = "💵",
                selected = selectedPaymentType == "COD",
                color = FreshGreen,
                onClick = { selectedPaymentType = "COD" }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // bKash Option Card
            PaymentOptionRow(
                title = Trans.t("bkash", isBn),
                logoText = "bKash",
                selected = selectedPaymentType == "bKash",
                color = BkashPink,
                onClick = { selectedPaymentType = "bKash" }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Nagad Option Card
            PaymentOptionRow(
                title = Trans.t("nagad", isBn),
                logoText = "Nagad",
                selected = selectedPaymentType == "Nagad",
                color = NagadOrange,
                onClick = { selectedPaymentType = "Nagad" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Sender Subforms
            if (selectedPaymentType == "bKash" || selectedPaymentType == "Nagad") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPaymentType == "bKash") BkashPink.copy(0.06f) else NagadOrange.copy(0.06f)
                    ),
                    border = BorderStroke(1.dp, if (selectedPaymentType == "bKash") BkashPink.copy(0.3f) else NagadOrange.copy(0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = if (selectedPaymentType == "bKash") Trans.t("inst_bkash", isBn) else Trans.t("inst_nagad", isBn),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedPaymentType == "bKash") BkashPink else NagadOrange,
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text("০১৮৬৭১৬৪৭২৬", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                if (isBn) "(টাকা পাঠানোর নাম্বার)" else "(Payment Send-Money No.)",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = transactionIdInput,
                            onValueChange = { transactionIdInput = it },
                            placeholder = { Text(Trans.t("trx_hint", isBn), fontSize = 12.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            inputError?.let {
                Text(
                    text = it,
                    color = BkashPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cost Recap inside Button Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (isBn) "পরিশোধযোগ্য মোট মূল্য" else "Total Payable Amount", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = "${Trans.t("price_symbol", isBn)} ${total.toInt()}",
                    fontSize = 17.sp,
                    color = FreshGreen,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                        inputError = if (isBn) "অনুগ্রহ করে সব তথ্য দিন!" else "Please fill up all delivery parameters!"
                        return@Button
                    }
                    if ((selectedPaymentType == "bKash" || selectedPaymentType == "Nagad") && transactionIdInput.trim().isEmpty()) {
                        inputError = if (isBn) "অনুগ্রহ করে Transaction ID বসান!" else "Transaction ID cannot be blank!"
                        return@Button
                    }

                    inputError = null
                    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                    val order = Order(
                        id = "REF" + (10000 + Random().nextInt(90000)),
                        customerName = name,
                        phoneNumber = phone,
                        deliveryAddress = address,
                        paymentMethod = selectedPaymentType,
                        transactionId = if (selectedPaymentType == "COD") "CASH_ON_DELIVERY" else transactionIdInput.uppercase().trim(),
                        items = cartList,
                        totalPrice = total,
                        status = "Pending",
                        timestamp = dateStr,
                        trackingStep = 1
                    )
                    onOrderPlaced(order)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(Trans.t("order_confirm", isBn), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
}

@Composable
fun PaymentOptionRow(
    title: String,
    logoText: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, if (selected) color else Color.Gray.copy(0.2f)),
        color = if (selected) color.copy(0.04f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(logoText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ================= TRACKING SCREEN =================
@Composable
fun TrackingScreen(
    isBn: Boolean,
    orders: List<Order>,
    products: List<Product>,
    onGoShopping: () -> Unit
) {
    if (orders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🚚", fontSize = 72.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (isBn) "কোনো একটিভ অর্ডার নেই!" else "No orders placed yet!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onGoShopping,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen)
            ) {
                Text(if (isBn) "কাঁচা বাজার দেখুন" else "Explore Fresh Items", color = Color.White)
            }
        }
    } else {
        // Track the first active order
        val order = orders.first()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            item {
                Text(
                    text = Trans.t("track_order", isBn),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 14.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "${Trans.t("order_id", isBn)}: #${order.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = FreshGreen
                                )
                                Text(order.timestamp, fontSize = 11.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .background(
                                        when (order.trackingStep) {
                                            4 -> FreshGreen
                                            else -> NagadOrange
                                        }, RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val statusText = when (order.trackingStep) {
                                    1 -> Trans.t("status_pending", isBn)
                                    2 -> Trans.t("status_packing", isBn)
                                    3 -> Trans.t("status_shipped", isBn)
                                    4 -> Trans.t("status_delivered", isBn)
                                    else -> order.status
                                }
                                Text(statusText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Text(
                            text = if (isBn) "২ কেজি আলু, ১ কেজি রুই মাছ..." else "Order contains:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.7f)
                        )

                        order.items.forEach { item ->
                            val p = products.find { it.id == item.productId }
                            p?.let {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• ${if (isBn) it.nameBn else it.nameEn} x ${item.quantity}", fontSize = 12.sp)
                                    Text("${Trans.t("price_symbol", isBn)} ${(it.currentPrice * item.quantity).toInt()}", fontSize = 12.sp)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBn) "পরিশোধের মাধ্যম:" else "Payment Method:", fontSize = 12.sp, color = Color.Gray)
                            Text(order.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBn) "মোট টাকা:" else "Total Paid:", fontSize = 12.sp, color = Color.Gray)
                            Text("${Trans.t("price_symbol", isBn)} ${order.totalPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                        }
                    }
                }
            }

            // Interactive Step Progress UI matching actual tracking app
            val stepsData = listOf(
                1 to ("status_pending" to "We are processing your bazaar bag."),
                2 to ("status_packing" to "Our local helper is selecting fresh vegetables."),
                3 to ("status_shipped" to "Rider is heading to your house."),
                4 to ("status_delivered" to "Your organic items successfully arrived.")
            )

            items(stepsData) { (step, textPair) ->
                val (titleKey, subtext) = textPair
                val activeOrCompleted = order.trackingStep >= step

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(if (activeOrCompleted) FreshGreen else Color.Gray.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (order.trackingStep > step) {
                                Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(14.dp))
                            } else {
                                Text(step.toString(), color = if (activeOrCompleted) Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (step < 4) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(if (order.trackingStep > step) FreshGreen else Color.Gray.copy(0.2f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = Trans.t(titleKey, isBn),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeOrCompleted) FreshGreen else Color.Gray
                        )
                        Text(
                            text = if (isBn) "আপনার তাজা অর্ডারের পরবর্তী ধাপ পর্যালোচনা করা হচ্ছে।" else subtext,
                            fontSize = 11.sp,
                            color = if (activeOrCompleted) MaterialTheme.colorScheme.onBackground.copy(0.7f) else Color.Gray.copy(0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

// ================= ACCOUNT PROFILE =================
@Composable
fun AccountScreen(
    isBn: Boolean,
    user: User,
    orders: List<Order>,
    products: List<Product>,
    onSaveUser: (User) -> Unit,
    onLogout: () -> Unit
) {
    var nameInput by remember { mutableStateOf(user.name) }
    var phoneInput by remember { mutableStateOf(user.phone) }
    var addressInput by remember { mutableStateOf(user.address) }

    // Mock Login phone validation states
    var phoneForLogin by remember { mutableStateOf("") }
    var otpField by remember { mutableStateOf("") }
    var loginStep by remember { mutableStateOf(1) } // 1 = Phone input, 2 = OTP check

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (!user.isLoggedIn) {
            // Logged out: Register flow
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = Trans.t("login", isBn),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreshGreen
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = Trans.t("reg_title", isBn),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (loginStep == 1) {
                        OutlinedTextField(
                            value = phoneForLogin,
                            onValueChange = { phoneForLogin = it },
                            label = { Text(Trans.t("phone", isBn)) },
                            placeholder = { Text("01XXXXXXXXX") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                if (phoneForLogin.length >= 11) {
                                    loginStep = 2
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isBn) "ওটিপি (OTP) পাঠান" else "Send Verification Code", color = Color.White)
                        }
                    } else {
                        Text(
                            text = "${Trans.t("verify_phone", isBn)} (OTP) sent to $phoneForLogin",
                            fontSize = 12.sp,
                            color = FreshGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = otpField,
                            onValueChange = { otpField = it },
                            label = { Text("OTP code (e.g., 1234)") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onSaveUser(
                                    User(
                                        name = "Aiman Chowdhury",
                                        phone = phoneForLogin,
                                        address = if (isBn) "পতেঙ্গা সমুদ্র সৈকত এলাকা, চট্টগ্রাম" else "Patenga Beach Road, Chattogram",
                                        isLoggedIn = true
                                    )
                                )
                                loginStep = 1
                                phoneForLogin = ""
                                otpField = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(Trans.t("get_started", isBn), color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { loginStep = 1 }) {
                            Text(if (isBn) "ভুল নাম্বার? পরিবর্তন করুন" else "Change Phone Number", color = Color.Gray)
                        }
                    }
                }
            }
        } else {
            // Logged in profile settings
            Text(
                text = if (isBn) "আপনার প্রোফাইল" else "User Account settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = FreshGreen
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(FreshGreen, SoftGreen)
                                        ),
                                        shape = CircleShape
                                    )
                                    .padding(4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = com.example.R.drawable.profile_img_1780039480588),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = user.name.ifEmpty { "Aiman Chowdhury" },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FreshGreen
                            )
                            Text(
                                text = if (isBn) "📍 পতেঙ্গা, চট্টগ্রাম" else "📍 Patenga, Chattogram",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text(Trans.t("name", isBn)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text(Trans.t("phone", isBn)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = { addressInput = it },
                        label = { Text(Trans.t("address", isBn)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                onSaveUser(user.copy(name = nameInput, phone = phoneInput, address = addressInput))
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isBn) "তথ্য পরিবর্তন করুন" else "Update Info", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onLogout,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BkashPink),
                            modifier = Modifier.weight(0.8f)
                        ) {
                            Text(Trans.t("logout", isBn), color = BkashPink)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (isBn) "পূর্বের অডারসমূহ" else "Order History logs",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            orders.forEach { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("#${order.id}", fontWeight = FontWeight.Bold, color = FreshGreen)
                            Text(order.timestamp, fontSize = 11.sp, color = Color.Gray)
                        }
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBn) "অবস্থা: ${order.status}" else "Status: ${order.status}", fontSize = 12.sp, color = NagadOrange, fontWeight = FontWeight.Bold)
                            Text("${Trans.t("price_symbol", isBn)} ${order.totalPrice.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ================= ADMIN SCREEN =================
@Composable
fun AdminScreen(
    isBn: Boolean,
    store: LocalStoreManager,
    onProductUpdated: () -> Unit,
    onStatusChanged: () -> Unit
) {
    var pNameEn by remember { mutableStateOf("") }
    var pNameBn by remember { mutableStateOf("") }
    var pCatId by remember { mutableStateOf("grocery") }
    var pPrice by remember { mutableStateOf("") }
    var pOldPrice by remember { mutableStateOf("") }
    var pStock by remember { mutableStateOf("") }
    var pDescEn by remember { mutableStateOf("") }
    var pDescBn by remember { mutableStateOf("") }
    var pEmoji by remember { mutableStateOf("🍅") }

    val ordersList = remember(store) { store.getOrders() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = Trans.t("bazaar_manager", isBn),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = FreshGreen
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Bento Metrics Row from the Sleek Interface
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color.Gray.copy(0.12f)),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "আজকের অর্ডারের সংখ্যা" else "DAILY ORDERS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${1482 + ordersList.size}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreshGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("▲ 12.5%", fontSize = 11.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color.Gray.copy(0.12f)),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "মোট রাজস্ব (টাকা)" else "REVENUE (BDT)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val dynamicRev = ordersList.sumOf { it.totalPrice }.toInt() + 82340
                    Text(
                        text = "৳${dynamicRev}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = FreshGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("▲ 8.2%", fontSize = 11.sp, color = FreshGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PAYMENT VERIFICATION MODULE (bKash/Nagad) from the Sleek Interface spec
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, Color.Gray.copy(0.12f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "পেমেন্ট যাচাইকরণ" else "Payment Verification",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isBn) "৩টি পেন্ডিং" else "3 Pending",
                            color = Color(0xFFB45309),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Pending Transaction 1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray.copy(0.04f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(BkashPink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("BK", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Hossain Ahmed", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("TRX ID: 9K2J8H3L7", fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("৳1,250", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            onClick = { },
                            color = FreshGreen,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(22.dp)
                        ) {
                            Text(
                                text = if (isBn) "অনুমোদন" else "APPROVE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pending Transaction 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Gray.copy(0.04f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(NagadOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("NG", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Fatima Begum", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("TRX ID: 4M1N9P2Q", fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("৳4,800", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FreshGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            onClick = { },
                            color = FreshGreen,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(22.dp)
                        ) {
                            Text(
                                text = if (isBn) "অনুমোদন" else "APPROVE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = if (isBn) "নতুন পণ্য" else "Add Dynamic Products",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = FreshGreen
        )
        Spacer(modifier = Modifier.height(6.dp))
        Spacer(modifier = Modifier.height(10.dp))

        // ADD NEW PRODUCTS FORM
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = Trans.t("add_product", isBn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FreshGreen
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = pNameEn,
                    onValueChange = { pNameEn = it },
                    label = { Text(Trans.t("p_name_en", isBn)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = pNameBn,
                    onValueChange = { pNameBn = it },
                    label = { Text(Trans.t("p_name_bn", isBn)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Dropdown mock category selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isBn) "ক্যাটাগরি" else "Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        MarketDetails.categories.forEach { cat ->
                            val selected = pCatId == cat.id
                            Surface(
                                onClick = { pCatId = cat.id },
                                shape = RoundedCornerShape(4.dp),
                                color = if (selected) FreshGreen else Color.Gray.copy(0.1f),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = if (isBn) cat.nameBn else cat.nameEn,
                                    fontSize = 10.sp,
                                    color = if (selected) Color.White else Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pPrice,
                        onValueChange = { pPrice = it },
                        label = { Text(Trans.t("price", isBn)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pOldPrice,
                        onValueChange = { pOldPrice = it },
                        label = { Text("Old Price") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = pStock,
                        onValueChange = { pStock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = pDescEn,
                    onValueChange = { pDescEn = it },
                    label = { Text(Trans.t("p_desc_en", isBn)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = pDescBn,
                    onValueChange = { pDescBn = it },
                    label = { Text(Trans.t("p_desc_bn", isBn)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Select Emoji list
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isBn) "ইমোজি বাছুন:" else "Emoji Icon:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    listOf("🍎", "🥦", "🦐", "🐟", "🥩", "🍼", "🧼", "🌾", "🍟", "🐓", "🧅", "🍌").forEach { em ->
                        Text(
                            text = em,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clickable { pEmoji = em }
                                .padding(horizontal = 4.dp)
                                .border(
                                    width = 1.5.dp,
                                    color = if (pEmoji == em) FreshGreen else Color.Transparent,
                                    shape = CircleShape
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val current = pPrice.toDoubleOrNull() ?: 100.0
                        val oldStr = pOldPrice.toDoubleOrNull() ?: current
                        val stockInt = pStock.toIntOrNull() ?: 20
                        if (pNameEn.isNotEmpty() && pNameBn.isNotEmpty()) {
                            val newP = Product(
                                id = "P" + (1000 + Random().nextInt(9000)),
                                nameEn = pNameEn,
                                nameBn = pNameBn,
                                categoryId = pCatId,
                                currentPrice = current,
                                oldPrice = oldStr,
                                stock = stockInt,
                                descriptionEn = pDescEn.ifEmpty { "Fresh bazaar product imported locally." },
                                descriptionBn = pDescBn.ifEmpty { "স্থানীয় খামার থেকে সংগৃহীত তাজা পণ্য" },
                                emoji = pEmoji,
                                isBestSeller = false,
                                isNewArrival = true
                            )
                            store.addProduct(newP)
                            onProductUpdated()

                            // Clear Form
                            pNameEn = ""
                            pNameBn = ""
                            pPrice = ""
                            pOldPrice = ""
                            pStock = ""
                            pDescEn = ""
                            pDescBn = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(Trans.t("save_product", isBn), color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MANAGE INCOMING CUSTOMER ORDERS
        Text(
            text = Trans.t("manage_orders", isBn) + " (${ordersList.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        ordersList.forEach { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("OrderID: #${order.id}", fontWeight = FontWeight.Bold, color = FreshGreen)
                    Text("Customer: ${order.customerName} (${order.phoneNumber})", fontSize = 11.sp)
                    Text("Address: ${order.deliveryAddress}", fontSize = 11.sp, color = Color.Gray)
                    Text("Payment: ${order.paymentMethod} (TrxID: ${order.transactionId})", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("Total Taka: ${order.totalPrice.toInt()} Tk", fontSize = 12.sp, color = BkashPink, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                store.updateOrderStatus(order.id, "Packing Items", 2)
                                onStatusChanged()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NagadOrange),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Pack", color = Color.White, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                store.updateOrderStatus(order.id, "Out for Delivery", 3)
                                onStatusChanged()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftGreen),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Ship", color = Color.White, fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                store.updateOrderStatus(order.id, "Delivered", 4)
                                onStatusChanged()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Deliver", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Tip Box styled matching emerald aesthetics of the Sleek Interface
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightGreenBg.copy(alpha = 0.6f)
            ),
            border = BorderStroke(1.dp, FreshGreen.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBn) "সহজ পরামর্শ" else "Quick Tip Support",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = DarkGreen
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isBn) {
                        "বিকাশ বা নগদ ট্রানজেকশন আইডি যাচাই করার পর FreshHat অর্ডার অটোমেটিকভাবে 'প্যাক্ড' বা 'ডেলিভারি' হিসেবে চিহ্নিত করা হয়। অনুমোদনের আগে মার্চেন্ট প্যানেল যাচাই করুন।"
                    } else {
                        "FreshHat orders are automatically updated once the transaction ID is verified. Ensure you check the bKash/Nagad Merchant Panel (01867164726) before approval."
                    },
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = DarkGreen.copy(alpha = 0.85f)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

// ================= DETAILS OVERLAY =================
@Composable
fun DetailsOverlay(
    product: Product,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(LightGreenBg.copy(0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 64.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) product.nameBn else product.nameEn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (product.stock > 0) Trans.t("in_stock", isBn) else Trans.t("out_of_stock", isBn),
                        fontSize = 11.sp,
                        color = if (product.stock > 0) FreshGreen else BkashPink,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${Trans.t("price_symbol", isBn)} ${product.currentPrice.toInt()}",
                        fontSize = 20.sp,
                        color = FreshGreen,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (product.oldPrice > product.currentPrice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${product.oldPrice.toInt()}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isBn) product.descriptionBn else product.descriptionEn,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.7f),
                    lineHeight = 16.sp,
                    modifier = Modifier.heightIn(max = 100.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, FreshGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(Trans.t("add_to_cart", isBn), color = FreshGreen)
                    }

                    Button(
                        onClick = onBuyNow,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text(Trans.t("buy_now", isBn), color = Color.White)
                    }
                }
            }
        }
    )
}
