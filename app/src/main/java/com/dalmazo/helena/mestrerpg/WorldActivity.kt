package com.dalmazo.helena.mestrerpg

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.dalmazo.helena.mestrerpg.fragment.MonsterFragment
import com.dalmazo.helena.mestrerpg.fragment.NpcFragment
import com.dalmazo.helena.mestrerpg.fragment.PlaceFragment
import com.dalmazo.helena.mestrerpg.fragment.WorldFragment
import com.dalmazo.helena.mestrerpg.model.World

class WorldActivity : AppCompatActivity() {

    val EXTRA_ENTITY_WORLD = "ENTITY_WORLD"

    lateinit var world: World

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        world = intent.extras.getSerializable(EXTRA_ENTITY_WORLD) as World

        startFragment(WorldFragment())

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun startFragment(fragment: Fragment) {
        title = world.name
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        transaction.commit()
    }
}
