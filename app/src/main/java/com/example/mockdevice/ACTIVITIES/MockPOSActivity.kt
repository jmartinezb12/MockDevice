package com.example.mockdevice.ACTIVITIES

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mockdevice.POS.POSDeviceImpl.EKeyType
import com.example.mockdevice.POS.POSDeviceImpl.IPOSDevice
import com.example.mockdevice.POS.POSDeviceImpl.MockPOSDevice

class MockPOSActivity  : AppCompatActivity() {
    private lateinit var posDevice: IPOSDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el MockPOSDevice
        posDevice = MockPOSDevice(
            this,
            appName = "MockPOS",
            appVersion = "1.0",
            deviceSerial = "MOCK12345",
            modelo = "D180S"
        )

        Log.d("MockTest", "MockPOSActivity iniciada")
        // Inicializar carga de AIDs y CAPKs con el contexto de la actividad

        posDevice.ConfigAids(this,false)
        posDevice.ConfigCapks(this,false)
        // Ejecutar pruebas de funcionalidad
        testMockDevice()
    }

    private fun testMockDevice() {
        Log.d("MockTest", "Escaneando dispositivos...")
        val devices = posDevice.scan("D180")
        devices.forEach { Log.d("MockTest", "Encontrado: ${it.name} - ${it.address}") }

        Log.d("MockTest", "Intentando conectar...")
        if (posDevice.connect("00:11:22:33:44:55")) {
            Log.d("MockTest", "Conectado correctamente")
        }

        Log.d("MockTest", "Inyectando clave en índice 3...")
        posDevice.injectKey(EKeyType.MASTER_KEY, 3, "1234567890ABCDEF")
        Log.d("MockTest", "KCV de índice 3: ${posDevice.getKCV(3)}")

        Log.d("MockTest", "Solicitando PIN...")
        val pinData = IPOSDevice.PinData(3, pan = "1234567890123456")
        posDevice.requestPIN(pinData)

        Log.d("MockTest", "Verificando clave instalada en índice 3...")
        posDevice.isKeyInstalled(3)

        Log.d("MockTest", "Enviando comando 'TEST_COMMAND'...")
        posDevice.sendCommand("TEST_COMMAND")

        Log.d("MockTest", "Recibiendo respuesta del POS...")
        val response = posDevice.receiveResponse()
        Log.d("MockTest", "Respuesta recibida: $response")

        Log.d("MockTest", "Inicializando transacción...")
        if (posDevice.initTransaction()) {
            Log.d("MockTest", "Transacción inicializada correctamente")
        }

        Log.d("MockTest", "Iniciando transacción...")
        if (posDevice.startTransaction()) {
            Log.d("MockTest", "Transacción en proceso")
        }

        Log.d("MockTest", "Confirmando transacción...")
        if (posDevice.confirmTransaction()) {
            Log.d("MockTest", "Transacción confirmada exitosamente")
        }

        Log.d("MockTest", "Mostrando mensajes en pantalla...")
        posDevice.displayMessages(listOf("Mensaje 1", "Mensaje 2", "Mensaje 3"))

        Log.d("MockTest", "Obteniendo información del POS...")
        val info = posDevice.getInfo()
        Log.d("MockTest", "Información del POS: ${info.appName} - ${info.appVersion}")

        Log.d("MockTest", "Desconectando dispositivo...")
        posDevice.disconnect()

    }
}