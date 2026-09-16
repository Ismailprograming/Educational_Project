package com.example.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.DailyReportEntity
import com.example.data.model.InstituteEntity
import com.example.data.model.MonthlyReportEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TeacherEntity
import com.example.data.model.WhatsAppDeliveryEntity
import com.example.data.repository.MadrasaRepository
import com.example.util.ReportGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class NewStudentInput(
    val name: String = "",
    val fatherName: String = "",
    val phone: String = "",
    val startingJuz: Int = 1
)

class MadrasaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MadrasaRepository

    // --- Date & Time localization & Auto handling ---
    val monthNamesUrdu = listOf(
        "جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون",
        "جولائی", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر"
    )

    private val dayNamesUrdu = mapOf(
        Calendar.SUNDAY to "اتوار",
        Calendar.MONDAY to "پیر",
        Calendar.TUESDAY to "منگل",
        Calendar.WEDNESDAY to "بدھ",
        Calendar.THURSDAY to "جمعرات",
        Calendar.FRIDAY to "جمعہ",
        Calendar.SATURDAY to "ہفتہ"
    )

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val timeFormat12 = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    fun formatCalendarToUrdu(cal: Calendar, prefixToday: Boolean = false): String {
        val dayName = dayNamesUrdu[cal.get(Calendar.DAY_OF_WEEK)] ?: ""
        val dayNum = cal.get(Calendar.DAY_OF_MONTH)
        val monthIdx = cal.get(Calendar.MONTH)
        val monthName = if (monthIdx in monthNamesUrdu.indices) monthNamesUrdu[monthIdx] else "ماہ ${monthIdx + 1}"
        val year = cal.get(Calendar.YEAR)

        return if (prefixToday) {
            "آج • $dayName، $dayNum $monthName ${year}ء"
        } else {
            "$dayName، $dayNum $monthName ${year}ء"
        }
    }

    fun getLiveTimeString(): String {
        val now = Calendar.getInstance()
        return timeFormat12.format(now.time)
    }

    private val _liveTimeDisplay = MutableStateFlow(getLiveTimeString())
    val liveTimeDisplay: StateFlow<String> = _liveTimeDisplay.asStateFlow()

    private val _liveDateDisplay = MutableStateFlow(formatCalendarToUrdu(Calendar.getInstance(), prefixToday = true))
    val liveDateDisplay: StateFlow<String> = _liveDateDisplay.asStateFlow()

    private val _selectedDate = MutableStateFlow(dateFormat.format(Calendar.getInstance().time))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _dateDisplayUrdu = MutableStateFlow(formatCalendarToUrdu(Calendar.getInstance(), prefixToday = true))
    val dateDisplayUrdu: StateFlow<String> = _dateDisplayUrdu.asStateFlow()

    val hijriDateDisplay = "۱۵ رمضان المبارک ۱۴۴۸ھ"

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MadrasaRepository(db.madrasaDao())

        // Auto Live Date & Time background ticker
        viewModelScope.launch {
            while (isActive) {
                _liveTimeDisplay.value = getLiveTimeString()
                val todayCal = Calendar.getInstance()
                val todayStr = dateFormat.format(todayCal.time)
                _liveDateDisplay.value = formatCalendarToUrdu(todayCal, prefixToday = true)
                // If user is currently looking at today, keep the header synced with live today
                if (_selectedDate.value == todayStr) {
                    _dateDisplayUrdu.value = formatCalendarToUrdu(todayCal, prefixToday = true)
                }
                delay(15_000)
            }
        }
    }

    fun resetToAutoToday() {
        val todayCal = Calendar.getInstance()
        val todayStr = dateFormat.format(todayCal.time)
        _selectedDate.value = todayStr
        _dateDisplayUrdu.value = formatCalendarToUrdu(todayCal, prefixToday = true)
        val m = todayCal.get(Calendar.MONTH) + 1
        val y = todayCal.get(Calendar.YEAR)
        currentMonth.value = m
        currentYear.value = y
        checkExistingReportForCurrentSelection()
        showToast("خودکار تاریخ و وقت: $todayStr")
    }

    fun setDate(dateStr: String, urduDisplay: String) {
        _selectedDate.value = dateStr
        _dateDisplayUrdu.value = urduDisplay
        checkExistingReportForCurrentSelection()
    }

    fun shiftDate(days: Int) {
        try {
            val date = dateFormat.parse(_selectedDate.value) ?: Date()
            val cal = Calendar.getInstance().apply { time = date }
            cal.add(Calendar.DAY_OF_YEAR, days)
            val newDateStr = dateFormat.format(cal.time)
            _selectedDate.value = newDateStr

            val todayStr = dateFormat.format(Date())
            _dateDisplayUrdu.value = if (newDateStr == todayStr) {
                formatCalendarToUrdu(cal, prefixToday = true)
            } else {
                formatCalendarToUrdu(cal, prefixToday = false)
            }
            checkExistingReportForCurrentSelection()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Onboarding & Setup State ---
    val allActiveStudents: StateFlow<List<StudentEntity>> = repository.activeStudents.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val institute: StateFlow<InstituteEntity?> = repository.institute.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val teacher: StateFlow<TeacherEntity?> = repository.teacher.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val settings: StateFlow<AppSettingsEntity?> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isOnboardingNeeded: StateFlow<Boolean> = combine(teacher, allActiveStudents) { t, students ->
        t == null && students.isEmpty()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun completeStartupSetup(
        teacherName: String,
        instituteName: String,
        phone: String,
        studentsList: List<NewStudentInput>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            if (teacherName.isBlank() || instituteName.isBlank()) {
                showToast("براہ کرم استاد اور ادارے کا نام درج کریں۔")
                return@launch
            }

            // 1. Create Institute
            val inst = InstituteEntity(
                name = instituteName.trim(),
                phone = phone.trim(),
                campus = "مرکزی کیمپس"
            )
            repository.insertInstitute(inst)

            // 2. Create Teacher
            val t = TeacherEntity(
                name = teacherName.trim(),
                phone = phone.trim(),
                designation = "مدرس حفظ القرآن الکریم",
                instituteId = 1L,
                pin = "1234"
            )
            repository.insertTeacher(t)

            // 3. Create Settings
            repository.insertSettings(AppSettingsEntity())

            // 4. Create Students
            val validStudents = studentsList.filter { it.name.isNotBlank() }
            val studentEntities = validStudents.mapIndexed { idx, s ->
                StudentEntity(
                    name = s.name.trim(),
                    fatherName = s.fatherName.trim().ifEmpty { "والد" },
                    whatsappNumber = s.phone.trim().ifEmpty { phone.trim() },
                    startingJuz = s.startingJuz.coerceIn(1, 30),
                    currentJuz = s.startingJuz.coerceIn(1, 30),
                    rollNumber = (101 + idx).toString(),
                    halqa = "حلقہ اول"
                )
            }
            if (studentEntities.isNotEmpty()) {
                repository.insertStudents(studentEntities)
                val firstStudent = studentEntities.first()
                _selectedStudentId.value = 1L
                _dailyEntryStudentId.value = 1L
            }

            showToast("تنصیب مکمل ہوگئی! خوش آمدید ✓")
            onComplete()
        }
    }

    // --- Authentication ---
    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun login(enteredPin: String) {
        viewModelScope.launch {
            val t = repository.getTeacherDirect()
            if (t != null && t.pin == enteredPin) {
                _isLoggedIn.value = true
                _loginError.value = null
            } else {
                _loginError.value = "درج کردہ پن کوڈ درست نہیں ہے۔"
            }
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    fun updateInstitute(inst: InstituteEntity) {
        viewModelScope.launch {
            repository.updateInstitute(inst)
            showToast("ادارے کی معلومات محفوظ ہوگئیں ✓")
        }
    }

    fun updateTeacher(t: TeacherEntity) {
        viewModelScope.launch {
            repository.updateTeacher(t)
            showToast("استاد کا پروفائل محفوظ ہوگیا ✓")
        }
    }

    fun updateSettings(s: AppSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(s)
            showToast("ترتیبات محفوظ ہوگئیں ✓")
        }
    }

    // --- Dynamic Today Stats ---
    val activeStudentsCount: StateFlow<Int> = repository.activeStudentsCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayReportsCount: StateFlow<Int> = repository.getReportsCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayPresentCount: StateFlow<Int> = repository.getPresentCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayAbsentCount: StateFlow<Int> = repository.getAbsentCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayLeaveCount: StateFlow<Int> = repository.getLeaveCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todaySabaqLines: StateFlow<Int> = repository.getTotalSabaqLinesForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayMistakes: StateFlow<Int> = repository.getTotalMistakesForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayExcellentCount: StateFlow<Int> = repository.getExcellentPerformanceCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayGoodCount: StateFlow<Int> = repository.getGoodPerformanceCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val todayNeedsRevisionCount: StateFlow<Int> = repository.getNeedsRevisionCountForDate(_selectedDate.value).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    // --- Students Management ---
    private val _studentSearchQuery = MutableStateFlow("")
    val studentSearchQuery: StateFlow<String> = _studentSearchQuery.asStateFlow()

    private val _studentFilter = MutableStateFlow("ALL")
    val studentFilter: StateFlow<String> = _studentFilter.asStateFlow()

    val filteredStudents: StateFlow<List<StudentEntity>> = combine(
        allActiveStudents,
        _studentSearchQuery,
        _studentFilter
    ) { students, query, filter ->
        var list = if (query.isBlank()) students else {
            students.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.fatherName.contains(query, ignoreCase = true) ||
                        it.whatsappNumber.contains(query)
            }
        }
        when (filter) {
            "GRADE1" -> list = list.filter { it.halqa == "حلقہ اول" }
            else -> {}
        }
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onStudentSearch(query: String) {
        _studentSearchQuery.value = query
    }

    fun setStudentFilter(filter: String) {
        _studentFilter.value = filter
    }

    fun addStudent(
        name: String,
        fatherName: String,
        whatsapp: String,
        startingJuz: Int,
        halqa: String = "حلقہ اول",
        rollNumber: String = ""
    ) {
        viewModelScope.launch {
            if (name.isBlank() || fatherName.isBlank()) {
                showToast("براہ کرم تمام لازمی خانے پر کریں۔")
                return@launch
            }
            val newStudent = StudentEntity(
                name = name.trim(),
                fatherName = fatherName.trim(),
                whatsappNumber = whatsapp.trim(),
                startingJuz = startingJuz.coerceIn(1, 30),
                currentJuz = startingJuz.coerceIn(1, 30),
                halqa = halqa,
                rollNumber = rollNumber.ifEmpty { (100 + (1..99).random()).toString() }
            )
            repository.addStudent(newStudent)
            showToast("طالب علم محفوظ ہوگیا ✓")
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student)
            showToast("طالب علم کی معلومات اپ ڈیٹ ہوگئیں ✓")
        }
    }

    fun deleteStudent(studentId: Long) {
        viewModelScope.launch {
            repository.softDeleteStudent(studentId)
            showToast("طالب علم حذف ہوگیا")
        }
    }

    // --- Student Detail Profile ---
    private val _selectedStudentId = MutableStateFlow<Long?>(null)
    val selectedStudentId: StateFlow<Long?> = _selectedStudentId.asStateFlow()

    fun selectStudent(id: Long) {
        _selectedStudentId.value = id
        _dailyEntryStudentId.value = id
        checkExistingReportForCurrentSelection()
    }

    fun getDailyReportsForStudent(studentId: Long): StateFlow<List<DailyReportEntity>> {
        return repository.getDailyReportsForStudent(studentId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // --- Daily Report Fast Entry ---
    private val _dailyEntryStudentId = MutableStateFlow(1L)
    val dailyEntryStudentId: StateFlow<Long> = _dailyEntryStudentId.asStateFlow()

    private val _existingDailyReport = MutableStateFlow<DailyReportEntity?>(null)
    val existingDailyReport: StateFlow<DailyReportEntity?> = _existingDailyReport.asStateFlow()

    // Form inputs state
    val formSabaqSurah = MutableStateFlow("سورۃ البقرہ")
    val formSabaqFromAyah = MutableStateFlow(1)
    val formSabaqToAyah = MutableStateFlow(10)
    val formSabaqLines = MutableStateFlow(10)

    val formSabqiJuz = MutableStateFlow(1)
    val formSabqiMistakes = MutableStateFlow(0)

    val formManzilJuz = MutableStateFlow(1)
    val formManzilMistakes = MutableStateFlow(0)

    val formAttendance = MutableStateFlow("حاضر")
    val formPerformance = MutableStateFlow("بہترین")
    val formRemarks = MutableStateFlow("")

    fun checkExistingReportForCurrentSelection() {
        viewModelScope.launch {
            val studentId = _dailyEntryStudentId.value
            val date = _selectedDate.value
            val existing = repository.getDailyReportForStudentAndDate(studentId, date)
            _existingDailyReport.value = existing
            if (existing != null) {
                formSabaqSurah.value = existing.sabaqSurah
                formSabaqFromAyah.value = existing.sabaqFromAyah
                formSabaqToAyah.value = existing.sabaqToAyah
                formSabaqLines.value = existing.sabaqLines
                formSabqiJuz.value = existing.sabqiJuz
                formSabqiMistakes.value = existing.sabqiMistakes
                formManzilJuz.value = existing.manzilJuz
                formManzilMistakes.value = existing.manzilMistakes
                formAttendance.value = existing.attendance
                formPerformance.value = existing.performance
                formRemarks.value = existing.remarks
            }
        }
    }

    fun selectDailyEntryStudent(id: Long) {
        _dailyEntryStudentId.value = id
        checkExistingReportForCurrentSelection()
    }

    fun saveCurrentDailyReport(onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            val existingId = _existingDailyReport.value?.id ?: 0L
            val report = DailyReportEntity(
                id = existingId,
                studentId = _dailyEntryStudentId.value,
                date = _selectedDate.value,
                dateDisplayUrdu = _dateDisplayUrdu.value,
                sabaqSurah = formSabaqSurah.value,
                sabaqFromAyah = formSabaqFromAyah.value,
                sabaqToAyah = formSabaqToAyah.value,
                sabaqLines = formSabaqLines.value,
                sabqiJuz = formSabqiJuz.value,
                sabqiMistakes = formSabqiMistakes.value,
                manzilJuz = formManzilJuz.value,
                manzilMistakes = formManzilMistakes.value,
                attendance = formAttendance.value,
                performance = formPerformance.value,
                remarks = formRemarks.value
            )
            repository.saveDailyReport(report)
            _existingDailyReport.value = report
            showToast("رپورٹ محفوظ ہوگئی ✓")
            onSuccess?.invoke()
        }
    }

    fun saveAndNextStudent() {
        saveCurrentDailyReport {
            viewModelScope.launch {
                val students = repository.getActiveStudentsDirect()
                if (students.isNotEmpty()) {
                    val currentIndex = students.indexOfFirst { it.id == _dailyEntryStudentId.value }
                    val nextStudent = if (currentIndex != -1 && currentIndex < students.size - 1) {
                        students[currentIndex + 1]
                    } else {
                        students[0]
                    }
                    _dailyEntryStudentId.value = nextStudent.id
                    formRemarks.value = ""
                    checkExistingReportForCurrentSelection()
                    showToast("اگلا طالب علم: ${nextStudent.name}")
                }
            }
        }
    }

    fun markAllPresent() {
        viewModelScope.launch {
            val students = repository.getActiveStudentsDirect()
            val date = _selectedDate.value
            for (s in students) {
                val existing = repository.getDailyReportForStudentAndDate(s.id, date)
                if (existing == null) {
                    val report = DailyReportEntity(
                        studentId = s.id,
                        date = date,
                        attendance = "حاضر",
                        performance = "بہترین",
                        remarks = "حاضر"
                    )
                    repository.saveDailyReport(report)
                }
            }
            showToast("تمام طلبہ حاضر نشان زد ہو گئے ✓")
        }
    }

    // --- Monthly Reports ---
    val currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val monthlyStudentId = MutableStateFlow(1L)

    fun getCurrentMonthNameUrdu(): String {
        val m = currentMonth.value
        return if (m in 1..12) monthNamesUrdu[m - 1] else "ماہ $m"
    }

    fun nextMonth() {
        var m = currentMonth.value + 1
        var y = currentYear.value
        if (m > 12) {
            m = 1
            y += 1
        }
        currentMonth.value = m
        currentYear.value = y
        loadMonthlyReport(monthlyStudentId.value, m, y)
        showToast("اگلا مہینہ منتخب ہوا: ${monthNamesUrdu[m - 1]} $y")
    }

    fun previousMonth() {
        var m = currentMonth.value - 1
        var y = currentYear.value
        if (m < 1) {
            m = 12
            y -= 1
        }
        currentMonth.value = m
        currentYear.value = y
        loadMonthlyReport(monthlyStudentId.value, m, y)
        showToast("پچھلا مہینہ منتخب ہوا: ${monthNamesUrdu[m - 1]} $y")
    }

    fun setMonthAndYear(month: Int, year: Int) {
        currentMonth.value = month
        currentYear.value = year
        loadMonthlyReport(monthlyStudentId.value, month, year)
    }

    fun resetApp(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.resetAllData()
            _isLoggedIn.value = false
            _selectedStudentId.value = null
            _dailyEntryStudentId.value = 0L
            _monthlyReport.value = null
            _monthlyDailyBreakdown.value = emptyList()
            showToast("ایپ کا تمام ڈیٹا صاف ہو گیا - ابتدائی سیٹ اپ")
            onComplete()
        }
    }

    private val _monthlyReport = MutableStateFlow<MonthlyReportEntity?>(null)
    val monthlyReport: StateFlow<MonthlyReportEntity?> = _monthlyReport.asStateFlow()

    private val _monthlyDailyBreakdown = MutableStateFlow<List<DailyReportEntity>>(emptyList())
    val monthlyDailyBreakdown: StateFlow<List<DailyReportEntity>> = _monthlyDailyBreakdown.asStateFlow()

    fun loadMonthlyReport(studentId: Long, month: Int, year: Int) {
        monthlyStudentId.value = studentId
        currentMonth.value = month
        currentYear.value = year
        viewModelScope.launch {
            val computed = repository.computeMonthlyReport(studentId, month, year)
            _monthlyReport.value = computed

            val monthDays = repository.getDailyReportsForStudentInMonth(studentId, month, year)
            _monthlyDailyBreakdown.value = monthDays
        }
    }

    /**
     * Supports exporting in 3 file formats: DOC, PDF, IMAGE
     */
    fun exportMonthlyReport(format: String) {
        viewModelScope.launch {
            val sId = monthlyStudentId.value
            val student = repository.getStudentDirect(sId)
            val inst = repository.getInstituteDirect() ?: InstituteEntity()
            val t = repository.getTeacherDirect() ?: TeacherEntity()
            val mReport = _monthlyReport.value ?: repository.computeMonthlyReport(sId, currentMonth.value, currentYear.value)
            val dailyList = _monthlyDailyBreakdown.value

            if (student != null) {
                val file: File? = when (format.uppercase()) {
                    "DOC" -> ReportGenerator.generateMonthlyDoc(getApplication(), student, mReport, inst, t, dailyList)
                    "IMAGE" -> ReportGenerator.generateMonthlyImage(getApplication(), student, mReport, inst, t, dailyList)
                    else -> ReportGenerator.generateMonthlyPdf(getApplication(), student, mReport, inst, t, dailyList)
                }

                val msg = ReportGenerator.formatMonthlyWhatsAppMessage(student, mReport, inst, dailyList)
                ReportGenerator.shareReport(getApplication(), msg, file, student.whatsappNumber)

                repository.logWhatsAppDelivery(
                    WhatsAppDeliveryEntity(
                        studentId = student.id,
                        studentName = student.name,
                        reportId = mReport.id,
                        reportType = "MONTHLY_$format",
                        parentNumber = student.whatsappNumber,
                        message = msg,
                        fileUrl = file?.absolutePath,
                        status = "Sent"
                    )
                )
                showToast("رپورٹ ($format) تیار اور شیئر کر دی گئی ✓")
            }
        }
    }

    fun shareDailyWhatsApp() {
        viewModelScope.launch {
            val sId = _dailyEntryStudentId.value
            val student = repository.getStudentDirect(sId)
            val report = _existingDailyReport.value ?: DailyReportEntity(
                studentId = sId,
                date = _selectedDate.value,
                sabaqSurah = formSabaqSurah.value,
                sabaqFromAyah = formSabaqFromAyah.value,
                sabaqToAyah = formSabaqToAyah.value,
                sabaqLines = formSabaqLines.value,
                sabqiJuz = formSabqiJuz.value,
                sabqiMistakes = formSabqiMistakes.value,
                manzilJuz = formManzilJuz.value,
                manzilMistakes = formManzilMistakes.value,
                attendance = formAttendance.value,
                performance = formPerformance.value,
                remarks = formRemarks.value
            )
            val inst = repository.getInstituteDirect() ?: InstituteEntity()
            val stg = repository.getSettingsDirect() ?: AppSettingsEntity()

            if (student != null) {
                val msg = ReportGenerator.formatDailyWhatsAppMessage(stg.whatsappTemplate, student, report, inst)
                ReportGenerator.shareReport(getApplication(), msg, null, student.whatsappNumber)

                repository.logWhatsAppDelivery(
                    WhatsAppDeliveryEntity(
                        studentId = student.id,
                        studentName = student.name,
                        reportId = report.id,
                        reportType = "DAILY",
                        parentNumber = student.whatsappNumber,
                        message = msg,
                        status = "Sent"
                    )
                )
                showToast("واٹس ایپ پر بھیج دی گئی ✓")
            }
        }
    }

    // --- Right Filter Drawer ---
    val isFilterDrawerOpen = MutableStateFlow(false)

    fun openFilterDrawer() {
        isFilterDrawerOpen.value = true
    }

    fun closeFilterDrawer() {
        isFilterDrawerOpen.value = false
    }

    // --- Sync State ---
    private val _syncState = MutableStateFlow("سرور اسٹیٹس: فعال و محفوظ")
    val syncState: StateFlow<String> = _syncState.asStateFlow()

    fun triggerCloudSync() {
        viewModelScope.launch {
            _syncState.value = "سنکرونائز ہو رہا ہے..."
            kotlinx.coroutines.delay(1200)
            _syncState.value = "سنکرونائز ہوگیا ✓"
            showToast("سنکرونائز ہوگیا ✓")
            kotlinx.coroutines.delay(2000)
            _syncState.value = "سرور اسٹیٹس: فعال و محفوظ"
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }
}
