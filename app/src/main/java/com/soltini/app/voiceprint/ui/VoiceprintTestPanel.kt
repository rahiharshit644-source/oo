package com.soltini.app.voiceprint.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltini.app.ui.theme.*
import com.soltini.app.voiceprint.domain.VoiceprintVerifier

/**
 * VoiceprintTestPanel
 *
 * Interactive testing panel with live score bar and state chips
 * allowing the user to verify themselves or test against other voices in real time.
 */
@Composable
fun VoiceprintTestPanel(
    viewModel: VoiceprintViewModel,
    modifier: Modifier = Modifier
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val liveLevel by viewModel.liveInputLevel.collectAsState()
    val testResult by viewModel.testResult.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentTeal.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentTeal.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = AccentTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "VoicePrint Live Test",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Speak a sentence to test speaker verification score",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (activeProfile == null) {
                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Text(
                        text = "Please enroll your voice profile first to enable live testing.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                return@Column
            }

            // Test Button & Mic Meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (isRecording) {
                            viewModel.stopRecordingAndTest()
                        } else {
                            viewModel.startRecording(forTestPanel = true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) RedError else AccentTeal
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRecording) "Stop & Verify" else "Test My Voice",
                        fontWeight = FontWeight.SemiBold,
                        color = if (isRecording) Color.White else Color.Black
                    )
                }

                if (isRecording) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Listening...",
                            color = RedError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkBorder)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(liveLevel.coerceIn(0.05f, 1f))
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(RedError)
                            )
                        }
                    }
                }
            }

            // Results Section
            AnimatedVisibility(visible = testResult != null) {
                testResult?.let { res ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkBackground)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Similarity Score",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )

                            // State Chip
                            val (chipColor, chipBg, chipText) = when (res.state) {
                                VoiceprintVerifier.VerificationStateEnum.OWNER ->
                                    Triple(AccentTeal, AccentTeal.copy(alpha = 0.2f), "OWNER VERIFIED")
                                VoiceprintVerifier.VerificationStateEnum.NOT_OWNER ->
                                    Triple(RedError, RedError.copy(alpha = 0.2f), "NOT OWNER")
                                VoiceprintVerifier.VerificationStateEnum.UNKNOWN ->
                                    Triple(Color(0xFFFFA726), Color(0xFFFFA726).copy(alpha = 0.2f), "UNKNOWN")
                            }

                            Surface(
                                color = chipBg,
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, chipColor)
                            ) {
                                Text(
                                    text = chipText,
                                    color = chipColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Score percentage
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${(res.score * 100).toInt()}%",
                                color = if (res.state == VoiceprintVerifier.VerificationStateEnum.OWNER) AccentTeal else TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Threshold: ${(res.effectiveThreshold * 100).toInt()}%",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Score Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(DarkBorder)
                        ) {
                            // Progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(res.score.coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        if (res.state == VoiceprintVerifier.VerificationStateEnum.OWNER) AccentTeal else RedError
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}
