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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InstituteEntity
import com.example.data.model.TeacherEntity
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.Primary900
import com.example.ui.theme.Surface1
import com.example.ui.theme.Surface2
import com.example.ui.theme.Surface3
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MadrasaTopBar(
    currentSectionTitle: String,
    sectionEnglishName: String,
    teacher: TeacherEntity?,
    institute: InstituteEntity? = null,
    liveDateDisplay: String = "",
    liveTimeDisplay: String = "",
    onOpenFilter: () -> Unit,
    onTeacherClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary900)
            .border(width = 1.dp, color = SurfaceBorder.copy(alpha = 0.5f), shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Right Icon: Filter Drawer Trigger
            IconButton(
                onClick = onOpenFilter,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface1)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .testTag("open_filter_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "فلٹر دراز کھولیں",
                    tint = AmberAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Center Branding & Tag: Institute Name and Auto Date/Time
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = institute?.name?.ifBlank { "جامعہ اسلامیہ" } ?: "جامعہ اسلامیہ",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Surface3)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = currentSectionTitle,
                            color = AmberAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (liveDateDisplay.isNotBlank()) "$liveDateDisplay • $liveTimeDisplay" else "حفظ القرآن مینجمنٹ سسٹم",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            // Left Icons: Notification & Teacher Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Surface1)
                        .clickable { /* Notification trigger */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "اطلاعات",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    // Amber Notification dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AmberAccent)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Teacher Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Surface2)
                        .border(1.5.dp, AmberAccent, CircleShape)
                        .clickable { onTeacherClick() }
                        .testTag("teacher_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (teacher?.name?.take(2) ?: "قار"),
                        color = AmberAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
