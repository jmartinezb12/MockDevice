package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import kotlin.random.Random


private const val MAX_KEY_INDEX = 10
private const val PIN_BLOCK = "0102030405060708"
private const val RESPONSE_OK = "OK"
private const val KCV_LENGTH = 6
private const val RANDOM_BOUND = 10

class MockPOSDevice(private val context: Context,private val appName: String, private val appVersion: String, private val deviceSerial: String, private val modelo: String) : IPOSDevice {
    private var connected = false
    private val random = Random(System.currentTimeMillis())


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

    private val btDevices = listOf(
        BTDeviceInfo("00:11:22:33:44:55", "D180000001"),
        BTDeviceInfo("66:77:88:99:AA:BB", "D190000001"),
        BTDeviceInfo("CC:DD:EE:FF:00:11", "D200000001"),
        BTDeviceInfo("22:33:44:55:66:77", "D180000002"),
        BTDeviceInfo("88:99:AA:BB:CC:DD", "D180000003")
    )

    private val keyData: MutableMap<Byte, Pair<Boolean, String>> = mutableMapOf(
        0.toByte() to (true to generateKCV()),
        5.toByte() to (true to generateKCV()),
        9.toByte() to (true to generateKCV())
    )

    private fun log(level: String = "INFO", message: String) {
        val stackTrace = Thread.currentThread().stackTrace[3]
        val functionName = stackTrace.methodName
        val className = stackTrace.className.substringAfterLast('.')
        val logTag = "$className: $functionName"

        when (level) {
            "INFO" -> Log.i(logTag, message)
            "DEBUG" -> Log.d(logTag, message)
            "WARN" -> Log.w(logTag, message)
            "ERROR" -> Log.e(logTag, message)
        }
    }

    override fun scan(filter: String): List<BTDeviceInfo> =
        btDevices.filter { it.name.contains(filter, ignoreCase = true) }
            .also { log("DEBUG", "Dispositivos escaneados: ${it.joinToString { dev -> dev.name }}") }

    override fun connect(macAddress: String): Boolean {
        log("INFO", "Intentando conectar con el dispositivo en la dirección: $macAddress")
        connected = true
        log("INFO", "Conexión exitosa con el dispositivo.")
        return connected
    }

    override fun disconnect() {
        log("INFO", "Desconectando el dispositivo...")
        connected = false
        log("INFO", "Dispositivo desconectado.")
    }

    override fun isConnect(): Boolean = connected.also {
        log("INFO", "Verificando si el dispositivo está conectado: $it")
    }

    override fun sendCommand(command: String) {
        log("INFO", "Enviando comando al POS: $command")
    }

    override fun receiveResponse(): String {
        log("INFO", "Recibiendo respuesta simulada del POS...")
        return RESPONSE_OK
    }

    override fun initTransaction(): Boolean {
        log("INFO", "Inicializando transacción simulada...")
        return true
    }

    override fun startTransaction(): Boolean {
        log("INFO", "Ejecutando transacción simulada...")
        return true
    }

    override fun requestPIN(pinData: IPOSDevice.PinData): Boolean {
        log("INFO", "Solicitando PIN al usuario -> $pinData")
        pinData.pinBlock = PIN_BLOCK
        pinData.result = true
        log("INFO", "PinBlock: ${pinData.pinBlock}  result -> ${pinData.result}")
        return pinData.result
    }

    override fun confirmTransaction(): Boolean {
        log("INFO", "Confirmando transacción...")
        return true
    }

    override fun LoadEMVParameters() {
        /*log("INFO", "Cargando parámetros EMV simulados de Credibanco...")

        mockEmvData.aids.forEach {
            log("DEBUG", "AID cargado: ${java.lang.String(it.aid)} - ${it.applicationLabel}")
        }

        mockEmvData.capks.forEach {
            log("DEBUG", "CAPK cargado: RID=${String(it.rid)}, Index=${it.index}, Modulus Length=${it.modulus.length}")
        }*/
    }

    override fun ConfigCapks(capkList: List<CapkData>): Boolean {
        return if (capkList.isNotEmpty()) {
            Log.d("MockPOSDevice", "CAPKs configurados correctamente (${capkList.size} CAPKs)")
            true
        } else {
            Log.w("MockPOSDevice", "No se recibieron CAPKs, intentando cargar desde JSON...")
            val fallbackCapks = CAPKS(context).loadCapksFromJson()
            return if (fallbackCapks.isNotEmpty()) {
                Log.d("MockPOSDevice", "CAPKs cargados desde JSON (${fallbackCapks.size} CAPKs)")
                true
            } else {
                Log.e("MockPOSDevice", "No se encontraron CAPKs en JSON, abortando configuración.")
                false
            }
        }
    }

    override fun ConfigAids(aidList: List<AidData>): Boolean {
        return if (aidList.isNotEmpty()) {
            Log.d("MockPOSDevice", "AIDs configurados correctamente (${aidList.size} AIDs)")
            true
        } else {
            Log.w("MockPOSDevice", "No se recibieron AIDs, intentando cargar desde JSON...")
            val fallbackAids = AIDS(context).loadAidsFromJson()
            return if (fallbackAids.isNotEmpty()) {
                Log.d("MockPOSDevice", "AIDs cargados desde JSON (${fallbackAids.size} AIDs)")
                true
            } else {
                Log.e("MockPOSDevice", "No se encontraron AIDs en JSON, abortando configuración.")
                false
            }
        }
    }

    private fun generateKCV(): String = (1..KCV_LENGTH).map { random.nextInt(0, RANDOM_BOUND) }.joinToString("")

    override fun getKCV(keyIndex: Byte): String = keyData[keyIndex]?.second ?: "Clave no encontrada"

    private fun setKeyState(index: Byte, state: Boolean): Boolean {
        return if (index in 0..MAX_KEY_INDEX) {
            keyData[index] = state to generateKCV()
            log("DEBUG", "Key index $index state set to $state")
            true
        } else {
            log("WARN", "Invalid key index: $index")
            false
        }
    }

    override fun isKeyInstalled(keyIndex: Byte): Boolean = keyData.containsKey(keyIndex).also {
        log("DEBUG", "Checking if key index $keyIndex is installed: $it")
    }

    override fun injectKey(keyType: EKeyType, keyIndex: Byte, key: String): Boolean = setKeyState(keyIndex, true).also {
        log("INFO", "Injected key at index $keyIndex of type $keyType")
    }

    override fun displayMessages(messages: List<String>) {
        log("INFO", "Displaying messages:")
        messages.forEach { message -> log("DEBUG", message) }
    }

    override fun getInfo(): AppInfo = mockInfo.also {
        log("DEBUG", "Retrieving device information: ${it.appName} - ${it.appVersion}")
    }
}
