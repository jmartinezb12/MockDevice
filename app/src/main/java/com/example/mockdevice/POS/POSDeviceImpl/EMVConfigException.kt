package com.example.mockdevice.POS.POSDeviceImpl

class EMVConfigException(
    val errorCode: Int = ERROR_UNKNOWN,
    message: String
) : Exception(message) {

    companion object {
        const val CAPK_NO_AVALIABLE = 1001
        const val AID_NO_AVALIABLE = 1002
        const val ERROR_UNKNOWN = 1099 // Código de error desconocido
    }
}