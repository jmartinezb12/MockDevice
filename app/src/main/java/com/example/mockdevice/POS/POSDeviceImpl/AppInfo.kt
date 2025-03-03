package com.example.mockdevice.POS.POSDeviceImpl

data class AppInfo(
    val appName: String = "",
    val appVersion: String = "",
    val serial: String = "",
    val emvVersion: String = "",
    val entryVersion: String = "",
    val deviceVersion: String = "",
    val waveVersion: String = "",
    val mcVersion: String = "",
    val aidChecksum: String = "",
    val capkChecksum: String = "",
    val aeVersion: String = "",
    val appRevision: String = "",
    val battLevel: Int = 0,
    val isRSATerminal: Boolean = false,
    val isRSASFE: Boolean = false,
    val isPrivateRSA: Boolean = false,
    val isInitialized: Boolean = false,
    val model: String = ""
)