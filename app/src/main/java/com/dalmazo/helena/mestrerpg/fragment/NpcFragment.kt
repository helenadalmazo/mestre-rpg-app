package com.dalmazo.helena.mestrerpg.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.dalmazo.helena.mestrerpg.WorldActivity
import com.dalmazo.helena.mestrerpg.NpcActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.model.Npc
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NpcFragment : Fragment() {

    val NPC_REQUEST_CODE = 15245

    val NPC_EXTRA = "NPC_EXTRA"
    val NPC_ACTION_EXTRA = "NPC_ACTION_EXTRA"
    val ADD_NPC_EXTRA = "ADD_NPC_EXTRA"
    val EDIT_NPC_EXTRA = "EDIT_NPC_EXTRA"
    val DELETE_NPC_EXTRA = "DELETE_NPC_EXTRA"

    lateinit var npcs: MutableList<Npc>
    lateinit var npcAdapter: NpcAdapter
    lateinit var npc: Npc

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val view = inflater.inflate(R.layout.fragment_npc, container, false)

        setHasOptionsMenu(true)

        view.findViewById<FloatingActionButton>(R.id.npc_add_new)?.setOnClickListener {
            startActivityForResult(Intent(activity, NpcActivity::class.java), NPC_REQUEST_CODE)
        }

        npcs = (activity as WorldActivity).world.npcs
        npcAdapter = NpcAdapter(activity as WorldActivity, npcs) //copy list

        (view.findViewById<ListView>(R.id.npc_list)).apply {
            adapter = npcAdapter
            onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
                npc = adapter.getItemAtPosition(position) as Npc
                startActivityForResult(Intent(activity, NpcActivity::class.java).apply { putExtra(NPC_EXTRA, npc) }, NPC_REQUEST_CODE)
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var message: String = ""
        if (resultCode == Activity.RESULT_OK && requestCode == NPC_REQUEST_CODE) {

            when (data?.extras?.get(NPC_ACTION_EXTRA)) {
                ADD_NPC_EXTRA -> {
                    val npcAdded = data.extras?.get(NPC_EXTRA) as Npc

                    npcs.add(npcAdded)

                    message = "NPC ${npcAdded.name} adicionado com sucesso!"
                }
                EDIT_NPC_EXTRA -> {
                    val npcEdited = data.extras.get(NPC_EXTRA) as Npc

                    val position = (activity as WorldActivity).world.npcs.indexOf(npc)

                    npcs.set(position, npcEdited)

                    message = "NPC ${npcEdited.name} salvo com sucesso!"

                }
                DELETE_NPC_EXTRA -> {
                    val npcDeleted = data.extras.get(NPC_EXTRA) as Npc

                    val position = (activity as WorldActivity).world.npcs.indexOf(npc)

                    npcs.removeAt(position)

                    message = "NPC ${npcDeleted.name} removido com sucesso!"
                }
            }

            npcAdapter.notifyDataSetChanged()
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_actions_buttons_list, menu)

        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                Toast.makeText(activity, "onMenuItemActionCollapse", Toast.LENGTH_LONG).show()
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                Toast.makeText(activity, "onMenuItemActionExpand", Toast.LENGTH_LONG).show()
                return true // Return true to expand action view
            }
        }

        val actionMenuItem = menu.findItem(R.id.action_search) as MenuItem

        actionMenuItem.setOnActionExpandListener(expandListener)

        (actionMenuItem.actionView as SearchView).setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(typedText: String?): Boolean {
                npcAdapter.filter.filter(typedText)
                return true
            }

        })
    }

    inner class NpcAdapter(context: Context, npcs: List<Npc>) : ArrayAdapter<Npc>(context, R.layout.basic_list_item, npcs) {

        private val dataNpcs: List<Npc> = npcs

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView: View = layoutInflater.inflate(R.layout.basic_list_item, null, false);

            val npc = getItem(position) as Npc
            rowView.findViewById<ImageView>(R.id.item_image).setImageResource(R.mipmap.ic_launcher) // TODO implementar imageResource
            rowView.findViewById<TextView>(R.id.item_name).text = npc.name

            return rowView
        }

//        override fun getFilter(): Filter {
//            return super.getFilter()
//        }

//        fun getAllItens(): List<NPC> {
//            var allNpcs: MutableList<NPC> = mutableListOf()
//            for (i in 0..(count-1)) {
//                allNpcs.add(getItem(i))
//            }
//            return this@NpcFragment.npcs
//        }

        override fun getFilter() = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var results = FilterResults()

                var originalNpcs = this@NpcFragment.npcs
                if (constraint != null && constraint != "") {
                    val filteredNpcs: MutableList<Npc> = mutableListOf()

                    for (currentNpc in originalNpcs){
                        if(currentNpc.name.contains(constraint)){
                            filteredNpcs.add(currentNpc);
                        }
                    }

                    results.values = filteredNpcs
                    results.count = filteredNpcs.size
                }

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()

                println("results?.count ${results?.count}")
                if (results?.count!! > 0) {
                    addAll(results?.values as List<Npc>)
                } else {
                    addAll(this@NpcFragment.npcs)
                }

                notifyDataSetChanged()
            }

        }
    }
}
