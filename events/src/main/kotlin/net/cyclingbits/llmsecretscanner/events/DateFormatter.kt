package net.cyclingbits.llmsecretscanner.events

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object Formatter {
    private val ZONE_ID = ZoneId.systemDefault()
    private val FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZONE_ID)
    private val REPORT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZONE_ID)
    private val JSON_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZONE_ID)
    
    fun formatForFileName(instant: Instant = Instant.now()): String = 
        FILE_NAME_FORMATTER.format(instant)
    
    fun formatForReport(instant: Instant = Instant.now()): String = 
        REPORT_FORMATTER.format(instant)
    
    fun formatForJson(instant: Instant): String = 
        JSON_FORMATTER.format(instant)

    fun formatPercent(value: Double): String = "%.1f".format(Locale.US, value)
    
    fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "${minutes}m ${seconds}s"
    }
}