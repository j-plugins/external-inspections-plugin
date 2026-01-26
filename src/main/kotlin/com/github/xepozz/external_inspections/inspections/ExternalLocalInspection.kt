package com.github.xepozz.external_inspections.inspections

import com.github.xepozz.external_inspections.models.CommandIntention
import com.github.xepozz.external_inspections.models.Intention
import com.github.xepozz.external_inspections.models.ReplaceIntention
import com.github.xepozz.external_inspections.services.ExternalDiagnosticsService
import com.intellij.codeInsight.intention.FileModifier
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.incorrectFormatting.ReplaceQuickFix
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.CapturingProcessHandler
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

class ExternalLocalInspection : LocalInspectionTool() {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean) =
        file.project
            .getService(ExternalDiagnosticsService::class.java)
            .getDiagnosticsForFile(file.name)
            .map { diagnostic ->
                val start = (diagnostic.start ?: 0).coerceIn(0, file.textLength)
                val end = (diagnostic.end ?: start).coerceIn(start, file.textLength)
                val range = TextRange(start, end)
                val level = parseLevel(diagnostic.level)
                val fixes = diagnostic.intentions.list.mapNotNull { intention ->
                    when (intention) {
                        is CommandIntention -> CommandQuickFix(intention)
                        is ReplaceIntention -> ReplaceQuickFix(intention, range)
                        else -> null
                    }
                }.toTypedArray()

                manager.createProblemDescriptor(
                    file,
                    range,
                    diagnostic.message,
                    level,
                    isOnTheFly,
                    *fixes
                )
            }
            .toTypedArray()

    private class CommandQuickFix(private val intention: CommandIntention) : LocalQuickFix {
        override fun getFamilyName() = intention.name
        override fun generatePreview(project: Project, previewDescriptor: ProblemDescriptor): IntentionPreviewInfo =
            IntentionPreviewInfo.EMPTY

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val commandLine = GeneralCommandLine(intention.binary)
                .withParameters(intention.arguments)
            val handler = CapturingProcessHandler(commandLine)
            handler.runProcess()
        }
    }

    private class ReplaceQuickFix(
        private val intention: ReplaceIntention,
        private val range: TextRange,
    ) : LocalQuickFix {
        override fun getFamilyName() = intention.name
        override fun getFileModifierForPreview(target: PsiFile) = ReplaceQuickFix(intention, range)

        override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
            val document = descriptor.psiElement.containingFile.viewProvider.document ?: return
            document.replaceString(range.startOffset, range.endOffset, intention.newText)
        }
    }

    override fun runForWholeFile() = true

    fun parseLevel(level: String) = when (level.lowercase()) {
        "error" -> ProblemHighlightType.ERROR
        "info" -> ProblemHighlightType.INFORMATION
        "warning" -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING
        "weak_warning" -> ProblemHighlightType.WEAK_WARNING
        "deprecated" -> ProblemHighlightType.LIKE_DEPRECATED
        "unused" -> ProblemHighlightType.LIKE_UNUSED_SYMBOL
        "unknown" -> ProblemHighlightType.LIKE_UNKNOWN_SYMBOL
        else -> ProblemHighlightType.INFORMATION
    }

    override fun getDisplayName() = "External Inspection"

    override fun getGroupDisplayName() = "External"

    override fun getShortName() = "ExternalLocal"
}
