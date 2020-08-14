package com.dalmazo.helena.mestrerpg.adapter

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dalmazo.helena.mestrerpg.MonsterActivity
import com.dalmazo.helena.mestrerpg.R
import com.dalmazo.helena.mestrerpg.enum.RequestCode
import com.dalmazo.helena.mestrerpg.fragment.MonsterFragment
import com.dalmazo.helena.mestrerpg.model.Monster
import com.dalmazo.helena.mestrerpg.util.Extra

class MonsterAdapter(private val fragment: MonsterFragment, private val monsterList: MutableList<Monster>) : RecyclerView.Adapter<MonsterAdapter.MonsterViewHolder>(), Filterable {

    private val originalMonsterList: MutableList<Monster> = monsterList.toMutableList()

    class MonsterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById(R.id.npc_item_image) as ImageView
        val name = itemView.findViewById(R.id.npc_item_name) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonsterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.npc_list_item, parent, false) as View
        return MonsterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MonsterViewHolder, position: Int) {
        val monster = monsterList[position]

        holder.name.text = monster.name

        if (monster.image.isEmpty()) {
            holder.image.setImageResource(R.drawable.no_image_available)
        } else {
            val bitmap = BitmapFactory.decodeByteArray(monster.image, 0, monster.image.size)
            holder.image.setImageBitmap(bitmap)
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(fragment.activity, MonsterActivity::class.java).apply { putExtra(Extra.MONSTER_OBJECT, monster) }
                fragment.startActivityForResult(intent, RequestCode.MONSTER.value)
            }}
        )
    }

    override fun getItemCount() = monsterList.size

    override fun getFilter() = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            monsterList.clear()

            if (constraint.isNullOrBlank()) {
                monsterList.addAll(originalMonsterList)
            } else {
//                val filteredMonsters = originalMonsterList.filter { it.name.toLowerCase().contains(constraint.toString().toLowerCase()) }
                for (currentMonster in originalMonsterList) {
                    if (currentMonster.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        monsterList.add(currentMonster)
                    }
                }
            }

            filterResults.values = monsterList
            filterResults.count = monsterList.count()

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            notifyDataSetChanged()
        }
    }

    fun add(monster: Monster) {
        originalMonsterList.add(monster)
        monsterList.add(monster)
        val position = getPosition(monster) // get position after add monster
        notifyItemInserted(position)
    }

    fun update(monster: Monster) {
        val position = getPosition(monster)
        originalMonsterList.set(position, monster)
        monsterList.set(position, monster)
        notifyItemChanged(position)
    }

    fun remove(monster: Monster) {
        val position = getPosition(monster)
        originalMonsterList.remove(monster)
        monsterList.remove(monster)
        notifyItemRemoved(position)
    }

    private fun getPosition(monster: Monster): Int {
        return originalMonsterList.indexOf(originalMonsterList.filter { it.id == monster.id }.first())
    }
}