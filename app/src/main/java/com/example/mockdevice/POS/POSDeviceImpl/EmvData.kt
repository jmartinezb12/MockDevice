package com.example.mockdevice.POS.POSDeviceImpl

data class EmvData(
    val aids: List<AidData>,
    val capks: List<CapkData>
)

// Simulación con solo una llave CAPK y algunas AID de prueba
/*val mockEmvData = EmvData(
    aids = listOf(
        AidData(aid = "A0000000031010", applicationLabel = "Visa Credit"),
        AidData(aid = "A0000000032010", applicationLabel = "Visa Debit"),
        AidData(aid = "A0000000041010", applicationLabel = "Mastercard Credit")
    ),
    capks = listOf(
        CapkData(
            rid = "A000000003",
            index = 0x01, // Solo una llave CAPK de 1024 bits
            exponent = 0x03,
            modulus = "01".repeat(128), // Simulación de 1024 bits representados en HEX
            checksum = "FF".repeat(20)  // Simulación de checksum de 20 bytes en HEX
        )
    )
)*/