package com.example.chatapp.chatroom.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsAdapter(private val eventsList: List<EventsDetails>) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val summaryTextView: TextView = itemView.findViewById(R.id.summary)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        private val timeTextView: TextView = itemView.findViewById(R.id.time)
//        private val dateTextView: TextView = itemView.findViewById(R.id.date)
//        private val dayTextView: TextView = itemView.findViewById(R.id.day)

        fun bind(event: EventsDetails) {
            summaryTextView.text = event.summary
            descriptionTextView.text = event.description
            timeTextView.text = event.startTime
//            dateTextView.text = formatDate(event.day, event.month, event.year)
//            dayTextView.text = formatDayOfWeek(event.year, event.month, event.day)

        }


//        private fun formatDayOfWeek(year: Int, month: Int, dayOfMonth: Int): String {
//            val calendar = Calendar.getInstance().apply {
//                set(year, month - 1, dayOfMonth)
//            }
//            val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
//            return dayOfWeekFormat.format(calendar.time)
//        }

//        private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
//            val calendar = Calendar.getInstance().apply {
//                set(year, month - 1, dayOfMonth)
//            }
//            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
//            return dateFormat.format(calendar.time)
//        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_events, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }
}

class EventsDetails(
    val summary: String,
    val description: String,
    val startTime: String,
//    val day: Int,
//    val month: Int,
//    val year: Int
)
