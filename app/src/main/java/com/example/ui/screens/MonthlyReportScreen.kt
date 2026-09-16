package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MistakesRed
import com.example.ui.theme.SabaqBlue
import com.example.ui.theme.SabqiPink
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.Surface1
import com.example.ui.theme.Surface2
import com.example.ui.theme.Surface3
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun MonthlyReportScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allActiveStudents.collectAsState()
    val selectedStudentId by viewModel.monthlyStudentId.collectAsState()
    val month by viewModel.currentMonth.collectAsState()
    val year by viewModel.currentYear.collectAsState()
    val report by viewModel.monthlyReport.collectAsState()
    val dailyBreakdown by viewModel.monthlyDailyBreakdown.collectAsState()

    LaunchedEffect(selectedStudentId, month, year) {
        if (selectedStudentId > 0) {
            viewModel.loadMonthlyReport(selectedStudentId, month, year)
        }
    }

    val currentStudent = students.find { it.id == selectedStudentId } ?: students.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Month Selector Header with Next Month & Month Controls
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            val currentUrduMonth = if (month in 1..12) viewModel.monthNamesUrdu[month - 1] else "ماہ $month"
                            Column {
                                Text(
                                    text = "ماہانہ رپورٹ • $currentUrduMonth $year",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(text = "تعلیمی سیشن: ${year}ء", color = TextMuted, fontSize = 10.sp)
                            }
                        }

                        // Month Switch Buttons: Previous Month and Next Month
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.previousMonth() },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Surface2)
                                    .testTag("prev_month_btn")
                            ) {
                                Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "پچھلا مہینہ", tint = TextPrimary, modifier = Modifier.size(18.dp))
                            }

                            Button(
                                onClick = { viewModel.nextMonth() },
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("next_month_btn"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AmberAccent,
                                    contentColor = BackgroundDark
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                            ) {
                                Text(text = "اگلا مہینہ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Quick Month Selector Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(viewModel.monthNamesUrdu) { index, mName ->
                            val mNum = index + 1
                            val isSelected = mNum == month
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AmberAccent else Surface2)
                                    .border(1.dp, if (isSelected) AmberAccent else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setMonthAndYear(mNum, year) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .testTag("month_chip_$mNum")
                            ) {
                                Text(
                                    text = mName,
                                    color = if (isSelected) BackgroundDark else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Student Selector Row
        item {
            if (students.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface1)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "کوئی طالب علم موجود نہیں ہے۔", color = TextMuted, fontSize = 12.sp)
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(students) { student ->
                        val isSelected = student.id == selectedStudentId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AmberAccent.copy(alpha = 0.2f) else Surface1)
                                .border(1.dp, if (isSelected) AmberAccent else SurfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { viewModel.loadMonthlyReport(student.id, month, year) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("select_monthly_student_${student.id}")
                        ) {
                            Text(
                                text = student.name,
                                color = if (isSelected) AmberAccent else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Student Info Banner
        if (currentStudent != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Surface1)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = currentStudent.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "ولدیت: ${currentStudent.fatherName} • رول: ${currentStudent.rollNumber.ifEmpty { "104" }}", color = TextMuted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "پارہ: ${currentStudent.currentJuz}", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "پختگی: ${report?.overallAccuracy ?: 90}%", color = SuccessGreen, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Attendance Box (Present, Absent, Leave) - "Attendance Box is good"
        item {
            val r = report
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "ماہانہ حاضری ریکارڈ",
                        color = AmberAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AttendanceItem(
                            label = "حاضر ایام",
                            value = "${r?.presentDays ?: 0} دن",
                            color = SuccessGreen,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceItem(
                            label = "غیر حاضر",
                            value = "${r?.absentDays ?: 0} دن",
                            color = MistakesRed,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceItem(
                            label = "رخصت",
                            value = "${r?.leaveDays ?: 0} دن",
                            color = AmberAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Export File Formats (Doc, PDF, Image) + WhatsApp Dispatch
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "رپورٹ ایکسپورٹ اور شیئرنگ (Form Options)",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Doc Export
                        Button(
                            onClick = { viewModel.exportMonthlyReport("DOC") },
                            modifier = Modifier.weight(1f).height(40.dp).testTag("export_doc_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = SabaqBlue),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SabaqBlue.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Doc", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // PDF Export
                        Button(
                            onClick = { viewModel.exportMonthlyReport("PDF") },
                            modifier = Modifier.weight(1f).height(40.dp).testTag("export_pdf_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = AmberAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Image Export
                        Button(
                            onClick = { viewModel.exportMonthlyReport("IMAGE") },
                            modifier = Modifier.weight(1f).height(40.dp).testTag("export_image_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = SabqiPink),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SabqiPink.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Image", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // WhatsApp Direct Share (Conditional: WhatsApp Business / Standard WhatsApp)
                    Button(
                        onClick = { viewModel.exportMonthlyReport("PDF") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("send_whatsapp_monthly_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "واٹس ایپ پر ماہانہ رپورٹ بھیجیں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 10. Table Section:
        // | Date | Sabaq (Surah to Surah) | Sabqi (Mistakes) | Manzil (Mistakes) |
        item {
            Text(
                text = "یومیہ کارکردگی چارٹ (سبق، سبقی، منزل اغلاط)",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Table Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
            ) {
                Column {
                    // Table Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Surface3)
                            .padding(vertical = 8.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date",
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(44.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "سبق (Surah)",
                            color = SabaqBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.3f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "سبقی (Mistakes)",
                            color = SabqiPink,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "منزل (Mistakes)",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (dailyBreakdown.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "اس ماہ کا کوئی یومیہ اندراج موجود نہیں ہے۔",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        dailyBreakdown.sortedBy { it.date }.forEachIndexed { index, item ->
                            val isAlt = index % 2 == 1
                            val dayNum = item.date.takeLast(2).toIntOrNull()?.toString() ?: item.date

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isAlt) Surface2 else Surface1)
                                    .border(0.5.dp, SurfaceBorder.copy(alpha = 0.5f))
                                    .padding(vertical = 7.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Date column
                                Text(
                                    text = dayNum,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.width(44.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )

                                // Sabaq column
                                Text(
                                    text = "${item.sabaqSurah} (${item.sabaqLines} سطر)",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    modifier = Modifier.weight(1.3f),
                                    textAlign = TextAlign.Center
                                )

                                // Sabqi column (Mistakes)
                                Text(
                                    text = "پارہ ${item.sabqiJuz} : ${item.sabqiMistakes}",
                                    color = if (item.sabqiMistakes > 0) MistakesRed else SuccessGreen,
                                    fontSize = 10.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (item.sabqiMistakes > 0) FontWeight.Bold else FontWeight.Normal
                                )

                                // Manzil column (Mistakes)
                                Text(
                                    text = "پارہ ${item.manzilJuz} : ${item.manzilMistakes}",
                                    color = if (item.manzilMistakes > 0) MistakesRed else SuccessGreen,
                                    fontSize = 10.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontWeight = if (item.manzilMistakes > 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AttendanceItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Surface2)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = TextMuted, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
