package com.spywarescanner.app.data.local

import androidx.room.TypeConverter
import com.spywarescanner.app.data.model.AlertType
import com.spywarescanner.app.data.model.ScanType
import com.spywarescanner.app.data.model.ThreatLevel
import com.spywarescanner.app.data.model.ThreatType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it, formatter) }
    }

    @TypeConverter
    fun fromThreatLevel(value: ThreatLevel): String {
        return value.name
    }

    @TypeConverter
    fun toThreatLevel(value: String): ThreatLevel {
        return ThreatLevel.valueOf(value)
    }

    @TypeConverter
    fun fromThreatType(value: ThreatType): String {
        return value.name
    }

    @TypeConverter
    fun toThreatType(value: String): ThreatType {
        return ThreatType.valueOf(value)
    }

    @TypeConverter
    fun fromThreatTypeList(value: List<ThreatType>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toThreatTypeList(value: String): List<ThreatType> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { ThreatType.valueOf(it) }
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isEmpty()) return emptyList()
        return value.split(",")
    }

    @TypeConverter
    fun fromScanType(value: ScanType): String {
        return value.name
    }

    @TypeConverter
    fun toScanType(value: String): ScanType {
        return ScanType.valueOf(value)
    }

    @TypeConverter
    fun fromAlertType(value: AlertType): String {
        return value.name
    }

    @TypeConverter
    fun toAlertType(value: String): AlertType {
        return AlertType.valueOf(value)
    }
}
