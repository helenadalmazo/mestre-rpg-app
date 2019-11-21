package com.dalmazo.helena.mestrerpg.enum

enum class Race(val value: String) {
    DWARF("Anão"),
    ELF("Elfo"),
    GOBLIN("Goblin"),
    HUMAN("Humano"),
    ORC("Ocr");

    companion object {
        fun get(v: String): Race? {
            return Race.values().find { race -> race.value == v }
        }
    }
}