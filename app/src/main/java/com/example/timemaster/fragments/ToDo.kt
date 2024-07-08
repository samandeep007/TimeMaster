package com.example.timemaster.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemaster.MainActivity
import com.example.timemaster.R
import com.example.timemaster.adapter.MyNotesAdapter
import com.example.timemaster.adapter.MyTodoAdapter
import com.example.timemaster.databinding.FragmentToDoBinding
import com.example.timemaster.model.NoteListModel
import com.example.timemaster.model.ToDoListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ToDo : Fragment() {

    lateinit var binding: FragmentToDoBinding
    lateinit var todoAdapter: MyTodoAdapter
    lateinit var noteAdapter: MyNotesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).binding.bottomMenuBar.menu.getItem(3).isChecked = true
        binding = FragmentToDoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //   Todoo RecyclerView


        val sharedPreferences =
            requireActivity().getSharedPreferences("TODO", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("todoArray", "null")
        val type: Type = object : TypeToken<ArrayList<ToDoListModel?>?>() {}.type
        var todoArray = gson.fromJson(json, type) as? ArrayList<ToDoListModel>

        if (todoArray == null) {
            todoArray = ArrayList()
        }

        todoAdapter = MyTodoAdapter(todoArray, object : MyTodoAdapter.onTodo {
            override fun onEditTodo(toDoListModel: ToDoListModel, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val todoView = layoutInflater.inflate(R.layout.add_my_todo_list, null)
                alertDialog.setView(todoView)
                alertDialog.show()
                val todoTitle = todoView.findViewById<EditText>(R.id.edtTodoTitle)
                val todoDescription = todoView.findViewById<EditText>(R.id.edtTodoDescription)
                val todoAddButton = todoView.findViewById<Button>(R.id.btnAddTodo)
                val todoCancelButton = todoView.findViewById<Button>(R.id.btnCancelTodo)

                todoTitle.setText(toDoListModel.todoName)
                todoDescription.setText(toDoListModel.toDoDescription)
                todoAddButton.text = "Update"
                todoAddButton.setOnClickListener {
                    if (todoTitle.text.isNotEmpty() && todoDescription.text.isNotEmpty()) {
                        if (todoTitle.text.toString() != toDoListModel.todoName || todoDescription.text.toString() != toDoListModel.toDoDescription) {
                            val sharedPreferencess =
                                requireActivity().getSharedPreferences("TODO", Context.MODE_PRIVATE)
                            val editors = sharedPreferencess.edit()
                            val gSon = Gson()
                            todoArray.removeAt(position)
                            todoArray?.add(
                                position,
                                ToDoListModel(
                                    todoTitle.text.toString(),
                                    todoDescription.text.toString()
                                )
                            )
                            val jSon: String = gSon.toJson(todoArray)
                            editors.putString("todoArray", jSon)
                            editors.apply()
                            todoAdapter.notifyDataSetChanged()
                            alertDialog.dismiss()
                        } else {
                            alertDialog.dismiss()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Provide Proper details !!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                todoCancelButton.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            override fun onDeleteTodo(position: Int) {
                todoArray.removeAt(position)
                val sharedPreferencess =
                    requireActivity().getSharedPreferences("TODO", Context.MODE_PRIVATE)
                val editors = sharedPreferencess.edit()
                val gSon = Gson()
                val jSon: String = gSon.toJson(todoArray)
                editors.putString("todoArray", jSon)
                editors.apply()
                todoAdapter.notifyDataSetChanged()
            }
        })



        binding.recyclerViewMyTodoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewMyTodoList.adapter = todoAdapter

        binding.btnMyToDo.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).create()
            val todoView = layoutInflater.inflate(R.layout.add_my_todo_list, null)
            alertDialog.setView(todoView)
            alertDialog.show()
            val todoTitle = todoView.findViewById<EditText>(R.id.edtTodoTitle)
            val todoDescription = todoView.findViewById<EditText>(R.id.edtTodoDescription)
            val todoAddButton = todoView.findViewById<Button>(R.id.btnAddTodo)
            val todoCancelButton = todoView.findViewById<Button>(R.id.btnCancelTodo)

            todoAddButton.setOnClickListener {
                if (todoTitle.text.isNotEmpty() && todoDescription.text.isNotEmpty()) {
                    val sharedPreferencess =
                        requireActivity().getSharedPreferences("TODO", Context.MODE_PRIVATE)
                    val editors = sharedPreferencess.edit()
                    val gSon = Gson()
                    todoArray?.add(
                        ToDoListModel(
                            todoTitle.text.toString(),
                            todoDescription.text.toString()
                        )
                    )
                    val jSon: String = gSon.toJson(todoArray)
                    editors.putString("todoArray", jSon)
                    editors.apply()
                    todoAdapter.notifyDataSetChanged()

                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Provide Proper details !!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            todoCancelButton.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        // Notes RecyclerView
        val notesharedPreferences =
            requireActivity().getSharedPreferences("Note", Context.MODE_PRIVATE)
        val notegson = Gson()
        val notejson = notesharedPreferences.getString("noteArray", "null")
        val notetype: Type = object : TypeToken<ArrayList<NoteListModel?>?>() {}.type
        var noteArray = notegson.fromJson(notejson, notetype) as? ArrayList<NoteListModel>

        if (noteArray == null) {
            noteArray = ArrayList()
        }

        noteAdapter = MyNotesAdapter(noteArray, object : MyNotesAdapter.onCLickedNote {
            override fun onEditNote(noteListModel: NoteListModel, position: Int) {

                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val noteView = layoutInflater.inflate(R.layout.add_my_notes, null)
                alertDialog.setView(noteView)
                alertDialog.show()
                val noteTitle = noteView.findViewById<EditText>(R.id.edtNoteTitle)
                val noteDescription = noteView.findViewById<EditText>(R.id.edtNoteDescription)
                val noteAddButton = noteView.findViewById<Button>(R.id.btnAddNote)
                val noteCancelButton = noteView.findViewById<Button>(R.id.btnCancelNote)

                noteTitle.setText(noteListModel.noteTitle)
                noteDescription.setText(noteListModel.noteDescription)
                noteAddButton.text = "Update"

                noteAddButton.setOnClickListener {
                    if (noteTitle.text.isNotEmpty() && noteDescription.text.isNotEmpty()) {
                        if (noteTitle.text.toString() != noteListModel.noteTitle || noteDescription.text.toString() != noteListModel.noteDescription) {
                            val sharedPreferencess =
                                requireActivity().getSharedPreferences("Note", Context.MODE_PRIVATE)
                            val editors = sharedPreferencess.edit()
                            val gSon = Gson()
                            noteArray.removeAt(position)
                            noteArray?.add(
                                position,
                                NoteListModel(
                                    noteTitle.text.toString(),
                                    noteDescription.text.toString()
                                )
                            )
                            val jSon: String = gSon.toJson(noteArray)
                            editors.putString("noteArray", jSon)
                            editors.apply()
                            noteAdapter.notifyDataSetChanged()

                            alertDialog.dismiss()
                        } else {
                            alertDialog.dismiss()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please provide proper details!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                noteCancelButton.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            override fun onDeleteNote(position: Int) {
                noteArray.removeAt(position)
                val sharedPreferencess =
                    requireActivity().getSharedPreferences("Note", Context.MODE_PRIVATE)
                val editors = sharedPreferencess.edit()
                val gSon = Gson()
                val jSon: String = gSon.toJson(noteArray)
                editors.putString("noteArray", jSon)
                editors.apply()
                noteAdapter.notifyDataSetChanged()
            }
        })
        binding.recyclerViewMyNotes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewMyNotes.adapter = noteAdapter


        binding.btnMyNotes.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).create()
            val noteView = layoutInflater.inflate(R.layout.add_my_notes, null)
            alertDialog.setView(noteView)
            alertDialog.show()
            val noteTitle = noteView.findViewById<EditText>(R.id.edtNoteTitle)
            val noteDescription = noteView.findViewById<EditText>(R.id.edtNoteDescription)
            val noteAddButton = noteView.findViewById<Button>(R.id.btnAddNote)
            val noteCancelButton = noteView.findViewById<Button>(R.id.btnCancelNote)

            noteAddButton.setOnClickListener {
                if (noteTitle.text.isNotEmpty() && noteDescription.text.isNotEmpty()) {
                    saveNoteList(noteArray, noteTitle, noteDescription)
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please provide proper details!!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            noteCancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

        }
    }

    fun saveNoteList(array: ArrayList<NoteListModel>, title: EditText, desc: EditText) {
        val sharedPreferencess =
            requireActivity().getSharedPreferences("Note", Context.MODE_PRIVATE)
        val editors = sharedPreferencess.edit()
        val gSon = Gson()
        array?.add(
            NoteListModel(
                title.text.toString(),
                desc.text.toString()
            )
        )
        val jSon: String = gSon.toJson(array)
        editors.putString("noteArray", jSon)
        editors.apply()
        noteAdapter.notifyDataSetChanged()

    }

}