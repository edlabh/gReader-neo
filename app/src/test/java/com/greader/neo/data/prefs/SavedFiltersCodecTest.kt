package com.greader.neo.data.prefs

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Codec dos filtros salvos (DataStore): round-trip e tolerância a dados inválidos. */
class SavedFiltersCodecTest {

    @Test
    fun roundTripPreservesFilters() {
        val filters = listOf(
            SavedFilter(name = "Tecnologia", folderUid = "user/1/category/tech", unreadOnly = true),
            SavedFilter(name = "Busca", query = "kotlin", sortOldest = true),
            SavedFilter(name = "Feed", feedId = 42L),
        )

        val decoded = SavedFiltersCodec.decode(SavedFiltersCodec.encode(filters))

        assertEquals(filters, decoded)
    }

    @Test
    fun decodeToleratesMissingOrCorruptedJson() {
        assertTrue(SavedFiltersCodec.decode(null).isEmpty())
        assertTrue(SavedFiltersCodec.decode("").isEmpty())
        assertTrue(SavedFiltersCodec.decode("{not json").isEmpty())
    }

    @Test
    fun decodeIgnoresUnknownFields() {
        val raw = """[{"name":"F","feedId":7,"futureField":true}]"""

        val decoded = SavedFiltersCodec.decode(raw)

        assertEquals(1, decoded.size)
        assertEquals(7L, decoded.single().feedId)
    }
}
