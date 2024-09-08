package com.example.chatapp.chatroom.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentCalendarBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private val eventStartTimes: MutableList<Long> = mutableListOf()
    private val REQUEST_AUTHORIZATION = 1001
    private var events: Events? = null


    companion object {
        fun newInstance(): CalendarFragment {
            return CalendarFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedDates: MutableList<CalendarDay> = mutableListOf()
        val calendarView = binding.calendarView
        calendarView.setSelectionColor(Color.parseColor("#4285f4"))

        val customFont = Typeface.createFromAsset(requireContext().assets, "PTSans_Regular.ttf")
        applyCustomFontToMonthLabel(calendarView, customFont)
        applyCustomFontToWeekLabels(calendarView, customFont)

        val today = CalendarDay.today()
        selectedDates.add(today)

        calendarView.setOnDateChangedListener { _, date, _ ->
            Log.e("CalendarFragment", "Selected Date: ${date.day}.${date.month}.${date.year}")

            if (events != null) {
                val eventsForDate = getEventsForDate(date, events!!)
                val summaries = eventsForDate.map { it.summary }.toTypedArray()
                val descriptions = eventsForDate.map { it.description }.toTypedArray()
                val startTimes = eventsForDate.map { formatDate(getStartTime(it)) }.toTypedArray()

                val intent = Intent(requireContext(), EventsActivity::class.java).apply {
                    putExtra("summaries", summaries)
                    putExtra("descriptions", descriptions)
                    putExtra("startTimes", startTimes)
                    putExtra("day", date.day)
                    putExtra("month", date.month)
                    putExtra("year", date.year)
                }
                startActivity(intent)
            } else {
                Log.e("Events are coming", "Events are null or not fetched yet.")
            }
        }


        fetchCalendarEvents()
    }

    private fun getEventsForDate(date: CalendarDay, events: Events): List<Event> {
        Log.e("getting events", "event list function called")
        val calendar = Calendar.getInstance().apply {
            set(date.year, date.month - 1, date.day, 0, 0, 0)
        }
        val startOfDay = calendar.time
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.time

        return events.items?.filter { event ->
            val eventTime = getStartTime(event) ?: return@filter false
            eventTime >= startOfDay.time && eventTime < endOfDay.time
        } ?: emptyList()
    }

    private fun applyCustomFontToWeekLabels(calendarView: MaterialCalendarView, customFont: Typeface) {
        val daysTextViews = mutableListOf<TextView>()
        val daysOfWeekLayout = calendarView.getChildAt(1) as? ViewGroup
        daysOfWeekLayout?.let { layout ->
            for (i in 0 until layout.childCount) {
                val childView = layout.getChildAt(i)
                if (childView is TextView) {
                    daysTextViews.add(childView)
                }
            }
            daysTextViews.forEach { it.typeface = customFont }
        }
    }

    private fun applyCustomFontToMonthLabel(calendarView: MaterialCalendarView, customFont: Typeface) {
        val monthTextViews = mutableListOf<TextView>()
        val monthLayout = calendarView.findViewById<ViewGroup>(com.prolificinteractive.materialcalendarview.R.id.header)
        monthLayout?.let { layout ->
            for (i in 0 until layout.childCount) {
                val childView = layout.getChildAt(i)
                if (childView is TextView) {
                    monthTextViews.add(childView)
                }
            }
            monthTextViews.forEach { it.typeface = customFont }
        }
    }

    private fun fetchCalendarEvents() {
        val context = requireContext()
        val credential = GoogleAccountCredential.usingOAuth2(context, setOf(CalendarScopes.CALENDAR))
        val account = GoogleSignIn.getLastSignedInAccount(context)
        credential.selectedAccount = account?.account

        if (account == null) {
            binding.calendarView.visibility = View.VISIBLE
            return
        }

        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

        val googleCalendar = com.google.api.services.calendar.Calendar.Builder(
            transport, jsonFactory, credential
        ).setApplicationName(R.string.app_name.toString()).build()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val now = Date()
                val eventResult: Events? = googleCalendar.events().list("primary")
                    .setTimeMin(com.google.api.client.util.DateTime(now))
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()

                events = eventResult

                events?.items?.forEach { event ->
                    val startTime: Long? = getStartTime(event)
                    startTime?.let {
                        eventStartTimes.add(it)
                    }

                    val summary = event.summary ?: "No summary"
                    val description = event.description ?: "No description"

                    Log.e("CalendarFragment", "Event summary: $summary")
                    Log.d("CalendarFragment", "Start time: ${formatDate(startTime)}")
                    Log.d("CalendarFragment", "Description: $description")
                }

                Log.e("CalendarFragment", "Updating calendar with ${eventStartTimes.size} events")
                activity?.runOnUiThread {
                    updateCalendar()
                }
            } catch (e: UserRecoverableAuthIOException) {
                startActivityForResult(e.intent, REQUEST_AUTHORIZATION)
            } catch (e: Exception) {
                Log.e("CalendarFragment", "Error fetching events: ${e.message}")
            }
        }
    }

    private fun getStartTime(event: Event): Long? {
        return event.start?.dateTime?.value ?: event.start?.date?.value
    }

    private fun formatDate(timestamp: Long?): String {
        timestamp?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val calendar = Calendar.getInstance().apply {
                timeInMillis = it
            }
            return dateFormat.format(calendar.time)
        }
        return ""
    }

    private fun updateCalendar() {
        val calendarView = binding.calendarView
        calendarView.removeDecorators()

        val typeface = Typeface.createFromAsset(context?.assets, "PTSans_Regular.ttf")
        val fontStyle = CalendarDayViewDecorator(typeface, calendarView)
        calendarView.addDecorator(fontStyle)

        val textColor = Color.RED
        val eventDays = eventStartTimes.mapNotNull { timestamp ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            val day = CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
            day
        }
        val eventDecorator = EventDecorator(eventDays, textColor)
        calendarView.addDecorator(eventDecorator)

        calendarView.invalidateDecorators()
    }


    class EventDecorator(private val eventDays: List<CalendarDay>, private val textColor: Int) :
        DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return eventDays.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(textColor))
            Log.e("Decorate", "Text color changed")
        }
    }

}

class CalendarDayViewDecorator(private val typeface: Typeface, private val calendarView: MaterialCalendarView) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return true
    }

    @SuppressLint("DiscouragedApi")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun decorate(view: DayViewFacade) {
        view.addSpan(TypefaceSpan(typeface))

    }

}
