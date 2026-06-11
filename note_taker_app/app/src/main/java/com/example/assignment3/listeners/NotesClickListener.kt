package com.example.assignment3.listeners

import androidx.cardview.widget.CardView
import com.example.assignment3.models.Notes

interface NoteClickListener {
    fun onClick(notes: Notes)
    fun onLongClick(notes: Notes, cardView: CardView)
}
