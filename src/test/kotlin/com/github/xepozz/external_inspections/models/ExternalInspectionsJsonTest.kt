package com.github.xepozz.external_inspections.models

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ExternalInspectionsJsonTest {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `test json deserialization`() {
        val jsonString = """
            {
              "diagnostics": [
                {
                  "message": "Example error",
                  "start": 0,
                  "end": 10,
                  "file": "example.txt",
                  "level": "error"
                }
              ]
            }
        """.trimIndent()

        val result = json.decodeFromString<ExternalInspections>(jsonString)
        assertEquals(1, result.diagnostics.size)
        val diagnostic = result.diagnostics[0]
        assertEquals("Example error", diagnostic.message)
        assertEquals(0, diagnostic.start)
        assertEquals(10, diagnostic.end)
        assertEquals("example.txt", diagnostic.file)
        assertEquals("error", diagnostic.level)
    }

    @Test
    fun `test json deserialization with string offsets`() {
        val jsonString = """
            {
              "diagnostics": [
                {
                  "message": "Example error",
                  "start": "0",
                  "end": "10",
                  "file": "example.txt",
                  "level": "error"
                }
              ]
            }
        """.trimIndent()

        val result = json.decodeFromString<ExternalInspections>(jsonString)
        assertEquals(1, result.diagnostics.size)
        val diagnostic = result.diagnostics[0]
        assertEquals(0, diagnostic.start)
        assertEquals(10, diagnostic.end)
    }

    @Test
    fun `test json deserialization with missing diagnostics field`() {
        val jsonString = "{}"
        val result = json.decodeFromString<ExternalInspections>(jsonString)
        assertEquals(0, result.diagnostics.size)
    }
}
