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
            AidData("A0000000041010", "0002", "0000000000", "FC50B8F800", "FC50B8A000", "00000000", "00000000", "9F3704000000000000000000000000000000000000", "9F02065F2A029A039C0195059F37040000000000", 0x00, "MasterCard"),
            AidData("A0000000043060", "0002", "0000000000", "FC50B8F800", "FC50B8A000", "00000000", "00000000", "9F3704000000000000000000000000000000000000", "9F02065F2A029A039C0195059F37040000000000", 0x00, "Maestro"),
            AidData("A0000000031010", "008C", "0010000000", "DC4004F800", "DC4000A800", "00000000", "00000000", "039F3704", "0F9F02065F2A029A039C0195059F3704", 0x01, "VISA"),
            AidData("A0000000032010", "008C", "0010000000", "DC4004F800", "DC4000A800", "00000000", "00000000", "039F3704", "0F9F02065F2A029A039C0195059F3704", 0x01, "VISAELECTRON"),
            AidData("A0000000033010", "008C", "0010000000", "DC4004F800", "DC4000A800", "00000000", "00000000", "9F3704000000000000000000000000000000000000", "0000000000000000000000000000000000000000", 0x01, "INTERLINK"),
            AidData("A0000000031020", "008C", "0010000000", "DC4004F800", "DC4000A800", "00000000", "00000000", "9F3704000000000000000000000000000000000000", "9F02060000000000000000000000000000000000", 0x00, "VISAPriv"),
            AidData("F04C4154414D00", "008C", "0010000000", "DC4004F800", "DC4000A800", "00000000", "00000000", "039F3704", "0F9F02065F2A029A039C0195059F3704", 0x01, "WHITELEVEL"),
            AidData("A00000030559", "008D", "0010000000", "DC4004F800", "DC4000A800", "00000000", "00000000", "039F3704", "209F02060000000000000000000000000000000000", 0x01, "CLAVE"),
            AidData("A00000049999", "0002", "0000000000", "FC50A88000", "FC50A88000", "00000000", "00000000", "039F3704", "209F02060000000000000000000000000000000000", 0x00, "Clave")
        )
    }
}