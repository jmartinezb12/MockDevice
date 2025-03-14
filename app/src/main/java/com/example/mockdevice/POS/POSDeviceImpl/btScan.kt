package com.example.mockdevice.POS.POSDeviceImpl

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.mockdevice.POS.POSDeviceImpl.IPOSDevice.BTDeviceInfo
import kotlin.apply


class btScan(private val context: Context) {
    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val scannedDevices = mutableListOf<BTDeviceInfo>()
    var isScanning = false

        private set

    companion object {
        val BLUETOOTH_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    fun mostrarListaDeDispositivos() {
        if (discoveredDevices.isEmpty()) {
            Log.d("MockTest", "⚠️ No se encontraron dispositivos Bluetooth.")
            return
        }

        val deviceNames = discoveredDevices.map {
            "${it.name ?: "Dispositivo desconocido"} (${it.address})"
        }.toTypedArray()

        // Aseguramos que el AlertDialog se muestre en el hilo principal
        Handler(Looper.getMainLooper()).post {
            AlertDialog.Builder(context)
                .setTitle("Selecciona un dispositivo para emparejar")
                .setItems(deviceNames) { _, which ->
                    val selectedDevice = discoveredDevices[which]
                    Log.d("MockTest", "🔄 Intentando emparejar con: ${selectedDevice.name ?: "Desconocido"} (${selectedDevice.address})")
                    pairDevice(selectedDevice)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun pairDevice(device: BluetoothDevice) {
        try {
            Log.d("MockTest", "🔄 Iniciando emparejamiento con ${device.name ?: "Desconocido"}...")
            val method = device.javaClass.getMethod("createBond")
            val success = method.invoke(device) as Boolean

            if (success) {
                Log.d("MockTest", "✅ Solicitud de emparejamiento enviada a ${device.name}")
            } else {
                Log.e("MockTest", "❌ No se pudo iniciar el emparejamiento con ${device.name}")
            }
        } catch (e: Exception) {
            Log.e("MockTest", "❌ Error al emparejar con ${device.name}: ${e.message}")
        }
    }

    fun hasBluetoothPermissions(): Boolean {
        val grantedPermissions = BLUETOOTH_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        Log.d("MockTest", "Permisos concedidos: $grantedPermissions")
        return grantedPermissions.size == BLUETOOTH_PERMISSIONS.size
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled == true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startScan() {
        if (!hasBluetoothPermissions()) {
            Log.e("MockTest", "❌ No tienes permisos para escanear dispositivos Bluetooth.")
            return
        }
        Log.d("MockTest", "✅ Permisos Bluetooth correctos.")

        if (!isBluetoothEnabled()) {
            Log.e("MockTest", "❌ Bluetooth está desactivado.")
            return
        }

        if (isScanning) {
            Log.w("MockTest", "⚠️ Un escaneo ya está en curso. No se iniciará otro.")
            return
        }

        if (bluetoothAdapter?.isDiscovering == true) {
            Log.w("MockTest", "⚠️ Ya hay un escaneo en curso. Cancelando y esperando para reiniciar...")
            bluetoothAdapter?.cancelDiscovery()
            Handler(Looper.getMainLooper()).postDelayed({
                startScan()
            }, 2000) // Espera 2 segundos antes de intentar nuevamente
            return
        }

        scannedDevices.clear()
        isScanning = true

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(bluetoothReceiver, filter)

        Log.d("MockTest", "🔍 Escaneando dispositivos Bluetooth Clásico...")
        val started = bluetoothAdapter?.startDiscovery()
        if (started == true) {
            Log.d("MockTest", "✅ Escaneo de Bluetooth Clásico iniciado...")
        } else {
            Log.e("MockTest", "❌ No se pudo iniciar el escaneo de Bluetooth.")
            isScanning = false
        }
    }

    fun stopScan(callback: (List<BTDeviceInfo>) -> Unit = {}) {
        if (isScanning) {
            bluetoothAdapter?.cancelDiscovery()
            context.unregisterReceiver(bluetoothReceiver)
            isScanning = false
            Log.d("MockTest", "✅ Escaneo detenido.")
            callback(scannedDevices)
        }
    }

    fun resetScannerState() {
        stopScan()
        scannedDevices.clear()
        Log.d("MockTest", "🔄 Estado del escáner reseteado correctamente.")
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (!discoveredDevices.any { d -> d.address == it.address }) {
                            discoveredDevices.add(it) // ✅ Agregar a la lista de dispositivos

                            val pairedStatus = if (it.bondState == BluetoothDevice.BOND_BONDED) "Emparejado" else "No emparejado"
                            Log.d("MockTest", "📡 Dispositivo detectado: ${it.name ?: "Desconocido"}, MAC: ${it.address}, Estado: $pairedStatus")
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("MockTest", "✅ Escaneo finalizado. 📌 Total dispositivos detectados: ${discoveredDevices.size}")

                    if (discoveredDevices.isNotEmpty()) {
                        mostrarListaDeDispositivos() // ✅ Mostrar lista al finalizar el escaneo
                    } else {
                        Log.d("MockTest", "⚠️ No se encontraron dispositivos Bluetooth.")
                    }
                }
            }
        }
    }
}