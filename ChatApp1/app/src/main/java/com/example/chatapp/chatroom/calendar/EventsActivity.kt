package com.example.chatapp.chatroom.calendar


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityEventsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventsBinding
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val year = intent.getIntExtra("year", 0)
        val month = intent.getIntExtra("month", 0)
        val dayOfMonth = intent.getIntExtra("day", 0)

        val formattedDate = formatDate(year, month, dayOfMonth)
        val formattedDay = formatDayOfWeek(year, month, dayOfMonth)
        binding.date.text = formattedDate
        binding.day.text = formattedDay

        setupRecyclerViews()

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun formatDayOfWeek(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, dayOfMonth)
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dayOfWeekFormat.format(calendar.time)
    }

    private fun setupRecyclerViews() {
        val eventsList = getEventsFromIntent()
        eventsAdapter = EventsAdapter(eventsList)
        binding.recyclerView.apply {
            adapter = eventsAdapter
            layoutManager = LinearLayoutManager(this@EventsActivity)
        }
    }

    private fun getEventsFromIntent(): List<EventsDetails> {
        val summaries = intent.getStringArrayExtra("summaries") ?: emptyArray()
        val descriptions = intent.getStringArrayExtra("descriptions") ?: emptyArray()
        val startTimes = intent.getStringArrayExtra("startTimes") ?: emptyArray()

        val eventsList = mutableListOf<EventsDetails>()
        for (i in summaries.indices) {
            eventsList.add(
                EventsDetails(
                    summaries[i],
                    descriptions[i],
                    formatTime(startTimes[i]),
                )
            )
        }
        return eventsList
    }

    private fun formatTime(dateTimeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateTime = inputFormat.parse(dateTimeString)
        return outputFormat.format(dateTime)
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, dayOfMonth)
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}