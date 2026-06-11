package com.example.assignment3

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.assignment3.models.Notes
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class NoteTakerActivity : AppCompatActivity() {

    private lateinit var toolbarNotes: Toolbar
    private lateinit var buttonBackPill: MaterialButton
    private lateinit var buttonSavePill: MaterialButton
    private lateinit var editTextTitle: EditText
    private lateinit var editTextNotes: EditText

    private var note: Notes = Notes()
    private var isOldNote = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_taker)

        toolbarNotes   = findViewById(R.id.toolbar_notes)
        editTextTitle  = findViewById(R.id.editText_title)
        editTextNotes  = findViewById(R.id.editText_notes)
        buttonBackPill = findViewById(R.id.button_back_pill)
        buttonSavePill = findViewById(R.id.button_save_pill)

        // Editing existing note
        intent.getParcelableExtra<Notes>("old_note")?.let {
            note = it
            editTextTitle.setText(note.title)
            editTextNotes.setText(note.notes)
            isOldNote = true
        }

        if (!isOldNote) {
            note.color_code = randomColorName()
        }

        buttonBackPill.setOnClickListener { finish() }

        buttonSavePill.setOnClickListener {
            val title = editTextTitle.text.toString()
            val details = editTextNotes.text.toString()

            if (details.isBlank()) {
                Toast.makeText(this, "Please add a note!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val format = SimpleDateFormat("EEE, d MMM yyyy HH:mm a", Locale.getDefault()) // keep the random color we set above

            note.title = title
            note.notes = details
            note.date = format.format(Date())

            val data = Intent().putExtra("note", note)
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun randomColorName(): String {
        val colors = listOf("Vanilla", "Olivine", "cambridgeBlue", "Khaki", "Lilac", "Platinum")
        return colors[Random.nextInt(colors.size)]
    }
}
