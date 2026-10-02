package com.soltini.app.voiceprint.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltini.app.ui.theme.*
import com.soltini.app.voiceprint.gate.VoiceGateMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * VoiceprintSettingsScreen
 *
 * Full Voice ID settings and calibration console.
 * Controls VoiceGate modes, sensitivity calibration, profile lifecycle, and live testing.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceprintSettingsScreen(
    viewModel: VoiceprintViewModel,
    onNavigateToEnrollment: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val activeProfile by viewModel.activeProfile.collectAsState()
    val gateMode by viewModel.gateMode.collectAsState()
    val sensitivityOffset by viewModel.sensitivityOffset.collectAsState()
    val isModelAvailable by viewModel.isModelAvailable.collectAsState()
    val modelStatus by viewModel.modelStatus.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Voice ID & Verification",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Model Status Notification (if not available)
            if (!isModelAvailable) {
                Surface(
                    color = Color(0xFF2D2318),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ONNX Model Status",
                                color = Color(0xFFF59E0B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = modelStatus,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Enrolled Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (activeProfile != null) "Owner Voiceprint Enrolled" else "No Voiceprint Enrolled",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = if (activeProfile != null) "Encrypted with AndroidKeyStore AES-256 GCM" else "Enroll your voice to personalize security",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (activeProfile != null) {
                        val profile = activeProfile!!
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        val formattedDate = remember(profile.createdAt) {
                            SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault()).format(Date(profile.createdAt))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Enrolled Date", color = TextSecondary, fontSize = 12.sp)
                            Text(formattedDate, color = TextPrimary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Calibrated Threshold", color = TextSecondary, fontSize = 12.sp)
                            Text("${(profile.calibratedThreshold * 100).toInt()}%", color = AccentTeal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Embedding Vector Dim", color = TextSecondary, fontSize = 12.sp)
                            Text("${profile.embeddingDim} dims", color = TextPrimary, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onNavigateToEnrollment,
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            ) {
                                Text("Re-enroll Voice", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = { showDeleteConfirmDialog = true },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RedError.copy(alpha = 0.6f)),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Delete", fontSize = 13.sp)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToEnrollment,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentTeal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text(
                                "Start Voice Enrollment",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Voice Gate Mode Selector
            Text(
                text = "Voice Gate Mode",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeSelectionItem(
                    title = "Protect Sensitive Actions (Recommended)",
                    description = "Anyone can chat with MYRAA, but sensitive actions (door locks, power relays, data wiping) require owner voice verification.",
                    selected = gateMode == VoiceGateMode.OWNER_FOR_SENSITIVE,
                    onClick = { viewModel.setGateMode(VoiceGateMode.OWNER_FOR_SENSITIVE) }
                )

                ModeSelectionItem(
                    title = "Owner Only (Strict Gate)",
                    description = "Complete gate: Audio is only streamed to Gemini Live after owner voice identity is verified.",
                    selected = gateMode == VoiceGateMode.OWNER_ONLY,
                    onClick = { viewModel.setGateMode(VoiceGateMode.OWNER_ONLY) }
                )

                ModeSelectionItem(
                    title = "Disabled (Off)",
                    description = "Microphone audio passes directly to Gemini Live without speaker verification.",
                    selected = gateMode == VoiceGateMode.OFF,
                    onClick = { viewModel.setGateMode(VoiceGateMode.OFF) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sensitivity Calibration Slider
            val calibrated = activeProfile?.calibratedThreshold ?: 0.72f
            val effective = (calibrated + sensitivityOffset).coerceIn(0.50f, 0.95f)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Verification Sensitivity",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${(effective * 100).toInt()}% (Offset: ${if (sensitivityOffset >= 0) "+" else ""}${(sensitivityOffset * 100).toInt()}%)",
                            color = AccentTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Calibrated base: ${(calibrated * 100).toInt()}%. Slide left for noisier rooms, right for stricter verification.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = sensitivityOffset,
                        onValueChange = { viewModel.setSensitivityOffset(it) },
                        valueRange = -0.15f..0.15f,
                        steps = 6,
                        colors = SliderDefaults.colors(
                            thumbColor = AccentTeal,
                            activeTrackColor = AccentTeal,
                            inactiveTrackColor = DarkBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Embedded Live Test Panel
            VoiceprintTestPanel(viewModel = viewModel)

            Spacer(modifier = Modifier.height(20.dp))

            // Security Notice / Disclaimer
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Security Notice",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "On-device voice verification is a convenience layer against casual unauthorized access. Acoustic replay recordings could potentially mimic voice features. High-risk financial payments and system settings require device biometric authentication.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        // Delete Confirmation Dialog
        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Delete Voiceprint?", color = TextPrimary) },
                text = {
                    Text(
                        "This will permanently erase your enrolled voiceprint embedding from the secure hardware storage. Speaker verification will be disabled until you re-enroll.",
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteProfile {
                                showDeleteConfirmDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedError)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Cancel", color = TextPrimary)
                    }
                },
                containerColor = DarkSurface,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun ModeSelectionItem(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) DarkSurface else DarkBackground,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) AccentBlue else DarkBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = AccentBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = if (selected) AccentBlue else TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
