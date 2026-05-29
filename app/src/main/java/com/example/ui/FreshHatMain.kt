package com.example.ui

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.*

// ================= GAME CONTEXT & MODELS =================

enum class GameScreen {
    Intro,
    MainMenu,
    Selection,
    Battle,
    Result
}

enum class FighterState {
    Idle,
    RunForward,
    RunBackward,
    Jump,
    Duck,
    Punch,
    Kick,
    Special,
    Block,
    Hit,
    KO,
    Victory
}

enum class BattleArena(val displayName: String, val bgStartColor: Color, val bgEndColor: Color) {
    Rooftop("City Rooftop Night", Color(0xFF0F172A), Color(0xFF1E1E38)),
    FightClub("Underground Fight Club", Color(0xFF1A120B), Color(0xFF3C2A21)),
    StreetArena("Street Fighting Arena", Color(0xFF0C131F), Color(0xFF1F2937))
}

data class FigSound(val type: String)

object SoundSynthesizer {
    private var sfxPrefVolume: Float = 0.8f
    private var bgmPrefVolume: Float = 0.5f
    private var isBgmPlaying = false
    private var bgmThread: Thread? = null

    fun setSfxVolume(vol: Float) { sfxPrefVolume = vol }
    fun setBgmVolume(vol: Float) { bgmPrefVolume = vol }

    fun playSfx(type: String) {
        val vol = sfxPrefVolume
        if (vol <= 0.05f) return
        Thread {
            try {
                val sampleRate = 22050
                val duration = when (type) {
                    "punch" -> 0.12
                    "kick" -> 0.16
                    "special" -> 0.40
                    "block" -> 0.08
                    "hit" -> 0.14
                    "ko" -> 1.20
                    else -> 0.10
                }
                val numSamples = (duration * sampleRate).toInt()
                val sample = FloatArray(numSamples)
                val generatedSnd = ByteArray(2 * numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val gain = max(0.0, 1.0 - t / duration)
                    when (type) {
                        "punch" -> {
                            val freq = 420.0 - (t * 2200.0)
                            sample[i] = (sin(2 * PI * freq * t) * gain).toFloat()
                        }
                        "kick" -> {
                            val freq = 320.0 - (t * 1800.0)
                            sample[i] = (sin(2 * PI * freq * t) * gain).toFloat()
                        }
                        "special" -> {
                            val freq = 120.0 + (t * 1500.0)
                            val noise = (Math.random() * 2.0 - 1.0) * 0.35
                            sample[i] = ((sin(2 * PI * freq * t) * 0.65 + noise) * gain).toFloat()
                        }
                        "block" -> {
                            val freq = 880.0
                            sample[i] = (sin(2 * PI * freq * t) * gain).toFloat()
                        }
                        "hit" -> {
                            val freq = 160.0
                            val noise = (Math.random() * 2.0 - 1.0) * 0.7
                            sample[i] = ((sin(2 * PI * freq * t) * 0.3 + noise) * gain).toFloat()
                        }
                        "ko" -> {
                            val freq = 80.0 - (t * 40.0)
                            val noise = (Math.random() * 2.0 - 1.0) * 0.2
                            sample[i] = ((sin(2 * PI * freq * t) * 0.8 + noise) * gain).toFloat()
                        }
                    }
                }

                var idx = 0
                for (dVal in sample) {
                    val valInt = (dVal * 32767 * vol).toInt().coerceIn(-32768, 32767)
                    generatedSnd[idx++] = (valInt and 0x00ff).toByte()
                    generatedSnd[idx++] = ((valInt and 0xff00) ushr 8).toByte()
                }

                val audioTrack = AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    generatedSnd.size,
                    AudioTrack.MODE_STATIC
                )
                audioTrack.write(generatedSnd, 0, generatedSnd.size)
                audioTrack.play()
                Thread.sleep((duration * 1000 + 40).toLong())
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun startBgm() {
        if (isBgmPlaying) return
        isBgmPlaying = true
        bgmThread = Thread {
            val sampleRate = 22050
            val noteFreqs = doubleArrayOf(
                110.0, 110.0, 130.8, 110.0,
                146.8, 146.8, 164.8, 130.8,
                98.0, 98.0, 116.5, 98.0,
                110.0, 130.8, 146.8, 164.8
            )
            var currentNote = 0

            while (isBgmPlaying) {
                try {
                    val vol = bgmPrefVolume
                    if (vol <= 0.05f) {
                        Thread.sleep(300)
                        continue
                    }
                    val freq = noteFreqs[currentNote % noteFreqs.size]
                    currentNote++

                    val duration = 0.32
                    val numSamples = (duration * sampleRate).toInt()
                    val sample = FloatArray(numSamples)
                    val generatedSnd = ByteArray(2 * numSamples)

                    for (i in 0 until numSamples) {
                        val t = i.toDouble() / sampleRate
                        val envelope = max(0.0, 1.0 - t / duration)
                        val wave = if (sin(2 * PI * freq * t) > 0) 1.0 else -1.0
                        sample[i] = (wave * 0.12 * envelope).toFloat()
                    }

                    var idx = 0
                    for (dVal in sample) {
                        val valInt = (dVal * 32767 * vol).toInt().coerceIn(-32768, 32767)
                        generatedSnd[idx++] = (valInt and 0x00ff).toByte()
                        generatedSnd[idx++] = ((valInt and 0xff00) ushr 8).toByte()
                    }

                    val track = AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRate,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        generatedSnd.size,
                        AudioTrack.MODE_STATIC
                    )
                    track.write(generatedSnd, 0, generatedSnd.size)
                    if (isBgmPlaying) {
                        track.play()
                        Thread.sleep((duration * 1000).toLong())
                    }
                    track.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        bgmThread?.start()
    }

    fun stopBgm() {
        isBgmPlaying = false
        bgmThread = null
    }
}

// Particle system data model
data class FighterParticle(
    val id: String = UUID.randomUUID().toString(),
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val color: Color,
    val size: Float,
    var maxLife: Int,
    var life: Int,
    val isElectric: Boolean = false,
    val isFire: Boolean = false
)

data class PowerProjectile(
    var x: Float,
    var y: Float,
    val vx: Float,
    val ownerId: Int, // 1 or 2
    var isActive: Boolean = true,
    val isElectric: Boolean
)

// Main Fighter game loop state representation
class FighterCharacter(
    val id: Int, // 1 for Player 1, 2 for Player 2/AI
    val name: String,
    val isAlamgir: Boolean, // Alamgir = true, Khorshed = false
    val colorAccent: Color,
    var isAIControlled: Boolean = false
) {
    var x by mutableStateOf(0f)
    var y by mutableStateOf(400f)
    var vx by mutableStateOf(0f)
    var vy by mutableStateOf(0f)
    var health by mutableStateOf(100f)
    var energy by mutableStateOf(30f)
    var state by mutableStateOf(FighterState.Idle)
    var facingRight by mutableStateOf(true)
    var isBlocking by mutableStateOf(false)
    var stateTimeLeft by mutableStateOf(0) // Tick constraints for lock states
    var comboCount by mutableStateOf(0)
    var lastHitRegistered by mutableStateOf(0L)

    fun reset(startingX: Float) {
        x = startingX
        y = 400f
        vx = 0f
        vy = 0f
        health = 100f
        energy = 30f
        state = FighterState.Idle
        facingRight = id == 1
        isBlocking = false
        stateTimeLeft = 0
        comboCount = 0
        lastHitRegistered = 0L
    }

    fun updatePhysics(logicalWidth: Float) {
        // Apply velocity physics
        x = (x + vx).coerceIn(40f, logicalWidth - 40f)
        y += vy

        // Gravity check
        if (y < 400f) {
            vy += 1.8f // Gravity acceleration
        } else {
            y = 400f
            vy = 0f
            if (state == FighterState.Jump) {
                state = FighterState.Idle
            }
        }

        // Apply friction when on ground and idle/running states
        if (y >= 400f) {
            vx *= 0.70f
        }

        // Cooldown timer
        if (stateTimeLeft > 0) {
            stateTimeLeft--
            if (stateTimeLeft == 0) {
                if (state != FighterState.KO && state != FighterState.Victory) {
                    state = if (y < 400f) FighterState.Jump else FighterState.Idle
                    isBlocking = false
                }
            }
        }
    }
}

@Composable
fun FreshHatMainApp() {
    val appCtx = LocalContext.current
    val store = remember { LocalGameStore(appCtx) }
    var currentScreen by remember { mutableStateOf(GameScreen.Intro) }

    // Sound volumes sync
    LaunchedEffect(Unit) {
        SoundSynthesizer.setSfxVolume(store.getSfxVolume())
        SoundSynthesizer.setBgmVolume(store.getBgmVolume())
        SoundSynthesizer.startBgm()
    }

    // Match variables
    var playerIsAlamgir by remember { mutableStateOf(true) }
    var selectedArena by remember { mutableStateOf(BattleArena.Rooftop) }
    var singlePlayerMode by remember { mutableStateOf(true) }

    var matchWinnerName by remember { mutableStateOf("") }
    var matchLogs by remember { mutableStateOf("") }

    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
        when (screen) {
            GameScreen.Intro -> GameIntroScreen(onFinished = { currentScreen = GameScreen.MainMenu })
            GameScreen.MainMenu -> GameMainMenuScreen(
                store = store,
                onStartSolo = {
                    singlePlayerMode = true
                    currentScreen = GameScreen.Selection
                },
                onStartVersus = {
                    singlePlayerMode = false
                    currentScreen = GameScreen.Selection
                },
                onExit = {
                    Toast.makeText(appCtx, "Thanks for playing Alamgir Vs Khorshed!", Toast.LENGTH_SHORT).show()
                }
            )
            GameScreen.Selection -> CharacterSelectionScreen(
                userSelectedAlamgir = playerIsAlamgir,
                onSelectChar = { playerIsAlamgir = it },
                arena = selectedArena,
                onSelectArena = { selectedArena = it },
                singlePlayer = singlePlayerMode,
                onBack = { currentScreen = GameScreen.MainMenu },
                onLaunchMatch = { currentScreen = GameScreen.Battle }
            )
            GameScreen.Battle -> FightingBattleArenaScreen(
                player1IsAlamgir = playerIsAlamgir,
                arena = selectedArena,
                isSolo = singlePlayerMode,
                store = store,
                onMatchOver = { winner, logSummary ->
                    matchWinnerName = winner
                    matchLogs = logSummary
                    currentScreen = GameScreen.Result
                },
                onQuit = { currentScreen = GameScreen.MainMenu }
            )
            GameScreen.Result -> MatchResultScreen(
                winnerName = matchWinnerName,
                summaryLogs = matchLogs,
                onRestart = { currentScreen = GameScreen.Battle },
                onMainMenu = { currentScreen = GameScreen.MainMenu }
            )
        }
    }
}

// ================= 1. GAME INTRO ANIMATION SCREEN =================

@Composable
fun GameIntroScreen(onFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "IntroPulse")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TitlePulse"
    )

    var triggerLogoAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        triggerLogoAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617)) // Pitch black sci-fi background
            .pointerInput(Unit) {
                detectTapGestures { onFinished() }
            },
        contentAlignment = Alignment.Center
    ) {
        // Retro Grid background effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val columns = 20
                    val rows = 15
                    val gridColor = Color(0xFF1E293B).copy(alpha = 0.4f)
                    for (i in 0..columns) {
                        val x = size.width / columns * i
                        drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                    }
                    for (j in 0..rows) {
                        val y = size.height / rows * j
                        drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                    }
                    // Cinematic bottom horizon line
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFFE2125B).copy(alpha = 0.08f))
                        )
                    )
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            AnimatedVisibility(
                visible = triggerLogoAnimation,
                enter = fadeIn(tween(1000)) + expandVertically(tween(1000)),
                label = "LogoAnimation"
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CHAMPIONSHIP 2D ARENA",
                        color = Color(0xFFFFCC00),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(letterSpacing = 4.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.scale(titleScale)
                    ) {
                        Text(
                            text = "ALAMGIR",
                            color = Color(0xFFEF4444), // Vibrant Red
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(letterSpacing = 2.sp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "VS",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            style = TextStyle(fontStyle = FontStyle.Italic)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "KHORSHED",
                            color = Color(0xFF3B82F6), // Cool Blue
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold,
                            style = TextStyle(letterSpacing = 2.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Pulse glowing tap-to-start
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "PulseText"
            )

            Text(
                text = "-- TAP TO FIGHT or PRESS SPACE --",
                color = Color.White.copy(alpha = pulseAlpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                style = TextStyle(letterSpacing = 2.sp)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "Offine Retro Fighting Experience",
                color = Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ================= 2. MAIN MENU SCREEN =================

@Composable
fun GameMainMenuScreen(
    store: LocalGameStore,
    onStartSolo: () -> Unit,
    onStartVersus: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current
    var totalWins by remember { mutableStateOf(store.getWins()) }
    var totalMatches by remember { mutableStateOf(store.getMatchesPlayed()) }
    var bgmVol by remember { mutableStateOf(store.getBgmVolume()) }
    var sfxVol by remember { mutableStateOf(store.getSfxVolume()) }
    var hardcoreAi by remember { mutableStateOf(store.isAiDifficult()) }

    var showStatsDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D1A)) // Dark space theme
    ) {
        // Glowing abstract background circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEF4444).copy(0.12f), Color.Transparent),
                    radius = 350f
                ),
                center = Offset(200f, 200f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF3B82F6).copy(0.12f), Color.Transparent),
                    radius = 400f
                ),
                center = Offset(size.width - 200f, size.height - 200f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game high score / statistics bar
                Text(
                    text = "WIN RATE: ${if (totalMatches > 0) ((totalWins.toFloat() / totalMatches) * 100).toInt() else 0}%",
                    color = Color(0xFFFFCC00),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .border(1.dp, Color(0xFFFFCC00).copy(0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )

                Text(
                    text = "BATTLE RECORD: ${totalWins}W - ${max(0, totalMatches - totalWins)}L",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Game Logo / Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ALAMGIR VS KHORSHED",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    style = TextStyle(letterSpacing = 1.sp)
                )
                Text(
                    text = "THE MINI FIGHTING CHAMPIONSHIP",
                    color = Color(0xFFFF6600),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 3.sp),
                    textAlign = TextAlign.Center
                )
            }

            // Interactive game choices
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onStartSolo,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Solo Play", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SOLO FIGHT (VS AI)", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onStartVersus,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Versus mode", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOCAL VS (2 PLAYERS)", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showSettingsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("OPTION", fontSize = 13.sp)
                    }

                    Button(
                        onClick = { showStatsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Stats", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("STATS", fontSize = 13.sp)
                    }
                }

                OutlinedButton(
                    onClick = onExit,
                    border = BorderStroke(1.dp, Color.Gray.copy(0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("EXIT GAME", color = Color.LightGray, fontSize = 12.sp)
                }
            }

            // Developer Credit
            Text(
                text = "Powered by Jetpack Compose Engine v1.1.0",
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // --- Option Settings Dialog ---
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text("GAME OPTIONS", fontWeight = FontWeight.Bold, color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("BGM MUSIC VOLUME", fontSize = 12.sp, color = Color.White)
                                Text("${(bgmVol * 100).toInt()}%", fontSize = 12.sp, color = Color.White)
                            }
                            Slider(
                                value = bgmVol,
                                onValueChange = {
                                    bgmVol = it
                                    store.setBgmVolume(it)
                                    SoundSynthesizer.setBgmVolume(it)
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF3B82F6),
                                    activeTrackColor = Color(0xFF3B82F6)
                                )
                            )
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("SOUND SFX VOLUME", fontSize = 12.sp, color = Color.White)
                                Text("${(sfxVol * 100).toInt()}%", fontSize = 12.sp, color = Color.White)
                            }
                            Slider(
                                value = sfxVol,
                                onValueChange = {
                                    sfxVol = it
                                    store.setSfxVolume(it)
                                    SoundSynthesizer.setSfxVolume(it)
                                },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFEF4444),
                                    activeTrackColor = Color(0xFFEF4444)
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("HARDCORE ENEMY AI", fontSize = 12.sp, color = Color.White)
                                Text(
                                    if (hardcoreAi) "Aggressive Opponent Combatant" else "Easy Combat Dummy Mode",
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                            Switch(
                                checked = hardcoreAi,
                                onCheckedChange = {
                                    hardcoreAi = it
                                    store.setAiDifficult(it)
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text("SAVE CHANGES", color = Color(0xFFFFCC00))
                    }
                },
                containerColor = Color(0xFF1E293B),
                textContentColor = Color.White
            )
        }

        // --- Statistics Dialog ---
        if (showStatsDialog) {
            AlertDialog(
                onDismissRequest = { showStatsDialog = false },
                title = { Text("FIGHT CHAMP RECORD", fontWeight = FontWeight.Bold, color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Matches:", color = Color.LightGray, fontSize = 13.sp)
                            Text("$totalMatches", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Wins:", color = Color.LightGray, fontSize = 13.sp)
                            Text("$totalWins", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Defeats:", color = Color.LightGray, fontSize = 13.sp)
                            Text("${max(0, totalMatches - totalWins)}", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Unlocked Arenas:", color = Color.LightGray, fontSize = 13.sp)
                            Text("3/3 Open", color = Color(0xFFFFCC00), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "To achieve better rank score, increase AI difficulties and train hard sweeps & blocks combos!",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            // Reset statistics safely
                            store.setHighScore(0)
                            totalWins = 0
                            totalMatches = 0
                            // Edit high scores directly
                            val rawPrefs = context.getSharedPreferences("AlamgirVsKhorshedPrefs", Context.MODE_PRIVATE)
                            rawPrefs.edit().putInt("total_wins", 0).putInt("matches_played", 0).apply()
                            Toast.makeText(context, "Battle logs cleared!", Toast.LENGTH_SHORT).show()
                            showStatsDialog = false
                        }) {
                            Text("RESET STATS", color = Color.Gray)
                        }

                        TextButton(onClick = { showStatsDialog = false }) {
                            Text("CLOSE", color = Color(0xFFEF4444))
                        }
                    }
                },
                containerColor = Color(0xFF1E293B),
                textContentColor = Color.White
            )
        }
    }
}

// ================= 3. CHARACTER SELECTION SCREEN =================

@Composable
fun CharacterSelectionScreen(
    userSelectedAlamgir: Boolean,
    onSelectChar: (Boolean) -> Unit,
    arena: BattleArena,
    onSelectArena: (BattleArena) -> Unit,
    singlePlayer: Boolean,
    onBack: () -> Unit,
    onLaunchMatch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SELECT FIGHTER & ARENA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 2.sp)
                )
                Text(
                    text = if (singlePlayer) "Solo mode: Vs Computer AI" else "Local mode: 2 Player split controls",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            // Main layout: Character Select Cards Side by Side
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Character 1: Alamgir
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectChar(true) }
                        .border(
                            2.dp,
                            if (userSelectedAlamgir) Color(0xFFEF4444) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (userSelectedAlamgir) Color(0xFF1E293B) else Color(0xFF0F172A).copy(0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Fighter Face Avatar Placeholder drawn neatly
                        Canvas(modifier = Modifier.size(100.dp)) {
                            // Head & Face
                            drawCircle(Color(0xFFFBC02D), radius = 35f)
                            // Hair style: Slick short black hair
                            val hairPath = Path().apply {
                                moveTo(size.width / 2 - 40f, size.height / 2 - 25f)
                                lineTo(size.width / 2 + 40f, size.height / 2 - 25f)
                                lineTo(size.width / 2 + 25f, size.height / 2 - 45f)
                                lineTo(size.width / 2 - 25f, size.height / 2 - 45f)
                                close()
                            }
                            drawPath(hairPath, Color(0xFF1E293B))

                            // Eyes (aggressive red shade/sunglasses)
                            drawCircle(Color.Black, radius = 5f, center = Offset(size.width / 2 - 12f, size.height / 2 - 5f))
                            drawCircle(Color.Black, radius = 5f, center = Offset(size.width / 2 + 12f, size.height / 2 - 5f))
                            // Red Fighting Jacket representation
                            drawRect(
                                Color(0xFFEF4444),
                                topLeft = Offset(size.width / 2 - 30f, size.height / 2 + 20f),
                                size = Size(60f, 40f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Alamgir Hossain", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Text("Aggressive Combat Style", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Stats description
                            CharacterStatBar("POWER", 0.9f, Color(0xFFEF4444))
                            CharacterStatBar("SPEED", 0.65f, Color(0xFFFFCC00))
                            CharacterStatBar("DEFENSE", 0.75f, Color(0xFF10B981))
                        }
                    }
                }

                // Character 2: Khorshed
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectChar(false) }
                        .border(
                            2.dp,
                            if (!userSelectedAlamgir) Color(0xFF3B82F6) else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!userSelectedAlamgir) Color(0xFF1E293B) else Color(0xFF0F172A).copy(0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Fighter face Avatar
                        Canvas(modifier = Modifier.size(100.dp)) {
                            drawCircle(Color(0xFFFFD54F), radius = 35f)
                            // Blue combat hoodie drawn neatly
                            drawCircle(Color(0xFF3B82F6), radius = 38f, style = Stroke(width = 8f))
                            drawCircle(Color.White, radius = 4f, center = Offset(size.width / 2 - 12f, size.height / 2 - 5f))
                            drawCircle(Color.White, radius = 4f, center = Offset(size.width / 2 + 12f, size.height / 2 - 5f))
                            // Cute fast look and blue chest base
                            drawRect(Color(0xFF3B82F6), topLeft = Offset(size.width / 2 - 30f, size.height / 2 + 20f), size = Size(60f, 40f))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Khorshed Alam", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                            Text("Fast Combo Fighting Style", color = Color(0xFF3B82F6), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            // Stats description
                            CharacterStatBar("POWER", 0.7f, Color(0xFFEF4444))
                            CharacterStatBar("SPEED", 0.95f, Color(0xFFFFCC00))
                            CharacterStatBar("DEFENSE", 0.6f, Color(0xFF10B981))
                        }
                    }
                }
            }

            // Arena Selection Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("CHOOSE BATTLEFIELD ARENA", color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BattleArena.values().forEach { ar ->
                        val selected = ar == arena
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSelectArena(ar) }
                                .background(
                                    if (selected) Color(0xFFFF6600) else Color(0xFF0F172A),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ar.displayName,
                                color = if (selected) Color.White else Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("BACK TO MENU", color = Color.LightGray)
                }

                Button(
                    onClick = {
                        SoundSynthesizer.playSfx("special")
                        onLaunchMatch()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("ENGAGE MATCH!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun CharacterStatBar(label: String, progress: Float, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 9.sp,
            modifier = Modifier.width(55.dp),
            fontWeight = FontWeight.Bold
        )
        LinearProgressIndicator(
            progress = progress,
            color = color,
            trackColor = Color.Gray.copy(0.2f),
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

// ================= 4. REAL-TIME OFF-LINE FIGHTING BATTLE ARENA =================

@Composable
fun FightingBattleArenaScreen(
    player1IsAlamgir: Boolean,
    arena: BattleArena,
    isSolo: Boolean,
    store: LocalGameStore,
    onMatchOver: (winnerName: String, summary: String) -> Unit,
    onQuit: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. Instantiating our two custom fighter objects using the selection parameters
    val p1 = remember {
        FighterCharacter(
            id = 1,
            name = if (player1IsAlamgir) "Alamgir" else "Khorshed",
            isAlamgir = player1IsAlamgir,
            colorAccent = if (player1IsAlamgir) Color(0xFFEF4444) else Color(0xFF3B82F6)
        )
    }

    val p2 = remember {
        FighterCharacter(
            id = 2,
            name = if (!player1IsAlamgir) "Alamgir" else "Khorshed",
            isAlamgir = !player1IsAlamgir,
            colorAccent = if (!player1IsAlamgir) Color(0xFFEF4444) else Color(0xFF3B82F6)
        ).apply {
            isAIControlled = isSolo
        }
    }

    // Game loop parameters
    var matchTimeLeft by remember { mutableStateOf(90) }
    var matchActive by remember { mutableStateOf(false) }
    var pauseState by remember { mutableStateOf(false) }
    var introCountdown by remember { mutableStateOf(3) }
    var introFinished by remember { mutableStateOf(false) }

    // Screen Shake state triggers
    var screenShakeOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    var shakeDurationTicks by remember { mutableStateOf(0) }
    var isSlowMotionFinisher by remember { mutableStateOf(false) }

    // Projectiles and Particle Lists
    val activeProjectiles = remember { mutableStateListOf<PowerProjectile>() }
    val activeParticles = remember { mutableStateListOf<FighterParticle>() }

    // Custom Focus Request to read PC key press events immediately
    val focusRequester = remember { FocusRequester() }

    // Hit-Combo UI Notices
    var showComboText by remember { mutableStateOf("") }
    var comboNoticeOpacity by remember { mutableStateOf(0f) }

    // Log tracking for final screen
    var player1Hits by remember { mutableIntStateOf(0) }
    var player2Hits by remember { mutableIntStateOf(0) }
    var p1MaxCombo by remember { mutableIntStateOf(0) }

    fun triggerScreenShake(ticks: Int) {
        shakeDurationTicks = ticks
    }

    fun triggerHitEffect(hitX: Float, hitY: Float, isElectric: Boolean) {
        val color = if (isElectric) Color(0xFF00E5FF) else Color(0xFFFF5722)
        // Spawn particles
        for (i in 0..12) {
            val vx = (Math.random() * 16 - 8).toFloat()
            val vy = (Math.random() * 16 - 8).toFloat()
            activeParticles.add(
                FighterParticle(
                    x = hitX,
                    y = hitY,
                    vx = vx,
                    vy = vy,
                    color = color,
                    size = (6..12).random().toFloat(),
                    maxLife = 20,
                    life = 20,
                    isElectric = isElectric,
                    isFire = !isElectric
                )
            )
        }
    }

    fun playCombatSound(sound: String) {
        SoundSynthesizer.playSfx(sound)
    }

    // Function to calculate attacks collisions
    fun checkAttackHit(attacker: FighterCharacter, defender: FighterCharacter, isPunch: Boolean) {
        // Distance constraints
        val deltaX = abs(attacker.x - defender.x)
        val validY = abs(attacker.y - defender.y) < 80f
        val validFacing = if (attacker.facingRight) attacker.x < defender.x else attacker.x > defender.x

        if (deltaX < 110f && validY && validFacing && defender.state != FighterState.KO) {
            // Triggered Hit! Calculate Blocks defense
            if (defender.state == FighterState.Block) {
                // Reduced block damage
                defender.health = max(0f, defender.health - 1.2f)
                defender.energy = min(100f, defender.energy + 8f) // Blocking charges energy
                triggerScreenShake(3)
                triggerHitEffect(defender.x, defender.y - 60f, attacker.isAlamgir)
                playCombatSound("block")
            } else {
                // Direct strike! Pushes back slightly, hurts, inflicts HIT state
                val damage = if (isPunch) 7f else 10f
                defender.health = max(0f, defender.health - damage)
                defender.state = FighterState.Hit
                defender.stateTimeLeft = 10 // Stunned for 10 ticks
                // Pushes opponent in direction of strike
                val recoilSign = if (attacker.facingRight) 1f else -1f
                defender.vx = recoilSign * 18f
                defender.vy = -6f // Tiny pop up feel

                // Score metrics tracking
                attacker.energy = min(100f, attacker.energy + 14f)
                attacker.comboCount++
                if (attacker.id == 1) {
                    player1Hits++
                    if (attacker.comboCount > p1MaxCombo) {
                        p1MaxCombo = attacker.comboCount
                    }
                } else {
                    player2Hits++
                }

                if (attacker.comboCount >= 2) {
                    showComboText = "${attacker.name}: ${attacker.comboCount}-HIT COMBO!"
                    comboNoticeOpacity = 1f
                }

                triggerScreenShake(7)
                triggerHitEffect(defender.x, defender.y - 60f, !attacker.isAlamgir)
                playCombatSound("hit")
            }
        }
    }

    // Projectile action firing
    fun shootSpecialProjectile(attacker: FighterCharacter) {
        if (attacker.energy >= 80f) {
            attacker.energy = 0f
            attacker.state = FighterState.Special
            attacker.stateTimeLeft = 24

            val direction = if (attacker.facingRight) 1f else -1f
            val projectileVx = direction * 24f
            val isElectric = !attacker.isAlamgir

            activeProjectiles.add(
                PowerProjectile(
                    x = attacker.x + (direction * 40f),
                    y = attacker.y - 60f,
                    vx = projectileVx,
                    ownerId = attacker.id,
                    isElectric = isElectric
                )
            )
            playCombatSound("special")
            triggerScreenShake(4)
        }
    }

    // Key event PC mapping
    fun handlePCControls(key: Key, isDown: Boolean) {
        if (pauseState || !matchActive) return

        if (isDown) {
            when (key) {
                // Player 1 controls
                Key.A -> {
                    p1.vx = -12f
                    p1.facingRight = false
                    if (p1.y >= 400f) p1.state = FighterState.RunBackward
                }
                Key.D -> {
                    p1.vx = 12f
                    p1.facingRight = true
                    if (p1.y >= 400f) p1.state = FighterState.RunForward
                }
                Key.W -> {
                    if (p1.y >= 400f) {
                        p1.vy = -24f
                        p1.state = FighterState.Jump
                        playCombatSound("punch")
                    }
                }
                Key.S -> {
                    p1.state = FighterState.Block
                    p1.isBlocking = true
                }
                Key.J -> {
                    if (p1.stateTimeLeft == 0) {
                        p1.state = FighterState.Punch
                        p1.stateTimeLeft = 8
                        playCombatSound("punch")
                        checkAttackHit(p1, p2, isPunch = true)
                    }
                }
                Key.K -> {
                    if (p1.stateTimeLeft == 0) {
                        p1.state = FighterState.Kick
                        p1.stateTimeLeft = 10
                        playCombatSound("kick")
                        checkAttackHit(p1, p2, isPunch = false)
                    }
                }
                Key.L -> {
                    shootSpecialProjectile(p1)
                }

                // Player 2 controls (Alternative PC controls mapping inside versus)
                Key.DirectionLeft -> {
                    if (!isSolo) {
                        p2.vx = -12f
                        p2.facingRight = false
                        if (p2.y >= 400f) p2.state = FighterState.RunForward
                    }
                }
                Key.DirectionRight -> {
                    if (!isSolo) {
                        p2.vx = 12f
                        p2.facingRight = true
                        if (p2.y >= 400f) p2.state = FighterState.RunBackward
                    }
                }
                Key.DirectionUp -> {
                    if (!isSolo && p2.y >= 400f) {
                        p2.vy = -24f
                        p2.state = FighterState.Jump
                        playCombatSound("punch")
                    }
                }
                Key.NumPad4 -> {
                    if (!isSolo && p2.stateTimeLeft == 0) {
                        p2.state = FighterState.Punch
                        p2.stateTimeLeft = 8
                        playCombatSound("punch")
                        checkAttackHit(p2, p1, isPunch = true)
                    }
                }
                Key.NumPad5 -> {
                    if (!isSolo && p2.stateTimeLeft == 0) {
                        p2.state = FighterState.Kick
                        p2.stateTimeLeft = 10
                        playCombatSound("kick")
                        checkAttackHit(p2, p1, isPunch = false)
                    }
                }
                Key.NumPad6 -> {
                    if (!isSolo) shootSpecialProjectile(p2)
                }
            }
        } else {
            // Key released resets
            when (key) {
                Key.A, Key.D -> {
                    if (p1.state == FighterState.RunForward || p1.state == FighterState.RunBackward) {
                        p1.state = FighterState.Idle
                    }
                }
                Key.S -> {
                    if (p1.state == FighterState.Block) {
                        p1.state = FighterState.Idle
                        p1.isBlocking = false
                    }
                }
                Key.DirectionLeft, Key.DirectionRight -> {
                    if (!isSolo && (p2.state == FighterState.RunForward || p2.state == FighterState.RunBackward)) {
                        p2.state = FighterState.Idle
                    }
                }
            }
        }
    }

    // Resetting fighters positioning on load
    LaunchedEffect(Unit) {
        p1.reset(160f)
        p2.reset(840f)
        activeProjectiles.clear()
        activeParticles.clear()
        focusRequester.requestFocus()

        // Match start countdown timer coroutines loop
        while (introCountdown > 0) {
            delay(1000)
            introCountdown--
        }
        introFinished = true
        matchActive = true

        // Timer counter logic
        while (matchTimeLeft > 0 && matchActive) {
            delay(1000)
            if (!pauseState && !isSlowMotionFinisher) {
                matchTimeLeft--
            }
        }
    }

    // Core Game/Physics Loop Execution (Runs periodically at 30fps/33ms ticks)
    LaunchedEffect(matchActive, pauseState, isSlowMotionFinisher) {
        while (true) {
            val frameDelay = if (isSlowMotionFinisher) 120L else 33L
            delay(frameDelay)

            if (pauseState || introCountdown > 0) continue

            // 1. Process physics updates
            p1.updatePhysics(1000f)
            p2.updatePhysics(1000f)

            // Auto-align character facing sides
            if (p1.state != FighterState.KO && p2.state != FighterState.KO) {
                p1.facingRight = p1.x < p2.x
                p2.facingRight = p2.x < p1.x
            }

            // 2. Combo notices fadeout effects
            if (comboNoticeOpacity > 0f) {
                comboNoticeOpacity -= 0.04f
            } else {
                p1.comboCount = 0
                p2.comboCount = 0
            }

            // 3. Screen shake tick updates
            if (shakeDurationTicks > 0) {
                val dx = (Math.random() * 20 - 10).toFloat()
                val dy = (Math.random() * 20 - 10).toFloat()
                screenShakeOffset = Offset(dx, dy)
                shakeDurationTicks--
            } else {
                screenShakeOffset = Offset(0f, 0f)
            }

            // 4. Update Game Particles
            val iterator = activeParticles.iterator()
            while (iterator.hasNext()) {
                val part = iterator.next()
                part.x += part.vx
                part.y += part.vy
                part.life--
                if (part.life <= 0) {
                    activeParticles.remove(part)
                }
            }

            // 5. Update Game Projectiles
            val pProjIterator = activeProjectiles.iterator()
            while (pProjIterator.hasNext()) {
                val proj = pProjIterator.next()
                if (!proj.isActive) {
                    activeProjectiles.remove(proj)
                    continue
                }

                proj.x += proj.vx

                // Out of screen constraints bounds
                if (proj.x < 10f || proj.x > 990f) {
                    proj.isActive = false
                    continue
                }

                // Check collision with opponent
                val target = if (proj.ownerId == 1) p2 else p1
                val distanceToTarget = abs(proj.x - target.x)
                val sameElevation = abs(proj.y - (target.y - 60f)) < 70f

                if (distanceToTarget < 50f && sameElevation && target.state != FighterState.KO) {
                    // Detonated Projectile! Damage logic
                    if (target.state == FighterState.Block) {
                        target.health = max(0f, target.health - 4.5f)
                        playCombatSound("block")
                    } else {
                        target.health = max(0f, target.health - 22f)
                        target.state = FighterState.Hit
                        target.stateTimeLeft = 14
                        target.vx = (if (proj.vx > 0) 1f else -1f) * 26f
                        target.vy = -8f
                        playCombatSound("hit")
                    }
                    proj.isActive = false
                    triggerScreenShake(12)
                    triggerHitEffect(target.x, target.y - 60f, proj.isElectric)
                }
            }

            // 6. ADAPTIVE SOLO COMBAT AI LOGIC
            if (isSolo && p2.state != FighterState.KO && p1.state != FighterState.KO) {
                val aiDist = abs(p2.x - p1.x)
                val aiDifficult = store.isAiDifficult()

                // Decide next AI moves asynchronously inside game ticks
                if (p2.stateTimeLeft == 0) {
                    if (aiDist > 200f) {
                        // Head towards player character
                        val speed = if (aiDifficult) 13f else 9f
                        val runDirection = if (p1.x > p2.x) 1f else -1f
                        p2.vx = runDirection * speed
                        p2.state = if (runDirection > 0) FighterState.RunForward else FighterState.RunBackward
                    } else {
                        // Nearby Close Range: Combat AI decision matrix
                        val strikeRoll = Math.random()
                        if (p2.energy >= 100f && strikeRoll < 0.3) {
                            shootSpecialProjectile(p2)
                        } else if (strikeRoll < (if (aiDifficult) 0.15 else 0.07)) {
                            // Punch trigger attack
                            p2.state = FighterState.Punch
                            p2.stateTimeLeft = 8
                            playCombatSound("punch")
                            checkAttackHit(p2, p1, isPunch = true)
                        } else if (strikeRoll < (if (aiDifficult) 0.28 else 0.13)) {
                            // Kick trigger attack
                            p2.state = FighterState.Kick
                            p2.stateTimeLeft = 10
                            playCombatSound("kick")
                            checkAttackHit(p2, p1, isPunch = false)
                        } else if (p1.state == FighterState.Punch || p1.state == FighterState.Kick) {
                            // Defense block adaptation
                            val blockRoll = Math.random()
                            if (blockRoll < (if (aiDifficult) 0.85 else 0.35)) {
                                p2.state = FighterState.Block
                                p2.isBlocking = true
                                p2.stateTimeLeft = 12
                            }
                        }
                    }
                }
            }

            // 7. WIN/LOSS DECISION TRIGGER KO CHECK
            if ((p1.health <= 0f || p2.health <= 0f) && matchActive) {
                if (isSlowMotionFinisher) {
                    // Match completed
                    matchActive = false
                    val winnerName = if (p1.health > p2.health) p1.name else p2.name
                    if (p1.health > p2.health) {
                        store.incrementWins()
                    }
                    store.incrementMatches()

                    val summaryString = """
                        🏆 MATCH BREAKDOWN REPORT:
                        ------------------------------
                        • Winner Fighter: $winnerName
                        • Hits Landed: P1 (${player1Hits}) | P2 (${player2Hits})
                        • Max Combo Strike: $p1MaxCombo
                        • Rounds Remaining Time: ${matchTimeLeft}s
                        • Arena Location: ${arena.displayName}
                    """.trimIndent()

                    onMatchOver(winnerName, summaryString)
                } else {
                    // Epic Slow Motion Finisher Entry Trigger
                    isSlowMotionFinisher = true
                    playCombatSound("ko")
                    triggerScreenShake(24)
                    if (p1.health <= 0f) p1.state = FighterState.KO
                    if (p2.health <= 0f) p2.state = FighterState.KO
                    delay(1200) // Keep slow motion going
                }
            }

            // Match timer expiration
            if (matchTimeLeft <= 0 && matchActive) {
                matchActive = false
                val winnerName = if (p1.health >= p2.health) p1.name else p2.name
                val summaryString = "Match expired due to time constraint! Winner decided by remaining health percentages."
                onMatchOver(winnerName, summaryString)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent {
                val isDown = it.type == KeyEventType.KeyDown
                handlePCControls(it.key, isDown)
                true
            }
            .offset { IntOffset(screenShakeOffset.x.toInt(), screenShakeOffset.y.toInt()) }
            .background(arena.bgStartColor)
    ) {
        // Core Visual Combat Canvas Drawing
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val scaleX = size.width / 1000f
            val scaleY = size.height / 500f

            // Arena Background Design drawing neatly
            when (arena) {
                BattleArena.Rooftop -> {
                    // Sky celestial objects: Moon & Stars
                    drawCircle(Color(0xFFFFF9C4).copy(0.4f), radius = 60f, center = Offset(150f * scaleX, 80f * scaleY))
                    drawCircle(Color.White, radius = 55f, center = Offset(150f * scaleX, 80f * scaleY))

                    // Draw skyline silhouettes
                    drawRect(Color(0xFF0C1322), topLeft = Offset(400f * scaleX, 220f * scaleY), size = Size(110f * scaleX, 180f * scaleY))
                    drawRect(Color(0xFF0F172A), topLeft = Offset(540f * scaleX, 140f * scaleY), size = Size(140f * scaleX, 260f * scaleY))
                    drawRect(Color(0xFF090D1A), topLeft = Offset(720f * scaleX, 260f * scaleY), size = Size(100f * scaleX, 140f * scaleY))

                    // Draw safety fence bar
                    drawLine(Color(0xFF334155), Offset(0f, 398f * scaleY), Offset(size.width, 398f * scaleY), strokeWidth = 4f)
                }
                BattleArena.FightClub -> {
                    // Gritty metal mesh & spotlight beam shadow projection representation
                    drawPath(
                        Path().apply {
                            moveTo(size.width / 2 - 120f, 0f)
                            lineTo(size.width / 2 + 120f, 0f)
                            lineTo(size.width / 2 + 350f, size.height)
                            lineTo(size.width / 2 - 350f, size.height)
                            close()
                        },
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFEE58).copy(0.18f), Color.Transparent)
                        )
                    )
                }
                BattleArena.StreetArena -> {
                    // Street lights glow circles
                    drawCircle(Color(0xFFFFA726).copy(alpha = 0.25f), radius = 100f, center = Offset(200f * scaleX, 120f * scaleY))
                    drawCircle(Color(0xFFFFA726).copy(alpha = 0.25f), radius = 100f, center = Offset(800f * scaleX, 120f * scaleY))
                }
            }

            // Drawn Platform Level (Ground bounds)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                ),
                topLeft = Offset(0f, 400f * scaleY),
                size = Size(size.width, size.height - 400f * scaleY)
            )
            // Platform grid line accents for spatial reference
            drawLine(Color(0xFFFF6600).copy(0.3f), Offset(0f, 400f * scaleY), Offset(size.width, 400f * scaleY), strokeWidth = 5f)

            // 2. Render Projectiles
            activeProjectiles.forEach { proj ->
                if (proj.isActive) {
                    val brush = if (proj.isElectric) {
                        Brush.radialGradient(colors = listOf(Color.White, Color(0xFF00E5FF), Color.Transparent))
                    } else {
                        Brush.radialGradient(colors = listOf(Color.White, Color(0xFFFF3D00), Color.Transparent))
                    }
                    drawCircle(
                        brush = brush,
                        radius = 28f * scaleX,
                        center = Offset(proj.x * scaleX, proj.y * scaleY)
                    )
                    // Flare lightning vectors representation
                    if (proj.isElectric) {
                        drawLine(Color.White, Offset((proj.x - 20) * scaleX, proj.y * scaleY), Offset((proj.x + 20) * scaleX, proj.y * scaleY), strokeWidth = 3f)
                    }
                }
            }

            // 3. Render Particles Splatters
            activeParticles.forEach { part ->
                drawCircle(
                    color = part.color.copy(alpha = part.life.toFloat() / part.maxLife),
                    radius = part.size * scaleX,
                    center = Offset(part.x * scaleX, part.y * scaleY)
                )
            }

            // 4. Render Fighter - PLAYER 1
            drawFighterVector(this, p1, scaleX, scaleY)

            // 5. Render Fighter - PLAYER 2 / AI
            drawFighterVector(this, p2, scaleX, scaleY)
        }

        // --- HUD Overlay Head up displays ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, start = 14.dp, end = 14.dp)
        ) {
            // Player stats health bar layer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // P1 Health/Energy bar
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        p1.name.uppercase() + " (P1)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        style = TextStyle(letterSpacing = 1.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(18.dp)
                            .background(Color.DarkGray, RoundedCornerShape(4.dp))
                    ) {
                        // Health bar Red/Green gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(p1.health / 100f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFEF4444), Color(0xFF10B981))
                                    ),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Energy bar Power indication
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(8.dp)
                            .background(Color.Black.copy(0.4f), RoundedCornerShape(2.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(p1.energy / 100f)
                                .fillMaxHeight()
                                .background(Color(0xFF00E5FF), RoundedCornerShape(2.dp))
                        )
                    }
                    if (p1.energy >= 80f) {
                        Text("SPECIAL POWER READIED!", color = Color(0xFF00E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Center Timer display badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFF1E293B), CircleShape)
                        .border(2.dp, Color(0xFFFFCC00), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$matchTimeLeft",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                }

                // P2/AI Health/Energy bar right aligned
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        p2.name.uppercase() + if (isSolo) " (AI)" else " (P2)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        style = TextStyle(letterSpacing = 1.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(18.dp)
                            .background(Color.DarkGray, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(p2.health / 100f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF10B981), Color(0xFF3B82F6))
                                    ),
                                    RoundedCornerShape(4.dp)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(8.dp)
                            .background(Color.Black.copy(0.4f), RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(p2.energy / 100f)
                                .fillMaxHeight()
                                .background(Color(0xFFFFA726), RoundedCornerShape(2.dp))
                        )
                    }
                    if (p2.energy >= 80f) {
                        Text("READY DEVASTATION!", color = Color(0xFFFFA726), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Combo Hit notification overlay text
            AnimatedVisibility(
                visible = comboNoticeOpacity > 0f,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = showComboText,
                    color = Color(0xFFFFEE58),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(letterSpacing = 1.sp)
                )
            }
        }

        // Main match status overlay: countdown values
        if (!introFinished) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (introCountdown > 0) "$introCountdown" else "FIGHT!",
                    color = if (introCountdown > 0) Color.White else Color(0xFFEF4444),
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Hit action overlay vignette on slow mo finisher K.O. check
        if (isSlowMotionFinisher) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "K. O.",
                    color = Color(0xFFEF4444),
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(letterSpacing = 6.sp)
                )
            }
        }

        // Pause/Settings buttons
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 12.dp, end = 12.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            IconButton(
                onClick = { pauseState = !pauseState },
                modifier = Modifier
                    .background(Color.White.copy(0.2f), CircleShape)
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = if (pauseState) Icons.Default.PlayArrow else Icons.Default.Close,
                    contentDescription = "Pause",
                    tint = Color.White
                )
            }
        }

        // --- PAUSE DIALOG OVERLAY ---
        if (pauseState) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.width(300.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("GAME PAUSED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                        Button(
                            onClick = { pauseState = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("RESUME COMBAT", color = Color.White)
                        }

                        Button(
                            onClick = {
                                p1.reset(160f)
                                p2.reset(840f)
                                activeProjectiles.clear()
                                activeParticles.clear()
                                matchTimeLeft = 90
                                introCountdown = 3
                                introFinished = false
                                pauseState = false
                                isSlowMotionFinisher = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6600)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("RESTART MATCH", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = onQuit,
                            border = BorderStroke(1.dp, Color.Gray),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("QUIT TO MENU", color = Color.LightGray)
                        }
                    }
                }
            }
        }

        // --- ANDROID VIRTUAL CONTROLS OVERLAYS ---
        // Displayed only if running in a screen context, or generally overlaid on mobile bottom bounds
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Movement buttons Left container side
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    // LEFT Move button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(0.18f), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        p1.vx = -12f
                                        p1.facingRight = false
                                        if (p1.y >= 400f) p1.state = FighterState.RunBackward
                                        tryAwaitRelease()
                                        if (p1.state == FighterState.RunBackward) p1.state = FighterState.Idle
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Left", tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    // RIGHT Move button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(0.18f), RoundedCornerShape(12.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        p1.vx = 12f
                                        p1.facingRight = true
                                        if (p1.y >= 400f) p1.state = FighterState.RunForward
                                        tryAwaitRelease()
                                        if (p1.state == FighterState.RunForward) p1.state = FighterState.Idle
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    // JUMP Button
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color.White.copy(0.18f), CircleShape)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (p1.y >= 400f) {
                                            p1.vy = -24f
                                            p1.state = FighterState.Jump
                                            playCombatSound("punch")
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Jump", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }

                // Combat attack buttons (Punch/Kick/Special/Block) right side layout
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Block button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF374151).copy(0.8f), RoundedCornerShape(8.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        p1.state = FighterState.Block
                                        p1.isBlocking = true
                                        tryAwaitRelease()
                                        p1.state = FighterState.Idle
                                        p1.isBlocking = false
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("BLOCK", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    // Punch Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFFEF4444).copy(0.85f), CircleShape)
                            .clickable {
                                if (p1.stateTimeLeft == 0) {
                                    p1.state = FighterState.Punch
                                    p1.stateTimeLeft = 8
                                    playCombatSound("punch")
                                    checkAttackHit(p1, p2, isPunch = true)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("PUNCH", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    // Kick Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFF3B82F6).copy(0.85f), CircleShape)
                            .clickable {
                                if (p1.stateTimeLeft == 0) {
                                    p1.state = FighterState.Kick
                                    p1.stateTimeLeft = 10
                                    playCombatSound("kick")
                                    checkAttackHit(p1, p2, isPunch = false)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("KICK", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }

                    // Super Projectile Special Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (p1.energy >= 80f) Color(0xFFFFD700) else Color.DarkGray.copy(0.6f),
                                CircleShape
                            )
                            .border(1.5.dp, Color.White, CircleShape)
                            .clickable {
                                shootSpecialProjectile(p1)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SUPER", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

// Visual drawing extension representing highly customized premium vector joint style fighter models
fun drawFighterVector(
    scope: DrawScope,
    f: FighterCharacter,
    scX: Float,
    scY: Float
) {
    val px = f.x * scX
    val py = f.y * scY

    // Ground point translation
    val headRadius = 20f * scX
    val chestY = py - 70f * scY
    val faceOffsetHorizontal = if (f.facingRight) 10f * scX else -10f * scX

    // 1. Draw Legs under state orientations
    val leftLegColor = if (f.isAlamgir) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val rightLegColor = if (f.isAlamgir) Color(0xFF1E293B) else Color(0xFFD1D5DB)

    when (f.state) {
        FighterState.Kick -> {
            // High kick stance - Leg fully raised & extended outward
            val kickTargetX = if (f.facingRight) px + 75f * scX else px - 75f * scX
            val kickTargetY = py - 60f * scY
            scope.drawLine(f.colorAccent, Offset(px, py - 40f * scY), Offset(kickTargetX, kickTargetY), strokeWidth = 14f * scX)
            // Electric effect around kick foot
            if (!f.isAlamgir) {
                scope.drawCircle(
                    Color(0xFF00E5FF).copy(0.4f),
                    radius = 24f * scX,
                    center = Offset(kickTargetX, kickTargetY)
                )
            }
        }
        else -> {
            // Legs standing / running walking joint paths representation
            val movementOffset = if (f.state == FighterState.RunForward || f.state == FighterState.RunBackward) {
                (sin(System.currentTimeMillis() / 80.0) * 20.0).toFloat()
            } else 0f

            scope.drawLine(leftLegColor, Offset(px, py - 30f * scY), Offset(px - 15f * scX + movementOffset, py), strokeWidth = 12f * scX)
            scope.drawLine(rightLegColor, Offset(px, py - 30f * scY), Offset(px + 15f * scX - movementOffset, py), strokeWidth = 12f * scX)
        }
    }

    // 2. Draw Torso / Body (Alamgir: Red Jacket, Khorshed: Blue Hoodie)
    val torsoColor = if (f.isAlamgir) Color(0xFFEF4444) else Color(0xFF3B82F6)
    scope.drawRect(
        color = torsoColor,
        topLeft = Offset(px - 22f * scX, chestY - 10f * scY),
        size = Size(44f * scX, 50f * scY),
        style = Fill
    )

    // Strip details or hoodies zip draws representation
    if (f.isAlamgir) {
        // Black zipper belt or jacket lines
        scope.drawLine(Color.Black, Offset(px, chestY - 10f * scY), Offset(px, chestY + 40f * scY), strokeWidth = 4f)
    } else {
        // Draw combat hood pouch design
        scope.drawCircle(torsoColor, radius = headRadius + 4f, center = Offset(px, chestY - headRadius))
    }

    // 3. Draw Arms and Attack segments
    val gloveColor = if (f.isAlamgir) Color.Black else Color.White
    when (f.state) {
        FighterState.Punch -> {
            // Punching arm fully extended
            val punchTargetX = if (f.facingRight) px + 75f * scX else px - 75f * scX
            scope.drawLine(torsoColor, Offset(px, chestY + 10f * scY), Offset(punchTargetX, chestY + 10f * scY), strokeWidth = 10f * scX)
            scope.drawCircle(gloveColor, radius = 9f * scX, center = Offset(punchTargetX, chestY + 10f * scY))

            // Fire punch particle burst representations
            if (f.isAlamgir) {
                scope.drawCircle(
                    Color(0xFFFF3D00).copy(0.4f),
                    radius = 24f * scX,
                    center = Offset(punchTargetX, chestY + 10f * scY)
                )
            }
        }
        FighterState.Block -> {
            // Protecting arms crossed
            scope.drawLine(torsoColor, Offset(px, chestY), Offset(px + faceOffsetHorizontal, chestY - 10f * scY), strokeWidth = 12f * scX)
            scope.drawCircle(gloveColor, radius = 10f * scX, center = Offset(px + faceOffsetHorizontal, chestY - 10f * scY))
        }
        else -> {
            // Normal fighting passive shield pose arms
            scope.drawLine(torsoColor, Offset(px - 10f * scX, chestY), Offset(px + faceOffsetHorizontal, chestY + 10f * scY), strokeWidth = 10f * scX)
            scope.drawCircle(gloveColor, radius = 8f * scX, center = Offset(px + faceOffsetHorizontal, chestY + 10f * scY))
        }
    }

    // 4. Draw Face / Head
    val skinColor = if (f.isAlamgir) Color(0xFFFFD54F) else Color(0xFFFFCC80)
    scope.drawCircle(skinColor, radius = headRadius, center = Offset(px, chestY - headRadius))

    // Draw Hair or Hood overrides
    if (f.isAlamgir) {
        // Slick back black hair styling representation
        val hPath = Path().apply {
            moveTo(px - headRadius, chestY - headRadius * 2)
            lineTo(px + headRadius, chestY - headRadius * 2)
            lineTo(px + headRadius * 0.5f, chestY - headRadius * 2.5f)
            lineTo(px - headRadius * 0.5f, chestY - headRadius * 2.5f)
            close()
        }
        scope.drawPath(hPath, Color(0xFF1E293B))
    }

    // Grimace face on damage Hit
    if (f.state == FighterState.Hit) {
        // Disoriented tiny red cross eyes representation
        scope.drawLine(Color.Red, Offset(px - 6f, chestY - headRadius - 4f), Offset(px - 2f, chestY - headRadius), strokeWidth = 3f)
        scope.drawLine(Color.Red, Offset(px + 2f, chestY - headRadius - 4f), Offset(px + 6f, chestY - headRadius), strokeWidth = 3f)
    } else {
        // Bold martial eyes
        scope.drawLine(Color.Black, Offset(px - 6f, chestY - headRadius - 2f), Offset(px - 2f, chestY - headRadius - 2f), strokeWidth = 3f)
        scope.drawLine(Color.Black, Offset(px + 2f, chestY - headRadius - 2f), Offset(px + 6f, chestY - headRadius - 2f), strokeWidth = 3f)
    }
}

// ================= 5. MATCH RESULT SCREEN =================

@Composable
fun MatchResultScreen(
    winnerName: String,
    summaryLogs: String,
    onRestart: () -> Unit,
    onMainMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(440.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "BATTLE CHAMPIONSHIP DECISION",
                    color = Color(0xFFFFCC00),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(letterSpacing = 2.sp)
                )

                Text(
                    text = "$winnerName WINS!",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                // Subtitle graphic representations
                Text("CROWNED AS OFFLINE MINI KING!", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)

                HorizontalDivider(color = Color.Gray.copy(0.3f))

                // Stats summaries panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(0.4f), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = summaryLogs,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onMainMenu,
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("MAIN MENU", color = Color.White)
                    }

                    Button(
                        onClick = {
                            SoundSynthesizer.playSfx("special")
                            onRestart()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("RETRY BATTLE", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
