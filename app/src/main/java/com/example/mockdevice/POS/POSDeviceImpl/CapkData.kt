package com.example.mockdevice.POS.POSDeviceImpl

data class CapkData(
    val rid: String,
    val index: Byte,
    val exponent: Byte,
    val modulus: String,
    val checksum: String,
    val expiryDate: String,
    val effectiveDate: String,
    val secureHash: String
)