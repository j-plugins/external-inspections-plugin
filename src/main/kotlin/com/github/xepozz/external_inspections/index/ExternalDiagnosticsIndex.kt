package com.github.xepozz.external_inspections.index

import com.github.xepozz.external_inspections.models.Diagnostic
import com.github.xepozz.external_inspections.models.ExternalInspections
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.DataInput
import java.io.DataOutput

class ExternalDiagnosticsIndex : FileBasedIndexExtension<String, List<Diagnostic>>() {
    companion object {
        val NAME = ID.create<String, List<Diagnostic>>("com.github.xepozz.external_inspections.ExternalDiagnosticsIndex")
    }

    private val xmlDecoder = XML {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Auto
        repairNamespaces = true
    }

    override fun getName(): ID<String, List<Diagnostic>> = NAME

    override fun getIndexer(): DataIndexer<String, List<Diagnostic>, FileContent> {
        return DataIndexer { inputData ->
            try {
                val xmlString = inputData.contentAsText.toString()
                val result = xmlDecoder.decodeFromString(
                    deserializer = ExternalInspections.serializer(),
                    string = xmlString,
                )
                // Map diagnostics by the file they refer to
                result.diagnostics.groupBy { it.file }
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<List<Diagnostic>> {
        return object : DataExternalizer<List<Diagnostic>> {
            override fun save(out: DataOutput, value: List<Diagnostic>) {
                out.writeInt(value.size)
                for (diagnostic in value) {
                    out.writeUTF(diagnostic.message)
                    out.writeInt(diagnostic.start)
                    out.writeInt(diagnostic.end)
                    out.writeUTF(diagnostic.file)
                    out.writeUTF(diagnostic.level)
                }
            }

            override fun read(`in`: DataInput): List<Diagnostic> {
                val size = `in`.readInt()
                val result = mutableListOf<Diagnostic>()
                repeat(size) {
                    result.add(
                        Diagnostic(
                            message = `in`.readUTF(),
                            start = `in`.readInt(),
                            end = `in`.readInt(),
                            file = `in`.readUTF(),
                            level = `in`.readUTF()
                        )
                    )
                }
                return result
            }
        }
    }

    override fun getVersion(): Int = 1

    override fun getInputFilter() = FileBasedIndex.InputFilter { file ->
        file.name == "inspections.xml"
    }

    override fun dependsOnFileContent(): Boolean = true
}
