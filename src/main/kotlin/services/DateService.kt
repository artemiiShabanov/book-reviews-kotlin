package services

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalQuery

/** Date template(can be changed). */
private val DATE_PATTERN = "dd.MM.yyyy"

/** Date formatter. */
private val DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN)

/**
 * Using [DateService1.DATE_PATTERN] to format date to string.
 * @param date - date
 * @return formatted string
 */
fun format(date: LocalDate?): String? {
    when (date) {
        null -> return null
        else -> return DATE_FORMATTER.format(date)
    }
}

/**
 * Converting string using [DateService1.DATE_PATTERN] into [LocalDate].
 * Returns null if string cant be converted.
 * @param dateString - Date as String
 * *
 * @return Resulting date
 */
fun parse(dateString: String): LocalDate? {
    try {
        return DATE_FORMATTER.parse<LocalDate>(dateString, { LocalDate.from(it) })
    } catch (e: DateTimeParseException) {
        return null
    }
}

/**
 * Parse from labirint.
 * Returns null if string cant be converted.
 * @param dateString - Date as String
 * @return Resulting date
 */
fun parseLabirint(dateString: String): LocalDate? {
    try {
        return DATE_FORMATTER.parse<LocalDate>(dateString.trim().substring(0, dateString.indexOf(' ')), { LocalDate.from(it) })
    } catch (e: DateTimeParseException) {
        return null
    }
}

/**
 * Parse from ozon.
 * Returns null if string cant be converted.
 * @param dateString - Date as String
 * @return Resulting date
 */
fun parseOzon(dateString: String): LocalDate? {
    var array = dateString.split(" ");
    val convertedString = array[0] + "." + monthToNum(array[1]) + "." + array[2]

    try {
        return DATE_FORMATTER.parse(convertedString) { LocalDate.from(it) }
    } catch (e: DateTimeParseException) {
        return null
    }
}

/**
 * Supporting function converting string-month to string-number.
 * @param month
 * @return
 */
private fun monthToNum(month: String): String {
    when (month) {
        "января" -> return "01"
        "февраля" -> return "02"
        "марта" -> return "03"
        "апреля" -> return "04"
        "мая" -> return "05"
        "июня" -> return "06"
        "июля" -> return "07"
        "августа" -> return "08"
        "сентября" -> return "09"
        "октября" -> return "10"
        "ноября" -> return "11"
        "декабря" -> return "12"
        else -> return "-1"
    }
}