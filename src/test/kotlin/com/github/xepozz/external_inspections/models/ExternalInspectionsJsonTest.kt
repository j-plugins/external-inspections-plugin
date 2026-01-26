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
    @Test
    fun `test json deserialization with intentions`() {
        val jsonString = """
            {
              "diagnostics": [
                {
                  "message": "Example error",
                  "start": 0,
                  "end": 10,
                  "file": "example.txt",
                  "level": "error",
                  "intentions": {
                    "list": [
                      {
                        "type": "com.github.xepozz.external_inspections.models.CommandIntention",
                        "name": "Run linter",
                        "binary": "linter",
                        "arguments": ["--fix", "example.txt"]
                      },
                      {
                        "type": "com.github.xepozz.external_inspections.models.ReplaceIntention",
                        "name": "Replace with fix",
                        "newText": "fix"
                      }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val result = json.decodeFromString<ExternalInspections>(jsonString)
        assertEquals(1, result.diagnostics.size)
        val diagnostic = result.diagnostics[0]
        assertEquals(2, diagnostic.intentions.list.size)

        val commandIntention = diagnostic.intentions.list[0] as CommandIntention
        assertEquals("Run linter", commandIntention.name)
        assertEquals("linter", commandIntention.binary)
        assertEquals(listOf("--fix", "example.txt"), commandIntention.arguments)

        val replaceIntention = diagnostic.intentions.list[1] as ReplaceIntention
        assertEquals("Replace with fix", replaceIntention.name)
        assertEquals("fix", replaceIntention.newText)
    }
}
