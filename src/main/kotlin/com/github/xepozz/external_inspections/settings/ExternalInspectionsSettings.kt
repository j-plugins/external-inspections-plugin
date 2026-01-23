package com.github.xepozz.external_inspections.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "ExternalInspectionsSettings", storages = [Storage("external_inspections.xml")])
class ExternalInspectionsSettings : SimplePersistentStateComponent<ExternalInspectionsSettings.State>(State()) {

    class State : BaseState() {
        var filePatterns by list<String>()

        init {
            if (filePatterns.isEmpty()) {
                filePatterns.add("*.inspections.xml")
            }
        }
    }

    companion object {
        fun getInstance(project: Project): ExternalInspectionsSettings = project.service()
    }
}
