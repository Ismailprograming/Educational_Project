package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.DailyReportEntity
import com.example.data.model.InstituteEntity
import com.example.data.model.MonthlyReportEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TeacherEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportGenerator {

    /**
     * Checks if a specific package is installed on the device
     */
    fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Determines the optimal WhatsApp package:
     * WhatsApp Business ("com.whatsapp.w4b") prioritized if installed,
     * otherwise standard WhatsApp ("com.whatsapp"), or null if neither.
     */
    fun getTargetWhatsAppPackage(context: Context): String? {
        val w4b = "com.whatsapp.w4b"
        val regular = "com.whatsapp"
        return when {
            isPackageInstalled(context, w4b) -> w4b
            isPackageInstalled(context, regular) -> regular
            else -> null
        }
    }

    /**
     * Formats the WhatsApp message text from template and variables
     */
    fun formatDailyWhatsAppMessage(
        template: String,
        student: StudentEntity,
        report: DailyReportEntity,
        institute: InstituteEntity
    ): String {
        val sabaqStr = "${report.sabaqSurah} (${report.sabaqFromAyah} تا ${report.sabaqToAyah}) - ${report.sabaqLines} سطریں"
        val sabqiStr = "پارہ ${report.sabqiJuz} (غلطیاں: ${report.sabqiMistakes})"
        val manzilStr = "پارہ ${report.manzilJuz} (غلطیاں: ${report.manzilMistakes})"

        return """
السلام علیکم ورحمۃ اللہ وبرکاتہ

محترم والدین (${student.fatherName})،
طالب علم: ${student.name}
تاریخ: ${report.date}

ادارہ: ${institute.name}
-----------------------------
سبق: $sabaqStr
سبقی: $sabqiStr
منزل: $manzilStr

حاضری: ${report.attendance}
کارکردگی: ${report.performance}
${if (report.remarks.isNotBlank()) "استاد کے تاثرات: ${report.remarks}\n" else ""}
${institute.officialDua}

جزاکم اللہ خیراً
        """.trimIndent()
    }

    /**
     * Formats monthly report text message for WhatsApp
     */
    fun formatMonthlyWhatsAppMessage(
        student: StudentEntity,
        monthly: MonthlyReportEntity,
        institute: InstituteEntity,
        dailyReports: List<DailyReportEntity> = emptyList()
    ): String {
        val tableBuilder = StringBuilder()
        if (dailyReports.isNotEmpty()) {
            tableBuilder.append("\n\nیومیہ تفصیلی کارکردگی:\n")
            tableBuilder.append("تاریخ | سبق | سبقی (غلطیاں) | منزل (غلطیاں)\n")
            tableBuilder.append("------------------------------------------\n")
            dailyReports.sortedBy { it.date }.forEach { d ->
                val dayNumber = d.date.takeLast(2).toIntOrNull()?.toString() ?: d.date
                tableBuilder.append("$dayNumber | ${d.sabaqSurah} | پارہ ${d.sabqiJuz}: ${d.sabqiMistakes} | پارہ ${d.manzilJuz}: ${d.manzilMistakes}\n")
            }
        }

        return """
السلام علیکم ورحمۃ اللہ وبرکاتہ

محترم والدین (${student.fatherName})،
طالب علم: ${student.name}
ماہانہ حفظ رپورٹ برائے: ${monthly.monthNameUrdu}

ادارہ: ${institute.name}
-----------------------------
حاضری: ${monthly.presentDays} / ${monthly.totalCalendarDays} دن
غیر حاضر: ${monthly.absentDays} دن | رخصت: ${monthly.leaveDays} دن

سبق: ${monthly.totalSabaqLines} سطریں (${monthly.totalSabaqDays} ایام)
سبقی: ${monthly.totalSabqiEntries} پارے (مجموعی غلطیاں: ${monthly.totalSabqiMistakes})
منزل: ${monthly.totalManzilEntries} پارے (منزل غلطیاں: ${monthly.totalManzilMistakes})

مجموعی پختگی: ${monthly.overallAccuracy}%$tableBuilder

${institute.officialDua}
جزاکم اللہ خیراً
        """.trimIndent()
    }

    /**
     * Generates a clean .DOC (Microsoft Word compatible document)
     */
    fun generateMonthlyDoc(
        context: Context,
        student: StudentEntity,
        monthly: MonthlyReportEntity,
        institute: InstituteEntity,
        teacher: TeacherEntity,
        dailyReports: List<DailyReportEntity> = emptyList()
    ): File? {
        return try {
            val cleanStudentName = student.name.replace(" ", "_")
            val fileName = "${cleanStudentName}_MonthlyReport_${monthly.month}_${monthly.year}.doc"
            val file = File(context.cacheDir, fileName)

            val writer = OutputStreamWriter(FileOutputStream(file), "UTF-8")
            writer.write("""
<html xmlns:o='urn:schemas-microsoft-com:office:office' xmlns:w='urn:schemas-microsoft-com:office:word' xmlns='http://www.w3.org/TR/REC-html40'>
<head>
<meta charset="utf-8">
<title>ماہانہ حفظ رپورٹ</title>
<style>
body { font-family: 'Arial', sans-serif; direction: rtl; text-align: right; margin: 30px; }
h1 { text-align: center; color: #1D1838; margin-bottom: 5px; }
h2 { text-align: center; color: #D48817; margin-top: 0; font-size: 16px; }
.info-table, .data-table { width: 100%; border-collapse: collapse; margin-top: 15px; }
.info-table td { padding: 8px; border: 1px solid #D1CDDD; font-size: 13px; }
.data-table th { background-color: #282149; color: #FFFFFF; padding: 8px; border: 1px solid #51486F; font-size: 12px; }
.data-table td { padding: 7px; border: 1px solid #D1CDDD; font-size: 12px; text-align: center; }
.dua { text-align: center; color: #D48817; font-weight: bold; margin-top: 25px; padding: 10px; background: #F8F7FC; border-radius: 6px; }
.footer { margin-top: 40px; display: flex; justify-content: space-between; }
</style>
</head>
<body>
<h1>${institute.name}</h1>
<h2>شعبہ تحفیظ القرآن الکریم • ماہانہ رپورٹ برائے: ${monthly.monthNameUrdu}</h2>

<table class="info-table">
<tr>
<td><b>طالب علم:</b> ${student.name}</td>
<td><b>ولدیت:</b> ${student.fatherName}</td>
<td><b>رول نمبر:</b> ${student.rollNumber.ifEmpty { "104" }}</td>
</tr>
<tr>
<td><b>ابتدائی پارہ:</b> ${student.startingJuz}</td>
<td><b>موجودہ پارہ:</b> ${student.currentJuz}</td>
<td><b>مجموعی حاضری:</b> ${monthly.presentDays} / ${monthly.totalCalendarDays} دن</td>
</tr>
</table>

<h3 style="margin-top: 20px; color: #1D1838;">خلاصہ کارکردگی</h3>
<table class="info-table">
<tr>
<td><b>سبق سطریں:</b> ${monthly.totalSabaqLines}</td>
<td><b>سبقی پارے:</b> ${monthly.totalSabqiEntries} (غلطیاں: ${monthly.totalSabqiMistakes})</td>
<td><b>منزل پارے:</b> ${monthly.totalManzilEntries} (غلطیاں: ${monthly.totalManzilMistakes})</td>
<td><b>مجموعی پختگی:</b> ${monthly.overallAccuracy}%</td>
</tr>
</table>

<h3 style="margin-top: 25px; color: #1D1838;">یومیہ کارکردگی چارٹ</h3>
<table class="data-table">
<tr>
<th>تاریخ</th>
<th>سبق</th>
<th>سبقی (غلطیاں)</th>
<th>منزل (غلطیاں)</th>
<th>حاضری</th>
</tr>
""")

            if (dailyReports.isEmpty()) {
                writer.write("<tr><td colspan='5'>کوئی یومیہ اندراج موجود نہیں ہے۔</td></tr>")
            } else {
                dailyReports.sortedBy { it.date }.forEach { d ->
                    val dayNum = d.date.takeLast(2).toIntOrNull()?.toString() ?: d.date
                    writer.write("""
<tr>
<td>$dayNum</td>
<td>${d.sabaqSurah} (${d.sabaqLines} سطریں)</td>
<td>پارہ ${d.sabqiJuz} : ${d.sabqiMistakes}</td>
<td>پارہ ${d.manzilJuz} : ${d.manzilMistakes}</td>
<td>${d.attendance}</td>
</tr>
""")
                }
            }

            writer.write("""
</table>

<div class="dua">${institute.officialDua}</div>

<table style="width:100%; margin-top: 40px;">
<tr>
<td style="text-align: right; width: 50%;"><b>دستخط استاد محترم:</b> ${teacher.name}<br>${teacher.designation}</td>
<td style="text-align: left; width: 50%;"><b>تصدیقی مہر:</b> ${institute.name}</td>
</tr>
</table>

</body>
</html>
""")
            writer.flush()
            writer.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generates a clean A4 PDF Report
     */
    fun generateMonthlyPdf(
        context: Context,
        student: StudentEntity,
        monthly: MonthlyReportEntity,
        institute: InstituteEntity,
        teacher: TeacherEntity,
        dailyReports: List<DailyReportEntity> = emptyList()
    ): File? {
        return try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val bgPaint = Paint().apply { color = Color.rgb(29, 24, 56) } // #1D1838
            val cardPaint = Paint().apply { color = Color.rgb(40, 33, 73) } // #282149
            val rowAltPaint = Paint().apply { color = Color.rgb(48, 41, 80) } // #302950
            val borderPaint = Paint().apply {
                color = Color.rgb(244, 166, 42) // Amber
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
            }
            val gridLinePaint = Paint().apply {
                color = Color.rgb(81, 72, 111) // #51486F
                style = Paint.Style.STROKE
                strokeWidth = 0.8f
            }
            val whiteText = Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            val amberText = Paint().apply {
                color = Color.rgb(244, 166, 42)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            val secondaryText = Paint().apply {
                color = Color.rgb(209, 205, 221)
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            // Draw Background
            canvas.drawRect(0f, 0f, 595f, 842f, bgPaint)

            // Outer border
            canvas.drawRoundRect(RectF(18f, 18f, 577f, 824f), 12f, 12f, borderPaint)

            // Header Banner
            whiteText.textSize = 20f
            whiteText.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText(institute.name, 297f, 50f, whiteText)

            amberText.textSize = 13f
            amberText.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("ماہانہ حفظ رپورٹ • ${monthly.monthNameUrdu}", 297f, 72f, amberText)

            // Student Summary Card
            val studentCard = RectF(32f, 85f, 563f, 140f)
            canvas.drawRoundRect(studentCard, 8f, 8f, cardPaint)

            whiteText.textSize = 13f
            whiteText.textAlign = Paint.Align.RIGHT
            canvas.drawText("طالب علم: ${student.name} | ولدیت: ${student.fatherName} | رول: ${student.rollNumber.ifEmpty { "104" }}", 545f, 110f, whiteText)

            secondaryText.textSize = 11f
            secondaryText.textAlign = Paint.Align.RIGHT
            canvas.drawText("موجودہ پارہ: ${student.currentJuz} | حاضری: ${monthly.presentDays}/${monthly.totalCalendarDays} دن | پختگی: ${monthly.overallAccuracy}%", 545f, 130f, secondaryText)

            // 4 Key Metric Badges
            val badgeW = (531f - 30f) / 4f
            drawMiniMetric(canvas, 32f, 150f, 32f + badgeW, 195f, cardPaint, "حاضری", "${monthly.presentDays} دن", Color.rgb(244, 166, 42))
            drawMiniMetric(canvas, 32f + badgeW + 10f, 150f, 32f + badgeW * 2 + 10f, 195f, cardPaint, "سبق", "${monthly.totalSabaqLines} سطریں", Color.rgb(110, 168, 232))
            drawMiniMetric(canvas, 32f + badgeW * 2 + 20f, 150f, 32f + badgeW * 3 + 20f, 195f, cardPaint, "سبقی", "غلطیاں: ${monthly.totalSabqiMistakes}", Color.rgb(217, 120, 181))
            drawMiniMetric(canvas, 32f + badgeW * 3 + 30f, 150f, 563f, 195f, cardPaint, "منزل", "غلطیاں: ${monthly.totalManzilMistakes}", Color.rgb(85, 185, 138))

            // Daily Rows Table Section
            val tableTop = 210f
            val tableHeaderRect = RectF(32f, tableTop, 563f, tableTop + 24f)
            val headerPaint = Paint().apply { color = Color.rgb(57, 50, 91) }
            canvas.drawRect(tableHeaderRect, headerPaint)

            val colDateX = 55f
            val colSabaqX = 180f
            val colSabqiX = 350f
            val colManzilX = 490f

            val thPaint = Paint().apply {
                color = Color.rgb(244, 166, 42)
                textSize = 10f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            canvas.drawText("تاریخ", colDateX, tableTop + 16f, thPaint)
            canvas.drawText("سبق (Surah)", colSabaqX, tableTop + 16f, thPaint)
            canvas.drawText("سبقی (Mistakes)", colSabqiX, tableTop + 16f, thPaint)
            canvas.drawText("منزل (Mistakes)", colManzilX, tableTop + 16f, thPaint)

            val rowHeight = 20f
            var currentY = tableTop + 24f
            val maxRows = 23
            val displayList = dailyReports.sortedBy { it.date }.take(maxRows)

            val cellPaint = Paint().apply {
                color = Color.rgb(248, 247, 252)
                textSize = 9.5f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            displayList.forEachIndexed { index, d ->
                val rowRect = RectF(32f, currentY, 563f, currentY + rowHeight)
                canvas.drawRect(rowRect, if (index % 2 == 0) cardPaint else rowAltPaint)
                canvas.drawRect(rowRect, gridLinePaint)

                val dayNum = d.date.takeLast(2).toIntOrNull()?.toString() ?: d.date
                canvas.drawText(dayNum, colDateX, currentY + 14f, cellPaint)
                canvas.drawText("${d.sabaqSurah} (${d.sabaqLines} سطر)", colSabaqX, currentY + 14f, cellPaint)
                canvas.drawText("پارہ ${d.sabqiJuz} : ${d.sabqiMistakes}", colSabqiX, currentY + 14f, cellPaint)
                canvas.drawText("پارہ ${d.manzilJuz} : ${d.manzilMistakes}", colManzilX, currentY + 14f, cellPaint)

                currentY += rowHeight
            }

            // Footer Dua & Signature
            val footerY = 740f
            amberText.textSize = 10.5f
            amberText.textAlign = Paint.Align.CENTER
            canvas.drawText(institute.officialDua, 297f, footerY, amberText)

            val signPaint = Paint().apply {
                color = Color.rgb(209, 205, 221)
                textSize = 11f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawLine(50f, 785f, 210f, 785f, borderPaint)
            canvas.drawText("دستخط استاد: ${teacher.name}", 130f, 802f, signPaint)

            canvas.drawLine(380f, 785f, 540f, 785f, borderPaint)
            canvas.drawText("تصدیقی مہر: ${institute.name}", 460f, 802f, signPaint)

            pdfDoc.finishPage(page)

            val cleanStudentName = student.name.replace(" ", "_")
            val fileName = "${cleanStudentName}_${monthly.month}_${monthly.year}.pdf"
            val file = File(context.cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            pdfDoc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDoc.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawMiniMetric(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        bgPaint: Paint,
        title: String,
        value: String,
        color: Int
    ) {
        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect, 6f, 6f, bgPaint)

        val tPaint = Paint().apply {
            this.color = color
            textSize = 10f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(title, (left + right) / 2f, top + 18f, tPaint)

        val vPaint = Paint().apply {
            this.color = Color.WHITE
            textSize = 11f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(value, (left + right) / 2f, top + 35f, vPaint)
    }

    /**
     * Generates a 1080px wide clean image for WhatsApp sharing
     */
    fun generateMonthlyImage(
        context: Context,
        student: StudentEntity,
        monthly: MonthlyReportEntity,
        institute: InstituteEntity,
        teacher: TeacherEntity,
        dailyReports: List<DailyReportEntity> = emptyList()
    ): File? {
        return try {
            val width = 1080
            val height = 1600
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val bgPaint = Paint().apply { color = Color.rgb(20, 17, 36) } // #141124
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            val cardPaint = Paint().apply { color = Color.rgb(40, 33, 73) } // #282149
            val altRowPaint = Paint().apply { color = Color.rgb(48, 41, 80) } // #302950
            val borderPaint = Paint().apply {
                color = Color.rgb(244, 166, 42)
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }
            canvas.drawRoundRect(RectF(25f, 25f, 1055f, 1575f), 20f, 20f, borderPaint)

            val textWhite = Paint().apply {
                color = Color.WHITE
                textSize = 38f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(institute.name, 540f, 95f, textWhite)

            val textAmber = Paint().apply {
                color = Color.rgb(244, 166, 42)
                textSize = 26f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("ماہانہ حفظ رپورٹ • ${monthly.monthNameUrdu}", 540f, 140f, textAmber)

            // Student banner
            val studentBox = RectF(50f, 170f, 1030f, 280f)
            canvas.drawRoundRect(studentBox, 14f, 14f, cardPaint)

            val textStudent = Paint().apply {
                color = Color.WHITE
                textSize = 30f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText("طالب علم: ${student.name} (ولدیت: ${student.fatherName})", 990f, 220f, textStudent)

            val textSub = Paint().apply {
                color = Color.rgb(209, 205, 221)
                textSize = 22f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText("رول نمبر: ${student.rollNumber.ifEmpty { "104" }} • موجودہ پارہ: ${student.currentJuz} • پختگی: ${monthly.overallAccuracy}%", 990f, 260f, textSub)

            // 4 Mini Summary Cards
            val cardW = (980f - 30f) / 4f
            drawImageBadge(canvas, 50f, 300f, 50f + cardW, 390f, cardPaint, "حاضری", "${monthly.presentDays} دن", Color.rgb(244, 166, 42))
            drawImageBadge(canvas, 50f + cardW + 10f, 300f, 50f + cardW * 2 + 10f, 390f, cardPaint, "سبق", "${monthly.totalSabaqLines} سطر", Color.rgb(110, 168, 232))
            drawImageBadge(canvas, 50f + cardW * 2 + 20f, 300f, 50f + cardW * 3 + 20f, 390f, cardPaint, "سبقی", "${monthly.totalSabqiMistakes} غلطیاں", Color.rgb(217, 120, 181))
            drawImageBadge(canvas, 50f + cardW * 3 + 30f, 300f, 1030f, 390f, cardPaint, "منزل", "${monthly.totalManzilMistakes} غلطیاں", Color.rgb(85, 185, 138))

            // Daily Rows Table
            val tableTop = 415f
            val headerBox = RectF(50f, tableTop, 1030f, tableTop + 50f)
            val hdrPaint = Paint().apply { color = Color.rgb(57, 50, 91) }
            canvas.drawRect(headerBox, hdrPaint)

            val colDate = 100f
            val colSabaq = 330f
            val colSabqi = 660f
            val colManzil = 920f

            val thTxt = Paint().apply {
                color = Color.rgb(244, 166, 42)
                textSize = 22f
                typeface = Typeface.DEFAULT_BOLD
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("تاریخ", colDate, tableTop + 34f, thTxt)
            canvas.drawText("سبق (Surah)", colSabaq, tableTop + 34f, thTxt)
            canvas.drawText("سبقی : غلطیاں", colSabqi, tableTop + 34f, thTxt)
            canvas.drawText("منزل : غلطیاں", colManzil, tableTop + 34f, thTxt)

            var rowY = tableTop + 50f
            val rowH = 44f
            val rowTxt = Paint().apply {
                color = Color.WHITE
                textSize = 20f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }

            val tableItems = dailyReports.sortedBy { it.date }.take(20)
            tableItems.forEachIndexed { i, d ->
                val rBox = RectF(50f, rowY, 1030f, rowY + rowH)
                canvas.drawRect(rBox, if (i % 2 == 0) cardPaint else altRowPaint)

                val dayNum = d.date.takeLast(2).toIntOrNull()?.toString() ?: d.date
                canvas.drawText(dayNum, colDate, rowY + 30f, rowTxt)
                canvas.drawText("${d.sabaqSurah} (${d.sabaqLines})", colSabaq, rowY + 30f, rowTxt)
                canvas.drawText("پارہ ${d.sabqiJuz} : ${d.sabqiMistakes}", colSabqi, rowY + 30f, rowTxt)
                canvas.drawText("پارہ ${d.manzilJuz} : ${d.manzilMistakes}", colManzil, rowY + 30f, rowTxt)

                rowY += rowH
            }

            // Dua Banner
            val textDua = Paint().apply {
                color = Color.rgb(244, 166, 42)
                textSize = 22f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(institute.officialDua, 540f, 1460f, textDua)

            val textFoot = Paint().apply {
                color = Color.rgb(158, 152, 178)
                textSize = 20f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("استاد محترم: ${teacher.name} • ${institute.name}", 540f, 1515f, textFoot)

            val cleanStudentName = student.name.replace(" ", "_")
            val fileName = "${cleanStudentName}_${monthly.month}_${monthly.year}.png"
            val file = File(context.cacheDir, fileName)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun drawImageBadge(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        bgPaint: Paint,
        title: String,
        value: String,
        color: Int
    ) {
        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect, 10f, 10f, bgPaint)

        val tPaint = Paint().apply {
            this.color = color
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(title, (left + right) / 2f, top + 34f, tPaint)

        val vPaint = Paint().apply {
            this.color = Color.WHITE
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(value, (left + right) / 2f, top + 68f, vPaint)
    }

    /**
     * Share via WhatsApp or WhatsApp Business or system chooser
     * Adheres to requirement 7:
     * If user has running WhatsApp Business, share on WhatsApp Business.
     * If user has WhatsApp, share on WhatsApp.
     */
    fun shareReport(
        context: Context,
        messageText: String,
        file: File?,
        phone: String = ""
    ) {
        val targetPackage = getTargetWhatsAppPackage(context)

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when {
                    file == null -> "text/plain"
                    file.name.endsWith(".pdf") -> "application/pdf"
                    file.name.endsWith(".doc") -> "application/msword"
                    else -> "image/png"
                }
                putExtra(Intent.EXTRA_TEXT, messageText)
                if (file != null) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                if (targetPackage != null) {
                    setPackage(targetPackage)
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to system chooser
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = when {
                    file == null -> "text/plain"
                    file.name.endsWith(".pdf") -> "application/pdf"
                    file.name.endsWith(".doc") -> "application/msword"
                    else -> "image/png"
                }
                putExtra(Intent.EXTRA_TEXT, messageText)
                if (file != null) {
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "رپورٹ شیئر کریں").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
