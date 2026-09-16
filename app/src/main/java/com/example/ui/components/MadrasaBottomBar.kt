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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.Primary900
import com.example.ui.theme.Surface1
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

enum class MadrasaTab(
    val id: String,
    val titleUrdu: String,
    val icon: ImageVector,
    val testTag: String
) {
    DASHBOARD("dashboard", "ڈیش بورڈ", Icons.Default.Dashboard, "tab_dashboard"),
    STUDENTS("students", "طلبہ", Icons.Default.Groups, "tab_students"),
    DAILY_REPORTS("daily_reports", "روزانہ رپورٹ", Icons.AutoMirrored.Filled.Assignment, "tab_daily_reports"),
    MONTHLY_REPORTS("monthly_reports", "ماہانہ رپورٹ", Icons.Default.CalendarMonth, "tab_monthly_reports"),
    ADVANCE("advance", "ایڈوانس", Icons.Default.Settings, "tab_advance")
}

@Composable
fun MadrasaBottomBar(
    currentTab: MadrasaTab,
    onTabSelected: (MadrasaTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary900)
            .border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MadrasaTab.values().forEach { tab ->
            val isSelected = currentTab == tab
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Surface1 else Primary900)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) AmberAccent.copy(alpha = 0.5f) else SurfaceBorder.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
                    .testTag(tab.testTag)
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.titleUrdu,
                    tint = if (isSelected) AmberAccent else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = tab.titleUrdu,
                    color = if (isSelected) AmberAccent else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
