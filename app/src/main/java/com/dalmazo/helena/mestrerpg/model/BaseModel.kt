package com.dalmazo.helena.mestrerpg.model

import java.io.Serializable

open class BaseModel(var id: Long, var name: String, var characteristics: String, var history: String) : Serializable { }