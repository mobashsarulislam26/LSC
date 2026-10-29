package com.example.ui.components

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.MediaStore
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.ParticipantEntity
import com.example.ui.screens.competitions.deriveEighteenStarSeals
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Printable & Exportable WAMY Bangladesh LSC 2026 Participant Graduation Certificate Dialog.
 * Includes the Official 18-Star Station Seal Grid and Official Dual Signatories.
 */
@Composable
fun CertificateGeneratorDialog(
    participant: ParticipantEntity,
    isBangla: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var awardTitle by remember { mutableStateOf("অসামান্য দক্ষতা, শৃঙ্খলা ও নৈতিক নেতৃত্ব") }
    val sealProgress = remember(participant) { deriveEighteenStarSeals(participant) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CardMembership, contentDescription = null, tint = LscGoldSecondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "অফিশিয়াল সনদপত্র (LSC 2026 Certificate)" else "Participant Certificate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = awardTitle,
                    onValueChange = { awardTitle = it },
                    label = { Text(if (isBangla) "স্বীকৃতি / সম্মানের সম্মাননা" else "Honor / Award Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Certificate Preview Canvas
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                    border = BorderStroke(3.dp, LscGoldSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("certificate_canvas_preview")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(LscGoldSecondary.copy(alpha = 0.2f))
                                .border(1.dp, LscGoldSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = LscGoldDark, modifier = Modifier.size(26.dp))
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "ওয়ামী বাংলাদেশ (WAMY Bangladesh)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF047857)
                        )

                        Text(
                            text = "লাইফ স্কিলস ক্যাম্পিং ২০২৬",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = LscEmeraldPrimary
                        )

                        Text(
                            text = "“নিজকে গড়ো, গড়তে শেখাও” • ২২–২৬ অক্টোবর ২০২৬",
                            fontSize = 10.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট, শফিপুর, গাজীপুর",
                            fontSize = 9.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = LscGoldSecondary.copy(alpha = 0.5f), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "সম্মাননা ও প্রশিক্ষণ সনদপত্র",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LscSlateDark
                        )

                        Text(
                            text = "CERTIFICATE OF ACHIEVEMENT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscGoldDark,
                            letterSpacing = 1.4.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "এতদ্বারা সগৌরবে প্রত্যয়ন করা যাচ্ছে যে,",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = participant.nameBn,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LscEmeraldPrimary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "আইডি: ${participant.campCode} | দল: ${participant.groupNameBn} (${participant.subgroup})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "সংস্থা: ${participant.organization} | জেলা: ${participant.districtBn}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "সফলভাবে ৫ দিনব্যাপী আবাসিক ক্যাম্পের ৬টি দক্ষতা স্টেশন (১৮টি সেশন), ঢাকা ও মেট্রোরেল শিক্ষা সফর এবং জীবন গঠনমূলক প্রশিক্ষণে অংশগ্রহণ করেছেন এবং '$awardTitle' অর্জনে কৃতিত্ব প্রদর্শন করেছেন।",
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp,
                            color = LscSlateDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // 18-Star Station Seal Grid Box
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "★ স্টেশন অংশগ্রহণ স্টার সিল: ${sealProgress.earnedCount}/১৮ (৬ স্টেশন × ৩ দিন)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    (1..6).forEach { stNo ->
                                        val s2 = if (sealProgress.hasSeal(stNo, 2)) "★" else "☆"
                                        val s4 = if (sealProgress.hasSeal(stNo, 4)) "★" else "☆"
                                        val s5 = if (sealProgress.hasSeal(stNo, 5)) "★" else "☆"
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(text = "S$stNo", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = LscSlateDark)
                                            Text(text = "$s2$s4$s5", fontSize = 10.sp, color = Color(0xFFD97706))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Official Dual Signatories (WAMY Country Director & Camp Chief)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("ড. মুহাম্মদ রেদওয়ানুর রহমান", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text("কান্ট্রি ডিরেক্টর, ওয়ামী বাংলাদেশ", fontSize = 8.5.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("এয়ার কমোডোর মো: শওকত আলী (অব:)", fontSize = 9.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                Text("ক্যাম্প প্রধান, লাইফ স্কিলস ক্যাম্পিং ২০২৬", fontSize = 8.5.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Export & Share Controls Panel
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBangla) "সনদপত্র এক্সপোর্ট ও শেয়ার অপশন:" else "Export & Share Options:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = LscEmeraldPrimary
                        )

                        Button(
                            onClick = {
                                saveCertificateImageToGallery(context, participant, awardTitle)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LscEmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_image_btn")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBangla) "গ্যালারিতে ইমেজ সেভ করুন (Local Storage)" else "Save Image to Local Storage")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    shareCertificateImageIntent(context, participant, awardTitle)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("share_certificate_intent_btn")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBangla) "ছবি শেয়ার" else "Share Image", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    printCertificatePdf(context, participant, awardTitle)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("print_certificate_pdf_btn")
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBangla) "PDF / প্রিন্ট" else "Print / PDF", fontSize = 12.sp)
                            }
                        }

                        TextButton(
                            onClick = {
                                shareCertificateText(context, participant, awardTitle)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("share_text_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBangla) "টেক্সট হিসেবে শেয়ার করুন" else "Share Text Description", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_certificate_dialog_btn")
            ) {
                Text(if (isBangla) "বন্ধ করুন" else "Close")
            }
        }
    )
}

/**
 * Creates a high-resolution Bitmap rendering of the WAMY LSC 2026 participant certificate.
 */
fun createCertificateBitmap(p: ParticipantEntity, awardTitle: String): Bitmap {
    val sealProgress = deriveEighteenStarSeals(p)
    val width = 1200
    val height = 880
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    canvas.drawColor(AndroidColor.parseColor("#FFFBEB"))

    val paint = Paint().apply { isAntiAlias = true }

    // Outer Border (Gold)
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = 16f
    paint.color = AndroidColor.parseColor("#D97706")
    canvas.drawRect(24f, 24f, width - 24f, height - 24f, paint)

    // Inner Border
    paint.strokeWidth = 4f
    paint.color = AndroidColor.parseColor("#B45309")
    canvas.drawRect(40f, 40f, width - 40f, height - 40f, paint)

    // Header Text
    paint.style = Paint.Style.FILL
    paint.textAlign = Paint.Align.CENTER

    paint.textSize = 20f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#047857")
    canvas.drawText("ওয়ামী বাংলাদেশ (WAMY Bangladesh) — ইয়াতিম তত্ত্বাবধান প্রকল্প", width / 2f, 90f, paint)

    paint.textSize = 34f
    paint.color = AndroidColor.parseColor("#064E3B")
    canvas.drawText("লাইফ স্কিলস ক্যাম্পিং ২০২৬ (Life Skills Camping 2026)", width / 2f, 132f, paint)

    paint.textSize = 20f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#92400E")
    canvas.drawText("“নিজকে গড়ো, গড়তে শেখাও” • ২২–২৬ অক্টোবর ২০২৬ • রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট, গাজীপুর", width / 2f, 166f, paint)

    // Gold Divider Line
    paint.strokeWidth = 3f
    paint.color = AndroidColor.parseColor("#F59E0B")
    canvas.drawLine(150f, 190f, width - 150f, 190f, paint)

    // Title
    paint.textSize = 36f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#0F172A")
    canvas.drawText("সম্মাননা ও প্রশিক্ষণ সনদপত্র", width / 2f, 242f, paint)

    paint.textSize = 17f
    paint.color = AndroidColor.parseColor("#B45309")
    canvas.drawText("CERTIFICATE OF ACHIEVEMENT", width / 2f, 272f, paint)

    paint.textSize = 21f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#334155")
    canvas.drawText("এতদ্বারা সগৌরবে প্রত্যয়ন করা যাচ্ছে যে,", width / 2f, 318f, paint)

    // Participant Name
    paint.textSize = 44f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#059669")
    canvas.drawText(p.nameBn, width / 2f, 382f, paint)

    // ID & Details
    paint.textSize = 21f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#475569")
    val detailsStr = "ক্যাম্প আইডি: ${p.campCode}  |  দল: ${p.groupNameBn} (${p.subgroup})  |  সংস্থা: ${p.organization}  |  জেলা: ${p.districtBn}"
    canvas.drawText(detailsStr, width / 2f, 426f, paint)

    // Award / Honor Description
    paint.textSize = 21f
    paint.color = AndroidColor.parseColor("#1E293B")
    val desc1 = "সফলভাবে ৫ দিনব্যাপী আবাসিক ক্যাম্পের ৬টি দক্ষতা স্টেশন (১৮টি সেশন), ঢাকা ও মেট্রোরেল শিক্ষা সফর"
    val desc2 = "এবং জীবন গঠনমূলক কর্মশালায় অংশগ্রহণ করেছেন ও '$awardTitle' অর্জনে কৃতিত্ব প্রদর্শন করেছেন।"
    canvas.drawText(desc1, width / 2f, 485f, paint)
    canvas.drawText(desc2, width / 2f, 520f, paint)

    // 18-Star Seal Summary Strip
    val starSummary = (1..6).joinToString("   |   ") { st ->
        val s2 = if (sealProgress.hasSeal(st, 2)) "★" else "☆"
        val s4 = if (sealProgress.hasSeal(st, 4)) "★" else "☆"
        val s5 = if (sealProgress.hasSeal(st, 5)) "★" else "☆"
        "স্টেশন-$st: $s2$s4$s5"
    }
    paint.textSize = 20f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#B45309")
    canvas.drawText("অর্জিত স্টেশন স্টার সিল (${sealProgress.earnedCount}/১৮):   $starSummary", width / 2f, 590f, paint)

    // Signatures / Footer line
    paint.strokeWidth = 2f
    paint.color = AndroidColor.parseColor("#CBD5E1")
    canvas.drawLine(120f, 720f, 480f, 720f, paint)
    canvas.drawLine(width - 480f, 720f, width - 120f, 720f, paint)

    paint.textSize = 20f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#0F172A")
    canvas.drawText("ড. মুহাম্মদ রেদওয়ানুর রহমান", 300f, 755f, paint)
    canvas.drawText("এয়ার কমোডোর মো: শওকত আলী (অব:)", width - 300f, 755f, paint)

    paint.textSize = 16f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#64748B")
    canvas.drawText("কান্ট্রি ডিরেক্টর, ওয়ামী বাংলাদেশ", 300f, 785f, paint)
    canvas.drawText("ক্যাম্প প্রধান, লাইফ স্কিলস ক্যাম্পিং ২০২৬", width - 300f, 785f, paint)

    return bitmap
}

/**
 * Saves generated certificate image to device Local Storage (Pictures/LifeSkillsCamp) via MediaStore.
 */
fun saveCertificateImageToGallery(context: Context, p: ParticipantEntity, awardTitle: String) {
    try {
        val bitmap = createCertificateBitmap(p, awardTitle)
        val filename = "LSC_Certificate_${p.campCode}_${System.currentTimeMillis()}.png"
        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/LifeSkillsCamp")
            }
            val resolver = context.contentResolver
            val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                fos = resolver.openOutputStream(imageUri)
            }
        } else {
            @Suppress("DEPRECATION")
            val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_PICTURES
            )
            val lscDir = File(imagesDir, "LifeSkillsCamp").apply { if (!exists()) mkdirs() }
            val imageFile = File(lscDir, filename)
            fos = FileOutputStream(imageFile)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            Toast.makeText(
                context,
                "সনদপত্র গ্যালারিতে (Pictures/LifeSkillsCamp) সফলভাবে সংরক্ষণ করা হয়েছে!",
                Toast.LENGTH_LONG
            ).show()
        } ?: run {
            Toast.makeText(context, "সংরক্ষণ করতে ব্যর্থ হয়েছে", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "সংরক্ষণ করতে ত্রুটি: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Shares the generated certificate image via Intent-based sharing (FileProvider Uri).
 */
fun shareCertificateImageIntent(context: Context, p: ParticipantEntity, awardTitle: String) {
    try {
        val bitmap = createCertificateBitmap(p, awardTitle)
        val cachePath = File(context.cacheDir, "certificates")
        cachePath.mkdirs()
        val file = File(cachePath, "certificate_${p.campCode}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        val contentUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            putExtra(Intent.EXTRA_SUBJECT, "সনদপত্র - ${p.nameBn}")
            putExtra(
                Intent.EXTRA_TEXT,
                "🎓 লাইফ স্কিলস ক্যাম্পিং ২০২৬ (ওয়ামী বাংলাদেশ) - ${p.nameBn} এর অর্জিত সনদপত্র ('$awardTitle')"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "সনদপত্র শেয়ার করুন (Share Certificate)"))
    } catch (e: Exception) {
        Toast.makeText(context, "শেয়ার করতে সমস্যা হয়েছে: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Native Android WebView PrintManager function to generate printable PDF for Certificate.
 */
private fun printCertificatePdf(context: Context, p: ParticipantEntity, awardTitle: String) {
    try {
        val sealProgress = deriveEighteenStarSeals(p)
        val starRowHtml = (1..6).joinToString("") { st ->
            val s2 = if (sealProgress.hasSeal(st, 2)) "★" else "☆"
            val s4 = if (sealProgress.hasSeal(st, 4)) "★" else "☆"
            val s5 = if (sealProgress.hasSeal(st, 5)) "★" else "☆"
            "<span style='margin:0 8px;'><strong>স্টেশন-$st:</strong> <span style='color:#D97706;'>$s2$s4$s5</span></span>"
        }

        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: 'SolaimanLipi', 'Kalpurush', sans-serif; text-align: center; padding: 28px; background: #FFFBEB; border: 10px solid #D97706; }
                    .org { font-size: 16px; color: #047857; font-weight: bold; }
                    .header { font-size: 28px; color: #064E3B; font-weight: bold; margin: 4px 0; }
                    .subheader { font-size: 15px; color: #92400E; margin-bottom: 16px; }
                    .title { font-size: 30px; color: #0F172A; font-weight: bold; margin: 16px 0; }
                    .name { font-size: 34px; color: #059669; font-weight: bold; margin: 12px 0; border-bottom: 2px solid #D97706; display: inline-block; padding-bottom: 4px; }
                    .details { font-size: 17px; color: #334155; margin: 16px 0; line-height: 1.6; }
                    .seals { background: #FFFFFF; border: 1px solid #FDE68A; border-radius: 8px; padding: 10px; margin: 16px auto; font-size: 15px; }
                    .signatures { margin-top: 44px; display: flex; justify-content: space-between; padding: 0 36px; }
                    .sig-box { display: inline-block; width: 44%; text-align: center; }
                </style>
            </head>
            <body>
                <div class="org">ওয়ামী বাংলাদেশ (WAMY Bangladesh) — ইয়াতিম তত্ত্বাবধান প্রকল্প</div>
                <div class="header">লাইফ স্কিলস ক্যাম্পিং ২০২৬ (Life Skills Camping 2026)</div>
                <div class="subheader">“নিজকে গড়ো, গড়তে শেখাও” • ২২–২৬ অক্টোবর ২০২৬ • রাঙামাটি ওয়াটারফ্রন্ট রিসোর্ট, শফিপুর, গাজীপুর</div>
                <div class="title">সম্মাননা ও প্রশিক্ষণ সনদপত্র (CERTIFICATE OF ACHIEVEMENT)</div>
                <p>এতদ্বারা সগৌরবে প্রত্যয়ন করা যাচ্ছে যে,</p>
                <div class="name">${p.nameBn}</div>
                <p><strong>ক্যাম্প আইডি:</strong> ${p.campCode} | <strong>দল:</strong> ${p.groupNameBn} (${p.subgroup}) | <strong>সংস্থা:</strong> ${p.organization} | <strong>জেলা:</strong> ${p.districtBn}</p>
                <div class="details">
                    সফলভাবে ৫ দিনব্যাপী আবাসিক ক্যাম্পের ৬টি দক্ষতা স্টেশন (১৮টি সেশন), ঢাকা ও মেট্রোরেল শিক্ষা সফর এবং জীবন গঠনমূলক কর্মশালায় অংশগ্রহণ করেছেন ও <strong>'$awardTitle'</strong> অর্জনে বিশেষ কৃতিত্ব প্রদর্শন করেছেন।
                </div>
                <div class="seals">
                    <div><strong>★ অর্জিত স্টেশন স্টার সিল: ${sealProgress.earnedCount}/১৮</strong></div>
                    <div style="margin-top:6px;">$starRowHtml</div>
                </div>
                <div class="signatures">
                    <div class="sig-box">
                        <br>___________________________<br>
                        <strong>ড. মুহাম্মদ রেদওয়ানুর রহমান</strong><br>কান্ট্রি ডিরেক্টর, ওয়ামী বাংলাদেশ
                    </div>
                    <div class="sig-box">
                        <br>___________________________<br>
                        <strong>এয়ার কমোডোর মো: শওকত আলী (অব:)</strong><br>ক্যাম্প প্রধান, লাইফ স্কিলস ক্যাম্পিং ২০২৬
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()

        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val printAdapter = webView.createPrintDocumentAdapter("LSC_Certificate_${p.campCode}")
                printManager.print("LSC_Certificate_${p.campCode}", printAdapter, PrintAttributes.Builder().build())
            }
        }
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    } catch (e: Exception) {
        Toast.makeText(context, "প্রিন্ট সেবা চালু করা যায়নি: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareCertificateText(context: Context, p: ParticipantEntity, awardTitle: String) {
    try {
        val sealProgress = deriveEighteenStarSeals(p)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                """
                🎓 লাইফ স্কিলস ক্যাম্পিং ২০২৬ (ওয়ামী বাংলাদেশ) - অর্জন সনদপত্র 🏆
                “নিজকে গড়ো, গড়তে শেখাও” (২২–২৬ অক্টোবর ২০২৬)
                --------------------------------------------------
                অংশগ্রহণকারীর নাম: ${p.nameBn} (${p.nameEn})
                ক্যাম্প আইডি: ${p.campCode}
                দল ও সাব-গ্রুপ: ${p.groupNameBn} (${p.subgroup})
                সংস্থা ও জেলা: ${p.organization}, ${p.districtBn}
                অর্জিত স্টেশন স্টার সিল: ★ ${sealProgress.earnedCount}/১৮
                বিশেষ অর্জন: $awardTitle
                --------------------------------------------------
                ড. মুহাম্মদ রেদওয়ানুর রহমান (কান্ট্রি ডিরেক্টর, ওয়ামী বাংলাদেশ)
                এয়ার কমোডোর মো: শওকত আলী (অব:) (ক্যাম্প প্রধান, LSC ২০২৬)
                """.trimIndent()
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Certificate Text"))
    } catch (_: Exception) {}
}
