package com.github.xepozz.external_inspections.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("external-inspections", namespace = "http://j-plugins.github.io/external-inspections.xsd")
data class ExternalInspections(
    @XmlChildrenName("diagnostics", namespace = "http://j-plugins.github.io/external-inspections.xsd")
    val diagnostics: List<Diagnostic> = emptyList()
): java.io.Serializable

@Serializable
@XmlSerialName("diagnostic", namespace = "http://j-plugins.github.io/external-inspections.xsd")
data class Diagnostic(
    val message: String = "Unknown error",
    @Serializable(with = LenientIntSerializer::class)
    val start: Int? = null,
    @Serializable(with = LenientIntSerializer::class)
    val end: Int? = null,
    val file: String = "",
    val level: String = "error",
): java.io.Serializable
