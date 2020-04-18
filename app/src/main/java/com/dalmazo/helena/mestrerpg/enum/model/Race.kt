package com.dalmazo.helena.mestrerpg.enum.model

enum class Race(val value: String) {
    DWARF("Anão"),
    ELF("Elfo"),
    GOBLIN("Goblin"),
    HUMAN("Humano"),
    ORC("Ocr");

    override fun toString(): String {
        return value
    }
}