package com.dalmazo.helena.mestrerpg.model

import com.dalmazo.helena.mestrerpg.enum.Race
import com.dalmazo.helena.mestrerpg.enum.Sex
import java.io.Serializable

class Npc(id: String = "",
          name: String = "",
          characteristics: String = "",
          history: String = "",
          val sex: Sex? = null,
          var race: Race? = null)
    : BaseModel(id, name, characteristics, history), Serializable { }