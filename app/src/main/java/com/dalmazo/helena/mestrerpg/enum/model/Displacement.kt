package com.dalmazo.helena.mestrerpg.enum.model

enum class Displacement(val value: String) {
    BURROW("Escavação"),
    CLIMB("Escalada"),
    FLY("Voo"),
    SWIM("Natação");

    override fun toString(): String {
        return value
    }
}