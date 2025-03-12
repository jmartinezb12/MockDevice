package com.example.mockdevice.POS.POSDeviceImpl

data class AidData(
    val aid: String,
    val version: String,
    val TACDenial: String,
    val TACOnline: String,
    val TACDefault: String,
    val terminalFloorLimit: String,
    val threshold: String,
    val dDOL: String,
    val tDOL: String,
    val permiteAIDParcial: Byte,
    val nombre: String,

    // Campos adicionales para la versión extendida
    val terminalType: Byte,
    val transCurrencyExponent: Byte,
    val terminalCountryCode: String,
    val transactionCurrencyCode: String,
    val terminalCapabilities: String,
    val additionalTerminalCapabilities: String,
    val AIDLen: Byte
) {
    // Constructor secundario (versión reducida, sin configuración avanzada)
    constructor(
        aid: String,
        version: String,
        TACDenial: String,
        TACOnline: String,
        TACDefault: String,
        terminalFloorLimit: String,
        threshold: String,
        dDOL: String,
        tDOL: String,
        permiteAIDParcial: Byte,
        nombre: String
    ) : this(
        aid, version, TACDenial, TACOnline, TACDefault, terminalFloorLimit, threshold, dDOL, tDOL, permiteAIDParcial, nombre,
        0x00, 0x00, "", "", "", "", 0x00
    )
}

