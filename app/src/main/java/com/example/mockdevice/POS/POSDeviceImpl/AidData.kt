package com.example.mockdevice.POS.POSDeviceImpl

data class AidData (
        val aid: String,
        val applicationLabel: String,
        val terminalCapabilities: String,
        val additionalTerminalCapabilities: String,
        val terminalType: Byte,
        val transactionCurrencyCode: String,
        val terminalCountryCode: String,
        val contactlessEnabled: Boolean,
        val soloContactless: Boolean,
        val TACDenial: String,
        val TACOnline: String,
        val TACDefault: String,
        val terminalFloorLimit: String,
        val threshold: String,
        val dDOL: String,
        val tDOL: String,
        val TRM: String,
        val permiteAIDParcial: Byte,
        val AIDLen: Byte,
        val transCurrencyExponent: Byte,
        val ClssTxnLimit: String,
        val ClssTxnLimitCDCVM: String,
        val ClssTxnLimitNoCDCVM: String,
        val ClssCVMReqLimit: String,
        val installmentsRequestLimit: String
    )