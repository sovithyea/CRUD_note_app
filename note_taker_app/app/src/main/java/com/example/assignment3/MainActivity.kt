package com.example.assignment3

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.assignment3.adapters.NotesListAdapter
import com.example.assignment3.database.RoomDB
import com.example.assignment3.listeners.NoteClickListener
import com.example.assignment3.models.Notes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notesListAdapter: NotesListAdapter
    private lateinit var toolbar: Toolbar
    private val notes: MutableList<Notes> = mutableListOf()
    private lateinit var database: RoomDB
    private lateinit var fabAdd: FloatingActionButton
    private var selectedNote: Notes? = null
    private lateinit var searchViewHome: SearchView
    private lateinit var textViewPlaceholder: TextView

    // Track whether we're showing only pinned
    private var showingPinnedOnly: Boolean = false

    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        const val LAYOUT = "layout"
    }

    // Activity Result API launchers

    private val addNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val note = result.data?.getParcelableExtra<Notes>("note") ?: return@registerForActivityResult
            lifecycleScope.launch {
                withContext(Dispatchers.IO) { database.mainDAO().insert(note) }
                refreshNotes()
                Toast.makeText(this@MainActivity, "Saved!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show()
        }
    }

    private val editNoteLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val note = result.data?.getParcelableExtra<Notes>("note") ?: return@registerForActivityResult
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    database.mainDAO().update(note.id, note.title, note.notes)
                }
                refreshNotes()
                Toast.makeText(this@MainActivity, "Updated!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Cancelled!", Toast.LENGTH_SHORT).show()
        }
    }

    // Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        recyclerView = findViewById(R.id.recycler_view)
        toolbar = findViewById(R.id.toolbar_home)
        fabAdd = findViewById(R.id.fab_add)
        searchViewHome = findViewById(R.id.search_view_home)
        textViewPlaceholder = findViewById(R.id.textView_placeholder)

        toolbar.inflateMenu(R.menu.home_menu)

        database = RoomDB.getInstance(this)

        // Load notes off the main thread
        lifecycleScope.launch { refreshNotes() }

        updateRecycler(loadLayoutStyle())

        fabAdd.setOnClickListener {
            val intent = Intent(this@MainActivity, NoteTakerActivity::class.java)
            addNoteLauncher.launch(intent)
        }

        searchViewHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText.orEmpty())
                return true
            }
        })

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.layout -> {
                    if (loadLayoutStyle() == "linear") saveLayoutStyle("grid") else saveLayoutStyle("linear")
                    updateRecycler(loadLayoutStyle())
                    Toast.makeText(this@MainActivity, "Layout updated!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.pinned -> {
                    togglePinnedView()
                    true
                }
                else -> false
            }
        }
    }

    // Helpers

    private suspend fun refreshNotes() {
        val latest = withContext(Dispatchers.IO) { database.mainDAO().getAll() }
        notes.clear()
        notes.addAll(latest)

        // Re-apply current view (all vs pinned) after refresh
        if (showingPinnedOnly) {
            showPinnedInAdapter()
        } else {
            showAllInAdapter()
        }
    }

    private fun saveLayoutStyle(layout: String) {
        val prefs: SharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        prefs.edit { putString(LAYOUT, layout) }
    }

    private fun loadLayoutStyle(): String {
        val prefs: SharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)
        return prefs.getString(LAYOUT, "linear").orEmpty()
    }

    private fun updateRecycler(layout: String) {
        textViewPlaceholder.visibility = if (notes.isNotEmpty()) View.GONE else View.VISIBLE
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager =
            if (layout == "linear") LinearLayoutManager(this)
            else StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        notesListAdapter = NotesListAdapter(this, notes.toMutableList(), noteClickListener)
        recyclerView.adapter = notesListAdapter
    }

    private fun filter(newText: String) {
        val baseList = if (showingPinnedOnly) notes.filter { it.isStarred } else notes
        val filteredList = baseList.filter { n ->
            n.title.contains(newText, ignoreCase = true) ||
                    n.notes.contains(newText, ignoreCase = true)
        }.toMutableList()
        notesListAdapter.filterList(filteredList, newText)
        textViewPlaceholder.visibility = if (filteredList.isEmpty()) View.VISIBLE else View.GONE
    }

    // Pinned view toggle

    private fun togglePinnedView() {
        if (showingPinnedOnly) {
            // back to all
            showingPinnedOnly = false
            showAllInAdapter()
            Toast.makeText(this, "Showing all notes", Toast.LENGTH_SHORT).show()
        } else {
            // go to pinned
            val pinned = notes.filter { it.isStarred }.toMutableList()
            if (pinned.isEmpty()) {
                Toast.makeText(this, "No pinned notes!", Toast.LENGTH_SHORT).show()
                return
            }
            showingPinnedOnly = true
            notesListAdapter.filterList(pinned, null)
            textViewPlaceholder.visibility = View.GONE
            Toast.makeText(this, "Showing pinned notes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAllInAdapter() {
        notesListAdapter.filterList(notes.toMutableList(), null)
        textViewPlaceholder.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showPinnedInAdapter() {
        val pinned = notes.filter { it.isStarred }.toMutableList()
        if (pinned.isEmpty()) {
            // If none after a refresh (e.g., unpinned last one), fall back to all
            showingPinnedOnly = false
            showAllInAdapter()
            Toast.makeText(this, "No pinned notes!", Toast.LENGTH_SHORT).show()
        } else {
            notesListAdapter.filterList(pinned, null)
            textViewPlaceholder.visibility = View.GONE
        }
    }

    // Clicks / Popup

    private val noteClickListener: NoteClickListener = object : NoteClickListener {
        override fun onClick(notes: Notes) {
            val intent = Intent(this@MainActivity, NoteTakerActivity::class.java)
            intent.putExtra("old_note", notes)
            editNoteLauncher.launch(intent)
        }
        override fun onLongClick(notes: Notes, cardView: CardView) {
            selectedNote = notes
            showPopup(cardView)
        }
    }

    fun showPopup(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.setOnMenuItemClickListener(this)
        popupMenu.inflate(R.menu.popup_menu)
        popupMenu.show()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        val noteToActOn = selectedNote ?: return false
        return when (menuItem.itemId) {
            R.id.pin -> {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        database.mainDAO().pin(noteToActOn.id, !noteToActOn.isStarred)
                    }
                    refreshNotes()
                    Toast.makeText(
                        this@MainActivity,
                        if (!noteToActOn.isStarred) "Pinned!" else "Unpinned!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }
            R.id.delete -> {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete note?")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Delete") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) { database.mainDAO().delete(noteToActOn) }
                            refreshNotes()
                            Toast.makeText(this@MainActivity, "Deleted!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            else -> false
        }
    }
}
