package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context

interface IPOSDevice {
    fun scan(filter: String):List<MockPOSDevice.BTDeviceInfo>
    fun connect(macAddress: String): Boolean
    fun disconnect()
    fun isConnect(): Boolean

    fun sendCommand(command: String)
    fun receiveResponse(): String?
    fun initTransaction(): Boolean
    fun startTransaction(): Boolean
    fun confirmTransaction(): Boolean
    fun LoadEMVParameters()
    fun ConfigCapks(capList:List<CapkData>)
    fun ConfigAids(aidList:List<AidData>)
    fun isKeyInstalled(keyIndex:Byte):Boolean
    fun getKCV(keyIndex:Byte): String
    fun injectKey(keyType: EKeyType, keyIndex: Byte, key: String):Boolean
    fun displayMessages(messages: List<String>)

    fun getInfo(): AppInfo

    enum class EPinMode{
        ONLINE,
        OFFLINE,
        DUKPT
    }

    data class PinData(
        val keyIndex: Byte,
        val minLength: Byte = 4,
        val maxLength: Byte = 12,
        val pan: String,
        val mode: EPinMode = EPinMode.ONLINE,
        val timeout: Int = 30000,
        var pinBlock: String = "",
        var result: Boolean = false
    )

    fun requestPIN(pinData: PinData): Boolean
}
