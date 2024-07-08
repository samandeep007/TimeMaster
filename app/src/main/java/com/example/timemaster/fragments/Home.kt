package com.example.timemaster.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemaster.MainActivity
import com.example.timemaster.R
import com.example.timemaster.adapter.MyTodoAdapter
import com.example.timemaster.adapter.UpcomingEventsAdapter
import com.example.timemaster.databinding.FragmentHomeBinding
import com.example.timemaster.model.EventListModel
import com.example.timemaster.model.ToDoListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

class Home : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var todoAdapter : MyTodoAdapter
    lateinit var eventAdapter : UpcomingEventsAdapter
    var lastSelectedYear = 0
    var lastSelectedMonth = 0
    var lastSelectedDayOfMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).binding.bottomMenuBar.menu.getItem(0).isChecked = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //upcoming Event
        val eventsharedPreferences =
            requireActivity().getSharedPreferences("Events", Context.MODE_PRIVATE)
        val eventgson = Gson()
        val eventjson = eventsharedPreferences.getString("upcomingEvents", null)
        val eventtype: Type = object : TypeToken<ArrayList<EventListModel?>?>() {}.type
        var eventArray = eventgson.fromJson(eventjson, eventtype) as? ArrayList<EventListModel>

        if (eventArray == null) {
            eventArray = ArrayList()
        }
        eventAdapter = UpcomingEventsAdapter(eventArray,requireActivity(),object : UpcomingEventsAdapter.onClickedEvent {
            override fun onEditEvent(eventListModel: EventListModel, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val eventView = layoutInflater.inflate(R.layout.add_upcoming_events, null)
                alertDialog.setView(eventView)
                alertDialog.show()
                val eventTitle = eventView.findViewById<EditText>(R.id.edtEventTitle)
                val eventDescription =
                    eventView.findViewById<EditText>(R.id.edtEventDescription)
                val eventAddButton = eventView.findViewById<Button>(R.id.btnAddEvent)
                val eventCancelButton = eventView.findViewById<Button>(R.id.btnCancelEvent)
                val txtEventStartDate = eventView.findViewById<TextView>(R.id.txtEventStartDate)
                val txtEventEndDate = eventView.findViewById<TextView>(R.id.txtEventEndDate)
                val txtEventTime = eventView.findViewById<TextView>(R.id.txtEventTime)
                val edtEventLocation = eventView.findViewById<EditText>(R.id.edtEventLocation)


                eventTitle.setText(eventListModel.eventTitle)
                eventDescription.setText(eventListModel.eventDescription)
                txtEventStartDate.setText(eventListModel.eventStartDate)
                txtEventEndDate.setText(eventListModel.eventEndDate)
                txtEventTime.setText(eventListModel.eventTime)
                edtEventLocation.setText(eventListModel.eventLocation)
                eventAddButton.text = "Update"
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
                    val mHour = c[Calendar.HOUR_OF_DAY]
                    val mMinute = c[Calendar.MINUTE]

                    val timePickerDialog = TimePickerDialog(requireActivity(), object :
                        TimePickerDialog.OnTimeSetListener {
                        @SuppressLint("DefaultLocale")
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
                                Toast.makeText(
                                    requireActivity(),
                                    "Invalid Time",
                                    Toast.LENGTH_LONG
                                ).show()
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
                            requireActivity().getSharedPreferences(
                                "Events",
                                Context.MODE_PRIVATE
                            )
                        val editor = eventSharedPreference.edit()
                        val eventGson = Gson()
                        eventArray.removeAt(position)
                        eventArray.add(
                            position,
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
                            requireContext(),
                            "Please provide proper details!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                eventCancelButton.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        })
        binding.recycleViewUpcomingEvents.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recycleViewUpcomingEvents.adapter = eventAdapter


        //Todo List
        val sharedPreferences =
            requireActivity().getSharedPreferences("TODO", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("todoArray", "null")
        val type: Type = object : TypeToken<ArrayList<ToDoListModel?>?>() {}.type
        var todoArray = gson.fromJson(json, type) as? ArrayList<ToDoListModel>

        if (todoArray == null) {
            todoArray = ArrayList()
        }

        todoAdapter = MyTodoAdapter(todoArray,object : MyTodoAdapter.onTodo {
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
        binding.recyclerViewTodoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTodoList.adapter = todoAdapter
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
                requireActivity(),
                dateSetListener,
                lastSelectedYear,
                lastSelectedMonth,
                lastSelectedDayOfMonth
            )

        // Show
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }
}