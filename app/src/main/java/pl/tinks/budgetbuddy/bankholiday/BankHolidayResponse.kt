package pl.tinks.budgetbuddy.bankholiday

import com.google.gson.annotations.SerializedName

data class BankHolidayResponse(
    @SerializedName("england-and-wales") val englandAndWales: BankHolidayRegion,
    @SerializedName("scotland") val scotland: BankHolidayRegion,
    @SerializedName("northern-ireland") val northernIreland: BankHolidayRegion,
)

data class BankHolidayRegion(
    val division: String,
    val events: List<BankHolidayEvent>
)

data class BankHolidayEvent(
    val title: String,
    val date: String,
    val notes: String,
    val bunting: Boolean
)
