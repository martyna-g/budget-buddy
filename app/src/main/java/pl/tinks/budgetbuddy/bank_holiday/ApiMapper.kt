package pl.tinks.budgetbuddy.bank_holiday

import android.content.Context
import pl.tinks.budgetbuddy.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ApiMapper @Inject constructor() {

    fun toBankHoliday(response: BankHolidayResponse, context: Context): List<BankHoliday> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return listOf(
            R.string.region_england_and_wales to response.englandAndWales.events,
            R.string.region_scotland to response.scotland.events,
            R.string.region_northern_ireland to response.northernIreland.events
        ).flatMap { (regionResId, events) ->
            events.map { event ->
                BankHoliday(
                    region = context.getString(regionResId),
                    title = event.title,
                    date = LocalDate.parse(event.date, formatter)
                )
            }
        }
    }
}
