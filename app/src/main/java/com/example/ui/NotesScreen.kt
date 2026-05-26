package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Notifications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var showAddNoteSheet by remember { mutableStateOf(false) }
    var noteToEdit by remember { mutableStateOf<Note?>(null) }
    var currentRoute by remember { mutableStateOf("notes") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Заметки") },
                    label = { Text("Заметки") },
                    selected = currentRoute == "notes",
                    onClick = { currentRoute = "notes" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Notifications, contentDescription = "Напоминания") },
                    label = { Text("Напоминания") },
                    selected = currentRoute == "reminders",
                    onClick = { currentRoute = "reminders" }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Menu, contentDescription = "Меню") },
                    label = { Text("Меню") },
                    selected = currentRoute == "menu",
                    onClick = { currentRoute = "menu" }
                )
            }
        },
        topBar = {
            if (currentRoute == "notes") {
                TopAppBar(
                    title = { 
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        ) 
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    )
                )
            } else if (currentRoute == "reminders") {
                TopAppBar(title = { Text("Напоминания") })
            } else {
                TopAppBar(title = { Text("Меню") })
            }
        },
        floatingActionButton = {
            if (currentRoute == "notes") {
                FloatingActionButton(
                    onClick = { 
                        noteToEdit = null
                        showAddNoteSheet = true 
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_note))
                }
            }
        }
    ) { innerPadding ->
        if (currentRoute == "notes") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Поиск заметок") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Поиск") },
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    )
                )
                
                if (notes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.empty_notes),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize().weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalItemSpacing = 12.dp,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(notes, key = { _, note -> note.id }) { index, note ->
                            NoteItem(
                                note = note,
                                index = index,
                                onClick = {
                                    noteToEdit = note
                                    showAddNoteSheet = true
                                },
                                onDeleteClick = { viewModel.deleteNote(note.id) }
                            )
                        }
                    }
                }
            }
        } else if (currentRoute == "reminders") {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Раздел в разработке (Напоминания)")
            }
        } else if (currentRoute == "menu") {
            Box(modifier = Modifier.padding(innerPadding)) {
                SettingsBottomSheet(viewModel = viewModel, themeViewModel = themeViewModel, onDismiss = {})
            }
        }
    }
        
    if (showAddNoteSheet) {
        Dialog(
            onDismissRequest = { showAddNoteSheet = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AddNoteScreen(
                note = noteToEdit,
                onDismiss = { showAddNoteSheet = false },
                onSave = { id, title, content, textSize, isBold, isItalic, fontFamily ->
                    if (id != null) {
                        viewModel.updateNote(id, title, content, textSize, isBold, isItalic, fontFamily)
                    } else {
                        viewModel.addNote(title, content, textSize, isBold, isItalic, fontFamily)
                    }
                    showAddNoteSheet = false
                }
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    index: Int,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOutline = index % 5 == 1
    val containerColor = when (index % 5) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> Color.Transparent
        2 -> MaterialTheme.colorScheme.secondaryContainer
        3 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when (index % 5) {
        0 -> MaterialTheme.colorScheme.onPrimaryContainer
        1 -> MaterialTheme.colorScheme.onSurface
        2 -> MaterialTheme.colorScheme.onSecondaryContainer
        3 -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = if (isOutline) BorderStroke(1.dp, MaterialTheme.colorScheme.outline) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val selectedFontFamily = when(note.fontFamily) {
                "Serif" -> FontFamily.Serif
                "Monospace" -> FontFamily.Monospace
                else -> FontFamily.SansSerif
            }
            Text(
                text = note.content,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = note.textSize.sp,
                    fontWeight = if (note.isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (note.isItalic) FontStyle.Italic else FontStyle.Normal,
                    fontFamily = selectedFontFamily,
                    color = contentColor.copy(alpha = 0.8f)
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(note.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    viewModel: NotesViewModel,
    themeViewModel: ThemeViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()
    var showAboutDialog by remember { mutableStateOf(false) }

    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("О приложении") },
            text = {
                Column {
                    Text("BetterNotes", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Версия 1.1")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val annotatedText = androidx.compose.ui.text.buildAnnotatedString {
                        append("Создано ")
                        pushStringAnnotation(tag = "URL", annotation = "https://github.com/EsElliot/BetterNotes")
                        withStyle(style = androidx.compose.ui.text.SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        )) {
                            append("EsElliot")
                        }
                        pop()
                        append(" с помощью инструмента Gemini 3.1 Pro")
                    }
                    androidx.compose.foundation.text.ClickableText(
                        text = annotatedText,
                        style = androidx.compose.ui.text.TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        ),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    uriHandler.openUri(annotation.item)
                                }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("ОК") }
            }
        )
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                if (account != null) {
                    viewModel.syncNotes(account)
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 48.dp)
        ) {
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text("Тема оформления", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                FilterChip(selected = themeMode == ThemeMode.SYSTEM, onClick = { themeViewModel.setThemeMode(ThemeMode.SYSTEM) }, label = { Text("Системная") })
                FilterChip(selected = themeMode == ThemeMode.LIGHT, onClick = { themeViewModel.setThemeMode(ThemeMode.LIGHT) }, label = { Text("Светлая") })
                FilterChip(selected = themeMode == ThemeMode.DARK, onClick = { themeViewModel.setThemeMode(ThemeMode.DARK) }, label = { Text("Темная") })
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Button(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(Scope("https://www.googleapis.com/auth/drive.appdata"))
                        .build()
                    val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(mGoogleSignInClient.signInIntent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = !isSyncing
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Синхронизация...", modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Text("Синхронизация с Google Drive", modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            TextButton(
                onClick = { showAboutDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("О приложении")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    note: Note?,
    onDismiss: () -> Unit,
    onSave: (Int?, String, String, Float, Boolean, Boolean, String) -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var textSize by remember { mutableStateOf(note?.textSize ?: 16f) }
    var isBold by remember { mutableStateOf(note?.isBold ?: false) }
    var isItalic by remember { mutableStateOf(note?.isItalic ?: false) }
    var fontFamily by remember { mutableStateOf(note?.fontFamily ?: "SansSerif") }

    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_note)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = android.content.Intent(android.provider.AlarmClock.ACTION_SET_ALARM).apply {
                            putExtra(android.provider.AlarmClock.EXTRA_MESSAGE, title.ifEmpty { "Напоминание (BetterNotes)" })
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Напоминание")
                    }
                    IconButton(onClick = {
                        if (title.isNotBlank() || content.isNotBlank()) {
                            onSave(note?.id, title.ifBlank { "Без названия" }, content, textSize, isBold, isItalic, fontFamily)
                        }
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = "Сохранить")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { if (textSize > 12f) textSize -= 2f }) {
                    Text("-", style = MaterialTheme.typography.titleLarge)
                }
                Text("${textSize.toInt()}", style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { if (textSize < 32f) textSize += 2f }) {
                    Text("+", style = MaterialTheme.typography.titleLarge)
                }
                
                VerticalDivider(modifier = Modifier.height(24.dp))
                
                IconToggleButton(checked = isBold, onCheckedChange = { isBold = it }) {
                    Text("B", fontWeight = FontWeight.Bold, color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
                IconToggleButton(checked = isItalic, onCheckedChange = { isItalic = it }) {
                    Text("I", fontStyle = FontStyle.Italic, color = if (isItalic) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
                
                VerticalDivider(modifier = Modifier.height(24.dp))
                
                IconButton(onClick = { content += "\n- [ ] " }) {
                    Text("[ ]", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    TextButton(onClick = { expanded = true }) {
                        Text(when(fontFamily) {
                            "Serif" -> "Serif"
                            "Monospace" -> "Mono"
                            else -> "Sans"
                        })
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text("SansSerif") }, onClick = { fontFamily = "SansSerif"; expanded = false })
                        DropdownMenuItem(text = { Text("Serif") }, onClick = { fontFamily = "Serif"; expanded = false })
                        DropdownMenuItem(text = { Text("Monospace") }, onClick = { fontFamily = "Monospace"; expanded = false })
                    }
                }
            }
            
            HorizontalDivider()

            val selectedFontFamily = when(fontFamily) {
                "Serif" -> FontFamily.Serif
                "Monospace" -> FontFamily.Monospace
                else -> FontFamily.SansSerif
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(stringResource(R.string.title_hint)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.titleLarge
            )
            
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                placeholder = { Text(stringResource(R.string.content_hint)) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = textSize.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                    fontFamily = selectedFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}
