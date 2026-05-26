package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Note
import com.example.data.NoteRepository
import com.example.sync.GoogleDriveSyncManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import kotlinx.coroutines.flow.combine

class NotesViewModel(
    application: Application,
    private val repository: NoteRepository
) : AndroidViewModel(application) {

    private val syncManager = GoogleDriveSyncManager(application, repository)
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val notes: StateFlow<List<Note>> = repository.allNotes
        .combine(_searchQuery) { notesList, query ->
            if (query.isBlank()) {
                notesList
            } else {
                notesList.filter { 
                    it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) 
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateNote(id: Int, title: String, content: String, textSize: Float, isBold: Boolean, isItalic: Boolean, fontFamily: String) {
        viewModelScope.launch {
            repository.insert(Note(id = id, title = title, content = content, textSize = textSize, isBold = isBold, isItalic = isItalic, fontFamily = fontFamily))
        }
    }

    fun addNote(title: String, content: String, textSize: Float, isBold: Boolean, isItalic: Boolean, fontFamily: String) {
        viewModelScope.launch {
            repository.insert(Note(title = title, content = content, textSize = textSize, isBold = isBold, isItalic = isItalic, fontFamily = fontFamily))
        }
    }

    fun deleteNote(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun syncNotes(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                syncManager.syncNotes(account)
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Sync failed", e)
            } finally {
                _isSyncing.value = false
            }
        }
    }
}

class NotesViewModelFactory(
    private val application: Application,
    private val repository: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
