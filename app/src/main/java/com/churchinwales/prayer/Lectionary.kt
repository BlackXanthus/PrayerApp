package com.churchinwales.prayer.ui

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import com.churchinwales.prayer.AppDebug


class Lectionary {

    companion object {
        const val SEASON: Int = 0
        const val WEEKOFSEASON: Int = 1
    }

    fun getDayOfWeek(): String
    {
        var cal = Calendar.getInstance()
        var formatter: SimpleDateFormat = SimpleDateFormat("EEEE")
        var rDayOfWeek: String =formatter.format(cal)

        AppDebug.log("TAG","Day Of The Week:"+rDayOfWeek)

        return rDayOfWeek;

    }

    fun getSeason(): Array<String> {
        val cal = java.util.Calendar.getInstance()
        var easter = java.util.Calendar.Builder()
            .setDate(2022, 4, 9)
            .build()

        if (cal[java.util.Calendar.YEAR] == 2022) {
            easter = java.util.Calendar.Builder()
                .setDate(2022, 3, 17)
                .build()
        }
        if (cal[java.util.Calendar.YEAR] == 2023) {
            easter = java.util.Calendar.Builder()
                .setDate(2023, 4, 9)
                .build()
        }

        var season = "ADVENT"
        var weekOfSeason = 1
        var dayOfWeek = "Monday"

        if (cal.compareTo(easter) > 0) {
            val weeks = cal.timeInMillis - easter.timeInMillis
            val newCal = java.util.Calendar.Builder().setInstant(weeks).build()
            val weeksSinceEaster = newCal[java.util.Calendar.WEEK_OF_YEAR]
            dayOfWeek = Lectionary().getDayOfWeek()
            //dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

            // weeksSinceEaster = weeksSinceEaster / (24 * 60 * 60 * 1000);
            AppDebug.log(
                "TAG",
                "Season:$season Week:$weekOfSeason Day:$dayOfWeek Week of Year:$weeksSinceEaster"
            )
            if (weeksSinceEaster <= 6) {
                season = "EASTER"
                weekOfSeason = weeksSinceEaster
            } else if (weeksSinceEaster >= 22) {
                season = "KINGDOM"
                weekOfSeason = weeksSinceEaster - 21
                if (weekOfSeason >= 4) {
                    weekOfSeason = 3
                }
            } else if (weeksSinceEaster >= 26) {
                season = "ADVENT"
                weekOfSeason = weeksSinceEaster - 25
            } else {
                season = "TRINITY"
                weekOfSeason = weeksSinceEaster - 6
            }
            if (cal.compareTo(easter) < 0) {
                AppDebug.log("TAG", "Date is before Easter")
                if (cal[java.util.Calendar.WEEK_OF_YEAR] >= 0) {
                    season = "NATIVITY"
                    weekOfSeason = cal[java.util.Calendar.WEEK_OF_YEAR]
                }
                if (cal[java.util.Calendar.WEEK_OF_YEAR] >= 3) {
                    season = "EPIPHANY"
                    weekOfSeason = cal[java.util.Calendar.WEEK_OF_YEAR] - 2
                }
            }
        }
        return arrayOf(season, weekOfSeason.toString())
    }


}