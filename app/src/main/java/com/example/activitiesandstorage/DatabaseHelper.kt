package com.example.activitiesandstorage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "NotesDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NOTES = "notes"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_CONTENT TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
        Log.d("DatabaseHelper", "Database created")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun addNote(note: Note): Long {
        Log.d("DatabaseHelper", "Adding note: ${note.name}")
        val values = ContentValues().apply {
            put(COLUMN_NAME, note.name)
            put(COLUMN_CONTENT, note.content)
        }
        return writableDatabase.insert(TABLE_NOTES, null, values).also {
            Log.d("DatabaseHelper", "Note added with id: $it")
        }
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val selectQuery = "SELECT * FROM $TABLE_NOTES ORDER BY $COLUMN_ID DESC"

        writableDatabase.rawQuery(selectQuery, null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                notes.add(Note(name = name, content = content, id = id))
            }
        }
        Log.d("DatabaseHelper", "Retrieved ${notes.size} notes")
        return notes
    }

    fun deleteNote(id: Long) {
        Log.d("DatabaseHelper", "Deleting note with id: $id")
        writableDatabase.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}