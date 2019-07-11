package com.dalmazo.helena.mestrerpg.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.dalmazo.helena.mestrerpg.WorldActivity
import com.dalmazo.helena.mestrerpg.NpcActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.enum.Action
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.util.Extra
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class NpcFragment : Fragment() {

    val REQUEST_CODE_NPC = 1111

    lateinit var worldId: String

    var npcs: MutableList<Npc> = mutableListOf()

    lateinit var npcAdapter: NpcAdapter

    lateinit var searchMenuItem: MenuItem

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        worldId = (activity as WorldActivity).world.id

        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_npc, container, false)

        view.findViewById<FloatingActionButton>(R.id.npc_add).setOnClickListener {
            startActivityForResult(Intent(activity, NpcActivity::class.java), REQUEST_CODE_NPC)
        }

        setNpcList(view)

        return view
    }

    private fun setNpcList(view: View) {
        FirebaseFirestore.getInstance()
            .collection("worlds").document(worldId)
            .collection("npcs").get().addOnCompleteListener { task ->
                if (!task.isSuccessful) return@addOnCompleteListener

                for (npcDoc in task.result!!) {
                    val npc: Npc = npcDoc.toObject(Npc::class.java).apply { this.id = npcDoc.id }
                    npcs.add(npc)
                }

                npcAdapter = NpcAdapter(activity as WorldActivity, npcs.toList())

                (view.findViewById<ListView>(R.id.npc_list)).apply {
                    adapter = npcAdapter
                    onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
                        val npcClicked = adapter.getItemAtPosition(position) as Npc
                        val intent = Intent(activity, NpcActivity::class.java).apply {
                            putExtra(Extra.NPC_OBJECT, npcClicked)
                        }
                        startActivityForResult(intent, REQUEST_CODE_NPC)
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_NPC) {

            val npc = data?.getSerializableExtra(Extra.NPC_OBJECT) as Npc

            when (data.getSerializableExtra(Extra.NPC_ACTION)) {
                Action.ADD -> addNpcFirestore(npc)
                Action.EDIT -> editNpcFirestore(npc)
                Action.DELETE -> deleteNpcFirestore(npc)
            }

            if (data.getSerializableExtra(Extra.NPC_IMAGE_ACTION) != null) {
                val image = data.getParcelableExtra<Bitmap>(Extra.NPC_IMAGE)

                when (data.getSerializableExtra(Extra.NPC_IMAGE_ACTION)) {
                    Action.EDIT -> {
                        val baos = ByteArrayOutputStream()
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                        val uploadTask = FirebaseStorage.getInstance().reference.child("npcs/${npc.id}.jpg")
                            .putBytes(baos.toByteArray())
                        uploadTask.addOnProgressListener { taskSnapshot ->
                            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                            println("### Upload is $progress% done")
                            npcAdapter.notifyDataSetChanged()
                        }.addOnPausedListener {
                            println("### Upload is paused")
                        }.addOnFailureListener {
                            println("### Upload failure")
                        }.addOnSuccessListener {
                            println("### Upload success")
                        }
                    }
                    Action.DELETE -> {
                        val deleteTask = FirebaseStorage.getInstance().reference.child("npcs/${npc.id}.jpg").delete()
                        deleteTask.addOnSuccessListener {
                            println("### Imagem removida com sucesso")
                        }.addOnFailureListener {
                            println("### Imagem não removida")
                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_actions_buttons_list, menu)

        searchMenuItem = menu.findItem(R.id.action_search) as MenuItem

        (searchMenuItem.actionView as SearchView).setOnQueryTextListener( object : SearchView.OnQueryTextListener {
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowView: View = layoutInflater.inflate(R.layout.basic_list_item, null, false);

            val npc = getItem(position) as Npc

            rowView.findViewById<ImageView>(R.id.item_image).setImageResource(R.mipmap.ic_launcher)

            FirebaseStorage.getInstance().reference
                .child("npcs/${npc.id}.jpg")
                .getBytes(1024 * 1024)
                .addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    rowView.findViewById<ImageView>(R.id.item_image).setImageBitmap(bitmap)
                }

            rowView.findViewById<TextView>(R.id.item_name).text = npc.name

            return rowView
        }

        override fun getFilter() = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val originalNpcs = this@NpcFragment.npcs.toList()
                val results = FilterResults()

                results.values = originalNpcs
                results.count = originalNpcs.size

                if (constraint != null && constraint != "") {
                    val filteredNpcs: MutableList<Npc> = mutableListOf()

                    for (currentNpc in originalNpcs){
                        if (currentNpc.name.toLowerCase().contains(constraint.toString().toLowerCase())){
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
                addAll(results?.values as List<Npc>)
                notifyDataSetChanged()
            }

        }
    }

    private fun addNpcFirestore(npc: Npc) {
        FirebaseFirestore.getInstance()
            .collection("worlds").document(worldId)
            .collection("npcs")
            .add(npc)
            .addOnSuccessListener { documentReference ->
                npc.id = documentReference.id
                npcs.add(npc)

                updateNpcList()

                Toast.makeText(activity, "NPC ${npc.name} criado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Erro ao criar o NPC ${npc.name}.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editNpcFirestore(npc: Npc) {
        FirebaseFirestore.getInstance()
            .collection("worlds").document(worldId)
            .collection("npcs").document(npc.id)
            .set(npc)
            .addOnSuccessListener {
                val position = npcs.indexOf(npcs.filter { it.id == npc.id }.first())
                npcs.set(position, npc)

                updateNpcList()

                Toast.makeText(activity, "NPC ${npc.name} salvo com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Erro ao salvar o NPC ${npc.name}.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteNpcFirestore(npc: Npc) {
        FirebaseFirestore.getInstance()
            .collection("worlds").document(worldId)
            .collection("npcs").document(npc.id)
            .delete()
            .addOnSuccessListener {
                val position = npcs.indexOf(npcs.filter { it.id == npc.id }.first())
                npcs.removeAt(position)

                updateNpcList()

                Toast.makeText(activity, "NPC ${npc.name} removido com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Erro ao remover o NPC ${npc.name}.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateNpcList() {
        npcAdapter.clear()
        npcAdapter.addAll(npcs)
        npcAdapter.notifyDataSetChanged()
        searchMenuItem.collapseActionView()
    }
}
