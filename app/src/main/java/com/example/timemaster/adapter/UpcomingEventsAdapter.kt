package com.example.timemaster.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.timemaster.R
import com.example.timemaster.model.EventListModel
import java.text.SimpleDateFormat
import java.util.*

class UpcomingEventsAdapter(val list: ArrayList<EventListModel>,val activity : Activity, val clicked : onClickedEvent) :
    RecyclerView.Adapter<UpcomingEventsAdapter.upcomingEventsViewHolder>() {

    class upcomingEventsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventTitle = view.findViewById<TextView>(R.id.txtNoteTitle)
        val eventDescription = view.findViewById<TextView>(R.id.txtNoteDetails)
        val eventStartDate = view.findViewById<TextView>(R.id.txtEventStartDate)
        val eventEndDate = view.findViewById<TextView>(R.id.txtEventEndDate)
        val eventTime = view.findViewById<TextView>(R.id.txtEventTime)
        val eventLocation = view.findViewById<TextView>(R.id.txtLocation)
        val editEvent = view.findViewById<ImageView>(R.id.imgEditEvent)
        val mainCard = view.findViewById<CardView>(R.id.mainCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upcomingEventsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_upcoming_events, parent, false)
        return upcomingEventsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: upcomingEventsViewHolder, position: Int) {
        holder.eventTitle.text = list[position].eventTitle
        holder.eventDescription.text = list[position].eventDescription
        holder.eventStartDate.text = list[position].eventStartDate
        holder.eventEndDate.text = list[position].eventEndDate
        holder.eventTime.text = list[position].eventTime
        holder.eventLocation.text = list[position].eventLocation

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = Calendar.getInstance().timeInMillis
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedTime = sdf.format(Date(currentTime))

        val timeString = list[position].eventTime
        val inputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timeString)
        val formattedTimes = outputFormat.format(date)

        if(list[position].eventEndDate.split("-")[0].toInt() <= currentDate.split("-")[0].toInt()  && formattedTimes <= formattedTime){
            holder.mainCard.visibility = View.GONE
        }else{
            holder.mainCard.visibility = View.VISIBLE
        }




        holder.editEvent.setOnClickListener {
            clicked.onEditEvent(list[position], position)
        }
    }
    interface onClickedEvent{
        fun onEditEvent(eventListModel: EventListModel, position: Int)

    }
}