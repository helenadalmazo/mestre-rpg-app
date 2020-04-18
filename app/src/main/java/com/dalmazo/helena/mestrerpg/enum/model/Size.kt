package com.dalmazo.helena.mestrerpg.enum.model

enum class Size(val value: String) {
    TINY("Miúdo"),
    SMALL("Pequeno"),
    MEDIUM("Médio"),
    LARGE("Grande"),
    HUGE("Enorme"),
    GARGANTUAN("Imenso");

    companion object {
        fun get(v: String): Size? {
            return values().find { it.value == v }
        }
    }
}