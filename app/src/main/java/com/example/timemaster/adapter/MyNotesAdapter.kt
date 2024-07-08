package com.example.timemaster.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.timemaster.R
import com.example.timemaster.model.NoteListModel

class MyNotesAdapter(val list: ArrayList<NoteListModel>, val clicked : onCLickedNote) :
    RecyclerView.Adapter<MyNotesAdapter.myNotesViewHolder>() {
    class myNotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val noteTitle = view.findViewById<TextView>(R.id.txtNoteTitle)
        val noteDescription = view.findViewById<TextView>(R.id.txtNoteDetails)
        val editNote = view.findViewById<ImageView>(R.id.imgEditNote)
        val deleteNote = view.findViewById<ImageView>(R.id.imgDeleteNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myNotesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_notes, parent, false)
        return myNotesViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: myNotesViewHolder, position: Int) {
        holder.noteTitle.text = list[position].noteTitle
        holder.noteDescription.text = list[position].noteDescription

        holder.editNote.setOnClickListener {
            clicked.onEditNote(list[position], position)
        }
        holder.deleteNote.setOnClickListener {
            clicked.onDeleteNote(position)
        }

    }
    interface onCLickedNote{
        fun onEditNote(noteListModel: NoteListModel, position: Int)
        fun onDeleteNote(position: Int)
    }
}