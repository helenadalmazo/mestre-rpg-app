package com.dalmazo.helena.mestrerpg.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class NpcFragment : Fragment() {

    private lateinit var npcRepository: NpcRepository

    private lateinit var npcAdapter: NpcAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        npcRepository = NpcRepository((activity as WorldActivity).world.id)

        setHasOptionsMenu(true)

        val view = inflater.inflate(R.layout.fragment_npc, container, false)

        view.findViewById<FloatingActionButton>(R.id.npc_add).setOnClickListener {
            startActivityForResult(Intent(activity, NpcActivity::class.java), RequestCode.NPC.value)
        }

        setNpcList(view)

        return view
    }

    private fun setNpcList(view: View) {
        npcRepository.list().addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener

            val npcs: MutableList<Npc> = mutableListOf()

            for (npcDoc in task.result!!) {
                val npc: Npc = npcDoc.toObject(Npc::class.java).apply { this.id = npcDoc.id }
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

            if (data.getSerializableExtra(Extra.NPC_ACTION) == Action.ADD
                && data.getSerializableExtra(Extra.NPC_IMAGE_ACTION) == Action.EDIT) {
                val image = data.getParcelableExtra<Bitmap>(Extra.NPC_IMAGE)
                addNpcAndImage(npc, image)

            } else {
                when (data.getSerializableExtra(Extra.NPC_ACTION)) {
                    Action.ADD -> add(npc)
                    Action.EDIT -> edit(npc)
                    Action.DELETE -> delete(npc)
                }

                if (data.getSerializableExtra(Extra.NPC_IMAGE_ACTION) != null) {
                    val image = data.getParcelableExtra<Bitmap>(Extra.NPC_IMAGE)

                    when (data.getSerializableExtra(Extra.NPC_IMAGE_ACTION)) {
                        Action.EDIT -> addImage(npc, image)
                        Action.DELETE -> deleteImage(npc)
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
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
    }

    private fun addNpcAndImage(npc: Npc, image: Bitmap) {
        npcRepository.add(npc).addOnSuccessListener { documentReference ->
            npc.id = documentReference.id
            npcAdapter.add(npc)
            showToast("NPC ${npc.name} criado com sucesso!")

            val byteArrayOutputStream = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

            NpcImageRepository().save(npc, byteArrayOutputStream.toByteArray()).addOnSuccessListener {
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

    private fun edit(npc: Npc) {
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

    private fun addImage(npc: Npc, image: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

        val uploadTask = NpcImageRepository().save(npc, byteArrayOutputStream.toByteArray())
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
}
