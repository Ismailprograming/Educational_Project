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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.InstituteEntity
import com.example.data.model.TeacherEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MistakesRed
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
fun AdvanceScreen(
    viewModel: MadrasaViewModel,
    modifier: Modifier = Modifier
) {
    val institute by viewModel.institute.collectAsState()
    val teacher by viewModel.teacher.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val syncState by viewModel.syncState.collectAsState()

    var instName by remember(institute) { mutableStateOf(institute?.name ?: "جامعہ اسلامیہ لتحفیظ القرآن") }
    var instCampus by remember(institute) { mutableStateOf(institute?.campus ?: "مرکزی کیمپس، سیکٹر جی-۹، اسلام آباد") }
    var instPhone by remember(institute) { mutableStateOf(institute?.phone ?: "+92 51 2233445") }

    var teacherName by remember(teacher) { mutableStateOf(teacher?.name ?: "قاری عبدالرحمن") }
    var teacherDesignation by remember(teacher) { mutableStateOf(teacher?.designation ?: "صدر مدرس - شعبہ تجوید و تحفیظ القرآن الکریم") }
    var teacherPhone by remember(teacher) { mutableStateOf(teacher?.phone ?: "+92 300 1234567") }

    var autoMonthlyReport by remember(settings) { mutableStateOf(settings?.autoMonthlyReport ?: true) }
    var autoAbsentAlert by remember(settings) { mutableStateOf(settings?.autoAbsentAlert ?: true) }
    var reportFormat by remember(settings) { mutableStateOf(settings?.dailyReportFormat ?: "PDF") }

    var showResetDialog by remember { mutableStateOf(false) }
    val currentMonthVal by viewModel.currentMonth.collectAsState()
    val currentYearVal by viewModel.currentYear.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: Offline Sync & Cloud Status
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Surface2),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "آف لائن ڈیٹا اور کلاؤڈ سنک", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = syncState, color = SuccessGreen, fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { viewModel.triggerCloudSync() },
                        modifier = Modifier.height(36.dp).testTag("trigger_sync_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberAccent,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Text(text = "ابھی سنک کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section: Monthly Session & App Reset Controls (When profiles exist)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface1)
                    .border(1.dp, AmberAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AmberAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = "ماہانہ سیشن اور ایپ کنٹرولز", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                val mName = if (currentMonthVal in 1..12) viewModel.monthNamesUrdu[currentMonthVal - 1] else "ماہ $currentMonthVal"
                                Text(
                                    text = "موجودہ فعال مہینہ: $mName ${currentYearVal}ء",
                                    color = AmberAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Text(
                        text = "پروفائل بننے کے بعد آپ نئے مہینے کا سیشن شروع کر سکتے ہیں یا مکمل ایپ ری سیٹ کر کے نیا سیٹ اپ کر سکتے ہیں۔",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Change to Next Month Button
                        Button(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("advance_next_month_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberAccent,
                                contentColor = BackgroundDark
                            )
                        ) {
                            Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "اگلا مہینہ تبدیل کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Reset App Button
                        Button(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("reset_app_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Surface2,
                                contentColor = MistakesRed
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MistakesRed.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "ایپ ری سیٹ کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section 2: Report Automation & WhatsApp Defaults
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "رپورٹ و واٹس ایپ ترتیبات", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto monthly report switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "ماہانہ رپورٹ خودکار تیاری", color = TextSecondary, fontSize = 13.sp)
                            Text(text = "ہر ماہ کی ۲۸ تاریخ کو خودکار تشکیل", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = autoMonthlyReport,
                            onCheckedChange = {
                                autoMonthlyReport = it
                                settings?.let { s -> viewModel.updateSettings(s.copy(autoMonthlyReport = it)) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = AmberAccent)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Auto absent alert switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "غیر حاضری کا خودکار الرٹ", color = TextSecondary, fontSize = 13.sp)
                            Text(text = "غیر حاضر نشان زد ہوتے ہی اطلاع تیار کریں", color = TextMuted, fontSize = 10.sp)
                        }
                        Switch(
                            checked = autoAbsentAlert,
                            onCheckedChange = {
                                autoAbsentAlert = it
                                settings?.let { s -> viewModel.updateSettings(s.copy(autoAbsentAlert = it)) }
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = BackgroundDark, checkedTrackColor = AmberAccent)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Preferred export format
                    Text(text = "بنیادی ایکسپورٹ فارمیٹ:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val pdfSelected = reportFormat == "PDF"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (pdfSelected) AmberAccent.copy(alpha = 0.2f) else Surface2)
                                .border(1.dp, if (pdfSelected) AmberAccent else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    reportFormat = "PDF"
                                    settings?.let { s -> viewModel.updateSettings(s.copy(dailyReportFormat = "PDF")) }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "پی ڈی ایف (PDF)", color = if (pdfSelected) AmberAccent else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        val imgSelected = reportFormat == "IMAGE"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (imgSelected) AmberAccent.copy(alpha = 0.2f) else Surface2)
                                .border(1.dp, if (imgSelected) AmberAccent else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    reportFormat = "IMAGE"
                                    settings?.let { s -> viewModel.updateSettings(s.copy(dailyReportFormat = "IMAGE")) }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "تصویر کارڈ (PNG)", color = if (imgSelected) AmberAccent else TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Section 3: Teacher Profile Edit
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
                    Text(text = "استاد محترم کا پروفائل", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("نام استاد محترم", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("teacher_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = Surface2,
                            unfocusedContainerColor = Surface2
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = teacherDesignation,
                        onValueChange = { teacherDesignation = it },
                        label = { Text("عہدہ و شعبہ", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = Surface2,
                            unfocusedContainerColor = Surface2
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            teacher?.let {
                                viewModel.updateTeacher(it.copy(name = teacherName, designation = teacherDesignation))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_teacher_profile_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = AmberAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                    ) {
                        Text(text = "استاد کا پروفائل محفوظ کریں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 4: Institute Profile
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
                    Text(text = "ادارے / مدرسہ کی معلومات", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = instName,
                        onValueChange = { instName = it },
                        label = { Text("نام ادارہ", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("institute_name_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = Surface2,
                            unfocusedContainerColor = Surface2
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = instCampus,
                        onValueChange = { instCampus = it },
                        label = { Text("کیمپس / پتہ", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = AmberAccent,
                            unfocusedBorderColor = SurfaceBorder,
                            focusedContainerColor = Surface2,
                            unfocusedContainerColor = Surface2
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            institute?.let {
                                viewModel.updateInstitute(it.copy(name = instName, campus = instCampus))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("save_institute_profile_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = AmberAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                    ) {
                        Text(text = "ادارے کی معلومات محفوظ کریں", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 5: Logout Action
        item {
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("teacher_logout_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Surface2,
                    contentColor = MistakesRed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MistakesRed.copy(alpha = 0.5f))
            ) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "لاگ آؤٹ (اسٹاف لاگ ان اسکرین پر جائیں)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null, tint = MistakesRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ایپ ری سیٹ کریں؟", color = MistakesRed, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Text(
                    text = "کیا آپ واقعی تمام ریکارڈز ری سیٹ کرنا چاہتے ہیں؟\n\nاس سے تمام اساتذہ، ادارہ، طلبہ اور روزانہ و ماہانہ حاضری و اسباق کا ڈیٹا مکمل صاف ہو جائے گا اور ایپ دوبارہ ابتدائی اسکرین (Start from Zero) سے شروع ہوگی۔",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetApp()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MistakesRed, contentColor = Color.White),
                    modifier = Modifier.testTag("confirm_reset_button")
                ) {
                    Text(text = "ہاں، مکمل ری سیٹ کریں", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showResetDialog = false }
                ) {
                    Text(text = "منسوخ", color = TextSecondary)
                }
            },
            containerColor = Surface1,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
