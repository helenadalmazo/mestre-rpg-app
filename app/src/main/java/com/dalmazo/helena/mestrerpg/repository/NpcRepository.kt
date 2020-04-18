package com.dalmazo.helena.mestrerpg.repository

import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.repository.base.BaseRepository

class NpcRepository(worldId: String): BaseRepository<Npc>(worldId, "npcs")
