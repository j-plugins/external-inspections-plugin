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
            <external-inspections xmlns="http://j-plugins.github.io/external-inspections.xsd">
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
        val xmlString = "<external-inspections xmlns=\"http://j-plugins.github.io/external-inspections.xsd\"/>"
        val result = xml.decodeFromString<ExternalInspections>(xmlString)
        assertEquals(0, result.diagnostics.size)
    }

    @Test
    fun `test xml deserialization with empty diagnostics tag`() {
        val xmlString = "<external-inspections xmlns=\"http://j-plugins.github.io/external-inspections.xsd\"><diagnostics/></external-inspections>"
        val result = xml.decodeFromString<ExternalInspections>(xmlString)
        assertEquals(0, result.diagnostics.size)
    }
    @Test
    fun `test xml deserialization with intentions`() {
        val xmlString = """
            <external-inspections xmlns="http://j-plugins.github.io/external-inspections.xsd">
                <diagnostics>
                    <diagnostic message="Example error" start="0" end="10" file="example.txt" level="error">
                        <intentions>
                            <command name="Run linter" binary="linter">
                                <arguments>
                                    <argument>--fix</argument>
                                    <argument>example.txt</argument>
                                </arguments>
                            </command>
                            <replace name="Replace with fix" newText="fix" />
                        </intentions>
                    </diagnostic>
                </diagnostics>
            </external-inspections>
        """.trimIndent()

        val result = xml.decodeFromString<ExternalInspections>(xmlString)
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
