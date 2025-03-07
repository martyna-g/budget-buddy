package pl.tinks.budgetbuddy.bank_holiday

import android.content.Context
import pl.tinks.budgetbuddy.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ApiMapper @Inject constructor() {

    fun toBankHoliday(response: BankHolidayResponse, context: Context): MutableList<BankHoliday> {
        val list: MutableList<BankHoliday> = mutableListOf()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        response.englandAndWales.events.forEach {
            val date = LocalDate.parse(it.date, formatter)
            list.add(BankHoliday(context.getString(R.string.region_england_and_wales), it.title, date))
        }
        response.scotland.events.forEach {
            val date = LocalDate.parse(it.date)
            list.add(BankHoliday(context.getString(R.string.region_scotland), it.title, date))
        }
        response.northernIreland.events.forEach {
            val date = LocalDate.parse(it.date)
            list.add(BankHoliday(context.getString(R.string.region_northern_ireland), it.title, date))
        }

        return list
    }
}
