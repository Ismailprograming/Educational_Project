package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StudentEntity
import com.example.ui.components.StatCard
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: MadrasaViewModel,
    onNavigateToDailyEntry: (studentId: Long) -> Unit,
    onNavigateToStudentDetail: (studentId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalStudents by viewModel.activeStudentsCount.collectAsState()
    val presentCount by viewModel.todayPresentCount.collectAsState()
    val absentCount by viewModel.todayAbsentCount.collectAsState()
    val leaveCount by viewModel.todayLeaveCount.collectAsState()
    val sabaqLines by viewModel.todaySabaqLines.collectAsState()
    val mistakes by viewModel.todayMistakes.collectAsState()
    val reportsCount by viewModel.todayReportsCount.collectAsState()

    val excellentCount by viewModel.todayExcellentCount.collectAsState()
    val goodCount by viewModel.todayGoodCount.collectAsState()
    val needsRevisionCount by viewModel.todayNeedsRevisionCount.collectAsState()

    val dateDisplay by viewModel.dateDisplayUrdu.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val liveDate by viewModel.liveDateDisplay.collectAsState()
    val liveTime by viewModel.liveTimeDisplay.collectAsState()
    val institute by viewModel.institute.collectAsState()
    val students by viewModel.allActiveStudents.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 0. Institute Header Card (Prominent Institute Name on Dashboard)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface1)
                    .border(1.dp, AmberAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = institute?.name?.ifBlank { "جامعہ اسلامیہ لتحفیظ القرآن" } ?: "جامعہ اسلامیہ لتحفیظ القرآن",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = institute?.campus?.ifBlank { "شعبہ تجوید و تحفیظ القرآن الکریم" } ?: "شعبہ تجوید و تحفیظ القرآن الکریم",
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "خودکار تاریخ و وقت: $liveDate • $liveTime",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Surface2)
                            .border(1.5.dp, AmberAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.madrasa_universal_logo_1789540100683),
                            contentDescription = "Institute Logo",
                            modifier = Modifier.size(42.dp).clip(CircleShape)
                        )
                    }
                }
            }
        }

        // 1. Date Switcher Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.shiftDate(-1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface2)
                            .testTag("prev_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "پچھلا دن",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.resetToAutoToday() }
                    ) {
                        Text(
                            text = dateDisplay,
                            color = AmberAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "منتخب تاریخ: $selectedDate",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Surface3)
                                    .clickable { viewModel.resetToAutoToday() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "آج (خودکار)",
                                    color = SuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { viewModel.shiftDate(1) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface2)
                            .testTag("next_date_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "اگلا دن",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2. Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (students.isNotEmpty()) {
                            onNavigateToDailyEntry(students.first().id)
                        }
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp)
                        .testTag("quick_daily_entry_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberAccent,
                        contentColor = BackgroundDark
                    )
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("روزانہ نیا اندراج", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.markAllPresent() },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("mark_all_present_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Surface2,
                        contentColor = SuccessGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تمام حاضر", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // 3. Dynamic Metric Cards (2x2 Grid)
        item {
            Text(
                text = "آج کے بنیادی اعداد و شمار (حفظ و حاضری)",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "کل طلبہ و حاضری",
                    value = "$presentCount / $totalStudents",
                    subtitle = "حاضر طلبہ کی شرح: ${if (totalStudents > 0) (presentCount * 100 / totalStudents) else 0}%",
                    accentColor = AmberAccent,
                    icon = Icons.Default.Groups,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "غیر حاضر و رخصت",
                    value = "$absentCount",
                    subtitle = "رخصت پر: $leaveCount طلبہ",
                    accentColor = MistakesRed,
                    icon = Icons.Default.EventBusy,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "آج پڑھا گیا نیا سبق",
                    value = "$sabaqLines سطریں",
                    subtitle = "اندراج شدہ اسباق: $reportsCount",
                    accentColor = SabaqBlue,
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "مجموعی اغلاط (سبقی و منزل)",
                    value = "$mistakes",
                    subtitle = "اوسط غلطیاں فی طالب علم",
                    accentColor = SabqiPink,
                    icon = Icons.Default.Error,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Performance Quality Breakdown Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "کارکردگی کا تناسب (معیارِ تحفیظ)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PerformancePill(label = "بہترین (ممتاز)", count = excellentCount, color = SuccessGreen)
                        PerformancePill(label = "اچھی (جید)", count = goodCount, color = SabaqBlue)
                        PerformancePill(label = "توجہ طلب", count = needsRevisionCount, color = MistakesRed)
                    }
                }
            }
        }

        // 5. Recent Students Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "طلبہ کی فہرست و روزانہ کیفیت",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "کل: ${students.size}",
                    color = AmberAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // 6. Student Rows
        items(students) { student ->
            StudentDashboardRow(
                student = student,
                onRowClick = { onNavigateToStudentDetail(student.id) },
                onAddReportClick = { onNavigateToDailyEntry(student.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PerformancePill(
    label: String,
    count: Int,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Surface2)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "$count", color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = label, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun StudentDashboardRow(
    student: StudentEntity,
    onRowClick: () -> Unit,
    onAddReportClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Surface1)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .clickable { onRowClick() }
            .padding(12.dp)
            .testTag("student_dashboard_item_${student.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Avatar & Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Surface2)
                        .border(1.dp, AmberAccent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.name.take(2),
                        color = AmberAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = student.name,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "ولدیت: ${student.fatherName} • رول: ${student.rollNumber.ifEmpty { "104" }}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "پارہ: ${student.currentJuz}",
                            color = ProgressViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• ${student.currentSurah}",
                            color = SabaqBlue,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Quick Record Button
            IconButton(
                onClick = onAddReportClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface2)
                    .border(1.dp, AmberAccent.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .testTag("record_entry_student_${student.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = "اندراج کریں",
                    tint = AmberAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
