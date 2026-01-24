package com.github.xepozz.external_inspections.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("external-inspections")
data class ExternalInspections(
    @XmlChildrenName("diagnostics")
    val diagnostics: List<Diagnostic> = emptyList()
): java.io.Serializable

@Serializable
@XmlSerialName("diagnostic")
data class Diagnostic(
    val message: String = "Unknown error",
    @Serializable(with = LenientIntSerializer::class)
    val start: Int? = null,
    @Serializable(with = LenientIntSerializer::class)
    val end: Int? = null,
    val file: String = "",
    val level: String = "error",
): java.io.Serializable
