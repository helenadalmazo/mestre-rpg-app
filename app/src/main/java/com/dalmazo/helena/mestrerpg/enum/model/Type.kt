package com.dalmazo.helena.mestrerpg.enum.model

enum class Type(val value: String) {
    ABERRATION("Aberração"),
    BEAST("Besta"),
    CELESTIAL("Celestial"),
    CONSTRUCT("Constructo"),
    DRAGON("Dragão"),
    ELEMENTAL("Elemental"),
    FEY("Fadas"),
    FIEND("Demônio"),
    GIANT("Gigante"),
    HUMANOID("Humonoide"),
    MONSTROSITY("Monstruosidade"),
    OOZE("Limos"),
    PLANT("Planta"),
    UNDEAD("Morto-vivo");

    override fun toString(): String {
        return value
    }
}