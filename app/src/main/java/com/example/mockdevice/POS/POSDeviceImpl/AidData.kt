package com.example.mockdevice.POS.POSDeviceImpl

data class AidData (

        val aid: String,
        val applicationLabel: String,
        val terminalCapabilities: String,
        val additionalTerminalCapabilities: String,
        val terminalType: Byte = 0,//Igual
        val transactionCurrencyCode: String,
        val terminalCountryCode: String,
        val contactlessEnabled: Boolean = false

    )