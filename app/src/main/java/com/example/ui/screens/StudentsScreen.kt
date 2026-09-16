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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import com.example.data.model.StudentEntity
import com.example.ui.components.AddStudentDialog
import com.example.ui.components.FilterChipItem
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.ProgressViolet
import com.example.ui.theme.SabaqBlue
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.Surface1
import com.example.ui.theme.Surface2
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun StudentsScreen(
    viewModel: MadrasaViewModel,
    onStudentClick: (studentId: Long) -> Unit,
    onDailyEntryClick: (studentId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.studentSearchQuery.collectAsState()
    val activeFilter by viewModel.studentFilter.collectAsState()
    val students by viewModel.filteredStudents.collectAsState()

    var isAddDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddDialogOpen = true },
                containerColor = AmberAccent,
                contentColor = BackgroundDark,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_student_fab")
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "نیا طالب علم شامل کریں")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Input
            item {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onStudentSearch(it) },
                    placeholder = { Text("طالب علم، والدیت یا فون نمبر سے تلاش کریں...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AmberAccent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_student_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AmberAccent,
                        unfocusedBorderColor = SurfaceBorder,
                        focusedContainerColor = Surface1,
                        unfocusedContainerColor = Surface1
                    ),
                    singleLine = true
                )
            }

            // Halqa / Category Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipItem(
                        label = "تمام طلبہ (${students.size})",
                        isSelected = activeFilter == "ALL",
                        onClick = { viewModel.setStudentFilter("ALL") }
                    )
                    FilterChipItem(
                        label = "حلقہ اول",
                        isSelected = activeFilter == "GRADE1",
                        onClick = { viewModel.setStudentFilter("GRADE1") }
                    )
                }
            }

            // Student Cards
            items(students) { student ->
                StudentCardItem(
                    student = student,
                    onClick = { onStudentClick(student.id) },
                    onDailyEntryClick = { onDailyEntryClick(student.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        if (isAddDialogOpen) {
            AddStudentDialog(
                onDismiss = { isAddDialogOpen = false },
                onSave = { name, fatherName, whatsapp, startingJuz, halqa, rollNo ->
                    viewModel.addStudent(name, fatherName, whatsapp, startingJuz, halqa, rollNo)
                }
            )
        }
    }
}

@Composable
fun StudentCardItem(
    student: StudentEntity,
    onClick: () -> Unit,
    onDailyEntryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface1)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("student_card_${student.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar + Name + Father
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Surface2)
                            .border(1.5.dp, AmberAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.take(2),
                            color = AmberAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = student.name,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ولد: ${student.fatherName} • رول: ${student.rollNumber.ifEmpty { "104" }}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Halqa Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Surface2)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = student.halqa,
                        color = AmberAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Metrics Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Surface2)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "موجودہ پارہ", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "پارہ ${student.currentJuz}", color = ProgressViolet, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(text = "آخری سبق", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = student.currentSurah, color = SabaqBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(text = "ابتدائی پارہ", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "پارہ ${student.startingJuz}", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions: WhatsApp Number & Fast Daily Entry Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = student.whatsappNumber, color = TextMuted, fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onDailyEntryClick,
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("card_daily_entry_${student.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Surface2,
                            contentColor = AmberAccent
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                    ) {
                        Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "روزانہ اندراج", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onClick,
                        modifier = Modifier.height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Surface2,
                            contentColor = TextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                    ) {
                        Text(text = "پروفائل", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
