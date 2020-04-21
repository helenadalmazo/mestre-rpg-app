package com.dalmazo.helena.mestrerpg.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.WorldActivity
import com.dalmazo.helena.mestrerpg.NpcActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.adapter.NpcAdapter
import com.dalmazo.helena.mestrerpg.enum.Action
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.model.Npc
import com.dalmazo.helena.mestrerpg.repository.NpcRepository
import com.dalmazo.helena.mestrerpg.repository.image.NpcImageRepository
import com.dalmazo.helena.mestrerpg.util.Extra
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NpcFragment: Fragment() {

    private lateinit var npcRepository: NpcRepository

    private lateinit var npcAdapter: NpcAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        npcRepository = NpcRepository(getWorldId())

        val view = inflater.inflate(R.layout.fragment_npc, container, false)

        view.findViewById<FloatingActionButton>(R.id.npc_add).setOnClickListener {
            startActivityForResult(Intent(activity, NpcActivity::class.java), RequestCode.NPC.value)
        }

        setNpcList(view)

        return view
    }

    private fun getWorldId(): String {
        return (activity as WorldActivity).world.id
    }

    private fun setNpcList(view: View) {
        npcRepository.list().addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener

            val npcs: MutableList<Npc> = mutableListOf()

            for (npcDoc in task.result!!) {
                var npc: Npc = npcDoc.toObject(Npc::class.java)
                npc.id = npcDoc.id

                NpcImageRepository().get(npc).addOnSuccessListener { bytes ->
                    npc.image = bytes
                    npcAdapter.update(npc)
                }

                npcs.add(npc)
            }

            npcAdapter = NpcAdapter(this, npcs)
            val recyclerView = view.findViewById(R.id.npc_list) as RecyclerView
            recyclerView.adapter = npcAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.NPC.value) {
            val npc = data?.getSerializableExtra(Extra.NPC_OBJECT) as Npc

            val action = data.getSerializableExtra(Extra.NPC_ACTION) as Action

            val imageAction = data.getSerializableExtra(Extra.NPC_IMAGE_ACTION)

            if (action == Action.ADD && imageAction == Action.UPDATE) addNpcAndImage(npc)

            if (action == Action.ADD) add(npc)
            if (action == Action.UPDATE) update(npc)
            if (action == Action.DELETE) delete(npc)

            if (imageAction == Action.UPDATE) saveImage(npc)
            if (imageAction == Action.DELETE) deleteImage(npc)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun addNpcAndImage(npc: Npc) {
        npcRepository.add(npc).addOnSuccessListener { documentReference ->
            npc.id = documentReference.id
            npcAdapter.add(npc)
            showToast("NPC ${npc.name} criado com sucesso!")

            NpcImageRepository().save(npc, npc.image).addOnSuccessListener {
                npcAdapter.update(npc)
            }
        }
        .addOnFailureListener { exception ->
            showToast("Ops, ocorreu um erro ao criar o NPC ${npc.name}.")
        }
    }

    private fun add(npc: Npc) {
        npcRepository.add(npc).addOnSuccessListener { documentReference ->
            npc.id = documentReference.id
            npcAdapter.add(npc)
            showToast("NPC ${npc.name} criado com sucesso!")
        }
        .addOnFailureListener { exception ->
            showToast("Ops, ocorreu um erro ao criar o NPC ${npc.name}.")
        }
    }

    private fun update(npc: Npc) {
        npcRepository.update(npc).addOnSuccessListener {
            npcAdapter.update(npc)
            showToast("NPC ${npc.name} salvo com sucesso!")
        }
        .addOnFailureListener { exception ->
            showToast("Ops, ocorreu um erro ao salvar o NPC ${npc.name}.")
        }
    }

    private fun delete(npc: Npc) {
        npcRepository.delete(npc).addOnSuccessListener {
            npcAdapter.remove(npc)
            showToast("NPC ${npc.name} removido com sucesso!")
        }
        .addOnFailureListener { exception ->
            showToast("Ops, ocorreu um  erro ao remover o NPC ${npc.name}.")
        }
    }

    private fun saveImage(npc: Npc) {
        val uploadTask = NpcImageRepository().save(npc, npc.image)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("### Upload is $progress% done")
        }.addOnPausedListener {
            println("### Upload is paused")
        }.addOnFailureListener {
            println("### Upload failure")
        }.addOnSuccessListener {
            println("### Upload success")
            npcAdapter.update(npc)
        }
    }

    private fun deleteImage(npc: Npc) {
        NpcImageRepository().delete(npc)?.addOnSuccessListener {
            println("### Imagem removida com sucesso")
            npcAdapter.update(npc)
        }?.addOnFailureListener {
            println("### Imagem não removida")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.app_bar_actions_buttons_list, menu)

        val searchMenuItem = menu.findItem(R.id.action_search) as MenuItem

        (searchMenuItem.actionView as SearchView).setOnQueryTextListener( object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(typedText: String?): Boolean {
                npcAdapter.filter.filter(typedText)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }
}
