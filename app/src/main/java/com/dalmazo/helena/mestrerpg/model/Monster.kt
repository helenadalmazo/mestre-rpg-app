package com.dalmazo.helena.mestrerpg.model

import com.dalmazo.helena.mestrerpg.enum.model.Displacement
import com.dalmazo.helena.mestrerpg.enum.model.Size
import com.dalmazo.helena.mestrerpg.enum.model.Type
import com.dalmazo.helena.mestrerpg.model.base.BaseModel
import com.google.firebase.firestore.Exclude
import java.io.Serializable

class Monster (id: String = "",
               @set:Exclude @get:Exclude var image: ByteArray = byteArrayOf(),
               name: String = "",
               characteristics: String = "",
               history: String = "",
               val size: Size? = null,
               val type: Type? = null,
               val displacement: Displacement? = null)
    : BaseModel(id, name, characteristics, history), Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false

        val that = other as Monster
        return id == that.id
                && image.contentEquals(that.image)
                && name == that.name
                && characteristics == that.characteristics
                && history == that.history
                && size == that.size
                && type == that.type
                && displacement == that.displacement
    }
}