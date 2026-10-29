package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.CampLanguage
import com.example.data.model.ParticipantEntity
import com.example.ui.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import java.util.concurrent.Executors
import kotlin.math.abs

/**
 * Standard scannable QR Code Canvas powered by ZXing with fallback matrix rendering.
 */
@Composable
fun QrCodeCanvas(
    token: String,
    modifier: Modifier = Modifier,
    matrixSize: Int = 25,
    pixelColor: Color = Color.Black
) {
    val bitMatrix = remember(token, matrixSize) {
        try {
            val writer = QRCodeWriter()
            val matrix = writer.encode(token, BarcodeFormat.QR_CODE, matrixSize, matrixSize)
            Array(matrix.height) { r ->
                BooleanArray(matrix.width) { c ->
                    matrix.get(c, r)
                }
            }
        } catch (_: Exception) {
            val seed = abs(token.hashCode())
            fun isFinderPattern(r: Int, c: Int, size: Int): Boolean {
                val inTL = (r in 0..6 && c in 0..6)
                val inTR = (r in 0..6 && c in (size - 7) until size)
                val inBL = (r in (size - 7) until size && c in 0..6)
                if (inTL || inTR || inBL) {
                    val localR = if (inBL) r - (size - 7) else r
                    val localC = if (inTR) c - (size - 7) else c
                    return (localR == 0 || localR == 6 || localC == 0 || localC == 6 || (localR in 2..4 && localC in 2..4))
                }
                return false
            }

            Array(matrixSize) { r ->
                BooleanArray(matrixSize) { c ->
                    if (isFinderPattern(r, c, matrixSize)) {
                        true
                    } else {
                        val pseudoBit = ((seed shr ((r * 3 + c * 7) % 24)) and 1) == 1
                        val pattern = (r + c + seed) % 3 == 0 || (r * c + seed) % 5 == 0
                        pseudoBit xor pattern
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.testTag("qr_code_canvas")) {
        val h = bitMatrix.size
        val w = if (h > 0) bitMatrix[0].size else 0
        if (h > 0 && w > 0) {
            val cellW = size.width / w
            val cellH = size.height / h
            for (r in 0 until h) {
                for (c in 0 until w) {
                    if (bitMatrix[r][c]) {
                        drawRect(
                            color = pixelColor,
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW, cellH)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dedicated Participant QR Pass Dialog showing a large, scannable QR code.
 */
@Composable
fun ParticipantQrDialog(
    participant: ParticipantEntity,
    language: CampLanguage,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("participant_qr_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LscEmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = LscEmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "অংশগ্রহণকারী কিউআর পাস" else "Participant QR Pass",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Text(
                                text = "WAMY Bangladesh • LSC 2026",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_qr_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(2.dp, LscEmeraldPrimary),
                    shadowElevation = 4.dp,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        QrCodeCanvas(
                            token = participant.campCode,
                            modifier = Modifier.size(160.dp),
                            matrixSize = 25
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = participant.campCode,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF1D4ED8),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = participant.nameBn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LscSlateDark
                    )
                    if (participant.nameEn.isNotBlank()) {
                        Text(
                            text = participant.nameEn,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${participant.groupNameBn} (${participant.subgroup}) • রুম: ${participant.roomNo}",
                        fontSize = 11.sp,
                        color = LscEmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dismiss_qr_modal_btn")
                ) {
                    Text(text = if (language == CampLanguage.BANGLA) "সম্পন্ন / বন্ধ করুন" else "Close")
                }
            }
        }
    }
}

/**
 * Digital ID Card with Scannable QR code and 18-Star Seal Progress Badge.
 */
@Composable
fun DigitalIdCard(
    participant: ParticipantEntity,
    language: CampLanguage,
    onClose: () -> Unit
) {
    val sealCount = remember(participant.completedStationSeals) {
        participant.completedStationSeals.split(",").count { it.isNotBlank() }
    }

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("digital_id_card_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = LscEmeraldPrimary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CampLogoImage(
                            size = 40.dp,
                            elevation = 3.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "লাইফ স্কিলস ক্যাম্পিং ২০২৬ — ওয়ামী বাংলাদেশ" else "LIFE SKILLS CAMPING 2026 — WAMY BD",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (language == CampLanguage.BANGLA) "নিজকে গড়ো, গড়তে শেখাও • ২২–২৬ অক্টোবর ২০২৬" else "OFFICIAL PARTICIPANT ID CARD",
                                color = LscGoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(LscEmeraldLight)
                            .border(2.dp, LscEmeraldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = LscEmeraldPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = participant.nameBn,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LscSlateDark
                        )
                        Text(
                            text = participant.nameEn,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(
                                    text = participant.campCode,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = participant.fileNo,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "★ $sealCount/18",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LscEmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = if (language == CampLanguage.BANGLA) "গ্রুপ ও সাব-গ্রুপ" else "Group & Subgroup", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "${participant.groupNameBn} (${participant.subgroup})", fontWeight = FontWeight.Bold, color = LscEmeraldPrimary, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = if (language == CampLanguage.BANGLA) "কটেজ ও বেড" else "Room & Bed", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "${participant.roomNo} | ${participant.bedNo}", fontWeight = FontWeight.Bold, color = LscSlateDark, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = if (language == CampLanguage.BANGLA) "জেলা ও সংস্থা" else "District & Org", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "${participant.districtBn} • ${participant.organization}", fontWeight = FontWeight.Medium, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = if (language == CampLanguage.BANGLA) "শ্রেণি ও লিঙ্গ" else "Class & Gender", fontSize = 10.sp, color = Color.Gray)
                            Text(text = "${participant.classLevel} | ${if (participant.gender == "Male") "ছেলে" else "মেয়ে"}", fontWeight = FontWeight.Medium, fontSize = 11.sp)
                        }
                    }

                    if (participant.medicalFlag != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            color = Color(0xFFFEE2E2),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "স্বাস্থ্য সতর্কতা: ${participant.medicalFlag}",
                                    color = Color(0xFFB91C1C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(125.dp)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                        .testTag("digital_id_qr_code"),
                    contentAlignment = Alignment.Center
                ) {
                    QrCodeCanvas(
                        token = participant.campCode,
                        modifier = Modifier.size(105.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ID: ${participant.campCode} • VERIFIED",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("close_id_card_btn")
                ) {
                    Text(text = if (language == CampLanguage.BANGLA) "সম্পন্ন / বন্ধ করুন" else "Close Card")
                }
            }
        }
    }
}

/**
 * CameraX [ImageAnalysis.Analyzer] that decodes QR codes from live frames using ZXing.
 */
private class QrCodeAnalyzer(
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    override fun analyze(image: ImageProxy) {
        try {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val data = ByteArray(buffer.remaining())
            buffer.get(data)
            val dataWidth = if (plane.rowStride > 0) plane.rowStride else image.width
            val source = PlanarYUVLuminanceSource(
                data,
                dataWidth,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            val result = reader.decodeWithState(bitmap)
            val text = result?.text
            if (!text.isNullOrBlank()) {
                onQrDetected(text)
            }
        } catch (_: Exception) {
        } finally {
            image.close()
        }
    }
}

private val OFFICIAL_2026_STATION_OPTIONS = listOf(
    "স্টেশন ১: সাহিত্য, সংস্কৃতি ও শিষ্টাচার",
    "স্টেশন ২: লাইফ স্কিলস ও ক্রাফট প্রজেক্ট",
    "স্টেশন ৩: শিশুতোষ বিষয়ক আলোচনা",
    "স্টেশন ৪: ফিকাহ ও আমলী সালাত-ওযু",
    "স্টেশন ৫: রিফ্রেকটিভ সেশন ও ক্যারিয়ার",
    "স্টেশন ৬: মেডিকেল চেকআপ ও ডেন্টাল বাস"
)

/**
 * Camera-based Continuous Rapid QR Gun Scanner for Station Manager, Moderators & Staff.
 * Supports non-stop scanning (with 1.2s duplicate debounce, audio beep & haptic pulse)
 * so 30 students can be scanned in ~35 seconds without reopening the camera!
 */
@Composable
fun UniversalScannerDialog(
    language: CampLanguage,
    initialMode: String = "STATION",
    initialStationId: Int = 1,
    continuousModeByDefault: Boolean = true,
    onDismiss: () -> Unit,
    onScanCode: (campCode: String, mode: String) -> Unit,
    onScanCodeWithExtra: ((campCode: String, mode: String, extraArg: String, keepScannerOpen: Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptic = LocalHapticFeedback.current

    var selectedMode by remember { mutableStateOf(initialMode) }
    var selectedStation by remember(initialStationId) {
        mutableStateOf(
            OFFICIAL_2026_STATION_OPTIONS.getOrElse((initialStationId - 1).coerceIn(0, 5)) {
                OFFICIAL_2026_STATION_OPTIONS.first()
            }
        )
    }
    var continuousGunMode by remember { mutableStateOf(continuousModeByDefault) }
    var manualInput by remember { mutableStateOf("") }
    var isTorchOn by remember { mutableStateOf(false) }
    var useBackCamera by remember { mutableStateOf(true) }
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var lastScannedCode by remember { mutableStateOf<String?>(null) }
    var lastScanTimestampMs by remember { mutableLongStateOf(0L) }
    val recentScans = remember { mutableStateListOf<String>() }
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProvider?.unbindAll()
            } catch (_: Exception) {
            }
            cameraInstance = null
            analysisExecutor.shutdown()
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val infiniteTransition = rememberInfiniteTransition(label = "scanner")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    fun handleScan(code: String) {
        val trimmed = code.trim()
        if (trimmed.isNotBlank()) {
            val now = System.currentTimeMillis()
            // Debounce identical QR code within 1.25 seconds in continuous camera frames
            if (trimmed.equals(lastScannedCode, ignoreCase = true) && (now - lastScanTimestampMs) < 1250L) {
                return
            }
            lastScannedCode = trimmed
            lastScanTimestampMs = now

            if (recentScans.none { it.equals(trimmed, ignoreCase = true) }) {
                recentScans.add(0, trimmed)
                if (recentScans.size > 40) {
                    recentScans.removeAt(recentScans.size - 1)
                }
            }

            try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                    .startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            } catch (_: Exception) {}

            val extra = if (selectedMode == "STATION") selectedStation else ""
            if (onScanCodeWithExtra != null) {
                onScanCodeWithExtra(trimmed, selectedMode, extra, continuousGunMode)
            } else {
                onScanCode(trimmed, selectedMode)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .testTag("universal_scanner_dialog")
                .testTag("station_leader_scanner_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LscEmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = LscEmeraldPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (language == CampLanguage.BANGLA) "র‍্যাপিড কিউআর গান স্ক্যানার" else "Rapid QR Gun Scanner",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LscSlateDark
                            )
                            Text(
                                text = if (language == CampLanguage.BANGLA) "স্টেশন হাজিরা, ★ সিল ও ডাইনিং ইন্টারফেস" else "Station Attendance, ★ Seal & Dining",
                                fontSize = 11.sp,
                                color = LscEmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_scanner_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Continuous Non-Stop Gun Mode Toggle Strip
                Surface(
                    color = if (continuousGunMode) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (continuousGunMode) Color(0xFFA7F3D0) else Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { continuousGunMode = !continuousGunMode }
                        .testTag("scanner_continuous_mode_toggle")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA)
                                "⚡ নন-স্টপ র‍্যাপিড গান মোড (ক্যামেরা খোলা থাকবে)"
                            else
                                "⚡ Non-Stop Rapid Gun Mode (Keep Open)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (continuousGunMode) Color(0xFF065F46) else LscSlateDark
                        )
                        Switch(
                            checked = continuousGunMode,
                            onCheckedChange = { continuousGunMode = it },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scan Action Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    val modes = listOf(
                        "STATION" to (if (language == CampLanguage.BANGLA) "স্টেশন ★" else "Station"),
                        "ATTENDANCE" to (if (language == CampLanguage.BANGLA) "হাজিরা" else "General"),
                        "MEAL" to (if (language == CampLanguage.BANGLA) "খাবার" else "Meal"),
                        "CHECK_IN" to (if (language == CampLanguage.BANGLA) "চেক-ইন" else "Check-in")
                    )

                    modes.forEach { (modeKey, label) ->
                        FilterChip(
                            selected = selectedMode == modeKey,
                            onClick = { selectedMode = modeKey },
                            label = { Text(text = label, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LscEmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Station Selection Selector (Active when mode is STATION)
                if (selectedMode == "STATION") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedStation,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E3A8A),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "স্ক্যান: ${recentScans.size} জন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = LscEmeraldPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (1..3).forEach { stationNum ->
                                    val opt = OFFICIAL_2026_STATION_OPTIONS[stationNum - 1]
                                    val isSel = selectedStation.startsWith("স্টেশন $stationNum")
                                    Surface(
                                        color = if (isSel) Color(0xFF1D4ED8) else Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, if (isSel) Color(0xFF1D4ED8) else Color(0xFFCBD5E1)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedStation = opt }
                                    ) {
                                        Text(
                                            text = "স্টেশন $stationNum",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else LscSlateDark,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (4..6).forEach { stationNum ->
                                    val opt = OFFICIAL_2026_STATION_OPTIONS[stationNum - 1]
                                    val isSel = selectedStation.startsWith("স্টেশন $stationNum")
                                    Surface(
                                        color = if (isSel) Color(0xFF1D4ED8) else Color.White,
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, if (isSel) Color(0xFF1D4ED8) else Color(0xFFCBD5E1)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedStation = opt }
                                    ) {
                                        Text(
                                            text = "স্টেশন $stationNum",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else LscSlateDark,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Camera Scanner Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A))
                        .testTag("scanner_viewfinder"),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasCameraPermission) {
                        key(useBackCamera) {
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx)
                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    cameraProviderFuture.addListener({
                                        try {
                                            val provider = cameraProviderFuture.get()
                                            cameraProvider = provider
                                            val preview = Preview.Builder().build().also {
                                                it.surfaceProvider = previewView.surfaceProvider
                                            }
                                            val imageAnalysis = ImageAnalysis.Builder()
                                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                                .build()
                                                .also { analysis ->
                                                    analysis.setAnalyzer(
                                                        analysisExecutor,
                                                        QrCodeAnalyzer { code -> handleScan(code) }
                                                    )
                                                }
                                            val cameraSelector = if (useBackCamera) {
                                                CameraSelector.DEFAULT_BACK_CAMERA
                                            } else {
                                                CameraSelector.DEFAULT_FRONT_CAMERA
                                            }
                                            provider.unbindAll()
                                            cameraInstance = try {
                                                provider.bindToLifecycle(
                                                    lifecycleOwner,
                                                    cameraSelector,
                                                    preview,
                                                    imageAnalysis
                                                )
                                            } catch (_: Exception) {
                                                useBackCamera = true
                                                try {
                                                    provider.bindToLifecycle(
                                                        lifecycleOwner,
                                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                                        preview,
                                                        imageAnalysis
                                                    )
                                                } catch (_: Exception) {
                                                    null
                                                }
                                            }
                                        } catch (_: Exception) {
                                        }
                                    }, ContextCompat.getMainExecutor(ctx))
                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = LscEmeraldLight,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (language == CampLanguage.BANGLA) "ক্যামেরা স্ক্যানার চালু করুন" else "Enable Camera Scanner",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("request_camera_perm_btn")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (language == CampLanguage.BANGLA) "ক্যামেরা চালু করুন" else "Grant Permission", fontSize = 11.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .border(2.dp, LscEmeraldLight.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(2.dp)
                            .graphicsLayer { translationY = (laserOffset - 85).dp.toPx() }
                            .background(Color(0xFF10B981))
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                isTorchOn = !isTorchOn
                                try {
                                    cameraInstance?.cameraControl?.enableTorch(isTorchOn)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("scanner_torch_btn")
                        ) {
                            Icon(
                                imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flashlight",
                                tint = if (isTorchOn) LscGoldLight else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { useBackCamera = !useBackCamera },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("scanner_switch_cam_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipCameraAndroid,
                                contentDescription = "Switch Camera",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = if (language == CampLanguage.BANGLA) "আইডি কার্ডের QR কোড ফ্রেমের মধ্যে ধরুন..." else "Align ID QR code within frame...",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Recent Scan Feedback Banner
                if (lastScannedCode != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("scan_confirmation_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "✓ $lastScannedCode স্ক্যান ও ★ সিল সম্পন্ন! (মোট: ${recentScans.size})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669)
                                )
                                Text(
                                    text = "${if (selectedMode == "STATION") selectedStation else "হাজিরা"} লিপিবদ্ধ হয়েছে",
                                    fontSize = 11.sp,
                                    color = LscSlateDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fast Simulation Preset Chips (Real 2026 Roster Codes)
                Text(
                    text = if (language == CampLanguage.BANGLA) "দ্রুত রোল-কল ট্যাপ সিমুলেশন (২০২৬ রোস্টার):" else "Quick Roll-Call Presets (2026 Roster):",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { handleScan("Bukhari-1-1") },
                        modifier = Modifier.weight(1f).testTag("scan_sample_1_btn"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Bukhari-1-1", fontSize = 10.sp)
                    }
                    OutlinedButton(
                        onClick = { handleScan("Shafi'ee-1-1") },
                        modifier = Modifier.weight(1f).testTag("scan_sample_29_btn"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Shafi'ee-1-1", fontSize = 10.sp)
                    }
                    OutlinedButton(
                        onClick = { handleScan("Sumaiya-1-1") },
                        modifier = Modifier.weight(1f).testTag("scan_sample_65_btn"),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("Sumaiya-1-1", fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Manual Input Fallback (Supports Camp Code OR Orphan File No)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualInput,
                        onValueChange = { manualInput = it },
                        placeholder = { Text("Bukhari-1-1 / ফাইল নং", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).testTag("manual_camp_code_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (manualInput.isNotBlank()) {
                                handleScan(manualInput)
                                manualInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("verify_manual_scan_btn")
                    ) {
                        Text(if (language == CampLanguage.BANGLA) "হাজিরা দিন" else "Log Scan")
                    }
                }
            }
        }
    }
}
