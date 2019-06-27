package com.dalmazo.helena.mestrerpg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dalmazo.helena.mestrerpg.enum.Race
import com.dalmazo.helena.mestrerpg.enum.Sex
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.model.Place
import com.dalmazo.helena.mestrerpg.model.World

class MainActivity : AppCompatActivity() {

    val EXTRA_ENTITY_WORLD = "ENTITY_WORLD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var npc1 = Npc(1,"AAAAAA", "AAAAAA características", "AAAAAA história", Sex.FEMALE, Race.ORC)
        var npc2 = Npc(2,"BBBBBB", "BBBBBB características", "BBBBBB história", Sex.FEMALE, Race.ORC)
        var npc3 = Npc(3,"CCCCCC", "CCCCCC características", "CCCCCC história", Sex.FEMALE, Race.ORC)
        var npc4 = Npc(4,"DDDDDD", "DDDDDD características", "DDDDDD história", Sex.FEMALE, Race.ORC)
        var npc5 = Npc(5,"EEEEEE", "EEEEEE características", "EEEEEE história", Sex.FEMALE, Race.ORC)
        var npc6 = Npc(6,"FFFFFF", "FFFFFF características", "FFFFFF história", Sex.FEMALE, Race.ORC)
        var npc7 = Npc(7,"GGGGGG", "GGGGGG características", "GGGGGG história", Sex.FEMALE, Race.ORC)
        var npc8 = Npc(8,"HHHHHH", "HHHHHH características", "HHHHHH história", Sex.FEMALE, Race.ORC)
        var npc9 = Npc(9,"IIIIII", "IIIIII características", "IIIIII história", Sex.FEMALE, Race.ORC)
        var npc0 = Npc(0,"JJJJJJ", "JJJJJJ características", "JJJJJJ história", Sex.FEMALE, Race.ORC)

        var place1 = Place(5,"Hospital", "Hospital características", "Hospital história")
        var place2 = Place(6,"Floresta", "Floresta características", "Floresta história")
        var place3 = Place(7,"Farmácia", "Farmácia características", "Farmácia história")
        var place4 = Place(8,"Castelo", "Castelo características", "Castelo história")

        var mundo1 = World(9, "Mundo 1", "Mundo 1 características", "Mundo 1 história", listOf(place1, place2, place3, place4), mutableListOf(npc1, npc2, npc3, npc4, npc5, npc6, npc7, npc8, npc9, npc0), listOf())

        with(findViewById<ListView>(R.id.world_list)) {
            adapter = WorldAdapter(this@MainActivity, listOf(mundo1))
            onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
                val worldClicked = adapter.getItemAtPosition(position) as World
                val intent = Intent(this@MainActivity, WorldActivity::class.java).apply {
                    putExtra(EXTRA_ENTITY_WORLD, worldClicked)
                }
                startActivity(intent)
            }
        }
    }

    fun clickCreateNewWorld(view: View) {
        startActivity(Intent(this, WorldActivity::class.java))
    }

    inner class WorldAdapter(context: Context, worlds: List<World>) : ArrayAdapter<World>(context, R.layout.basic_list_item, worlds) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView: View = layoutInflater.inflate(R.layout.basic_list_item, null, false);

            val world = getItem(position) as World

            rowView.findViewById<ImageView>(R.id.item_image).setImageResource(R.mipmap.ic_launcher) // TODO implementar imageResource
            rowView.findViewById<TextView>(R.id.item_name).text = world.name

            return rowView
        }
    }
}
