package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
class AIDProvider (private val context: Context){
    companion object {
        var aidList = mutableListOf<AidData>()
    }
    private fun loadAIDs(): List<AidData> =
        loadAidsFromJson().ifEmpty { getDefaultAids() }

    fun getAIDS(mandatory: Boolean): List<AidData> {
        Log.i("AIDS", "Verificando si es mandatorio....")

        if (mandatory || aidList.isEmpty()) {
            Log.i("AIDS", "ES MANDATORIO")
            Log.d("AIDS", "Cargando AIDs desde JSON o valores por defecto...")
            aidList.clear() // Asegura que los datos anteriores no interfieran
            aidList.addAll(loadAIDs())
        }else{
            Log.w("AIDS", "La actualización es correcta. No se necesita actualizar")
        }
        return aidList
    }

    private fun loadAidsFromJson(): List<AidData> {
        return try {
            val json = context.assets.open("aids.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<AidData>>() {}.type
            val parsedJson: List<AidData> = Gson().fromJson(json, type)

            Log.i("AIDS", "Checking AIDS loading process via json is empty")
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

    fun getDefaultAids(): List<AidData> {
        return listOf(
            AidData(
                aid = "A0000000031010",
                applicationLabel = "Visa Credit",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = false
            ),
            AidData(
                aid = "A0000000032010",
                applicationLabel = "Visa Debit",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = false
            ),
            AidData(
                aid = "A0000000041010",
                applicationLabel = "MasterCard Credit",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = false
            ),
            AidData(
                aid = "A0000000043060",
                applicationLabel = "MasterCard Debit",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = false
            ),
            AidData(
                aid = "A0000001523010",
                applicationLabel = "Credibanco AID 1",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = true
            ),
            AidData(
                aid = "A0000001524010",
                applicationLabel = "Credibanco AID 2",
                terminalCapabilities = "E000F0",
                additionalTerminalCapabilities = "F000F0A001",
                terminalType = 0x00,
                transactionCurrencyCode = "0840",
                terminalCountryCode = "032",
                contactlessEnabled = true
            )
        )
    }
}