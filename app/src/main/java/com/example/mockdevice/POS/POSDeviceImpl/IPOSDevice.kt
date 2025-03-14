package com.example.mockdevice.POS.POSDeviceImpl

interface IPOSDevice {
    //BT Connection
    data class BTDeviceInfo(val address: String, val name: String, val paired:Boolean)

    fun scan(timeout: Int):Set<BTDeviceInfo>
    fun connect(deviceInfo: BTDeviceInfo, timeout: Int): Boolean
    fun disconnect()
    fun isConnect(): Boolean

    //Crypto
    fun isKeyInstalled(keyIndex:Byte):Boolean
    fun getKCV(keyIndex:Byte): String
    fun injectKey(keyType: EKeyType, keyIndex: Byte, key: String):Boolean

    enum class EPinMode{
        ONLINE,
        OFFLINE,
        DUKPT
    }

    data class EPinData(
        val mandatory: Boolean = false,
        val keyIndex: Byte,
        val minLength: Byte = 4,
        val maxLength: Byte = 12,
        val pan: String,
        val mode: EPinMode = EPinMode.ONLINE,
        val timeout: Int = 30000,
        var pinBlock: String = "",
        var result: Boolean = false
    )

    fun requestPIN(pinData: EPinData): Boolean

    //EMV Transactions
    enum class ECardType {
        MAGNETIC, CHIP, CONTACTLESS
    }

    fun detectCard(supportedCards: Set<ECardType>, supportFallback: String, timeout: Int): Boolean

    data class TerminalData(val terminalId: String, val merchantId: String, val categoryCode: String)

    enum class ETransType(val code: String) {
        PURCHASE("00"),
        CASH_WITHDRAWAL("01"),
        REVERSAL("02"),
        ADJUSTMENT("03"),
        DEPOSIT("04"),
        PAYMENT("05"),
        FUNDS_TRANSFER("06"),
        BALANCE_INQUIRY("09"),
        PRE_AUTHORIZATION("20"),
        AUTHORIZATION_CAPTURE("21"),
        VOID_TRANSACTION("22"),
        BATCH_SETTLEMENT("30"),
        REFUND("40");

        companion object {
            fun fromCode(code: String): ETransType? {
                return entries.find { it.code == code }
            }
        }
    }


    data class TransactionData(val type: ETransType, val amount: String, val otherAmount: String, val stan: String, val tagList: String )

    fun startTransaction(terminalData: TerminalData, transactionData: TransactionData, pinData: EPinData): Boolean

    data class IssuerData(val responseCode: String, val scripts: String, val authCode: String)
    fun confirmTransaction( issuerData: IssuerData): Boolean

    fun getTlvData():String

    //EMV Parameters
    fun loadEmvParameters(capList:Set<CapkData>,aidList:Set<AidData>): Boolean
    fun configCapks(capList:Set<CapkData>):Boolean
    fun configAids(aidList:Set<AidData>):Boolean

    //Utilities
    enum class ELightColor {
        LIGHT_RED,
        LIGHT_GREEN,
        LIGHT_YELLOW,
        LIGHT_BLUE
    }

    fun setLightState(colors: Set<ELightColor>, state: Boolean)
    fun changeAllLightState(state: Boolean)

    fun displayMessages(messages: List<String>)
    fun getInfo(): AppInfo

    enum class EKeyboard{
        KEY_TIMEOUT,
        KEY_1,
        KEY_2,
        KEY_3,
        KEY_4,
        KEY_5,
        KEY_6,
        KEY_7,
        KEY_8,
        KEY_9,
        KEY_0,
        KEY_CLEAR,
        KEY_ENTER,
        KEY_CANCEL
    }

    fun waitKeys(keys: Set<EKeyboard>, timeout: Int): EKeyboard
}
