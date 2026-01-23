package com.github.xepozz.external_inspections.startup

import com.github.xepozz.external_inspections.models.ExternalInspections
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML

class MyProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        getRandomNumber2()
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    val xmlDecoder: XML by lazy {
        XML {
            xmlVersion = XmlVersion.XML10
            xmlDeclMode = XmlDeclMode.Auto
            indentString = "  "
            repairNamespaces = true
        }
    }

    fun getRandomNumber2() {
        val xmlString = """
        <external-inspections>
            <diagnostics>
                <diagnostic
                        message="help me"
                        start="0"
                        end="3"
                        file="1.php"
                />
            </diagnostics>
        </external-inspections>
        """.trimIndent()

        val result = xmlDecoder.decodeFromString(
            deserializer = ExternalInspections.serializer(),
            string = xmlString,
        )
        println("result: $result")
    }
}