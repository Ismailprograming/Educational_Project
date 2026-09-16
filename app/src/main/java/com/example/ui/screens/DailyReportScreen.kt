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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
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
import com.example.util.QuranData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyReportScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allActiveStudents.collectAsState()
    val selectedStudentId by viewModel.dailyEntryStudentId.collectAsState()
    val existingReport by viewModel.existingDailyReport.collectAsState()
    val dateDisplay by viewModel.dateDisplayUrdu.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    val sabaqSurah by viewModel.formSabaqSurah.collectAsState()
    val sabaqFromAyah by viewModel.formSabaqFromAyah.collectAsState()
    val sabaqToAyah by viewModel.formSabaqToAyah.collectAsState()
    val sabaqLines by viewModel.formSabaqLines.collectAsState()

    val sabqiJuz by viewModel.formSabqiJuz.collectAsState()
    val sabqiMistakes by viewModel.formSabqiMistakes.collectAsState()

    val manzilJuz by viewModel.formManzilJuz.collectAsState()
    val manzilMistakes by viewModel.formManzilMistakes.collectAsState()

    val attendance by viewModel.formAttendance.collectAsState()
    val performance by viewModel.formPerformance.collectAsState()
    val remarks by viewModel.formRemarks.collectAsState()

    var surahMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date & Status Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "یومیہ اندراج • $dateDisplay",
                    color = AmberAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedDate,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Student Horizontal Selector
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
                                .clickable { viewModel.selectDailyEntryStudent(student.id) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("select_daily_student_${student.id}")
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

        // Existing Record Info
        if (existingReport != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface2)
                        .border(1.dp, AmberAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اس طالب علم کی آج کی رپورٹ درج شدہ ہے (ترمیم کے بعد محفوظ کریں)",
                            color = AmberAccent,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 1. سبق (Sabaq)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SabaqBlue)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سبق",
                            color = SabaqBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Surah dropdown
                    ExposedDropdownMenuBox(
                        expanded = surahMenuExpanded,
                        onExpandedChange = { surahMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = sabaqSurah,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("سورۃ مبارکہ", color = TextMuted, fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = surahMenuExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("sabaq_surah_picker"),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = SabaqBlue,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedContainerColor = Surface2,
                                unfocusedContainerColor = Surface2
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = surahMenuExpanded,
                            onDismissRequest = { surahMenuExpanded = false },
                            modifier = Modifier.background(Surface2)
                        ) {
                            QuranData.surahs.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.number}. ${s.nameUrdu}", color = TextPrimary) },
                                    onClick = {
                                        viewModel.formSabaqSurah.value = s.nameUrdu
                                        surahMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Ayah From, Ayah To, Lines
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CounterField(
                            label = "آیت سے",
                            value = sabaqFromAyah,
                            onValueChange = { viewModel.formSabaqFromAyah.value = it },
                            accentColor = SabaqBlue,
                            minValue = 0,
                            modifier = Modifier.weight(1f)
                        )
                        CounterField(
                            label = "آیت تک",
                            value = sabaqToAyah,
                            onValueChange = { viewModel.formSabaqToAyah.value = it },
                            accentColor = SabaqBlue,
                            minValue = 0,
                            modifier = Modifier.weight(1f)
                        )
                        CounterField(
                            label = "سطریں",
                            value = sabaqLines,
                            onValueChange = { viewModel.formSabaqLines.value = it },
                            accentColor = SabaqBlue,
                            minValue = 0,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. سبقی (Sabqi) - Atkan removed as explicitly requested!
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SabqiPink)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سبقی",
                            color = SabqiPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CounterField(
                            label = "پارہ نمبر (1 تا 30)",
                            value = sabqiJuz,
                            onValueChange = { viewModel.formSabqiJuz.value = it.coerceIn(1, 30) },
                            accentColor = SabqiPink,
                            minValue = 1,
                            maxValue = 30,
                            modifier = Modifier.weight(1f)
                        )
                        CounterField(
                            label = "غلطیاں",
                            value = sabqiMistakes,
                            onValueChange = { viewModel.formSabqiMistakes.value = maxOf(0, it) },
                            accentColor = MistakesRed,
                            minValue = 0,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. منزل (Manzil)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "منزل",
                            color = SuccessGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CounterField(
                            label = "پارہ نمبر (1 تا 30)",
                            value = manzilJuz,
                            onValueChange = { viewModel.formManzilJuz.value = it.coerceIn(1, 30) },
                            accentColor = SuccessGreen,
                            minValue = 1,
                            maxValue = 30,
                            modifier = Modifier.weight(1f)
                        )
                        CounterField(
                            label = "غلطیاں",
                            value = manzilMistakes,
                            onValueChange = { viewModel.formManzilMistakes.value = maxOf(0, it) },
                            accentColor = MistakesRed,
                            minValue = 0,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. حاضری
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
                        text = "حاضری",
                        color = AmberAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val attendanceOptions = listOf("حاضر" to SuccessGreen, "غیر حاضر" to MistakesRed, "چھٹی" to AmberAccent)
                        attendanceOptions.forEach { (opt, color) ->
                            val isSelected = attendance == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) color.copy(alpha = 0.22f) else Surface2)
                                    .border(1.dp, if (isSelected) color else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.formAttendance.value = opt }
                                    .padding(vertical = 8.dp)
                                    .testTag("attendance_opt_$opt"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    color = if (isSelected) color else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. کارکردگی
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
                        text = "کارکردگی",
                        color = ProgressViolet,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val perfOptions = listOf("بہترین", "اچھی", "مناسب", "مزید محنت درکار")
                        perfOptions.forEach { opt ->
                            val isSelected = performance == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AmberAccent.copy(alpha = 0.2f) else Surface2)
                                    .border(1.dp, if (isSelected) AmberAccent else SurfaceBorder, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.formPerformance.value = opt }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    color = if (isSelected) AmberAccent else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Save and Next
                Button(
                    onClick = { viewModel.saveAndNextStudent() },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("save_and_next_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberAccent,
                        contentColor = BackgroundDark
                    )
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "محفوظ اور اگلا", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Simple Save
                Button(
                    onClick = { viewModel.saveCurrentDailyReport() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("save_daily_report_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Surface2,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "محفوظ", fontSize = 12.sp)
                }

                // WhatsApp Share
                Button(
                    onClick = { viewModel.shareDailyWhatsApp() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("share_daily_whatsapp_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Surface2,
                        contentColor = SuccessGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "واٹس ایپ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CounterField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    accentColor: Color = AmberAccent,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 999
) {
    var textValue by remember(value) {
        mutableStateOf(if (value == 0 && minValue > 0) "" else value.toString())
    }

    Column(modifier = modifier) {
        Text(text = label, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Surface2)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Surface3)
                    .clickable {
                        val next = maxOf(minValue, value - 1)
                        textValue = next.toString()
                        onValueChange(next)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "-", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            // Direct editable number entry: Teacher can directly enter/type any verse/juz number without pressing '+'!
            BasicTextField(
                value = textValue,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    textValue = filtered
                    if (filtered.isNotEmpty()) {
                        val num = filtered.toIntOrNull() ?: minValue
                        onValueChange(num.coerceIn(minValue, maxValue))
                    } else {
                        onValueChange(minValue)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                cursorBrush = SolidColor(accentColor)
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Surface3)
                    .clickable {
                        val next = minOf(maxValue, value + 1)
                        textValue = next.toString()
                        onValueChange(next)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
