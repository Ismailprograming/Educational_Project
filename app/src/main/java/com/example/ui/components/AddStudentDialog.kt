package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudentEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.Surface1
import com.example.ui.theme.Surface2
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddStudentDialog(
    initialStudent: StudentEntity? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, fatherName: String, whatsapp: String, startingJuz: Int, halqa: String, rollNo: String) -> Unit
) {
    var name by remember { mutableStateOf(initialStudent?.name ?: "") }
    var fatherName by remember { mutableStateOf(initialStudent?.fatherName ?: "") }
    var whatsapp by remember { mutableStateOf(initialStudent?.whatsappNumber ?: "0300") }
    var startingJuz by remember { mutableStateOf(initialStudent?.startingJuz?.toString() ?: "1") }
    var halqa by remember { mutableStateOf(initialStudent?.halqa ?: "حلقہ اول") }
    var rollNo by remember { mutableStateOf(initialStudent?.rollNumber ?: "") }

    val halqaOptions = listOf("حلقہ اول", "حلقہ دوم", "حلقہ سوئم")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Surface1)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(18.dp))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (initialStudent != null) "طالب علم کی تدوین" else "نیا طالب علم شامل کریں",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Surface2)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "بند کریں", tint = TextSecondary, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name
                Text(text = "طالب علم کا نام *", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("مثال: محمد احمد", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input"),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Father Name
                Text(text = "ولدیت *", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = fatherName,
                    onValueChange = { fatherName = it },
                    placeholder = { Text("مثال: محمد اسلم", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("student_father_input"),
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

                Spacer(modifier = Modifier.height(12.dp))

                // WhatsApp Number
                Text(text = "والد کا واٹس ایپ نمبر (رپورٹ بھیجنے کیلئے) *", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = whatsapp,
                    onValueChange = { whatsapp = it },
                    placeholder = { Text("03001234567", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("student_whatsapp_input"),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Starting Juz & Roll No
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "ابتدائی پارہ (1 تا 30)", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = startingJuz,
                            onValueChange = { startingJuz = it },
                            placeholder = { Text("1", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("student_juz_input"),
                            shape = RoundedCornerShape(10.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "رول نمبر", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = rollNo,
                            onValueChange = { rollNo = it },
                            placeholder = { Text("104", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("student_roll_input"),
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
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Halqa
                Text(text = "حلقہ منتخب کریں", color = AmberAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    halqaOptions.forEach { opt ->
                        val isSelected = halqa == opt
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AmberAccent.copy(alpha = 0.2f) else Surface2)
                                .border(1.dp, if (isSelected) AmberAccent else SurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { halqa = opt }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                color = if (isSelected) AmberAccent else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("منسوخ")
                    }

                    Button(
                        onClick = {
                            val juzInt = startingJuz.toIntOrNull() ?: 1
                            onSave(name, fatherName, whatsapp, juzInt, halqa, rollNo)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(46.dp).testTag("save_student_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberAccent,
                            contentColor = BackgroundDark
                        )
                    ) {
                        Text("محفوظ کریں", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
