package com.example.incidentreport.models

data class User(
    val id: Long = Long.MIN_VALUE,
    val fullName: String = "",
    val email: String = "",
    val password: String = ""
)
