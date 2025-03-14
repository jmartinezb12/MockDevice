package com.example.mockdevice.ACTIVITIES

import android.Manifest
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mockdevice.POS.POSDeviceImpl.ConfigEMV
import com.example.mockdevice.POS.POSDeviceImpl.EKeyType
import com.example.mockdevice.POS.POSDeviceImpl.MockPOSDevice
import com.example.mockdevice.POS.POSDeviceImpl.IPOSDevice
import com.example.mockdevice.POS.POSDeviceImpl.btScan

class MockPOSActivity  : AppCompatActivity() {
    private lateinit var posDevice: IPOSDevice
    val bluetoothManager = btScan(this)

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
        bluetoothManager.resetScannerState()
        requestBluetoothPermissions()
    }

    private fun requestBluetoothPermissions() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> { // Android 13+ (API 33)
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> { // Android 12 (API 31-32)
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> { // Android 10 y 11 (API 29-30)
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> { // Android 9 y anteriores
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            Log.d("MockTest", "✅ Permisos ya concedidos.")
            testMockDevice()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                Log.d("MockTest", "✅ Todos los permisos concedidos. Iniciando escaneo...")
                testMockDevice()
            } else {
                Log.e("MockTest", "❌ Permisos denegados. No se puede escanear Bluetooth.")
            }
        }

    private fun testMockDevice() {
        Log.d("MockTest", "Escaneando dispositivos...")
        Log.d("MockTest", "Inyectando clave en índice 3...")
        posDevice.injectKey(EKeyType.MASTER_KEY, 3, "1234567890ABCDEF")
        Log.d("MockTest", "KCV de índice 3: ${posDevice.getKCV(3)}")

        Log.d("MockTest", "Verificando clave instalada en índice 3...")
        val isKeySet = posDevice.isKeyInstalled(3)
        Log.d("MockTest", "Clave instalada en índice 3: $isKeySet")

        Log.d("MockTest", "Mostrando mensajes en pantalla...")
        posDevice.displayMessages(listOf("Mensaje 1", "Mensaje 2", "Mensaje 3"))

        Log.d("MockTest", "Obteniendo información del POS...")
        val info = posDevice.getInfo()
        Log.d("MockTest", "Información del POS: ${info.appName} - ${info.appVersion}")

        Log.d("MockTest", "Desconectando dispositivo...")
        posDevice.disconnect()

        EMVConfig()
        bluetoothManager.startScan()
    }

    fun EMVConfig(){
        val configEMV = ConfigEMV(this,posDevice)
        Log.d("MockTest", "Configurando AIDs...")
        configEMV.ConfigAids(true)
        Log.d("MockTest", "Configurando CAPKs...")
        configEMV.ConfigCapks(true)
        Log.d("MockTest", "Ejecutando configuración general...")
        configEMV.configure(true)
    }

}