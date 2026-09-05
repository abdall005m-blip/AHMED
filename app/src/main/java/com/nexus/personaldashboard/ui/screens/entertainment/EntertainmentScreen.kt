package com.nexus.personaldashboard.ui.screens.entertainment

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexus.personaldashboard.domain.model.PrivateMessage
import com.nexus.personaldashboard.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntertainmentScreen(
    viewModel: EntertainmentViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "ترفيه 🎮",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.currentUserId != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🪙 ", fontSize = 12.sp)
                                val coins = if (state.currentUserId == "ahmed") state.ahmedUser?.coins ?: 0 else state.rodyUser?.coins ?: 0
                                Text("$coins", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs Row
            TabRow(
                selectedTabIndex = state.currentTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = state.currentTab == EntertainmentTab.CHAT,
                    onClick = { viewModel.selectTab(EntertainmentTab.CHAT) },
                    text = { Text("الدردشة 💬", fontSize = 13.sp) }
                )
                Tab(
                    selected = state.currentTab == EntertainmentTab.GAMES,
                    onClick = { viewModel.selectTab(EntertainmentTab.GAMES) },
                    text = { Text("الألعاب 🎮", fontSize = 13.sp) }
                )
                Tab(
                    selected = state.currentTab == EntertainmentTab.ISLAMIC,
                    onClick = { viewModel.selectTab(EntertainmentTab.ISLAMIC) },
                    text = { Text("إسلاميات 🕌", fontSize = 13.sp) }
                )
                Tab(
                    selected = state.currentTab == EntertainmentTab.MOOD,
                    onClick = { viewModel.selectTab(EntertainmentTab.MOOD) },
                    text = { Text("نفسيتي 😊", fontSize = 13.sp) }
                )
            }

            // Tab Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (state.currentTab) {
                    EntertainmentTab.CHAT -> ChatTabContent(state = state, viewModel = viewModel)
                    EntertainmentTab.GAMES -> GamesTabContent(state = state, viewModel = viewModel)
                    EntertainmentTab.ISLAMIC -> IslamicTabContent(state = state, viewModel = viewModel)
                    EntertainmentTab.MOOD -> MoodTabContent(state = state, viewModel = viewModel)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. CHAT TAB
// -------------------------------------------------------------
@Composable
fun ChatTabContent(
    state: EntertainmentUiState,
    viewModel: EntertainmentViewModel
) {
    if (state.currentUserId == null) {
        LoginBox(
            error = state.loginError,
            onLogin = { user, code -> viewModel.login(user, code) }
        )
    } else {
        var messageText by remember { mutableStateOf("") }
        val otherUser = if (state.currentUserId == "ahmed") state.rodyUser else state.ahmedUser

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Heart Counter Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("❤️", fontSize = 28.sp, modifier = Modifier.padding(end = 8.dp))
                        Column {
                            Text(
                                "مدة تواصلنا بالحب والأيام",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "${state.chatDurationDays} يوم مكتمل",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        "Ahmed & Rody ✨",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Other User Note if available
            otherUser?.note?.let { note ->
                if (note.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📝", modifier = Modifier.padding(end = 6.dp))
                            Text(
                                "${otherUser.name}: $note",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }

            // Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                items(state.messages) { msg ->
                    val isMe = msg.senderId == state.currentUserId
                    MessageBubble(message = msg, isMe = isMe)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Input Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("اكتب رسالة...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText)
                            messageText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: PrivateMessage, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text(
                    text = message.content,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. GAMES TAB
// -------------------------------------------------------------
@Composable
fun GamesTabContent(
    state: EntertainmentUiState,
    viewModel: EntertainmentViewModel
) {
    if (state.currentUserId == null) {
        LoginBox(
            error = state.loginError,
            onLogin = { user, code -> viewModel.login(user, code) }
        )
        return
    }

    var selectedGame by remember { mutableStateOf("xo") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Game Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedGame == "xo",
                onClick = { selectedGame = "xo" },
                label = { Text("⭕ XO") }
            )
            FilterChip(
                selected = selectedGame == "racing",
                onClick = { selectedGame = "racing" },
                label = { Text("🏎️ سباق السيارات") }
            )
        }

        Spacer(Modifier.height(16.dp))

        if (selectedGame == "xo") {
            // XO Game
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "لعبة إكس أو (XO) ⚔️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    val status = when (state.xoWinner) {
                        "draw" -> "تعادل رائع بينكما! 🤝"
                        "ahmed" -> "الفائز: Ahmed 🏆 (+20 كوينز)"
                        "rody" -> "الفائز: Rody 🏆 (+20 كوينز)"
                        else -> "الدور الحالي: ${if (state.xoTurn == "ahmed") "Ahmed (X)" else "Rody (O)"}"
                    }
                    Text(status, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(Modifier.height(16.dp))

                    // 3x3 Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (row in 0..2) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (col in 0..2) {
                                    val idx = row * 3 + col
                                    val cell = state.xoBoard[idx]
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                            .clickable { viewModel.makeXOMove(idx) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cell ?: "",
                                            fontSize = 28.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (cell == "X") MaterialTheme.colorScheme.primary else Color(0xFFF43F5E)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.resetXO() }) {
                        Text("إعادة اللعبة 🔄")
                    }
                }
            }
        } else {
            // Racing Game
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "سباق السيارات السريع 🏎️💨",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    val rStatus = state.racingWinner?.let {
                        "الفائز بالسباق: ${if (it == "ahmed") "Ahmed 🏆" else "Rody 🏆"} (+30 كوينز)"
                    } ?: "دوس بنزين بأسرع ما يمكنك! 🏁"
                    Text(rStatus, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(Modifier.height(16.dp))

                    // Tracks
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Ahmed (أزرق) 🏎️", style = MaterialTheme.typography.labelSmall)
                        LinearProgressIndicator(
                            progress = { (state.ahmedCarPos / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            color = Color(0xFF3B82F6)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Rody (وردي) 🏎️", style = MaterialTheme.typography.labelSmall)
                        LinearProgressIndicator(
                            progress = { (state.rodyCarPos / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            color = Color(0xFFEC4899)
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.tapCar() },
                            enabled = state.racingWinner == null
                        ) {
                            Text("دوس بنزين! 🚀")
                        }
                        OutlinedButton(onClick = { viewModel.resetRacing() }) {
                            Text("إعادة 🔄")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. ISLAMIC TAB
// -------------------------------------------------------------
@Composable
fun IslamicTabContent(
    state: EntertainmentUiState,
    viewModel: EntertainmentViewModel
) {
    val azkarList = remember {
        listOf(
            "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لاَ إِلَـهَ إِلاَّ اللهُ وَحْدَهُ لاَ شَرِيكَ لَهُ." to 1,
            "اللَّهُمَّ أَنْتَ رَبِّي لاَ إِلَهَ إِلاَّ أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ." to 1,
            "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ." to 3,
            "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالإِسْلاَمِ دِينًا، وَبِمُحَمَّدٍ صلى الله عليه وسلم نَبِيًّا." to 3,
            "يَا حَيُّ يَا قَيُّومُ بِرَحْمَتِكَ أَسْتَغِيثُ، أَصْلِحْ لِي شَأْنِي كُلَّهُ." to 1
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        "🕌 مواقيت الصلاة اليوم (القاهرة)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الفجر: 04:55 ص", fontSize = 12.sp)
                        Text("الظهر: 12:54 م", fontSize = 12.sp)
                        Text("العصر: 04:28 م", fontSize = 12.sp)
                        Text("المغرب: 07:05 م", fontSize = 12.sp)
                        Text("العشاء: 08:24 م", fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(
                "أذكار مختارة وموثوقة ✨",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(azkarList) { (zekr, count) ->
            var currentCount by remember { mutableIntStateOf(count) }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        zekr,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (currentCount > 0) currentCount--
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (currentCount == 0) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (currentCount == 0) "تم ✓" else "التكرار: $currentCount")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. MOOD TAB
// -------------------------------------------------------------
@Composable
fun MoodTabContent(
    state: EntertainmentUiState,
    viewModel: EntertainmentViewModel
) {
    if (state.currentUserId == null) {
        LoginBox(
            error = state.loginError,
            onLogin = { user, code -> viewModel.login(user, code) }
        )
        return
    }

    val otherUser = if (state.currentUserId == "ahmed") state.rodyUser else state.ahmedUser
    val currentUser = if (state.currentUserId == "ahmed") state.ahmedUser else state.rodyUser

    var customNote by remember { mutableStateOf(currentUser?.note ?: "") }
    val moods = listOf("😊 مبسوط", "😢 تعبان", "😐 زهقان", "😔 مضايق", "😌 كويس", "🥳 فرحان")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Other Person Mood
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    "نفسية ${otherUser?.name ?: "الطرف الثاني"} الآن 💕",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    otherUser?.moodEmoji ?: "😊 كويس",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!otherUser?.moodText.isNullOrBlank()) {
                    Text(
                        otherUser!!.moodText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // My Mood Picker
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    "حدد نفسيتك الحالية 🌸",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.take(3).forEach { m ->
                        FilterChip(
                            selected = currentUser?.moodEmoji?.contains(m.take(2)) == true,
                            onClick = { viewModel.updateMood(m, "") },
                            label = { Text(m, fontSize = 12.sp) }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.drop(3).forEach { m ->
                        FilterChip(
                            selected = currentUser?.moodEmoji?.contains(m.take(2)) == true,
                            onClick = { viewModel.updateMood(m, "") },
                            label = { Text(m, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // Note Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    "اكتب نوت تظهر للطرف الآخر 📝",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = customNote,
                    onValueChange = { customNote = it },
                    placeholder = { Text("مثال: محتاج أروق شوية ☕") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.updateNote(customNote) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("تحديث النوت ✨")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// LOGIN COMPONENT
// -------------------------------------------------------------
@Composable
fun LoginBox(
    error: String?,
    onLogin: (String, String) -> Unit
) {
    var selectedUser by remember { mutableStateOf("ahmed") }
    var code by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("💬", fontSize = 42.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "بوابة الدخول الخاصة",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "اختر اسمك وأدخل رمز الدخول السري",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = selectedUser == "ahmed",
                        onClick = { selectedUser = "ahmed" },
                        label = { Text("Ahmed 👨", fontWeight = FontWeight.Bold) }
                    )
                    FilterChip(
                        selected = selectedUser == "rody",
                        onClick = { selectedUser = "rody" },
                        label = { Text("Rody 👩", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    placeholder = { Text("رمز الدخول (مثال: AHM4821)") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (error != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { onLogin(selectedUser, code) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("دخول الشات والترفيه 🚀")
                }
            }
        }
    }
}
