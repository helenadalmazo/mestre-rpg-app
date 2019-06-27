package com.dalmazo.helena.mestrerpg.model

import java.io.Serializable

class Monster (id: Long, name: String, characteristics: String, history: String)
    : BaseModel(id, name, characteristics, history), Serializable { }