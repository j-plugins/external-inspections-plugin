package com.github.xepozz.external_inspections

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.StandardResourceProvider

class ExternalInspectionsResourceProvider : StandardResourceProvider {
    override fun registerResources(registrar: ResourceRegistrar) {
        registrar.addStdResource(
            "https://raw.githubusercontent.com/j-plugins/external-inspections-plugin/main/src/main/resources/external-inspections.xsd",
            "external-inspections.xsd",
            javaClass.classLoader,
        )
        registrar.addStdResource(
            "http://j-plugins.github.io/external-inspections.xsd",
            "external-inspections.xsd",
            javaClass.classLoader,
        )
    }
}
