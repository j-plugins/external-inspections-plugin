package com.github.xepozz.external_inspections.inspections

import com.github.xepozz.external_inspections.services.MyProjectService
import com.intellij.codeInspection.*
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class ExternalLocalInspection : LocalInspectionTool() {
    override fun runForWholeFile(): Boolean {
        return true
    }
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        val project = file.project
        val service = project.service<MyProjectService>()
        val diagnostics = service.getDiagnosticsForFile(file.name)
        println("diagnostics: $diagnostics")
        if (diagnostics.isEmpty()) return null

        val problems = diagnostics.map { diagnostic ->
            val start = diagnostic.start.coerceIn(0, file.textLength)
            val end = diagnostic.end.coerceIn(start, file.textLength)
            val range = TextRange(start, end)
            val level = parseLevel(diagnostic.level)

            manager.createProblemDescriptor(
                file,
                range,
                diagnostic.message,
                level,
                isOnTheFly
            )
        }

        return problems.toTypedArray()
    }

    fun parseLevel(level: String) = when (level.lowercase()) {
        "error" -> ProblemHighlightType.ERROR
        "info" -> ProblemHighlightType.INFORMATION
        "warning" -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING
        "deprecated" -> ProblemHighlightType.LIKE_DEPRECATED
        "unused" -> ProblemHighlightType.LIKE_UNUSED_SYMBOL
        else -> ProblemHighlightType.INFORMATION
    }

    override fun getDisplayName(): String {
        return "External Inspection"
    }

    override fun getGroupDisplayName(): String {
        return "External"
    }

    override fun getShortName(): String {
        return "ExternalLocal"
    }
}
