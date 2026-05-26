package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class, Reminder::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun reminderDao(): ReminderDao
}
