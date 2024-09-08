//package com.example.chatapp
//
//import androidx.lifecycle.MutableLiveData
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.asFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.flatMapConcat
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOn
//import kotlinx.coroutines.flow.map
//import java.text.SimpleDateFormat
//import java.util.Calendar
//import java.util.Locale
//
//class ChatRepository  {
//    val isLoading = MutableLiveData(true)
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    fun getDateList(): Flow<List<Contact>> = flow {
//        val listContact = ArrayList<Contact>()
//        val years = ConstantFunction.firstYear()..ConstantFunction.lastYear()
//
//        years.asFlow().flatMapConcat { year ->
//            (1..12).asFlow().map { month ->
//                findDatesInMonth(year, month)
//            }
//        }.collect { mCalendar ->
//            listContact.add(mCalendar)
//        }
//        emit(listContact)
//        isLoading.postValue(false)
//    }.catch {
//        isLoading.postValue(false)
//        emit(arrayListOf())
//    }.flowOn(Dispatchers.IO)
//
//    private fun findDatesInMonth(year: Int, month: Any) {
//        val dayList = ArrayList<String>()
//        val calendar = Calendar.getInstance()
//        val smallSdf = SimpleDateFormat("d-MMM", Locale.getDefault())
//        val titleSdf = SimpleDateFormat("MMMM, yyyy", Locale.getDefault())
//        val fullSdf = SimpleDateFormat("d-MMM, yyyy", Locale.getDefault())
//
//        calendar.clear()
//        calendar.set(year, month - 1, 1)
//        val title = titleSdf.format(calendar.time)
//        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//
//        while ((calendar.get(Calendar.DAY_OF_MONTH) < daysInMonth) && (calendar.get(Calendar.MONTH) == month - 1)) {
//            //for find monday
//            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
//                calendar.add(Calendar.DAY_OF_MONTH, 1)
//            }
//
//            val monTime = calendar.time
//            var days = calendar.get(Calendar.DAY_OF_MONTH).toString()
//            val mon = Calendar.from(year, month, calendar.get(Calendar.DAY_OF_MONTH))
//
//            //for find sunday
//            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
//                calendar.add(Calendar.DAY_OF_MONTH, 1)
//            }
//
//            //for find sunday
//            while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
//                calendar.add(Calendar.DAY_OF_MONTH, 1)
//            }
//
//            if (mon.year != calendar.get(Calendar.YEAR)) {
//                days = "${fullSdf.format(monTime)} ― ${fullSdf.format(calendar.time)}"
//            } else {
//                if (mon.month != calendar.get(Calendar.MONTH) + 1) {
//                    days = "${smallSdf.format(monTime)} ― ${fullSdf.format(calendar.time)}"
//                } else {
//                    days += " ― ${fullSdf.format(calendar.time)}"
//                }
//            }
//
//            dayList.add(days)
//
//        }
////        return Contact
//      }
//    }