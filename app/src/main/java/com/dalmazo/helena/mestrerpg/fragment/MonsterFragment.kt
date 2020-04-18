package com.dalmazo.helena.mestrerpg.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.MonsterActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.WorldActivity
import com.dalmazo.helena.mestrerpg.adapter.MonsterAdapter
import com.dalmazo.helena.mestrerpg.enum.Action
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.model.Monster
import com.dalmazo.helena.mestrerpg.repository.MonsterRepository
import com.dalmazo.helena.mestrerpg.repository.image.MonsterImageRepository
import com.dalmazo.helena.mestrerpg.util.Extra
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MonsterFragment : Fragment() {

    private lateinit var monsterRepository: MonsterRepository

    private lateinit var monsterAdapter: MonsterAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        setHasOptionsMenu(true)

        monsterRepository = MonsterRepository(getWorldId())

        monsterAdapter = MonsterAdapter(this, mutableListOf())

        val view = inflater.inflate(R.layout.fragment_monster, container, false)

        view.findViewById<FloatingActionButton>(R.id.monster_add).setOnClickListener {
            startActivityForResult(Intent(activity, MonsterActivity::class.java), RequestCode.MONSTER.value)
        }

        setList(view)

        return view
    }

    private fun getWorldId(): String {
        return (activity as WorldActivity).world.id
    }

    private fun setList(view: View) {
        monsterRepository.list().addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener

            val monsters: MutableList<Monster> = mutableListOf()

            for (monsterDoc in task.result!!) {
                var monster: Monster = monsterDoc.toObject(Monster::class.java)
                monster.id = monsterDoc.id

                monsters.add(monster)
                monsterAdapter.add(monster)

                MonsterImageRepository().get(monster).addOnSuccessListener { bytes ->
                    monster.image = bytes
                    monsterAdapter.update(monster)
                }
            }

            val recyclerView = view.findViewById(R.id.monster_list) as RecyclerView
            recyclerView.adapter = monsterAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.MONSTER.value) {
            val monster = data?.getSerializableExtra(Extra.MONSTER_OBJECT) as Monster

            val action = data.getSerializableExtra(Extra.MONSTER_ACTION) as Action

            val imageAction = data.getSerializableExtra(Extra.MONSTER_IMAGE_ACTION)

            if (action == Action.ADD && imageAction == Action.UPDATE) addObjectAndSaveImage(monster)

            if (action == Action.ADD) add(monster)
            if (action == Action.UPDATE) update(monster)
            if (action == Action.DELETE) delete(monster)

            if (imageAction == Action.UPDATE) saveImage(monster)
            if (imageAction == Action.DELETE) deleteImage(monster)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun addObjectAndSaveImage(monster: Monster) {
        monsterRepository.add(monster).addOnSuccessListener { documentReference ->
            monster.id = documentReference.id
            monsterAdapter.add(monster)
            showToast("Monstro ${monster.name} criado com sucesso!")

            saveImage(monster)
        }.addOnFailureListener { exception ->
            showToast("Ops, ocorreu um erro ao criar o Monstro ${monster.name}.")
        }
    }

    private fun add(monster: Monster) {
        monsterRepository.add(monster).addOnSuccessListener { documentReference ->
            monster.id = documentReference.id
            monsterAdapter.add(monster)
            showToast("Monstro ${monster.name} criado com sucesso!")
        }.addOnFailureListener { exception ->
            showToast("Ops, ocorreu um erro ao criar o Monstro ${monster.name}.")
        }
    }

    private fun update(monster: Monster) {
        monsterRepository.update(monster).addOnSuccessListener {
            monsterAdapter.update(monster)
            showToast("Monstro ${monster.name} salvo com sucesso!")
        }.addOnFailureListener { exception ->
            showToast("Ops, ocorreu um erro ao salvar o Monstro ${monster.name}.")
        }
    }

    private fun delete(monster: Monster) {
        monsterRepository.delete(monster).addOnSuccessListener {
            monsterAdapter.remove(monster)
            showToast("Monstro ${monster.name} removido com sucesso!")
        }.addOnFailureListener { exception ->
            showToast("Ops, ocorreu um  erro ao remover o Monstro ${monster.name}.")
        }
    }

    private fun saveImage(monster: Monster) {
        val uploadTask = MonsterImageRepository().save(monster, monster.image)
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("### Upload is $progress% done")
        }.addOnPausedListener {
            println("### Upload is paused")
        }.addOnFailureListener {
            println("### Upload failure")
        }.addOnSuccessListener {
            println("### Upload success")
            monsterAdapter.update(monster)
        }
    }

    private fun deleteImage(monster: Monster) {
        MonsterImageRepository().delete(monster)?.addOnSuccessListener {
            println("### Imagem removida com sucesso")
            monsterAdapter.update(monster)
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
                monsterAdapter.filter.filter(typedText)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }
}