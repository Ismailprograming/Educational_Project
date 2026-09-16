package com.example.data.repository

import com.example.data.db.MadrasaDao
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.DailyReportEntity
import com.example.data.model.InstituteEntity
import com.example.data.model.MonthlyReportEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TeacherEntity
import com.example.data.model.WhatsAppDeliveryEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MadrasaRepository(private val dao: MadrasaDao) {

    // --- Students ---
    val activeStudents: Flow<List<StudentEntity>> = dao.getActiveStudents()
    val activeStudentsCount: Flow<Int> = dao.getActiveStudentsCount()

    fun searchStudents(query: String): Flow<List<StudentEntity>> = dao.searchStudents(query)

    fun getStudent(id: Long): Flow<StudentEntity?> = dao.getStudentById(id)

    suspend fun getStudentDirect(id: Long): StudentEntity? = dao.getStudentByIdDirect(id)

    suspend fun addStudent(student: StudentEntity): Long = dao.insertStudent(student)

    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)

    suspend fun softDeleteStudent(studentId: Long) = dao.softDeleteStudent(studentId)

    suspend fun getActiveStudentsDirect(): List<StudentEntity> = dao.getActiveStudentsDirect()

    // --- Daily Reports ---
    val allDailyReports: Flow<List<DailyReportEntity>> = dao.getAllDailyReports()

    fun getDailyReportsForDate(date: String): Flow<List<DailyReportEntity>> =
        dao.getDailyReportsForDate(date)

    fun getDailyReportsForStudent(studentId: Long): Flow<List<DailyReportEntity>> =
        dao.getDailyReportsForStudent(studentId)

    suspend fun getDailyReportForStudentAndDate(studentId: Long, date: String): DailyReportEntity? =
        dao.getDailyReportForStudentAndDate(studentId, date)

    suspend fun saveDailyReport(report: DailyReportEntity): Long {
        // Also update the student's current Juz / Surah / Ayah
        val student = dao.getStudentByIdDirect(report.studentId)
        if (student != null) {
            val updated = student.copy(
                currentJuz = if (report.sabqiJuz > 0) report.sabqiJuz + 1 else student.currentJuz,
                currentSurah = report.sabaqSurah,
                currentAyah = report.sabaqToAyah,
                updatedAt = System.currentTimeMillis()
            )
            dao.updateStudent(updated)
        }
        return if (report.id == 0L) {
            val existing = dao.getDailyReportForStudentAndDate(report.studentId, report.date)
            if (existing != null) {
                dao.updateDailyReport(report.copy(id = existing.id))
                existing.id
            } else {
                dao.insertDailyReport(report)
            }
        } else {
            dao.updateDailyReport(report)
            report.id
        }
    }

    suspend fun deleteDailyReport(id: Long) = dao.deleteDailyReport(id)

    // --- Dynamic Today Stats ---
    fun getReportsCountForDate(date: String): Flow<Int> = dao.getReportsCountForDate(date)
    fun getPresentCountForDate(date: String): Flow<Int> = dao.getPresentCountForDate(date)
    fun getAbsentCountForDate(date: String): Flow<Int> = dao.getAbsentCountForDate(date)
    fun getLeaveCountForDate(date: String): Flow<Int> = dao.getLeaveCountForDate(date)
    fun getTotalSabaqLinesForDate(date: String): Flow<Int> = dao.getTotalSabaqLinesForDate(date)
    fun getTotalMistakesForDate(date: String): Flow<Int> = dao.getTotalMistakesForDate(date)
    fun getExcellentPerformanceCountForDate(date: String): Flow<Int> = dao.getExcellentPerformanceCountForDate(date)
    fun getGoodPerformanceCountForDate(date: String): Flow<Int> = dao.getGoodPerformanceCountForDate(date)
    fun getNeedsRevisionCountForDate(date: String): Flow<Int> = dao.getNeedsRevisionCountForDate(date)

    // --- Dynamic Monthly Statistics Calculation ---
    suspend fun computeMonthlyReport(studentId: Long, month: Int, year: Int): MonthlyReportEntity {
        val monthPrefix = String.format(Locale.ENGLISH, "%04d-%02d", year, month)
        val monthReports = dao.getDailyReportsForStudentAndMonth(studentId, monthPrefix)

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        val totalCalendarDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        var presentDays = 0
        var absentDays = 0
        var leaveDays = 0
        var totalSabaqDays = 0
        var totalSabaqLines = 0
        var totalSabqiEntries = 0
        var totalManzilEntries = 0
        var totalSabqiMistakes = 0
        var totalManzilMistakes = 0
        var perfScoreSum = 0.0

        for (report in monthReports) {
            when (report.attendance) {
                "حاضر" -> presentDays++
                "غیر حاضر" -> absentDays++
                "چھٹی" -> leaveDays++
                else -> presentDays++
            }
            if (report.sabaqLines > 0) {
                totalSabaqDays++
                totalSabaqLines += report.sabaqLines
            }
            if (report.sabqiJuz > 0) {
                totalSabqiEntries++
                totalSabqiMistakes += report.sabqiMistakes
            }
            if (report.manzilJuz > 0) {
                totalManzilEntries++
                totalManzilMistakes += report.manzilMistakes
            }

            perfScoreSum += when (report.performance) {
                "بہترین" -> 5.0
                "اچھی" -> 4.0
                "مناسب" -> 3.0
                "مزید محنت درکار" -> 2.0
                else -> 4.0
            }
        }

        val count = monthReports.size
        val avgPerf = if (count > 0) String.format(Locale.ENGLISH, "%.1f", perfScoreSum / count).toDoubleOrNull() ?: 4.4 else 4.4

        val monthNamesUrdu = arrayOf(
            "جنوری", "فروری", "مارچ", "اپریل", "مئی", "جون",
            "جولائی", "اگست", "ستمبر", "اکتوبر", "نومبر", "دسمبر"
        )
        val monthNameUrdu = "${monthNamesUrdu.getOrElse(month - 1) { "ستمبر" }} $year"

        // Scores calculation
        val sabaqScore = if (totalCalendarDays > 0) minOf(100, maxOf(70, ((presentDays.toDouble() / totalCalendarDays) * 100).toInt() + 5)) else 92
        val sabqiScore = maxOf(60, minOf(100, 100 - (totalSabqiMistakes * 2)))
        val manzilScore = maxOf(60, minOf(100, 100 - (totalManzilMistakes * 3)))
        val overallAccuracy = ((sabqiScore + manzilScore) / 2)

        val report = MonthlyReportEntity(
            studentId = studentId,
            month = month,
            year = year,
            monthNameUrdu = monthNameUrdu,
            presentDays = presentDays,
            absentDays = absentDays,
            leaveDays = leaveDays,
            totalCalendarDays = totalCalendarDays,
            totalSabaqDays = totalSabaqDays,
            totalSabaqLines = totalSabaqLines,
            totalSabqiEntries = totalSabqiEntries,
            totalManzilEntries = totalManzilEntries,
            totalSabqiMistakes = totalSabqiMistakes,
            totalManzilMistakes = totalManzilMistakes,
            averagePerformance = avgPerf,
            sabaqScore = sabaqScore,
            sabqiScore = sabqiScore,
            manzilScore = manzilScore,
            overallAccuracy = overallAccuracy,
            generatedAt = System.currentTimeMillis()
        )

        dao.insertMonthlyReport(report)
        return report
    }

    // --- Institute & Teacher ---
    val institute: Flow<InstituteEntity?> = dao.getInstitute()
    suspend fun getInstituteDirect(): InstituteEntity? = dao.getInstituteDirect()
    suspend fun insertInstitute(institute: InstituteEntity) = dao.insertInstitute(institute)
    suspend fun updateInstitute(institute: InstituteEntity) = dao.updateInstitute(institute)

    val teacher: Flow<TeacherEntity?> = dao.getTeacher()
    suspend fun getTeacherDirect(): TeacherEntity? = dao.getTeacherDirect()
    suspend fun insertTeacher(teacher: TeacherEntity) = dao.insertTeacher(teacher)
    suspend fun updateTeacher(teacher: TeacherEntity) = dao.updateTeacher(teacher)

    // --- Settings ---
    val settings: Flow<AppSettingsEntity?> = dao.getSettings()
    suspend fun getSettingsDirect(): AppSettingsEntity? = dao.getSettingsDirect()
    suspend fun insertSettings(settings: AppSettingsEntity) = dao.insertSettings(settings)
    suspend fun updateSettings(settings: AppSettingsEntity) = dao.updateSettings(settings)

    suspend fun insertStudents(students: List<StudentEntity>) = dao.insertStudents(students)

    suspend fun getDailyReportsForStudentInMonth(studentId: Long, month: Int, year: Int): List<DailyReportEntity> {
        val monthPrefix = String.format(Locale.ENGLISH, "%04d-%02d", year, month)
        return dao.getDailyReportsForStudentAndMonth(studentId, monthPrefix)
    }

    // --- WhatsApp ---
    suspend fun logWhatsAppDelivery(delivery: WhatsAppDeliveryEntity): Long =
        dao.insertWhatsAppDelivery(delivery)

    val recentDeliveries: Flow<List<WhatsAppDeliveryEntity>> = dao.getRecentWhatsAppDeliveries()
    val successfulDeliveriesCount: Flow<Int> = dao.getSuccessfulDeliveriesCount()
    val totalDeliveriesCount: Flow<Int> = dao.getTotalDeliveriesCount()

    // --- Reset App Data ---
    suspend fun resetAllData() {
        dao.clearDailyReports()
        dao.clearMonthlyReports()
        dao.clearStudents()
        dao.clearTeachers()
        dao.clearInstitutes()
        dao.clearSettings()
        dao.clearWhatsAppDeliveries()
    }
}
