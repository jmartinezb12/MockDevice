package com.example.mockdevice.POS.POSDeviceImpl

import android.content.Context
import com.example.mockdevice.POS.POSDeviceImpl.EMVConfigException.Companion.AID_NO_AVALIABLE
import com.example.mockdevice.POS.POSDeviceImpl.EMVConfigException.Companion.CAPK_NO_AVALIABLE

class ConfigEMV (private val context:Context,private val posDevice: IPOSDevice){
    private fun ConfigCapks(mandatory: Boolean):Boolean {
        val capkList = CAPKProvider(context).getCAPKS(mandatory)
        if (capkList.isEmpty()){
            throw EMVConfigException(CAPK_NO_AVALIABLE,"NON AVALIABLE CAPKS",)
        }
        posDevice.ConfigCapks(capkList)
    }
    private fun ConfigAids(mandatory: Boolean):Boolean{
        val aidList = AIDProvider(context).getAIDS(mandatory)
        if (aidList.isEmpty()){
            throw EMVConfigException(AID_NO_AVALIABLE,"NON AVALIABLE AIDS",)
        }
        posDevice.ConfigAids(aidList)
    }
    fun execute(mandatory:Boolean):Boolean{

        ConfigAids(mandatory)
        if (ConfigCapks(mandatory)){

        }
    }
}
