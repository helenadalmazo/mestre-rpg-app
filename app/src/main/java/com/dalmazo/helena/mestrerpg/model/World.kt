package com.dalmazo.helena.mestrerpg.model

import com.dalmazo.helena.mestrerpg.model.base.BaseModel
import java.io.Serializable

class World(id: String = "",
            name: String = "",
            characteristics: String = "",
            history: String = "")
    : BaseModel(id, name, characteristics, history), Serializable { }