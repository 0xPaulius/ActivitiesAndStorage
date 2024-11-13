package com.example.activitiesandstorage

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class DeleteNote : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DeleteNote", "onCreate called")
        dbHelper = DatabaseHelper(this)

        setContent {
            MaterialTheme {
                DeleteNoteScreen(
                    dbHelper = dbHelper,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteNoteScreen(
    dbHelper: DatabaseHelper,
    onBack: () -> Unit
) {
    var notes by remember { mutableStateOf(listOf<Note>()) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    LaunchedEffect(Unit) {
        notes = dbHelper.getAllNotes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delete Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (notes.isEmpty()) {
                Text("No notes available to delete")
            } else {
                notes.forEach { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = note == selectedNote,
                            onClick = { selectedNote = note }
                        )
                        Text(
                            text = note.name,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedNote?.let { note ->
                            dbHelper.deleteNote(note.id)
                            onBack()
                        }
                    },
                    enabled = selectedNote != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Selected Note")
                }
            }
        }
    }
}