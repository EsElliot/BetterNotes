package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.NoteRepository
import androidx.compose.runtime.getValue
import com.example.ui.NotesScreen
import com.example.ui.NotesViewModel
import com.example.ui.NotesViewModelFactory
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.ThemeMode
import com.example.ui.ThemeViewModel

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "notes-db"
        ).fallbackToDestructiveMigration().build()
    }
    
    private val repository by lazy {
        NoteRepository(db.noteDao(), db.reminderDao())
    }

    private val viewModel: NotesViewModel by viewModels {
        NotesViewModelFactory(application, repository)
    }

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                else -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NotesScreen(viewModel = viewModel, themeViewModel = themeViewModel)
                }
            }
        }
    }
}
