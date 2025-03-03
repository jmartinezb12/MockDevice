package com.kinpos.mposcredibanco.POS.POSDeviceImpl

enum class IconType(val value: Byte) {
    NONE(0),
    CARD(1),
    READER(2),
    HOST(3),
    ERROR(4),
    INFO(5),
    WARNING(6)
}

private const val PROMPT = "prompt"

enum class EPromptId {
    SYNCHRONIZED,
    KEY_ERROR,
    ENTRY_PIN
}

data class PromptMessage(
    val key: String = "",
    val message1: String = "",
    val message2: String = "",
    val stopProgress: Boolean = false,
    val cancellable: Boolean = false,
    val icon: IconType = IconType.NONE
) {
    companion object {
        fun messageManager(id: EPromptId): PromptMessage? {
            return when (id) {
                EPromptId.SYNCHRONIZED -> PromptMessage(
                    key = PROMPT,
                    message1 = "Sincronizando",
                    message2 = "Estamos configurando tu dátafono",
                    icon = IconType.HOST
                )

                EPromptId.KEY_ERROR -> PromptMessage(
                    key = PROMPT,
                    message1 = "Error Obteniendo Llaves*",
                    message2 = "Transaccion No Completada",
                    icon = IconType.ERROR
                )

                EPromptId.ENTRY_PIN -> PromptMessage(
                    key = PROMPT,
                    message1 = "Transacción en proceso",
                    message2 = "Digita tu clave",
                    icon = IconType.INFO
                )
                else -> null
            }
        }
    }
}


