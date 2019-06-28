package com.dalmazo.helena.mestrerpg.model

import com.google.firebase.firestore.Exclude
import java.io.Serializable

open class BaseModel(@get:Exclude var id: String = "",
                     val name: String = "",
                     val characteristics: String = "",
                     val history: String = "")
    : Serializable { }