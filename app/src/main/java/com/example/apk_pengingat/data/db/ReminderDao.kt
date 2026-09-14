package com.example.apk_pengingat.data.db

import androidx.room.*
import com.example.apk_pengingat.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY expiryDate ASC")
    fun getActiveReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 ORDER BY expiryDate ASC LIMIT :limit")
    suspend fun getUniqueActiveReminders(limit: Int = 3): List<Reminder>

    @Query("SELECT * FROM reminders WHERE isCompleted = 1 ORDER BY expiryDate DESC")
    fun getCompletedReminders(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("UPDATE reminders SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Long)

    @Query("SELECT * FROM reminders WHERE isCompleted = 0 AND expiryDate <= :date ORDER BY expiryDate ASC")
    fun getRemindersDueBefore(date: String): Flow<List<Reminder>>
}
