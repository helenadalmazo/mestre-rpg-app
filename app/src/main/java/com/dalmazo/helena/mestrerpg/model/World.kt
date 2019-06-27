package com.dalmazo.helena.mestrerpg.model

import java.io.Serializable

class World(id: Long, name: String, characteristics: String, history: String,
            var places: List<Place>, var npcs: MutableList<Npc>, var monsters: List<Monster>)
    : BaseModel(id, name, characteristics, history), Serializable { }