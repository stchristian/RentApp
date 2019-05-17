package com.example.rentapp.data

import java.net.URL

data class Gadget(
    val name: String,
    val qr_code: String,
    val serial_no: String,
    val responsible: String,
    val description: String,
    val thumbnailUrl: String,
    val images: Array<String>
)