package com.github.xepozz.external_inspections.index

import com.github.xepozz.external_inspections.models.*
import com.github.xepozz.external_inspections.settings.ExternalInspectionsSettings
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.ProjectManager
import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.DataInput
import java.io.DataOutput
import java.util.*

typealias IndexKey = String
typealias IndexValue = Collection<Diagnostic>

class ExternalDiagnosticsIndex : FileBasedIndexExtension<IndexKey, IndexValue>() {
    companion object {
        val NAME = ID.create<IndexKey, IndexValue>("ExternalInspections.ExternalDiagnosticsIndex")
    }

    private val xmlDecoder = XML.recommended_1_0 {
        xmlVersion = XmlVersion.XML10
        xmlDeclMode = XmlDeclMode.Auto
        repairNamespaces = true
    }

    private val jsonDecoder = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun getName(): ID<IndexKey, IndexValue> = NAME

    override fun getIndexer(): DataIndexer<IndexKey, IndexValue, FileContent> {
        return DataIndexer { inputData ->
            try {
                val content = inputData.contentAsText.toString()
                if (content.isBlank()) return@DataIndexer emptyMap()
                val extension = inputData.file.extension?.lowercase(Locale.getDefault())
                val result = if (extension == "json") {
                    jsonDecoder.decodeFromString(
                        deserializer = ExternalInspections.serializer(),
                        string = content,
                    )
                } else {
                    xmlDecoder.decodeFromString(
                        deserializer = ExternalInspections.serializer(),
                        string = content,
                    )
                }
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
        ProjectManager
            .getInstance()
            .openProjects
            .any { project ->
                val settings = ExternalInspectionsSettings.getInstance(project)
                settings.state.filePatterns.any { file.name.wildcardMatches(it) }
            }
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
            out.writeInt(diagnostic.intentions.list.size)
            for (intention in diagnostic.intentions.list) {
                when (intention) {
                    is CommandIntention -> {
                        out.writeByte(0)
                        out.writeUTF(intention.name)
                        out.writeUTF(intention.binary)
                        out.writeInt(intention.arguments.size)
                        for (argument in intention.arguments) {
                            out.writeUTF(argument)
                        }
                    }

                    is ReplaceIntention -> {
                        out.writeByte(1)
                        out.writeUTF(intention.name)
                        out.writeUTF(intention.newText)
                    }
                }
            }
        }
    }

    override fun read(`in`: DataInput): IndexValue {
        val size = `in`.readInt()
        val result = mutableListOf<Diagnostic>()
        repeat(size) {
            val message = `in`.readUTF()
            val start = readNullableInt(`in`)
            val end = readNullableInt(`in`)
            val file = `in`.readUTF()
            val level = `in`.readUTF()
            val intentionsSize = `in`.readInt()
            val intentions = mutableListOf<Intention>()
            repeat(intentionsSize) {
                val type = `in`.readByte()
                when (type.toInt()) {
                    0 -> {
                        val name = `in`.readUTF()
                        val binary = `in`.readUTF()
                        val argumentsSize = `in`.readInt()
                        val arguments = mutableListOf<String>()
                        repeat(argumentsSize) {
                            arguments.add(`in`.readUTF())
                        }
                        intentions.add(CommandIntention(name, binary, arguments))
                    }

                    1 -> {
                        val name = `in`.readUTF()
                        val newText = `in`.readUTF()
                        intentions.add(ReplaceIntention(name, newText))
                    }
                }
            }
            result.add(
                Diagnostic(
                    message = message,
                    start = start,
                    end = end,
                    file = file,
                    level = level,
                    intentions = Intentions(intentions)
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

fun String.wildcardMatches(pattern: String): Boolean {
    // 1. Escape special regex characters in the pattern, except for * and ?
    val regexPattern = pattern.replace(Regex("""[.\\+^$\[\](){}|-]""")) {
        "\\" + it.value
    }
        // 2. Replace wildcard characters with their regex equivalents
        .replace("?", ".")
        .replace("*", ".*")
        // 3. Ensure the regex matches the entire string, not just a partial match
        .let { "^$it$" }

    // 4. Create a Regex object and check for a match
    return Regex(regexPattern).matches(this)
}