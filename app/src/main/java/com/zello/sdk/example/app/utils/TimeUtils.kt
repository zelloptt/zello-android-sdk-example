package com.zello.sdk.example.app.utils

import android.text.format.DateFormat
import java.util.Calendar
import java.util.Locale

class TimeUtils {
	companion object {
		fun timestampToString(time: Long): String {
			val cal: Calendar = Calendar.getInstance(Locale.ENGLISH)
			cal.timeInMillis = time
			val date: String = DateFormat.format("yyyy-MM-dd hh:mm:ss a", cal).toString()
			return date
		}
	}
}