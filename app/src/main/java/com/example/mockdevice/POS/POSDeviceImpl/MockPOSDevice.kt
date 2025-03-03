package com.example.mockdevice.POS.POSDeviceImpl

import android.util.Log
import kotlin.random.Random

class MockPOSDevice (private val appName: String, private val appVersion: String, private val deviceSerial: String, private val modelo: String): IPOSDevice {
    private var connected = false
    private val mockInfo = AppInfo(
        appName = "MockPOS",
        appVersion = "1.0",
        serial = "MOCK12345",
        emvVersion = "MockEMV1.0",
        entryVersion = "EntryV1",
        deviceVersion = "DeviceV1",
        waveVersion = "WaveV1",
        mcVersion = "McV1",
        aidChecksum = "AID123",
        capkChecksum = "CAPK123",
        aeVersion = "AEV1.0",
        appRevision = "Rev1",
        battLevel = 100,
        isRSATerminal = true,
        isRSASFE = false,
        isPrivateRSA = true,
        isInitialized = true,
        model = "D180S"
    )

    data class BTDeviceInfo(val address: String, val name: String)
    private val keyData: mutableMapOf<Byte, Pair<Boolean, String>>()

    override fun scan(filter: String): List<BTDeviceInfo> {
        val devices = listOf(
            BTDeviceInfo("00:11:22:33:44:55", "D180000001"),
            BTDeviceInfo("66:77:88:99:AA:BB", "D190000001"),
            BTDeviceInfo("CC:DD:EE:FF:00:11", "D200000001"),
            BTDeviceInfo("22:33:44:55:66:77", "d180000002"),
            BTDeviceInfo("88:99:AA:BB:CC:DD", "D180000003")
        ).filter { device -> device.name.contains(filter, ignoreCase = true) }

        // Log para ver los dispositivos encontrados en el mock
        Log.d("BluetoothMock", "Dispositivos escaneados: ${devices.joinToString { it.name }}")

        return devices
    }

    override fun connect(macAddress: String): Boolean {
        Log.i("MockPOSDevice", "Intentando conectar con el dispositivo en la dirección: $macAddress")
        connected = true
        Log.d("MockPOSDevice", "Conexión establecida con: $macAddress")
        return connected
    }

    override fun disconnect() {
        Log.i("MockPOSDevice", "Desconectando el dispositivo...")
        connected = false
        Log.i("MockPOSDevice", "Dispositivo desconectado.")
    }

    override fun isConnect(): Boolean {
        Log.i("MockPOSDevice", "Verificando si el dispositivo está conectado: $connected")
        return connected
    }

    override fun sendCommand(command: String) {
        Log.i("MockPOSDevice", "Enviando comando al POS: $command")
    }

    override fun receiveResponse(): String {
        Log.i("MockPOSDevice", "Recibiendo respuesta simulada del POS...")
        return "OK"
    }

    override fun initTransaction(): Boolean {
        Log.i("MockPOSDevice", "Inicializando transacción simulada...")
        return true
    }

    override fun startTransaction(): Boolean {
        Log.i("MockPOSDevice", "Ejecutando transacción simulada...")
        return true
    }

    override fun requestPIN(pinData: IPOSDevice.PinData): Boolean {
        Log.i("MockPOSDevice", "Solicitando PIN al usuario -> $pinData")
        pinData.pinBlock = "0102030405060708"
        pinData.result = true
        return pinData.result
    }

    override fun confirmTransaction(): Boolean {
        Log.i("MockPOSDevice", "Confirmando transacción...")
        return true
    }

    override fun LoadEMVParameters() {
        Log.i("MockPOSDevice", "Cargando parámetros EMV simulados...")
    }

    private fun generateKCV(): String {
        return (1..6).map { Random.nextInt(0, 10) }.joinToString("")
    }

    override fun getKCV(keyIndex:Byte):String{
        return keyData[keyIndex]?.second ?: "Clave no encontrada"
    }

    private fun setKeyState(index: Byte, state: Boolean) {
        if (index in 0..10) {
            val kcv = generateKCV()
            keyData[index] = Pair(state, kcv)
            Log.d("MOCK_POS", "Key index $index state set to $state")
        } else {
            Log.w("MOCK_POS", "Invalid key index: $index")
        }
    }
    override fun isKeyInstalled(keyIndex: Byte): Boolean {
        return if (keyIndex in 0..10) {
            setKeyState(0, true)
            setKeyState(3, true)
            setKeyState(5, true)
            val kcv = generateKCV()
            keyData[keyIndex] = Pair(true, kcv)
            Log.d("MOCK_POS", "Checking if key index $keyIndex is installed: $kcv")
            true
        } else {
            Log.w("MOCK_POS", "Invalid key index: $keyIndex")
            false
        }
    }

    override fun injectKey(keyType: EKeyType, keyIndex: Byte, key: String): Boolean {
        return if (keyIndex in 0..10) {
            keyData[keyIndex] = true
            Log.d("MOCK_POS", "Injecting key of type $keyType at index $keyIndex: Success")
            true
        } else {
            Log.w("MOCK_POS", "Failed to inject key at index $keyIndex: Invalid index")
            false
        }
    }

    override fun displayMessages(messages: List<String>) {
        Log.d("MOCK_POS", "Displaying messages:")
        messages.forEach { message -> Log.d("MOCK_POS", message) }
    }

    override fun getInfo(): AppInfo {
        Log.d("MOCK_POS", "Retrieving device information: ${mockInfo.appName} - ${mockInfo.appVersion}")
        return mockInfo
    }
}
