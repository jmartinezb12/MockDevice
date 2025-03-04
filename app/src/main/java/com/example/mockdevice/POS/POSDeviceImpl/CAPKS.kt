package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken


class CAPKS(private val context: Context, private val mockPOSDevice: IPOSDevice) {
    companion object{
        private var Capklist = mutableListOf<CapkData>()
    }

    fun process(mandatory:Boolean):Boolean{
        return if (Capklist.isNotEmpty()) {
            Log.d("CAPKS", "Usando CAPKs desde la caché (${Capklist.size} CAPKs)")
            mockPOSDevice.ConfigCapks(Capklist)
        } else {
            Log.d("CAPKS", "Caché vacía, buscando en JSON...")
            val loadedCapks = loadCapksFromJson()
            Capklist.addAll(loadedCapks)

            if (Capklist.isEmpty()) {
                if (mandatory) {
                    Log.e("CAPKS", "Error: No hay CAPKs disponibles y mandatory es true")
                    return false // Error crítico
                } else {
                    Log.d("CAPKS", "No se encontraron CAPKs en JSON, usando CAPKs por defecto")
                    Capklist.addAll(getDefaultCapks())
                }
            }
            mockPOSDevice.ConfigCapks(Capklist)
        }
    }
    fun loadCapksFromJson(): List<CapkData> {
        return try {
            val json = context.assets.open("capks.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<CapkData>>() {}.type
            val parsedJson: List<CapkData> = Gson().fromJson(json, type)

            if (parsedJson.isEmpty()) {
                Log.e("CAPKS", "Error: JSON de CAPKs vacío.")
            } else {
                Log.d("CAPKS", "CAPKs cargados correctamente desde JSON: ${Gson().toJson(parsedJson)}")
            }
            parsedJson
        } catch (ex: IOException) {
            Log.e("CAPKS", "Error al cargar CAPKs desde JSON: ${ex.message}")
            emptyList()
        } catch (ex: JsonSyntaxException) {
            Log.e("CAPKS", "Formato incorrecto en JSON: ${ex.message}")
            emptyList()
        }
    }

    fun getDefaultCapks(): List<CapkData> {
        return listOf(
            CapkData(
                rid = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10).toString(),
                index = 0x01,
                exponent = 0x03,
                modulus = "DEFAULT_MODULUS_1",
                checksum = "CHECKSUM1",
                expiryDate = "260101",
                effectiveDate = "230101",
                secureHash = "DEFAULT_HASH_1"),
            CapkData(
                rid = byteArrayOf(0xA0.toByte(), 0x00, 0x00, 0x00, 0x03, 0x10, 0x10).toString(),
                index = 0x02,
                exponent = 0x03,
                modulus = "DEFAULT_MODULUS_2",
                checksum = "CHECKSUM2",
                expiryDate = "260101",
                effectiveDate = "230101",
                secureHash = "DEFAULT_HASH_2")
        )
    }
}