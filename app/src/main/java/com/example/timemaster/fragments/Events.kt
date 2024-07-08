package com.example.timemaster.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timemaster.Function
import com.example.timemaster.MainActivity
import com.example.timemaster.R
import com.example.timemaster.adapter.UpcomingEventsAdapter
import com.example.timemaster.databinding.FragmentEventsBinding
import com.example.timemaster.model.EventListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*


class Events : Fragment() {

    lateinit var binding: FragmentEventsBinding
    lateinit var eventAdapter: UpcomingEventsAdapter

    var lastSelectedYear = 0
    var lastSelectedMonth = 0
    var lastSelectedDayOfMonth = 0

    open var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var locationManager: LocationManager? = null
    private val minTime: Long = 10000
    private val minDistance = 10f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? { // Inflate the layout for this fragment
        binding = FragmentEventsBinding.inflate(layoutInflater, container, false)
        (activity as MainActivity).binding.bottomMenuBar.menu.getItem(1).isChecked = true
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLocation()

        val sharedPreferences =
            requireActivity().getSharedPreferences("Events", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("upcomingEvents", null)
        val type: Type = object : TypeToken<ArrayList<EventListModel?>?>() {}.type
        var eventArray = gson.fromJson(json, type) as? ArrayList<EventListModel>

        if (eventArray == null) {
            eventArray = ArrayList()
        }

        eventAdapter =
            UpcomingEventsAdapter(eventArray, requireActivity() ,object : UpcomingEventsAdapter.onClickedEvent {
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

        binding.btnCreateEvent.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).create()
            val eventView = layoutInflater.inflate(R.layout.add_upcoming_events, null)
            alertDialog.setView(eventView)
            alertDialog.show()
            val eventTitle = eventView.findViewById<EditText>(R.id.edtEventTitle)
            val eventDescription = eventView.findViewById<EditText>(R.id.edtEventDescription)
            val eventAddButton = eventView.findViewById<Button>(R.id.btnAddEvent)
            val eventCancelButton = eventView.findViewById<Button>(R.id.btnCancelEvent)
            val txtEventStartDate = eventView.findViewById<TextView>(R.id.txtEventStartDate)
            val txtEventEndDate = eventView.findViewById<TextView>(R.id.txtEventEndDate)
            val txtEventTime = eventView.findViewById<TextView>(R.id.txtEventTime)
            val edtEventLocation = eventView.findViewById<EditText>(R.id.edtEventLocation)


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
                            Toast.makeText(requireActivity(), "Invalid Time", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }, mHour, mMinute, false)
                timePickerDialog.show();
            }

            eventAddButton.setOnClickListener {
                if (eventTitle.text.isNotEmpty() && eventDescription.text.isNotEmpty() && !txtEventStartDate.text.equals("Date") && !txtEventEndDate.text.equals("Date") && !txtEventTime.text.equals("Event Time") && edtEventLocation.text.isNotEmpty()
                ) {
                    val eventSharedPreference =
                        requireActivity().getSharedPreferences("Events", Context.MODE_PRIVATE)
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

                    val intent = Intent(Intent.ACTION_EDIT)
                    intent.type = "vnd.android.cursor.item/event"
                    intent.putExtra("beginTime", txtEventTime.text.toString())
                    intent.putExtra("allDay", true)
                    intent.putExtra("rule", "FREQ=YEARLY")
                    intent.putExtra("endTime", txtEventTime.text.toString())
                    intent.putExtra("title", eventTitle.text.toString())
                    startActivity(intent)
                    alertDialog.dismiss()
                } else {
                    Toast.makeText(
                        requireContext(), "Please provide proper details!!!", Toast.LENGTH_SHORT
                    ).show()
                }
            }
            eventCancelButton.setOnClickListener {
                alertDialog.dismiss()
            }
        }


    }

    private fun buttonSelectDate(txtEventStartDate: TextView) {
        // Date Select Listener.
        val dateSetListener = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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


    private val locationListener = LocationListener {
        currentLatitude = it.latitude
        currentLongitude = it.longitude
        val asyncTask =
            Function.placeIdTask { weather_city, weather_description, weather_temperature, tempMin, tempMax, weather_updatedOn, weather_iconText, sun_rise -> //                cityField.setText(weather_city)
                //                updatedField.setText(weather_updatedOn)
                //                detailsField.setText(weather_description)
                binding.txtTemp.text = weather_temperature
                binding.txtmax.text = "Max: $tempMax"
                binding.txtmin.text = "Min: $tempMin"
                binding.weatherIcon.text = Html.fromHtml(weather_iconText)
            }
        asyncTask.execute(currentLatitude.toString(), currentLongitude.toString())

    }

    private fun getLocation() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isAltitudeRequired = true
        criteria.isBearingRequired = true
        criteria.isCostAllowed = true
        criteria.verticalAccuracy = Criteria.ACCURACY_HIGH
        criteria.horizontalAccuracy = Criteria.ACCURACY_HIGH

        val providers = locationManager!!.getProviders(criteria, true)
        for (provider in providers) {
            if (!provider.contains("gps")) { // if gps is disabled
                val poke = Intent()
                poke.setClassName(
                    "com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider"
                )
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
                poke.data = Uri.parse("3")
                requireActivity().sendBroadcast(poke)
            } // Get the location from the given provider
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (requireActivity() as MainActivity).methodRequiresTwoPermission()
            } else {
                locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, minTime, minDistance, locationListener
                )
            }
        }
    }

}