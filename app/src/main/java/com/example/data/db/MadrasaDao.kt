package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettingsEntity
import com.example.data.model.AttendanceEntity
import com.example.data.model.DailyReportEntity
import com.example.data.model.InstituteEntity
import com.example.data.model.MonthlyReportEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TeacherEntity
import com.example.data.model.WhatsAppDeliveryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MadrasaDao {

    // --- Students ---
    @Query("SELECT * FROM students WHERE isArchived = 0 ORDER BY id ASC")
    fun getActiveStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE isArchived = 0 ORDER BY id ASC")
    suspend fun getActiveStudentsDirect(): List<StudentEntity>

    @Query("SELECT * FROM students ORDER BY id ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudentById(id: Long): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getStudentByIdDirect(id: Long): StudentEntity?

    @Query("SELECT COUNT(*) FROM students WHERE isArchived = 0")
    fun getActiveStudentsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Query("UPDATE students SET isArchived = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDeleteStudent(id: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("""
        SELECT * FROM students 
        WHERE isArchived = 0 
        AND (name LIKE '%' || :query || '%' OR fatherName LIKE '%' || :query || '%' OR whatsappNumber LIKE '%' || :query || '%')
        ORDER BY id ASC
    """)
    fun searchStudents(query: String): Flow<List<StudentEntity>>

    // --- Daily Reports ---
    @Query("SELECT * FROM daily_reports ORDER BY date DESC, id DESC")
    fun getAllDailyReports(): Flow<List<DailyReportEntity>>

    @Query("SELECT * FROM daily_reports WHERE date = :date ORDER BY id DESC")
    fun getDailyReportsForDate(date: String): Flow<List<DailyReportEntity>>

    @Query("SELECT * FROM daily_reports WHERE date = :date")
    suspend fun getDailyReportsForDateDirect(date: String): List<DailyReportEntity>

    @Query("SELECT * FROM daily_reports WHERE studentId = :studentId ORDER BY date DESC, id DESC")
    fun getDailyReportsForStudent(studentId: Long): Flow<List<DailyReportEntity>>

    @Query("SELECT * FROM daily_reports WHERE studentId = :studentId AND date LIKE :yearMonthPrefix || '%' ORDER BY date ASC")
    suspend fun getDailyReportsForStudentAndMonth(studentId: Long, yearMonthPrefix: String): List<DailyReportEntity>

    @Query("SELECT * FROM daily_reports WHERE studentId = :studentId AND date = :date LIMIT 1")
    suspend fun getDailyReportForStudentAndDate(studentId: Long, date: String): DailyReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReport(report: DailyReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyReports(reports: List<DailyReportEntity>)

    @Update
    suspend fun updateDailyReport(report: DailyReportEntity)

    @Query("DELETE FROM daily_reports WHERE id = :id")
    suspend fun deleteDailyReport(id: Long)

    // --- Dynamic Today Stats Queries ---
    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date")
    fun getReportsCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date AND attendance = 'حاضر'")
    fun getPresentCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date AND attendance = 'غیر حاضر'")
    fun getAbsentCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date AND attendance = 'چھٹی'")
    fun getLeaveCountForDate(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(sabaqLines), 0) FROM daily_reports WHERE date = :date")
    fun getTotalSabaqLinesForDate(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(sabqiMistakes + manzilMistakes), 0) FROM daily_reports WHERE date = :date")
    fun getTotalMistakesForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date AND performance = 'بہترین'")
    fun getExcellentPerformanceCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date AND performance = 'اچھی'")
    fun getGoodPerformanceCountForDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_reports WHERE date = :date AND performance = 'مزید محنت درکار'")
    fun getNeedsRevisionCountForDate(date: String): Flow<Int>

    // --- Monthly Reports ---
    @Query("SELECT * FROM monthly_reports WHERE month = :month AND year = :year ORDER BY id DESC")
    fun getMonthlyReports(month: Int, year: Int): Flow<List<MonthlyReportEntity>>

    @Query("SELECT * FROM monthly_reports WHERE studentId = :studentId AND month = :month AND year = :year LIMIT 1")
    suspend fun getMonthlyReportForStudent(studentId: Long, month: Int, year: Int): MonthlyReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonthlyReport(report: MonthlyReportEntity): Long

    // --- Attendance ---
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    // --- Institute ---
    @Query("SELECT * FROM institutes WHERE id = 1 LIMIT 1")
    fun getInstitute(): Flow<InstituteEntity?>

    @Query("SELECT * FROM institutes WHERE id = 1 LIMIT 1")
    suspend fun getInstituteDirect(): InstituteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstitute(institute: InstituteEntity)

    @Update
    suspend fun updateInstitute(institute: InstituteEntity)

    // --- Teacher ---
    @Query("SELECT * FROM teachers WHERE id = 1 LIMIT 1")
    fun getTeacher(): Flow<TeacherEntity?>

    @Query("SELECT * FROM teachers WHERE id = 1 LIMIT 1")
    suspend fun getTeacherDirect(): TeacherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: TeacherEntity)

    @Update
    suspend fun updateTeacher(teacher: TeacherEntity)

    // --- Settings ---
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettingsEntity)

    @Update
    suspend fun updateSettings(settings: AppSettingsEntity)

    // --- WhatsApp Logs ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWhatsAppDelivery(delivery: WhatsAppDeliveryEntity): Long

    @Query("SELECT * FROM whatsapp_deliveries ORDER BY sentAt DESC LIMIT 50")
    fun getRecentWhatsAppDeliveries(): Flow<List<WhatsAppDeliveryEntity>>

    @Query("SELECT COUNT(*) FROM whatsapp_deliveries WHERE status = 'Sent'")
    fun getSuccessfulDeliveriesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM whatsapp_deliveries")
    fun getTotalDeliveriesCount(): Flow<Int>

    // --- Reset App (Wipe all data) ---
    @Query("DELETE FROM daily_reports")
    suspend fun clearDailyReports()

    @Query("DELETE FROM monthly_reports")
    suspend fun clearMonthlyReports()

    @Query("DELETE FROM students")
    suspend fun clearStudents()

    @Query("DELETE FROM teachers")
    suspend fun clearTeachers()

    @Query("DELETE FROM institutes")
    suspend fun clearInstitutes()

    @Query("DELETE FROM app_settings")
    suspend fun clearSettings()

    @Query("DELETE FROM whatsapp_deliveries")
    suspend fun clearWhatsAppDeliveries()
}
