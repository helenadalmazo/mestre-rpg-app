package com.dalmazo.helena.mestrerpg.model

import com.dalmazo.helena.mestrerpg.enum.Race
import com.dalmazo.helena.mestrerpg.enum.Sex
import java.io.Serializable

class Npc(id: Long, name: String, characteristics: String, history: String,
          var sex: Sex, var race: Race)
    : BaseModel(id, name, characteristics, history), Serializable { }