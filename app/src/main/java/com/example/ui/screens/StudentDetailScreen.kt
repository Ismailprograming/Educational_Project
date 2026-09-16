package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyReportEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.AddStudentDialog
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MistakesRed
import com.example.ui.theme.ProgressViolet
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
fun StudentDetailScreen(
    studentId: Long,
    viewModel: MadrasaViewModel,
    onBack: () -> Unit,
    onNavigateToDailyEntry: (studentId: Long) -> Unit,
    onNavigateToMonthly: (studentId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allActiveStudents.collectAsState()
    val student = students.find { it.id == studentId }
    val historyReports by viewModel.getDailyReportsForStudent(studentId).collectAsState()

    var isEditDialogOpen by remember { mutableStateOf(false) }

    if (student == null) {
        Box(modifier = modifier.fillMaxSize().background(BackgroundDark), contentAlignment = Alignment.Center) {
            Text(text = "طالب علم دستیاب نہیں ہے", color = TextPrimary)
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface2)
                        .testTag("detail_back_button")
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "واپس", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }

                Text(
                    text = "پروفائل: ${student.name}",
                    color = AmberAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = { isEditDialogOpen = true },
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Surface2)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "ترمیم", tint = AmberAccent, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = {
                            viewModel.deleteStudent(student.id)
                            onBack()
                        },
                        modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Surface2)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف کریں", tint = MistakesRed, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Student Profile Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Surface2)
                                    .border(2.dp, AmberAccent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(2),
                                    color = AmberAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = student.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "ولدیت: ${student.fatherName}", color = TextSecondary, fontSize = 13.sp)
                                Text(text = "رول نمبر: ${student.rollNumber.ifEmpty { "104" }} • ${student.halqa}", color = TextMuted, fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Surface2)
                                .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "پارہ ${student.currentJuz}", color = AmberAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Overview Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Surface2)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "ابتدائی پارہ", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "پارہ ${student.startingJuz}", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "موجودہ پارہ", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "پارہ ${student.currentJuz}", color = ProgressViolet, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "آخری پڑھا سبق", color = TextMuted, fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = student.currentSurah, color = SabaqBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToDailyEntry(student.id) },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent, contentColor = BackgroundDark)
                        ) {
                            Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "روزانہ اندراج", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onNavigateToMonthly(student.id) },
                            modifier = Modifier.weight(1f).height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = SabaqBlue),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SabaqBlue.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ماہانہ رپورٹ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // History Daily Reports
        item {
            Text(
                text = "ماضی کے روزانہ اسباق و حاضری ریکارڈ (${historyReports.size})",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(historyReports) { report ->
            HistoryReportCard(report = report)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (isEditDialogOpen) {
        AddStudentDialog(
            initialStudent = student,
            onDismiss = { isEditDialogOpen = false },
            onSave = { name, fatherName, whatsapp, startingJuz, halqa, rollNo ->
                viewModel.updateStudent(
                    student.copy(
                        name = name,
                        fatherName = fatherName,
                        whatsappNumber = whatsapp,
                        startingJuz = startingJuz,
                        halqa = halqa,
                        rollNumber = rollNo,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
        )
    }
}

@Composable
fun HistoryReportCard(report: DailyReportEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "تاریخ: ${report.date}", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                val attColor = when (report.attendance) {
                    "حاضر" -> SuccessGreen
                    "غیر حاضر" -> MistakesRed
                    else -> AmberAccent
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(attColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(text = report.attendance, color = attColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "سبق: ${report.sabaqSurah} (${report.sabaqLines} سطریں)", color = SabaqBlue, fontSize = 11.sp)
                Text(text = "سبقی: پارہ ${report.sabqiJuz} (اغلاط: ${report.sabqiMistakes})", color = SabqiPink, fontSize = 11.sp)
                Text(text = "منزل: پارہ ${report.manzilJuz}", color = SuccessGreen, fontSize = 11.sp)
            }

            if (report.remarks.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "استاد کی رائے: ${report.remarks}", color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}
