package com.github.xepozz.external_inspections.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("external-inspections")
data class ExternalInspections(
    @XmlChildrenName("diagnostics")
    val diagnostics: List<Diagnostic>
)

@Serializable
@XmlSerialName("diagnostic")
data class Diagnostic(
    val message: String,
    val start: Int,
    val end: Int,
    val file: String,
    val level: String,
)
