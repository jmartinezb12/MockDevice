package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.Collections


class CAPKProvider(private val context: Context) {
    companion object{
        //var capkList = mutableListOf<CapkData>()
        var capkList:MutableSet<CapkData> = Collections.synchronizedSet(mutableSetOf())
    }
    private fun loadCAPKs(): Set<CapkData> =
        loadCapksFromJson().ifEmpty { getDefaultCapks() }

    fun getCAPKS(mandatory:Boolean): Set<CapkData> {
        Log.i("CAPKS", "Verificando si es mandatorio....")
        synchronized(capkList){

            if (mandatory || capkList.isEmpty()) {
                Log.i("CAPKS", "ES MANDATORIO")
                Log.d("CAPKS", "Cargando CAPKs desde JSON o valores por defecto...")
                capkList.clear() // Asegura que los datos anteriores no interfieran
                capkList.addAll(loadCAPKs())
            }else{
                Log.w("CAPKS", "La actualización es correcta. No se necesita actualizar")
            }
        }
        return capkList.toSet()
    }
    private fun loadCapksFromJson(): Set<CapkData> {
        Log.i("CAPKS", "Initiating CAPKS loading process via json.....")
        return try {
            val json = context.assets.open("capks.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<Set<CapkData>>() {}.type
            val parsedJson: Set<CapkData> = Gson().fromJson(json, type)

            Log.i("CAPKS", "Checking CAPKS loading process via json is empty")
            if (parsedJson.isEmpty()) {
                Log.e("CAPKS", "Error: JSON de CAPKs vacío.")
            } else {
                Log.d("CAPKS", "CAPKs cargados correctamente desde JSON: ${Gson().toJson(parsedJson)}")
            }
            parsedJson
        } catch (ex: IOException) {
            Log.e("CAPKS", "Error al cargar CAPKs desde JSON: ${ex.message}")
            emptySet()
        } catch (ex: JsonSyntaxException) {
            Log.e("CAPKS", "Formato incorrecto en JSON: ${ex.message}")
            emptySet()
        }
    }

    private fun getDefaultCapks(): Set<CapkData> {
        return setOf(
            CapkData(
                rid = "A000000003",
                index = 0x01,
                exponent = 0x03,
                modulus = "DEFAULT_MODULUS_1",
                checksum = "CHECKSUM1",
                expiryDate = "260101",
                effectiveDate = "230101",
                secureHash = "DEFAULT_HASH_1"
            ),
            CapkData(
                rid = "A000000003",
                index = 0x02,
                exponent = 0x03,
                modulus = "DEFAULT_MODULUS_2",
                checksum = "CHECKSUM2",
                expiryDate = "260101",
                effectiveDate = "230101",
                secureHash = "DEFAULT_HASH_2"
            ),
            CapkData(
                rid = "A000000004",
                index = 0x03,
                exponent = 0x03,
                modulus = "DEFAULT_MODULUS_3",
                checksum = "CHECKSUM3",
                expiryDate = "260101",
                effectiveDate = "230101",
                secureHash = "DEFAULT_HASH_3"
            ),
            CapkData(
                rid = "A000000004",
                index = 0x04,
                exponent = 0x03,
                modulus = "CREDIBANCO_MODULUS_1",
                checksum = "CREDIBANCO_CHECKSUM1",
                expiryDate = "270101",
                effectiveDate = "240101",
                secureHash = "CREDIBANCO_HASH_1"
            ),
            CapkData(
                rid = "A000000004",
                index = 0x05,
                exponent = 0x03,
                modulus = "CREDIBANCO_MODULUS_2",
                checksum = "CREDIBANCO_CHECKSUM2",
                expiryDate = "270101",
                effectiveDate = "240101",
                secureHash = "CREDIBANCO_HASH_2"
            )
        )
    }
}