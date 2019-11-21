package com.dalmazo.helena.mestrerpg.enum

enum class Sex(val value: String) {
    FEMALE("Feminino"),
    MALE("Masculino");

    companion object {
        fun get(v: String): Sex? {
            return Sex.values().find { sex -> sex.value == v }
        }
    }
}