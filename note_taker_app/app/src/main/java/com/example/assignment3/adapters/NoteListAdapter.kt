package com.example.assignment3.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment3.R
import com.example.assignment3.listeners.NoteClickListener
import com.example.assignment3.models.Notes

class NotesListAdapter(
    private val context: Context,
    private var list: MutableList<Notes>,
    private val noteClickListener: NoteClickListener
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false)
        return NotesViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val item = list[position]

        holder.textViewTitle.text = item.title
        holder.textViewTitle.isSelected = true

        holder.textViewNotes.text = item.notes
        holder.textViewDate.text = item.date
        holder.textViewDate.isSelected = true

        if (item.isStarred) {
            holder.imageViewPin.setImageResource(R.drawable.ic_pin)
        } else {
            holder.imageViewPin.setImageDrawable(null)
        }

        // Apply card color based on note's color
        val colorName = item.color_code
        val colorResId = if (colorName.isNotBlank())
            context.resources.getIdentifier(colorName, "color", context.packageName)
        else 0

        val fallbackWhite = ContextCompat.getColor(context, R.color.white)
        val cardColor = if (colorResId != 0) ContextCompat.getColor(context, colorResId) else fallbackWhite
        holder.notesContainer.setCardBackgroundColor(cardColor)

        holder.notesContainer.setOnClickListener {
            noteClickListener.onClick(item)
        }

        holder.notesContainer.setOnLongClickListener {
            noteClickListener.onLongClick(item, holder.notesContainer)
            true
        }
    }

    override fun getItemCount(): Int = list.size

    fun filterList(filteredList: MutableList<Notes>, @Suppress("UNUSED_PARAMETER") newText: String?) {
        list = filteredList
        notifyDataSetChanged()
    }
}

class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val notesContainer: CardView = itemView.findViewById(R.id.notes_container)
    val textViewTitle: TextView = itemView.findViewById(R.id.textView_title)
    val textViewNotes: TextView = itemView.findViewById(R.id.textView_notes)
    val textViewDate: TextView = itemView.findViewById(R.id.textView_date)
    val imageViewPin: ImageView = itemView.findViewById(R.id.imageView_pin)
}
