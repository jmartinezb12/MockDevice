package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import android.util.Log
import com.example.mockdevice.POS.POSDeviceImpl.EMVConfigException.Companion.AID_NO_AVALIABLE
import com.example.mockdevice.POS.POSDeviceImpl.EMVConfigException.Companion.CAPK_NO_AVALIABLE

class ConfigEMV (private val context:Context,private val posDevice: IPOSDevice){
    fun ConfigCapks(mandatory: Boolean):Boolean {
        Log.i("INFO_CONFIG_EMV","Getting CAPKS beginning....")
        val capkList = CAPKProvider(context).getCAPKS(mandatory)
        if (capkList.isEmpty()){
            Log.i("FAIL_CONFIG_EMV","Failing CAPKS process")
            throw EMVConfigException(CAPK_NO_AVALIABLE,"NON AVALIABLE CAPKS")
        }
        return posDevice.configCapks(capkList)

    }
    fun ConfigAids(mandatory: Boolean):Boolean{
        Log.i("INFO_CONFIG_EMV","Getting AIDS beginning....")
        val aidList = AIDProvider(context).getAIDS(mandatory)
        if (aidList.isEmpty()){
            Log.i("FAIL_CONFIG_EMV","Failing AIDS process")
            throw EMVConfigException(AID_NO_AVALIABLE,"NON AVALIABLE AIDS")
        }
        return posDevice.configAids(aidList)
    }
    fun configure(mandatory:Boolean):Boolean{

        if (!ConfigCapks(mandatory)){
            Log.w("ERROR_CONFIGURE_CAPKS","couldn't configure CAPKS correctly")
            return false
        }
        if(!ConfigAids(mandatory)){
            Log.w("ERROR_CONFIGURE_AIDS","couldn't configure AIDS correctly")
            return false
        }
        Log.i("SUCCESS","The EMV configuration was done correctly!!")
        return true
    }
}
