package com.dalmazo.helena.mestrerpg.enum.model

enum class Displacement(val value: String) {
    BURROW("Escavação"),
    CLIMB("Escalada"),
    FLY("Voo"),
    SWIM("Natação");

    companion object {
        fun get(v: String): Displacement? {
            return values().find { it.value == v }
        }
    }
}