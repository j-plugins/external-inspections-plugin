package com.github.xepozz.external_inspections.inspections

import com.github.xepozz.external_inspections.services.MyProjectService
import com.intellij.codeInspection.*
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class ExternalLocalInspection : LocalInspectionTool() {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean) =
        file.project
            .service<MyProjectService>()
            .getDiagnosticsForFile(file.name)
            .map { diagnostic ->
                val start = (diagnostic.start ?: 0).coerceIn(0, file.textLength)
                val end = (diagnostic.end ?: start).coerceIn(start, file.textLength)
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
            .toTypedArray()

    override fun runForWholeFile() = true

    fun parseLevel(level: String) = when (level.lowercase()) {
        "error" -> ProblemHighlightType.ERROR
        "info" -> ProblemHighlightType.INFORMATION
        "warning" -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING
        "deprecated" -> ProblemHighlightType.LIKE_DEPRECATED
        "unused" -> ProblemHighlightType.LIKE_UNUSED_SYMBOL
        else -> ProblemHighlightType.INFORMATION
    }

    override fun getDisplayName() = "External Inspection"

    override fun getGroupDisplayName() = "External"

    override fun getShortName() = "ExternalLocal"
}
