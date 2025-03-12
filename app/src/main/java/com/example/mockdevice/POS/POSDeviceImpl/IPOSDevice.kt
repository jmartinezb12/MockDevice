package com.example.mockdevice.POS.POSDeviceImpl

import com.example.mockdevice.POS.POSDeviceImpl.MockPOSDevice.BTDeviceInfo

interface IPOSDevice {
    //BT Connection
    fun scan(filter: String, timeout: Int):List<BTDeviceInfo>
    fun getPairedDevices(filter: String, timeout: Int): List<BTDeviceInfo>
    fun connect(macAddress: String, timeout: Int): Boolean
    fun disconnect()
    fun isConnect(): Boolean

    fun sendCommand(command: String)
    fun receiveResponse(): String?
    //EMV Transactions
    fun initTransaction(): Boolean
    fun startTransaction(): Boolean
    fun confirmTransaction(): Boolean

    //EMV Parameters
    fun LoadEMVParameters()
    fun ConfigCapks(capList:List<CapkData>):Boolean
    fun ConfigAids(aidList:List<AidData>):Boolean

    //Cripto
    fun isKeyInstalled(keyIndex:Byte):Boolean
    fun getKCV(keyIndex:Byte): String
    fun injectKey(keyType: EKeyType, keyIndex: Byte, key: String):Boolean
    enum class ELightColor {
        LIGHT_RED,
        LIGHT_GREEN,
        LIGHT_YELLOW,
        LIGHT_BLUE
    }
    //Utilities
    fun setLightState(colors: Set<ELightColor>, state: Boolean)
    fun changeAllLightState(state: Boolean) // Turns all LEDs ON or OFF
    fun displayMessages(messages: List<String>)

    fun getInfo(): AppInfo
    enum class LedColor {
        LIGHT_RED, LIGHT_GREEN, LIGHT_YELLOW, LIGHT_BLUE
    }
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
