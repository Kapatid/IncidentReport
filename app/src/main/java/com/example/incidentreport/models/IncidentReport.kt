package com.example.incidentreport.models

import java.text.SimpleDateFormat
import java.util.*

data class IncidentReport(
    val id: Long = Long.MIN_VALUE,
    val reporter: String = "",
    val incidentType: String,
    val dateTime: Date,
    val injured: Int,
    val missing: Int,
    val fatalities: Int,
    val location: String = "",
    val description: String = "",
    val victims: List<String>,
    val updatedAt: Date? = SimpleDateFormat(
        "EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
        .parse(
            SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
                .format(Calendar.getInstance().time)
        ),
    val createdAt: Date? = SimpleDateFormat(
        "EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
        .parse(
            SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
                .format(Calendar.getInstance().time)
        )
)