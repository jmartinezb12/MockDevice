package com.kinpos.mposcredibanco.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import com.kinpos.mposcredibanco.POS.PAX.D180Device
class POSDeviceFactory {
    companion object {
        fun obtenerDispositivo(tipo: DeviceType, context: Context? = null): IPOSDevice {
            return when (tipo) {
                DeviceType.MOCK -> {
                    Log.i("POSDeviceFactory", "Modo prueba activado: usando MockPOSDevice")
                    context?.let { MockPOSDevice(it) }
                        ?: throw IllegalArgumentException("Context no puede ser nulo para MockPOSDevice")
                }

                DeviceType.D180 -> {
                    Log.i("POSDeviceFactory", "Creando instancia de D180Device")
                    context?.let { D180Device(it) }
                        ?: throw IllegalArgumentException("Context no puede ser nulo para D180Device")
                }
            }
        }
    }
}