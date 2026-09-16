package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MistakesRed
import com.example.ui.theme.Surface1
import com.example.ui.theme.Surface2
import com.example.ui.theme.Surface3
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MadrasaViewModel
import com.example.ui.viewmodel.NewStudentInput

@Composable
fun StartupSetupScreen(
    viewModel: MadrasaViewModel,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var teacherName by remember { mutableStateOf("") }
    var instituteName by remember { mutableStateOf("") }
    var teacherPhone by remember { mutableStateOf("") }

    val studentsList = remember {
        mutableStateListOf(
            NewStudentInput(name = "", fatherName = "", phone = "", startingJuz = 1)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Logo & Header
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Surface1)
                        .border(1.5.dp, AmberAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.madrasa_universal_logo_1789540100683),
                        contentDescription = "Logo",
                        modifier = Modifier.size(54.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "حفظ القرآن مینجمنٹ سسٹم",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "برائے جملہ مدارس و جامعات الاسلامیہ",
                    color = AmberAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "استاد محترم کا اکاؤنٹ اور مدرسہ کا سیٹ اپ",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Teacher & Institute Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.School, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "استاد اور مدرسہ کی بنیادی تفصیلات", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Teacher Name
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("نام استاد محترم *", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("startup_teacher_name"),
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

                    // Institute Name
                    OutlinedTextField(
                        value = instituteName,
                        onValueChange = { instituteName = it },
                        label = { Text("ادارے / مدرسہ کا نام *", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("startup_institute_name"),
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

                    // Phone
                    OutlinedTextField(
                        value = teacherPhone,
                        onValueChange = { teacherPhone = it },
                        label = { Text("رابطہ نمبر / واٹس ایپ", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("startup_teacher_phone"),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                }
            }
        }

        // Students Information Header & Stepper
        item {
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
                    Column {
                        Text(text = "طلبہ کی تعداد اور اندراج", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(text = "تعداد: ${studentsList.size} طلبہ", color = AmberAccent, fontSize = 11.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                studentsList.add(NewStudentInput())
                            },
                            modifier = Modifier.height(36.dp).testTag("add_student_row_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Surface2, contentColor = AmberAccent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "طالب علم شامل کریں", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Dynamic Student Rows
        itemsIndexed(studentsList) { index, studentItem ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "طالب علم نمبر ${index + 1}",
                            color = AmberAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (studentsList.size > 1) {
                            IconButton(
                                onClick = { studentsList.removeAt(index) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف کریں",
                                    tint = MistakesRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Student Name
                    OutlinedTextField(
                        value = studentItem.name,
                        onValueChange = { studentsList[index] = studentItem.copy(name = it) },
                        label = { Text("طالب علم کا نام *", color = TextMuted, fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("startup_student_name_$index"),
                        shape = RoundedCornerShape(8.dp),
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

                    // Father Name & Cell Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = studentItem.fatherName,
                            onValueChange = { studentsList[index] = studentItem.copy(fatherName = it) },
                            label = { Text("ولدیت", color = TextMuted, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("startup_father_name_$index"),
                            shape = RoundedCornerShape(8.dp),
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

                        OutlinedTextField(
                            value = studentItem.phone,
                            onValueChange = { studentsList[index] = studentItem.copy(phone = it) },
                            label = { Text("والد کا واٹس ایپ", color = TextMuted, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f).testTag("startup_phone_$index"),
                            shape = RoundedCornerShape(8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                    }

                    // Starting Juz
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "ابتدائی پارہ (1 تا 30):", color = TextSecondary, fontSize = 11.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Surface2)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val cur = studentItem.startingJuz
                                        if (cur > 1) {
                                            studentsList[index] = studentItem.copy(startingJuz = cur - 1)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "-", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }

                            // Directly typeable number field
                            BasicTextField(
                                value = if (studentItem.startingJuz == 0) "" else studentItem.startingJuz.toString(),
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }
                                    val num = digits.toIntOrNull() ?: 1
                                    studentsList[index] = studentItem.copy(startingJuz = num.coerceIn(1, 30))
                                },
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Surface2)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = AmberAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                cursorBrush = SolidColor(AmberAccent)
                            )

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Surface2)
                                    .border(1.dp, SurfaceBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val cur = studentItem.startingJuz
                                        if (cur < 30) {
                                            studentsList[index] = studentItem.copy(startingJuz = cur + 1)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "+", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Submit Button
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    viewModel.completeStartupSetup(
                        teacherName = teacherName,
                        instituteName = instituteName,
                        phone = teacherPhone,
                        studentsList = studentsList,
                        onComplete = onFinished
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("complete_startup_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberAccent,
                    contentColor = BackgroundDark
                )
            ) {
                Text(text = "محفوظ کریں اور شروع کریں", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
