package com.github.xepozz.external_inspections.services

import com.github.xepozz.external_inspections.index.ExternalDiagnosticsIndex
import com.github.xepozz.external_inspections.models.Diagnostic
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex

@Service(Service.Level.PROJECT)
class ExternalDiagnosticsService(private val project: Project) {
    fun getDiagnosticsForFile(fileName: String): List<Diagnostic> {
        val diagnostics = mutableListOf<Diagnostic>()
        FileBasedIndex.getInstance().processValues(
            ExternalDiagnosticsIndex.NAME,
            fileName,
            null,
            { _, value ->
                diagnostics.addAll(value)
                true
            },
            GlobalSearchScope.allScope(project)
        )
        return diagnostics
    }
}
