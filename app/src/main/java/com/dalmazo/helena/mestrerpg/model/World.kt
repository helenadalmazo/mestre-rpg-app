package com.dalmazo.helena.mestrerpg.model

import java.io.Serializable

class World(id: String = "",
            name: String = "",
            characteristics: String = "",
            history: String = "",
            val places: List<Place> = listOf(),
            val npcs: MutableList<Npc> = mutableListOf(),
            val monsters: List<Monster> = listOf())
    : BaseModel(id, name, characteristics, history), Serializable { }