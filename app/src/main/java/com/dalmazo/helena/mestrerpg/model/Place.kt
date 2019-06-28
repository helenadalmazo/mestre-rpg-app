package com.dalmazo.helena.mestrerpg.model

import java.io.Serializable

class Place(id: String, name: String, characteristics: String, history: String)
    : BaseModel(id, name, characteristics, history), Serializable { }