package com.example.rentapp.models

import com.google.gson.annotations.Expose

data class RegistrationData(
    val email: String,
    val jelszo: String,
    val keresztnev: String,
    val telefonszam: String,
    val vezeteknev: String
)