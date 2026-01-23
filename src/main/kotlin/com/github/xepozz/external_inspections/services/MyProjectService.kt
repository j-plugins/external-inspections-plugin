package com.github.xepozz.external_inspections.services

import com.github.xepozz.external_inspections.ExternalInspectionsBundle
import com.github.xepozz.external_inspections.models.Diagnostic
import com.github.xepozz.external_inspections.models.ExternalInspections
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File

@Service(Service.Level.PROJECT)
class MyProjectService(private val project: Project) {

    private var diagnostics: List<Diagnostic> = emptyList()

    private val xmlDecoder: XML by lazy {
        XML {
            xmlVersion = XmlVersion.XML10
            xmlDeclMode = XmlDeclMode.Auto
            indentString = "  "
            repairNamespaces = true
        }
    }

    init {
        thisLogger().info(ExternalInspectionsBundle.message("projectService", project.name))
        loadDiagnostics()
    }

    private fun loadDiagnostics() {
        val inspectionsFile = File(project.basePath, "inspections.xml")
        if (!inspectionsFile.exists()) {
            thisLogger().warn("Inspections file not found at ${inspectionsFile.absolutePath}")
            return
        }

        try {
            val xmlString = inspectionsFile.readText()
            println("read: $xmlString")
            val result = xmlDecoder.decodeFromString(
                deserializer = ExternalInspections.serializer(),
                string = xmlString,
            )
            diagnostics = result.diagnostics
            thisLogger().info("Loaded ${diagnostics.size} diagnostics")
        } catch (e: Exception) {
            thisLogger().error("Failed to load diagnostics", e)
        }
    }

    fun getDiagnosticsForFile(fileName: String): List<Diagnostic> {
        return diagnostics.filter { it.file == fileName }
    }

    fun reload() {
        loadDiagnostics()
    }
}
