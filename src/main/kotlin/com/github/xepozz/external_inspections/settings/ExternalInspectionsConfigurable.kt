package com.github.xepozz.external_inspections.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.JBTable
import com.intellij.util.indexing.FileBasedIndex
import com.github.xepozz.external_inspections.index.ExternalDiagnosticsIndex
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.LabelPosition
import com.intellij.util.ui.ColumnInfo
import com.intellij.util.ui.ListTableModel
import javax.swing.JComponent

class ExternalInspectionsConfigurable(private val project: Project) : BoundConfigurable("External Inspections") {
    private val settings = ExternalInspectionsSettings.getInstance(project)
    private val model: ListTableModel<String> = ListTableModel<String>(
        object : ColumnInfo<String, String>("File Name") {
            override fun valueOf(item: String): String = item
            override fun isCellEditable(item: String): Boolean = true
            override fun setValue(item: String, value: String) {
                if (item == value) return
                val index = model.indexOf(item)
                if (index != -1) {
                    model.removeRow(index)
                    model.insertRow(index, value)
                }
            }
        }
    )

    override fun createPanel(): DialogPanel {
        model.items = settings.state.filePatterns.toMutableList()

        val table = JBTable(model)
        val decorator = ToolbarDecorator.createDecorator(table)
            .setAddAction {
                model.addRow("")
            }
            .setRemoveAction {
                val index = table.selectedRow
                if (index != -1) {
                    model.removeRow(index)
                }
            }
            .disableUpDownActions()

        return panel {
            row {
                cell(decorator.createPanel())
                    .align(Align.FILL)
                    .label("File Patterns", LabelPosition.TOP)
                    .comment("Files matching these names will be indexed for external diagnostics.")
            }
        }
    }

    override fun isModified(): Boolean {
        return super.isModified() || model.items != settings.state.filePatterns
    }

    override fun apply() {
        settings.state.filePatterns.clear()
        settings.state.filePatterns.addAll(model.items.filter { it.isNotEmpty() })
        FileBasedIndex.getInstance().requestRebuild(ExternalDiagnosticsIndex.NAME)
    }

    override fun reset() {
        super.reset()
        model.items = settings.state.filePatterns.toMutableList()
    }
}
