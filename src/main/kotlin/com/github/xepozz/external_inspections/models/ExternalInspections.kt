package com.github.xepozz.external_inspections.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlChildrenName
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("external-inspections", namespace = "http://j-plugins.github.io/external-inspections.xsd")
data class ExternalInspections(
    @XmlChildrenName("diagnostics", namespace = "http://j-plugins.github.io/external-inspections.xsd")
    val diagnostics: List<Diagnostic> = emptyList()
) : java.io.Serializable

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
    @XmlSerialName("intentions", namespace = "http://j-plugins.github.io/external-inspections.xsd")
    val intentions: Intentions = Intentions(),
) : java.io.Serializable

@Serializable
@XmlSerialName("intentions", namespace = "http://j-plugins.github.io/external-inspections.xsd")
data class Intentions(
    val list: List<Intention> = emptyList()
) : java.io.Serializable

@Serializable
sealed interface Intention : java.io.Serializable {
    val name: String
}

@Serializable
@XmlSerialName("command", namespace = "http://j-plugins.github.io/external-inspections.xsd")
data class CommandIntention(
    override val name: String,
    val binary: String,
    @XmlChildrenName("argument", namespace = "http://j-plugins.github.io/external-inspections.xsd")
    @XmlSerialName("arguments", namespace = "http://j-plugins.github.io/external-inspections.xsd")
    val arguments: List<String> = emptyList(),
) : Intention

@Serializable
@XmlSerialName("replace", namespace = "http://j-plugins.github.io/external-inspections.xsd")
data class ReplaceIntention(
    override val name: String,
    val newText: String,
) : Intention
