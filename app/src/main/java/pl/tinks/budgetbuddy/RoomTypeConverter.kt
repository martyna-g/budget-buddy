package pl.tinks.budgetbuddy

import androidx.room.TypeConverter
import org.joda.money.Money
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class RoomTypeConverter {

    @TypeConverter
    fun fromMoney(money: Money): String = money.toString()

    @TypeConverter
    fun toMoney(moneyString: String): Money = Money.parse(moneyString)

    @TypeConverter
    fun fromUUID(uuid: UUID): String = uuid.toString()

    @TypeConverter
    fun toUUID(uuidString: String): UUID = UUID.fromString(uuidString)

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): Long =
        localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(timeInMillis: Long): LocalDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneOffset.UTC)

}
