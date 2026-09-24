package com.nexus.personaldashboard.ui.screens.coins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexus.personaldashboard.ui.components.SectionBackground
import kotlinx.coroutines.launch

data class StoreItem(val emoji: String, val name: String, val nameAr: String, val price: Int, val category: String)

val storeItems = listOf(
    StoreItem("💬", "Heart Bubbles", "فقاعات قلوب", 50, "bubbles"),
    StoreItem("⭐", "Star Bubbles", "فقاعات نجوم", 50, "bubbles"),
    StoreItem("☁️", "Cloud Bubbles", "فقاعات سحاب", 75, "bubbles"),
    StoreItem("🌸", "Flower Bubbles", "فقاعات زهور", 75, "bubbles"),
    StoreItem("🌌", "Night Sky", "سماء الليل", 100, "backgrounds"),
    StoreItem("🌸", "Cherry Blossom", "زهر الكرز", 100, "backgrounds"),
    StoreItem("🌅", "Ocean Sunset", "غروب البحر", 150, "backgrounds"),
    StoreItem("👑", "Golden Frame", "إطار ذهبي", 200, "decorations"),
    StoreItem("💎", "Diamond Ring", "خاتم الماس", 300, "decorations"),
    StoreItem("🎭", "VIP Badge", "شارة VIP", 400, "decorations"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinStoreScreen(onBack: () -> Unit) {
    var coins by remember { mutableStateOf(500) }
    val owned = remember { mutableStateListOf<String>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            SectionBackground(section = "store")
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                TopAppBar(
                    title = { Text("متجر الكوينز 🛒", fontWeight = FontWeight.Bold) },
                    navigationIcon = { IconButton(onBack) { Icon(Icons.Rounded.ArrowBack, null) } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
                // Coin balance
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
                        .padding(16.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("رصيدك الحالي", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        Text("🪙 $coins", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = listOf(
                        "bubbles" to "💬 أشكال الفقاعات",
                        "backgrounds" to "🖼️ خلفيات الشات",
                        "decorations" to "✨ ديكورات البروفايل"
                    )
                    categories.forEach { (cat, title) ->
                        item { Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
                        val catItems = storeItems.filter { it.category == cat }
                        catItems.forEach { item ->
                            item {
                                val isOwned = item.name in owned
                                Card(
                                    Modifier.fillMaxWidth(),
                                    RoundedCornerShape(16.dp),
                                    CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                                    CardDefaults.cardElevation(2.dp)
                                ) {
                                    Row(
                                        Modifier.fillMaxWidth().padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(item.emoji, fontSize = 36.sp)
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(item.nameAr, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text(item.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text("🪙 ${item.price}", style = MaterialTheme.typography.labelMedium, color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
                                        }
                                        if (isOwned) {
                                            Box(
                                                Modifier.clip(RoundedCornerShape(12.dp)).background(Color(0xFF22C55E).copy(.15f)).padding(horizontal = 12.dp, vertical = 6.dp)
                                            ) { Text("✅ مشتري", style = MaterialTheme.typography.labelMedium, color = Color(0xFF22C55E)) }
                                        } else {
                                            Button(
                                                onClick = {
                                                    if (coins >= item.price) {
                                                        coins -= item.price
                                                        owned.add(item.name)
                                                        scope.launch { snackbarHostState.showSnackbar("✅ اشتريت ${item.nameAr}!") }
                                                    } else {
                                                        scope.launch { snackbarHostState.showSnackbar("❌ مش معاك كوينز كافية") }
                                                    }
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                                            ) { Text("اشتري") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}
