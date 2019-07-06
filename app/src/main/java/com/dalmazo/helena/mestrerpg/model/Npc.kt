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
    : BaseModel(id, name, characteristics, history), Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        val that = other as Npc
        return id == that.id
                && name == that.name
                && characteristics == that.characteristics
                && history == that.history
                && sex == that.sex
                && race == that.race
    }
}