package com.example.mockdevice.ACTIVITIES

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mockdevice.POS.POSDeviceImpl.EKeyType
import com.example.mockdevice.POS.POSDeviceImpl.IPOSDevice
import com.example.mockdevice.POS.POSDeviceImpl.MockPOSDevice

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mockDevice = MockPOSDevice(applicationContext,"MockApp", "1.0", "123456789", "D180S")

        Log.d("MockTest", "Escaneando dispositivos...")
        val devices = mockDevice.scan("D180")
        devices.forEach { Log.d("MockTest", "Encontrado: ${it.name} - ${it.address}") }

        Log.d("MockTest", "Intentando conectar...")
        if (mockDevice.connect("00:11:22:33:44:55")) {
            Log.d("MockTest", "Conectado correctamente")
        }

        Log.d("MockTest", "Inyectando clave en índice 3...")
        mockDevice.injectKey(EKeyType.MASTER_KEY, 3, "1234567890ABCDEF")
        Log.d("MockTest", "KCV de índice 3: ${mockDevice.getKCV(3)}")

        Log.d("MockTest", "Solicitando PIN...")
        val pinData = IPOSDevice.PinData(3, pan = "1234567890123456")
        mockDevice.requestPIN(pinData)

        Log.d("MockTest", "Desconectando dispositivo...")
        mockDevice.disconnect()
    }
}