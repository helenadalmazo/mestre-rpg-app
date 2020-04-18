package com.dalmazo.helena.mestrerpg.enum.model

enum class Sex(val value: String) {
    FEMALE("Feminino"),
    MALE("Masculino");

    override fun toString(): String {
        return value
    }
}