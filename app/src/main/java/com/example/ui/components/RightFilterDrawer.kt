package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.StudentEntity
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RightFilterDrawer(
    students: List<StudentEntity>,
    onClose: () -> Unit,
    onApplyFilter: (studentId: Long?, category: String?, attendance: String?, performance: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStudentId by remember { mutableStateOf<Long?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedAttendance by remember { mutableStateOf<String?>(null) }
    var selectedPerformance by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(Surface1)
            .border(width = 1.dp, color = SurfaceBorder)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Drawer Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تلاش و فلٹر دراز",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Surface2)
                    .testTag("close_filter_drawer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "بند کریں",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Select Student
        Text(
            text = "طالب علم منتخب کریں",
            color = AmberAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipItem(
                label = "تمام طلبہ",
                isSelected = selectedStudentId == null,
                onClick = { selectedStudentId = null }
            )
            students.take(6).forEach { student ->
                FilterChipItem(
                    label = student.name,
                    isSelected = selectedStudentId == student.id,
                    onClick = { selectedStudentId = student.id }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Category Filter (Sabaq, Sabqi, Manzil, Mistakes)
        Text(
            text = "زمرہ جات (قرآنی اسباق)",
            color = AmberAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChipItem(
                label = "سبق",
                isSelected = selectedCategory == "سبق",
                accentColor = SabaqBlue,
                onClick = { selectedCategory = if (selectedCategory == "سبق") null else "سبق" }
            )
            FilterChipItem(
                label = "سبقی",
                isSelected = selectedCategory == "سبقی",
                accentColor = SabqiPink,
                onClick = { selectedCategory = if (selectedCategory == "سبقی") null else "سبقی" }
            )
            FilterChipItem(
                label = "منزل",
                isSelected = selectedCategory == "منزل",
                accentColor = SuccessGreen,
                onClick = { selectedCategory = if (selectedCategory == "منزل") null else "منزل" }
            )
            FilterChipItem(
                label = "اغلاط",
                isSelected = selectedCategory == "اغلاط",
                accentColor = MistakesRed,
                onClick = { selectedCategory = if (selectedCategory == "اغلاط") null else "اغلاط" }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Attendance Filter (Present, Absent, Leave)
        Text(
            text = "حاضری کی صورتحال",
            color = AmberAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChipItem(
                label = "حاضر",
                isSelected = selectedAttendance == "حاضر",
                accentColor = SuccessGreen,
                onClick = { selectedAttendance = if (selectedAttendance == "حاضر") null else "حاضر" }
            )
            FilterChipItem(
                label = "غیر حاضر",
                isSelected = selectedAttendance == "غیر حاضر",
                accentColor = MistakesRed,
                onClick = { selectedAttendance = if (selectedAttendance == "غیر حاضر") null else "غیر حاضر" }
            )
            FilterChipItem(
                label = "چھٹی",
                isSelected = selectedAttendance == "چھٹی",
                accentColor = AmberAccent,
                onClick = { selectedAttendance = if (selectedAttendance == "چھٹی") null else "چھٹی" }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Performance Filter (Excellent, Good, Needs Revision)
        Text(
            text = "کارکردگی کا درجہ",
            color = AmberAccent,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChipItem(
                label = "بہترین (ممتاز)",
                isSelected = selectedPerformance == "بہترین",
                accentColor = SuccessGreen,
                onClick = { selectedPerformance = if (selectedPerformance == "بہترین") null else "بہترین" }
            )
            FilterChipItem(
                label = "اچھی (جید)",
                isSelected = selectedPerformance == "اچھی",
                accentColor = SabaqBlue,
                onClick = { selectedPerformance = if (selectedPerformance == "اچھی") null else "اچھی" }
            )
            FilterChipItem(
                label = "مزید محنت درکار (توجہ طلب)",
                isSelected = selectedPerformance == "مزید محنت درکار",
                accentColor = MistakesRed,
                onClick = { selectedPerformance = if (selectedPerformance == "مزید محنت درکار") null else "مزید محنت درکار" }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    selectedStudentId = null
                    selectedCategory = null
                    selectedAttendance = null
                    selectedPerformance = null
                    onApplyFilter(null, null, null, null)
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("reset_filter_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "ری سیٹ کریں", fontSize = 12.sp)
            }

            Button(
                onClick = {
                    onApplyFilter(selectedStudentId, selectedCategory, selectedAttendance, selectedPerformance)
                    onClose()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("apply_filter_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberAccent,
                    contentColor = BackgroundDark
                )
            ) {
                Text(text = "فلٹر لگائیں", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    accentColor: Color = AmberAccent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.2f) else Surface2)
            .border(
                width = 1.dp,
                color = if (isSelected) accentColor else SurfaceBorder,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
