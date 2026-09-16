package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.MadrasaBottomBar
import com.example.ui.components.MadrasaTab
import com.example.ui.components.MadrasaTopBar
import com.example.ui.components.RightFilterDrawer
import com.example.ui.screens.AdvanceScreen
import com.example.ui.screens.DailyReportScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.StartupSetupScreen
import com.example.ui.screens.StudentDetailScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.screens.TeacherLoginScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.viewmodel.MadrasaViewModel

@Composable
fun MainAppScreen(
    viewModel: MadrasaViewModel
) {
    val isOnboardingNeeded by viewModel.isOnboardingNeeded.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isFilterOpen by viewModel.isFilterDrawerOpen.collectAsState()
    val teacher by viewModel.teacher.collectAsState()
    val institute by viewModel.institute.collectAsState()
    val liveDateDisplay by viewModel.liveDateDisplay.collectAsState()
    val liveTimeDisplay by viewModel.liveTimeDisplay.collectAsState()
    val allStudents by viewModel.allActiveStudents.collectAsState()

    var currentTab by remember { mutableStateOf(MadrasaTab.DASHBOARD) }
    var selectedStudentDetailId by remember { mutableStateOf<Long?>(null) }

    // User requirement 1: UI Starts from English Side (Left to Right) but in URDU
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        if (isOnboardingNeeded) {
            // User requirement 8: Ask Teacher Name, Institute Name, How many students, Student Information
            StartupSetupScreen(
                viewModel = viewModel,
                onFinished = {
                    currentTab = MadrasaTab.DASHBOARD
                }
            )
        } else if (!isLoggedIn) {
            TeacherLoginScreen(viewModel = viewModel)
        } else {
            Scaffold(
                containerColor = BackgroundDark,
                topBar = {
                    MadrasaTopBar(
                        currentSectionTitle = currentTab.titleUrdu,
                        sectionEnglishName = currentTab.name,
                        teacher = teacher,
                        institute = institute,
                        liveDateDisplay = liveDateDisplay,
                        liveTimeDisplay = liveTimeDisplay,
                        onOpenFilter = { viewModel.openFilterDrawer() },
                        onTeacherClick = {
                            selectedStudentDetailId = null
                            currentTab = MadrasaTab.ADVANCE
                        }
                    )
                },
                bottomBar = {
                    MadrasaBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            selectedStudentDetailId = null
                            currentTab = tab
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Main Content Pages
                    if (selectedStudentDetailId != null) {
                        StudentDetailScreen(
                            studentId = selectedStudentDetailId!!,
                            viewModel = viewModel,
                            onBack = { selectedStudentDetailId = null },
                            onNavigateToDailyEntry = { sId ->
                                selectedStudentDetailId = null
                                viewModel.selectDailyEntryStudent(sId)
                                currentTab = MadrasaTab.DAILY_REPORTS
                            },
                            onNavigateToMonthly = { sId ->
                                selectedStudentDetailId = null
                                viewModel.loadMonthlyReport(sId, viewModel.currentMonth.value, viewModel.currentYear.value)
                                currentTab = MadrasaTab.MONTHLY_REPORTS
                            }
                        )
                    } else {
                        when (currentTab) {
                            MadrasaTab.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToDailyEntry = { sId ->
                                    viewModel.selectDailyEntryStudent(sId)
                                    currentTab = MadrasaTab.DAILY_REPORTS
                                },
                                onNavigateToStudentDetail = { sId ->
                                    selectedStudentDetailId = sId
                                }
                            )

                            MadrasaTab.STUDENTS -> StudentsScreen(
                                viewModel = viewModel,
                                onStudentClick = { sId ->
                                    selectedStudentDetailId = sId
                                },
                                onDailyEntryClick = { sId ->
                                    viewModel.selectDailyEntryStudent(sId)
                                    currentTab = MadrasaTab.DAILY_REPORTS
                                }
                            )

                            MadrasaTab.DAILY_REPORTS -> DailyReportScreen(
                                viewModel = viewModel
                            )

                            MadrasaTab.MONTHLY_REPORTS -> MonthlyReportScreen(
                                viewModel = viewModel
                            )

                            MadrasaTab.ADVANCE -> AdvanceScreen(
                                viewModel = viewModel
                            )
                        }
                    }

                    // Right Filter Drawer overlay
                    AnimatedVisibility(
                        visible = isFilterOpen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.55f))
                                .clickable { viewModel.closeFilterDrawer() }
                        )
                    }

                    AnimatedVisibility(
                        visible = isFilterOpen,
                        enter = slideInHorizontally(initialOffsetX = { -it }),
                        exit = slideOutHorizontally(targetOffsetX = { -it }),
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        RightFilterDrawer(
                            students = allStudents,
                            onClose = { viewModel.closeFilterDrawer() },
                            onApplyFilter = { studentId, _, _, _ ->
                                if (studentId != null) {
                                    viewModel.selectStudent(studentId)
                                    selectedStudentDetailId = studentId
                                }
                                viewModel.closeFilterDrawer()
                            }
                        )
                    }
                }
            }
        }
    }
}
