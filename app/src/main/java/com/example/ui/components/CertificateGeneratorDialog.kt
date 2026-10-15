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
import com.example.ui.theme.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Printable & Exportable Participant Achievement / Graduation Certificate Dialog.
 * Supports saving as Image to local storage (Pictures/LifeSkillsCamp) and intent-based sharing.
 */
@Composable
fun CertificateGeneratorDialog(
    participant: ParticipantEntity,
    isBangla: Boolean = true,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var awardTitle by remember { mutableStateOf("অসামান্য পারফরম্যান্স ও নেতৃত্ব") }

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
                        text = if (isBangla) "অংশগ্রহণকারী সনদপত্র (Certificate)" else "Participant Certificate",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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
                // Award Title Selector
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
                        // Header Gold Emblem
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(LscGoldSecondary.copy(alpha = 0.2f))
                                .border(1.dp, LscGoldSecondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = LscGoldDark, modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "লাইফ স্কিলস ক্যাম্প ২০২৬",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LscEmeraldPrimary
                        )

                        Text(
                            text = "Life Skills Residential Camp 2026",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(color = LscGoldSecondary.copy(alpha = 0.5f), thickness = 1.dp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "সম্মাননা ও প্রশিক্ষণ সনদপত্র",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LscSlateDark
                        )

                        Text(
                            text = "CERTIFICATE OF ACHIEVEMENT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LscGoldDark,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "এতদ্বারা সগৌরবে প্রত্যয়ন করা যাচ্ছে যে,",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = participant.nameBn,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LscEmeraldPrimary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "আইডি: ${participant.campCode} | দল: ${participant.groupNameBn} (${participant.subgroup})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )

                        Text(
                            text = "জেলা: ${participant.districtBn}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "সফলভাবে ৪ দিনব্যাপী আবাসিক ক্যাম্পের সকল দক্ষতা স্টেশন, জীবন গঠনমূলক কর্মশালা ও শারীরিক প্রশিক্ষণে অংশগ্রহণ করেছেন এবং '$awardTitle' অর্জনে কৃতিত্ব প্রদর্শন করেছেন।",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            color = LscSlateDark
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Signatures
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("হাফেজ মাওলানা শিব্বীর আহমদ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("ক্যাম্প প্রধান ও পরিচালক", fontSize = 9.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("আকরাম হোসেন রাজু", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("আহবায়ক, লাইফ স্কিলস ক্যাম্প", fontSize = 9.sp, color = Color.Gray)
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

                        // 1. Save Image to Storage (Pictures/LifeSkillsCamp)
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
                            // 2. Share Image Intent
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

                            // 3. Print / PDF
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

                        // 4. Share Text Description
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
 * Creates a high-resolution Bitmap rendering of the participant certificate.
 */
fun createCertificateBitmap(p: ParticipantEntity, awardTitle: String): Bitmap {
    val width = 1200
    val height = 850
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background Color
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

    paint.textSize = 34f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#064E3B")
    canvas.drawText("লাইফ স্কিলস ক্যাম্প ২০২৬ (Life Skills Camp)", width / 2f, 110f, paint)

    paint.textSize = 20f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#64748B")
    canvas.drawText("আবাসিক কওমী তরুণদের চরিত্র গঠন ও দক্ষতা উন্নয়ন কোর্স", width / 2f, 150f, paint)

    // Gold Divider Line
    paint.strokeWidth = 3f
    paint.color = AndroidColor.parseColor("#F59E0B")
    canvas.drawLine(150f, 180f, width - 150f, 180f, paint)

    // Title
    paint.textSize = 38f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#0F172A")
    canvas.drawText("সম্মাননা ও প্রশিক্ষণ সনদপত্র", width / 2f, 240f, paint)

    paint.textSize = 18f
    paint.color = AndroidColor.parseColor("#B45309")
    canvas.drawText("CERTIFICATE OF ACHIEVEMENT", width / 2f, 275f, paint)

    // Subtitle
    paint.textSize = 22f
    paint.color = AndroidColor.parseColor("#334155")
    canvas.drawText("এতদ্বারা সগৌরবে প্রত্যয়ন করা যাচ্ছে যে,", width / 2f, 330f, paint)

    // Participant Name
    paint.textSize = 46f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#059669")
    canvas.drawText(p.nameBn, width / 2f, 400f, paint)

    // ID & Details
    paint.textSize = 22f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#475569")
    val detailsStr = "ক্যাম্প আইডি: ${p.campCode}   |   দল: ${p.groupNameBn} (${p.subgroup})   |   জেলা: ${p.districtBn}"
    canvas.drawText(detailsStr, width / 2f, 450f, paint)

    // Award / Honor Description
    paint.textSize = 22f
    paint.color = AndroidColor.parseColor("#1E293B")
    val desc1 = "সফলভাবে ৪ দিনব্যাপী আবাসিক ক্যাম্পের সকল দক্ষতা স্টেশন, জীবন গঠনমূলক কর্মশালা"
    val desc2 = "ও শারীরিক প্রশিক্ষণে অংশগ্রহণ করেছেন এবং '$awardTitle' অর্জনে বিশেষ কৃতিত্ব প্রদর্শন করেছেন।"
    canvas.drawText(desc1, width / 2f, 520f, paint)
    canvas.drawText(desc2, width / 2f, 560f, paint)

    // Signatures / Footer line
    paint.strokeWidth = 2f
    paint.color = AndroidColor.parseColor("#CBD5E1")
    canvas.drawLine(120f, 690f, 480f, 690f, paint)
    canvas.drawLine(width - 480f, 690f, width - 120f, 690f, paint)

    paint.textSize = 20f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    paint.color = AndroidColor.parseColor("#0F172A")
    canvas.drawText("হাফেজ মাওলানা শিব্বীর আহমদ", 300f, 725f, paint)
    canvas.drawText("আকরাম হোসেন রাজু", width - 300f, 725f, paint)

    paint.textSize = 16f
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    paint.color = AndroidColor.parseColor("#64748B")
    canvas.drawText("ক্যাম্প প্রধান ও পরিচালক", 300f, 755f, paint)
    canvas.drawText("আহবায়ক, লাইফ স্কিলস ক্যাম্প", width - 300f, 755f, paint)

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
                "🎓 লাইফ স্কিলস ক্যাম্প ২০২৬ - ${p.nameBn} এর অর্জিত সনদপত্র ('$awardTitle')"
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
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <style>
                    body { font-family: 'SolaimanLipi', 'Kalpurush', sans-serif; text-align: center; padding: 30px; background: #FFFBEB; border: 10px solid #D97706; }
                    .header { font-size: 28px; color: #064E3B; font-weight: bold; margin-bottom: 5px; }
                    .subheader { font-size: 16px; color: #64748B; margin-bottom: 20px; }
                    .title { font-size: 32px; color: #0F172A; font-weight: bold; text-transform: uppercase; margin: 20px 0; }
                    .name { font-size: 36px; color: #059669; font-weight: bold; margin: 15px 0; border-bottom: 2px solid #D97706; display: inline-block; padding-bottom: 5px; }
                    .details { font-size: 18px; color: #334155; margin: 20px 0; line-height: 1.6; }
                    .signatures { margin-top: 50px; display: flex; justify-content: space-between; padding: 0 40px; }
                    .sig-box { display: inline-block; width: 40%; text-align: center; }
                </style>
            </head>
            <body>
                <div class="header">লাইফ স্কিলস ক্যাম্প ২০২৬ (Life Skills Camp)</div>
                <div class="subheader">আবাসিক কওমী তরুণদের চরিত্র গঠন ও দক্ষতা উন্নয়ন কোর্স</div>
                <div class="title">সনদপত্র (CERTIFICATE OF ACHIEVEMENT)</div>
                <p>এতদ্বারা সগৌরবে প্রত্যয়ন করা যাচ্ছে যে,</p>
                <div class="name">${p.nameBn}</div>
                <p><strong>ক্যাম্প আইডি:</strong> ${p.campCode} | <strong>দল:</strong> ${p.groupNameBn} (${p.subgroup}) | <strong>জেলা:</strong> ${p.districtBn}</p>
                <div class="details">
                    সফলভাবে ৪ দিনব্যাপী আবাসিক ক্যাম্পের সকল দক্ষতা স্টেশন, জীবন গঠনমূলক কর্মশালা ও শারীরিক প্রশিক্ষণে অংশগ্রহণ করেছেন এবং <strong>'$awardTitle'</strong> অর্জনে বিশেষ কৃতিত্ব প্রদর্শন করেছেন।
                </div>
                <div class="signatures">
                    <div class="sig-box">
                        <br>___________________________<br>
                        <strong>হাফেজ মাওলানা শিব্বীর আহমদ</strong><br>ক্যাম্প প্রধান ও পরিচালক
                    </div>
                    <div class="sig-box">
                        <br>___________________________<br>
                        <strong>আকরাম হোসেন রাজু</strong><br>আহবায়ক, লাইফ স্কিলস ক্যাম্প
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
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                """
                🎓 লাইফ স্কিলস ক্যাম্প ২০২৬ - শিক্ষার্থী অর্জন সনদপত্র 🏆
                --------------------------------------------------
                অংশগ্রহণকারীর নাম: ${p.nameBn} (${p.nameEn})
                ক্যাম্প আইডি: ${p.campCode}
                দল: ${p.groupNameBn} (${p.subgroup})
                জেলা: ${p.districtBn}
                অর্জন: $awardTitle
                --------------------------------------------------
                লাইফ স্কিলস ক্যাম্প ২০২৬ পরিচালনা পর্ষদ
                """.trimIndent()
            )
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Certificate Text"))
    } catch (_: Exception) {}
}
