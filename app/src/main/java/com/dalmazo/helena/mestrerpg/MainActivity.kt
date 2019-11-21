package com.dalmazo.helena.mestrerpg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dalmazo.helena.mestrerpg.model.World
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    val EXTRA_ENTITY_WORLD = "ENTITY_WORLD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setWorldList()
    }

    private fun setWorldList() {
        FirebaseFirestore.getInstance().collection("worlds").get().addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener

            val worlds : MutableList<World> = mutableListOf()
            for (worldDoc in task.result!!) {
                val world: World = worldDoc.toObject(World::class.java).apply { this.id = worldDoc.id }
                worlds.add(world)
            }

            with(findViewById<ListView>(R.id.world_list)) {
                adapter = WorldAdapter(this@MainActivity, worlds)
                onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
                    val worldClicked = adapter.getItemAtPosition(position) as World
                    startActivity(Intent(this@MainActivity, WorldActivity::class.java).apply {
                        putExtra(EXTRA_ENTITY_WORLD, worldClicked)
                    })
                }
            }
        }
    }

    fun clickCreateNewWorld(view: View) {
        startActivity(Intent(this, WorldActivity::class.java))
    }

    inner class WorldAdapter(context: Context, worlds: List<World>) : ArrayAdapter<World>(context, R.layout.basic_list_item, worlds) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView: View = layoutInflater.inflate(R.layout.basic_list_item, null, false)

            val world = getItem(position) as World

//            rowView.findViewById<ImageView>(R.id.item_image).setImageResource(R.mipmap.ic_launcher) // TODO implementar imageResource
            rowView.findViewById<TextView>(R.id.item_name).text = world.name
            rowView.findViewById<TextView>(R.id.item_characteristics).text = world.characteristics

            return rowView
        }
    }
}
