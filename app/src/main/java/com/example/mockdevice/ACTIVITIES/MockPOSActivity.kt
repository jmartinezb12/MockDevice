package com.example.mockdevice.ACTIVITIES

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mockdevice.POS.POSDeviceImpl.AIDS
import com.example.mockdevice.POS.POSDeviceImpl.CAPKS
import com.example.mockdevice.POS.POSDeviceImpl.EKeyType
import com.example.mockdevice.POS.POSDeviceImpl.IPOSDevice
import com.example.mockdevice.POS.POSDeviceImpl.MockPOSDevice
import com.google.gson.Gson

class MockPOSActivity  : AppCompatActivity() {
    private lateinit var mockPOSDevice: MockPOSDevice
    private lateinit var aidsProcessor: AIDS
    private lateinit var capksProcessor: CAPKS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el MockPOSDevice
        mockPOSDevice = MockPOSDevice(
            this,
            appName = "MockPOS",
            appVersion = "1.0",
            deviceSerial = "MOCK12345",
            modelo = "D180S"
        )

        Log.d("MockTest", "MockPOSActivity iniciada")
        // Inicializar carga de AIDs y CAPKs con el contexto de la actividad
        aidsProcessor = AIDS(this, mockPOSDevice)
        capksProcessor = CAPKS(this, mockPOSDevice)
        Log.d("MockTest", "Cargando AIDs y CAPKs...")

        val aidsLoaded = aidsProcessor.process(mandatory = false)
        val capksLoaded = capksProcessor.process(mandatory = false)

        if (!aidsLoaded) {
            Log.e("MockPOSActivity", "ERROR: No se pudieron configurar los AIDs.")
        } else {
            Log.d("MockPOSActivity", "✅ AIDs configurados correctamente.")
            Log.d("MockPOSActivity", "📌 AIDs Cargados: ${Gson().toJson(AIDS.Companion)}")
        }

        if (!capksLoaded) {
            Log.e("MockPOSActivity", "ERROR: No se pudieron configurar los CAPKs.")
        } else {
            Log.d("MockPOSActivity", "✅ CAPKs configurados correctamente.")
            Log.d("MockPOSActivity", "📌 CAPKs Cargados: ${Gson().toJson(CAPKS.Companion)}")
        }

        // Ejecutar pruebas de funcionalidad
        testMockDevice()
    }

    private fun testMockDevice() {
        Log.d("MockTest", "Escaneando dispositivos...")
        val devices = mockPOSDevice.scan("D180")
        devices.forEach { Log.d("MockTest", "Encontrado: ${it.name} - ${it.address}") }

        Log.d("MockTest", "Intentando conectar...")
        if (mockPOSDevice.connect("00:11:22:33:44:55")) {
            Log.d("MockTest", "Conectado correctamente")
        }

        Log.d("MockTest", "Inyectando clave en índice 3...")
        mockPOSDevice.injectKey(EKeyType.MASTER_KEY, 3, "1234567890ABCDEF")
        Log.d("MockTest", "KCV de índice 3: ${mockPOSDevice.getKCV(3)}")

        Log.d("MockTest", "Solicitando PIN...")
        val pinData = IPOSDevice.PinData(3, pan = "1234567890123456")
        mockPOSDevice.requestPIN(pinData)

        Log.d("MockTest", "Verificando clave instalada en índice 3...")
        mockPOSDevice.isKeyInstalled(3)

        Log.d("MockTest", "Enviando comando 'TEST_COMMAND'...")
        mockPOSDevice.sendCommand("TEST_COMMAND")

        Log.d("MockTest", "Recibiendo respuesta del POS...")
        val response = mockPOSDevice.receiveResponse()
        Log.d("MockTest", "Respuesta recibida: $response")

        Log.d("MockTest", "Inicializando transacción...")
        if (mockPOSDevice.initTransaction()) {
            Log.d("MockTest", "Transacción inicializada correctamente")
        }

        Log.d("MockTest", "Iniciando transacción...")
        if (mockPOSDevice.startTransaction()) {
            Log.d("MockTest", "Transacción en proceso")
        }

        Log.d("MockTest", "Confirmando transacción...")
        if (mockPOSDevice.confirmTransaction()) {
            Log.d("MockTest", "Transacción confirmada exitosamente")
        }

        Log.d("MockTest", "Mostrando mensajes en pantalla...")
        mockPOSDevice.displayMessages(listOf("Mensaje 1", "Mensaje 2", "Mensaje 3"))

        Log.d("MockTest", "Obteniendo información del POS...")
        val info = mockPOSDevice.getInfo()
        Log.d("MockTest", "Información del POS: ${info.appName} - ${info.appVersion}")

        Log.d("MockTest", "Desconectando dispositivo...")
        mockPOSDevice.disconnect()

    }
}