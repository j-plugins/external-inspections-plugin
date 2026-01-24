package com.github.xepozz.external_inspections

import com.intellij.javaee.ResourceRegistrar
import com.intellij.javaee.StandardResourceProvider

class ExternalInspectionsResourceProvider : StandardResourceProvider {
    override fun registerResources(registrar: ResourceRegistrar) {
        registrar.addStdResource(
            "http://j-plugins.github.io/external-inspections.xsd",
            null,
            "/external-inspections.xsd",
            javaClass,
        )
    }
}
