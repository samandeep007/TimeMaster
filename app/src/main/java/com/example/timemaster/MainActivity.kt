package com.example.timemaster

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils.replace
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.timemaster.adapter.MyNotesAdapter
import com.example.timemaster.adapter.MyTodoAdapter
import com.example.timemaster.adapter.UpcomingEventsAdapter
import com.example.timemaster.databinding.ActivityMainBinding
import com.example.timemaster.fragments.Events
import com.example.timemaster.fragments.Home
import com.example.timemaster.fragments.Setting
import com.example.timemaster.fragments.ToDo
import com.example.timemaster.model.EventListModel
import com.example.timemaster.model.NoteListModel
import com.example.timemaster.model.ToDoListModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.lang.reflect.Type
import java.util.*

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    lateinit var binding: ActivityMainBinding
    private var isFabOpen = false
    lateinit var eventAdapter: UpcomingEventsAdapter
    lateinit var todoAdapter: MyTodoAdapter
    lateinit var noteAdapter: MyNotesAdapter

    var lastSelectedYear = 0
    var lastSelectedMonth = 0
    var lastSelectedDayOfMonth = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        binding.bottomMenuBar.menu.getItem(2).isEnabled = false


        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, Home())
            commit()
        }

        val sharedPreferences = getSharedPreferences("Register", MODE_PRIVATE)
        val name = sharedPreferences.getString("user", "")

        binding.txtUserName.text = name

        binding.bottomMenuBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menuHome -> {
                    setFragment(Home())
                    true
                }
                R.id.menuEvents -> {
                    setFragment(Events())
                    true
                }
                R.id.menuToDo -> {
                    setFragment(ToDo())
                    true
                }
                R.id.menuSetting -> {
                    setFragment(Setting())
                    true
                }
                else -> false
            }
        }
        val fbtn2 = findViewById<FloatingActionButton>(R.id.fbtn2)
        val fbtn3 = findViewById<FloatingActionButton>(R.id.fbtn3)
        val fbtn4 = findViewById<FloatingActionButton>(R.id.fbtn4)
        val layoutManu = findViewById<ConstraintLayout>(R.id.layoutMenu)

        layoutManu.setBackgroundResource(0)
        fbtn2.hide()
        fbtn3.hide()
        fbtn4.hide()


        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            manageMenu()
        }

    }

    private fun manageMenu() {
        val fbtn2 = findViewById<FloatingActionButton>(R.id.fbtn2)
        val fbtn3 = findViewById<FloatingActionButton>(R.id.fbtn3)
        val fbtn4 = findViewById<FloatingActionButton>(R.id.fbtn4)
        val layoutManu = findViewById<ConstraintLayout>(R.id.layoutMenu)

        if (isFabOpen) {
            isFabOpen = false
            layoutManu.setBackgroundResource(0)
            fbtn2.hide()
            fbtn3.hide()
            fbtn4.hide()
        } else {
            isFabOpen = true
            layoutManu.setBackgroundResource(R.drawable.shape_top_corner)
            fbtn2.show()
            fbtn3.show()
            fbtn4.show()
        }

        fbtn4.setOnClickListener {
            addEvent()
            layoutManu.setBackgroundResource(0)
            fbtn2.hide()
            fbtn3.hide()
            fbtn4.hide()
        }
        fbtn3.setOnClickListener {
            addTodo()
            layoutManu.setBackgroundResource(0)
            fbtn2.hide()
            fbtn3.hide()
            fbtn4.hide()
        }
        fbtn2.setOnClickListener {
            addNote()
            layoutManu.setBackgroundResource(0)
            fbtn2.hide()
            fbtn3.hide()
            fbtn4.hide()
        }
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
            commit()
        }
    }


    private fun addEvent() {
        val sharedPreferences =
            getSharedPreferences("Events", MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("upcomingEvents", null)
        val type: Type = object : TypeToken<ArrayList<EventListModel?>?>() {}.type
        var eventArray = gson.fromJson(json, type) as? ArrayList<EventListModel>

        if (eventArray == null) {
            eventArray = ArrayList()
        }
        eventAdapter =
            UpcomingEventsAdapter(eventArray, this, object : UpcomingEventsAdapter.onClickedEvent {
                override fun onEditEvent(eventListModel: EventListModel, position: Int) {
                }
            })

        val alertDialog = AlertDialog.Builder(this).create()
        val eventView = layoutInflater.inflate(R.layout.add_upcoming_events, null)
        alertDialog.setView(eventView)
        alertDialog.show()
        val eventTitle = eventView.findViewById<EditText>(R.id.edtEventTitle)
        val eventDescription = eventView.findViewById<EditText>(R.id.edtEventDescription)
        val eventAddButton = eventView.findViewById<Button>(R.id.btnAddEvent)
        val eventCancelButton = eventView.findViewById<Button>(R.id.btnCancelEvent)
        var txtEventStartDate = eventView.findViewById<TextView>(R.id.txtEventStartDate)
        var txtEventEndDate = eventView.findViewById<TextView>(R.id.txtEventEndDate)
        var txtEventTime = eventView.findViewById<TextView>(R.id.txtEventTime)
        var edtEventLocation = eventView.findViewById<EditText>(R.id.edtEventLocation)

        // Get Current Date
        val mCalendar = Calendar.getInstance()
        lastSelectedYear = mCalendar.get(Calendar.YEAR);
        lastSelectedMonth = mCalendar.get(Calendar.MONTH);
        lastSelectedDayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        txtEventStartDate.setOnClickListener {
            buttonSelectDate(txtEventStartDate);
        }

        txtEventEndDate.setOnClickListener {
            buttonSelectDate(txtEventEndDate);
        }



        txtEventTime.setOnClickListener {
            val c = Calendar.getInstance()
            var mHour = c[Calendar.HOUR_OF_DAY]
            var mMinute = c[Calendar.MINUTE]

            var timePickerDialog = TimePickerDialog(this, object :
                TimePickerDialog.OnTimeSetListener {
                override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                    val datetime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                    datetime[Calendar.MINUTE] = minute
                    if (datetime.timeInMillis >= c.timeInMillis) { //it's after current
                        val hour = hourOfDay % 12
                        txtEventTime.setText(
                            java.lang.String.format(
                                "%02d:%02d %s",
                                if (hour == 0) 12 else hour,
                                minute,
                                if (hourOfDay < 12) "am" else "pm"
                            )
                        )
                    } else { //it's before current'
                        Toast.makeText(this@MainActivity, "Invalid Time", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }, mHour, mMinute, false)
            timePickerDialog.show();
        }

        eventAddButton.setOnClickListener {
            if (eventTitle.text.isNotEmpty() && eventDescription.text.isNotEmpty() && !txtEventStartDate.text.equals(
                    "Date"
                ) && !txtEventEndDate.text.equals("Date") && !txtEventTime.text.equals("Event Time") && edtEventLocation.text.isNotEmpty()
            ) {
                val eventSharedPreference =
                    getSharedPreferences("Events", Context.MODE_PRIVATE)
                val editor = eventSharedPreference.edit()
                val eventGson = Gson()
                eventArray.add(
                    EventListModel(
                        eventTitle.text.toString(),
                        txtEventStartDate.text.toString(),
                        txtEventEndDate.text.toString(),
                        txtEventTime.text.toString(),
                        edtEventLocation.text.toString(),
                        eventDescription.text.toString()
                    )
                )
                val eventJson: String = eventGson.toJson(eventArray)
                editor.putString("upcomingEvents", eventJson)
                editor.apply()
                eventAdapter.notifyDataSetChanged()
                alertDialog.dismiss()
            } else {
                Toast.makeText(
                    this, "Please provide proper details!!!", Toast.LENGTH_SHORT
                ).show()
            }
        }
        eventCancelButton.setOnClickListener {
            alertDialog.dismiss()
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, Home())
            addToBackStack(null)
            commit()
        }
    }


private fun addTodo() {
    val sharedPreferences =
        getSharedPreferences("TODO", MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("todoArray", "null")
    val type: Type = object : TypeToken<ArrayList<ToDoListModel?>?>() {}.type
    var todoArray = gson.fromJson(json, type) as? ArrayList<ToDoListModel>

    if (todoArray == null) {
        todoArray = ArrayList()
    }


    todoAdapter = MyTodoAdapter(todoArray, object : MyTodoAdapter.onTodo {
        override fun onEditTodo(toDoListModel: ToDoListModel, position: Int) {
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
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
                            getSharedPreferences("TODO", Context.MODE_PRIVATE)
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
                        this@MainActivity,
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
            todoAdapter.notifyDataSetChanged()
        }


    })


    val alertDialog = AlertDialog.Builder(this).create()
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
                getSharedPreferences("TODO", MODE_PRIVATE)
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
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, ToDo())
                addToBackStack(null)
                commit()
            }
        } else {
            Toast.makeText(
                this,
                "Provide Proper details !!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    todoCancelButton.setOnClickListener {
        alertDialog.dismiss()
    }
}

private fun addNote() {
    val notesharedPreferences =
        getSharedPreferences("Note", MODE_PRIVATE)
    val notegson = Gson()
    val notejson = notesharedPreferences.getString("noteArray", "null")
    val notetype: Type = object : TypeToken<ArrayList<NoteListModel?>?>() {}.type
    var noteArray = notegson.fromJson(notejson, notetype) as? ArrayList<NoteListModel>

    if (noteArray == null) {
        noteArray = ArrayList()
    }

    noteAdapter = MyNotesAdapter(noteArray, object : MyNotesAdapter.onCLickedNote {
        override fun onEditNote(noteListModel: NoteListModel, position: Int) {

            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
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
                            getSharedPreferences("Note", Context.MODE_PRIVATE)
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
                        this@MainActivity,
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
            noteAdapter.notifyDataSetChanged()
        }
    })
    val alertDialog = AlertDialog.Builder(this).create()
    val noteView = layoutInflater.inflate(R.layout.add_my_notes, null)
    alertDialog.setView(noteView)
    alertDialog.show()
    val noteTitle = noteView.findViewById<EditText>(R.id.edtNoteTitle)
    val noteDescription = noteView.findViewById<EditText>(R.id.edtNoteDescription)
    val noteAddButton = noteView.findViewById<Button>(R.id.btnAddNote)
    val noteCancelButton = noteView.findViewById<Button>(R.id.btnCancelNote)

    noteAddButton.setOnClickListener {
        if (noteTitle.text.isNotEmpty() && noteDescription.text.isNotEmpty()) {
            val sharedPreferencess =
                getSharedPreferences("Note", Context.MODE_PRIVATE)
            val editors = sharedPreferencess.edit()
            val gSon = Gson()
            noteArray?.add(
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
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragmentContainer, ToDo())
                addToBackStack(null)
                commit()
            }
        } else {
            Toast.makeText(this, "Please provide proper details!!!", Toast.LENGTH_SHORT).show()
        }
    }
    noteCancelButton.setOnClickListener {
        alertDialog.dismiss()
    }

}

override fun onResume() {
    super.onResume()
    binding.txtGreetingDay.text = getGreetingMessage()
    if (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        methodRequiresTwoPermission()
    }

}

private fun buttonSelectDate(txtEventStartDate: TextView) {

    // Date Select Listener.
    val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            txtEventStartDate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
        }
    var datePickerDialog: DatePickerDialog? = null
    datePickerDialog =
        DatePickerDialog(
            this,
            dateSetListener,
            lastSelectedYear,
            lastSelectedMonth,
            lastSelectedDayOfMonth
        )

    // Show
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
    datePickerDialog.show()
}

fun getGreetingMessage(): String {
    val c = Calendar.getInstance()
    val timeOfDay = c.get(Calendar.HOUR_OF_DAY)

    return when (timeOfDay) {
        in 0..11 -> "Good Morning"
        in 12..15 -> "Good Afternoon"
        in 16..20 -> "Good Evening"
        in 21..23 -> "Good Night"
        else -> "Hello"
    }
}


override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
}

@SuppressLint("MissingPermission")
@AfterPermissionGranted(ACCESS_FINE_LOCATION)
open fun methodRequiresTwoPermission() {
    val perms = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    if (EasyPermissions.hasPermissions(this, *perms)) {

    } else {
        val permissions = """
                ${getString(R.string.permission_location)}
                """.trimIndent()
        EasyPermissions.requestPermissions(this, permissions, ACCESS_FINE_LOCATION, *perms)
    }
}

companion object {
    const val ACCESS_FINE_LOCATION = 5001
}

override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
        AppSettingsDialog.Builder(this).build().show()
    }
}
}


