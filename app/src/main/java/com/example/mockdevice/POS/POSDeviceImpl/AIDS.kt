package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
class AIDS (private val context: Context, private val mockPOSDevice: IPOSDevice){
    companion object {
        private var Aidlist = mutableListOf<AidData>()
    }
    fun process(mandatory: Boolean): Boolean {
        return if (Aidlist.isNotEmpty()) {
            Log.d("AIDS", "Usando AIDs desde la caché (${Aidlist.size} AIDs)")
            mockPOSDevice.ConfigAids(Aidlist)
        } else {
            Log.d("AIDS", "Caché vacía, buscando en JSON...")
            val loadedAids = loadAidsFromJson()
            Aidlist.addAll(loadedAids)

            if (Aidlist.isEmpty()) {
                if (mandatory) {
                    Log.e("AIDS", "Error: No hay AIDs disponibles y mandatory es true")
                    return false // Error crítico
                } else {
                    Log.d("AIDS", "No se encontraron AIDs en JSON, usando AIDs por defecto")
                    Aidlist.addAll(getDefaultAids())
                }
            }

            mockPOSDevice.ConfigAids(Aidlist)
        }
    }

    fun loadAidsFromJson(): List<AidData> {
        return try {
            val json = context.assets.open("aids.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<AidData>>() {}.type
            val parsedJson: List<AidData> = Gson().fromJson(json, type)

            if (parsedJson.isEmpty()) {
                Log.e("AIDS", "Error: JSON de AIDs vacío.")
            } else {
                Log.d("AIDS", "AIDs cargados correctamente desde JSON: ${Gson().toJson(parsedJson)}")
            }
            parsedJson
        } catch (ex: IOException) {
            Log.e("AIDS", "Error al cargar AIDs desde JSON: ${ex.message}")
            emptyList()
        } catch (ex: JsonSyntaxException) {
            Log.e("AIDS", "Formato incorrecto en JSON: ${ex.message}")
            emptyList()
        }
    }

    private fun getDefaultAids(): List<AidData> {
        return listOf(
            AidData(
                aid = "A0000000041010",
                applicationLabel = "MasterCard",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = false
            )
        )
    }
}