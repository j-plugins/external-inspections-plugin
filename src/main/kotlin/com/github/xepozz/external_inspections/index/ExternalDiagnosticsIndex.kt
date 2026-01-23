package com.github.xepozz.external_inspections.index

import com.github.xepozz.external_inspections.models.Diagnostic
import com.github.xepozz.external_inspections.models.ExternalInspections
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.DataInput
import java.io.DataOutput

typealias IndexKey = String
typealias IndexValue = Collection<Diagnostic>

class ExternalDiagnosticsIndex : FileBasedIndexExtension<IndexKey, IndexValue>() {
    companion object {
        val NAME = ID.create<IndexKey, IndexValue>("com.github.xepozz.external_inspections.ExternalDiagnosticsIndex")
    }

    private val xmlDecoder = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Auto
        repairNamespaces = true
    }

    override fun getName(): ID<IndexKey, IndexValue> = NAME

    override fun getIndexer(): DataIndexer<IndexKey, IndexValue, FileContent> {
        return DataIndexer { inputData ->
            try {
                val xmlString = inputData.contentAsText.toString()
                if (xmlString.isBlank()) return@DataIndexer emptyMap()
                val result = xmlDecoder.decodeFromString(
                    deserializer = ExternalInspections.serializer(),
                    string = xmlString,
                )
                // Map diagnostics by the file they refer to
                result.diagnostics.groupBy { it.file }
            } catch (e: Exception) {
                thisLogger().warn("Failed to index ${inputData.file.path}", e)
                emptyMap()
            }
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer() = ExternalDiagnosticsExternalizer

    override fun getVersion(): Int = 3

    override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
        file.name == "inspections.xml"
    }

    override fun dependsOnFileContent(): Boolean = true
}

object ExternalDiagnosticsExternalizer : DataExternalizer<IndexValue> {
    override fun save(out: DataOutput, value: IndexValue) {
        out.writeInt(value.size)
        for (diagnostic in value) {
            out.writeUTF(diagnostic.message)
            writeNullableInt(out, diagnostic.start)
            writeNullableInt(out, diagnostic.end)
            out.writeUTF(diagnostic.file)
            out.writeUTF(diagnostic.level)
        }
    }

    override fun read(`in`: DataInput): IndexValue {
        val size = `in`.readInt()
        val result = mutableListOf<Diagnostic>()
        repeat(size) {
            result.add(
                Diagnostic(
                    message = `in`.readUTF(),
                    start = readNullableInt(`in`),
                    end = readNullableInt(`in`),
                    file = `in`.readUTF(),
                    level = `in`.readUTF()
                )
            )
        }
        return result
    }

    private fun writeNullableInt(out: DataOutput, value: Int?) {
        if (value == null) {
            out.writeBoolean(false)
        } else {
            out.writeBoolean(true)
            out.writeInt(value)
        }
    }

    private fun readNullableInt(`in`: DataInput): Int? {
        return if (`in`.readBoolean()) {
            `in`.readInt()
        } else {
            null
        }
    }
}