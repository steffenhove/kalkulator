package no.steffenhove.betongkalkulator.ui.utils

import no.steffenhove.betongkalkulator.utils.UnitConverter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class UnitConverterTest {

    @Test
    fun testConvert() {
        val result = UnitConverter.convert(1000.0, "mm", "m")
        assertEquals(1.0, result, 0.0)
    }
}