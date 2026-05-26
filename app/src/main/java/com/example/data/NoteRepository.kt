package com.example.data

import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao, private val reminderDao: ReminderDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAllNotes()
    val allReminders: Flow<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun insert(note: Note) = noteDao.insertNote(note)

    suspend fun deleteById(id: Int) = noteDao.deleteNoteById(id)
    
    suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insert(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.update(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.delete(reminder)
}
