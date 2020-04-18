package com.dalmazo.helena.mestrerpg.model.base

import com.google.firebase.firestore.Exclude
import java.io.Serializable

open class BaseModel(@set:Exclude @get:Exclude var id: String = "",
                     val name: String = "",
                     val characteristics: String = "",
                     val history: String = "")
    : Serializable