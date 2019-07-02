package com.dalmazo.helena.mestrerpg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dalmazo.helena.mestrerpg.fragment.MonsterFragment
import com.dalmazo.helena.mestrerpg.fragment.NpcFragment
import com.dalmazo.helena.mestrerpg.fragment.PlaceFragment
import com.dalmazo.helena.mestrerpg.fragment.WorldFragment
import com.dalmazo.helena.mestrerpg.model.World
import com.google.android.material.bottomnavigation.BottomNavigationView

class WorldActivity : AppCompatActivity() {

    val EXTRA_ENTITY_WORLD = "ENTITY_WORLD"

    lateinit var world: World

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world)

        world = intent.extras.getSerializable(EXTRA_ENTITY_WORLD) as World

        title = world.name

        startFragment(WorldFragment())

        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_world -> {
                    startFragment(WorldFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_place -> {
                    startFragment(PlaceFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_npc -> {
                    startFragment(NpcFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_monster -> {
                    startFragment(MonsterFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
}
