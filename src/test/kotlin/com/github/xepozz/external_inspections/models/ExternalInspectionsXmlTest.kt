package com.github.xepozz.external_inspections.models

import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.Assert.assertEquals
import org.junit.Test

class ExternalInspectionsXmlTest {
    private val xml = XML {
        repairNamespaces = true
    }

    @Test
    fun `test xml deserialization`() {
        val xmlString = """
            <external-inspections>
                <diagnostics>
                    <diagnostic message="Example error" start="0" end="10" file="example.txt" level="error" />
                </diagnostics>
            </external-inspections>
        """.trimIndent()

        val result = xml.decodeFromString<ExternalInspections>(xmlString)
        assertEquals(1, result.diagnostics.size)
        val diagnostic = result.diagnostics[0]
        assertEquals("Example error", diagnostic.message)
        assertEquals(0, diagnostic.start)
        assertEquals(10, diagnostic.end)
        assertEquals("example.txt", diagnostic.file)
        assertEquals("error", diagnostic.level)
    }

    @Test
    fun `test xml deserialization with missing diagnostics field`() {
        val xmlString = "<external-inspections/>"
        val result = xml.decodeFromString<ExternalInspections>(xmlString)
        assertEquals(0, result.diagnostics.size)
    }

    @Test
    fun `test xml deserialization with empty diagnostics tag`() {
        val xmlString = "<external-inspections><diagnostics/></external-inspections>"
        val result = xml.decodeFromString<ExternalInspections>(xmlString)
        assertEquals(0, result.diagnostics.size)
    }
}
