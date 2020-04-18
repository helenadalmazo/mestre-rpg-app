package com.dalmazo.helena.mestrerpg.repository

import com.dalmazo.helena.mestrerpg.model.Monster
import com.dalmazo.helena.mestrerpg.repository.base.BaseRepository

class MonsterRepository(worldId: String) : BaseRepository<Monster>(worldId, "monsters")
